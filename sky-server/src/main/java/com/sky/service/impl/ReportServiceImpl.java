package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    public UserMapper userMapper;

    /**
     * 统计指定时间区间的营业额
     *
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datelist.add(begin);
        }
        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        //查询date日期对应的营业额数据，营业额是状态为已完成订单的总额
        for (LocalDate date : datelist) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
        turnoverReportVO.setDateList(StringUtils.join(datelist, ","));
        return turnoverReportVO;
    }

    /**
     * 统计指定时间区间的用户数据
     *
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datelist.add(begin);
        }
        //存放每天新增用户
        List<Integer> newUserList = new ArrayList<>();
        //存放每天总用户量
        List<Integer> totalUserList = new ArrayList<>();
        for (int i = 0; i < datelist.size(); i++) {
            LocalDate date = datelist.get(i);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("end", endTime);
            Integer newUsers = userMapper.countByMap(map);
            newUserList.add(newUsers);
            map.put("begin", beginTime);
            Integer totalUsers = userMapper.countByMap(map);
            totalUserList.add(totalUsers);
        }
        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(StringUtils.join(datelist, ","));
        userReportVO.setNewUserList(StringUtils.join(newUserList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ","));
        return userReportVO;
    }

    /**
     * 统计指定时间区间的订单数据
     *
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datelist.add(begin);
        }
        //存放每天订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();
        //遍历datelist集合，查询每天的有效订单数据和总订单
        for (int i = 0; i < datelist.size(); i++) {
            LocalDate date = datelist.get(i);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("end", endTime);
            map.put("begin", beginTime);
            //查询每天的订单总数
            Integer orderCount = orderMapper.countByMap(map);
            //查询每天的有效订单
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //计算时间区间内的订单总数量
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        for (int i = 0; i < orderCountList.size(); i++) {
            totalOrderCount = totalOrderCount + orderCountList.get(i);
        }
        //计算订单区间内的有效订单总数量
        for (int i = 0; i < validOrderCountList.size(); i++) {
            validOrderCount = validOrderCount + validOrderCountList.get(i);
        }
        //订单完成率
        Double orderCompletionRate = 0.0;

        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }
        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(StringUtils.join(datelist, ","));
        orderReportVO.setOrderCountList(StringUtils.join(orderCountList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList, ","));
        orderReportVO.setValidOrderCount(validOrderCount);
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }

    /**
     * 统计指定时间区间内的销量排名前10
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (int i = 0; i < salesTop10.size(); i++) {
            GoodsSalesDTO goodsSalesDTO = salesTop10.get(i);
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        salesTop10ReportVO.setNumberList(StringUtils.join(numberList, ","));
        salesTop10ReportVO.setNameList(StringUtils.join(nameList, ","));
        return salesTop10ReportVO;
    }
}


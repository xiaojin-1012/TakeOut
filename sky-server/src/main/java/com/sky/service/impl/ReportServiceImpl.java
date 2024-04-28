package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出运营数据报表
     *
     * @return
     */
    public void exportBusinessData(HttpServletResponse response) {
        //查询最近三十天的营业数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //查询数据库
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        //通过POI将数据写入Excel表中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于模板文件创建一个Excel表
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格的标签页
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            //填充数据---时间
            sheet1.getRow(1).getCell(1).setCellValue("时间" + begin + "至" + end);
            //获取第四行
            XSSFRow row = sheet1.getRow(3);
            //获取第四行中的第三个单元格
            XSSFCell cell = row.getCell(2);
            //填充营业额
            cell.setCellValue(businessData.getTurnover());
            //填充订单完成率
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            //填充新增用户数
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //获得第五行
            XSSFRow row1 = sheet1.getRow(4);
            //填充有效订单
            row1.getCell(2).setCellValue(businessData.getValidOrderCount());
            //填充平均客单价
            row1.getCell(4).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(1);
                BusinessDataVO businessDataVo = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row2 = sheet1.getRow(i + 7);
                row2.getCell(1).setCellValue(date.toString());
                row2.getCell(2).setCellValue(businessDataVo.getTurnover());
                row2.getCell(3).setCellValue(businessDataVo.getValidOrderCount());
                row2.getCell(4).setCellValue(businessDataVo.getOrderCompletionRate());
                row2.getCell(5).setCellValue(businessDataVo.getUnitPrice());
                row2.getCell(6).setCellValue(businessDataVo.getNewUsers());
            }

            //获得输出流对象
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


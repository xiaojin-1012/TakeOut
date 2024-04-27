package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     *统计指定时间区间的营业额
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间的用户数据
     *
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
    /**
     * 统计指定时间区间的订单数据
     *
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
    /**
     * 统计指定时间区间的销量排名前10
     *
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}

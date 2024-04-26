package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
private OrderMapper orderMapper;
    /**
     * 处理超时订单
     *
     */
    @Scheduled(cron = "0 0,1 * * * ? ")//每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单：{}",LocalDateTime.now());
//        LocalDateTime.now()求现在时间，plusMinutes用来减去多长时间Minutes为分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        for (int i =0; i<ordersList.size();i++){
            ordersList.get(i).setStatus(Orders.CANCELLED);
            ordersList.get(i).setCancelReason("订单超时自动取消");
            ordersList.get(i).setCancelTime(LocalDateTime.now());
            orderMapper.update(ordersList.get(i));
        }
    }
    /**
     * 处理一直处于派送中的订单
     *
     */
    @Scheduled(cron = "0 0 1 * * ?")
     public  void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单：{}",LocalDateTime.now());
        //        LocalDateTime.now()求现在时间，plusHours用来减去多长时间Hours为分钟
        List<Orders>ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusHours(-1));
        for (int i =0; i<ordersList.size();i++){
            ordersList.get(i).setStatus(Orders.COMPLETED);
            ordersList.get(i).setCancelReason("订单超时自动取消");
            ordersList.get(i).setCancelTime(LocalDateTime.now());
            orderMapper.update(ordersList.get(i));
        }
    }
}

package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
/**
 * 自定义任务类
 *
 */
public class MyTask {
    /**
     * 定时任务
     *
     */
@Scheduled(cron = "0 1,0 * * * ? ")
    public void executeTask(){log.info("定时任务开始执行:{}",new Date());}
}

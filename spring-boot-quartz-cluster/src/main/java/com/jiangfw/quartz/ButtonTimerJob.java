package com.jiangfw.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiangfw
 * @Date: 2019-04-30
 * @DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行. 注org.quartz.threadPool.threadCount的数量有多个的情况,@DisallowConcurrentExecution才生效
 */
@DisallowConcurrentExecution
public class ButtonTimerJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ButtonTimerJob.class);

    /**
     * 核心方法,Quartz Job真正的执行逻辑.
     *
     * @throws JobExecutionException execute()方法只允许抛出JobExecutionException异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("--------------定时任务执行逻辑---------------------");
    }
}

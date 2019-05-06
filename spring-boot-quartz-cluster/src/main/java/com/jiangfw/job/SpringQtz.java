package com.jiangfw.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @Author: jiangfw
 * @Date: 2019-05-06
 */
public class SpringQtz extends QuartzJobBean {

    private static Logger logger = LoggerFactory.getLogger(SpringQtz.class);

    private static int counter = 0;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        long ms = System.currentTimeMillis();
        logger.error(" SpringQtz start  执行");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        logger.info(
                "**********"
                        + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(ms))
                        + "  "
                        + "("
                        + counter++
                        + ")");
    }
}
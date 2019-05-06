package com.jiangfw.config;

import com.jiangfw.quartz.ButtonTimerJob;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @Author: jiangfw
 * @Date: 2019-04-30
 */
@Configuration  //(可以跟applicationContext.xml配置文件中的配置效果一致)
public class QuartzConfigure {

    // 配置文件路径
    private static final String QUARTZ_CONFIG = "/quartz.properties";

    @Autowired
    @Qualifier(value = "dataSource")
    private DataSource dataSource;

    @Value("${quartz.cronExpression}")
    private String cronExpression;

    /**
     * 从quartz.properties文件中读取Quartz配置属性
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_CONFIG));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * JobFactory与schedulerFactoryBean中的JobFactory相互依赖,注意bean的名称
     * 在这里为JobFactory注入了Spring上下文
     */
    @Bean
    public JobFactory buttonJobFactory(ApplicationContext applicationContext) {
        AutoWiredSpringBeanToJobFactory jobFactory = new AutoWiredSpringBeanToJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * @param buttonJobFactory 为SchedulerFactory配置JobFactory
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory buttonJobFactory,
            Trigger... cronJobTrigger) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(buttonJobFactory);
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true); // 设置自行启动
        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(cronJobTrigger);
        factory.setDataSource(dataSource);// 使用应用的dataSource替换quartz的dataSource
        return factory;
    }

    /**
     * 配置JobDetailFactory
     * JobDetailFactoryBean与CronTriggerFactoryBean相互依赖,注意bean的名称
     */
    @Bean
    public JobDetailFactoryBean buttonobDetail() {
        //集群模式下必须使用JobDetailFactoryBean，MethodInvokingJobDetailFactoryBean 类中的 methodInvoking 方法，是不支持序列化的
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        jobDetail.setDurability(true);
        jobDetail.setRequestsRecovery(true);
        jobDetail.setJobClass(ButtonTimerJob.class);
        jobDetail.setName("jiangfw");
        return jobDetail;
    }

    /**
     * 配置具体执行规则
     *
     * buttonobDetail 优先从beanName来查找对应的bean（从buttonobDetail的JobDetailFactoryBean中getObject出来。），
     * 如果没有同名的bean，则从同类型的bean中查找，如果查找多个同类型的则报错（比如xml中配置了springQtzJobDetail和springQtzJobDetail1）。
     */
    @Bean
    public CronTriggerFactoryBean cronJobTrigger(JobDetail buttonobDetail) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(buttonobDetail);
        trigger.setStartDelay(2000);   //延迟启动
        trigger.setCronExpression(cronExpression);  //从application.yml文件读取
        return trigger;
    }
}
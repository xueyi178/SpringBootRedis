package com.jbg.redis.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 *    多线程配置的 config 配置类
 * </p>
 *
 * @author xueyi
 * @since 2020/4/18 15:22
 */
@Configuration
@EnableAsync
@Slf4j
public class ThreadConfig {

    /**
     * 核心线程池大小
     */
    private int corePoolSize= 10;
    /**
     * 最大可创建的线程数
     */
    private int maxPoolSize= 40;
    /**
     * 队列最大长度
     */
    private int queueCapacity= 1000;
    private String namePrefix= "async-";
    /**
     * 线程池维护线程所允许的空闲时间
     */
    private int keepAliveSeconds = 300;


    @Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);
        //执行初始化
        executor.initialize();
        return executor;
    }
}

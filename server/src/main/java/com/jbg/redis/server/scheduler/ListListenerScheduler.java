package com.jbg.redis.server.scheduler;

import com.google.common.collect.Lists;
import com.jbg.redis.model.entity.Notice;
import com.jbg.redis.model.entity.User;
import com.jbg.redis.model.mapper.UserMapper;
import com.jbg.redis.server.service.impl.EmailServiceImpl;
import com.jbg.redis.server.thread.NoticeThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *      Redis列表-队列的消费者  Listener    监听器
 * </p>
 *
 * @author xueyi
 * @since 2020/4/12 16:10
 */
@Component
@EnableScheduling
public class ListListenerScheduler {

    private static final Logger log = LoggerFactory.getLogger(ListListenerScheduler.class);

    private static final String listenKey = "SpringBootRedis:List:Queue:Notice";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailServiceImpl emailService;

    /**
     * 描述: TODO：近实时的定时任务检测
     * 作者: xueyi
     * 日期: 2020/4/12 16:26
     * 参数: []
     * 返回: void
     */
    //@Scheduled(cron = "0/10 * * * * ?")
    //@Scheduled(cron = "0/59 * * * * ?")
    public void schedulerListenNotice() {
        log.info("----定时任务调度队列监听、检测通告消息，监听list中的数据");

        ListOperations<String, Notice> listOperations = redisTemplate.opsForList();
        Notice notice = listOperations.rightPop(listenKey);
        while (notice != null) {
            //TODO:发送给到所有的商户的邮箱
            this.noticeUser(notice);

            //TODO: 出队列
            notice = listOperations.rightPop(listenKey);
        }
    }

    // 发送通知给到不同的商户, 使用异步的方式去执行
    @Async("asyncServiceExecutor")
    public void noticeUser(Notice notice) {
        if (notice != null) {
            List<User> list = userMapper.selectList();

            /*//TODO:写法一-java8 stream api触发
            if (CollectionUtil.isNotEmpty(list)) {
                list.forEach(user -> emailService.emailUserNotice(notice, user));
            }*/


            //TODO:写法二-线程池/多线程触发, 不同的线程去处理不同的用户
            try {
                if (list!=null && !list.isEmpty()){
                    //建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
                    ExecutorService executorService= Executors.newFixedThreadPool(6);
                    List<NoticeThread> threads= Lists.newLinkedList();

                    list.forEach(user -> {
                        threads.add(new NoticeThread(user,notice,emailService));
                    });

                    executorService.invokeAll(threads);
                }
            }catch (Exception e){
                log.error("近实时的定时任务检测-发送通知给到不同的商户-法二-线程池/多线程触发-发生异常：",e.fillInStackTrace());
            }
        }
    }
}
























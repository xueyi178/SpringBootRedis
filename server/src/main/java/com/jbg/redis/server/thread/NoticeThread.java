package com.jbg.redis.server.thread;

import com.jbg.redis.model.entity.Notice;
import com.jbg.redis.model.entity.User;
import com.jbg.redis.server.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;

/**
 * <p>
 *      发送通过到商户多线程
 * </p>
 *
 * @author xueyi
 * @since 2020/4/18 15:42
 */
public class NoticeThread implements Callable<Boolean> {

    /**
     * 用户
     */
    private User user;

    /**
     * 用户
     */
    private Notice notice;

    @Autowired
    private EmailServiceImpl emailService;

    /**
     * 构造方法
     */
    public NoticeThread(User user, Notice notice, EmailServiceImpl emailService) {
        this.user = user;
        this.notice = notice;
        this.emailService = emailService;
    }

    @Override
    public Boolean call() throws Exception {
        boolean fag = emailService.emailUserNotice(notice, user);
        return fag ? true : false;
    }
}

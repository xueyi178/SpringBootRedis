package com.jbg.redis.server.service.impl;

import com.jbg.redis.model.entity.Notice;
import com.jbg.redis.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *      发送邮件的 ServiceImpl 实现类
 * </p>
 *
 * @author xueyi
 * @since 2020/4/12 16:29
 */
@Service
@Slf4j
public class EmailServiceImpl {

    public boolean emailUserNotice(Notice notice, User user) {
        log.info("----给指定的用户：{} 发送通告：{}",user,notice);
        return false;
    }
}

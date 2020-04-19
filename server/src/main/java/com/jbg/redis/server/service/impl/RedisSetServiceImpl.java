package com.jbg.redis.server.service.impl;

import com.jbg.redis.model.entity.User;
import com.jbg.redis.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 *      Redis中Set数据类型的存储 Service
 * </p>
 *
 * @author xueyi
 * @since 2020/4/18 16:29
 */
@Service
@Slf4j
public class RedisSetServiceImpl {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 1. 描述: 用户注册
     *    作者: xueyi
     *    日期: 2020/4/18 16:40
     *    参数: [user: 用户的实体类]
     *    返回: java.lang.Integer
     */
    public Integer registerUser(User user){
        boolean exist = this.exist(user.getEmail());
        if (exist) {
            return 0;
        }
        int i = userMapper.insertSelective(user);
        if (i > 0) {
            SetOperations<String, String> setOperations = redisTemplate.opsForSet();
            setOperations.add("SpringBoot:Redis:Set:User:Email", user.getEmail());
        }
        return user.getId();
    }

    /**
     * 查看邮箱是否存在于缓存中
     */
    public boolean exist(final String email){
        /*//TODO: 写法一
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        // 查看缓存中是否存在
        Boolean res = setOperations.isMember("SpringBoot:Redis:Set:User:Email", email);
        if (res){
            return true;
        }else{
            User user=userMapper.selectByEmail(email);
            if (user != null){
                setOperations.add("SpringBoot:Redis:Set:User:Email", user.getEmail());
                return true;
            }else{
                return false;
            }
        }*/

        //TODO:写法二
        SetOperations<String,String> setOperations=redisTemplate.opsForSet();
        Long size=setOperations.size("SpringBoot:Redis:Set:User:Email");
        if (size > 0 &&  setOperations.isMember("SpringBoot:Redis:Set:User:Email",email)){
            return true;
        }else{
            User user=userMapper.selectByEmail(email);
            if (user!=null){
                setOperations.add("SpringBoot:Redis:Set:User:Email", user.getEmail());
                return true;
            }else{
                return false;
            }
        }
    }

    /**
     * 取出缓存中已注册的用户的邮箱列表
     */
    public Set<String> getEmails() throws Exception{
        // 取出set中所有的key
        SetOperations<String,String> setOperations=redisTemplate.opsForSet();
        /*SetOperations<String,String> setOperations=redisTemplate.opsForSet();
        return setOperations.members(Constant.RedisSetKey);*/
        //return redisTemplate.opsForSet().members("SpringBoot:Redis:Set:User:Email");

        // 随机取出set中的key
        //return setOperations.randomMembers(Constant.RedisSetKey,setOperations.size(Constant.RedisSetKey));
        return (Set<String>) setOperations.randomMembers("SpringBoot:Redis:Set:User:Email", setOperations.size("SpringBoot:Redis:Set:User:Email"));

    }



}

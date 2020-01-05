package com.jbg.redis.model.mapper;

import com.jbg.redis.model.entity.PhoneFare;

public interface PhoneFareMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PhoneFare record);

    int insertSelective(PhoneFare record);

    PhoneFare selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PhoneFare record);

    int updateByPrimaryKey(PhoneFare record);
}
package com.jbg.redis.model.mapper;

import com.jbg.redis.model.entity.RedRobRecord;

public interface RedRobRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedRobRecord record);

    int insertSelective(RedRobRecord record);

    RedRobRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedRobRecord record);

    int updateByPrimaryKey(RedRobRecord record);
}
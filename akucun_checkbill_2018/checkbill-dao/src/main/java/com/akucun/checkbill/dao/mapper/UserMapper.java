package com.akucun.checkbill.dao.mapper;

//import com.akucun.checkbill.dao.mapper.BaseMapper;
import com.akucun.checkbill.dao.entity.User;

public interface UserMapper //extends BaseMapper<User>
{
    public User queryUserById(String id);
}
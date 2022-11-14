package com.akucun.checkbill.service.impl;

import com.akucun.checkbill.dao.entity.User;
import com.akucun.checkbill.dao.mapper.UserMapper;
import com.akucun.checkbill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@com.alibaba.dubbo.config.annotation.Service(version="",gr)
//	@Reference(group="${}" ,version = "")
public class UserServiceImpl // extends BaseServiceImpl<User>
 implements IUserService
{

    @Autowired
    UserMapper userMapper;

    @Override
    public User queryUserById(String id) {
        return userMapper.queryUserById(id);
    }
}

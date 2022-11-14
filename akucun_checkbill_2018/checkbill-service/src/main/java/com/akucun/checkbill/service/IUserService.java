package com.akucun.checkbill.service;

import com.akucun.checkbill.service.IBaseService;
import com.akucun.checkbill.dao.entity.User;

public interface IUserService //extends IBaseService<User>
{
    public User queryUserById(String id);
}

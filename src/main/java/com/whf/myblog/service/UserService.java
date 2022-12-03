package com.whf.myblog.service;

import com.whf.myblog.po.User;

public interface UserService {
    User checkUser(String userName, String password);
}

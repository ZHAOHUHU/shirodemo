package shenzhen.teamway.service;

import shenzhen.teamway.bean.User;

public interface UserService {
    /*
    根据用户名查询用户
     */
    User selectByName(String name);
}

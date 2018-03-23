package shenzhen.teamway.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shenzhen.teamway.bean.User;
import shenzhen.teamway.dao.UserMapper;
import shenzhen.teamway.service.UserService;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User selectByName(String name) {
        return userMapper.selectByName(name);
    }
}

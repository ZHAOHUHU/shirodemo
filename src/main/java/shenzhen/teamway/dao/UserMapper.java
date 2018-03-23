package shenzhen.teamway.dao;

import java.util.List;
import shenzhen.teamway.bean.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    User selectByPrimaryKey(Integer id);
    User selectByName(String name);

    List<User> selectAll();

    int updateByPrimaryKey(User record);
}
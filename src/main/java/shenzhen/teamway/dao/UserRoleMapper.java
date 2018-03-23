package shenzhen.teamway.dao;

import java.util.List;
import shenzhen.teamway.bean.UserRole;

public interface UserRoleMapper {
    int insert(UserRole record);

    List<UserRole> selectAll();
}
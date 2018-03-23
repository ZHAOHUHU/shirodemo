package shenzhen.teamway.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shenzhen.teamway.bean.RoleResources;

public interface RoleResourcesMapper {
    int deleteByPrimaryKey(@Param("roleid") Integer roleid, @Param("resourcesid") Integer resourcesid);

    int insert(RoleResources record);

    List<RoleResources> selectAll();
}
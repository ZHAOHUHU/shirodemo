package shenzhen.teamway.dao;

import java.util.List;
import java.util.Map;

import shenzhen.teamway.bean.Resources;

public interface ResourcesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Resources record);

    Resources selectByPrimaryKey(Integer id);

    List<Resources> selectAll();

    List<Resources> selectResourcesByUserId(Map map);

    int updateByPrimaryKey(Resources record);
}
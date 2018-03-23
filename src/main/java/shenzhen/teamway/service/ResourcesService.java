package shenzhen.teamway.service;

import shenzhen.teamway.bean.Resources;

import java.util.List;
import java.util.Map;

public interface ResourcesService {


    List<Resources> queryAll();

    /*
    更加用户id查询用户对应的权限
     */
    List<Resources> loadResources(Map map);
}

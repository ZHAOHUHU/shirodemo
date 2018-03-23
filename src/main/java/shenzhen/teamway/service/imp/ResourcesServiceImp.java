package shenzhen.teamway.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shenzhen.teamway.bean.Resources;
import shenzhen.teamway.dao.ResourcesMapper;
import shenzhen.teamway.service.ResourcesService;

import java.util.List;
import java.util.Map;

@Service
public class ResourcesServiceImp implements ResourcesService {

    @Autowired
    ResourcesMapper resourcesMapper;

    @Override
    public List<Resources> queryAll() {
        return resourcesMapper.selectAll();
    }

    @Override
    public List<Resources> loadResources(Map map) {
        return resourcesMapper.selectResourcesByUserId(map);
    }
}

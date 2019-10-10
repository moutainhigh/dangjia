package com.dangjia.acg.service.config;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IServiceTypeMapper;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.config.ServiceType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @类 名： ServiceTypeService.java
 * @功能描述： 服务类型配配置
 */
@Service
public class ServiceTypeService {
    private static Logger logger = LoggerFactory.getLogger(ServiceTypeService.class);
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IServiceTypeMapper iServiceTypeMapper;

    /**
     * 根据ID查询服务详情
     * @param id
     * @return
     */
    public ServerResponse selectServiceTypeById( String id) {
        try {
            ServiceType serviceType=iServiceTypeMapper.selectByPrimaryKey(id);
            return ServerResponse.createBySuccess("查询成功", serviceType);
        } catch (Exception e) {
            logger.error("selectServiceTypeById查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 查询服务类型
     * @param pageDTO
     * @return
     */
    public ServerResponse<PageInfo> selectServiceTypeList( PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ServiceType> serviceTypeList = iServiceTypeMapper.getServiceTypeList();
            List<Map<String, Object>> list = new ArrayList<>();
            for (ServiceType serviceType : serviceTypeList) {
                Map<String, Object> map = BeanUtils.beanToMap(serviceType);
                map.put("imageUrl",address+serviceType.getImage());
                list.add(map);
            }
            PageInfo pageResult = new PageInfo(serviceTypeList);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改服务类型
     * @param id
     * @param name
     * @param image
     * @return
     */
    public ServerResponse updateServiceType( String id, String name, String image) {
        try{
            ServiceType serviceType=new ServiceType();
            serviceType.setId(id);
            serviceType.setName(name);
            serviceType.setImage(image);
            iServiceTypeMapper.updateByPrimaryKeySelective(serviceType);
            return ServerResponse.createBySuccess("修改成功", serviceType.getId());
        } catch (Exception e) {
            logger.error("修改失败",e);
            return ServerResponse.createByErrorMessage("修改失败");
        }

    }

    /**
     * 添加服务类型
     * @param name
     * @param image
     * @return
     */
    public ServerResponse insertServiceType(String name, String image) {
        try{
            ServiceType serviceType=new ServiceType();
            serviceType.setName(name);
            serviceType.setImage(image);
            iServiceTypeMapper.insert(serviceType);
            return ServerResponse.createBySuccess("新增成功", serviceType.getId());
        } catch (Exception e) {
            logger.error("新增失败",e);
            return ServerResponse.createByErrorMessage("新增失败");
        }

    }

    /**
     * 删除服务类型
     * @param id
     * @return
     */
    public ServerResponse deleteServiceType( String id) {
        try{
            iServiceTypeMapper.deleteById(id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("删除成功",e);
            return ServerResponse.createByErrorMessage("删除失败");
        }

    }
}

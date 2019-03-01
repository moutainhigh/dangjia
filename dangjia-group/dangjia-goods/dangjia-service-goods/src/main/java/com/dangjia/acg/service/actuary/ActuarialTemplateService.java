package com.dangjia.acg.service.actuary;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.basics.ActuarialTemplateDTO;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @类 名： ProductServiceImpl
 * @功能描述： 商品service实现类
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class ActuarialTemplateService {

    @Autowired
    private IActuarialTemplateMapper iActuarialTemplateMapper;
    @Autowired
    private IBudgetWorkerMapper iBudgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    private static Logger LOG = LoggerFactory.getLogger(ActuarialTemplateService.class);

    /**
     * 查询所有精算模板
     *
     * @param pageDTO
     * @param workerTypeId
     * @param stateType    查询功能，state_type传1表示查询所有启用的，0为所有停用的，2或者不传为查询所有
     * @param name
     * @return
     */
    public ServerResponse<PageInfo> queryActuarialTemplate(PageDTO pageDTO, String workerTypeId, String stateType, String name) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ActuarialTemplate> tList = iActuarialTemplateMapper.query(StringUtils.isBlank(workerTypeId) ? null : workerTypeId,
                    StringUtils.isBlank(stateType) ? null : stateType, name);
            if (tList == null || tList.size() <= 0) {
                return ServerResponse.createByErrorMessage("查无数据！");
            }
            PageInfo pageResult = new PageInfo(tList);
            pageResult.setList(tList);
            return ServerResponse.createBySuccess("查询精算模版成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询精算模版失败");
        }
    }

    /**
     * 新增精算模板
     *
     * @param userId
     * @param name
     * @param styleId
     * @param styleName
     * @param applicableArea
     * @param stateType
     * @param workerTypeName
     * @param workerTypeId
     * @return
     */
    public ServerResponse<String> insertActuarialTemplate(String userId, String name, String styleId, String styleName, String applicableArea,
                                                          Integer stateType, String workerTypeName, Integer workerTypeId) {
        List<ActuarialTemplate> actuarialTemplateList = iActuarialTemplateMapper.queryByName(workerTypeId, name);
        if (actuarialTemplateList.size() > 0)
            return ServerResponse.createByErrorMessage("精算名字不能重复");
        ActuarialTemplate t = new ActuarialTemplate();
        t.setUserId(userId);
        t.setName(name);
        t.setStyleId(styleId);
        t.setStyleName(styleName);
        t.setApplicableArea(applicableArea);
        t.setStateType(stateType);
        t.setNumberOfUse(0);
        t.setWorkerTypeName(workerTypeName);
        t.setWorkerTypeId(workerTypeId);
        int isok = iActuarialTemplateMapper.insert(t);
        if (isok > 0) {
            return ServerResponse.createBySuccess("新增精算模版成功", t.getId());
        }
        return ServerResponse.createByErrorMessage("新增精算模版失败");
    }

    /**
     * 修改精算模版 根据精算模版ID修改
     *
     * @param id
     * @param name
     * @param styleId
     * @param styleName
     * @param applicableArea
     * @param stateType
     * @return
     */
    public ServerResponse<String> updateActuarialTemplate(String id, String name, String styleId, String styleName, String applicableArea, Integer stateType) {
        try {
            if (!StringUtils.isNotBlank(id)) {
                return ServerResponse.createByErrorMessage("修改精算模版参数错误");
            }
            ActuarialTemplate oldActuarialTemplate = iActuarialTemplateMapper.selectByPrimaryKey(id);
            List<ActuarialTemplate> actuarialTemplateList = iActuarialTemplateMapper.queryByName(oldActuarialTemplate.getWorkerTypeId(), name);
            LOG.info("actuarialTemplateList:" + actuarialTemplateList.size());
            if (!oldActuarialTemplate.getName().equals(name)) {
                if (actuarialTemplateList.size() > 0)
                    return ServerResponse.createByErrorMessage("精算名字已存在");
            }
            ActuarialTemplate t = new ActuarialTemplate();
            t.setId(id);
            t.setName(name);
            t.setStyleId(styleId);
            t.setStyleName(styleName);
            t.setApplicableArea(applicableArea);
            t.setStateType(stateType);
            int isok = iActuarialTemplateMapper.updateByPrimaryKeySelective(t);
            if (isok > 0) {
                return ServerResponse.createBySuccessMessage("修改精算模版成功");
            }
            return ServerResponse.createByErrorMessage("修改精算模版失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改精算模版失败");
        }
    }

    /**
     * 删除精算模板
     *
     * @param id
     * @return
     */
    public ServerResponse<String> deleteActuarialTemplate(String id) {
        try {
            if (!StringUtils.isNotBlank(id)) {
                return ServerResponse.createByErrorMessage("删除精算模版参数错误");
            }
            int isok = iActuarialTemplateMapper.deleteByPrimaryKey(id);
            if (isok > 0) {
                iBudgetMaterialMapper.deleteBytemplateId(id);
                iBudgetWorkerMapper.deleteBytemplateId(id);
                return ServerResponse.createBySuccessMessage("删除精算模版成功");
            } else {
                return ServerResponse.createByErrorMessage("删除精算模版失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除精算模版失败");
        }
    }

}


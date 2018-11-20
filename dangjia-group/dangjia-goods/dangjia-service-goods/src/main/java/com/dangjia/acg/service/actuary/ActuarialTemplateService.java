package com.dangjia.acg.service.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.basics.ActuarialTemplateDTO;
import com.dangjia.acg.mapper.actuary.IActuarialTemplateMapper;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 查询精算模版
     * @param pageNum
     * @param pageSize
     * @param workerTypeId
     * @param stateType
     * @return
     */
    public ServerResponse<PageInfo> queryActuarialTemplate(Integer pageNum, Integer pageSize, String workerTypeId, String stateType,String name) {
       try {
           if (pageNum == null) {
               pageNum = 1;
           }
           if (pageSize == null) {
               pageSize = 10;
           }
           PageHelper.startPage(pageNum, pageSize);
           List<ActuarialTemplate> tList = iActuarialTemplateMapper.query(StringUtils.isBlank(workerTypeId) ? null : workerTypeId,
                   StringUtils.isBlank(stateType) ? null : stateType,name);
           if (tList == null || tList.size() <= 0) {
               return ServerResponse.createByErrorMessage("查无数据！");
           }

           List<ActuarialTemplateDTO> actuarialTemplateResults = new ArrayList<ActuarialTemplateDTO>();
           PageInfo pageResult = new PageInfo(tList);
           pageResult.setList(tList);
           return ServerResponse.createBySuccess("查询精算模版成功", pageResult);
       }catch (Exception e){
           e.printStackTrace();
           return ServerResponse.createBySuccessMessage("查询精算模版失败");
       }
    }

    /**
     * 新增精算模板
     * @param userId
     * @param name
     * @param styleType
     * @param applicableArea
     * @param stateType
     * @param workerTypeId
     * @return
     */
    public ServerResponse<String> insertActuarialTemplate(String userId, String name, String styleType, String applicableArea,
                                                          Integer stateType, String workerTypeName,Integer workerTypeId) {
        ActuarialTemplate t = new ActuarialTemplate();
        t.setUserId(userId);
        t.setName(name);
        t.setStyleType(styleType);
        t.setApplicableArea(applicableArea);
        t.setStateType(stateType);
        t.setWorkerTypeName(workerTypeName);
        t.setWorkerTypeId(workerTypeId);
        int isok = iActuarialTemplateMapper.insert(t);
        if (isok > 0) {
            return ServerResponse.createBySuccess("新增精算模版成功",t.getId());
        }
        return ServerResponse.createByErrorMessage("新增精算模版失败");

    }
    //修改精算模版
    public ServerResponse<String> updateActuarialTemplate(String id, String name, String styleType, String applicableArea, Integer stateType, String workingProcedure) {
      try {
          if (!StringUtils.isNotBlank(id)) {
              return ServerResponse.createByErrorMessage("修改精算模版参数错误");
          }
          ActuarialTemplate t = new ActuarialTemplate();
          t.setId(id);
          t.setName(name);
          t.setStyleType(styleType);
          t.setApplicableArea(applicableArea);
          t.setStateType(stateType);
          int isok = iActuarialTemplateMapper.updateByPrimaryKeySelective(t);
          if (isok > 0) {
              return ServerResponse.createBySuccessMessage("修改精算模版成功");
          }
          return ServerResponse.createByErrorMessage("修改精算模版失败");
      }catch (Exception e){
          e.printStackTrace();
          return ServerResponse.createByErrorMessage("修改精算模版失败");
      }
    }

    //删除精算模版
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
           }else{
               return ServerResponse.createByErrorMessage("删除精算模版失败");
           }
       }catch (Exception e) {
           e.printStackTrace();
           return ServerResponse.createBySuccessMessage("删除精算模版失败");
       }
    }

}


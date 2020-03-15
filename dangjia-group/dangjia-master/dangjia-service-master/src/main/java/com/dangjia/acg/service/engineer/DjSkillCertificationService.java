package com.dangjia.acg.service.engineer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.DjSkillCertificationDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.engineer.DjSkillCertificationMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.engineer.DjSkillCertification;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 11/12/2019
 * Time: 上午 10:15
 */
@Service
public class DjSkillCertificationService {

    @Autowired
    private DjSkillCertificationMapper djSkillCertificationMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWorkerTypeMapper iWorkerTypeMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 技能认证待选列表
     * @param workerTypeId
     * @param searchKey
     * @param skillCertificationId
     * @return
     */
    public ServerResponse querySkillsCertificationWaitingList(PageDTO pageDTO, Integer workerTypeId, String searchKey, String skillCertificationId, String cityId) {
        try {
            Map<String,Object> map=new HashMap<>();
            Example example=new Example(WorkerType.class);
            example.createCriteria().andEqualTo(WorkerType.DATA_STATUS,0);
            example.orderBy(WorkerType.SORT).asc();
            List<WorkerType> workerTypes = iWorkerTypeMapper.selectByExample(example);
            map.put("workerTypes",workerTypes);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsProductTemplate> djBasicsProductTemplates =
                    iMasterProductTemplateMapper.querySkillsCertificationWaitingList(workerTypeId, skillCertificationId, searchKey, cityId);
            if(djBasicsProductTemplates.size()>0){
                PageInfo pageInfo=new PageInfo(djBasicsProductTemplates);
                map.put("djSkillCertifications",pageInfo);
            }
            return ServerResponse.createBySuccess("查询成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /***
     *
     * @param pageDTO
     * @param workerTypeId
     * @param searchKey
     * @param skillCertificationId
     * @param cityId
     * @return
     */
    public ServerResponse queryWorkerTypeSkillWaitingList(PageDTO pageDTO, Integer workerTypeId, String searchKey, String skillCertificationId, String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsProductTemplate> djBasicsProductTemplates =
                    iMasterProductTemplateMapper.querySkillsCertificationWaitingList(workerTypeId, skillCertificationId, searchKey, cityId);
            if(djBasicsProductTemplates.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo=new PageInfo(djBasicsProductTemplates);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 技能认证已选列表
     * @param pageDTO
     * @param searchKey
     * @param skillCertificationId
     * @return
     */
    public ServerResponse querySkillCertificationSelectedList(PageDTO pageDTO, String searchKey, String skillCertificationId, Integer type, String cityId) {
        try {
            Example example = new Example(DjSkillCertification.class);
            if(type==2) {
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(DjSkillCertification.SKILL_CERTIFICATION_ID, skillCertificationId)
                        .andEqualTo(DjSkillCertification.TYPE, type)
                        .andEqualTo(DjSkillCertification.CITY_ID, cityId);
                if (StringUtils.isNotBlank(searchKey))
                    criteria.andCondition("CONCAT(product_name,product_sn) like CONCAT('%','" + searchKey + "','%')");
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
                if(djSkillCertifications.size()<=0)
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
                PageInfo pageInfo=new PageInfo(djSkillCertifications);
                return ServerResponse.createBySuccess("查询成功",pageInfo);
            }else{
                Map<String,Object> map=new HashMap<>();
                List<WorkerType> workerTypes = iWorkerTypeMapper.querySkillCertificationSelectedList(skillCertificationId);
                map.put("workerTypes",workerTypes);
                example.createCriteria().andEqualTo(DjSkillCertification.SKILL_CERTIFICATION_ID, skillCertificationId)
                        .andEqualTo(DjSkillCertification.TYPE, 1)
                        .andEqualTo(DjSkillCertification.CITY_ID, cityId);
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
                if(djSkillCertifications.size()>0){
                    PageInfo pageInfo=new PageInfo(djSkillCertifications);
                    map.put("djSkillCertifications",pageInfo);
                }
                return ServerResponse.createBySuccess("查询成功",map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 技能认证
     * @param jsonStr
     * @param skillCertificationId
     * @return
     */
    public ServerResponse insertSkillCertification(String jsonStr, String skillCertificationId, String cityId) {
        try {
            Member member = iMemberMapper.selectByPrimaryKey(skillCertificationId);
            if(member.getRealNameState()==3){
                JSONArray jsonArray = JSONArray.parseArray(jsonStr);
                jsonArray.forEach(str ->{
                    JSONObject obj = (JSONObject) str;
                    DjSkillCertification djSkillCertification=new DjSkillCertification();
                    djSkillCertification.setSkillCertificationId(skillCertificationId);
                    djSkillCertification.setType(obj.getInteger("type"));
                    djSkillCertification.setProductType(obj.getInteger("productType"));
                    djSkillCertification.setProdTemplateId(obj.getString("id"));
                    djSkillCertification.setProductName(obj.getString("name"));
                    djSkillCertification.setProductSn(obj.getString("productSn"));
                    djSkillCertification.setCityId(cityId);
                    djSkillCertificationMapper.insert(djSkillCertification);
                });
            }else {
                return ServerResponse.createByErrorMessage("该工匠暂未实名认证,请联系客服确认！");
            }
            return ServerResponse.createBySuccessMessage("认证成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("认证失败");
        }
    }


    /**
     * 工种技能包配置列表
     * @return
     */
    public ServerResponse queryWorkerTypeSkillPackConfigurationList() {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Example example=new Example(WorkerType.class);
            example.createCriteria().andEqualTo(WorkerType.DATA_STATUS,0);
            example.orderBy(WorkerType.SORT).asc();
            List<WorkerType> workerTypes = iWorkerTypeMapper.selectByExample(example);
            workerTypes.forEach(workerType -> {
                workerType.setImageUrl(workerType.getImage());
                workerType.setImage(imageAddress+workerType.getImage());
                if(StringUtils.isEmpty(workerType.getSkillPackName())){
                    workerType.setSkillPackName("");
                }
            });
            return ServerResponse.createBySuccess("查询成功",workerTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 工种技能包配置详情
     * @param workerTypeId
     * @return
     */
    public ServerResponse queryWorkerTypeSkillPackConfigurationDetail(String workerTypeId) {
        try {
            WorkerType workerType = iWorkerTypeMapper.selectByPrimaryKey(workerTypeId);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            workerType.setImageUrl(workerType.getImage());
            workerType.setImage(imageAddress+workerType.getImage());
            return ServerResponse.createBySuccess("查询成功",workerType);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 工种技能包配置
     * @param jsonStr
     * @param workerType
     * @return
     */
    public ServerResponse insertWorkerTypeSkillPackConfiguration(String jsonStr, WorkerType workerType, String cityId) {
        try {
            WorkerType oldWorkerType = iWorkerTypeMapper.selectByPrimaryKey(workerType.getId());
            if(oldWorkerType!=null) {
                if (!workerType.getName().equals(oldWorkerType.getName())) {
                    Example example = new Example(WorkerType.class);
                    example.createCriteria().andEqualTo(WorkerType.NAME, workerType.getName())
                            .andEqualTo(WorkerType.DATA_STATUS, 0);
                    if (iWorkerTypeMapper.selectByExample(example).size() > 0) {
                        return ServerResponse.createByErrorMessage("工种已存在");
                    }
                }
                iWorkerTypeMapper.updateByPrimaryKeySelective(workerType);
            }else{
                Example example=new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.DATA_STATUS,0);
                example.orderBy(WorkerType.TYPE).desc();
                WorkerType workerType1 = iWorkerTypeMapper.selectOneByExample(example);
                workerType.setType(workerType1.getType()+1);
                iWorkerTypeMapper.insert(workerType);
            }
            JSONArray jsonArray = JSONArray.parseArray(jsonStr);
            jsonArray.forEach(str ->{
                JSONObject obj = (JSONObject) str;
                DjSkillCertification djSkillCertification=new DjSkillCertification();
                djSkillCertification.setSkillCertificationId(workerType.getId());
                djSkillCertification.setType(2);
                djSkillCertification.setProductType(obj.getInteger("productType"));
                djSkillCertification.setProdTemplateId(obj.getString("id"));
                djSkillCertification.setProductName(obj.getString("name"));
                djSkillCertification.setProductSn(obj.getString("productSn"));
                djSkillCertification.setCityId(cityId);
                djSkillCertificationMapper.insert(djSkillCertification);
            });
            iWorkerTypeMapper.updateByPrimaryKeySelective(workerType);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("操作失败");
        }
    }


    /**
     * 技能删除
     * @param id
     * @return
     */
    public ServerResponse deleteSkillCertification(String id) {
        try {
            djSkillCertificationMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除成功 ");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败 ");
        }
    }
}

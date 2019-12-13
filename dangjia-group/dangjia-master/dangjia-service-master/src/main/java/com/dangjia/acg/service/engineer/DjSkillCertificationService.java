package com.dangjia.acg.service.engineer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.engineer.DjSkillCertificationMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.modle.engineer.DjSkillCertification;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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

    /**
     * 技能认证待选列表
     * @param workerTypeId
     * @param searchKey
     * @param workerId
     * @return
     */
    public ServerResponse querySkillsCertificationWaitingList(PageDTO pageDTO, Integer workerTypeId, String searchKey, String workerId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsProductTemplate> djBasicsProductTemplates =
                    iMasterProductTemplateMapper.querySkillsCertificationWaitingList(workerTypeId, workerId, searchKey);
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
     * @param workerId
     * @return
     */
    public ServerResponse querySkillCertificationSelectedList(PageDTO pageDTO, String searchKey, String workerId) {
        try {
            Example example=new Example(DjSkillCertification.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(DjSkillCertification.WORKER_ID,workerId);
            if(StringUtils.isNotBlank(searchKey))
                criteria.andCondition("CONCAT(product_name,product_sn) like CONCAT('%','" + searchKey + "','%')");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
            if(djSkillCertifications.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo=new PageInfo(djSkillCertifications);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 技能认证
     * @param jsonStr
     * @param workerId
     * @return
     */
    public ServerResponse insertSkillCertification(String jsonStr, String workerId) {
        try {
            Member member = iMemberMapper.selectByPrimaryKey(workerId);
            if(member.getRealNameState()==3){
                JSONArray jsonArray = JSONArray.parseArray(jsonStr);
                jsonArray.forEach(str ->{
                    JSONObject obj = (JSONObject) str;
                    DjSkillCertification djSkillCertification=new DjSkillCertification();
                    djSkillCertification.setWorkerId(workerId);
                    djSkillCertification.setProductId(obj.getString("id"));
                    djSkillCertification.setProductName(obj.getString("name"));
                    djSkillCertification.setProductSn(obj.getString("productSn"));
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
}

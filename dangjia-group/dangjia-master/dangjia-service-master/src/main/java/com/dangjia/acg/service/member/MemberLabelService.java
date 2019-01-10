package com.dangjia.acg.service.member;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.MemberCustomerDTO;
import com.dangjia.acg.dto.member.MemberLabelDTO;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberLabel;
import com.dangjia.acg.service.core.WorkerTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 */
@Service
public class MemberLabelService {
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    protected static final Logger LOG = LoggerFactory.getLogger(MemberLabelService.class);

    //查询所有的标签
    public ServerResponse<PageInfo> getMemberLabelList(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<MemberLabel> parentMemberLabelList = iMemberLabelMapper.getAllParentLabel();
            LOG.info("parentMemberLabelList:" + parentMemberLabelList.size());
            List<MemberLabelDTO> mlDTOList = new ArrayList<>();
            for (MemberLabel member : parentMemberLabelList) {
                MemberLabelDTO memberLabelDTO = new MemberLabelDTO();
                memberLabelDTO.setParentId(member.getParentId());
                memberLabelDTO.setParentName(member.getParentName());
                List<MemberLabel> childMemberLabelList = iMemberLabelMapper.getChildLabelByParentId(member.getParentId());
                memberLabelDTO.setChildMemberLabelList(childMemberLabelList);
                mlDTOList.add(memberLabelDTO);
            }
            PageInfo pageResult = new PageInfo(mlDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 添加/修改商品标签
     *
     * @param jsonStr
     * @return
     */
    public ServerResponse setMemberLabel(String jsonStr) {
        try {
            LOG.info("jsonStr :" + jsonStr);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String parentId = jsonObject.getString("parentId");
            String parentName = jsonObject.getString("parentName");

            JSONArray childLabelList = JSONArray.parseArray(jsonObject.getString("childLabelList"));
            MemberLabel childMemberLabel = null;
            MemberLabel parentMemberLabel = null;  //数据库里存在的 父标签对象
            if (!StringUtils.isNotBlank(parentId))//没有id则新增
            {
                if (iMemberLabelMapper.getLabelByParentName(parentName) != null) {
                    if (iMemberLabelMapper.getLabelByParentName(parentName).size() > 0)
                        return ServerResponse.createByErrorMessage("标签名称已存在");
                }
                parentMemberLabel = new MemberLabel();
                parentMemberLabel.setParentId(parentMemberLabel.getId());
                parentMemberLabel.setParentName(parentName);
                if (childLabelList.size() == 0) //如果增加 标签值就需要 直接插入 父标签
                    iMemberLabelMapper.insertSelective(parentMemberLabel);
            } else {//修改
                List<MemberLabel> memberLabelList = iMemberLabelMapper.getChildLabelByParentId(parentId);
                if (memberLabelList.size() == 0)
                    return ServerResponse.createByErrorMessage("没有该标签");
                else {
                    parentMemberLabel = memberLabelList.get(0);//数据库里存在的 父标签对象
                }

                //查询 要修改的名字 是否已存在
                List<MemberLabel> memberLabelNameList = iMemberLabelMapper.getLabelByParentName(parentName);
                //遍历 查找 不能重复修改 ，， 标签名称已经存在
                for (MemberLabel memberLabel : memberLabelNameList) {
                    //如果 要修改的 name 和 数据库里的不一样 才是修改
                    if(!parentName.equals(parentMemberLabel.getParentName()))
                    {
                        //查找 所有父标签名字相同的， 并且  存在不同 父标签id
                        if (memberLabel.getName().equals(parentMemberLabel.getParentName())
                                && memberLabel.getId().equals(parentId)) {
                            return ServerResponse.createByErrorMessage("标签名称已存在");
                        }
                    }
                }

                for (MemberLabel memberLabel : memberLabelList) {
                    memberLabel.setParentName(parentName);
                    iMemberLabelMapper.updateByPrimaryKeySelective(memberLabel);
                    parentMemberLabel = memberLabel;
                }
            }

            for (int i = 0; i < childLabelList.size(); i++) {
                JSONObject obj = childLabelList.getJSONObject(i);
                String childId = obj.getString("childId");//子标签id
                String childName = obj.getString("childName");//子标签name
                if (!StringUtils.isNotBlank(childId))//没有id则新增
                {
                    childMemberLabel = new MemberLabel();
                    childMemberLabel.setName(childName);
                    childMemberLabel.setParentId(parentMemberLabel.getParentId());
                    childMemberLabel.setParentName(parentMemberLabel.getParentName());
                    iMemberLabelMapper.insertSelective(childMemberLabel);
                } else {//修改
                    childMemberLabel = iMemberLabelMapper.selectByPrimaryKey(childId);
                    if (childMemberLabel == null)
                        return ServerResponse.createByErrorMessage("没有该标签值");

                    if (!childMemberLabel.getName().equals(childName)) {
                        if (iMemberLabelMapper.getLabelByName(childName).size() > 0)
                            return ServerResponse.createByErrorMessage("标签值名称已存在");
                    }
                    childMemberLabel.setName(childName);
                    childMemberLabel.setModifyDate(new Date());
                    iMemberLabelMapper.updateByPrimaryKeySelective(childMemberLabel);
                }
            }

            return ServerResponse.createBySuccessMessage("操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


}

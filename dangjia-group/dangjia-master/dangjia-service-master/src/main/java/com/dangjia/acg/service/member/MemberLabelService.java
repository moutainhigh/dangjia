package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
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
    private ConfigUtil configUtil;

    @Autowired
    private ICustomerMapper customerMapper;
    @Autowired
    private ICustomerRecordMapper customerImageMapper;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private WorkerTypeService workerTypeService;
    @Autowired
    private RedisClient redisClient;

    protected static final Logger LOG = LoggerFactory.getLogger(MemberLabelService.class);

    //查询所有的标签
    public ServerResponse<PageInfo> getMemberLabelList(PageDTO pageDTO) {
        try {
            if (pageDTO == null) {
                pageDTO = new PageDTO();
            }
            if (pageDTO.getPageNum() == null) {
                pageDTO.setPageNum(1);
            }
            if (pageDTO.getPageSize() == null) {
                pageDTO.setPageSize(10);
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

            List<MemberLabel> memberLabelList = iMemberLabelMapper.getLabel();

            LOG.info("memberLabelList:" + memberLabelList.size());
            for (MemberLabel memberLabel : memberLabelList) {
                Map<String, Object> map = new HashMap<String, Object>();
                if (!StringUtils.isNotBlank(memberLabel.getId())) {
                    map.put("id", "");
                    map.put("name", "");
                    map.put("valueArr", "");
                } else {
                    map.put("id", memberLabel.getId());
                    map.put("name", memberLabel.getName());
                    map.put("valueArr", memberLabel.getValueArr());
                }
                mapList.add(map);
            }
            PageInfo pageResult = new PageInfo(memberLabelList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 添加/修改商品标签
     *
     * @param memberLabel
     * @return
     */
    public ServerResponse setMemberLabel(MemberLabel memberLabel) {
        try {
            MemberLabel tmpMemberLabel = null;
            LOG.info("memberLabel:" + memberLabel);
            LOG.info("memberLabel:" + memberLabel.getId());
            LOG.info("memberLabel getName:" + memberLabel.getName());
            LOG.info("memberLabel:" + memberLabel.getValueArr());
            LOG.info("memberLabel:" + iMemberLabelMapper);

            List<MemberLabel> sll = iMemberLabelMapper.getLabel();
            LOG.info("sll:" + sll);
            if (!StringUtils.isNotBlank(memberLabel.getId()))//没有id则新增
            {
                if (iMemberLabelMapper.getLabelByName(memberLabel.getName()) != null) {
                    if (iMemberLabelMapper.getLabelByName(memberLabel.getName()).size() > 0)
                        return ServerResponse.createByErrorMessage("标签名称已存在");
                }

                tmpMemberLabel = new MemberLabel();
                tmpMemberLabel.setName(memberLabel.getName());
                tmpMemberLabel.setValueArr(memberLabel.getValueArr());
                iMemberLabelMapper.insert(tmpMemberLabel);
            } else {//修改
                tmpMemberLabel = iMemberLabelMapper.selectByPrimaryKey(memberLabel.getId());
                if (tmpMemberLabel == null)
                    return ServerResponse.createByErrorMessage("没有该标签");

                if (!tmpMemberLabel.getName().equals(memberLabel.getName())) {
                    if (iMemberLabelMapper.getLabelByName(memberLabel.getName()).size() > 0)
                        return ServerResponse.createByErrorMessage("标签名称已存在");
                }
                tmpMemberLabel.setName(memberLabel.getName());
                tmpMemberLabel.setValueArr(memberLabel.getValueArr());
                tmpMemberLabel.setModifyDate(new Date());
                iMemberLabelMapper.updateByPrimaryKeySelective(tmpMemberLabel);
            }
//            return ServerResponse.createBySuccessMessage("ok");
            return ServerResponse.createBySuccessMessage("操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


}

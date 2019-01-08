package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.modle.member.CustomerRecord;
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
public class CustomerRecordService {
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private ICustomerMapper customerMapper;
    @Autowired
    private ICustomerRecordMapper iCustomerRecordMapper;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private WorkerTypeService workerTypeService;
    @Autowired
    private RedisClient redisClient;

    protected static final Logger LOG = LoggerFactory.getLogger(CustomerRecordService.class);


    /**
     * 查询业主沟通记录，指定业主id的所有记录 （null：查所有记录）
     * @param pageDTO
     * @param memberId
     * @return
     */
    public ServerResponse<PageInfo> getCustomerRecordList(PageDTO pageDTO,String memberId) {
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

            List<CustomerRecord> customerRecordList = iCustomerRecordMapper.getCustomerRecordByMemberId(memberId);

            LOG.info("customerRecordList:" + customerRecordList.size());
//            for (CustomerRecord memberLabel : customerRecordList) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                if (!StringUtils.isNotBlank(memberLabel.getId())) {
//                    map.put("id", "");
//                    map.put("name", "");
//                    map.put("valueArr", "");
//                } else {
//                    map.put("id", memberLabel.getId());
//                    map.put("name", memberLabel.getName());
//                    map.put("valueArr", memberLabel.getValueArr());
//                }
//                mapList.add(map);
//            }

            PageInfo pageResult = new PageInfo(customerRecordList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 添加/修改业主沟通记录
     * @param customerRecord
     * @return
     */
    public ServerResponse addCustomerRecord(CustomerRecord customerRecord) {
        try {
            CustomerRecord tmpCustomerRecord = null;
            LOG.info("memberLabel:" + customerRecord);
            LOG.info("memberLabel:" + customerRecord.getId());
            LOG.info("memberLabel:" + iCustomerRecordMapper);

            if (!StringUtils.isNotBlank(customerRecord.getId()))//没有id则新增
            {
                tmpCustomerRecord = new CustomerRecord();
                tmpCustomerRecord.setDescribe(customerRecord.getDescribe());
                tmpCustomerRecord.setRemindDate(customerRecord.getRemindDate());
                iCustomerRecordMapper.insert(tmpCustomerRecord);
            } else {//修改
                tmpCustomerRecord = iCustomerRecordMapper.selectByPrimaryKey(customerRecord.getId());
                tmpCustomerRecord.setDescribe(customerRecord.getDescribe());
                tmpCustomerRecord.setRemindDate(customerRecord.getRemindDate());
                tmpCustomerRecord.setModifyDate(new Date());
                iCustomerRecordMapper.updateByPrimaryKeySelective(tmpCustomerRecord);
            }
//            return ServerResponse.createBySuccessMessage("ok");
            return ServerResponse.createBySuccessMessage("操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


}

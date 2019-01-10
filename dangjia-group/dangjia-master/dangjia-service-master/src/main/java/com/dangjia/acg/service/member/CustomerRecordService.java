package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.CustomerRecordDTO;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.MemberLabel;
import com.dangjia.acg.modle.user.MainUser;
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
    private UserMapper userMapper;

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
     *
     * @param pageDTO
     * @param memberId
     * @return
     */
    public ServerResponse<PageInfo> getCustomerRecordList(PageDTO pageDTO, String memberId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

            List<CustomerRecord> customerRecordList = iCustomerRecordMapper.getCustomerRecordByMemberId(memberId);
            LOG.info("customerRecordList:" + customerRecordList.size());

            List<CustomerRecordDTO> customerRecordListDTO = new ArrayList<>();
            for (CustomerRecord customerRecord : customerRecordList) {
                CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
                MainUser mainUser = userMapper.selectByPrimaryKey(customerRecord.getUserId());
                customerRecordDTO.setCustomerName(mainUser.getUsername());
                customerRecordDTO.setCreateDate(customerRecord.getCreateDate());
                customerRecordDTO.setRemindTime(customerRecord.getRemindTime());
                customerRecordDTO.setDescribes(customerRecord.getDescribes());
//                customerRecordDTO.setRemindCustomerRecord(customerRecord);
                customerRecordListDTO.add(customerRecordDTO);
            }

            PageInfo pageResult = new PageInfo(customerRecordList);
            pageResult.setList(customerRecordListDTO);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 添加/修改业主沟通记录
     *
     * @param customerRecord
     * @return
     */
    public ServerResponse addCustomerRecord(CustomerRecord customerRecord) {
        try {
            LOG.info("addCustomerRecord:" + customerRecord);

//            if (!StringUtils.isNotBlank(customerRecord.getId()))//没有id则新增
//            {
            CustomerRecord tmpCustomerRecord = new CustomerRecord();
            tmpCustomerRecord.setMemberId(customerRecord.getMemberId());
            tmpCustomerRecord.setUserId(customerRecord.getUserId());
            tmpCustomerRecord.setDescribes(customerRecord.getDescribes());
            tmpCustomerRecord.setRemindTime(customerRecord.getRemindTime());
            iCustomerRecordMapper.insertSelective(tmpCustomerRecord);

            Customer customer = customerMapper.getCustomerByMemberId(tmpCustomerRecord.getMemberId(), -1);

            List<CustomerRecord> customerRecordList = iCustomerRecordMapper.getCustomerRecordByMemberId(tmpCustomerRecord.getMemberId());
            //录入第一条的时候，改为 stage 继续跟进
            if (customerRecordList.size() == 1)
                customer.setStage(1);// 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单
            customer.setCurrRecordId(tmpCustomerRecord.getId());

            CustomerRecord maxNearRemindCustomerRecord = null;//最近提醒的那条
            if (customerRecord.getRemindTime() != null) {
                Date nowDate = new Date();
                LOG.info("nowDate getTime :" + nowDate.getTime());
                long tempMaxNearTime = new Date().getTime(); //离当前时间最近的
                CustomerRecord tempMaxCr = null;
                //遍历找到 距离当时间最近时间的提醒内容的对象
                for (CustomerRecord cr : customerRecordList) {
                    if (cr.getRemindTime() == null)
                        continue;
                    //如果该时间 大于 当前时间，并小于最小时间
                    if (cr.getRemindTime().getTime() > nowDate.getTime()
                            && tempMaxNearTime < cr.getRemindTime().getTime()) {
                        tempMaxNearTime = cr.getRemindTime().getTime();
                        tempMaxCr = cr;
                    }
                }

                //判断最近提醒是否超过了一天，超过一天则不显示提醒内容
                if (tempMaxCr != null) {
                    Date nextDayDate = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(nextDayDate);//设置起时间
                    cal.add(Calendar.DATE, 1);//增加一天
                    if (tempMaxCr.getRemindTime().getTime() < cal.getTime().getTime()) {
                        maxNearRemindCustomerRecord = tempMaxCr;
                    }
                }

                if (maxNearRemindCustomerRecord != null)
                    customer.setRemindRecordId(maxNearRemindCustomerRecord.getId());
            }
            customerMapper.updateByPrimaryKeySelective(customer);

//            }
//            else {//修改
//                tmpCustomerRecord = iCustomerRecordMapper.selectByPrimaryKey(customerRecord.getId());
//                tmpCustomerRecord.setDescribes(customerRecord.getDescribes());
//                tmpCustomerRecord.setRemindDate(customerRecord.getRemindDate());
//                tmpCustomerRecord.setModifyDate(new Date());
//                iCustomerRecordMapper.updateByPrimaryKeySelective(tmpCustomerRecord);
//            }
            return ServerResponse.createBySuccessMessage("操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


}

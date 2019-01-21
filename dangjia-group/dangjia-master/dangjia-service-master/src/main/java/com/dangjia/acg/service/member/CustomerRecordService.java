package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.pay.domain.Data;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            if (!StringUtils.isNotBlank(customerRecord.getMemberId()))
                return ServerResponse.createByErrorMessage("业主id不能为null");

            CustomerRecord newCustomerRecord = new CustomerRecord();
            newCustomerRecord.setMemberId(customerRecord.getMemberId());
            newCustomerRecord.setUserId(customerRecord.getUserId());
            newCustomerRecord.setDescribes(customerRecord.getDescribes());
            newCustomerRecord.setRemindTime(customerRecord.getRemindTime());
            iCustomerRecordMapper.insertSelective(newCustomerRecord);

            Customer customer = customerMapper.getCustomerByMemberId(newCustomerRecord.getMemberId());
            if(customer == null)
            {
                customer = new Customer();
                customer.setMemberId(customerRecord.getMemberId());
                customer.setUserId(customerRecord.getUserId());
                customer.setStage(0);
                customerMapper.insertSelective(customer);
            }

            List<CustomerRecord> customerRecordList = iCustomerRecordMapper.getCustomerRecordByMemberId(newCustomerRecord.getMemberId());
            //录入第一条的时候，改为 stage 继续跟进
            if (customerRecordList.size() == 1)
                customer.setStage(1);// 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单
            customer.setCurrRecordId(newCustomerRecord.getId());
            customerMapper.updateByPrimaryKeySelective(customer);

            //更新最近的提醒时间
            if (newCustomerRecord.getRemindTime() != null) //设置新的提醒了，需要重新计算最新最近的提醒记录和时间
                updateMaxNearRemind(customer);
            return ServerResponse.createBySuccessMessage("操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 更新业主的最近沟通记录的提醒时间
     *
     * @param customer
     */
    public void updateMaxNearRemind(Customer customer) {

        List<CustomerRecord> customerRecordList = iCustomerRecordMapper.getCustomerRecordByMemberId(customer.getMemberId());

        String oldRemindRecordId = null;//记录 老的提醒id
        if (customer.getRemindRecordId() != null)
            oldRemindRecordId = customer.getRemindRecordId();
        CustomerRecord maxNearRemindCustomerRecord = null;//最近提醒的那条
        Date nowDate = new Date();
        CustomerRecord tempMaxCr = null;//离当前时间最近的提醒记录
        CustomerRecord tempLastMaxCr = null;//离当前时间 前一天的最近的提醒记录

        List<CustomerRecord> lastCustomerRecordList = new ArrayList<>();

        //遍历找到 距离当时间最近时间的提醒内容的对象
        for (CustomerRecord cr : customerRecordList) {
            if (cr.getRemindTime() == null)
                continue;
            //数据库提醒时间 大于 当前时间，并小于最小时间
            //找到 离当前时间最近的 沟通记录
            if (cr.getRemindTime().getTime() > nowDate.getTime()) {
                if (tempMaxCr == null) {
                    tempMaxCr = cr;
                }
                if (cr.getRemindTime().getTime() < tempMaxCr.getRemindTime().getTime()) {
                    tempMaxCr = cr;
                }
            }
            Date date = checkOverdueTime(cr.getRemindTime());
            if (date != null) { //添加 提醒 时间在 前一天内的记录
                lastCustomerRecordList.add(cr);
            }
        }

        //判断最近提醒是否超过了一天，超过一天则不显示提醒内容
        if (tempMaxCr != null) {
            Date nextDayDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(nextDayDate);//设置起时间
            cal.add(Calendar.DATE, 1);//增加一天
            if (tempMaxCr.getRemindTime().getTime() < cal.getTime().getTime()) {//在距离当前时间增加一天内
                LOG.info("tempMaxCr 最终 :" + tempMaxCr.getDescribes() + tempMaxCr);
                maxNearRemindCustomerRecord = tempMaxCr;
            }
        }
        if (maxNearRemindCustomerRecord != null)//有最近的提醒
            customer.setRemindRecordId(maxNearRemindCustomerRecord.getId());
        else {//没有最近的提醒，，就查找前一天的最近提醒
            for (CustomerRecord cr : lastCustomerRecordList) {
                if (tempLastMaxCr == null)
                    tempLastMaxCr = cr;
                else {
                    //如果 距离当前时间 ，在前一天内，最近的提醒时间
                    if (cr.getRemindTime().getTime() > tempLastMaxCr.getRemindTime().getTime()) {
                        tempLastMaxCr = cr;
                    }
                }
            }
            //检查是否超过一天, 就是设置为null
            if (tempLastMaxCr == null)
                customer.setRemindRecordId(null);
            else
                customer.setRemindRecordId(tempLastMaxCr.getId());
            LOG.info("前一天内 更新后最近的沟通提醒 ：" + customer + " size:" + lastCustomerRecordList.size());
        }

        LOG.info("updateMaxNearRemind 更新后最近的沟通提醒 ：" + customer);
        LOG.info("updateMaxNearRemind oldRemindRecordId ：" + oldRemindRecordId + " 更新后:" + customer.getRemindRecordId());

        if (oldRemindRecordId != null) { //如果之前有设置过提醒
            if (!oldRemindRecordId.equals(customer.getRemindRecordId())) {
                customerMapper.updateByPrimaryKey(customer);
                LOG.info("更新后最近的沟通提醒 成功了");
            }
        } else if (oldRemindRecordId == null && customer.getRemindRecordId() != null) { //第一次 设置提醒
            customerMapper.updateByPrimaryKey(customer);
            LOG.info("更新后最近的沟通提醒 第一次设置提醒 成功了");
        }
    }


    /**
     * 检查是否超过一天
     *
     * @param date
     * @return
     */
    private Date checkOverdueTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date lastDate = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date lastDay = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        LOG.info(" 前一天:" + df.format(lastDate));
        if (date.getTime() <= lastDate.getTime()) {//如果 传入时间 小于 前一天
            return null;
        }
        return date;
    }

}

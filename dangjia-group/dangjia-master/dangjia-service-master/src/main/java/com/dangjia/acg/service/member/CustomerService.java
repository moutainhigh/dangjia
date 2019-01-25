package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.service.core.WorkerTypeService;
import com.dangjia.acg.service.house.WarehouseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 */
@Service
public class CustomerService {

    @Autowired
    private ICustomerMapper iCustomerMapper;
    private static Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    /**
     * 修改业主的客服跟进
     *
     * @param customer
     * @return
     */
    public ServerResponse setMemberCustomer(Customer customer) {
        try {
            LOG.info("setMemberCustomer :" + customer);
            if (!StringUtils.isNotBlank(customer.getMemberId()))
                return ServerResponse.createByErrorMessage("业主id不能为null");
            Customer srcCustomer = iCustomerMapper.getCustomerByMemberId(customer.getMemberId());
            if (srcCustomer == null) {
                //每个业主增加关联 客服跟进
                srcCustomer = new Customer();
                srcCustomer.setMemberId(customer.getMemberId());
                srcCustomer.setStage(0);
                iCustomerMapper.insertSelective(srcCustomer);
            }

            if (StringUtils.isNotBlank(customer.getUserId()))
                srcCustomer.setUserId(customer.getUserId());
            if (customer.getLabelIdArr() != "noUpdate")
                srcCustomer.setLabelIdArr(customer.getLabelIdArr());
            if (!StringUtils.isNotBlank(customer.getLabelIdArr())) //如果为null 或者 “”，就删除
                srcCustomer.setLabelIdArr(null);
            if (customer.getStage() >= 0) {
                //如果是 放弃跟进 操作，
                if (customer.getStage() == 2 && srcCustomer.getStage() != 1) {
                    return ServerResponse.createByErrorMessage("当前不是继续跟进，不能放弃跟进");
                }
//                阶段: 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单
                if (customer.getStage() == 2) {//如果是 2放弃跟进 ,清空客服
                    srcCustomer.setUserId(null);
                }
                srcCustomer.setStage(customer.getStage());
            }
            srcCustomer.setModifyDate(new Date());
            iCustomerMapper.updateByPrimaryKey(srcCustomer);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }

    }

}

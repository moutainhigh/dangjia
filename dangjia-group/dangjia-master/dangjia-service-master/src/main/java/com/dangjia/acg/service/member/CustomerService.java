package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.service.core.WorkerTypeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 *
 */
@Service
public class CustomerService {
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private ICustomerRecordMapper iCustomerRecordMapper;
    @Autowired
    private WorkerTypeService workerTypeService;
    @Autowired
    private RedisClient redisClient;
    private Logger LOG = LoggerFactory.getLogger(MemberService.class);

    public ServerResponse addCustomer(HttpServletRequest request, Customer customer, String imageurl) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        customer.setMemberId(accessToken.getMemberId());
//		customer.setState(Constants.STATE_TJ);
//		if(!CommonUtil.isEmpty(customer.getWorkerTypeId())) {
//			customer.setWorkerType(workerTypeService.getWorkerTypeId(customer.getWorkerTypeId()).getType());
//		}
        iCustomerMapper.insertSelective(customer);
        if (!CommonUtil.isEmpty(imageurl)) {
            String[] imageurls = imageurl.split(",");
            if (imageurls != null && imageurls.length > 0) {
                for (String i : imageurls) {
                    CustomerRecord img = new CustomerRecord();
//					img.setCustomerId(customer.getId());
//					img.setImageurl(i);
                    iCustomerRecordMapper.insertSelective(img);
                }
            }
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 修改业主的客服跟进
     *
     * @param customer
     * @return
     */
    public ServerResponse setMemberCustomer(Customer customer) {
        try {
            LOG.info("setMemberCustomer :" + customer);
            if (!StringUtils.isNotBlank(customer.getUserId()))
                return ServerResponse.createByErrorMessage("业主id不能为null");
            Customer srcCustomer = iCustomerMapper.getCustomerByMemberId(customer.getMemberId(), -1);
            if (srcCustomer == null) {
                //每个业主增加关联 客服跟进
                srcCustomer = new Customer();
                srcCustomer.setMemberId(customer.getMemberId());
                srcCustomer.setStage(0);
                if (StringUtils.isNotBlank(customer.getUserId()))
                    srcCustomer.setUserId(customer.getUserId());
                iCustomerMapper.insertSelective(srcCustomer);
            }

            if (StringUtils.isNotBlank(customer.getUserId()))
                srcCustomer.setUserId(customer.getUserId());
//            if (StringUtils.isNotBlank(customer.getLabelIdArr()))
            if (customer.getLabelIdArr() != "noUpdate")
                srcCustomer.setLabelIdArr(customer.getLabelIdArr());
            if (customer.getStage() >= 0) {
                //如果是 放弃跟进 操作，
                if (customer.getStage() == 2 && srcCustomer.getStage() != 1) {
                    return ServerResponse.createByErrorMessage("当前不是继续跟进，不能放弃跟进");
                }
                srcCustomer.setStage(customer.getStage());
            }
            srcCustomer.setModifyDate(new Date());
            iCustomerMapper.updateByPrimaryKeySelective(srcCustomer);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }

    }

}

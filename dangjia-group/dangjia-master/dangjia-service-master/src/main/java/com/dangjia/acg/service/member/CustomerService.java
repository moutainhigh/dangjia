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
     * @param customer
     * @return
     */
    public ServerResponse setMemberCustomer( Customer customer) {
        Customer srcCustomer = iCustomerMapper.getCustomerByMemberId(customer.getMemberId());
        if(srcCustomer == null)
            return ServerResponse.createByErrorMessage("该业主没有客服跟进");
        if (!StringUtils.isNotBlank(customer.getUserId()))
            srcCustomer.setUserId(customer.getUserId());
        if (!StringUtils.isNotBlank(customer.getLabelId()))
            srcCustomer.setLabelId(customer.getLabelId());
        if (customer.getStage() >= 0)
            srcCustomer.setStage(customer.getStage());
        srcCustomer.setModifyDate(new Date());
        iCustomerMapper.updateByPrimaryKeySelective(srcCustomer);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

}

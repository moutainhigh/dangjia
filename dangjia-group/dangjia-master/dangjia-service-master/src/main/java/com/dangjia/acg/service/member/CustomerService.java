package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.member.ICustomerImageMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerImage;
import com.dangjia.acg.service.core.WorkerTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 */
@Service
public class CustomerService {
	@Autowired
	private ConfigUtil configUtil;

	@Autowired
	private ICustomerMapper customerMapper;
	@Autowired
	private ICustomerImageMapper customerImageMapper;
	@Autowired
	private WorkerTypeService workerTypeService;
	@Autowired
	private RedisClient redisClient;

	public ServerResponse addCustomer(HttpServletRequest request, Customer customer,String imageurl){
		String userToken = request.getParameter(Constants.USER_TOKEY);
		AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
		customer.setMemberId(accessToken.getMemberId());
		customer.setState(Constants.STATE_TJ);
		if(!CommonUtil.isEmpty(customer.getWorkerTypeId())) {
			customer.setWorkerType(workerTypeService.getWorkerTypeId(customer.getWorkerTypeId()).getType());
		}
		customerMapper.insertSelective(customer);
		if(!CommonUtil.isEmpty(imageurl)) {
			String[] imageurls=imageurl.split(",");
			if (imageurls != null && imageurls.length > 0) {
				for (String i : imageurls) {
					CustomerImage img = new CustomerImage();
					img.setCustomerId(customer.getId());
					img.setImageurl(i);
					customerImageMapper.insertSelective(img);
				}
			}
		}
		return ServerResponse.createBySuccessMessage("ok");
	}


}

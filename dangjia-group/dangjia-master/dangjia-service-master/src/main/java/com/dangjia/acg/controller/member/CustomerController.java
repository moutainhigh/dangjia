package com.dangjia.acg.controller.member;

import com.dangjia.acg.api.member.CustomerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.service.member.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:27
 */
@RestController
public class CustomerController implements CustomerAPI {

    @Autowired
    private CustomerService customerService;

    @Override
    @ApiMethod
    public ServerResponse addCustomer(HttpServletRequest request, Customer customer, String imageurl){
        return customerService.addCustomer(request,customer,imageurl);
    }
}

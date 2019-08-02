package com.dangjia.acg.controller.sale.rob;

import com.dangjia.acg.api.sale.rob.RobAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.service.sale.rob.RobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * 抢单模块
 * author: ljl
 * Date: 2019/7/30
 * Time: 9:59
 */
@RestController
public class RobController implements RobAPI {

    @Autowired
    private RobService robService;

    /**
     * 查询抢单列表
     * @param request
     * @param userId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRobSingledata(HttpServletRequest request, String userId,String storeId) {
        return robService.queryRobSingledata(userId,storeId);
    }

    /**
     * 查询客户详情
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCustomerInfo(HttpServletRequest request, String userId,String memberId) {
        return robService.queryCustomerInfo(memberId,userId);
    }


    /**
     * 新增标签
     * @param memberId
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addLabel(HttpServletRequest request,String memberId,String labelId) {
        return robService.addLabel(memberId,labelId);
    }

    /**
     * 新增沟通记录
     * @param customerRecord
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addDescribes(HttpServletRequest request, CustomerRecord customerRecord) {
        return robService.addDescribes(customerRecord);
    }

    /**
     * 修改客户信息
     * @param clue
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse upDateCustomerInfo(HttpServletRequest request, Clue clue) {
        return robService.upDateCustomerInfo(clue);
    }


}

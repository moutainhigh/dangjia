package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.WebEngineerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.WorkerDisclosure;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.engineer.EngineerService;
import com.dangjia.acg.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:35
 */
@RestController
public class WebEngineerController implements WebEngineerAPI {
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private MemberService memberService;


    /**
     * 工匠审核
     */
    @Override
    @ApiMethod
    public ServerResponse checkWorker(String workerId, Integer checkType, String checkDescribe) {
        return memberService.checkWorker(workerId, checkType, checkDescribe);
    }

    /**
     * 已支付换工匠
     */
    @Override
    @ApiMethod
    public ServerResponse changePayed(String houseWorkerId, String workerId) {
        return engineerService.changePayed(houseWorkerId, workerId);
    }

    /**
     * 抢单未支付
     * 换工匠重新抢
     */
    @Override
    @ApiMethod
    public ServerResponse changeWorker(String houseWorkerId) {
        return engineerService.changeWorker(houseWorkerId);
    }

    /**
     * 取消指定
     */
    @Override
    @ApiMethod
    public ServerResponse cancelLockWorker(String houseFlowId) {
        return engineerService.cancelLockWorker(houseFlowId);
    }

    /**
     * 指定/修改指定工匠
     */
    @Override
    @ApiMethod
    public ServerResponse setLockWorker(String houseFlowId, String workerId) {
        return engineerService.setLockWorker(houseFlowId, workerId);
    }

    /**
     * 抢单记录
     */
    @Override
    @ApiMethod
    public ServerResponse grabRecord(String houseId, String workerTypeId) {
        return engineerService.grabRecord(houseId, workerTypeId);
    }

    /**
     * 查看工匠订单
     */
    @Override
    @ApiMethod
    public ServerResponse workerOrder(String houseId) {
        return engineerService.workerOrder(houseId);
    }


    /**
     * 禁用启用工序
     */
    @Override
    @ApiMethod
    public ServerResponse setState(String houseFlowId) {
        return engineerService.setState(houseFlowId);
    }

    /**
     * 查看工序
     */
    @Override
    @ApiMethod
    public ServerResponse houseFlowList(String houseId) {
        return engineerService.houseFlowList(houseId);
    }
    /**
     * 查看工序
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseFlowList(String houseId) {
        return engineerService.getHouseFlowList(houseId);
    }
    /**
     * 工匠钱包 信息
     */
    @Override
    @ApiMethod
    public ServerResponse workerMess(String workerId) {
        return engineerService.workerMess(workerId);
    }

    /**
     * 历史工地
     */
    @Override
    @ApiMethod
    public ServerResponse historyHouse(String workerId) {
        return engineerService.historyHouse(workerId);
    }


    /**
     * 工地暂停施工
     */
    @Override
    @ApiMethod
    public ServerResponse setPause(String houseId) {
        return engineerService.setPause(houseId);
    }

    /**
     * 工地列表
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseList(HttpServletRequest request, PageDTO pageDTO, Integer visitState, String searchKey,String startDate, String endDate, String supKey) {
        return engineerService.getHouseList( request,pageDTO, visitState, searchKey, startDate,  endDate, supKey);
    }

    /**
     * 工匠列表
     */
    @Override
    @ApiMethod
    public ServerResponse artisanList(HttpServletRequest request,String name, String workerTypeId, String type,String checkType ,PageDTO pageDTO) {
        String cityId = request.getParameter(Constants.CITY_ID);
        return engineerService.artisanList(cityId,name, workerTypeId, type, checkType ,pageDTO);
    }

    /**
     *  修改设计师绑定风格
     * @param request
     * @param member
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setMemberStyle(HttpServletRequest request,Member member) {
        return engineerService.setMemberStyle(member);
    }
    /**
     * 设计师风格详情
     */
    @Override
    @ApiMethod
    public ServerResponse getMemberStyles(HttpServletRequest request, String mamberId) {
        return engineerService.getMemberStyles(request,mamberId);
    }
    @Override
    @ApiMethod
    public ServerResponse getWareHouse(String houseId, PageDTO pageDTO) {
        return engineerService.getWareHouse(houseId, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse exportWareHouse(HttpServletResponse response, String houseId, String userName, String address) {
        return engineerService.exportWareHouse(response, houseId, userName, address);
    }

    @Override
    @ApiMethod
    public ServerResponse freeze(String memberId, boolean type) {
        return engineerService.freeze(memberId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getSureList(Integer type,Integer state,String search,PageDTO pageDTO) {
        return engineerService.getSureList(type,state,search,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addSure(WorkerDisclosure workerDisclosure) {
        return engineerService.addSure(workerDisclosure);
    }

    @Override
    @ApiMethod
    public ServerResponse updateSure(String name, String details, String img,Integer state,String id) {
        return engineerService.updateSure(name,details,img,state,id);
    }

    @Override
    @ApiMethod
    public ServerResponse getItemsList( Integer type, Integer state, String search,PageDTO pageDTO) {
        return engineerService.getItemsList(type,state,search,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addItems(String name, Integer type, Integer state) {
        return engineerService.addItems(name,type,state);
    }

    @Override
    @ApiMethod
    public ServerResponse updateItems(String name, Integer type, Integer state, String id) {
        return engineerService.updateItems(name,type,state,id);
    }
}

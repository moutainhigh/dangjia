package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.WorkerGoodsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.service.basics.WorkerGoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 工价商品Controller
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/12 上午11:09
 */
@RestController
public class WorkerGoodsController implements WorkerGoodsAPI {

    @Autowired
    private WorkerGoodsService workerGoodsService;

    /**
     * 查询工价商品
     *
     * @param pageDTO
     * @param workerTypeId
     * @param searchKey
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getWorkerGoodses(HttpServletRequest request, PageDTO pageDTO,String istops ,String workerTypeId, String searchKey, String showGoods) {
        try {
            return workerGoodsService.getWorkerGoodses(pageDTO, istops,workerTypeId, searchKey, showGoods);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询工价商品失败");
        }
    }

    /**
     * 新增或更新工价商品
     *
     * @param workerGoods
     * @param technologyJsonList
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<String> setWorkerGoods(HttpServletRequest request, WorkerGoods workerGoods, String technologyJsonList, String deleteTechnologyIds) {
//    public ServerResponse<String> setWorkerGoods(HttpServletRequest request,WorkerGoods workerGoods, String technologyListJson) {
        try {
            return workerGoodsService.setWorkerGoods(workerGoods, technologyJsonList, deleteTechnologyIds);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增或更新工价商品失败");
        }
    }

    /**
     * 每工种未删除 或 已支付工钱
     *
     * @param houseId
     * @param houseFlowId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getWorkertoCheck(HttpServletRequest request, String houseId, String houseFlowId) {
        return workerGoodsService.getWorkertoCheck(houseId, houseFlowId);
    }

    /**
     * 从精算表查工种已支付工钱
     *
     * @param houseId
     * @param houseFlowId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getPayedWorker(HttpServletRequest request, String houseId, String houseFlowId) {
        return workerGoodsService.getPayedWorker(houseId, houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHomeProductList(HttpServletRequest request) {
        return workerGoodsService.getHomeProductList();
    }

}

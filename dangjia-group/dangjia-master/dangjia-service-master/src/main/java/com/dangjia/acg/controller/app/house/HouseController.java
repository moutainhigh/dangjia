package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.service.house.HouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/11/2 0002
 * Time: 19:52
 */
@RestController
public class HouseController implements HouseAPI {
    protected static final Logger logger = LoggerFactory.getLogger(HouseController.class);
    @Autowired
    private HouseService houseService;

    @Override
    @ApiMethod
    public House selectHouseById(String  id) {
        return houseService.selectHouseById(id);
    }

    /**
     * 切换房产
     */
    @Override
    @ApiMethod
    public ServerResponse setSelectHouse(String userToken, String houseId) {
        return houseService.setSelectHouse(userToken, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getMyHouseList(PageDTO pageDTO, String userToken) {
        return houseService.getMyHouseList(pageDTO, userToken);
    }

    /**
     * 我的房产
     */
    @Override
    @ApiMethod
    public ServerResponse queryMyHouse(String userToken) {
        return houseService.queryMyHouse(userToken);
    }


    /**
     *
     * @param userToken 用户token
     * @param cityId 城市ID
     * @param houseType 房屋ID
     * @param latitude 纬度
     * @param longitude 经度
     * @param address 地址
     * @param name 地址名称
     * @param square 面积
     * @param actuarialDesignAttr 设计精算列表 (
     *      * id	String	设计精算模板ID
     *      * configName	String	设计精算名称
     *      * configType	String	配置类型1：设计阶段 2：精算阶段
     *      * productList	List	商品列表
     *      * productList.productId	String	商品ID
     *      * productList.productName	String	商品名称
     *      * productList.productSn	String	商品编码
     *      * productList.goodsId	String	货品ID
     *      * productList.storefrontId	String	店铺ID
     *      * productList.price	double	商品价格
     *      * productList.unit	String	商品单位
     *      * productList.unitName	String	单位名称
     *      * productList.image	String	图片
     *      * productList.imageUrl	String	详情图片地址
     *      * productList.valueIdArr	String	商品规格ID
     *      * productList.valueNameArr	String	商品规格名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setStartHouse(String userToken, String cityId, String houseType,
                                        String latitude, String longitude, String address, String name, BigDecimal square, String actuarialDesignAttr) {
        try{
            return houseService.setStartHouse(userToken, cityId, houseType, latitude, longitude, address, name,square,actuarialDesignAttr);

        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     *
     * @param userToken 用户token
     * @param cityId 城市ID
     * @param houseType 房屋类型
     * @param addressId 地址ID
     * @param actuarialDesignAttr 设计精算列表 商品列表(
     * id	String	设计精算模板ID
     * configName	String	设计精算名称
     * configType	String	配置类型1：设计阶段 2：精算阶段
     * productList	List	商品列表
     * productList.productId	String	商品ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse applicationDecorationHouse(String userToken,String cityId,String houseType,String addressId,String actuarialDesignAttr){
        try{
            return houseService.applicationDecorationHouse(userToken, cityId, houseType, addressId,actuarialDesignAttr);

        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 撤销房子装修
     *
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse revokeHouse(@RequestParam("userToken") String userToken) {
        return houseService.revokeHouse(userToken);
    }
    /**
     * 查询房子提交的货品记录
     *
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchBudgetInfoList(String userToken){
        return houseService.searchBudgetInfoList(userToken);
    }

    /**
     * 修改房子精算状态
     *
     * @param houseId
     * @param budgetOk
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseBudgetOk(String houseId, Integer budgetOk) {
        return houseService.setHouseBudgetOk(houseId, budgetOk);
    }

    /**
     * app修改房子精算状态
     *
     * @param houseId
     * @param budgetOk
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseBudgetOk(String userToken, String houseId, Integer budgetOk) {
        return houseService.setHouseBudgetOk(houseId, budgetOk);
    }


    @Override
    @ApiMethod
    public ServerResponse getRenovationManualinfo(String id) {
        return houseService.getRenovationManualinfo(id);
    }

    /**
     * 保存装修指南
     *
     * @param userToken
     * @param saveList
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveRenovationManual(String userToken, String saveList) {
        return houseService.saveRenovationManual(userToken, saveList);
    }

    /**
     * 施工记录
     *
     * @param houseId 房子ID @Instance: 835240771552816792532
     * @param pageDTO 分页内容
     * @return 施工记录列表 @see swagger:房产接口-> 施工记录
     */
    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecord(String houseId, String day, String workerType, PageDTO pageDTO) {
//        return  houseService.queryConstructionRecord(houseId, pageDTO, null);
        return houseService.queryConstructionRecordAll(houseId, null, day, workerType, 0, pageDTO);
    }

    /**
     * 施工记录
     *
     * @param houseId 房子ID @Instance: 835240771552816792532
     * @param pageDTO 分页内容
     * @return 施工记录列表 @see swagger:房产接口-> 施工记录
     */
    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecordAll(String houseId, String ids, String day, String workerType, Integer type, PageDTO pageDTO) {
        return houseService.queryConstructionRecordAll(houseId, ids, day, workerType, type, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecordType(String houseId) {
        return houseService.queryConstructionRecordType(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getStageProgress(String houseFlowId) {
        return houseService.getStageProgress(houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseFlowApply(String houseFlowApplyId) {
        return houseService.getHouseFlowApply(houseFlowApplyId);
    }

    /**
     * 工序记录
     */
    @Override
    @ApiMethod
    public ServerResponse queryFlowRecord(String houseFlowId) {
        return houseService.queryFlowRecord(houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryHomeConstruction() {
        return houseService.queryHomeConstruction();
    }

    /**
     * 根据id查询房子信息
     *
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public House getHouseById(String houseId) {
        return houseService.getHouseById(houseId);
    }

    /**
     * 参考报价
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getReferenceBudget(HttpServletRequest request, String cityId, String villageId, Double square, String houseType) {
        if (square == null) {
            square = 15d;
        }
        Double minSquare = square - 15;
        Double maxSquare = square + 15;
        return houseService.getReferenceBudget(request, cityId, villageId, minSquare, maxSquare, houseType);
    }

    @Override
    @ApiMethod
    public ServerResponse updateByHouseId(String building, String unit, String number, String houseId,
                                          String villageId, String cityId, Double buildSquare) {
        return houseService.updateByHouseId(building, unit, number, houseId, villageId, cityId, buildSquare);
    }


    @Override
    @ApiMethod
    public ServerResponse updateCustomEdit(String houseId) {
        return houseService.updateCustomEdit(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseChoiceCases(String id) {
        return houseService.getHouseChoiceCases(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAcceptanceDynamic(PageDTO pageDTO,String houseId) {
        return houseService.queryAcceptanceDynamic(pageDTO,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryApplyComplaints(String houseFlowApplyId) {
        return houseService.queryApplyComplaints(houseFlowApplyId);
    }

    @Override
    @ApiMethod
    public ServerResponse setRemindButlerCheck(String houseFlowApplyId) {
        return houseService.setRemindButlerCheck(houseFlowApplyId);
    }

    @Override
    @ApiMethod
    public ServerResponse setHousekeeperInitiatedAcceptance(String houseFlowApplyId, Integer supervisorCheck, String image, String applyDec) {
        try {
            return houseService.setHousekeeperInitiatedAcceptance(houseFlowApplyId,supervisorCheck,image,applyDec);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("操作失败");
        }
    }


    @Override
    @ApiMethod
    public ServerResponse setOwnerBy(String houseFlowApplyId,Integer memberCheck) {
        try {
            return houseService.setOwnerBy(houseFlowApplyId,memberCheck);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

}


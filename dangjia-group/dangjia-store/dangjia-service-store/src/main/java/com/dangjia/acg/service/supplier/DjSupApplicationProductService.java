package com.dangjia.acg.service.supplier;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supplier.DjSupSupplierProductDTO;
import com.dangjia.acg.mapper.supplier.DjAdjustRecordMapper;
import com.dangjia.acg.mapper.supplier.DjSupApplicationMapper;
import com.dangjia.acg.mapper.supplier.DjSupApplicationProductMapper;
import com.dangjia.acg.mapper.supplier.DjSupSupplierProductMapper;
import com.dangjia.acg.modle.supplier.DjAdjustRecord;
import com.dangjia.acg.modle.supplier.DjSupApplicationProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:46
 */
@Service
public class DjSupApplicationProductService {

    @Autowired
    private DjSupApplicationMapper djSupApplicationMapper;
    @Autowired
    private DjSupApplicationProductMapper djSupApplicationProductMapper;
    @Autowired
    private DjSupSupplierProductMapper djSupSupplierProductMapper;
    @Autowired
    private DjAdjustRecordMapper djAdjustRecordMapper;


    /**
     * 供应商申请供应商品
     * @param jsonStr
     * @return
     */
    public ServerResponse insertDjSupApplicationProduct(String jsonStr) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str ->{
                JSONObject obj = (JSONObject) str;
                DjSupApplicationProduct djSupApplicationProduct=new DjSupApplicationProduct();
                djSupApplicationProduct.setDataStatus(0);
                djSupApplicationProduct.setSupId(obj.getString("supId"));
                djSupApplicationProduct.setShopId(obj.getString("shopId"));
                djSupApplicationProduct.setProductId(obj.getString("productId"));
                djSupApplicationProduct.setPrice(obj.getDouble("price"));
                djSupApplicationProduct.setStock(obj.getInteger("stock"));
                djSupApplicationProduct.setPorterage(obj.getDouble("porterage"));
                djSupApplicationProduct.setIsCartagePrice(obj.getString("isCartagePrice"));
                djSupApplicationProduct.setSupplyRelationShip(obj.getString("supplyRelationShip"));
                djSupApplicationProduct.setApplicationStatus("0");
                djSupApplicationProduct.setGoodsId(obj.getString("goodsId"));
                djSupApplicationProductMapper.insert(djSupApplicationProduct);
            });
            return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }


    /**
     * 查询已供/打回商品
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse queryHaveGoods(String supId, String shopId, PageDTO pageDTO, String applicationStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupSupplierProductDTO> djSupSupplierProductDTOS = djSupSupplierProductMapper.queryHaveGoods(supId, shopId,applicationStatus);
            PageInfo pageResult = new PageInfo(djSupSupplierProductDTOS);
            if(djSupSupplierProductDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询成功失败："+e);
        }
    }


    /**
     * 编辑已供商品
     * @param jsonStr
     * @return
     */
    public ServerResponse updateHaveGoods(String jsonStr) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str ->{
                JSONObject obj = (JSONObject) str;
                String applicationProductId = obj.getString("applicationProductId");//供应商品表id
                Double price = obj.getDouble("price");//供应价
                Double porterage = obj.getDouble("porterage");//搬运费
                Double adjustPrice = obj.getDouble("adjustPrice");//调后价
                Date adjustTime = obj.getDate("adjustTime");//调价时间
                String isCartagePrice = obj.getString("isCartagePrice");//是否收取上楼费 0=否，1=是
                String supplyRelationship = obj.getString("supplyRelationship");//供应关系 0:供应 1:停供
                String userId = obj.getString("userId");//操作人
                DjSupApplicationProduct djSupApplicationProduct=new DjSupApplicationProduct();
                djSupApplicationProduct.setId(applicationProductId);
                djSupApplicationProduct.setPrice(price);
                djSupApplicationProduct.setPorterage(porterage);
                djSupApplicationProduct.setIsCartagePrice(isCartagePrice);
                djSupApplicationProduct.setSupplyRelationShip(supplyRelationship);
                djSupApplicationProductMapper.updateByPrimaryKeySelective(djSupApplicationProduct);
                DjAdjustRecord djAdjustRecord=new DjAdjustRecord();
                djAdjustRecord.setAdjustPrice(adjustPrice);
                djAdjustRecord.setAdjustTime(adjustTime);
                djAdjustRecord.setApplicationProductId(applicationProductId);
                djAdjustRecord.setUserId(userId);
                djAdjustRecordMapper.insert(djAdjustRecord);
            });
            return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败："+e);
        }
    }

    /**
     * 查询待审核的供应商品
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse getExaminedProduct(HttpServletRequest request, String supId, String shopId,String keyWord) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已供商品
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse getSuppliedProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 全部打回
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse rejectAllProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 部分通过
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse rejectPartProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

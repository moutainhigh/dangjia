package com.dangjia.acg.service.supplier;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supplier.DjSupSupplierProductDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDTO;
import com.dangjia.acg.mapper.supplier.DjSupApplicationMapper;
import com.dangjia.acg.mapper.supplier.DjSupApplicationProductMapper;
import com.dangjia.acg.mapper.supplier.DjSupSupplierProductMapper;
import com.dangjia.acg.mapper.supplier.DjSupplierMapper;
import com.dangjia.acg.modle.supplier.DjSupApplicationProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
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
    private DjSupplierMapper djSupplierMapper;
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
     * 查询已供商品
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse queryHaveGoods(String supId, String shopId,String applicationStatus, PageDTO pageDTO) {
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

        return null;
    }

    /**
     * 查询待审核的供应商品
     * @param request
     * @param applicationStatus
     * @param shopId
     * @param keyWord
     * @return
     */
    public ServerResponse getExaminedProduct(HttpServletRequest request, PageDTO pageDTO, String applicationStatus, String shopId,String keyWord) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupplierDTO>  list=djSupplierMapper.queryDjSupplierByShopID(keyWord,applicationStatus,shopId);
            if (list.size() <= 0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }






    /**
     * 店铺-审核供货列表-已供商品
     * @param request
     * @param supId
     * @param shopId
     * @param applicationStatus
     * @param pageDTO
     * @return
     */
    public ServerResponse getSuppliedProduct(HttpServletRequest request, String supId, String shopId,String applicationStatus, PageDTO pageDTO  ) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupSupplierProductDTO> djSupSupplierProductDTOS = djSupSupplierProductMapper.queryHaveGoods(supId, shopId,applicationStatus);
            PageInfo pageResult = new PageInfo(djSupSupplierProductDTOS);
            if(djSupSupplierProductDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功",pageResult);
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
            Example example=new Example(DjSupApplicationProduct.class);
            //申请状态 0:审核中 1:通过 2:不通过
            example.createCriteria().andEqualTo(DjSupApplicationProduct.APPLICATION_STATUS,2)
                    .andEqualTo(DjSupApplicationProduct.SUP_ID,supId)
                    .andEqualTo(DjSupApplicationProduct.SHOP_ID,shopId);
            djSupApplicationProductMapper.updateByExampleSelective(null,example);
            return ServerResponse.createBySuccessMessage("全部打回成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("全部打回失败");
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
    public ServerResponse rejectPartProduct(HttpServletRequest request, String id , String supId, String shopId) {
        try {

            DjSupApplicationProduct djSupApplicationProduct=new DjSupApplicationProduct();
            djSupApplicationProduct.setId(id);
            djSupApplicationProduct.setSupId(supId);
            djSupApplicationProduct.setShopId(shopId);
            djSupApplicationProduct.setApplicationStatus("1");//申请状态 0:审核中 1:通过 2:不通过
            djSupApplicationProductMapper.updateByPrimaryKeySelective(djSupApplicationProduct);
            return ServerResponse.createBySuccessMessage("部分通过成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("部分通过失败");
        }
    }
}

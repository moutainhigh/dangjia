package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.basics.GoodsCategoryAPI;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.mapper.deliver.ICartMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.Cart;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: qyx
 * Date: 2019/04/15 0009
 * Time: 10:55
 */
@Service
public class CartService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ICartMapper cartMapper;
    @Autowired
    private ProductAPI productAPI;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;

    @Autowired
    private IWarehouseMapper warehouseMapper;

    @Autowired
    private GoodsCategoryAPI goodsCategoryAPI;


    @Autowired
    private ForMasterAPI forMasterAPI;

    /**
     * 设置购物车商品数量
     * @param request
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse setCart(HttpServletRequest request, String userToken, Cart cart){
        request.setAttribute(Constants.CITY_ID,request.getParameter(Constants.CITY_ID));
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member operator = accessToken.getMember();
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID,cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID,operator.getWorkerTypeId())
                .andEqualTo(Cart.PRODUCT_ID,cart.getProductId())
                .andEqualTo(Cart.MEMBER_ID,operator.getId());
        List<Cart> list=cartMapper.selectByExample(example);
        if(list.size()>0){
            Cart cart1=list.get(0);
            if(cart.getShopCount()<0){
                cartMapper.delete(cart1);
            }
            cart1.setShopCount(cart.getShopCount());
            cartMapper.updateByPrimaryKeySelective(cart1);
        }else{
            if(cart.getShopCount()>0){
                ServerResponse serverResponse=productAPI.getProductById(request,cart.getProductId());
                if(serverResponse!=null&&serverResponse.getResultObj()!=null){
                    Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
                    Goods goods=forMasterAPI.getGoods(request.getParameter(Constants.CITY_ID), product.getGoodsId());
                    cart.setProductSn(product.getProductSn());
                    cart.setProductName(product.getName());
                    cart.setMemberId(operator.getId());
                    cart.setPrice(product.getPrice());
                    cart.setWorkerTypeId(operator.getWorkerTypeId());
                    cart.setProductType(goods.getType());
                    cart.setUnitName(product.getUnitName());
                    cart.setCategoryId(product.getCategoryId());
                    cartMapper.insert(cart);
                }
            }
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 清空购物车商品
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse clearCart(String userToken, Cart cart){
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member operator = accessToken.getMember();
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID,cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID,operator.getWorkerTypeId())
                .andEqualTo(Cart.MEMBER_ID,operator.getId());
        cartMapper.deleteByExample(example);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 查询购物车商品
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse queryCart(String userToken, Cart cart){
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member operator = accessToken.getMember();
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID,cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID,operator.getWorkerTypeId())
                .andEqualTo(Cart.MEMBER_ID,operator.getId());
        List<Cart> list=cartMapper.selectByExample(example);
        return ServerResponse.createBySuccess("操作成功",list);
    }

    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    public ServerResponse askAndQuit(HttpServletRequest request, String userToken, PageDTO pageDTO, String houseId, String categoryId, String name) {
        try {
            String cityId = request.getParameter(Constants.CITY_ID);

            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            String productType="0";
            if(worker.getWorkerType() == 3){
                productType="1";
            }
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            List<String> productIdList;
            if(worker.getWorkerType() == 3){
                productIdList = orderSplitMapper.getOrderProduct(houseId,productType,"",worker.getId());
            }else {
                productIdList = orderSplitMapper.getOrderProduct(houseId,productType,worker.getWorkerTypeId(),worker.getId());
            }

            if(productIdList==null||productIdList.size()==0){
                return ServerResponse.createBySuccessMessage("查询成功");
            }
            String[] productIdArr = productIdList.toArray(new String[productIdList.size()]);
            request.setAttribute(Constants.CITY_ID, cityId);
            PageInfo pageResult= productAPI.queryProductData(request,pageDTO.getPageNum(),pageDTO.getPageSize(),name,categoryId,productType,productIdArr);
            List<JSONObject> products=pageResult.getList();
            for (JSONObject product : products) {
                Example example = new Example(Warehouse.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(Warehouse.HOUSE_ID, houseId);
                criteria.andEqualTo(Warehouse.PRODUCT_ID, product.get(Product.ID));
                List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setProductId(String.valueOf(product.get(Product.ID)));
                warehouseDTO.setProductName(String.valueOf(product.get(Product.NAME)));
                warehouseDTO.setPrice(Double.parseDouble(String.valueOf(product.get(Product.PRICE))));
                warehouseDTO.setUnitName(String.valueOf(product.get(Product.UNIT_NAME)));
                warehouseDTO.setProductType(Integer.parseInt(productType));
                warehouseDTO.setImage(address + product.get(Product.IMAGE));
                if (warehouseList.size() > 0) {
                    Warehouse warehouse = warehouseList.get(0);
                    warehouseDTO.setImage(address + warehouse.getImage());
                    warehouseDTO.setShopCount(warehouse.getShopCount());
                    warehouseDTO.setAskCount(warehouse.getAskCount());
                    warehouseDTO.setBackCount(warehouse.getBackCount());
                    warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                    warehouseDTO.setSurCount(warehouse.getShopCount() - warehouse.getBackCount() - warehouse.getAskCount());//所有买的数量 - 退货 - 收的=仓库剩余
                    warehouseDTO.setPrice(warehouse.getPrice());
                    warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                    warehouseDTO.setReceive(warehouse.getReceive());
                    warehouseDTO.setAskTime(warehouse.getAskTime());
                    warehouseDTO.setRepTime(warehouse.getRepTime());
                    warehouseDTO.setBackTime(warehouse.getBackTime());
                    warehouseDTOS.add(warehouseDTO);
                }else{
                    warehouseDTO.setShopCount(0.0);
                    warehouseDTO.setRepairCount(0.0);
                    warehouseDTO.setRealCount(0.0);
                    warehouseDTO.setAskCount(0.0);//已要数量
                    warehouseDTO.setSurCount(0.0);
                    warehouseDTO.setBackCount(0.0);//退总数
                    warehouseDTO.setReceive(0.0);
                    warehouseDTO.setTolPrice(0.0);
                    warehouseDTO.setAskTime(0);
                    warehouseDTO.setRepTime(0);
                    warehouseDTO.setBackTime(0);
                    warehouseDTOS.add(warehouseDTO);
                }
            }
            pageResult.setList(warehouseDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询分类
    public ServerResponse queryGoodsCategory(HttpServletRequest request, String userToken,String houseId) {
        try {
            String cityId = request.getParameter(Constants.CITY_ID);
            request.setAttribute(Constants.CITY_ID, cityId);
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            String productType="0";
            if(worker.getWorkerType() == 3){
                productType="1";
            }
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            List<String> orderCategory = orderSplitMapper.getOrderCategory(houseId,productType,worker.getWorkerTypeId(),worker.getId());
            for (String categoryId : orderCategory) {
                GoodsCategory goodsCategory=goodsCategoryAPI.getGoodsCategory(request,categoryId);
                Map<String, Object> map = new HashMap<String, Object>();
                if(goodsCategory!=null){
                    map.put("id", goodsCategory.getId());
                    map.put("name", goodsCategory.getName());
                    mapList.add(map);
                }

            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}

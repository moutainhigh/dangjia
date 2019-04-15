package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.mapper.deliver.ICartMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.Cart;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
                cartMapper.updateByPrimaryKeySelective(cart1);
            }
            cart1.setShopCount(cart.getShopCount());
            cartMapper.updateByPrimaryKeySelective(cart1);
        }else{
            if(cart.getShopCount()<0){
                ServerResponse serverResponse=productAPI.getProductById(request,cart.getProductId());
                if(serverResponse!=null&&serverResponse.getResultObj()!=null){
                    Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
                    cart.setProductSn(product.getProductSn());
                    cart.setProductName(product.getName());
                    cart.setMemberId(operator.getId());
                    cart.setPrice(product.getPrice());
                    cart.setUnitName(product.getUnitName());
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

            List<String> productIdList = orderSplitMapper.getOrderProduct(houseId,worker.getWorkerTypeId(),worker.getId());

            if(productIdList==null||productIdList.size()==0){
                return ServerResponse.createBySuccessMessage("查询成功");
            }
            String[] productIdArr = productIdList.toArray(new String[productIdList.size()]);
            request.setAttribute(Constants.CITY_ID, cityId);

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Product> products= productAPI.queryProductData(request,name,categoryId,productType,productIdArr);
            PageInfo pageResult = new PageInfo(products);
            for (Product product : products) {
                Warehouse warehouse = warehouseMapper.getByProductId(product.getId(),houseId);
                if(warehouse == null) continue;
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setImage(address + warehouse.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount(warehouse.getBackCount());
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount() - warehouse.getBackCount() - warehouse.getAskCount());//所有买的数量 - 退货 - 收的=仓库剩余
                warehouseDTO.setProductName(warehouse.getProductName());
                warehouseDTO.setPrice(warehouse.getPrice());
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setReceive(warehouse.getReceive());
                warehouseDTO.setUnitName(warehouse.getUnitName());
                warehouseDTO.setProductType(warehouse.getProductType());
                warehouseDTO.setAskTime(warehouse.getAskTime());
                warehouseDTO.setRepTime(warehouse.getRepTime());
                warehouseDTO.setBackTime(warehouse.getBackTime());
                warehouseDTO.setProductId(warehouse.getProductId());
                warehouseDTOS.add(warehouseDTO);
            }
            pageResult.setList(warehouseDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}

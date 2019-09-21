package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.basics.GoodsCategoryAPI;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.mapper.deliver.ICartMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.Cart;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
    private ConfigUtil configUtil;
    @Autowired
    private ICartMapper cartMapper;
    @Autowired
    private DjBasicsProductAPI djBasicsProductAPI;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;

    @Autowired
    private IWarehouseMapper warehouseMapper;

    @Autowired
    private GoodsCategoryAPI goodsCategoryAPI;

    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private CraftsmanConstructionService constructionService;

    /**
     * 设置购物车商品数量
     *
     * @param request
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse setCart(HttpServletRequest request, String userToken, Cart cart) {
        request.setAttribute(Constants.CITY_ID, request.getParameter(Constants.CITY_ID));
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID, cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID, operator.getWorkerTypeId())
                .andEqualTo(Cart.PRODUCT_ID, cart.getProductId())
                .andEqualTo(Cart.MEMBER_ID, operator.getId());
        List<Cart> list = cartMapper.selectByExample(example);
        if (list.size() > 0) {
            Cart cart1 = list.get(0);
            if (cart.getShopCount() < 0) {
                cartMapper.delete(cart1);
            }
            cart1.setShopCount(cart.getShopCount());
            cartMapper.updateByPrimaryKeySelective(cart1);
        } else {
            if (cart.getShopCount() > 0) {
                ServerResponse serverResponse = djBasicsProductAPI.getProductById(request.getParameter(Constants.CITY_ID), cart.getProductId());
                if (serverResponse != null && serverResponse.getResultObj() != null) {
                    Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
                    BasicsGoods goods = forMasterAPI.getGoods(request.getParameter(Constants.CITY_ID), product.getGoodsId());
                    cart.setProductSn(product.getProductSn());
                    cart.setProductName(product.getName());
                    cart.setMemberId(operator.getId());
                    cart.setPrice(product.getPrice());
                    cart.setWorkerTypeId(operator.getWorkerTypeId());
                    cart.setProductType(goods.getType());
                    cart.setUnitName(product.getUnitName());
                    cart.setCategoryId(product.getCategoryId());
                    cart.setCityId(request.getParameter(Constants.CITY_ID));
                    cartMapper.insert(cart);
                }
            }
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 清空购物车商品
     *
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse clearCart(String userToken, Cart cart) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID, cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID, operator.getWorkerTypeId())
                .andEqualTo(Cart.MEMBER_ID, operator.getId());
        cartMapper.deleteByExample(example);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 查询购物车商品
     *
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse queryCart(String userToken, Cart cart) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        House house = iHouseMapper.selectByPrimaryKey(cart.getHouseId());
        request.setAttribute(Constants.CITY_ID, house.getCityId());
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID, cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID, operator.getWorkerTypeId())
                .andEqualTo(Cart.MEMBER_ID, operator.getId());
        example.setOrderByClause(Cart.CATEGORY_ID);
        List<Cart> list = cartMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<Map> listMap = new ArrayList<>();
        for (Cart cart1 : list) {
            Map map = BeanUtils.beanToMap(cart1);
            ServerResponse serverResponse = djBasicsProductAPI.getProductById(request.getParameter(Constants.CITY_ID), cart1.getProductId());
            if (serverResponse != null && serverResponse.getResultObj() != null) {
                Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
                if (product.getType() == 0 || product.getMaket() == 0) {
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, cart1.getHouseId()).andEqualTo(Warehouse.PRODUCT_ID, cart1.getProductId());
                    List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                    if (warehouseList.size() > 0) {
                        Warehouse warehouse = warehouseList.get(0);
                        map.put("maxCount", warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                    }
                }
            }
            listMap.add(map);
        }
        return ServerResponse.createBySuccess("操作成功", listMap);
    }


    /**
     * 查询购物车商品(分类后的商品）
     *
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse queryCategoryCart(String userToken, Cart cart) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        House house = iHouseMapper.selectByPrimaryKey(cart.getHouseId());
        request.setAttribute(Constants.CITY_ID, house.getCityId());
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Member operator = (Member) object;
        Example example = new Example(Cart.class);
        example.createCriteria()
                .andEqualTo(Cart.HOUSE_ID, cart.getHouseId())
                .andEqualTo(Cart.WORKER_TYPE_ID, operator.getWorkerTypeId())
                .andEqualTo(Cart.MEMBER_ID, operator.getId());
        List<Cart> list = cartMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        //保存分类的大类
        Map categoryMap = new HashMap();
        Map<String,Object> containsKeyMap = new HashMap<String,Object>();
        List<Map<String,Object>> cartCategroyList;
        for (Cart cart1 : list) {

            Map map = BeanUtils.beanToMap(cart1);
            ServerResponse serverResponse = djBasicsProductAPI.getProductById(request.getParameter(Constants.CITY_ID), cart1.getProductId());
            if (serverResponse != null && serverResponse.getResultObj() != null) {
                Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
                if (product.getType() == 0 || product.getMaket() == 0) {
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, cart1.getHouseId()).andEqualTo(Warehouse.PRODUCT_ID, cart1.getProductId());
                    List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                    if (warehouseList.size() > 0) {
                        Warehouse warehouse = warehouseList.get(0);
                        map.put("shopCount",warehouse.getShopCount());//购买量
                        map.put("surCount",warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                        map.put("maxCount", warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                    }else{
                        map.put("shopCount",0D);//购买量
                        map.put("surCount",0D);//剩余量
                    }
                    map.put("image",address + product.getImage());
                }
            }
            if(!containsKeyMap.containsKey(cart1.getCategoryId())){//判断是否有已有的大类
                GoodsCategory goodsCategory = goodsCategoryAPI.getGoodsCategory(request.getParameter(Constants.CITY_ID), cart1.getCategoryId());
                if (goodsCategory != null) {
                    GoodsCategory goodsCategorytop = goodsCategoryAPI.getGoodsCategory(request.getParameter(Constants.CITY_ID), goodsCategory.getParentTop());
                    if (goodsCategorytop != null) {
                        goodsCategory = goodsCategorytop;
                    }
                    categoryMap=new HashMap();
                    categoryMap.put("categoryId",cart1.getCategoryId());
                    categoryMap.put("parentTopCategoryId",goodsCategory.getId());
                    categoryMap.put("parentTopCategoryName",goodsCategory.getName());
                    cartCategroyList = new ArrayList<>();
                    cartCategroyList.add(map);
                    categoryMap.put("cartList",cartCategroyList);
                    containsKeyMap.put(cart1.getCategoryId(),categoryMap);
                }

            }else{
                categoryMap= (Map) containsKeyMap.get(cart1.getCategoryId());
                cartCategroyList = (List)categoryMap.get("cartList");
                cartCategroyList.add(map);
                categoryMap.put("cartList",cartCategroyList);
            }

        }
        List<Object> resCartList = new ArrayList<>(categoryMap.values());//map转换成List
        return ServerResponse.createBySuccess("操作成功", resCartList);
    }
    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    public ServerResponse askAndQuit(HttpServletRequest request, String userToken, PageDTO pageDTO, String houseId, String categoryId, String name) {
        try {
            String cityId = request.getParameter(Constants.CITY_ID);
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String productType = "0";
            if (worker.getWorkerType() == 3) {
                productType = "1";
            }
            Example example = new Example(Warehouse.class);
            example.createCriteria()
                    .andEqualTo(Warehouse.HOUSE_ID, houseId)
                    .andEqualTo(Warehouse.PRODUCT_TYPE, productType);
            List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
            if (warehouseList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            Map<String, Warehouse> warehouseMap = new HashMap<>();
            String[] productIdArr = new String[warehouseList.size()];
            for (int i = 0; i < warehouseList.size(); i++) {
                productIdArr[i] = warehouseList.get(i).getProductId();
                warehouseMap.put(warehouseList.get(i).getProductId(), warehouseList.get(i));
            }
           // PageInfo pageResult = productAPI.queryProductData(cityId, pageDTO.getPageNum(), pageDTO.getPageSize(), name, categoryId, productType, productIdArr);
            PageInfo pageResult = djBasicsProductAPI.queryBasicsProductData(cityId, pageDTO.getPageNum(), pageDTO.getPageSize(), name, categoryId, productType, productIdArr);
            List<JSONObject> products = pageResult.getList();
            for (JSONObject product : products) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setProductId(String.valueOf(product.get(Product.ID)));
                warehouseDTO.setProductName(String.valueOf(product.get(Product.NAME)));
                warehouseDTO.setMaket(1);
                if ("0".equals(product.get(Product.MAKET).toString()) || "0".equals(product.get(Product.TYPE).toString())) {
                    warehouseDTO.setMaket(0);
                }
                warehouseDTO.setPrice(Double.parseDouble(String.valueOf(product.get(Product.PRICE))));
                warehouseDTO.setProductType(Integer.parseInt(productType));
                warehouseDTO.setImage(address + product.get(Product.IMAGE));
                Warehouse warehouse = warehouseMap.get(warehouseDTO.getProductId());
                if (warehouse != null) {
                    BasicsGoods goods = forMasterAPI.getGoods(cityId, String.valueOf(product.get(Product.GOODS_ID)));
                    if (goods != null) {
                        warehouseDTO.setSales(goods.getSales());
                    }
                    warehouseDTO.setUnitName(warehouse.getUnitName());
                    warehouseDTO.setImage(address + warehouse.getImage());
                    warehouseDTO.setShopCount(warehouse.getShopCount());
                    warehouseDTO.setAskCount(warehouse.getAskCount());
                    warehouseDTO.setBackCount((warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                    warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                    warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());//所有买的数量 - 退货 - 收的=仓库剩余
                    warehouseDTO.setPrice(warehouse.getPrice());
                    warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                    warehouseDTO.setReceive(warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                    warehouseDTO.setAskTime(warehouse.getAskTime());
                    warehouseDTO.setRepTime(warehouse.getRepTime());
                    warehouseDTO.setBackTime(warehouse.getBackTime());
                    warehouseDTOS.add(warehouseDTO);
                } else {
                    String unit = forMasterAPI.getUnitName(cityId, String.valueOf(product.get(Product.CONVERT_UNIT)));
                    warehouseDTO.setUnitName(unit);
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
    public ServerResponse queryGoodsCategory(HttpServletRequest request, String userToken, String houseId) {
        String cityId = request.getParameter(Constants.CITY_ID);
        request.setAttribute(Constants.CITY_ID, cityId);
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        String productType = "0";
        if (worker.getWorkerType() == 3) {
            productType = "1";
        }
        List<String> orderCategory = orderSplitMapper.getOrderCategory(houseId, productType, worker.getWorkerTypeId(), worker.getId());
        if (orderCategory.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> mapTop = new HashMap<>();//记录以及添加的顶级分类
        for (String categoryId : orderCategory) {
            GoodsCategory goodsCategory = goodsCategoryAPI.getGoodsCategory(cityId, categoryId);
            if (goodsCategory != null) {
                GoodsCategory goodsCategorytop = goodsCategoryAPI.getGoodsCategory(cityId, goodsCategory.getParentTop());
                if (goodsCategorytop != null) {
                    goodsCategory = goodsCategorytop;
                }
                Map<String, Object> map = new HashMap<>();
                if (mapTop.get(goodsCategory.getId()) == null) {
                    map.put("id", goodsCategory.getId());
                    map.put("name", goodsCategory.getName());
                    mapList.add(map);
                    mapTop.put(goodsCategory.getId(), goodsCategory);
                }
            }
        }
        if (mapList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }

}

package com.dangjia.acg.service.deliver;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.product.BasicsGoodsCategoryAPI;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.dto.product.DjBasicsProductTemplateDTO;
import com.dangjia.acg.mapper.core.IMasterBasicsGoodsCategoryMapper;
import com.dangjia.acg.mapper.core.IMasterBasicsGoodsMapper;
import com.dangjia.acg.mapper.delivery.ICartMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.deliver.Cart;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    private BasicsGoodsCategoryAPI basicsGoodsCategoryAPI;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMasterBasicsGoodsMapper iMasterBasicsGoodsMapper;
    @Autowired
    private IMasterBasicsGoodsCategoryMapper iMasterBasicsGoodsCategoryMapper;
    @Autowired
    private IMemberMapper iMemberMapper;

    /**
     * 设置购物车商品数量
     *
     * @param request
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse setCart(HttpServletRequest request, String userToken, Cart cart) {
        House house = iHouseMapper.selectByPrimaryKey(cart.getHouseId());
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
                //查询对应的符合条件的商品信息
                StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(cart.getProductId());
                if(storefrontProduct!=null&& StringUtils.isNotBlank(storefrontProduct.getId())) {//店铺商品(补人工为店铺商品)
                    DjBasicsProductTemplate djBasicsProductTemplate=iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
                    BasicsGoods basicsGoods=iMasterBasicsGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
                    cart.setProductSn(djBasicsProductTemplate.getProductSn());
                    cart.setProductName(storefrontProduct.getProductName());
                    cart.setMemberId(operator.getId());
                    cart.setPrice(storefrontProduct.getSellPrice());
                    cart.setWorkerTypeId(operator.getWorkerTypeId());
                    cart.setProductType(basicsGoods.getType());
                    cart.setUnitName(djBasicsProductTemplate.getUnitName());
                    cart.setCategoryId(djBasicsProductTemplate.getCategoryId());
                    cart.setCityId(house.getCityId());
                }
//                  else {//商品库商品
//                    DjBasicsProductTemplate djBasicsProductTemplate=iMasterProductTemplateMapper.selectByPrimaryKey(cart.getProductId());
//                    BasicsGoods basicsGoods=iMasterBasicsGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
//                    cart.setProductSn(djBasicsProductTemplate.getProductSn());
//                    cart.setProductName(djBasicsProductTemplate.getName());
//                    cart.setMemberId(operator.getId());
//                    cart.setPrice(djBasicsProductTemplate.getPrice());
//                    cart.setWorkerTypeId(operator.getWorkerTypeId());
//                    cart.setProductType(basicsGoods.getType());
//                    cart.setUnitName(djBasicsProductTemplate.getUnitName());
//                    cart.setCategoryId(djBasicsProductTemplate.getCategoryId());
//                    cart.setCityId(house.getCityId());
//
//                }
                cartMapper.insert(cart);
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
        example.setOrderByClause(" category_id");
        List<Cart> list = cartMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<Map> listMap = new ArrayList<>();
        for (Cart cart1 : list) {
            Map map = BeanUtils.beanToMap(cart1);
            //增加店铺概念 现在cart productId为店铺商品表主键id
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(cart1.getProductId());
//            ServerResponse serverResponse = djBasicsProductAPI.getProductById(house.getCityId(), storefrontProduct.getProdTemplateId());
            if (storefrontProduct != null) {
//                DjBasicsProduct product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), DjBasicsProduct.class);
                if (storefrontProduct.getDataStatus() == 0 && storefrontProduct.getIsShelfStatus().equals("1")) {
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, cart1.getHouseId())
                            .andEqualTo(Warehouse.PRODUCT_ID, cart1.getProductId());
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
        if(cart.getProductType()!=null){
            example.createCriteria().andEqualTo(Cart.PRODUCT_TYPE,cart.getProductType());//如果是人工商品，此字段不能为空
        }
        example.setOrderByClause(" category_id");
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
            //ServerResponse serverResponse = djBasicsProductAPI.getProductById(request.getParameter(Constants.CITY_ID), cart1.getProductId());
           // if (serverResponse != null && serverResponse.getResultObj() != null) {
                StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(cart1.getProductId());
                DjBasicsProductTemplate product = iMasterProductTemplateMapper.selectByPrimaryKey(cart1.getProductId());
                if(product==null||StringUtils.isNotBlank(product.getId())){
                    product = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
                }
                map.put("shopCount",0D);//购买量
                map.put("surCount",0D);//剩余量
                if (product.getType() == 0 || product.getMaket() == 0) {
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, cart1.getHouseId()).andEqualTo(Warehouse.PRODUCT_ID, cart1.getProductId());
                    List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                    if (warehouseList.size() > 0) {
                        Warehouse warehouse = warehouseList.get(0);
                        map.put("shopCount",warehouse.getShopCount());//购买量
                        map.put("surCount",warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                        map.put("maxCount", warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                    }

                }
                map.put("image",address + product.getImage());
           // }
            if(!containsKeyMap.containsKey(cart1.getCategoryId())){//判断是否有已有的大类
                BasicsGoodsCategory goodsCategory =iMasterBasicsGoodsCategoryMapper.selectByPrimaryKey(cart1.getCategoryId());
                        //basicsGoodsCategoryAPI.getGoodsCategory(request.getParameter(Constants.CITY_ID), cart1.getCategoryId());
                if (goodsCategory != null) {
                    BasicsGoodsCategory goodsCategorytop = basicsGoodsCategoryAPI.getGoodsCategory(request.getParameter(Constants.CITY_ID), goodsCategory.getParentTop());
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
                containsKeyMap.put(cart1.getCategoryId(),categoryMap);
            }

        }
        List<Object> resCartList = new ArrayList<>(containsKeyMap.values());//map转换成List
        return ServerResponse.createBySuccess("操作成功", resCartList);
    }

    /**
     * 查询人工购物车商品（补人工时）
     * @param userToken
     * @param cart
     * @return
     */
    public ServerResponse queryWorkerCategoryCart(String userToken, Cart cart){
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
                .andEqualTo(Cart.MEMBER_ID, operator.getId())
                .andEqualTo(Cart.PRODUCT_TYPE,cart.getProductType());//如果是人工商品，此字段不能为空、
        example.setOrderByClause(" category_id");
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
            StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(cart1.getProductId());
            DjBasicsProductTemplate product = iMasterProductTemplateMapper.selectByPrimaryKey(cart1.getProductId());
            if(product==null||StringUtils.isNotBlank(product.getId())){
                product = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            }
            map.put("shopCount",cart1.getShopCount());//购买量
            map.put("image",StringTool.getImage(product.getImage(),address));
            // }
            if(!containsKeyMap.containsKey(cart1.getCategoryId())){//判断是否有已有的大类
                BasicsGoodsCategory goodsCategory =iMasterBasicsGoodsCategoryMapper.selectByPrimaryKey(cart1.getCategoryId());
                //basicsGoodsCategoryAPI.getGoodsCategory(request.getParameter(Constants.CITY_ID), cart1.getCategoryId());
                if (goodsCategory != null) {
                    BasicsGoodsCategory goodsCategorytop = basicsGoodsCategoryAPI.getGoodsCategory(request.getParameter(Constants.CITY_ID), goodsCategory.getParentTop());
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
                containsKeyMap.put(cart1.getCategoryId(),categoryMap);
            }

        }
        List<Object> resCartList = new ArrayList<>(containsKeyMap.values());//map转换成List
        return ServerResponse.createBySuccess("操作成功", resCartList);
    }
    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    public ServerResponse askAndQuit(HttpServletRequest request, String userToken, PageDTO pageDTO, String houseId, String categoryId, String name) {
        try {
            House house = iHouseMapper.selectByPrimaryKey(houseId);
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String productType = "0";
            if (worker.getWorkerType() == 3) {
                productType = "1";
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
           // PageInfo pageResult = productAPI.queryProductData(cityId, pageDTO.getPageNum(), pageDTO.getPageSize(), name, categoryId, productType, productIdArr);
            List<BasicsGoodsCategory> basicsGoodsCategories;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if(StringUtils.isNotBlank(categoryId)){
                Example example=new Example(BasicsGoodsCategory.class);
                example.createCriteria().andEqualTo(BasicsGoodsCategory.IS_LAST_CATEGORY,1)
                        .andEqualTo(BasicsGoodsCategory.PARENT_TOP,categoryId);
                basicsGoodsCategories =
                        iMasterBasicsGoodsCategoryMapper.selectByExample(example);
            }else {
                basicsGoodsCategories =
                        iMasterBasicsGoodsCategoryMapper.queryHouseWarehouseGoodsCategory(houseId);
            }
            List<Map> list=new ArrayList<>();
            for (BasicsGoodsCategory basicsGoodsCategory : basicsGoodsCategories) {
                List<DjBasicsProductTemplateDTO> djBasicsProductTemplateDTOS = iMasterProductTemplateMapper.queryProductData(houseId,name, basicsGoodsCategory.getId(), productType);
                List<WarehouseDTO> warehouseDTOS=new ArrayList<>();
                for (DjBasicsProductTemplateDTO djBasicsProductTemplateDTO : djBasicsProductTemplateDTOS) {
                    WarehouseDTO warehouseDTO = new WarehouseDTO();
                    warehouseDTO.setProductId(djBasicsProductTemplateDTO.getStorefrontProductId());
                    warehouseDTO.setMaket(1);
                    if ((djBasicsProductTemplateDTO.getMaket()!=null&&"0".equals(djBasicsProductTemplateDTO.getMaket()))
                            ||(djBasicsProductTemplateDTO.getType()!=null&& "0".equals(djBasicsProductTemplateDTO.getType()))) {
                        warehouseDTO.setMaket(0);
                    }
                    warehouseDTO.setPrice(Double.parseDouble(String.valueOf(djBasicsProductTemplateDTO.getPrice())));
                    warehouseDTO.setProductType(Integer.parseInt(productType));
                    warehouseDTO.setImage(address + djBasicsProductTemplateDTO.getImage());
                    BasicsGoods basicsGoods = iMasterBasicsGoodsMapper.selectByPrimaryKey(djBasicsProductTemplateDTO.getGoodsId());
                    if (basicsGoods != null) {
                        warehouseDTO.setSales(basicsGoods.getSales());
                    }
                    warehouseDTO.setUnitName(djBasicsProductTemplateDTO.getUnitName());
                    warehouseDTO.setImage(address + djBasicsProductTemplateDTO.getImage());
                    warehouseDTO.setImageUrl(address+ djBasicsProductTemplateDTO.getImage());
                    warehouseDTO.setShopCount(djBasicsProductTemplateDTO.getShopCount());
                    warehouseDTO.setAskCount(djBasicsProductTemplateDTO.getAskCount());
                    warehouseDTO.setBackCount((djBasicsProductTemplateDTO.getWorkBack() == null ? 0D : djBasicsProductTemplateDTO.getWorkBack()));
                    warehouseDTO.setRealCount(djBasicsProductTemplateDTO.getShopCount() - djBasicsProductTemplateDTO.getBackCount());
                    warehouseDTO.setSurCount(djBasicsProductTemplateDTO.getShopCount() - (djBasicsProductTemplateDTO.getOwnerBack() == null ? 0D : djBasicsProductTemplateDTO.getOwnerBack()) - djBasicsProductTemplateDTO.getAskCount());//所有买的数量 - 退货 - 收的=仓库剩余
                    warehouseDTO.setPrice(djBasicsProductTemplateDTO.getPrice());
                    warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * djBasicsProductTemplateDTO.getPrice());
                    warehouseDTO.setReceive(djBasicsProductTemplateDTO.getReceive() - (djBasicsProductTemplateDTO.getWorkBack() == null ? 0D : djBasicsProductTemplateDTO.getWorkBack()));
                    warehouseDTO.setAskTime(djBasicsProductTemplateDTO.getAskTime());
                    warehouseDTO.setRepTime(djBasicsProductTemplateDTO.getRepTime());
                    warehouseDTO.setBackTime(djBasicsProductTemplateDTO.getBackTime());
                    warehouseDTO.setStorefrontId(djBasicsProductTemplateDTO.getStorefrontId());
                    warehouseDTO.setProductName(djBasicsProductTemplateDTO.getProductName());
                    warehouseDTO.setProductSn(djBasicsProductTemplateDTO.getProductSn());
                    warehouseDTOS.add(warehouseDTO);
                }
                if(warehouseDTOS.size()>0){
                    Map map=new HashMap();
                    map.put("categoryId",basicsGoodsCategory.getId());
                    map.put("name",basicsGoodsCategory.getName());
                    map.put("warehouseDTOS",warehouseDTOS);
                    list.add(map);
                }
            }
            if(list.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult=new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询分类
    public ServerResponse queryGoodsCategory(HttpServletRequest request, String userToken, String houseId) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
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
            BasicsGoodsCategory goodsCategory = basicsGoodsCategoryAPI.getGoodsCategory(house.getCityId(), categoryId);
            if (goodsCategory != null) {
                BasicsGoodsCategory goodsCategorytop = basicsGoodsCategoryAPI.getGoodsCategory(house.getCityId(), goodsCategory.getParentTop());
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

package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.ShoppingCartDTO;
import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberCollectMapper;
import com.dangjia.acg.mapper.product.IMasterGoodsMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.product.IShoppingCartMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberCollect;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author:Chenyufeng
 * title:新版购物车处理类
 * date:2019.9.15
 */
@Service
public class ShopCartService {


    @Autowired
    private IMasterGoodsMapper iMasterGoodsMapper;

    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IShoppingCartMapper iShoppingCartmapper;

    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    private Logger logger = LoggerFactory.getLogger(ShopCartService.class);

    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;

    @Autowired
    private IMemberCollectMapper iMemberCollectMapper;

    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 获取购物车列表
     *
     * @param userToken
     * @return
     */
    public ServerResponse queryCartList(PageDTO pageDTO,String userToken, String cityId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<String> strings = iShoppingCartmapper.queryStorefrontIds(member.getId(), cityId);
            List<ShoppingCartDTO> shoppingCartDTOS=new ArrayList<>();
            strings.forEach(str ->{
                ShoppingCartDTO shoppingCartDTO=new ShoppingCartDTO();
                Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(str);
                shoppingCartDTO.setStorefrontName(storefront.getStorefrontName());
                shoppingCartDTO.setStorefrontId(storefront.getId());
                shoppingCartDTO.setStorefrontIcon(imageAddress+storefront.getSystemLogo());
                List<ShoppingCartListDTO> shoppingCartListDTOS = iShoppingCartmapper.queryCartList(member.getId(), cityId, str,null);
                shoppingCartListDTOS.forEach(shoppingCartListDTO -> {
                    shoppingCartListDTO.setImage(imageAddress+shoppingCartListDTO.getImage());
                });
                shoppingCartDTO.setShoppingCartListDTOS(shoppingCartListDTOS);
                shoppingCartDTOS.add(shoppingCartDTO);
            });
            if(shoppingCartDTOS.size()<=0)
                return  ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(shoppingCartDTOS);
            return ServerResponse.createBySuccess("获取购物车列表成功!",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统报错，获取购物车列表失败!");
        }
    }
    /**
     * 获取购物车数量
     *
     * @param userToken
     * @return
     */
    public ServerResponse getCartNum(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return ServerResponse.createBySuccess("获取购物车数量成功!",0);
            }
            Member member = (Member) object;
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId());
            Integer gnum = iShoppingCartmapper.selectCountByExample(example);
            return ServerResponse.createBySuccess("获取购物车数量成功!",gnum);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统报错，获取购物车信息失败!");
        }
    }
    /**
     * 清空购物车
     *
     * @param userToken
     * @return
     */
    public ServerResponse delCar(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId());
            int i = iShoppingCartmapper.deleteByExample(example);
            if (i >= 0) {
                return ServerResponse.createBySuccessMessage("清空购物车成功!");
            } else {
                return ServerResponse.createBySuccessMessage("清空购物车失败!");
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，清空购物车失败!");
        }
    }

    /**
     * 修改购物车商品数量
     * @param shopCartId
     * @param shopCount
     * @return
     */
    public ServerResponse updateCart(String shopCartId, Integer shopCount) {
        try {
            ShoppingCart newShoppingCart=new ShoppingCart();
            ShoppingCart oldShoppingCart = iShoppingCartmapper.selectByPrimaryKey(shopCartId);
            if(shopCount==0){
                return ServerResponse.createBySuccessMessage("修改成功");
            } else if(oldShoppingCart.getShopCount()==1&&shopCount<0){
                return ServerResponse.createByErrorMessage("受不了了,宝贝不能在减少了哦");
            } else if(shopCount==1||shopCount==-1){
                newShoppingCart.setId(shopCartId);
                newShoppingCart.setShopCount(oldShoppingCart.getShopCount()+shopCount);
                iShoppingCartmapper.updateByPrimaryKeySelective(newShoppingCart);
            }else {
                newShoppingCart.setId(shopCartId);
                newShoppingCart.setShopCount(shopCount);
                iShoppingCartmapper.updateByPrimaryKeySelective(newShoppingCart);
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，修改购车数量失败!");
        }
    }


    /**
     * 加入购物车
     *
     * @param userToken
     * @param cityId
     * @param productId
     * @param shopCount
     * @return
     */
    public ServerResponse addCart(String userToken, String cityId, String productId, Integer shopCount) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
            DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            BasicsGoods goods = iMasterGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
            //有房有精算  根据用户的member_id去区分
            //无房无精算  根据用户的member_id去区分
            //purchaseRestrictions:0自由购房；1有房无精算；2有房有精算
            Integer purchaseRestrictions = iShoppingCartmapper.queryPurchaseRestrictions(productId);
            Example example=new Example(House.class);
            if(purchaseRestrictions==1){
                example.createCriteria().andEqualTo(House.MEMBER_ID,member.getId())
                        .andEqualTo(House.DATA_STATUS,0);
                List<House> houses = iHouseMapper.selectByExample(example);
                if(houses.size()<=0)
                    return ServerResponse.createByErrorMessage("加入购物车失败,该商品为有房才能购买!");
            } else if(purchaseRestrictions==2){
                List<House> houses = iShoppingCartmapper.queryWhetherThereIsActuarial(member.getId());
                if(houses.size()<=0)
                    return ServerResponse.createByErrorMessage("加入购物车失败,该商品为有房有精算才能购买!");
            }
            //判断去重,如果有的话就购买数量加1
            example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                    .andEqualTo(ShoppingCart.PRODUCT_ID, productId)
                    .andEqualTo(ShoppingCart.STOREFRONT_ID,storefrontProduct.getStorefrontId());
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            if (list.size() == 0) {
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setMemberId(member.getId());
                shoppingCart.setProductId(productId);
                shoppingCart.setProductSn(djBasicsProductTemplate.getProductSn());
                shoppingCart.setProductName(storefrontProduct.getProductName());
                shoppingCart.setPrice(new BigDecimal(storefrontProduct.getSellPrice()));
                shoppingCart.setShopCount(shopCount);
                shoppingCart.setUnitName(djBasicsProductTemplate.getUnitName());
                shoppingCart.setCategoryId(djBasicsProductTemplate.getCategoryId());
                shoppingCart.setProductType(goods.getType());
                shoppingCart.setStorefrontId(storefrontProduct.getStorefrontId());
                shoppingCart.setCityId(cityId);
                shoppingCart.setImage(storefrontProduct.getImage());
                if (iShoppingCartmapper.insert(shoppingCart) > 0)
                    return ServerResponse.createBySuccessMessage("加入购物车成功!");
                return ServerResponse.createBySuccessMessage("加入购物车失败!");
            } else {
                ShoppingCart myCart = list.get(0);
                myCart.setShopCount(myCart.getShopCount() + 1);
                if (iShoppingCartmapper.updateByPrimaryKeySelective(myCart) > 0)
                    return ServerResponse.createBySuccessMessage("加入购物车成功!");
                return ServerResponse.createBySuccessMessage("加入购物车失败!");
            }
        } catch (Exception e) {
            logger.info("添加失败",e);
            return ServerResponse.createByErrorMessage("系统报错，加入购物车失败!");
        }
    }


    public ServerResponse cartSettle(String userToken) {
        try {

            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            //第一步
            //购物车结算：生成订单方法{获取购物车商品，插入订单}，并返回订单ID
            /**
             *    购物车有变动，重写
             */

            return null;
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，删除已选商品失败!");
        }
    }

    /**
     * 删除已选购物车
     */
    public ServerResponse delCheckCart(String shopCartIds) {
        try {
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andIn(ShoppingCart.ID, Arrays.asList(shopCartIds.split(",")));
            int i = iShoppingCartmapper.deleteByExample(example);
            if (i >= 0) {
                return ServerResponse.createBySuccessMessage("删除已选商品成功!");
            } else {
                return ServerResponse.createBySuccessMessage("删除已选商品失败!");
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，删除已选商品失败!");
        }
    }


    /**
     * 更换购物车商品
     * @param shoppingCartId
     * @param productId
     * @return
     */
    public ServerResponse replaceShoppingCart(String shoppingCartId, String productId) {
        try {
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
            DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            BasicsGoods goods = iMasterGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setId(shoppingCartId);
            shoppingCart.setProductId(productId);
            shoppingCart.setProductSn(djBasicsProductTemplate.getProductSn());
            shoppingCart.setProductName(storefrontProduct.getProductName());
            shoppingCart.setImage(storefrontProduct.getImage());
            shoppingCart.setPrice(new BigDecimal(storefrontProduct.getSellPrice()));
            shoppingCart.setProductType(goods.getType());
            shoppingCart.setUnitName(djBasicsProductTemplate.getUnitName());
            shoppingCart.setStorefrontId(storefrontProduct.getStorefrontId());
            iShoppingCartmapper.updateByPrimaryKeySelective(shoppingCart);
            return ServerResponse.createBySuccessMessage("更换成功!");
        } catch (Exception e) {
            logger.info("系统报错,更换失败!",e);
            return ServerResponse.createByErrorMessage("系统报错,更换失败!");
        }
    }


    /**
     * 购物车移入收藏
     * @param userToken
     * @param jsonStr
     * @return
     */
    public ServerResponse insertToCollect(String userToken, String jsonStr) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            Example example=new Example(MemberCollect.class);
            for (Object o : jsonArr) {
                JSONObject obj = (JSONObject) o;
                String id=obj.getString("id");
                String storefrontId = obj.getString("storefrontId");
                String productId=obj.getString("productId");
                example.createCriteria().andEqualTo(MemberCollect.CONDITION_TYPE,1)
                        .andEqualTo(MemberCollect.DATA_STATUS,0)
                        .andEqualTo(MemberCollect.COLLECT_ID,productId)
                        .andEqualTo(MemberCollect.MEMBER_ID,member.getId())
                        .andEqualTo(MemberCollect.STOREFRONT_ID,storefrontId);
                List<MemberCollect> memberCollects = iMemberCollectMapper.selectByExample(example);
                if(memberCollects.size()>0)
                    return ServerResponse.createBySuccessMessage("移入成功");
                MemberCollect memberCollect=new MemberCollect();
                memberCollect.setCollectId(productId);
                memberCollect.setConditionType("1");
                memberCollect.setMemberId(member.getId());
                memberCollect.setStorefrontId(storefrontId);
                iMemberCollectMapper.insert(memberCollect);
                iShoppingCartmapper.deleteByPrimaryKey(id);
            }
            return ServerResponse.createBySuccessMessage("移入成功");
        } catch (Exception e) {
            logger.info("添加失败",e);
            return ServerResponse.createByErrorMessage("系统报错，移入失败!");
        }
    }
}

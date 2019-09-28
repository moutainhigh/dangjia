package com.dangjia.acg.service.product;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.member.IMemberCollectMapper;
import com.dangjia.acg.mapper.product.ISaleOrderDetailMapper;
import com.dangjia.acg.mapper.product.ISaleOrderMapper;
import com.dangjia.acg.mapper.product.IShoppingCartMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;


/**
 * author:Chenyufeng
 * title:新版购物车处理类
 * date:2019.9.15
 */
@Service
public class ShopCartService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private IMemberCollectMapper iMemberCollectMapper;

    @Autowired
    private DjBasicsProductAPI djBasicsProductAPI ;

    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IShoppingCartMapper iShoppingCartmapper;

    @Autowired
    private ForMasterAPI forMasterAPI;

    @Autowired
    private ISaleOrderMapper isaleOrderMapper ;

    @Autowired
    private ISaleOrderDetailMapper isaleOrderDetailMapper ;

    /**
     * 获取购物车列表
     * @param userToken
     * @return
     */
    public ServerResponse queryCartList(String userToken,String productId) {
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            Example example = new Example(ShoppingCart.class);
            Example.Criteria criteria=example.createCriteria();
            criteria.andEqualTo(ShoppingCart.MEMBER_ID,operator.getId());
            if(StringUtils.isNotEmpty(productId))
            {
                criteria.andEqualTo(ShoppingCart.PRODUCT_ID, productId);
            }
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            return ServerResponse.createBySuccess("获取购物车列表成功!",list);
        }
        catch(Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，获取购物车列表失败!");
        }
    }

    /**
     * 清空购物车
     * @param userToken
     * @return
     */
    public ServerResponse delCar(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, operator.getId());
            int i = iShoppingCartmapper.deleteByExample(example);
            if (i >= 0) {
                return ServerResponse.createBySuccess("清空购物车成功!",i);
            } else {
                return ServerResponse.createBySuccessMessage("清空购物车失败!");
            }
        }
        catch(Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，清空购物车失败!");
        }
    }

    /**
     *修改购物车商品数量
     * @param request
     * @param userToken
     * @return
     */
    public ServerResponse updateCart(HttpServletRequest request, String userToken, String productId,Integer shopCount) {
        try {
            request.setAttribute(Constants.CITY_ID, request.getParameter(Constants.CITY_ID));
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;

            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID,operator.getId()).andEqualTo(ShoppingCart.PRODUCT_ID,productId);
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            if (list.size() > 0) {
                ShoppingCart mycart = list.get(0);
                if (shopCount < 0) {
                    iShoppingCartmapper.delete(mycart);
                }
                mycart.setShopCount(shopCount);
                int i = iShoppingCartmapper.updateByPrimaryKeySelective(mycart);
                if (i > 0) {
                    return ServerResponse.createBySuccessMessage("修改购物车数量成功!");

                } else {
                    return ServerResponse.createBySuccessMessage("修改购物车数量失败!");
                }
            } else {
                if (shopCount > 0) {
                    ServerResponse serverResponse = djBasicsProductAPI.getProductById(request.getParameter(Constants.CITY_ID), productId);
                    if (serverResponse != null && serverResponse.getResultObj() != null) {
                        Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
                        BasicsGoods goods = forMasterAPI.getGoods(request.getParameter(Constants.CITY_ID), product.getGoodsId());
                        ShoppingCart newshoppingCart = new ShoppingCart();
                        newshoppingCart.setProductSn(product.getProductSn());//产品编码
                        newshoppingCart.setProductName(product.getName());//获取产品名称
                        newshoppingCart.setMemberId(operator.getId());//获取用户ID
                        newshoppingCart.setPrice(new BigDecimal(product.getPrice()));//产品价格
                        newshoppingCart.setProductType(product.getType());//产品类型
                        newshoppingCart.setUnitName(product.getUnitName());//单位名称
                        newshoppingCart.setCategoryId(product.getCategoryId());//分类编号
                        newshoppingCart.setCityId(request.getParameter(Constants.CITY_ID));//所属城市
                        int i = iShoppingCartmapper.insert(newshoppingCart);
                        if (i > 0) {
                            return ServerResponse.createBySuccessMessage("插入购物成功!");
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，修改购车数量失败!");
        }
    }


    /**
     * 加入购物车
     * @param userToken
     * @param cityId
     * @param productId
     * @param productSn
     * @param productName
     * @param price
     * @param shopCount
     * @param unitName
     * @param categoryId
     * @param productType
     * @param seller
     * @return
     */
    public ServerResponse addCart(String userToken, String cityId,String productId,
                                  String productSn, String productName,String price,String shopCount,
                                  String unitName,String categoryId,String productType,String seller) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            //有房有精算  根据用户的member_id去区分
            //无房无精算  根据用户的member_id去区分
            //判断去重,如果有的话就购买数量加1
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, operator.getId()).andEqualTo(ShoppingCart.PRODUCT_ID, productId);
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            if (list.size() == 0) {
                ShoppingCart shoppingCart=new ShoppingCart();
                shoppingCart.setMemberId(operator.getId());
                shoppingCart.setProductId(productId);
                shoppingCart.setProductSn(productSn);
                shoppingCart.setProductName(productName);
                shoppingCart.setPrice(new BigDecimal(price));
                shoppingCart.setShopCount(Integer.parseInt(shopCount));
                shoppingCart.setUnitName(unitName);
                shoppingCart.setCategoryId(categoryId);
                shoppingCart.setProductType(Integer.parseInt(productType));
                shoppingCart.setSeller(seller);
                int i = iShoppingCartmapper.insert(shoppingCart);
                if (i > 0) {
                    return ServerResponse.createBySuccessMessage("加入购物车成功!");
                } else {
                    return ServerResponse.createBySuccessMessage("加入购物车失败!");
                }
            } else {
                ShoppingCart myCart = list.get(0);
                myCart.setShopCount(myCart.getShopCount() + 1);
                int i = iShoppingCartmapper.updateByPrimaryKeySelective(myCart);
                if (i > 0) {
                    return ServerResponse.createBySuccessMessage("加入购物车成功!");
                } else {
                    return ServerResponse.createBySuccessMessage("加入购物车失败!");
                }
            }
        } catch (Exception e) {
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
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, operator.getId());
            List<ShoppingCart> list = iShoppingCartmapper.selectByExample(example);
            for(ShoppingCart shoppingCart :list)
            {

            }
            return null;
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，删除已选商品失败!");
        }
    }

    /**
     * 删除已选购物车
     */
    public ServerResponse delCheckCart(String userToken,String productId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, operator.getId()).andEqualTo(ShoppingCart.PRODUCT_ID, productId);
            int i = iShoppingCartmapper.deleteByExample(example);
            if (i >= 0) {
                return ServerResponse.createBySuccess("删除已选商品成功!");
            } else {
                return ServerResponse.createBySuccessMessage("删除已选商品失败!");
            }
        }
        catch(Exception e) {
            return ServerResponse.createByErrorMessage("系统报错，删除已选商品失败!");
        }
    }
}

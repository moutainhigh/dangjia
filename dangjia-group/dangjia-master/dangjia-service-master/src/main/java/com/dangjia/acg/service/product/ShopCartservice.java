package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.CartDTO;
import com.dangjia.acg.mapper.member.IMemberCollectMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * author:Chenyufeng
 * title:新版购物车处理类
 * date:2019.9.15
 */
@Service
public class ShopCartservice {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMemberCollectMapper iMemberCollectMapper;

    /**
     * 加入购物车
     *
     * @param request
     * @param userToken
     * @param productId
     * @param num
     * @return
     */
    public ServerResponse add(HttpServletRequest request, String userToken, String productId, int num) {

        try{
            Boolean exists = redisClient.exists(productId);//首先判断redis数据库中是否存在当前键
            if (exists) {
                //此处要修改，用用户的的Userid进行区分
                //获取现有的购物车中的数据
                String jsonStr = redisClient.getCache(Constants.REDIS_DANGJIA_CACHE + productId.toString(), String.class);
                if (jsonStr != null) {
                    CartDTO cartDTO = JSON.toJavaObject(JSONObject.parseObject(jsonStr), CartDTO.class);
                    cartDTO.setProductNum(cartDTO.getProductNum() + num);
                    redisClient.put(Constants.REDIS_DANGJIA_CACHE + productId.toString(), JSON.toJSON(jsonStr).toString());
                } else {
                    return ServerResponse.createBySuccess("没有检索到数据", 0);
                }
                return ServerResponse.createBySuccess("检索到数据", 1);
            }
            //根据商品ID获取商品
            //设置购物车值
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("检索到数据异常");
        }

    }

    /**
     * 获取购物车列表
     *
     * @param request
     * @param userToken
     * @return
     */
    public ServerResponse getCartList(HttpServletRequest request, String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createBySuccess("您还没有房子,不能查看购物车列表!", 0);
            }

            //此处要修改，用用户的的Userid进行区分
            List<String> jsonList = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), String.class);
            List<CartDTO> cartDtoList = new LinkedList<CartDTO>();
            for (String json : jsonList) {
                CartDTO cartDTO = JSON.toJavaObject(JSONObject.parseObject(json), CartDTO.class);
                cartDtoList.add(cartDTO);
            }
            return ServerResponse.createBySuccess("查询购物车成功!", cartDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询购物车异常");
        }

    }

    /**
     * 更新数量
     *
     * @param request
     * @param userToken
     * @param productId
     * @param num
     * @return
     */
    public ServerResponse updateCartNum(HttpServletRequest request, String userToken, String productId, int num) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createBySuccess("您还没有房子,不能更新数量!", 0);
            }

//            String userId = "测试";
            String json = redisClient.getCache(Constants.REDIS_DANGJIA_CACHE + member.getId(), String.class);
            if (json == null) {
                return ServerResponse.createBySuccess("更新数量失败!", 0);
            }
            CartDTO cartDTO = JSON.toJavaObject(JSONObject.parseObject(json), CartDTO.class);
            cartDTO.setProductNum(num);
            redisClient.put(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), JSON.toJSON(cartDTO).toString());
            return ServerResponse.createBySuccess("更新数量成功!", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新数量异常");
        }

    }


    /**
     * 全选商品
     *
     * @param request
     * @param userToken
     * @param checked
     * @return
     */
    public ServerResponse checkAll(HttpServletRequest request, String userToken, String checked) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createBySuccess("您还没有房子,不能全选商品!", 0);
            }
            //获取商品列表
            List<String> jsonList = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), String.class);
            for (String json : jsonList) {
                CartDTO cartDTO = JSON.toJavaObject(JSONObject.parseObject(json), CartDTO.class);
                if ("true".equals(checked)) {
                    cartDTO.setCheck("1");
                } else if ("false".equals(checked)) {
                    cartDTO.setCheck("0");
                } else {
                    return ServerResponse.createBySuccess("全选失败!", 1);
                }
            }
            return ServerResponse.createBySuccess("全选商品成功!", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("全选商品异常");
        }

    }

    /**
     * 删除勾选商品
     *
     * @param request
     * @param userToken
     * @param productId
     * @return
     */
    public ServerResponse delCartProduct(HttpServletRequest request, String userToken, String productId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createBySuccess("您还没有房子,不能删除勾选商品!", 0);
            }
            redisClient.deleteCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString());
            return ServerResponse.createBySuccess("删除勾选商品成功!", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除勾选商品异常");
        }

    }

    /**
     * 清空购物车
     *
     * @param request
     * @param userToken
     * @return
     */
    public ServerResponse delCart(HttpServletRequest request, String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createBySuccess("您还没有房子,不能清空!", 0);
            }
            //   String userId = null;
            redisClient.deleteCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString());
            return ServerResponse.createBySuccess("清空购物车成功!", 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("清空购物车异常");
        }

    }

}

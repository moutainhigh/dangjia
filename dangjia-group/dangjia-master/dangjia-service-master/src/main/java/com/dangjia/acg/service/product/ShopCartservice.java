package com.dangjia.acg.service.product;

import cn.jiguang.common.utils.StringUtils;
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
import java.util.ArrayList;
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
     * @param cartDTO
     * @return
     */
    public ServerResponse add(HttpServletRequest request, String userToken, CartDTO cartDTO) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createBySuccess("您还没有房子,不能选购商品!");
            }
            //首先判断redis数据库中是否存在当前键
            Boolean exists = redisClient.exists(Constants.REDIS_DANGJIA_CACHE + member.getId().toString());
            if (exists) {
                //获取现有的购物车中的数据
                List<CartDTO> cartDTOlist = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), CartDTO.class);
                if (cartDTOlist != null) {
                    for (CartDTO clscartDto : cartDTOlist) {
                        String productId = clscartDto.getProductId();
                        //判断是同一商品
                        if (cartDTO.getProductId().equals(productId)) {
                            //数量相加
                            clscartDto.setProductNum(clscartDto.getProductNum() + cartDTO.getProductNum());
                        }
                    }
                    redisClient.putListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), cartDTOlist);
                    return ServerResponse.createBySuccess("商品加入购物车成功");
                } else {
                    return ServerResponse.createBySuccess("数据库中不存在当前购物车");
                }
            }
            //redis数据库里面不存在当前存储的键,键生成方式:Constants.REDIS_DANGJIA_CACHE + member.getId().toString()，就往购物车里面增加商品
            List<CartDTO> list = new ArrayList<CartDTO>();
            CartDTO clsCartDTO = new CartDTO();
            clsCartDTO.setProductId(cartDTO.getProductId());
            clsCartDTO.setProductNum(cartDTO.getProductNum());
            clsCartDTO.setCheck(cartDTO.getCheck());
            clsCartDTO.setProductIcon(cartDTO.getProductIcon());
            clsCartDTO.setProductPrice(cartDTO.getProductPrice());
            clsCartDTO.setProductStatus(cartDTO.getProductStatus());
            list.add(clsCartDTO);
            //将商品集合存入redis
            redisClient.putListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), list);
            return ServerResponse.createByErrorMessage("增加购物车成功!");
        } catch (Exception e) {
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
                return ServerResponse.createBySuccess("您还没有房子,不能查看购物车列表!");
            }
            //获取缓存列表中的集合
            List<CartDTO> cartDTOList = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), CartDTO.class);
            return ServerResponse.createBySuccess("查询购物车列表成功!", cartDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询购物车列表异常");
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
                return ServerResponse.createBySuccess("您还没有房子,不能更新数量!");
            }
            if (StringUtils.isNotEmpty(productId)) {
                //获取缓存中存储的列表集合
                List<CartDTO> cartDTOList = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), CartDTO.class);
                for (CartDTO cartDTO : cartDTOList) {
                    String redisProductId = cartDTO.getProductId();
                    if (redisProductId.equals(productId)) {
                        cartDTO.setProductNum(cartDTO.getProductNum() + num);
                    }
                }
                //重新封装list，存储到redis数据库
                redisClient.putListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), cartDTOList);
                return ServerResponse.createBySuccess("更新数量成功!");
            } else {
                return ServerResponse.createByErrorMessage("商品编号不能为空!");
            }
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
            List<CartDTO> cartDTOList = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), CartDTO.class);
            for (CartDTO cartDTO : cartDTOList) {
                if ("true".equals(checked)) {
                    cartDTO.setCheck("1");
                } else if ("false".equals(checked)) {
                    cartDTO.setCheck("0");
                } else {
                    return ServerResponse.createBySuccess("全选失败!");
                }
            }
            redisClient.putListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), cartDTOList);
            return ServerResponse.createBySuccess("全选商品成功!");
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

            List<CartDTO> cartDTOList = redisClient.getListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), CartDTO.class);
            for (CartDTO cartDTO : cartDTOList) {
                if (cartDTO.getProductId().equals(productId)) {
                    cartDTOList.remove(cartDTO);//删除当前匹配的对象实体
                }
            }
            //重新封装list，存储到redis数据库
            redisClient.putListCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString(), cartDTOList);
            return ServerResponse.createBySuccess("删除勾选商品成功!");
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
                return ServerResponse.createBySuccess("您还没有房子,不能清空!");
            }
            redisClient.deleteCache(Constants.REDIS_DANGJIA_CACHE + member.getId().toString());
            return ServerResponse.createBySuccess("清空购物车成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("清空购物车异常");
        }

    }

}

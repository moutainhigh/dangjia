package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.deliver.IChangeMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.Change;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: qyx
 * Date: 2019/04/15 0009
 * Time: 10:55
 */
@Service
public class ChangeService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IChangeMapper changeMapper;
    @Autowired
    private ProductAPI productAPI;


    /**
     * 设置换货车商品数量
     * @param request
     * @param userToken
     * @param change
     * @return
     */
    public ServerResponse setChange(HttpServletRequest request, String userToken, Change change){
        request.setAttribute(Constants.CITY_ID,request.getParameter(Constants.CITY_ID));
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member operator = accessToken.getMember();
        Example example = new Example(Change.class);
        example.createCriteria()
                .andEqualTo(Change.HOUSE_ID,change.getHouseId())
                .andEqualTo(Change.ORIGINAL_PRODUCT_ID,change.getOriginalProductId())
                .andEqualTo(Change.MEMBER_ID,operator.getId());
        List<Change> list=changeMapper.selectByExample(example);
        if(list.size()>0){
            Change change1=list.get(0);
            if(change.getOriginalSurCount()<0||change.getNewSurCount()<0){
                changeMapper.delete(change1);
            }else {
                change.setId(change1.getId());
                change1 = getNewProduct(request,change);
                changeMapper.updateByPrimaryKeySelective(change1);
            }
        }else{
            if(change.getOriginalSurCount()>0){
                change.setMemberId(operator.getId());
                change = getNewProduct(request,change);
                changeMapper.insert(change);
            }
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public Change getNewProduct(HttpServletRequest request,Change change){
        request.setAttribute(Constants.CITY_ID,request.getParameter(Constants.CITY_ID));
        ServerResponse serverResponse=productAPI.getProductById(request,change.getOriginalProductId());
        if(serverResponse!=null&&serverResponse.getResultObj()!=null){
            Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
            change.setOriginalProductSn(product.getProductSn());
            change.setOriginalProductName(product.getName());
            change.setOriginalPrice(product.getPrice());
            change.setOriginalUnitName(product.getUnitName());
            change.setCategoryId(product.getCategoryId());
        }
        serverResponse=productAPI.getProductById(request,change.getNewProductId());
        if(serverResponse!=null&&serverResponse.getResultObj()!=null){
            Product product = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Product.class);
            change.setNewProductSn(product.getProductSn());
            change.setNewProductName(product.getName());
            change.setNewPrice(product.getPrice());
            change.setNewUnitName(product.getUnitName());
        }
        return change;
    }
    /**
     * 清空换货车商品
     * @param userToken
     * @param change
     * @return
     */
    public ServerResponse clearChange(String userToken, Change change){
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member operator = accessToken.getMember();
        Example example = new Example(Change.class);
        example.createCriteria()
                .andEqualTo(Change.HOUSE_ID,change.getHouseId())
                .andEqualTo(Change.MEMBER_ID,operator.getId());
        changeMapper.deleteByExample(example);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 查询换货车商品
     * @param userToken
     * @param change
     * @return
     */
    public ServerResponse queryChange(String userToken, Change change){
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member operator = accessToken.getMember();
        Example example = new Example(Change.class);
        example.createCriteria()
                .andEqualTo(Change.HOUSE_ID,change.getHouseId())
                .andEqualTo(Change.MEMBER_ID,operator.getId());
        List<Change> list=changeMapper.selectByExample(example);
        return ServerResponse.createBySuccess("操作成功",list);
    }

}

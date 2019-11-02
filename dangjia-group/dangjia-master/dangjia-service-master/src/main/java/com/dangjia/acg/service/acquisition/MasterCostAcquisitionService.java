package com.dangjia.acg.service.acquisition;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.sw.EncodingXmlWriter;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.product.BasicsGoodsCategoryAPI;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.mapper.delivery.ICartMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.Cart;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * author: fzh
 * Date: 2019/11/02
 */
@Service
public class MasterCostAcquisitionService {
    protected static final Logger logger = LoggerFactory.getLogger(MasterCostAcquisitionService.class);
    @Autowired
    private IQuantityRoomMapper iQuantityRoomMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    /**
     * 获取商品的搬运费
     * @param houseId 房子ID
     * @param productId 商品ID
     * @param count 商品数量
     *
     *    * 计算可退搬运费
     *    * @param elevator 是否电梯房（1是，0否）
     *    * @param floor 电梯楼层
     *    * @param isUpstairsCost 是否按1层收取上楼费(1是，0否）
     *    * @param moveCost 每层搬运费
     *    * @param count 数量
     *    * @deprecated 1.先判断是否按1层收取上楼费
     *    *             1.1若为否，则判断是否为电梯房
     *    *             1.2若为否，则楼层数设置为实际楼层数，
     *    *             1.3若都不为否，则楼层数设为1
     *    *            可退搬运费=楼层数*每层搬运费*数量
     *    * @return
     * @return
     */
    public Double getStevedorageCost(String houseId,String productId,Double count){
        try{
            //查询房子信息，获取房子对应的楼层
            QuantityRoom quantityRoom=iQuantityRoomMapper.getQuantityRoom(houseId,0);
            Integer elevator= 1;//是否电梯房
            String floor="1";
            if(quantityRoom!=null&& StringUtils.isNotBlank(quantityRoom.getId())){
                elevator=quantityRoom.getElevator();//是否电梯房
                floor=quantityRoom.getFloor();//楼层
            }
            //查询商品信息，获取对应的搬运费设置信息
            StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
            String  isUpstairsCost= "1";//是否按1层收取上楼费
            double moveCost=0.0;
            if(storefrontProduct!=null&&storefrontProduct.getMoveCost()!=null){
                isUpstairsCost=storefrontProduct.getIsUpstairsCost();
                moveCost=storefrontProduct.getMoveCost().doubleValue();
            }
            //计算楼层数
            Double floorCount=1.0;//楼层数
            if("0".equals(isUpstairsCost)){//判断是否按1层收取上楼费，若为否
                if(elevator==0){//若不为电梯房，则楼层数设置为实际楼层数
                    if(org.apache.commons.lang.StringUtils.isNotBlank(floor)){
                        floorCount=new Double(floor).doubleValue();
                    }
                }
            }
            return MathUtil.mul(MathUtil.mul(floorCount,moveCost),count);
        }catch (Exception e){
            logger.error("搬运费计算异常",e);
            return 0.0;
        }
    }



}

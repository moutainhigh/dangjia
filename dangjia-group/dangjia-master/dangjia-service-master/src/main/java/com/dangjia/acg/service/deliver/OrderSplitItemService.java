package com.dangjia.acg.service.deliver;

import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.refund.RefundOrderItemDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IMasterBasicsGoodsMapper;
import com.dangjia.acg.mapper.core.IMasterBrandMapper;
import com.dangjia.acg.mapper.core.IMasterUnitMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:30
 * <p>
 * 生成发货单 发货操作类
 */
@Service
public class OrderSplitItemService {

    protected static final Logger logger = LoggerFactory.getLogger(OrderSplitItemService.class);
    @Autowired
    private IMasterBrandMapper iMasterBrandMapper;
    @Autowired
    private IMasterBasicsGoodsMapper iMasterBasicsGoodsMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMasterUnitMapper iMasterUnitMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private MasterProductTemplateService masterProductTemplateService;

    /**
     * 根据房子ID，工匠ID查询所有已购买的商品
     * @param houseId
     * @param workerId
     * @return
     */
    public PageInfo getOrderItemListByhouseMemberId(PageDTO pageDTO, String houseId, String workerId, String searchKey){
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        logger.info("getOrderItemListByhouseMemberId清理剩余材料：workerId={},houseId{}",workerId,houseId);
        List<OrderSplitItemDTO> orderSplitItemList=orderSplitItemMapper.getOrderItemListByhouseMemberId( houseId, workerId,searchKey);
         String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
         getProductList(orderSplitItemList,address);//商品信息
         PageInfo pageResult = new PageInfo(orderSplitItemList);
         return pageResult;
    }
    /**
     * 查询商品对应的规格详情，品牌，单位信息
     * @param productList
     * @param address
     */
    private  void getProductList(List<OrderSplitItemDTO> productList, String address){
        if(productList!=null&&productList.size()>0){
            for(OrderSplitItemDTO ap:productList){
                setProductInfo(ap,address);
            }
        }
    }
    /**
     * 替换对应的信息
     * @param ap
     * @param address
     */
    private  void setProductInfo(OrderSplitItemDTO ap,String address){
        String productTemplateId=ap.getProductTemplateId();
        DjBasicsProductTemplate pt=iMasterProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if(pt!=null&& StringUtils.isNotBlank(pt.getId())){
            String image=ap.getImage();
            if (image == null) {
                image=pt.getImage();
            }
            ap.setConvertUnit(pt.getConvertUnit());
            ap.setCost(pt.getCost());
            ap.setCategoryId(pt.getCategoryId());
            if(ap.getStorefrontIcon()!=null&&StringUtils.isNotBlank(ap.getStorefrontIcon())){
                ap.setStorefrontIcon(address+ap.getStorefrontIcon());
            }
            //添加图片详情地址字段
            String[] imgArr = image.split(",");
            //StringBuilder imgStr = new StringBuilder();
            // StringBuilder imgUrlStr = new StringBuilder();
            // StringTool.get.getImages(address, imgArr, imgStr, imgUrlStr);
            if(imgArr!=null&&imgArr.length>0){
                ap.setImageUrl(address+imgArr[0]);//图片详情地址设置
            }

            String unitId=pt.getUnitId();
            //查询单位
            if(pt.getConvertQuality()!=null&&pt.getConvertQuality()>0){
                unitId=pt.getConvertUnit();
            }
            if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                Unit unit= iMasterUnitMapper.selectByPrimaryKey(unitId);
                ap.setUnitId(unitId);
                ap.setUnitName(unit!=null?unit.getName():"");
                ap.setUnitType(unit!=null?unit.getType():2);
            }

            BasicsGoods goods=iMasterBasicsGoodsMapper.selectByPrimaryKey(pt.getGoodsId());
            ap.setProductType(goods.getType().toString());
            if(StringUtils.isNotBlank(goods.getBrandId())){
                Brand brand=iMasterBrandMapper.selectByPrimaryKey(goods.getBrandId());
                ap.setBrandId(goods.getId());
                ap.setBrandName(brand!=null?brand.getName():"");
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(masterProductTemplateService.getNewValueNameArr(pt.getValueIdArr()).replaceAll(",", " "));
            }
        }

    }

}

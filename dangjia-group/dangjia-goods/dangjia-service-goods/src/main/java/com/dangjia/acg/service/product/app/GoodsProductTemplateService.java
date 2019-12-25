package com.dangjia.acg.service.product.app;


import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.product.ProductAppDTO;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsProductTemplateService {

    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    @Autowired
    private IAttributeMapper iAttributeMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IBrandMapper iBrandMapper;
    @Autowired
    private ConfigUtil configUtil;
    /**
     * 获取对应的属性值信息(查询APP端显示的规格属性，属性值）
     * @param valueIdArr
     * @return
     */
    public String getNewValueNameArr(String valueIdArr){
        String strNewValueNameArr = "";
        String[] newValueNameArr = valueIdArr.split(",");
        for (int i = 0; i < newValueNameArr.length; i++) {
            String valueId = newValueNameArr[i];
            if (StringUtils.isNotBlank(valueId)) {
                AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
                if(attributeValue!=null&&StringUtils.isNotBlank(attributeValue.getName())){
                    Attribute attribute=iAttributeMapper.selectByPrimaryKey(attributeValue.getAttributeId());
                    if (attribute!=null&&attribute.getType()==2&&StringUtils.isNotBlank(strNewValueNameArr)) {
                        strNewValueNameArr = attributeValue.getName();
                    } else if (attribute!=null&&attribute.getType()==2){
                        strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                    }
                }

            }
        }
        return strNewValueNameArr;
    }

    public  void getProductList(List<ProductAppDTO> productList){
        if(productList!=null&&productList.size()>0){
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for(ProductAppDTO ap:productList){
                String image=ap.getImage();
                if (image == null) {
                    continue;
                }
                //添加图片详情地址字段
                ap.setImageUrl(StringTool.getImage(ap.getImage(),address));//图多张
                ap.setImageSingle(StringTool.getImageSingle(ap.getImage(),address));//图一张
                //查询单位
                String unitId=ap.getUnit();
                //查询单位
                if(ap.getConvertQuality()!=null&&ap.getConvertQuality()>0){
                    unitId=ap.getConvertUnit();
                }

                if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                    Unit unit= iUnitMapper.selectByPrimaryKey(unitId);
                    ap.setUnitName(unit!=null?unit.getName():"");
                    ap.setUnitType(unit.getType());
                }

                if(StringUtils.isNotBlank(ap.getBrandId())){
                    Brand brand=iBrandMapper.selectByPrimaryKey(ap.getBrandId());
                    ap.setBrandName(brand!=null?brand.getName():"");
                }
                //查询规格名称
                if (StringUtils.isNotBlank(ap.getValueIdArr())) {
                    ap.setValueNameArr(getNewValueNameArr(ap.getValueIdArr()).replaceAll(",", " "));
                }
            }
        }
    }
}

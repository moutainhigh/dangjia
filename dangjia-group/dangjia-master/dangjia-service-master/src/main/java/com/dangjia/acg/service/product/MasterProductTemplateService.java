package com.dangjia.acg.service.product;


import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.core.IMasterAttributeMapper;
import com.dangjia.acg.mapper.core.IMasterAttributeValueMapper;
import com.dangjia.acg.mapper.core.IMasterUnitMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterProductTemplateService {

    @Autowired
    private IMasterAttributeMapper iMasterAttributeMapper;
    @Autowired
    private IMasterAttributeValueMapper iMasterAttributeValueMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMasterUnitMapper iMasterUnitMapper;
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
                AttributeValue attributeValue = iMasterAttributeValueMapper.selectByPrimaryKey(valueId);
                if(attributeValue!=null&&StringUtils.isNotBlank(attributeValue.getName())){
                    Attribute attribute=iMasterAttributeMapper.selectByPrimaryKey(attributeValue.getAttributeId());
                    if (attribute!=null&&attribute.getType()==2&&StringUtils.isBlank(strNewValueNameArr)) {
                        strNewValueNameArr = attributeValue.getName();
                    } else if (attribute!=null&&attribute.getType()==2){
                        strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                    }
                }

            }
        }
        return strNewValueNameArr;
    }

    /**
     * 根据模板ID查询符合条件的商品信息
     * @param productTemplateId
     * @return
     */
    public StorefrontProductDTO getStorefrontProductByTemplateId(String productTemplateId){

        return iMasterProductTemplateMapper.getStorefrontProductByTemplateId(productTemplateId);
    }

    /**
     * 根据模板ID查询符合条件的商品信息
     * @param productTemplateId
     * @return
     */
    public Unit getUnitInfoByTemplateId(String productTemplateId){
        DjBasicsProductTemplate djBasicsProductTemplate=iMasterProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        String unitId=null;
        if(djBasicsProductTemplate!=null){
            unitId=djBasicsProductTemplate.getUnitId();
            if(StringUtils.isNotBlank(djBasicsProductTemplate.getConvertUnit())){
                unitId=djBasicsProductTemplate.getConvertUnit();
            }
        }

        return iMasterUnitMapper.selectByPrimaryKey(unitId);
    }

}

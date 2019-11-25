package com.dangjia.acg.service.product;


import com.dangjia.acg.mapper.refund.IBillAttributeMapper;
import com.dangjia.acg.mapper.refund.IBillAttributeValueMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillProductTemplateService {

    @Autowired
    private IBillAttributeValueMapper iBillAttributeValueMapper;
    @Autowired
    private IBillAttributeMapper iBillAttributeMapper;

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
                AttributeValue attributeValue = iBillAttributeValueMapper.selectByPrimaryKey(valueId);
                if(attributeValue!=null&&StringUtils.isNotBlank(attributeValue.getName())){
                    Attribute attribute=iBillAttributeMapper.selectByPrimaryKey(attributeValue.getAttributeId());
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
}

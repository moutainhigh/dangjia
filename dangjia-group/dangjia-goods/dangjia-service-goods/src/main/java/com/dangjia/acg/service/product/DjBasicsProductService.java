package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.product.DjBasicsLabelMapper;
import com.dangjia.acg.mapper.product.DjBasicsLabelValueMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductLabelValMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.modle.product.DjBasicsLabel;
import com.dangjia.acg.modle.product.DjBasicsLabelValue;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.modle.product.DjBasicsProductLabelVal;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.store.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 产品逻辑处理层
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class DjBasicsProductService {

    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private DjBasicsLabelMapper djBasicsLabelMapper;
    @Autowired
    private DjBasicsLabelValueMapper djBasicsLabelValueMapper;
    @Autowired
    private DjBasicsProductLabelValMapper djBasicsProductLabelValMapper;

    /**
     * 查询商品信息
     * @param name
     * @return
     */
    public ServerResponse queryProductData(String name){
        Example example = new Example(DjBasicsProduct.class);
        if(!CommonUtil.isEmpty(name)){
            example.createCriteria().andLike(DjBasicsProduct.NAME, "%" + name + "%");
            List<DjBasicsProduct> list = djBasicsProductMapper.selectByExample(example);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", list);
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }


    /**
     * 查询商品标签
     * @param productId
     * @return
     */
    public ServerResponse queryProductLabels(String productId) {
        DjBasicsProduct djBasicsProduct = djBasicsProductMapper.selectByPrimaryKey(productId);
        List<DjBasicsLabelDTO> djBasicsLabelDTOList=new ArrayList<>();
        Arrays.asList(djBasicsProduct.getLabelId().split(",")).forEach(str ->{
            DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(str);
            DjBasicsLabelDTO djBasicsLabelDTO=new DjBasicsLabelDTO();
            djBasicsLabelDTO.setId(djBasicsLabel.getId());
            djBasicsLabelDTO.setName(djBasicsLabel.getName());
            Example example=new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID,str)
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS,0);
            djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
            djBasicsLabelDTOList.add(djBasicsLabelDTO);
        });
        if(djBasicsLabelDTOList.size()<=0)
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        return ServerResponse.createBySuccess("查询成功",djBasicsLabelDTOList);
    }


    /**
     * 商品打标签
     * @return
     */
    public ServerResponse addLabelsValue(String jsonStr) {
        try {
            JSONObject villageObj = JSONObject.parseObject(jsonStr);
            String productId = villageObj.getString("productId");//商品id
            //遍历标签值对象 数组  ， 一个商品 对应 多个标签
            String productLabelValList = villageObj.getString("productLabelValList");
            JSONArray productLabelValArr = JSONArray.parseArray(productLabelValList);
            for (int i = 0; i < productLabelValArr.size(); i++) {//遍历户型
                JSONObject obj = productLabelValArr.getJSONObject(i);
                String productLabelValId = obj.getString("id");//商品标签值id
                String labelId = obj.getString("labelId");//标签id
                String labelValId = obj.getString("labelValId");//标签值id
                DjBasicsProductLabelVal djBasicsProductLabelVal;
                if (CommonUtil.isEmpty(productLabelValId)) {//没有id则新增
                    djBasicsProductLabelVal = new DjBasicsProductLabelVal();
                    djBasicsProductLabelVal.setProductId(productId);
                    djBasicsProductLabelVal.setDataStatus(0);
                    djBasicsProductLabelVal.setLabelId(labelId);
                    djBasicsProductLabelVal.setLabelValId(labelValId);
                    djBasicsProductLabelValMapper.insert(djBasicsProductLabelVal);
                } else {
                    djBasicsProductLabelVal = djBasicsProductLabelValMapper.selectByPrimaryKey(productLabelValId);
                    if (djBasicsProductLabelVal.getLabelId().equals(labelId) && djBasicsProductLabelVal.getLabelValId().equals(labelValId)) {
                        return ServerResponse.createByErrorMessage("商品标签值已存在");
                    }
                    djBasicsProductLabelVal.setLabelId(labelId);
                    djBasicsProductLabelVal.setLabelValId(labelValId);
                    djBasicsProductLabelValMapper.updateByPrimaryKeySelective(djBasicsProductLabelVal);
                }
            }
            //要删除商品标签值id数组，逗号分隔
            String[] deleteproductLabelValIds = villageObj.getString("deleteproductLabelValIds").split(",");
            for (String deleteproductLabelValId : deleteproductLabelValIds) {
                if (djBasicsProductLabelValMapper.selectByPrimaryKey(deleteproductLabelValId) != null) {
                    if (djBasicsProductLabelValMapper.deleteByPrimaryKey(deleteproductLabelValId) < 0)
                        return ServerResponse.createByErrorMessage("删除id：" + deleteproductLabelValId + "失败");
                }
            }
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 查询单个商品
     * @param request
     * @param id
     * @return
     */
    public DjBasicsProduct queryProductDataByID(HttpServletRequest request, String id)
    {
        return djBasicsProductMapper.selectByPrimaryKey(id);
    }

}

package com.dangjia.acg.service.actuary;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.BudgetStageCostDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.mapper.product.IBasicsProductTemplateMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class BudgetMaterialService {

    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    @Autowired
    private ITechnologyMapper iTechnologyMapper;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private BudgetWorkerService budgetWorkerService;
    @Autowired
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;


    private static Logger LOG = LoggerFactory.getLogger(BudgetMaterialService.class);
    //房子精算总花费
    public BigDecimal getHouseBudgetTotalAmount(String houseId){
        return iBudgetMaterialMapper.getHouseBudgetTotalAmount(houseId);
    }
    //精算阶段花费统计
    public ServerResponse getHouseBudgetStageCost(String houseId, String workerTypeId) {
        try {
            List<BudgetStageCostDTO> mapList = iBudgetMaterialMapper.getHouseBudgetStageCost(houseId, workerTypeId);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询所有精算
    public ServerResponse getAllBudgetMaterial() {
        try {
            List<Map<String, Object>> mapList = iBudgetMaterialMapper.getBudgetMaterial();
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据HouseFlowId查询房子材料精算
    public ServerResponse queryBudgetMaterialByHouseFlowId(String houseFlowId) {
        try {
            Example example = new Example(BudgetMaterial.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlowId).andEqualTo("deleteState", 0);
            List<BudgetMaterial> budgetMaterialist = iBudgetMaterialMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", budgetMaterialist);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据houseId和workerTypeId查询房子材料精算
    public ServerResponse getAllBudgetMaterialById(String houseId, String workerTypeId) {
        try {
            List<Map<String, Object>> mapList = iBudgetMaterialMapper.getBudgetMaterialById(houseId, workerTypeId);
            LOG.info("getAllBudgetMaterialById houseId:" + houseId + " workerTypeId:" + workerTypeId + " size:" + mapList.size());
//            BudgetWorkerService.setGoods(mapList, iGoodsMapper, iProductMaper, iUnitMapper);
            budgetWorkerService.setGoods(mapList);
            for (Map<String, Object> obj : mapList) {
                String goodsId = obj.get("goodsId").toString();
                Goods goods = iGoodsMapper.queryById(goodsId);
                if (goods != null) {
                    Unit unit = iUnitMapper.selectByPrimaryKey(goods.getUnitId());
                    if (unit != null)
                        obj.put("goodsUnitName", unit.getName());
                    if (!CommonUtil.isEmpty(goods.getName()))
                        obj.put("goodsName", goods.getName());
                }

            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据Id查询到精算
    public ServerResponse getBudgetMaterialByMyId(String id) {
        try {
            BudgetMaterial budgetMaterial = iBudgetMaterialMapper.selectByPrimaryKey(id);
            return ServerResponse.createBySuccess("查询成功", budgetMaterial);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据ID删除精算
    public ServerResponse deleteById(String id) {
        try {
            iBudgetMaterialMapper.deleteById(id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    //根据类别Id查到所有所属商品goods
    public ServerResponse getAllGoodsByCategoryId(String categoryId) {
        try {
            List<Goods> mapList = iGoodsMapper.queryByCategoryId(categoryId);
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据商品Id查货品
    public ServerResponse getAllProductByGoodsId(String goodsId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<DjBasicsProductTemplate> pList = iBasicsProductTemplateMapper.queryByGoodsId(goodsId);

            List<Map<String, Object>> mapList = new ArrayList<>();
            for (DjBasicsProductTemplate p : pList) {
                if (p.getImage() == null) {
                    continue;
                }

                List<Technology> pTechnologyList = iTechnologyMapper.queryTechnologyByWgId(p.getId());
                List<Map<String, Object>> tTechnologymMapList = new ArrayList<>();
                for (Technology t : pTechnologyList) {
                    if (t.getImage() == null) {
                        continue;
                    }
                    String[] imgArr = t.getImage().split(",");
                    StringBuilder imgStr = new StringBuilder();
                    StringBuilder imgUrlStr = new StringBuilder();
                    StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                    t.setImage(imgUrlStr.toString());
                    Map<String, Object> map = BeanUtils.beanToMap(t);
                    map.put("imageUrl", imgStr.toString());
                    map.put("sampleImageUrl", address + t.getSampleImage());
                    tTechnologymMapList.add(map);
                }


                String[] imgArr = p.getImage().split(",");
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                p.setImage(imgStr.toString());
                Map<String, Object> map = BeanUtils.beanToMap(p);
                map.put("imageUrl", imgUrlStr.toString());
                if (!StringUtils.isNotBlank(p.getLabelId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");

                } else {
                    map.put("labelId", p.getLabelId());
                    Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                    if (label.getName() != null)
                        map.put("labelName", label.getName());
                }

                String strNewValueNameArr = "";
                if (StringUtils.isNotBlank(p.getValueIdArr())) {
                    String[] newValueNameArr = p.getValueIdArr().split(",");
                    for (int i = 0; i < newValueNameArr.length; i++) {
                        String valueId = newValueNameArr[i];
                        if (StringUtils.isNotBlank(valueId)) {
                            AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
                            if (i == 0) {
                                strNewValueNameArr = attributeValue.getName();
                            } else {
                                strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                            }
                        }
                    }
                }
                map.put("newValueNameArr", strNewValueNameArr);

                map.put("tTechnologymMapList", tTechnologymMapList);
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}

package com.dangjia.acg.service.actuary;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.WorkTypeEnums;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ExportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.export.actuary.TActuaryGoods;
import com.dangjia.acg.export.actuary.TActuaryGoodsTotal;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.ILabelMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
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
    private IBudgetWorkerMapper iBudgetWorkerMapper;

    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private ConfigUtil configUtil;

    private static Logger LOG = LoggerFactory.getLogger(BudgetMaterialService.class);

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
            List<Product> pList = iProductMapper.queryByGoodsId(goodsId);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Product p : pList) {
                if (p.getImage() == null) {
                    continue;
                }
                String[] imgArr = p.getImage().split(",");
                String imgStr = "";
                String imgUrlStr = "";
                for (int i = 0; i < imgArr.length; i++) {
                    if (i == imgArr.length - 1) {
                        imgStr += address + imgArr[i];
                        imgUrlStr += imgArr[i];
                    } else {
                        imgStr += address + imgArr[i] + ",";
                        imgUrlStr += imgArr[i] + ",";
                    }
                }
                p.setImage(imgStr);
                Map<String, Object> map = CommonUtil.beanToMap(p);
                map.put("imageUrl", imgUrlStr);
                if (!StringUtils.isNotBlank(p.getLabelId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");
                } else {
                    map.put("labelId", p.getLabelId());
                    Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                    if (label.getName() != null)
                        map.put("labelName", label.getName());
                }
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}

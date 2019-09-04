package com.dangjia.acg.service.actuary;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ExportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.FlowActuaryDTO;
import com.dangjia.acg.dto.actuary.FlowDTO;
import com.dangjia.acg.export.actuary.TActuaryGoods;
import com.dangjia.acg.export.actuary.TActuaryGoodsTotal;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.core.WorkerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/12 0012
 * Time: 19:01
 */
@Service
public class HouseDataService {
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    @Autowired
    private IBudgetWorkerMapper iBudgetWorkerMapper;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;

    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private ConfigUtil configUtil;

    private static Logger LOG = LoggerFactory.getLogger(HouseDataService.class);

    /**
     * 自购清单
     */
    public ServerResponse selfBuyingList(String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String, String>> flowList = getForBudgetAPI.getFlowList(houseId);
            List<FlowDTO> returnList = new ArrayList<>();
            for (Map<String, String> flowMap : flowList) {
                String name = flowMap.get("name");
                String workerTypeId = flowMap.get("workerTypeId");
                //自购
                Example example = new Example(BudgetMaterial.class);
                example.createCriteria().andEqualTo(BudgetMaterial.WORKER_TYPE_ID, workerTypeId).andEqualTo(BudgetMaterial.HOUSE_ID, houseId)
                        .andEqualTo(BudgetMaterial.STETA, 2).andNotEqualTo(BudgetMaterial.DELETE_STATE, 1);
                List<BudgetMaterial> budgetMaterialList = iBudgetMaterialMapper.selectByExample(example);

                if (budgetMaterialList.size() > 0) {
                    FlowDTO flowDTO = new FlowDTO();
                    flowDTO.setName(name);
                    List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<>();
                    for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                        FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                        flowActuaryDTO.setImage(address + "icon/zigou.png");
                        flowActuaryDTO.setBuy(budgetMaterial.getProductType());//0:材料；1：包工包料
                        flowActuaryDTO.setName(budgetMaterial.getGoodsName());
                        flowActuaryDTO.setShopCount(budgetMaterial.getShopCount());
                        flowActuaryDTO.setConvertCount(budgetMaterial.getConvertCount());
                        flowActuaryDTO.setUnitName(budgetMaterial.getUnitName());//单位
                        flowActuaryDTOList.add(flowActuaryDTO);
                    }
                    flowDTO.setFlowActuaryDTOList(flowActuaryDTOList);
                    returnList.add(flowDTO);
                }
            }

            return ServerResponse.createBySuccess("查询成功", returnList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 导出指定房子的所有精算商品数据
     *
     * @param houseId
     * @return
     */
    public ServerResponse exportActuaryTotal(HttpServletResponse response, String houseId) {
        try {
            LOG.info("exportActuaryTotal :" + houseId);
            List<TActuaryGoods> tActuaryGoodsList = new ArrayList<>();//商品基础数据结果集
            List<TActuaryGoodsTotal> tActuaryGoodsTotalList = new ArrayList<>();//商品汇总数据结果集
            Map<String, TActuaryGoodsTotal> mapsTotal = new HashMap<>();//存放 统计结果数据
            ServerResponse serverResponse = workerTypeAPI.getWorkerTypeList(-1);
            JSONArray jsonArray = null;
            if (serverResponse.isSuccess()) {
                jsonArray = (JSONArray) serverResponse.getResultObj();
            }
            if (jsonArray != null) {
                /**
                 *  // 工匠数据库对应id： 3: 大管家 ，4：拆除 ，5：防水（弃用）  ，6：水电 ，7：泥工 ，8：木工 ，9：油漆
                 *  // 一维： 0: 大管家 ，1：拆除 ，2：  ，3：水电 ，4：泥工 ，5：木工 ，6：油漆
                 *  //二维： 0：材料 ，1，包工包料，2，人工
                 *   例如： [0][0]: 表示 大管家->材料
                 *   例如： [0][1]: 表示 大管家->包工包料
                 */
                for (Object aJsonArray : jsonArray) { //遍历每个工序
                    for (int j = 0; j < 3; ++j) {
                        JSONObject workerType = (JSONObject) aJsonArray;
                        if (workerType.getInteger(WorkerType.TYPE) < 3) {
                            continue;
                        }
                        TActuaryGoodsTotal tActuaryGoodsTotal = new TActuaryGoodsTotal();
                        tActuaryGoodsTotal.setName(workerType.getString(WorkerType.NAME));
                        tActuaryGoodsTotal.setPriceTotal(0.0);
                        if (j == 0)
                            tActuaryGoodsTotal.setGoodsType("材料");
                        if (j == 1)
                            tActuaryGoodsTotal.setGoodsType("包工包料");
                        if (j == 2)
                            tActuaryGoodsTotal.setGoodsType("人工");
                        mapsTotal.put(workerType.getString(WorkerType.ID) + "-" + j, tActuaryGoodsTotal);
                    }
                    JSONObject workerType = (JSONObject) aJsonArray;  // 3: 大管家 ，4：拆除 ，5：  ，6：水电 ，7：泥工 ，8：木工 ，9：油漆
                    if (workerType.getInteger(WorkerType.TYPE) < 3) {
                        continue;
                    }
                    //根据houseId和workerTypeId查询房子材料精算
                    List<BudgetMaterial> materialMapList = iBudgetMaterialMapper.getBudgetMaterialByHouseIdAndWorkerTypeId(houseId, workerType.getString(WorkerType.ID));
                    for (BudgetMaterial material : materialMapList) {
                        if (CommonUtil.isEmpty(material.getProductName()))
                            continue;
                        Goods goods = iGoodsMapper.selectByPrimaryKey(material.getGoodsId());
                        if (goods == null) continue;
                        TActuaryGoods tActuaryGoods = new TActuaryGoods();
                        tActuaryGoods.setName(workerType.getString(WorkerType.NAME));
                        tActuaryGoods.setShopNum(material.getShopCount());

                        Product product = iProductMapper.selectByPrimaryKey(material.getProductId());
                        //                    购买性质0：必买；1可选；2自购
                        if (goods.getBuy() != 2) {
                            tActuaryGoods.setProductSn(product.getProductSn());
                            tActuaryGoods.setGoodsUnitName(product.getUnitName());
                            tActuaryGoods.setConvertQuality(product.getConvertQuality());
                            tActuaryGoods.setUnit(iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName());
                        }

                        //用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次购买
                        switch (material.getDeleteState()) {
                            case 0:
                                tActuaryGoods.setDeleteState("未支付");
                                break;
                            case 1:
                                tActuaryGoods.setDeleteState("已删除");
                                continue;//不显示 已删除的
                            case 2:
                                tActuaryGoods.setDeleteState("业主取消");
                                break;
                            case 3:
                                tActuaryGoods.setDeleteState("已支付");
                                break;
                            case 4:
                                tActuaryGoods.setDeleteState("再次购买");
                                break;
                        }
                        if (0 == goods.getType())//0:材料；1：包工包料
                            tActuaryGoods.setGoodsType("材料");//商品类型 : 材料，包工包料，人工，
                        else if (1 == goods.getType())
                            tActuaryGoods.setGoodsType("包工包料");//商品类型 : 材料，包工包料，人工，
                        TActuaryGoodsTotal total = mapsTotal.get(workerType.getString(WorkerType.ID) + "-" + goods.getType());
                        if (goods.getBuy() == 2) //自购
                        {
                            tActuaryGoods.setProductName("自购商品:" + product.getName());
                            tActuaryGoods.setProductNum(0d);
                            tActuaryGoods.setPrice(0.0);
                            tActuaryGoods.setPriceTotal(0.0);
                            tActuaryGoods.setUnit("自购商品单位:" + material.getUnitName());
                        } else {
                            tActuaryGoods.setProductName(product.getName());
                            //                        tActuaryGoods.setProductNum(materialMap.get("shopCount").toString());
                            tActuaryGoods.setProductNum(material.getConvertCount());
                            tActuaryGoods.setPrice(material.getPrice());
                            tActuaryGoods.setPriceTotal(material.getTotalPrice());
                            tActuaryGoods.setUnit(material.getUnitName());

                            total.setPriceTotal(total.getPriceTotal() + tActuaryGoods.getPriceTotal());
                        }
                        tActuaryGoodsList.add(tActuaryGoods);
                    }

                    //根据houseId和wokerTypeId查询房子人工精算
                    //                List<Map<String, Object>> workerMapList = iBudgetWorkerMapper.getBudgetWorkerById(houseId, workerTypeId);
                    List<BudgetWorker> workerMapList = iBudgetWorkerMapper.getBudgetWorkerByHouseIdAndWorkerTypeId(houseId, workerType.getString(WorkerType.ID));
                    for (BudgetWorker worker : workerMapList) {
                        TActuaryGoods tActuaryGoods = new TActuaryGoods();
                        tActuaryGoods.setName(workerType.getString(WorkerType.NAME));

                        //用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次购买
                        switch (worker.getDeleteState()) {
                            case 0:
                                tActuaryGoods.setDeleteState("未支付");
                                break;
                            case 1:
                                tActuaryGoods.setDeleteState("已删除");
                                continue;//不显示 已删除的
                            case 2:
                                tActuaryGoods.setDeleteState("业主取消");
                                break;
                            case 3:
                                tActuaryGoods.setDeleteState("已支付");
                                break;
                        }

                        tActuaryGoods.setGoodsType("人工");//商品类型 : 人工，材料，包工包料
                        if (CommonUtil.isEmpty(worker.getName()))
                            continue;
                        tActuaryGoods.setProductName(worker.getName());
                        tActuaryGoods.setProductNum(worker.getShopCount());
                        tActuaryGoods.setShopNum(worker.getShopCount());
                        tActuaryGoods.setGoodsUnitName(worker.getUnitName());
                        tActuaryGoods.setPrice(worker.getPrice());
                        tActuaryGoods.setPriceTotal(worker.getTotalPrice());
                        tActuaryGoods.setUnit(worker.getUnitName());
                        tActuaryGoods.setProductSn(worker.getWorkerGoodsSn());
                        tActuaryGoodsList.add(tActuaryGoods);

                        TActuaryGoodsTotal total = mapsTotal.get(workerType.getString(WorkerType.ID) + "-2");
                        total.setPriceTotal(total.getPriceTotal() + tActuaryGoods.getPriceTotal());
                    }
                }
            }
            for (Map.Entry<String, TActuaryGoodsTotal> entry : mapsTotal.entrySet())
                tActuaryGoodsTotalList.add(entry.getValue());
            ExportExcel exportExcel = new ExportExcel();//创建表格实例
            exportExcel.setDataList("精算价格单", TActuaryGoods.class, tActuaryGoodsList);
            exportExcel.setDataList("精算价格汇总", TActuaryGoodsTotal.class, tActuaryGoodsTotalList);
            exportExcel.write(response, houseId + ".xlsx");
            return ServerResponse.createBySuccessMessage("导出Excel成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
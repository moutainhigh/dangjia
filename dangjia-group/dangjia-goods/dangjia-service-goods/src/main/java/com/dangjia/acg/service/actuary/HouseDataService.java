package com.dangjia.acg.service.actuary;

import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.WorkTypeEnums;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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
                        flowActuaryDTO.setBuy(budgetMaterial.getProductType());//0:材料；1：服务
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

            Map<Integer, TActuaryGoodsTotal> mapsTotal = new HashMap<>();//存放 统计结果数据
            /**
             *  // 工匠数据库对应id： 3: 大管家 ，4：拆除 ，5：防水（弃用）  ，6：水电 ，7：泥工 ，8：木工 ，9：油漆
             *  // 一维： 0: 大管家 ，1：拆除 ，2：  ，3：水电 ，4：泥工 ，5：木工 ，6：油漆
             *  //二维： 0：材料 ，1，服务，2，人工
             *   例如： [0][0]: 表示 大管家->材料
             *   例如： [0][1]: 表示 大管家->服务
             */
            int[][] keyArrs = new int[7][3];
            int indexCount = 0;
            for (int i = 0; i < keyArrs.length; ++i) {
                for (int j = 0; j < 3; ++j) {
                    keyArrs[i][j] = indexCount++;
//                    if(i == 5) //5：防水（弃用）
//                        continue;
                    TActuaryGoodsTotal tActuaryGoodsTotal = new TActuaryGoodsTotal();
                    tActuaryGoodsTotal.setName(WorkTypeEnums.getInstance(i + 3).getDesc() + "");
                    tActuaryGoodsTotal.setPriceTotal(0.0);
                    if (j == 0)
                        tActuaryGoodsTotal.setGoodsType("材料");
                    if (j == 1)
                        tActuaryGoodsTotal.setGoodsType("服务");
                    if (j == 2)
                        tActuaryGoodsTotal.setGoodsType("人工");
                    mapsTotal.put(keyArrs[i][j], tActuaryGoodsTotal);
                }
            }
//            for (int i = 0; i < keyArrs.length; i++) { //遍历二维数组，遍历出来的每一个元素是一个一维数组
//                for (int j = 0; j < keyArrs[i].length; j++) { //遍历对应位置上的一维数组
//                    LOG.info("keyArrs:" + keyArrs[i][j] + " i:" + i + "J:" + j);
//                }
//            }
            for (int i = 3; i < 10; i++) { //遍历每个工序
                String workerTypeId = i + "";  // 3: 大管家 ，4：拆除 ，5：  ，6：水电 ，7：泥工 ，8：木工 ，9：油漆
                //根据houseId和workerTypeId查询房子材料精算
//                List<Map<String, Object>> materialMapList = iBudgetMaterialMapper.getBudgetMaterialById(houseId, workerTypeId);
                List<BudgetMaterial> materialMapList = iBudgetMaterialMapper.getBudgetMaterialByHouseIdAndWorkerTypeId(houseId, workerTypeId);
                for (BudgetMaterial material : materialMapList) {
                    if (CommonUtil.isEmpty(material.getProductName()))
                        continue;
                    Goods goods = iGoodsMapper.selectByPrimaryKey(material.getGoodsId());
                    if (goods == null) continue;
                    TActuaryGoods tActuaryGoods = new TActuaryGoods();
                    String name = WorkTypeEnums.getInstance(Integer.parseInt(material.getWorkerTypeId())).getDesc();
                    tActuaryGoods.setName(name);
                    tActuaryGoods.setShopNum(material.getShopCount());
//                    购买性质0：必买；1可选；2自购
                    if (goods.getBuy() != 2) {
                        Product product = iProductMapper.selectByPrimaryKey(material.getProductId());
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

                    if (goods != null) {
                        if (0 == goods.getType())//0:材料；1：服务
                            tActuaryGoods.setGoodsType("材料");//商品类型 : 材料，服务，人工，
                        else if (1 == goods.getType())
                            tActuaryGoods.setGoodsType("服务");//商品类型 : 材料，服务，人工，
                    }

                    TActuaryGoodsTotal total = mapsTotal.get(keyArrs[i - 3][goods.getType()]);
                    if (goods.getBuy() == 2) //自购
                    {
                        tActuaryGoods.setProductName("自购商品:" + material.getProductName());
                        tActuaryGoods.setProductNum(0);
                        tActuaryGoods.setPrice(0.0);
                        tActuaryGoods.setPriceTotal(0.0);
                        tActuaryGoods.setUnit("自购商品单位:" + material.getUnitName());
                    } else {
                        tActuaryGoods.setProductName(material.getProductName());
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
                List<BudgetWorker> workerMapList = iBudgetWorkerMapper.getBudgetWorkerByHouseIdAndWorkerTypeId(houseId, workerTypeId);
                for (BudgetWorker worker : workerMapList) {
                    TActuaryGoods tActuaryGoods = new TActuaryGoods();
                    String name = WorkTypeEnums.getInstance(Integer.parseInt(worker.getWorkerTypeId())).getDesc();
                    tActuaryGoods.setName(name);

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

                    tActuaryGoods.setGoodsType("人工");//商品类型 : 人工，材料，服务
                    if (CommonUtil.isEmpty(worker.getName()))
                        continue;
                    tActuaryGoods.setProductName(worker.getName());
                    tActuaryGoods.setProductNum(worker.getShopCount().intValue());
                    tActuaryGoods.setPrice(worker.getPrice());
                    tActuaryGoods.setPriceTotal(worker.getTotalPrice());
                    tActuaryGoods.setUnit(worker.getUnitName());
                    tActuaryGoodsList.add(tActuaryGoods);

                    TActuaryGoodsTotal total = mapsTotal.get(keyArrs[i - 3][2]);
                    total.setPriceTotal(total.getPriceTotal() + tActuaryGoods.getPriceTotal());
                }
            }

            for (Map.Entry<Integer, TActuaryGoodsTotal> entry : mapsTotal.entrySet())
                tActuaryGoodsTotalList.add(entry.getValue());

            ExportExcel exportExcel = new ExportExcel();//创建表格实例
            exportExcel.setDataList("精算价格单", TActuaryGoods.class, tActuaryGoodsList);
            exportExcel.setDataList("精算价格汇总", TActuaryGoodsTotal.class, tActuaryGoodsTotalList);
            exportExcel.write(response, houseId + ".xlsx");
//            File file= ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX+"excel/template.xlsx");
//            exportExcel.writeFileDownload(response, file.getPath(),houseId + ".xlsx");//创建文件并输出
            return ServerResponse.createBySuccessMessage("导出Excel成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
package com.dangjia.acg.service.actuary;

import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.*;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:27
 */
@Service
public class ActuaryOperationService {

    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private IWorkerGoodsMapper workerGoodsMapper;
    @Autowired
    private IUnitMapper unitMapper;
    @Autowired
    private ITechnologyMapper technologyMapper;


    /**
     * 商品详情
     * gId:  workerGoodsId   productId
     */
    public ServerResponse getCommo(String gId, String cityId, int type){
        if (type == 1){//人工
            WorkerGoods workerGoods = workerGoodsMapper.selectByPrimaryKey(gId);//人工商品
            WGoodsDTO wGoodsDTO = new WGoodsDTO();
            wGoodsDTO.setImage(workerGoods.getImage());
            wGoodsDTO.setPrice("￥"+workerGoods.getPrice()+"/"+unitMapper.selectByPrimaryKey(workerGoods.getUnitId()).getName());
            wGoodsDTO.setWorkerDec(workerGoods.getWorkerDec());
            List<Technology> technologyList = technologyMapper.queryTechnologyByWgId(gId);
            wGoodsDTO.setTechnologyList(technologyList);
            return ServerResponse.createBySuccess("查询成功",wGoodsDTO);
        }else if(type == 2){//材料商品

        }else if(type == 3){//服务商品

        }
        return null;
    }

    /**
     * 查看工序 type 人工1 材料2 服务3
     */
    public ServerResponse confirmActuaryDetail(String userToken,String houseId,String workerTypeId,int type,String cityId){
        ServerResponse serverResponse =workerTypeAPI.getNameByWorkerTypeId(workerTypeId);
        String  workerTypeName = "";
        if(serverResponse.isSuccess()) {
            workerTypeName = serverResponse.getResultObj().toString();
        }else {
            return ServerResponse.createByErrorMessage("查询工序精算失败");
        }
        FlowDTO flowDTO = new FlowDTO();
        flowDTO.setName(workerTypeName);
        List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<FlowActuaryDTO>();
        if (type == 1){//查人工
            Example example = new Example(BudgetWorker.class);
            example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("workerTypeId", workerTypeId).andEqualTo("deleteState", 0);
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.selectByExample(example);
            for (BudgetWorker bw : budgetWorkerList){
                FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                flowActuaryDTO.setName(bw.getName());
                flowActuaryDTO.setTypeName("人工");
                flowActuaryDTO.setShopCount(bw.getShopCount());
                flowActuaryDTO.setUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                        +"commo?gId="+bw.getWorkerGoodsId()+"&cityId="+cityId+"&type="+1+"&title=人工商品详情");
                flowActuaryDTOList.add(flowActuaryDTO);
            }
        }else if(type == 2){//查材料商品
            Example example = new Example(BudgetMaterial.class);
            example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("workerTypeId", workerTypeId)
                    .andEqualTo("deleteState", 0).andEqualTo("product_type",0);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.selectByExample(example);
            for (BudgetMaterial bm : budgetMaterialList){
                FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                flowActuaryDTO.setName(bm.getGoodsName());
                flowActuaryDTO.setTypeName("材料");
                flowActuaryDTO.setShopCount(bm.getShopCount());
                flowActuaryDTO.setUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                        +"commo?gId="+bm.getProductId()+"&cityId="+cityId+"&type="+2+"&title=材料商品详情");
                flowActuaryDTOList.add(flowActuaryDTO);
            }
        }else if (type == 3){//查服务商品
            Example example = new Example(BudgetMaterial.class);
            example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("workerTypeId", workerTypeId)
                    .andEqualTo("deleteState", 0).andEqualTo("product_type",1);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.selectByExample(example);
            for (BudgetMaterial bm : budgetMaterialList){
                FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
                flowActuaryDTO.setName(bm.getGoodsName());
                flowActuaryDTO.setTypeName("服务");
                flowActuaryDTO.setShopCount(bm.getShopCount());
                flowActuaryDTO.setUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                        +"commo?gId="+bm.getProductId()+"&cityId="+cityId+"&type="+3+"&title=服务商品详情");
                flowActuaryDTOList.add(flowActuaryDTO);
            }
        }else {
            return ServerResponse.createByErrorMessage("type参数错误");
        }
        flowDTO.setFlowActuaryDTOList(flowActuaryDTOList);
        return ServerResponse.createBySuccess("查询成功",flowDTO);
    }

    /**
     * 精算详情 productType  0：材料；1：服务
     */
    public ServerResponse confirmActuary(String userToken,String houseId, String cityId){
        //从master获取工序详情
        List<Map<String,String>> mapList  = getForBudgetAPI.getFlowList(houseId);
        ActuaryDetailsDTO actuaryDetailsDTO = new ActuaryDetailsDTO();//最外层
        List<FlowDetailsDTO> flowDetailsDTOList = new ArrayList<FlowDetailsDTO>();

        for (Map<String,String> map : mapList){
            String name = map.get("name");
            String workerTypeId = map.get("workerTypeId");
            FlowDetailsDTO flowDetailsDTO = new FlowDetailsDTO();
            flowDetailsDTO.setName(name);
            List<DetailsDTO> detailsDTOList = new ArrayList<DetailsDTO>();//人工材料服务
            List<BudgetWorker> budgetWorkerList = budgetWorkerMapper.getBudgetWorkerList(houseId,workerTypeId);
            if(budgetWorkerList.size() > 0){
                DetailsDTO detailsDTO = new DetailsDTO();
                detailsDTO.setImage("");
                detailsDTO.setNameA("人工");
                detailsDTO.setNameB(name + "阶段人工");
                detailsDTO.setNameC("人工明细");
                detailsDTO.setUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                        +"confirmActuaryDetail?userToken="+userToken+"&houseId="+houseId+"&workerTypeId="+workerTypeId+"&cityId="+cityId+"&type="+1+"&title=人工明细");
                detailsDTOList.add(detailsDTO);
            }
            List<BudgetMaterial> materialCaiList = budgetMaterialMapper.getBudgetCaiList(houseId,workerTypeId);
            if(materialCaiList.size() > 0){
                DetailsDTO detailsDTO = new DetailsDTO();
                detailsDTO.setImage("");
                detailsDTO.setNameA("材料");
                detailsDTO.setNameB(name + "阶段材料");
                detailsDTO.setNameC("材料明细");
                detailsDTO.setUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                        +"confirmActuaryDetail?userToken="+userToken+"&houseId="+houseId+"&workerTypeId="+workerTypeId+"&cityId="+cityId+"&type="+2+"&title=材料明细");
                detailsDTOList.add(detailsDTO);
            }
            List<BudgetMaterial> materialSerList = budgetMaterialMapper.getBudgetSerList(houseId,workerTypeId);
            if (materialSerList.size() > 0){
                DetailsDTO detailsDTO = new DetailsDTO();
                detailsDTO.setImage("");
                detailsDTO.setNameA("服务");
                detailsDTO.setNameB(name + "阶段服务");
                detailsDTO.setNameC("服务明细");
                detailsDTO.setUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                        +"confirmActuaryDetail?userToken="+userToken+"&houseId="+houseId+"&workerTypeId="+workerTypeId+"&cityId="+cityId+"&type="+3+"&title=服务明细");
                detailsDTOList.add(detailsDTO);
            }
            flowDetailsDTO.setDetailsDTOList(detailsDTOList);
            flowDetailsDTOList.add(flowDetailsDTO);
        }
        actuaryDetailsDTO.setHouseId(houseId);
        actuaryDetailsDTO.setFlowDetailsDTOList(flowDetailsDTOList);
        return ServerResponse.createBySuccess("查询成功", actuaryDetailsDTO);
    }
}

package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import com.dangjia.acg.dto.basics.WorkerGoodsDTO;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IProductWorkerMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductWorkerMapper;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 工价商品Service实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/12 上午10:59
 */
@Service
public class WorkerGoodsService {
    private static Logger logger = LoggerFactory.getLogger(WorkerGoodsService.class);
    @Autowired
    private IProductWorkerMapper iWorkerGoodsMapper;
    @Autowired
    private ITechnologyMapper iTechnologyMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private DjBasicsProductWorkerMapper djBasicsProductWorkerMapper;
    /**
     * 每工种未删除 或 已支付工钱
     *
     * @param houseId
     * @param houseFlowId
     * @return
     */
    public ServerResponse getWorkertoCheck(String houseId, String houseFlowId) {
        try {
            Double totalPrice = iWorkerGoodsMapper.getWorkertoCheck(houseId, houseFlowId);
            JSONObject object = new JSONObject();
            object.put("totalPrice", totalPrice);
            return ServerResponse.createBySuccess("查询未删除或已支付的工钱成功", object);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询未删除或已支付的工钱失败");

        }

    }

    public WorkerGoodsDTO getWorkerGoodsDTO(String workerGoodsSn, String workerTypeId, String shopCount) {
//        Example example = new Example(WorkerGoods.class);
//        example.createCriteria()
//                .andEqualTo(WorkerGoods.DATA_STATUS, '0')
//                .andEqualTo(WorkerGoods.SHOW_GOODS, 1)
//                .andEqualTo(WorkerGoods.WORKER_GOODS_SN, workerGoodsSn)
//                .andEqualTo(WorkerGoods.WORKER_TYPE_ID, workerTypeId)
//        ;
//        List<WorkerGoods> workerGoods = iWorkerGoodsMapper.selectByExample(example);
        List<WorkerGoodsDTO> workerGoodsDTOS = djBasicsProductWorkerMapper.queryWorkerGoodsDTO(workerGoodsSn, workerTypeId);
        WorkerGoodsDTO workerGoodsDTO = new WorkerGoodsDTO();
        if (workerGoodsDTOS != null && workerGoodsDTOS.size() > 0) {
            workerGoodsDTO = workerGoodsDTOS.get(0);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            workerGoodsDTO.setImage(getImageAddress(address, workerGoodsDTO.getImage()));
            workerGoodsDTO.setImageUrl(workerGoodsDTO.getImage());
            workerGoodsDTO.setWorkerDec(getImageAddress(address, workerGoodsDTO.getWorkerDec()));
            workerGoodsDTO.setWorkerDecUrl(workerGoodsDTO.getWorkerDec());
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(workerGoodsDTO.getWorkerTypeId());
            if (response.isSuccess()) {
                workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
            }
            //将工艺列表返回
            List<TechnologyDTO> technologies = new ArrayList<>();
            List<Technology> technologyList = iTechnologyMapper.queryTechnologyList(workerGoodsDTO.getTechnologyIds());
            for (Technology technology : technologyList) {
                TechnologyDTO technologyResult = new TechnologyDTO();
                technologyResult.setId(technology.getId());
                technologyResult.setName(technology.getName());
                technologyResult.setWorkerTypeId(technology.getWorkerTypeId());
                technologyResult.setContent(technology.getContent());
                technologyResult.setImage(getImageAddress(address, technology.getImage()));
                technologyResult.setImageUrl(technology.getImage());
                technologyResult.setSampleImage(technology.getSampleImage());
                technologyResult.setSampleImageUrl(address + technology.getSampleImage());
                technologyResult.setType(technology.getType());

                technologyResult.setCreateDate(DateUtils.timedate(String.valueOf(technology.getCreateDate().getTime())));
                technologyResult.setModifyDate(DateUtils.timedate(String.valueOf(technology.getModifyDate().getTime())));
                technologies.add(technologyResult);
            }
            workerGoodsDTO.setTechnologies(technologies);
            workerGoodsDTO.setCreateDate(DateUtils.timedate(String.valueOf(workerGoodsDTO.getCreateDate())));
            workerGoodsDTO.setModifyDate(DateUtils.timedate(String.valueOf(workerGoodsDTO.getModifyDate())));
            workerGoodsDTO.setWorkerTypeName(workerTypeName);
            workerGoodsDTO.setShopCount(shopCount);
        } else {
            workerGoodsDTO.setWorkerGoodsSn(workerGoodsSn);
            workerGoodsDTO.setWorkerTypeId(workerTypeId);
            workerGoodsDTO.setMsg("找不到该人工商品（" + workerGoodsSn + "）,请检查是否创建或者停用！");
        }
        return workerGoodsDTO;
    }

    private String getImageAddress(String address, String image) {
        StringBuilder imgStr = new StringBuilder();
        if (!CommonUtil.isEmpty(image)) {
            String[] imgArr = image.split(",");
            for (int i = 0; i < imgArr.length; i++) {
                if (i == imgArr.length - 1) {
                    imgStr.append(address).append(imgArr[i]);
                } else {
                    imgStr.append(address).append(imgArr[i]).append(",");
                }
            }
        }
        return imgStr.toString();
    }
    /**
     * 从精算表查工种已支付工钱
     *
     * @param houseId
     * @param houseFlowId
     * @return
     */
    public ServerResponse getPayedWorker(String houseId, String houseFlowId) {
        try {
            Double totalPrice = iWorkerGoodsMapper.getPayedWorker(houseId, houseFlowId);
            JSONObject object = new JSONObject();
            object.put("totalPrice", totalPrice);
            return ServerResponse.createBySuccess("查询未删除或已支付的工钱成功", object);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询未删除或已支付的工钱失败");

        }
    }

    /**
     * 从精算表查代购商品支付工钱
     * @param houseId
     * @param houseFlowId
     * @return
     */
    public ServerResponse getAgencyPurchaseMoney(String houseId, String houseFlowId){
        try {
            Double totalAgencyPurchasePrice = iWorkerGoodsMapper.getAgencyPurchaseMoney(houseId, houseFlowId);
            JSONObject object = new JSONObject();
            object.put("totalAgencyPurchasePrice", totalAgencyPurchasePrice);
            return ServerResponse.createBySuccess("查询精算表查代购商品支付工钱成功", object);
        } catch (Exception e) {
           logger.error("查询精算表查代购商品支付工钱失败",e);
            return ServerResponse.createByErrorMessage("查询精算表查代购商品支付工钱失败");

        }
    }


    public ServerResponse getHomeProductList() {
        List<HomeProductDTO> homeProductDTOS = iWorkerGoodsMapper.getHomeProductList();
        if (homeProductDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (HomeProductDTO homeProductDTO : homeProductDTOS) {
            String imageUrl = homeProductDTO.getImage();
            homeProductDTO.setImage(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        return ServerResponse.createBySuccess("查询成功", homeProductDTOS);
    }

}

package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.repair.MasterMendWorkerAPI;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import com.dangjia.acg.dto.basics.WorkerGoodsDTO;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.util.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private IWorkerGoodsMapper iWorkerGoodsMapper;
    @Autowired
    private ITechnologyMapper iTechnologyMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private TechnologyService technologyService;
    @Autowired
    private IBudgetWorkerMapper iBudgetWorkerMapper;
    @Autowired
    private MasterMendWorkerAPI masterMendWorkerAPI;

    public ServerResponse<PageInfo> getWorkerGoodses(PageDTO pageDTO, String workerTypeId, String searchKey, String showGoods) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<WorkerGoods> productList = iWorkerGoodsMapper.selectList(StringUtils.isBlank(workerTypeId) ? null : workerTypeId,
                StringUtils.isBlank(searchKey) ? null : searchKey, StringUtils.isBlank(showGoods) ? null : showGoods);

        if (productList == null || productList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无工价商品");
        }
        List<WorkerGoodsDTO> workerGoodsResults = new ArrayList<>();

        for (WorkerGoods workerGoods : productList) {
            WorkerGoodsDTO workerGoodsResult = assembleWorkerGoodsResult(workerGoods);
            workerGoodsResults.add(workerGoodsResult);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(workerGoodsResults);
        return ServerResponse.createBySuccess("获取工价商品列表成功", pageResult);
    }

    public WorkerGoodsDTO getWorkerGoodsDTO(String workerGoodsSn, String workerTypeId, String shopCount) {
        Example example = new Example(WorkerGoods.class);
        example.createCriteria()
                .andEqualTo(WorkerGoods.DATA_STATUS, '0')
                .andEqualTo(WorkerGoods.SHOW_GOODS, 1)
                .andEqualTo(WorkerGoods.WORKER_GOODS_SN, workerGoodsSn)
                .andEqualTo(WorkerGoods.WORKER_TYPE_ID, workerTypeId)
        ;
        List<WorkerGoods> workerGoods = iWorkerGoodsMapper.selectByExample(example);
        WorkerGoodsDTO workerGoodsDTO = new WorkerGoodsDTO();
        if (workerGoods != null && workerGoods.size() > 0) {
            workerGoodsDTO = assembleWorkerGoodsResult(workerGoods.get(0));
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

    public WorkerGoodsDTO assembleWorkerGoodsResult(WorkerGoods workerGoods) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            WorkerGoodsDTO workerGoodsResult = new WorkerGoodsDTO();
            workerGoodsResult.setId(workerGoods.getId());
            workerGoodsResult.setName(workerGoods.getName());
            workerGoodsResult.setWorkerGoodsSn(workerGoods.getWorkerGoodsSn());
            workerGoodsResult.setImage(getImageAddress(address, workerGoods.getImage()));
            workerGoodsResult.setImageUrl(workerGoods.getImage());
            workerGoodsResult.setWorkerDec(getImageAddress(address, workerGoods.getWorkerDec()));
            workerGoodsResult.setWorkerDecUrl(workerGoods.getWorkerDec());
            workerGoodsResult.setUnitId(workerGoods.getUnitId());
            workerGoodsResult.setUnitName(workerGoods.getUnitName());
            workerGoodsResult.setOtherName(workerGoods.getOtherName());
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(workerGoods.getWorkerTypeId());
            if (response.isSuccess()) {
                workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
            }
            workerGoodsResult.setWorkerTypeName(workerTypeName);
            workerGoodsResult.setPrice(workerGoods.getPrice());
            workerGoodsResult.setIstop(workerGoods.getIstop());
            workerGoodsResult.setSales(workerGoods.getSales());
            workerGoodsResult.setWorkExplain(workerGoods.getWorkExplain());
            workerGoodsResult.setWorkerStandard(workerGoods.getWorkerStandard());
            workerGoodsResult.setWorkerTypeId(workerGoods.getWorkerTypeId());
            workerGoodsResult.setWorkerTypeName(workerTypeName);
            workerGoodsResult.setShowGoods(workerGoods.getShowGoods());

            workerGoodsResult.setLastPrice(workerGoods.getLastPrice());
            workerGoodsResult.setLastTime(workerGoods.getLastTime());
            workerGoodsResult.setTechnologyIds(workerGoods.getTechnologyIds());
            workerGoodsResult.setConsiderations(workerGoods.getConsiderations());
            workerGoodsResult.setCalculateContent(workerGoods.getCalculateContent());
            workerGoodsResult.setBuildContent(workerGoods.getBuildContent());

            //将工艺列表返回
            List<TechnologyDTO> technologies = new ArrayList<>();
            List<Technology> technologyList = iTechnologyMapper.queryTechnologyList(workerGoods.getTechnologyIds());
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
            workerGoodsResult.setTechnologies(technologies);
            workerGoodsResult.setCreateDate(DateUtils.timedate(String.valueOf(workerGoods.getCreateDate().getTime())));
            workerGoodsResult.setModifyDate(DateUtils.timedate(String.valueOf(workerGoods.getModifyDate().getTime())));
            return workerGoodsResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> setWorkerGoods(WorkerGoods workerGoods, String technologyJsonList, String deleteTechnologyIds) {
        if (workerGoods == null) {
            return ServerResponse.createByErrorMessage("请传入参数");
        }
        List<WorkerGoods> workerGoodsList = iWorkerGoodsMapper.selectByName(workerGoods.getName(), workerGoods.getWorkerTypeId());
        List<WorkerGoods> workerGoodsSnList = iWorkerGoodsMapper.selectByWorkerGoodsSn(workerGoods.getWorkerGoodsSn(), workerGoods.getWorkerTypeId());
        WorkerGoods workerG = iWorkerGoodsMapper.selectByPrimaryKey(workerGoods.getId());
        if (StringUtils.isNotBlank(workerGoods.getId()) && workerG != null) {
            WorkerGoods srcWorkerGoods = iWorkerGoodsMapper.selectByPrimaryKey(workerGoods.getId());
            if (!srcWorkerGoods.getName().equals(workerGoods.getName()))//要修改 商品名称
            {
                if (workerGoodsList.size() > 0)
                    return ServerResponse.createByErrorMessage("商品名称已存在");
            }
            if (!srcWorkerGoods.getWorkerGoodsSn().equals(workerGoods.getWorkerGoodsSn()))//要修改 商品标号
            {
                if (workerGoodsSnList.size() > 0)
                    return ServerResponse.createByErrorMessage("商品编号已存在");
            }
        } else {//新增
            if (workerGoodsList.size() > 0)
                return ServerResponse.createByErrorMessage("商品名称不能重复");
            if (workerGoodsSnList.size() > 0)
                return ServerResponse.createByErrorMessage("商品编号不能重复");

        }
//        String ret = technologyService.insertTechnologyList(technologyJsonList, workerGoods.getWorkerTypeId(), 1, workerGoods.getId());
//        if (!ret.equals("1"))  //如果不成功 ，弹出是错误提示
//            return ServerResponse.createByErrorMessage(ret);

        if (StringUtils.isNotBlank(workerGoods.getId()) && workerG != null) {
            workerGoods.setModifyDate(new Date());
            if (iWorkerGoodsMapper.updateByPrimaryKeySelective(workerGoods) < 0) {
                return ServerResponse.createByErrorMessage("更新工价商品失败");
            } else {
                //相关联表也更新
                iBudgetWorkerMapper.updateBudgetMaterialById(workerGoods.getId());
                Example example = new Example(WorkerGoods.class);
                example.createCriteria().andEqualTo(WorkerGoods.ID, workerGoods.getId());
                List<WorkerGoods> list = iWorkerGoodsMapper.selectByExample(example);
                masterMendWorkerAPI.updateMendWorker(JSON.toJSONString(list));
            }
        } else {
            workerGoods.setCreateDate(new Date());
            workerGoods.setModifyDate(new Date());
            if (iWorkerGoodsMapper.insert(workerGoods) < 0)
                return ServerResponse.createByErrorMessage("新增工价商品失败");
        }
        if (!CommonUtil.isEmpty(deleteTechnologyIds)) {
            String[] deleteTechnologyIdArr = deleteTechnologyIds.split(",");
            for (String aDeleteTechnologyIdArr : deleteTechnologyIdArr) {
                if (iTechnologyMapper.selectByPrimaryKey(aDeleteTechnologyIdArr) != null) {
                    if (iTechnologyMapper.deleteByPrimaryKey(aDeleteTechnologyIdArr) < 0)
                        return ServerResponse.createByErrorMessage("删除id：" + aDeleteTechnologyIdArr + "失败");
                }
            }
        }

        return ServerResponse.createBySuccessMessage("操作工价商品成功");
    }

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

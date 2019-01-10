package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.design.HouseDesignImageDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IDesignImageTypeMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:29
 */
@Service
public class HouseDesignImageService {
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private IDesignImageTypeMapper designImageTypeMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IWorkDepositMapper workDepositMapper;


    /**
     * 查看施工图
     */
    public ServerResponse designImageList(String houseId){
        try{
            if (StringUtil.isEmpty(houseId)){
                return ServerResponse.createByErrorMessage("houseId不能为空");
            }
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseDesignImage.HOUSE_ID, houseId);
            List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.selectByExample(example);
            List<HouseDesignImageDTO> imageDTOList = new ArrayList<HouseDesignImageDTO>();
            for (HouseDesignImage houseDesignImage : houseDesignImageList){
                DesignImageType designImageType = designImageTypeMapper.selectByPrimaryKey(houseDesignImage.getDesignImageTypeId());
                if(designImageType != null&& !CommonUtil.isEmpty(houseDesignImage.getImageurl())){
                    HouseDesignImageDTO houseDesignImageDTO = new HouseDesignImageDTO();
                    houseDesignImageDTO.setImageurl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class)+houseDesignImage.getImageurl());
                    houseDesignImageDTO.setName(designImageType.getName());
                    imageDTOList.add(houseDesignImageDTO);
                }
            }
            return ServerResponse.createBySuccess("查询成功",imageDTOList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 设计通过
     */
    public ServerResponse checkPass(String houseId,int type){
        House house = houseMapper.selectByPrimaryKey(houseId);
        Example examples = new Example(HouseFlow.class);
        examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE, "1");
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
        HouseWorkerOrder hwo =null;
        if(houseFlows.size()>0) {
            hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlows.get(0).getHouseId(),houseFlows.get(0).getWorkerTypeId());
        }
        if (house.getDesignerOk() == 5){
            if (type == 1){//通过
                house.setDesignerOk(7);
            }else if (type == 0){//不通过
                house.setDesignerOk(6);
            }
            houseMapper.updateByPrimaryKeySelective(house);

            //app推送和发送短信给工匠
            if (house.getDesignerOk()==6) {//通过
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "平面图已通过", String.format(DjConstants.PushMessage.PLANE_OK, house.getHouseName()), "");
                }
            }
            if (house.getDesignerOk()==6) {//不通过
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "平面图未通过", String.format(DjConstants.PushMessage.PLANE_ERROR, house.getHouseName()), "");
                }
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        }else if(house.getDesignerOk() == 2){
            if (type == 1){//通过
                house.setDesignerOk(3);
                /*
                 设计通过后生成精算的houseFlow
                 */
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("2");
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE_ID, workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                if(houseFlowList.size() > 0) {
                    return ServerResponse.createByErrorMessage("设计通过生成精算houseFlow异常");
                }else if(houseFlowList.size() == 0){
                    HouseFlow houseFlow = new HouseFlow(true);
                    houseFlow.setCityId(house.getCityId());
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setWorkType(3);//自动抢单待支付精算费
                    houseFlow.setWorkerId("2c911c24606f21720160726f5e6a00df");
                    //这里算出精算费
                    WorkDeposit workDeposit = workDepositMapper.selectAll().get(0);//结算比例表
                    houseFlow.setWorkPrice(house.getSquare().multiply(workDeposit.getBudgetCost()));
                    houseFlowMapper.insert(houseFlow);
                    HouseWorker houseWorker = new HouseWorker();
                    houseWorker.setHouseId(house.getId());
                    houseWorker.setWorkerId("2c911c24606f21720160726f5e6a00df");//当家精算
                    houseWorker.setWorkerTypeId(houseFlow.getWorkerTypeId());
                    houseWorker.setWorkerType(houseFlow.getWorkerType());
                    houseWorker.setWorkType(1);//已抢单
                    houseWorker.setIsSelect(1);
                    houseWorkerMapper.insert(houseWorker);
                }
            }else if (type == 0){//不通过
                house.setDesignerOk(8);
            }
            houseMapper.updateByPrimaryKeySelective(house);
            //app推送和发送短信给工匠
            if (house.getDesignerOk()==3) {//通过
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "施工图已通过", String.format(DjConstants.PushMessage.CONSTRUCTION_OK, house.getHouseName()), "");
                }
            }
            if (house.getDesignerOk()==8) {//不通过
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "施工图未通过", String.format(DjConstants.PushMessage.CONSTRUCTION_ERROR, house.getHouseName()), "");
                }
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        }else {
            return ServerResponse.createByErrorMessage("house参数错误,操作失败");
        }
    }

    /**
     * 审核设计图
     */
    public ServerResponse checkDesign(String houseId){
        Map<String, Object> map = new HashMap<String, Object>();

        House house = houseMapper.selectByPrimaryKey(houseId);
        List<HouseDesignImageDTO> houseDesignImageDTOList = new ArrayList<HouseDesignImageDTO>();
        List<HouseDesignImage> houseDesignImageList;
        HouseDesignImageDTO houseDesignImageDTO;
        Example example = new Example(HouseDesignImage.class);
        if(house.getDesignerOk() == 5){
            example.createCriteria().andEqualTo(HouseDesignImage.HOUSE_ID, houseId).andEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1");
            houseDesignImageList = houseDesignImageMapper.selectByExample(example);
            HouseDesignImage houseDesignImage = houseDesignImageList.get(0);
            houseDesignImageDTO = new HouseDesignImageDTO();
            houseDesignImageDTO.setImageurl(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class)+houseDesignImage.getImageurl());
            houseDesignImageDTO.setName("平面图");
            houseDesignImageDTOList.add(houseDesignImageDTO);
            map.put("button", "确认平面图");
            map.put("list", houseDesignImageDTOList);
            return ServerResponse.createBySuccess("查询成功",map);
        }
        if(house.getDesignerOk() == 2){
            example.createCriteria().andEqualTo("houseId", houseId).andNotEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID,"1")
                    .andIsNotNull(HouseDesignImage.IMAGEURL);
            houseDesignImageList = houseDesignImageMapper.selectByExample(example);
            for (HouseDesignImage houseDesignImage : houseDesignImageList){
                if(StringUtil.isNotEmpty(houseDesignImage.getImageurl())){
                    DesignImageType designImageType = designImageTypeMapper.selectByPrimaryKey(houseDesignImage.getDesignImageTypeId());
                    houseDesignImageDTO = new HouseDesignImageDTO();
                    houseDesignImageDTO.setImageurl(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class)+houseDesignImage.getImageurl());
                    houseDesignImageDTO.setName(designImageType.getName());
                    houseDesignImageDTO.setSell(designImageType.getSell());
                    houseDesignImageDTOList.add(houseDesignImageDTO);
                }
            }
            map.put("button", "确认设计图");
            map.put("list", houseDesignImageDTOList);
            return ServerResponse.createBySuccess("查询成功",map);
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }

    /**
     * 升级设计
     */
    public ServerResponse upgradeDesign(String userToken, String houseId, String designImageTypeId, int selected){
        try{
            if(selected == 0){//新增
                HouseDesignImage houseDesignImage = new HouseDesignImage();
                houseDesignImage.setHouseId(houseId);
                houseDesignImage.setDesignImageTypeId(designImageTypeId);
                houseDesignImage.setSell(1);
                houseDesignImageMapper.insert(houseDesignImage);
            }else {//删除
                Example example = new Example(HouseDesignImage.class);
                example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("designImageTypeId", designImageTypeId);
                houseDesignImageMapper.deleteByExample(example);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}

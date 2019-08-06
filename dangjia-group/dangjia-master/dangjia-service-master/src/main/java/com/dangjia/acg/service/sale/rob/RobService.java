package com.dangjia.acg.service.sale.rob;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.IntentionHouseDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDTO;
import com.dangjia.acg.dto.sale.rob.*;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.sale.royalty.IntentionHouseMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.clue.ClueTalk;
import com.dangjia.acg.modle.home.IntentionHouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 抢单模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/30
 * Time: 16:16
 */
@Service
public class RobService {
    @Autowired
    private ClueMapper  clueMapper;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ICustomerRecordMapper iCustomerRecordMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private ClueTalkMapper clueTalkMapper;
    @Autowired
    private IntentionHouseMapper intentionHouseMapper;

    /**
     * 查询抢单列表
     * @param userToken
     * @param storeId
     * @return
     */
    public ServerResponse queryRobSingledata(String userToken,String storeId){

        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        Integer type = iCustomerMapper.queryTypeId(accessToken.getUserId());

        Map<String,Object> map = new HashMap<>();

        map.put("userId",accessToken.getUserId());

        if (!CommonUtil.isEmpty(type)) {
            map.put("type",type);
        }
        if (!CommonUtil.isEmpty(storeId)) {
            map.put("storeId",storeId);
        }
        List<RobDTO> list = clueMapper.queryRobSingledata(map);

        //查询标签
        List<RobDTO> robDTOs = new ArrayList<>();
        for (RobDTO li:list) {
            RobDTO robDTO = new RobDTO();
            if (!CommonUtil.isEmpty(li.getLabelIdArr())) {
                String[] labelIds = li.getLabelIdArr().split(",");
                List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                robDTO.setList(labelByIds);
            }
            robDTO.setPhone(li.getPhone());
            robDTO.setOwerName(li.getOwerName());
            robDTO.setVisitState(li.getVisitState());
            robDTOs.add(robDTO);
        }
        if (robDTOs.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询提成列表", robDTOs);
    }



    public ServerResponse upDateIsRobStats(String id){
        try {
            if (!CommonUtil.isEmpty(id)) {
                Map<String,Object> map = new HashMap<>();
                map.put("id",id);
                clueMapper.upDateIsRobStats(map);
                return ServerResponse.createBySuccessMessage("修改成功");
            }
            return ServerResponse.createByErrorMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改成功");
        }

    }


    /**
     * 查询客户详情
     * @param memberId
     * @return
     */
    public ServerResponse queryCustomerInfo(String userToken,
                                            String memberId,
                                            String clueId,
                                            Integer phaseStatus,
                                            String stage){
        //获取图片url
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);


        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        //客户阶段查询客户详情
        if(phaseStatus == 1){
            Map<String,Object> map = new HashMap<>();
            if (!CommonUtil.isEmpty(memberId)) {
                map.put("memberId",memberId);
            }
            map.put("userId",accessToken.getUserId());
            map.put("stage",stage);
            RobArrInFoDTO robArrInFoDTO = new RobArrInFoDTO();

            List<RobInfoDTO> robInfoDTO = clueMapper.queryCustomerInfo(map);

            if(!CommonUtil.isEmpty(robInfoDTO)){
                //查询意向房子
                List<IntentionHouseDTO> intentionHouseList= intentionHouseMapper.queryIntentionHouse(robInfoDTO.get(0).getClueId());
                robArrInFoDTO.setIntentionHouseList(intentionHouseList);
            }


            if(!CommonUtil.isEmpty(robInfoDTO)){
                for (RobInfoDTO to:robInfoDTO) {
                    //查询大管家信息
                    if (!CommonUtil.isEmpty(to.getHouseId())) {
                        WorkerTypeDTO workerTypeDTO = iMemberLabelMapper.queryWorkerType(to.getHouseId());
                        if(null != workerTypeDTO){
                            workerTypeDTO.setHead(imageAddress + workerTypeDTO.getHead());
                        }
                        to.setWorkerTypeDTO(workerTypeDTO);
                    }
                }
            }

            //查询客户标签
            if(!CommonUtil.isEmpty(robInfoDTO)){
                String str = robInfoDTO.get(0).getLabelIdArr();
                if(null != str){
                    String[] labelIds = str.split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    robArrInFoDTO.setList(labelByIds);
                }
            }

            //查询客户沟通记录
            if (!CommonUtil.isEmpty(memberId)) {
                List<com.dangjia.acg.dto.member.CustomerRecordInFoDTO> data = iMemberLabelMapper.queryDescribes(memberId);
                for (com.dangjia.acg.dto.member.CustomerRecordInFoDTO datum : data) {
                    datum.setHead(imageAddress + datum.getHead());
                }
                robArrInFoDTO.setData(data);
            }

            //销售人员订单数量
            List<UserAchievementDTO> uadto = clueMapper.queryUserAchievementInFo(map);

            //全部提成数量
            int arrRoyalty = 1000;
            int s = 0;
            //每条数据当月提成
            if(!uadto.isEmpty()){
                for (UserAchievementDTO to:uadto) {
                    if(to.getVisitState() == 1){
                        s = (int) (arrRoyalty*0.75);
                        to.setMonthRoyaltys(s);
                        to.setMeterRoyaltys(s);
                    }
                    if(to.getVisitState() == 3){
                        s = (int) (arrRoyalty*0.25);
                        to.setMonthRoyaltys(s);
                        to.setMeterRoyaltys(arrRoyalty);
                    }
                    to.setHead(imageAddress + to.getHead());
                    to.setArrRoyalty(arrRoyalty);
                }
                robArrInFoDTO.setUserInFo(uadto.get(0));
            }


            robArrInFoDTO.setCustomerList(robInfoDTO);
            if (CommonUtil.isEmpty(robArrInFoDTO)) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询客户详情", robArrInFoDTO);
        }else{
            //线索阶段查询详情
            Map<String,Object> map = new HashMap<>();
            map.put("id",clueId);
            UserInfoDTO userInfoDTO = clueMapper.queryTips(map);

            if(!CommonUtil.isEmpty(userInfoDTO)){
                //查询意向房子
                List<IntentionHouseDTO> intentionHouseList= intentionHouseMapper.queryIntentionHouse(userInfoDTO.getClueId());
                userInfoDTO.setIntentionHouseList(intentionHouseList);
            }

            //查询线索阶段标签
            if(!CommonUtil.isEmpty(userInfoDTO)){
                if(!CommonUtil.isEmpty(userInfoDTO)){
                    String str = userInfoDTO.getLabelId();
                    if(null != str){
                        String[] labelIds = str.split(",");
                        List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                        userInfoDTO.setList(labelByIds);
                    }
                }
            }

            //查询线索沟通记录
            List<com.dangjia.acg.dto.member.CustomerRecordInFoDTO> data = iMemberLabelMapper.queryTalkContent(clueId);
            for (com.dangjia.acg.dto.member.CustomerRecordInFoDTO datum : data) {
                datum.setHead(imageAddress + datum.getHead());
            }
            userInfoDTO.setData(data);

            return ServerResponse.createBySuccess("查询客户详情", userInfoDTO);
        }

    }


    /**
     * 新增标签
     * @param memberId
     * @param labelId
     * @return
     */
    public ServerResponse addLabel(String memberId, String labelId,String clueId,Integer phaseStatus) {
        try {
            if(phaseStatus == 1) {
                //客户阶段新增标签
                Map<String,Object> Map = new HashMap<>();
                if(!CommonUtil.isEmpty(memberId)) {
                    String str = iCustomerMapper.queryLabelIdArr(memberId);
                    if(!CommonUtil.isEmpty(str)){
                        String[] strs = str.split(",");
                        List<String> strsToList= Arrays.asList(strs);
                        for(String s:strsToList){
                            if(s.equals(labelId)){
                                return ServerResponse.createBySuccessMessage("标签已存在");
                            }
                        }
                    }

                    String labelIdrr = str +","+ labelId;
                    Map.put("labelIdArr",labelIdrr);
                    Map.put("memberId",memberId);
                    iCustomerMapper.upDateLabelIdArr(Map);
                    return ServerResponse.createBySuccessMessage("新增成功");
                }
            }else {
                //线索阶段新增标签
                if (CommonUtil.isEmpty(clueId)) {
                    Map<String, Object> Map = new HashMap<>();
                        String str = iCustomerMapper.queryLabelId(clueId);
                        if (!CommonUtil.isEmpty(str)) {
                            String[] strs = str.split(",");
                            List<String> strsToList = Arrays.asList(strs);
                            for (String s : strsToList) {
                                if (s.equals(labelId)) {
                                    return ServerResponse.createBySuccessMessage("标签已存在 ");
                                }
                            }
                        }
                        String labelIdrr = str + "," + labelId;
                        Map.put("labelIdArr", labelIdrr);
                        Map.put("memberId", memberId);
                        iCustomerMapper.upDateLabelIdArr(Map);
                        return ServerResponse.createBySuccessMessage("新增成功");
                    }
            }

            return ServerResponse.createByErrorMessage("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 删除标签
     * @param memberId
     * @param labelIdArr
     * @return
     */
    public ServerResponse deleteLabel(String memberId, String labelIdArr,String clueId,Integer phaseStatus) {
        try {
            if (phaseStatus == 1) {
                //删除客户阶段标签
                Map<String,Object> Map = new HashMap<>();
                if (!CommonUtil.isEmpty(memberId)) {
                    Map.put("labelIdArr",labelIdArr);
                    Map.put("memberId",memberId);
                    iCustomerMapper.upDateLabelIdArr(Map);
                    return ServerResponse.createBySuccessMessage("删除成功");
                }
            }else{
                //删除线索阶段标签
                Map<String,Object> Map = new HashMap<>();
                if (!CommonUtil.isEmpty(memberId)) {
                    Map.put("labelId",labelIdArr);
                    Map.put("clueId",clueId);
                    iCustomerMapper.upDateLabelId(Map);
                    return ServerResponse.createBySuccessMessage("删除成功 ");
                }
            }

            return ServerResponse.createByErrorMessage("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 新增沟通记录
     * @param customerRecDTO
     * @return
     */
    public ServerResponse addDescribes(CustomerRecDTO customerRecDTO) {
        try {
            if (!CommonUtil.isEmpty(customerRecDTO)) {
                if(customerRecDTO.getPhaseStatus() == 1){
                    //客户阶段新增沟通记录
                    CustomerRecord customerRecord = new CustomerRecord();
                    customerRecord.setDescribes(customerRecDTO.getDescribes());
                    customerRecord.setRemindTime(customerRecDTO.getRemindTime());
                    customerRecord.setUserId(customerRecDTO.getUserId());
                    customerRecord.setMemberId(customerRecDTO.getMemberId());
                    iCustomerRecordMapper.insert(customerRecord);
                    return ServerResponse.createBySuccessMessage("新增成功");
                }else{
                    // 线索阶段新增沟通记录
                    ClueTalk clueTalk = new ClueTalk();
                    clueTalk.setUserId(customerRecDTO.getUserId());
                    clueTalk.setClueId(customerRecDTO.getClueId());
                    clueTalk.setTalkContent(customerRecDTO.getDescribes());
                    clueTalkMapper.insert(clueTalk);
                    return ServerResponse.createBySuccessMessage("新增成功");
                }
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }


    /**
     * 修改客户信息
     * @param clue
     * @return
     */
    public ServerResponse upDateCustomerInfo(Clue clue) {
        try {
            if (!CommonUtil.isEmpty(clue)) {
                clueMapper.updateByPrimaryKeySelective(clue);
                return ServerResponse.createBySuccessMessage("修改成功");
            }
            return ServerResponse.createByErrorMessage("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 添加意向房子
     * @param intentionHouse
     * @return
     */
    public ServerResponse addIntentionHouse(IntentionHouse intentionHouse) {
        try {
            if (!CommonUtil.isEmpty(intentionHouse)) {
                Example example=new Example(IntentionHouse.class);
                example.createCriteria().andEqualTo(IntentionHouse.RESIDENTIAL_NAME,intentionHouse.getResidentialName())
                        .andEqualTo(IntentionHouse.BUILDING_NAME,intentionHouse.getBuildingName())
                        .andEqualTo(IntentionHouse.NUMBER_NAME,intentionHouse.getNumberName());
                List<IntentionHouse> intentionHouses = intentionHouseMapper.selectByExample(example);
                if(intentionHouses.size()>0) {
                    return ServerResponse.createByErrorMessage("该意向房子已存在");
                }
                intentionHouseMapper.insert(intentionHouse);
                return ServerResponse.createBySuccessMessage("新增成功");
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }

    }


    /**
     * 删除意向房子
     * @param id
     * @return
     */
    public ServerResponse deleteIntentionHouse(String id) {
        try {
            if (!CommonUtil.isEmpty(id)) {
                intentionHouseMapper.deleteIntentionHouse(id);
                return ServerResponse.createBySuccessMessage("删除成功");
            }
            return ServerResponse.createByErrorMessage("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }

    }


}

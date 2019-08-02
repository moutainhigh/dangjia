package com.dangjia.acg.service.sale.rob;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDTO;
import com.dangjia.acg.dto.sale.rob.RobArrInFoDTO;
import com.dangjia.acg.dto.sale.rob.RobDTO;
import com.dangjia.acg.dto.sale.rob.RobInfoDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.member.CustomerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 查询抢单列表
     * @param userId
     * @param storeId
     * @return
     */
    public ServerResponse queryRobSingledata(String userId,String storeId){
        Integer type = iCustomerMapper.queryTypeId(userId);

        Map<String,Object> map = new HashMap<>();
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }
        if (!CommonUtil.isEmpty(type)) {
            map.put("type",type);
        }
        if (!CommonUtil.isEmpty(storeId)) {
            map.put("storeId",storeId);
        }
        List<RobDTO> list = clueMapper.queryRobSingledata(map);

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

    /**
     * 查询客户详情
     * @param memberId
     * @return
     */
    public ServerResponse queryCustomerInfo(String memberId,String userId){

        Map<String,Object> map = new HashMap<>();
        if (!CommonUtil.isEmpty(memberId)) {
            map.put("memberId",memberId);
        }
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }
        //获取图片url
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);

        RobArrInFoDTO robArrInFoDTO = new RobArrInFoDTO();

        List<RobInfoDTO> robInfoDTO = clueMapper.queryCustomerInfo(map);

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

        //查询标签
        if(!CommonUtil.isEmpty(robInfoDTO)){
            String str = robInfoDTO.get(0).getLabelIdArr();
            if(null != str){
                String[] labelIds = str.split(",");
                List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                robArrInFoDTO.setList(labelByIds);
            }
        }

        //根据客户id查询沟通记录
        if (!CommonUtil.isEmpty(memberId)) {
            List<CustomerRecordInFoDTO> data = iMemberLabelMapper.queryDescribes(memberId);
            for (CustomerRecordInFoDTO datum : data) {
                datum.setHead(imageAddress + datum.getHead());
            }
            robArrInFoDTO.setData(data);
        }

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
        }

        robArrInFoDTO.setUserInFo(uadto.get(0));
        robArrInFoDTO.setCustomerList(robInfoDTO);
        if (CommonUtil.isEmpty(robArrInFoDTO)) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询客户详情", robArrInFoDTO);
    }


    /**
     * 新增标签
     * @param memberId
     * @param labelId
     * @return
     */
    public ServerResponse addLabel(String memberId, String labelId) {
        try {
            Map<String,Object> Map = new HashMap<>();
            if (!CommonUtil.isEmpty(memberId)) {
                String str = iCustomerMapper.queryLabelIdArr(memberId);
                String labelIdrr = str +","+ labelId;
                Map.put("labelIdArr",labelIdrr);
                Map.put("memberId",memberId);
                iCustomerMapper.upDateLabelIdArr(Map);
                return ServerResponse.createBySuccessMessage("新增成功");
            }

            if (!CommonUtil.isEmpty(memberId)) {
                String str = iCustomerMapper.queryLabelIdArr(memberId);
                String[] arr = str.split(","); // 用,分割
                List<String> list = Arrays.asList(arr);

//                list.contains(labelId);
                for (String s:list) {
                    if(s.contains(labelId)){
                       list.remove(labelId);
                    }
                }



//                Map.put("labelIdArr",labelIdrr);
                Map.put("memberId",memberId);
                iCustomerMapper.upDateLabelIdArr(Map);
                return ServerResponse.createBySuccessMessage("新增成功");

            }



            return ServerResponse.createByErrorMessage("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 新增沟通记录
     * @param customerRecord
     * @return
     */
    public ServerResponse addDescribes(CustomerRecord customerRecord) {
        try {
            iCustomerRecordMapper.insert(customerRecord);
            return ServerResponse.createBySuccessMessage("新增成功");
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
            clueMapper.updateByPrimaryKeySelective(clue);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改成功");
        }
    }

}

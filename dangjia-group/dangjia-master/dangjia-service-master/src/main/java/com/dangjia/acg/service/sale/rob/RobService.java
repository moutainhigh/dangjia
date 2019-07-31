package com.dangjia.acg.service.sale.rob;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.sale.rob.RobDTO;
import com.dangjia.acg.dto.sale.rob.RobInfoDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ICityMapper iCityMapper;
    @Autowired
    private IModelingVillageMapper iModelingVillageMapper;

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
     * @param houseId
     * @return
     */
    public ServerResponse queryCustomerInfo(String houseId,String labelIdArr){

        Map<String,Object> map = new HashMap<>();
        if (!CommonUtil.isEmpty(houseId)) {
            map.put("houseId",houseId);
        }

        RobInfoDTO robInfoDTO = clueMapper.queryCustomerInfo(map);
        if(robInfoDTO != null){
            Map<String,Object> icityMap = new HashMap<>();
            icityMap.put("id",robInfoDTO.getCityId());
            String cityName = iCityMapper.queryCityName(icityMap);
            robInfoDTO.setCityName(cityName);

            Map<String,Object> villageMap = new HashMap<>();
            villageMap.put("id",robInfoDTO.getVillageId());
            String villageName = iModelingVillageMapper.queryVillageName(villageMap);
            robInfoDTO.setVillageName(villageName);
        }

        //查询标签
        if (!CommonUtil.isEmpty(labelIdArr)) {
            String[] labelIds = labelIdArr.split(",");
            List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
            robInfoDTO.setList(labelByIds);
        }



        if (robInfoDTO == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询提成列表", robInfoDTO);
    }


    public ServerResponse addLabel(String memberId, String labelId) {



//        return robService.addLabel(memberId,labelId);
        return null;
    }



}

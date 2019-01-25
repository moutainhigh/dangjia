package com.dangjia.acg.service.engineer;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.engineer.ArtisanDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:37
 * 工程部
 */
@Service
public class EngineerService {
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseMapper houseMapper;

    /**
     * 工地列表
     */
    public ServerResponse getHouseList(Integer pageNum, Integer pageSize) {
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;

        PageHelper.startPage(pageNum, pageSize);
        List<House> houseList = houseMapper.selectAll();
        PageInfo pageResult = new PageInfo(houseList);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (House house : houseList) {
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
            Map<String, Object> map = new HashMap<>();
            map.put("houseId", house.getId());
            map.put("address", house.getHouseName());
            map.put("memberName", member.getNickName() == null ? member.getName() : member.getNickName());
            map.put("mobile", member.getMobile());
            Member supervisor  = memberMapper.getSupervisor(house.getId());
            if(supervisor != null){
                map.put("supName",supervisor.getName());
                map.put("supMobile",supervisor.getMobile());
            }
            map.put("createDate",house.getCreateDate());
            map.put("visitState", house.getVisitState()); //0待确认开工,1装修中,2休眠中,3已完工
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询用户列表成功", pageResult);
    }

    /**
     * 工匠列表
     */
    public ServerResponse artisanList(Integer pageNum, Integer pageSize){
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;

        PageHelper.startPage(pageNum, pageSize);
        List<Member> memberList = memberMapper.artisanList();
        PageInfo pageResult = new PageInfo(memberList);
        List<ArtisanDTO> artisanDTOS = new ArrayList<>();
        for (Member member : memberList){
            ArtisanDTO artisanDTO = new ArtisanDTO();
            artisanDTO.setId(member.getId());
            artisanDTO.setName(member.getName());
            artisanDTO.setMobile(member.getMobile());
            artisanDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());
            artisanDTO.setCreateDate(member.getCreateDate());
            artisanDTO.setInviteNum(member.getInviteNum());
            artisanDTO.setCheckType(member.getCheckType());
            artisanDTO.setEvaluationScore(member.getEvaluationScore());
            artisanDTOS.add(artisanDTO);
        }
        pageResult.setList(artisanDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }


    /**
     * 查看工匠 工地详情
     */
    public ServerResponse lookWorker(String houseId){
        try{
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId);
            example.orderBy(HouseWorker.CREATE_DATE).desc();
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);

            return ServerResponse.createBySuccess("查询成功",houseWorkerList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 指定工匠
     */
    public ServerResponse setLockWorker(String houseFlowId,String workerId){
        try{

            return ServerResponse.createBySuccess("查询成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 更换工匠
     */
    public ServerResponse changeWorker(){
        try{

            return ServerResponse.createBySuccess("查询成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

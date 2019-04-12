package com.dangjia.acg.service.house;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseFlowApplyImage;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 杨帅
 */
@Service
@Slf4j
public class HouseApplyChangeOnYsService {

    private IHouseFlowMapper houseFlowMapper;
    private IWorkerTypeMapper workerTypeMapper;
    private ConfigUtil configUtil;
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    private IMemberMapper memberMapper;
    private IHouseWorkerMapper houseWorkerMapper;
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;

    @Autowired
    public HouseApplyChangeOnYsService(IHouseFlowMapper houseFlowMapper, IWorkerTypeMapper workerTypeMapper
            , ConfigUtil configUtil, IHouseFlowApplyMapper houseFlowApplyMapper, IMemberMapper memberMapper, IHouseWorkerMapper houseWorkerMapper
            , IHouseFlowApplyImageMapper houseFlowApplyImageMapper) {
        this.houseFlowMapper = houseFlowMapper;
        this.workerTypeMapper = workerTypeMapper;
        this.configUtil = configUtil;
        this.houseFlowApplyMapper = houseFlowApplyMapper;
        this.memberMapper = memberMapper;
        this.houseWorkerMapper = houseWorkerMapper;
        this.houseFlowApplyImageMapper = houseFlowApplyImageMapper;
    }


    /**
     * 施工记录
     * 原实现方法
     *
     * @version 1.31 冲刺
     * @see com.dangjia.acg.service.house.HouseService
     * 在原实现方法上添加了施工记录中展示的交底巡查验收三种属性的显示人为大管家
     * 且三种属性未被管家通过的情况下不会再施工记录显示.
     */
    public ServerResponse queryConstructionRecord(String houseId, Integer pageNum, Integer pageSize, String workerTypeId) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (houseId == null) {
            return ServerResponse.createByErrorMessage("请指定houseId");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.queryHfaByHouseId(houseId, workerTypeId);
        PageInfo pageResult = new PageInfo(hfaList);
        List<Map<String, Object>> listMap = this.houseFlowApplyDetail(hfaList);
        if (listMap == null) {
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("查询施工记录成功", pageResult);
    }


    /**
     * 施工记录
     * 添加补丁施工记录中展示的交底巡查验收三种属性的显示人为大管家
     *
     * @version 1.31 冲刺
     */
    private List<Map<String, Object>> houseFlowApplyDetail(List<HouseFlowApply> hfaList) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            for (HouseFlowApply hfa : hfaList) {
                // 如果为交底巡查验收三种属性就调用getHouseFlowApplyMapOnPatrol将显示人为大管家
                if (hfa.getApplyType() == 1 || hfa.getApplyType() == 2
                        || hfa.getApplyType() == 5 || hfa.getApplyType() == 6 || hfa.getApplyType() == 7) {
                    listMap.add(getHouseFlowApplyMapOnPatrol(hfa, address));
                } else {
                    listMap.add(getHouseFlowApplyMap(hfa, address));
                }
                // 原来的代码
//               listMap.add(getHouseFlowApplyMap(hfa, address));
            }
            return listMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将巡查，交底，验收的操作人改为大管家
     *
     * @param hfa     数据库查询出来的施工记录
     * @param address 图片源地址
     * @return 以map形式返回一条数据
     * @link getHouseFlowApplyMap
     */
    private Map<String, Object> getHouseFlowApplyMapOnPatrol(HouseFlowApply hfa, String address) {
        Map<Integer, String> applyTypeMap = new HashMap<>();
        applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "每日完工");
        applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "阶段完工");
        applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "整体完工");
        applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "停工申请");
        applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "每日开工");
        applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "巡查");
        applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "巡查");
        applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "巡查");
//        applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG_SUCCESS, "阶段完工审核");
//        applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG_SUCCESS, "整体完工审核");
//        applyTypeMap.put(DjConstants.ApplyType.NO_PASS, "审核未通过");
        Map<String, Object> map = new HashMap<>();
        map.put("id", hfa.getId());
        Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
        Member supervisor = houseFlowMapper.getMemberByHouseIdAndWorkerType(hfa.getHouseId(), 3);
        map.put("workerHead", address + supervisor.getHead());//工人头像
        map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
        map.put("workerName", supervisor.getName());//工人名称
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo("houseId", hfa.getHouseId()).andEqualTo("workerId", hfa.getWorkerId());
        List<HouseWorker> listHw = houseWorkerMapper.selectByExample(example);
        changeConstruction(map, listHw);
        map.put("content", hfa.getApplyDec());
        example = new Example(HouseFlowApplyImage.class);
        example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, hfa.getId());
        List<HouseFlowApplyImage> hfaiList = houseFlowApplyImageMapper.selectByExample(example);
        String[] imgArr = new String[hfaiList.size()];
        for (int i = 0; i < hfaiList.size(); i++) {
            HouseFlowApplyImage hfai = hfaiList.get(i);
            String string = hfai.getImageUrl();
            imgArr[i] = address + string;
        }
        map.put("imgArr", imgArr);
        map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
        map.put("createDate", hfa.getCreateDate().getTime());
        return map;
    }

    /**
     * 这个方法不会讲操作人改为大管家，还是原来的样式
     *
     * @param hfa     数据库查询出来的施工记录
     * @param address 图片源地址
     * @return 以map形式返回一条数据
     * @link getHouseFlowApplyMapOnPatrol
     */
    private Map<String, Object> getHouseFlowApplyMap(HouseFlowApply hfa, String address) {
        Map<Integer, String> applyTypeMap = new HashMap<>();
        applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "每日完工");
        applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "阶段完工");
        applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "整体完工");
        applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "停工申请");
        applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "每日开工");
        applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "巡查");
        applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "巡查");
        applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "巡查");
//        applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG_SUCCESS, "阶段完工审核");
//        applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG_SUCCESS, "整体完工审核");
//        applyTypeMap.put(DjConstants.ApplyType.NO_PASS, "审核未通过");
        Map<String, Object> map = new HashMap<>();
        map.put("id", hfa.getId());
        Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
        map.put("workerHead", address + member.getHead());//工人头像
        map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
        map.put("workerName", member.getName());//工人名称
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo("houseId", hfa.getHouseId()).andEqualTo("workerId", hfa.getWorkerId());
        List<HouseWorker> listHw = houseWorkerMapper.selectByExample(example);
        changeConstruction(map, listHw);
        map.put("content", hfa.getApplyDec());
        example = new Example(HouseFlowApplyImage.class);
        example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, hfa.getId());
        List<HouseFlowApplyImage> hfaiList = houseFlowApplyImageMapper.selectByExample(example);
        String[] imgArr = new String[hfaiList.size()];
        for (int i = 0; i < hfaiList.size(); i++) {
            HouseFlowApplyImage hfai = hfaiList.get(i);
            String string = hfai.getImageUrl();
            imgArr[i] = address + string;
        }
        map.put("imgArr", imgArr);
        map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
        map.put("createDate", hfa.getCreateDate().getTime());
        return map;
    }

    private static void changeConstruction(Map<String, Object> map, List<HouseWorker> listHw) {
        if (listHw.size() > 0) {
            HouseWorker houseWorker = listHw.get(0);
            if (houseWorker.getWorkType() == 4) {
                map.put("isNormal", "已更换");
            } else {
                map.put("isNormal", "正常施工");
            }
        } else {
            map.put("isNormal", "正常施工");
        }
    }


}

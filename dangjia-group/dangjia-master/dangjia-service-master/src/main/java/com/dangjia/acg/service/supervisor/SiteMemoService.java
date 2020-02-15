package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.supervisor.ISiteMemoMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.supervisor.SiteMemo;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.util.*;

@Service
public class SiteMemoService {
    @Autowired
    private ISiteMemoMapper iSiteMemoMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private ConfigMessageService configMessageService;

    /**
     * 添加备忘录/周报
     *
     * @param userToken       userToken
     * @param houseId         房子ID
     * @param type            0=普通,1=周计划
     * @param remark          备注
     * @param remindMemberIds 被提醒人ID，","分割
     * @param reminderTime    指定时间提醒我
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addSiteMemo(String userToken, String houseId, Integer type, String remark,
                                      String remindMemberIds, String reminderTime) {
        if (CommonUtil.isEmpty(type) || CommonUtil.isEmpty(remark)) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        SiteMemo siteMemo = new SiteMemo();
        siteMemo.setHouseId(houseId);
        siteMemo.setRemark(remark);
        siteMemo.setMemberId(worker.getId());
        if (worker.getId().equals(house.getMemberId())) {
            siteMemo.setWorkerType(0);
            siteMemo.setWorkerTypeName("业主");
        } else {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            if (workerType != null) {
                siteMemo.setWorkerType(workerType.getType());
                siteMemo.setWorkerTypeName(workerType.getName());
            } else {
                siteMemo.setWorkerType(0);
                siteMemo.setWorkerTypeName("");
            }
        }
        siteMemo.setType(type);
        siteMemo.setState(1);
        siteMemo.setRemindMemberId(worker.getId());
        try {
            Date date = DateUtil.parseDate(reminderTime);
            if (new Date().getTime() > date.getTime()) {
                siteMemo.setSendState(1);
            } else {
                siteMemo.setSendState(0);
            }
            siteMemo.setReminderTime(date);
        } catch (ParseException e) {
            siteMemo.setSendState(1);
            siteMemo.setReminderTime(null);
        }
        iSiteMemoMapper.insertSelective(siteMemo);
        String memoId = siteMemo.getId();
        if (!CommonUtil.isEmpty(remindMemberIds)) {//关联生成
            String[] memberIds = remindMemberIds.split(",");
            for (String memberId : memberIds) {
                if (worker.getId().equals(memberId)) {
                    continue;
                }
                siteMemo.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
                siteMemo.setSendState(1);
                siteMemo.setReminderTime(null);
                siteMemo.setState(0);
                siteMemo.setRemindMemberId(memberId);
                siteMemo.setMemoId(memoId);
                iSiteMemoMapper.insertSelective(siteMemo);
            }
        }
        return ServerResponse.createBySuccessMessage("增加成功");
    }

    /**
     * 备忘录全部已读
     *
     * @param userToken userToken
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setAllSiteMemo(String houseId,String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example =new Example(SiteMemo.class);
        example.createCriteria().andEqualTo(SiteMemo.REMIND_MEMBER_ID,worker.getId()).andEqualTo(SiteMemo.HOUSE_ID,houseId);
        SiteMemo siteMemo=new SiteMemo();
        siteMemo.setId(null);
        siteMemo.setCreateDate(null);
        siteMemo.setModifyDate(new Date());
        siteMemo.setState(1);
        iSiteMemoMapper.updateByExample(siteMemo,example);
        return ServerResponse.createBySuccessMessage("已读成功");
    }

    /**
     * 删除备忘录
     *
     * @param userToken userToken
     * @param memoId    备忘录ID
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteSiteMemo(String userToken, String memoId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        if (iSiteMemoMapper.deleteByPrimaryKey(memoId) > 0) {//关联删除
            Example example = new Example(SiteMemo.class);
            example.createCriteria().andEqualTo(SiteMemo.MEMO_ID, memoId);
            iSiteMemoMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
    }

    /**
     * 获取备忘录消息数量
     *
     * @param userToken userToken
     * @return ServerResponse
     */
    public ServerResponse getMemoMessage(String houseId,String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example = new Example(SiteMemo.class);
        example.createCriteria()
                .andEqualTo(SiteMemo.REMIND_MEMBER_ID, worker.getId())
                .andEqualTo(SiteMemo.HOUSE_ID,houseId)
                .andEqualTo(SiteMemo.STATE, 0);
        List<SiteMemo> siteMemoList = iSiteMemoMapper.selectByExample(example);
        if (siteMemoList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("size", siteMemoList.size());
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        Member member = memberMapper.selectByPrimaryKey(siteMemoList.get(0).getMemberId());
        if (member != null) {
            dataMap.put("memberImage", imageAddress + member.getHead());
        }
        return ServerResponse.createBySuccess("查询成功", dataMap);
    }

    /**
     * 获取备忘录消息列表/获取备忘录列表
     *
     * @param pageDTO   分页
     * @param userToken userToken
     * @param type      1查询未读
     * @return ServerResponse
     */
    public ServerResponse getMemoList(PageDTO pageDTO, String userToken,String houseId, int type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(SiteMemo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SiteMemo.REMIND_MEMBER_ID, worker.getId())
                .andEqualTo(SiteMemo.HOUSE_ID,houseId);
        if (type == 1) {
            criteria.andEqualTo(SiteMemo.STATE, 0);
        }
        List<SiteMemo> siteMemoList = iSiteMemoMapper.selectByExample(example);
        if (siteMemoList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(siteMemoList);
        List<Map<String, Object>> dataMaps = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (SiteMemo siteMemo : siteMemoList) {
            Map map = BeanUtils.beanToMap(siteMemo);
            if (siteMemo.getMemberId().equals(worker.getId())) {
                map.put("remind", 0);
            } else {
                map.put("remind", 1);
            }
            Member member = memberMapper.selectByPrimaryKey(siteMemo.getMemberId());
            if (member != null) {
                map.put("memberName", CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                map.put("memberImage", imageAddress + member.getHead());
                if(!CommonUtil.isEmpty(member.getWorkerTypeId())) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                    if (workerType != null) {
                        map.put("workerTypeColor", workerType.getColor());
                    }
                }
            }
            dataMaps.add(map);
        }
        pageResult.setList(dataMaps);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 获取备忘录详情
     *
     * @param userToken
     * @param memoId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse getSiteMemo(String userToken, String memoId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        SiteMemo siteMemo = iSiteMemoMapper.selectByPrimaryKey(memoId);
        if (siteMemo == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        Map map = BeanUtils.beanToMap(siteMemo);
        Member member = memberMapper.selectByPrimaryKey(siteMemo.getMemberId());
        if (member != null) {
            map.put("memberName", CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
        }
        if (siteMemo.getState() == 0) {
            siteMemo.setState(1);
            siteMemo.setModifyDate(new Date());
            iSiteMemoMapper.updateByPrimaryKeySelective(siteMemo);
        }
        return ServerResponse.createBySuccess("查询成功", map);
    }

    /**
     * 备忘录提醒
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse remindSiteMemo() {
        Example example = new Example(SiteMemo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SiteMemo.SEND_STATE, 0);
        criteria.andLessThanOrEqualTo(SiteMemo.REMINDER_TIME, new Date());
        List<SiteMemo> siteMemoList = iSiteMemoMapper.selectByExample(example);
        //发推送
        for (SiteMemo siteMemo : siteMemoList) {
            AppType appType;
            if (siteMemo.getWorkerType() == 0) {
                appType = AppType.ZHUANGXIU;
            } else {
                appType = AppType.GONGJIANG;
            }
            configMessageService.addConfigMessage(appType, siteMemo.getMemberId(), "备忘录提醒",
                    siteMemo.getRemark(), 201);
        }
        SiteMemo siteMemo = new SiteMemo();
        siteMemo.setId(null);
        siteMemo.setCreateDate(null);
        siteMemo.setSendState(1);
        iSiteMemoMapper.updateByExampleSelective(siteMemo, example);
        return ServerResponse.createBySuccessMessage("提醒成功");
    }

    /**
     * 获取当前房子参与人员（除开自己）
     *
     * @param userToken userToken
     * @param houseId   房子ID
     * @return ServerResponse
     */
    public ServerResponse getHouseMemberList(String userToken, String houseId) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (!worker.getId().equals(house.getMemberId())) {
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
            if (member != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("memberId", member.getId());
                map.put("memberName", CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                map.put("memberImage", imageAddress + member.getHead());
                map.put("workerTypeName", "业主");
                dataList.add(map);
            }
        }
        Example example = new Example(HouseFlow.class);
        example.createCriteria()
                .andEqualTo(HouseFlow.WORK_TYPE, 4)
                .andEqualTo(HouseFlow.HOUSE_ID, houseId);
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        for (HouseFlow houseFlow : houseFlows) {
            if (!CommonUtil.isEmpty(houseFlow.getWorkerId())
                    && !worker.getId().equals(houseFlow.getWorkerId())) {
                Member member = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
                if (member != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("memberId", member.getId());
                    map.put("memberName", CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                    map.put("memberImage", imageAddress + member.getHead());
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
                    if (workerType != null) {
                        map.put("workerTypeName", workerType.getName());
                    }
                    dataList.add(map);
                }
            }
        }
        return ServerResponse.createBySuccess("查询成功", dataList);
    }

}

package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.PatrolRecordDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.supervisor.IPatrolRecordMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishCorrelationMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.supervisor.PatrolRecord;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.RewardPunishCorrelation;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatrolRecordServices {

    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IPatrolRecordMapper iPatrolRecordMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private IRewardPunishCorrelationMapper rewardPunishCorrelationMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 督导添加巡查
     *
     * @param userToken userToken
     * @param houseId   房子ID
     * @param content   巡查内容
     * @param images    巡查图片","分割
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addPatrolRecord(String userToken, String houseId, String content, String images) {
        if (CommonUtil.isEmpty(content) || CommonUtil.isEmpty(images)) {
            return ServerResponse.createByErrorMessage("请传入巡查内容和图片");
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
        return addPatrolRecord(worker.getId(), houseId, content, images, 2, null);
    }

    /**
     * 添加督导巡查/奖罚记录
     */
    public ServerResponse addPatrolRecord(String memberId, String houseId, String content,
                                          String images, int type, String rewardPunishId) {
        PatrolRecord patrolRecord = new PatrolRecord();
        patrolRecord.setMemberId(memberId);
        patrolRecord.setHouseId(houseId);
        patrolRecord.setImages(images);
        patrolRecord.setContent(content);
        patrolRecord.setType(type);
        patrolRecord.setRewardPunishId(rewardPunishId);
        if (iPatrolRecordMapper.insertSelective(patrolRecord) > 0) {
            return ServerResponse.createBySuccessMessage("新增成功");
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    /**
     * 中台查询督导工作记录
     *
     * @param pageDTO   分页
     * @param type      -1:全部;0:奖励;1:处罚,2:巡查
     * @param searchKey 搜索值
     * @return ServerResponse
     */
    public ServerResponse getPatrolRecordList(PageDTO pageDTO, Integer type, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<PatrolRecordDTO> patrolRecordDTOS = iPatrolRecordMapper.getPatrolRecordList(type, searchKey);
        if (patrolRecordDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo<PatrolRecordDTO> pageResult = new PageInfo<>(patrolRecordDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * App查询督导工作记录
     *
     * @param userToken userToken
     * @param pageDTO   分页
     * @param type      2：巡查，0：奖罚
     * @return
     */
    public ServerResponse getAppPatrolRecordList(String userToken, PageDTO pageDTO, Integer type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<PatrolRecordDTO> patrolRecordDTOS = iPatrolRecordMapper.getAppPatrolRecordList(type, worker.getId());
        if (patrolRecordDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo<PatrolRecordDTO> pageResult = new PageInfo<>(patrolRecordDTOS);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (PatrolRecordDTO patrolRecordDTO : patrolRecordDTOS) {
            List<String> imageList = new ArrayList<>();
            if (patrolRecordDTO.getType() != 2) {
                RewardPunishRecord record = rewardPunishRecordMapper.selectByPrimaryKey(patrolRecordDTO.getRewardPunishId());
                StringBuilder content = new StringBuilder();
                if (patrolRecordDTO.getType() == 0) {
                    content.append("奖励");
                } else {
                    content.append("惩罚");
                }
                if (record != null) {
                    Member member = memberMapper.selectByPrimaryKey(record.getMemberId());
                    if (member != null) {
                        content.append(member.getName());
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                        if (workerType != null) {
                            patrolRecordDTO.setWorkerTypeType(workerType.getType());
                            content.append("-").append(workerType.getName());
                        }
                    }
                    if (!CommonUtil.isEmpty(record.getImages())) {
                        String[] images = record.getImages().split(",");
                        for (String image : images) {
                            imageList.add(imageAddress + image);
                        }
                    }
                }
                patrolRecordDTO.setContent(content.toString());
            } else if (!CommonUtil.isEmpty(patrolRecordDTO.getImages())) {
                String[] images = patrolRecordDTO.getImages().split(",");
                for (String image : images) {
                    imageList.add(imageAddress + image);
                }
            }
            patrolRecordDTO.setImageList(imageList);
        }
        pageResult.setList(patrolRecordDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 获取督导记录详情
     *
     * @param userToken      userToken
     * @param patrolRecordId 记录ID
     * @return ServerResponse
     */
    public ServerResponse getPatrolRecordDetails(String userToken, String patrolRecordId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        PatrolRecord patrolRecord = iPatrolRecordMapper.selectByPrimaryKey(patrolRecordId);
        if (patrolRecord == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        PatrolRecordDTO dto = new PatrolRecordDTO();
        dto.setPatrolRecordId(patrolRecord.getId());
        dto.setCreateDate(patrolRecord.getCreateDate());
        dto.setOperatorId(patrolRecord.getMemberId());
        dto.setHouseId(patrolRecord.getHouseId());
        dto.setType(patrolRecord.getType());
        Member worker = memberMapper.selectByPrimaryKey(patrolRecord.getMemberId());
        if (worker != null) {
            dto.setOperatorName(worker.getName());
            dto.setOperatorMobile(worker.getMobile());
        } else {
            MainUser user = userMapper.selectByPrimaryKey(patrolRecord.getMemberId());
            if (user != null) {
                dto.setOperatorName(user.getUsername());
                dto.setOperatorMobile(user.getMobile());
            }
        }
        //房子地址
        Example example = new Example(MemberAddress.class);
        example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID, patrolRecord.getHouseId());
        MemberAddress memberAddress = iMasterMemberAddressMapper.selectOneByExample(example);
        if (memberAddress != null) {
            dto.setHouseName(memberAddress.getAddress());
        } else {
            House house = iHouseMapper.selectByPrimaryKey(patrolRecord.getHouseId());
            if (house != null) {
                dto.setHouseName(house.getHouseName());
            }
        }
        List<String> imageList = new ArrayList<>();
        if (patrolRecord.getType() == 2) {//巡查
            if (!CommonUtil.isEmpty(patrolRecord.getImages())) {
                String[] images = patrolRecord.getImages().split(",");
                for (String image : images) {
                    imageList.add(imageAddress + image);
                }
            }
            dto.setContent(patrolRecord.getContent());
        } else {//奖罚
            RewardPunishRecord record = rewardPunishRecordMapper.selectByPrimaryKey(patrolRecord.getRewardPunishId());
            if (record != null) {
                dto.setContent(record.getRemarks());
                Member member = memberMapper.selectByPrimaryKey(record.getMemberId());
                if (member != null) {
                    dto.setMemberId(member.getId());
                    dto.setMemberName(member.getName());
                    dto.setMemberHead(imageAddress + member.getHead());
                    dto.setMemberMobile(member.getMobile());
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                    if (workerType != null) {
                        dto.setWorkerTypeType(workerType.getType());
                        dto.setWorkerTypeName(workerType.getName());
                    }
                }
                RewardPunishCorrelation rewardPunishCorrelation = rewardPunishCorrelationMapper.selectByPrimaryKey(record.getRewardPunishCorrelationId());
                if (rewardPunishCorrelation != null) {
                    dto.setRewardPunishCorrelation(rewardPunishCorrelation.getName());
                }
                if (!CommonUtil.isEmpty(record.getImages())) {
                    String[] images = record.getImages().split(",");
                    for (String image : images) {
                        imageList.add(imageAddress + image);
                    }
                }
            }
        }
        dto.setImageList(imageList);
        return ServerResponse.createBySuccess("查询成功", dto);
    }


}

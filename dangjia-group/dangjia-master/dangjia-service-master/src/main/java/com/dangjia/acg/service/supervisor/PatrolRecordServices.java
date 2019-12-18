package com.dangjia.acg.service.supervisor;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.JFRewardPunishRecordDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordIndexDTO;
import com.dangjia.acg.dto.supervisor.WorkerRewardPunishRecordDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.supervisor.DjBasicsPatrolRecordMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.supervisor.DjBasicsPatrolRecord;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class PatrolRecordServices {

    private static Logger logger = LoggerFactory.getLogger(PatrolRecordServices.class);
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private DjBasicsPatrolRecordMapper djBasicsPatrolRecordMapper ;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper ;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private MemberAPI memberAPI;
    /**
     * 督导首页
     * @param request
     * @return
     */
    public ServerResponse getSupHomePage(HttpServletRequest request,PageDTO pageDTO,String userToken,String keyWord) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member worker = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<PatrolRecordIndexDTO> list = rewardPunishRecordMapper.getSupHomePage(worker.getId(),keyWord);
            list.forEach(patrolRecordIndexDTO->{
                patrolRecordIndexDTO.setImage(imageAddress+patrolRecordIndexDTO.getImage());
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("获取督导首页异常", e);
            return ServerResponse.createByErrorMessage("获取督导首页异常");
        }
    }

    /**
     *新建巡检
     * @param request
     * @return
     */
    public ServerResponse addDjBasicsPatrolRecord(HttpServletRequest request, String userToken, String houseId, String images, String content) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            DjBasicsPatrolRecord djBasicsPatrolRecord = new DjBasicsPatrolRecord();
            djBasicsPatrolRecord.setContent(content);
            djBasicsPatrolRecord.setHouseId(houseId);
            djBasicsPatrolRecord.setImages(images);
            djBasicsPatrolRecord.setMemberId(worker.getId());
            int i=djBasicsPatrolRecordMapper.insertSelective(djBasicsPatrolRecord);
            if(i<=0)
                return ServerResponse.createByErrorMessage("新建巡检失败");
            return ServerResponse.createBySuccessMessage("新建巡检成功");
        } catch (Exception e) {
            logger.error("新建巡检异常", e);
            return ServerResponse.createByErrorMessage("新建巡检异常");
        }
    }


    /**
     *查询巡检记录
     * @param request
     * @return
     */
    public ServerResponse queryDjBasicsPatrolRecord(HttpServletRequest request, String userToken) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Example example=new Example(DjBasicsPatrolRecord.class);
            example.createCriteria().andEqualTo(DjBasicsPatrolRecord.MEMBER_ID,worker.getId());
            List<DjBasicsPatrolRecord> list=djBasicsPatrolRecordMapper.selectByExample(example);
            list.forEach(djBasicsPatrolRecord->{
                String imageAddress=StringTool.getImage(djBasicsPatrolRecord.getImages(),address);
                djBasicsPatrolRecord.setImages(imageAddress);
            });
            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            logger.error("查询巡检记录异常", e);
            return ServerResponse.createByErrorMessage("查询巡检记录异常");
        }
    }

    /**
     * 查询督导工作记录
     * @param request
     * @param keyWord
     * @return
     */
    public ServerResponse queryWorkerRewardPunishRecord(HttpServletRequest request, PageDTO pageDTO , String type , String keyWord) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WorkerRewardPunishRecordDTO> list = rewardPunishRecordMapper.queryRewardPunishRecordBykeyWord(keyWord,type);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询督导工作记录异常", e);
            return ServerResponse.createByErrorMessage("查询督导工作记录异常");
        }
    }

    /**
     *巡检详情
     * @param request
     * @param rewordPunishCorrelationId
     * @return
     */
    public ServerResponse queryPatrolRecordDetail(HttpServletRequest request, String rewordPunishCorrelationId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PatrolRecordDTO djBasicsPatrolRecord = djBasicsPatrolRecordMapper.queryPatrolRecordDetail(rewordPunishCorrelationId);
            if (djBasicsPatrolRecord!=null)
            {
                if(djBasicsPatrolRecord.getImages()!=null)
                {
                    String imageAddress=StringTool.getImage(djBasicsPatrolRecord.getImages(),address);
                    djBasicsPatrolRecord.setImages(imageAddress);
                    djBasicsPatrolRecord.setImagesDetail(imageAddress.split(","));
                }
            }
            return ServerResponse.createBySuccess("查询成功", djBasicsPatrolRecord);
        } catch (Exception e) {
            logger.error("巡检详情异常", e);
            return ServerResponse.createByErrorMessage("巡检详情异常");
        }
    }

    /**
     *奖励/惩罚详情
     * @param request
     * @param id
     * @return
     */
    public ServerResponse queryRewardPunishRecordDetail(HttpServletRequest request, String id) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            JFRewardPunishRecordDTO jFRewardPunishRecordDTO=  rewardPunishRecordMapper.queryRewardPunishRecordDetail(id);
            if (jFRewardPunishRecordDTO!=null)
            {
                jFRewardPunishRecordDTO.setWorkerTypeName(jFRewardPunishRecordDTO.getWorkerType()!=null?workerTypeMapper.selectByPrimaryKey(jFRewardPunishRecordDTO.getWorkerType()).getName():"");
                if(jFRewardPunishRecordDTO.getImages()!=null)
                {
                    String imageAddress=StringTool.getImage(jFRewardPunishRecordDTO.getImages(),address);
                    jFRewardPunishRecordDTO.setImages(imageAddress);
                    jFRewardPunishRecordDTO.setImagesDetail(imageAddress.split(","));
                }
            }
            return ServerResponse.createBySuccess("查询成功", jFRewardPunishRecordDTO);
        } catch (Exception e) {
            logger.error("奖励/惩罚详情异常", e);
            return ServerResponse.createByErrorMessage("奖励/惩罚详情异常");
        }
    }
}

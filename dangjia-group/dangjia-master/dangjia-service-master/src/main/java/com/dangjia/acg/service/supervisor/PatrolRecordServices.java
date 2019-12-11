package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.wechat.HttpClientUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.supervisor.DjBasicsPatrolRecordMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.supervisor.DjBasicsPatrolRecord;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
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
}

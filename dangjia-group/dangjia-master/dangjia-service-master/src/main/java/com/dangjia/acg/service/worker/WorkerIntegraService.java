package com.dangjia.acg.service.worker;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.worker.WorkerRunkDTO;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.model.DateRange;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工匠积分管理
 * qiyuxiang
 */
@Service
public class WorkerIntegraService {

    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private ConfigUtil configUtil;
    /**
     * 获取积分排行记录
     * @param type   0=排行榜 1=飙升榜
     * @param userToken
     * @return
     */
    public ServerResponse queryRankingIntegral(Integer type, String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        Member member = (Member) object;
        WorkerRunkDTO myWorkerRunk= null;
        Map map =new HashMap();
        //上个季度的时间范围
        DateRange lastQuarter = DateUtil.getLastQuarter();
        if(type==1) {
            List<WorkerRunkDTO> listnew=new ArrayList<>();
            //上个季度的时间范围
            List<WorkerRunkDTO> list = workIntegralMapper.querySoaringWorker(member.getWorkerType(),DateUtil.getDateString(lastQuarter.getStart().getTime()),DateUtil.getDateString(lastQuarter.getEnd().getTime()));
            //得到当前工匠所在的排行
            for (int i = 0; i < list.size(); i++) {
                WorkerRunkDTO workerRunkDTO= list.get(i);
                if(workerRunkDTO.getWorkerId().equals(member.getId())){
                    myWorkerRunk=workerRunkDTO;
                    break;
                }
            }
            if(myWorkerRunk==null){
                myWorkerRunk= new WorkerRunkDTO();
                myWorkerRunk.setWorkerId(member.getId());
                myWorkerRunk.setWorkerHead(imageAddress+member.getHead());
                myWorkerRunk.setWorkerName(member.getName());
                myWorkerRunk.setRankNo(-1);
                myWorkerRunk.setIntegral(new BigDecimal(0));
            }
            map.put("worker",myWorkerRunk);
            for (int i = 0; i < 20; i++) {
                WorkerRunkDTO  workerRunk=list.get(i);
                workerRunk.setWorkerHead(imageAddress+workerRunk.getWorkerHead());
                listnew.add(workerRunk);
            }
            map.put("list",listnew);
        }else{
            List<WorkerRunkDTO> list = workIntegralMapper.queryRankingWorker(member.getWorkerType());
            map.put("list",list);

            Example example=new Example(Member.class);

            if(member.getWorkerType()!=null&&member.getWorkerType()>3){
                example.createCriteria().andCondition(" (("+member.getEvaluationScore()+" < m.evaluation_score) OR ("+member.getEvaluationScore()+" = m.evaluation_score AND '"+DateUtil.getDateString(member.getCreateDate().getTime())+"' >= m.create_date)) AND m.worker_type > 3 ");
            }
            if(member.getWorkerType()!=null&&member.getWorkerType()==3){
                example.createCriteria().andCondition(" (("+member.getEvaluationScore()+" < m.evaluation_score) OR ("+member.getEvaluationScore()+" = m.evaluation_score AND '"+DateUtil.getDateString(member.getCreateDate().getTime())+"' >= m.create_date)) AND m.worker_type = 3 ");
            }
            Integer rankNo= memberMapper.selectCountByExample(example);
            myWorkerRunk= new WorkerRunkDTO();
            myWorkerRunk.setWorkerId(member.getId());
            myWorkerRunk.setWorkerHead(imageAddress+member.getHead());
            myWorkerRunk.setWorkerName(member.getName());
            myWorkerRunk.setRankNo(rankNo);
            myWorkerRunk.setIntegral(member.getEvaluationScore());
            map.put("worker",myWorkerRunk);
        }
        map.put("time",DateUtil.getDateString1(lastQuarter.getStart().getTime())+" - "+DateUtil.getDateString1(lastQuarter.getEnd().getTime()));
        return ServerResponse.createBySuccess("ok", map);
    }
}

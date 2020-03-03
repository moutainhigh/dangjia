package com.dangjia.acg.service.worker;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.IConfigMapper;
import com.dangjia.acg.mapper.worker.IWorkerChoiceCaseMapper;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.WorkerChoiceCase;
import com.dangjia.acg.service.ConfigService;
import com.dangjia.acg.service.configRule.ConfigRuleUtilService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2019/12/26 0031
 */
@Service
public class WorkerChoiceCaseService {

    private static Logger logger = LoggerFactory.getLogger(WorkerService.class);
    @Autowired
    private IWorkerChoiceCaseMapper workerChoiceCaseMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IConfigMapper iConfigMapper;

    public ServerResponse getWorkerChoiceCases(String userToken) {
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String jdAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Example example = new Example(WorkerChoiceCase.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(WorkerChoiceCase.DATA_STATUS, 0);
            criteria.andEqualTo(WorkerChoiceCase.WORKER_ID, worker.getId());
            example.orderBy(Activity.MODIFY_DATE).desc();
            List<WorkerChoiceCase> list = workerChoiceCaseMapper.selectByExample(example);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            Map<String,Object> map=new HashMap<>();
            Map paramMap;
            List<Map<String,Object>> newList=new ArrayList<>();
            for (WorkerChoiceCase v : list) {
                paramMap= BeanUtils.beanToMap(v);
                paramMap.put("imageUrl",StringTool.getImage(v.getImage(),jdAddress));
                newList.add(paramMap);
            }
            map.put("list",newList);
            //判断是否显示可上传图片
            map.put("showUploadButton",1);//是否可上传，1是，0否
            Config config=iConfigMapper.selectConfigInfoByParamKey("SHOW_SELECTED_CASES_COUNT");
            if(config!=null){
                if(list.size()>=Integer.parseInt(config.getParamValue())){
                    map.put("showUploadButton",0);//是否可上传，1是，0否
                }
            }else if(list.size()>=2){
                map.put("showUploadButton",0);//是否可上传，1是，0否
            }
            return ServerResponse.createBySuccess("查询成功", map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public ServerResponse getWorkerChoiceCasesCount(String userToken){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Map<String,Object> map=new HashMap<>();
            Example example = new Example(WorkerChoiceCase.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(WorkerChoiceCase.DATA_STATUS, 0);
            criteria.andEqualTo(WorkerChoiceCase.WORKER_ID, worker.getId());
            Integer myCount=workerChoiceCaseMapper.selectCountByExample(example);
            Config config=iConfigMapper.selectConfigInfoByParamKey("SHOW_SELECTED_CASES_COUNT");
            if(config!=null){
                map.put("totalCount",Integer.parseInt(config.getParamValue()));//可显示案例总数
            }else{
                map.put("totalCount",2);
            }
            map.put("myCount",myCount);//工人已拥有案例数量
            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public ServerResponse delWorkerChoiceCase(String id) {
        WorkerChoiceCase workerChoiceCase=workerChoiceCaseMapper.selectByPrimaryKey(id);
        workerChoiceCase.setDataStatus(1);
        workerChoiceCase.setModifyDate(new Date());
        if (this.workerChoiceCaseMapper.updateByPrimaryKey(workerChoiceCase) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     *
     * @param workerChoiceCase
     * @return
     */
    public ServerResponse editWorkerChoiceCase(WorkerChoiceCase workerChoiceCase) {
        return setWorkerChoiceCase(1, workerChoiceCase);
    }

    /**
     * 新增
     *
     * @param workerChoiceCase
     * @return
     */
    public ServerResponse addWorkerChoiceCase(String userToken,WorkerChoiceCase workerChoiceCase) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        WorkerChoiceCase choiceCase=new WorkerChoiceCase();
        choiceCase.setImage(workerChoiceCase.getImage());
        choiceCase.setTextContent(workerChoiceCase.getTextContent());
        choiceCase.setRemark(workerChoiceCase.getRemark());
        choiceCase.setWorkerId(worker.getId());
        return setWorkerChoiceCase(0, choiceCase);
    }

    private ServerResponse setWorkerChoiceCase(int type, WorkerChoiceCase workerChoiceCase) {
        if (type == 0) {//新增
            if (this.workerChoiceCaseMapper.insertSelective(workerChoiceCase) <= 0) {
                return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
            }
        } else {//修改
            if (this.workerChoiceCaseMapper.updateByPrimaryKeySelective(workerChoiceCase) <= 0) {
                return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
            }
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}

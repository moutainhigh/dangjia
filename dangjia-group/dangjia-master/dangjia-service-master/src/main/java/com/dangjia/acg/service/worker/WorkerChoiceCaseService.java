package com.dangjia.acg.service.worker;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.worker.IWorkerChoiceCaseMapper;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.worker.WorkerChoiceCase;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2019/12/26 0031
 */
@Service
public class WorkerChoiceCaseService {

    @Autowired
    private IWorkerChoiceCaseMapper workerChoiceCaseMapper;

    @Autowired
    private ConfigUtil configUtil;

    public ServerResponse getWorkerChoiceCases(PageDTO pageDTO, String workerId) {
        String jdAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Example example = new Example(WorkerChoiceCase.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(WorkerChoiceCase.DATA_STATUS, 0);
        criteria.andEqualTo(WorkerChoiceCase.WORKER_ID, workerId);
        example.orderBy(Activity.MODIFY_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<WorkerChoiceCase> list = workerChoiceCaseMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(list);
        for (WorkerChoiceCase v : list) {
            v.initPath(jdAddress);
        }
        pageResult.setList(list);
        return ServerResponse.createBySuccess("查询成功", pageResult);
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
    public ServerResponse addWorkerChoiceCase(WorkerChoiceCase workerChoiceCase) {
        return setWorkerChoiceCase(0, workerChoiceCase);
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

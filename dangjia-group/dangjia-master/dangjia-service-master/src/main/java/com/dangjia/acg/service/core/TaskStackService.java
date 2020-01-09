package com.dangjia.acg.service.core;

import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.mapper.task.IMasterTaskStackMapper;
import com.dangjia.acg.modle.house.TaskStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 任务记录表
 */
@Service
public class TaskStackService {
    @Autowired
    private IMasterTaskStackMapper iMasterTaskStackMapper;

    /**
     * 添加任务信息
     * @param houseId 房子ID
     * @param memberId 用户ID
     * @param name 任务名称
     * @param image 图片地址
     * @param type 任务类型
     * @param data 数值
     */
    public void insertTaskStackInfo(String houseId,String memberId ,String name,String image,Integer type,String data){
        TaskStack taskStack=new TaskStack();
        taskStack.setHouseId(houseId);
        taskStack.setMemberId(memberId);
        taskStack.setName(name);
        taskStack.setImage(image);
        taskStack.setType(type);
        taskStack.setData(data);
        taskStack.setState(0);
        iMasterTaskStackMapper.insert(taskStack);
    }

    /**
     * 查询待处理的任务
     * @param houseId
     * @param memberId
     * @return
     */
    public List<Task> selectTaskStackInfo(String houseId, String memberId){
        return iMasterTaskStackMapper.selectTaskStackInfo(houseId,memberId);
    }

    public List<Task> selectTaskStackInfoByType(String houseId,String type){
        return iMasterTaskStackMapper.selectTaskStackInfoByType(houseId,type);
    }

    /**
     * 查询符合条件的数据
     * @param houseId
     * @param type
     * @param data
     * @return
     */
    public TaskStack selectTaskStackByData(String houseId,Integer type,String data){
        Example example=new Example(TaskStack.class);
        example.createCriteria().andEqualTo(TaskStack.HOUSE_ID,houseId)
                .andEqualTo(TaskStack.TYPE,type)
                .andEqualTo(TaskStack.DATA,data)
                .andEqualTo(TaskStack.STATE,0);
        TaskStack taskStack=iMasterTaskStackMapper.selectOneByExample(example);
        return taskStack;
    }

    /**
     * 查询符合条件的数据
     * @param houseId
     * @param data
     * @return
     */
    public TaskStack selectTaskStackByHouseIdData(String houseId,String data){
        Example example=new Example(TaskStack.class);
        example.createCriteria().andEqualTo(TaskStack.HOUSE_ID,houseId)
                .andEqualTo(TaskStack.DATA,data)
                .andEqualTo(TaskStack.STATE,0);
        TaskStack taskStack=iMasterTaskStackMapper.selectOneByExample(example);
        return taskStack;
    }

    /**
     * 修改对应的数据
     * @param taskStack
     */
    public void updateTaskStackInfo(TaskStack taskStack){
        iMasterTaskStackMapper.updateByPrimaryKeySelective(taskStack);
    }

    /**
     * 根据任务ID查询任务
     * @param taskId
     * @return
     */
    public TaskStack selectTaskStackById(String taskId){

        return  iMasterTaskStackMapper.selectByPrimaryKey(taskId);
    }


}

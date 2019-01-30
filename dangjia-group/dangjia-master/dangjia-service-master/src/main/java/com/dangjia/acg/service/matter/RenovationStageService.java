package com.dangjia.acg.service.matter;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.matter.IRenovationStageMapper;
import com.dangjia.acg.modle.matter.RenovationStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date: 2018/11/20 0001
 * Time: 17:56
 */
@Service
public class RenovationStageService {
    @Autowired
    private IRenovationStageMapper renovationStageMapper;
    /**
     * 查询所有装修指南阶段配置
     * @return
     */
    public ServerResponse queryRenovationStage(){
        try{
            List<RenovationStage> rmList = renovationStageMapper.selectAll();
            return ServerResponse.createBySuccess("获取所有装修指南阶段配置成功",rmList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("获取所有装修指南阶段配置失败");
        }
    }

    /**
     * 新增装修指南阶段配置
     * @return
     */
    public ServerResponse addRenovationStage(String name){
        try{
            RenovationStage renovationStage=new RenovationStage();
            renovationStage.setName(name);
            renovationStageMapper.insertSelective(renovationStage);
            return ServerResponse.createBySuccessMessage("新增装修指南阶段配置成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("新增装修指南阶段配置失败");
        }
    }

    /**
     * 修改装修指南阶段配置
     * @return
     */
    public ServerResponse updateRenovationStage(String id,String name){
        try{
            RenovationStage renovationStage=new RenovationStage();
            renovationStage.setId(id);
            renovationStage.setName(name);
            renovationStageMapper.updateByPrimaryKeySelective(renovationStage);
            return ServerResponse.createBySuccessMessage("修改装修指南阶段配置成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("修改装修指南阶段配置失败");
        }
    }

    /**
     * 删除装修指南阶段配置
     * @return
     */
    public ServerResponse deleteRenovationStage(String id){
        try{
            RenovationStage renovationStage=new RenovationStage();
            renovationStage.setId(id);
            renovationStageMapper.deleteByPrimaryKey(renovationStage);
            return ServerResponse.createBySuccessMessage("删除装修指南阶段配置成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("删除装修指南阶段配置失败");
        }
    }


}

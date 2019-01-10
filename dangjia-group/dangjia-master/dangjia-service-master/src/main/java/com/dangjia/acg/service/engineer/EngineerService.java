package com.dangjia.acg.service.engineer;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.modle.core.HouseWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:37
 * 工程部
 */
@Service
public class EngineerService {
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;


    /**
     * 查看工匠 工地详情
     */
    public ServerResponse lookWorker(String houseId){
        try{
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId);
            example.orderBy(HouseWorker.CREATE_DATE).desc();
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);

            return ServerResponse.createBySuccess("查询成功",houseWorkerList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 指定工匠
     */
    public ServerResponse setLockWorker(String houseFlowId,String workerId){
        try{

            return ServerResponse.createBySuccess("查询成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 更换工匠
     */
    public ServerResponse changeWorker(){
        try{

            return ServerResponse.createBySuccess("查询成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

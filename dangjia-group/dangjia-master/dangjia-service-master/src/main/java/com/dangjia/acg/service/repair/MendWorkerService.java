package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 11:40
 */
@Service
public class MendWorkerService {

    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;

    @Autowired
    private MendMaterielService mendMaterielService;
    /**
     * 房子id查询退人工
     */
    public ServerResponse workerBackState(String houseId,Integer pageNum, Integer pageSize,String beginDate, String endDate,String likeAddress){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
//            List<MendOrder> mendOrderList = mendOrderMapper.workerBackState(houseId); 3
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(houseId, 3, beginDate, endDate, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = mendMaterielService.getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 人工单明细mendOrderId
     */
    public ServerResponse mendWorkerList(String mendOrderId){
        List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrderId);
        for (MendWorker mendWorker : mendWorkerList){
            mendWorker.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return ServerResponse.createBySuccess("查询成功", mendWorkerList);
    }

   /**
     * 房子id查询补人工单列表
     */
    public ServerResponse workerOrderState(String houseId,Integer pageNum, Integer pageSize,String beginDate, String endDate,String likeAddress){
        try{
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
//            List<MendOrder> mendOrderList = mendOrderMapper.workerOrderState(houseId);
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(houseId, 1, beginDate, endDate, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = mendMaterielService.getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);

            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}

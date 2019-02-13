package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.finance.WebWorkerDetailDTO;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class WebWalletService {
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private static Logger LOG = LoggerFactory.getLogger(WebWalletService.class);

    /**
     * 所有用户（工人和业主）流水
     */
    public ServerResponse getAllWallet(PageDTO pageDTO, String workerId, String houseId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WorkerDetail> list = iWorkerDetailMapper.getAllWallet(workerId, houseId);
            List<WebWorkerDetailDTO> workerDetailDTOList = new ArrayList<>();
            for (WorkerDetail workerDetail : list) {
                WebWorkerDetailDTO workerDetailDTO = new WebWorkerDetailDTO();
                workerDetailDTO.setId(workerDetail.getId());
                workerDetailDTO.setDefinedName(workerDetail.getDefinedName());
                workerDetailDTO.setHaveMoney(workerDetail.getHaveMoney());
                House house = iHouseMapper.selectByPrimaryKey(workerDetail.getHouseId());
                if (house != null)
                    workerDetailDTO.setHouseName(house.getHouseName());
                workerDetailDTO.setMoney(workerDetail.getMoney());
                workerDetailDTO.setName(workerDetail.getName());
                workerDetailDTO.setState(workerDetail.getState());
                workerDetailDTO.setWorkerName(workerDetail.getWorkerName());
                workerDetailDTO.setCreateDate(workerDetail.getCreateDate());
                workerDetailDTO.setModifyDate(workerDetail.getModifyDate());
                workerDetailDTOList.add(workerDetailDTO);
            }
            PageInfo pageResult = new PageInfo(list);
            pageResult.setList(workerDetailDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


}

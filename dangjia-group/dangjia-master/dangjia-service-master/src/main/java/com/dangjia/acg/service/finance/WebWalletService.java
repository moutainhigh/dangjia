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
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 所有用户（工人和业主）流水
     */
    public ServerResponse getAllWallet(PageDTO pageDTO, String workerId, String houseId, String likeMobile, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WorkerDetail> list = iWorkerDetailMapper.getAllWallet(workerId, houseId, likeMobile, likeAddress);
            List<WebWorkerDetailDTO> workerDetailDTOList = new ArrayList<>();
            for (WorkerDetail workerDetail : list) {
                WebWorkerDetailDTO workerDetailDTO = new WebWorkerDetailDTO();
                workerDetailDTO.setId(workerDetail.getId());
                workerDetailDTO.setDefinedName(workerDetail.getDefinedName());
                workerDetailDTO.setHaveMoney(workerDetail.getHaveMoney());
                House house = iHouseMapper.selectByPrimaryKey(workerDetail.getHouseId());
                if (house != null)
                    workerDetailDTO.setHouseName(house.getHouseName());
                if (StringUtils.isNoneBlank(workerDetail.getWorkerId())) {
                    Member member = iMemberMapper.selectByPrimaryKey(workerDetail.getWorkerId());
                    if (member != null) {
                        workerDetailDTO.setMemberId(member.getId());
                        workerDetailDTO.setMobile(member.getMobile());
                    }
                }
                workerDetailDTO.setMoney(workerDetail.getMoney());
                workerDetailDTO.setName(workerDetail.getName());
                workerDetailDTO.setState(workerDetail.getState());
                workerDetailDTO.setWorkerName(workerDetail.getWorkerName());
                workerDetailDTO.setCreateDate(workerDetail.getCreateDate());
                workerDetailDTO.setModifyDate(workerDetail.getModifyDate());
                workerDetailDTO.setWalletMoney(workerDetail.getWalletMoney());
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

    /**
     * 添加用户（工人和业主）流水
     * 修改用户钱包
     */
    public ServerResponse addWallet(WorkerDetail workerDetail) {
        try {
            if (!StringUtils.isNoneBlank(workerDetail.getName()))
                return ServerResponse.createByErrorMessage("流水说明 不能为null");
            if (!StringUtils.isNoneBlank(workerDetail.getDefinedName()))
                return ServerResponse.createByErrorMessage("自定义流水说明 不能为null");

            Member worker = iMemberMapper.selectByPrimaryKey(workerDetail.getWorkerId());
//            if(workerDetail.getState() == 3 && worker.getSurplusMoney().compareTo(workerDetail.getMoney()) < 0){
//                return ServerResponse.createByErrorMessage("工匠余额不足");
//            }
            if (workerDetail.getState() == 3) {//减钱
                worker.setHaveMoney(worker.getHaveMoney().subtract(workerDetail.getMoney()));
                worker.setSurplusMoney(worker.getSurplusMoney().subtract(workerDetail.getMoney()));
            }
            if (workerDetail.getState() == 2) {//加钱
                worker.setHaveMoney(worker.getHaveMoney().add(workerDetail.getMoney()));
                worker.setSurplusMoney(worker.getSurplusMoney().add(workerDetail.getMoney()));
            }
            iMemberMapper.updateByPrimaryKeySelective(worker);

            //记录到管家流水
            WorkerDetail newWorkerDetail = new WorkerDetail();
            newWorkerDetail.setName(workerDetail.getName());
            newWorkerDetail.setWorkerId(workerDetail.getWorkerId());
            if (worker != null)
                newWorkerDetail.setWorkerName(worker.getName());

            if (workerDetail.getHouseId() != null)
                newWorkerDetail.setHouseId(workerDetail.getHouseId());


            newWorkerDetail.setMoney(workerDetail.getMoney());
            newWorkerDetail.setState(workerDetail.getState());//进钱  0工钱收入,1提现,2自定义增加金额,3自定义减少金额,4退材料退款,5剩余材料退款,6退人工退款
            newWorkerDetail.setDefinedName(workerDetail.getDefinedName());
            iWorkerDetailMapper.insert(newWorkerDetail);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }


}

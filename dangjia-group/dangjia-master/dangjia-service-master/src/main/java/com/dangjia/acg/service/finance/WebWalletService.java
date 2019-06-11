package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.finance.WebWorkerDetailDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private CraftsmanConstructionService constructionService;

    /**
     * --         0每日完工  1阶段完工，
     * --         2整体完工  3巡查, 4验收,
     * --         8补人工, 9退人工, 10奖 11罚
     *
     * @param houseId
     * @param userToken
     * @return
     */
    public ServerResponse getHouseWallet(String houseId, String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        List<WebWorkerDetailDTO> workerDetailDTOList = iWorkerDetailMapper.getHouseWallet(houseId, member.getId(), String.valueOf(member.getWorkerType()));
        for (int i = 0; i < workerDetailDTOList.size(); i++) {
            WebWorkerDetailDTO webWorkerDetailDTO = workerDetailDTOList.get(i);
            webWorkerDetailDTO = calculateIntegralMoney(houseId, webWorkerDetailDTO);
            workerDetailDTOList.remove(i);
            workerDetailDTOList.add(i, webWorkerDetailDTO);
        }
        return ServerResponse.createBySuccess("查询成功", workerDetailDTOList);
    }

    public WebWorkerDetailDTO calculateIntegralMoney(String houseId, WebWorkerDetailDTO webWorkerDetailDTO) {
//     * --         0每日完工  1阶段完工，
//     * --         2整体完工  3巡查, 4验收,5整体竣工
        int star = webWorkerDetailDTO.getStar();

        webWorkerDetailDTO.setHaveMoney(webWorkerDetailDTO.getMoney());
        BigDecimal supervisorMoney = webWorkerDetailDTO.getMoney();
        if (webWorkerDetailDTO.getState() == 4) {
            if (star == 5) {
                supervisorMoney = webWorkerDetailDTO.getMoney();//大管家的验收收入
            } else if (star == 3 || star == 4) {
                supervisorMoney = webWorkerDetailDTO.getMoney().multiply(new BigDecimal(0.8));
            } else {
                supervisorMoney = new BigDecimal(0);//为0元
            }
        }
        if (webWorkerDetailDTO.getState() == 5) {
            if(star == 5){
                supervisorMoney = webWorkerDetailDTO.getMoney();//大管家的验收收入
            }else if(star == 3 || star == 4){
                supervisorMoney = webWorkerDetailDTO.getMoney().multiply(new BigDecimal(0.9));
            }else{
                supervisorMoney = webWorkerDetailDTO.getMoney().multiply(new BigDecimal(0.8));
            }
        }
        if (webWorkerDetailDTO.getState() == 1 || webWorkerDetailDTO.getState() == 2) {
            //工人钱
            if (star >= 4) {
                supervisorMoney = webWorkerDetailDTO.getMoney();
            } else if (star > 2) {
                supervisorMoney = webWorkerDetailDTO.getMoney().multiply(new BigDecimal(0.97));
            } else {
                supervisorMoney = webWorkerDetailDTO.getMoney().multiply(new BigDecimal(0.95));
            }
        }
        if (webWorkerDetailDTO.getState() == 13) {
            supervisorMoney = webWorkerDetailDTO.getMoney();//大管家的提前结束退款收入
        }
        webWorkerDetailDTO.setMoney(supervisorMoney);
        return webWorkerDetailDTO;
    }

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
            BigDecimal haveMoney = new BigDecimal(0);
            BigDecimal surplusMoney = new BigDecimal(0);
//            if(workerDetail.getState() == 3 && worker.getSurplusMoney().compareTo(workerDetail.getMoney()) < 0){
//                return ServerResponse.createByErrorMessage("工匠余额不足");
//            }
            if (workerDetail.getState() == 3) {//减钱
                haveMoney = worker.getHaveMoney().subtract(workerDetail.getMoney());
                surplusMoney = worker.getSurplusMoney().subtract(workerDetail.getMoney());
            }
            if (workerDetail.getState() == 2) {//加钱
                haveMoney = worker.getHaveMoney().add(workerDetail.getMoney());
                surplusMoney = worker.getSurplusMoney().add(workerDetail.getMoney());
            }
            worker.setHaveMoney(haveMoney);
            worker.setSurplusMoney(surplusMoney);
            iMemberMapper.updateByPrimaryKeySelective(worker);

            //记录到管家流水
            WorkerDetail newWorkerDetail = new WorkerDetail();
            newWorkerDetail.setName(workerDetail.getName());
            newWorkerDetail.setWorkerId(workerDetail.getWorkerId());
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

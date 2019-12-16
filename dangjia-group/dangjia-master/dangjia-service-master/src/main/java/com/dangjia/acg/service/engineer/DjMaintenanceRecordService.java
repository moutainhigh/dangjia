package com.dangjia.acg.service.engineer;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordDTO;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordProductDTO;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordResponsiblePartyDTO;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordResponsiblePartyMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordResponsibleParty;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 10:01
 */
@Service
public class DjMaintenanceRecordService {

    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordResponsiblePartyMapper djMaintenanceRecordResponsiblePartyMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper djMaintenanceRecordProductMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;
    @Autowired
    IWorkerDetailMapper iWorkerDetailMapper;

    /**
     * 查询质保审核列表
     *
     * @param pageDTO
     * @param searchKey
     * @return
     */
    public ServerResponse queryDjMaintenanceRecordList(PageDTO pageDTO, String searchKey, Integer state) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjMaintenanceRecordDTO> djMaintenanceRecordDTOS =
                    djMaintenanceRecordMapper.queryDjMaintenanceRecordList(searchKey,state);
            if (djMaintenanceRecordDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo = new PageInfo(djMaintenanceRecordDTOS);
            return ServerResponse.createBySuccess("查询成功", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private List<String> getImage(String image){
        List<String> strList = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<String> result = Arrays.asList(image.split(","));
        for (int i = 0; i < result.size(); i++) {
            String str = imageAddress + result.get(i);
            strList.add(str);
        }
        return strList;
    }



    /**
     * 查询质保审核详情
     *
     * @param id
     * @return
     */
    public ServerResponse queryDjMaintenanceRecordDetail(String id) {
        try {
            DjMaintenanceRecordDTO djMaintenanceRecordDTOS =
                    djMaintenanceRecordMapper.queryDjMaintenanceRecordDetail(id);
            List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                    djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(id);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductDTOS);
            Example example = new Example(DjMaintenanceRecordResponsibleParty.class);
            example.createCriteria().andEqualTo(DjMaintenanceRecordResponsibleParty.MAINTENANCE_RECORD_ID, id)
                    .andEqualTo(DjMaintenanceRecordResponsibleParty.DATA_STATUS, 0);
            List<DjMaintenanceRecordResponsibleParty> djMaintenanceRecordResponsibleParties =
                    djMaintenanceRecordResponsiblePartyMapper.selectByExample(example);
            List<DjMaintenanceRecordResponsiblePartyDTO> djMaintenanceRecordResponsiblePartyDTOS = new ArrayList<>();
            djMaintenanceRecordResponsibleParties.forEach(djMaintenanceRecordResponsibleParty -> {
                DjMaintenanceRecordResponsiblePartyDTO djMaintenanceRecordResponsiblePartyDTO =
                        djMaintenanceRecordResponsiblePartyMapper.queryDjMaintenanceRecordResponsibleParty(
                                djMaintenanceRecordResponsibleParty.getId(),
                                djMaintenanceRecordResponsibleParty.getResponsiblePartyType());
                djMaintenanceRecordResponsiblePartyDTO.setImage(imageAddress + djMaintenanceRecordResponsiblePartyDTO.getImage());
                djMaintenanceRecordResponsiblePartyDTOS.add(djMaintenanceRecordResponsiblePartyDTO);
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordResponsiblePartyDTOS(djMaintenanceRecordResponsiblePartyDTOS);
            djMaintenanceRecordDTOS.setOwnerImages(this.getImage(djMaintenanceRecordDTOS.getOwnerImage()));
            djMaintenanceRecordDTOS.setStewardImages(this.getImage(djMaintenanceRecordDTOS.getStewardImage()));
            return ServerResponse.createBySuccess("查询成功",djMaintenanceRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 处理质保审核
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setDjMaintenanceRecord(String id,Integer state, String userId) {
        try {
            DjMaintenanceRecord djMaintenanceRecord;
            if(state==2){//通过
                Example example=new Example(DjMaintenanceRecordResponsibleParty.class);
                DjMaintenanceRecord djMaintenanceRecord1 = djMaintenanceRecordMapper.selectByPrimaryKey(id);
                example.createCriteria().andEqualTo(DjMaintenanceRecordResponsibleParty.MAINTENANCE_RECORD_ID,id)
                        .andEqualTo(DjMaintenanceRecordResponsibleParty.DATA_STATUS,0);
                List<DjMaintenanceRecordResponsibleParty> djMaintenanceRecordResponsibleParties =
                        djMaintenanceRecordResponsiblePartyMapper.selectByExample(example);
                djMaintenanceRecordResponsibleParties.forEach(djMaintenanceRecordResponsibleParty -> {
                    //扣除金额
                    Double amountDeducted=(djMaintenanceRecordResponsibleParty.getProportion()/100)*djMaintenanceRecord1.getSincePurchaseAmount();
                    if(djMaintenanceRecordResponsibleParty.getResponsiblePartyType()==2){
                        AccountFlowRecord accountFlowRecord=new AccountFlowRecord();
                        accountFlowRecord.setState(3);
                        accountFlowRecord.setDefinedAccountId(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        accountFlowRecord.setCreateBy(userId);
                        accountFlowRecord.setHouseOrderId(djMaintenanceRecordResponsibleParty.getId());
                        Storefront storefront =
                                iMasterStorefrontMapper.selectByPrimaryKey(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        accountFlowRecord.setAmountBeforeMoney(storefront.getRetentionMoney());//入账前金额
                        storefront.setRetentionMoney(storefront.getRetentionMoney()-amountDeducted);
                        //扣除店铺占比金额
                        iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
                        accountFlowRecord.setAmountAfterMoney(storefront.getRetentionMoney());//入账后金额
                        accountFlowRecord.setFlowType("1");
                        accountFlowRecord.setMoney(amountDeducted);
                        accountFlowRecord.setDefinedName("店铺维保占比,扣除滞留金：" + amountDeducted);
                        //记录流水
                        iMasterAccountFlowRecordMapper.insert(accountFlowRecord);
                    }else if(djMaintenanceRecordResponsibleParty.getResponsiblePartyType()==3){
                        WorkerDetail workerDetail=new WorkerDetail();
                        workerDetail.setName("维保占比,扣除滞留金：" + amountDeducted);
                        workerDetail.setWorkerId(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        Member member =
                                iMemberMapper.selectByPrimaryKey(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
                        //扣除工匠占比金额
                        member.setRetentionMoney(member.getRetentionMoney().subtract(new BigDecimal(amountDeducted)));
                        iMemberMapper.updateByPrimaryKeySelective(member);
                        workerDetail.setWorkerName(member.getName());
                        workerDetail.setMoney(new BigDecimal(amountDeducted));
                        workerDetail.setState(3);
                        workerDetail.setDefinedWorkerId(djMaintenanceRecordResponsibleParty.getId());
                        //记录流水
                        iWorkerDetailMapper.insert(workerDetail);
                    }
                });
                djMaintenanceRecord=new DjMaintenanceRecord();
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(2);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            }else if(state==3){//拒绝
                djMaintenanceRecord=new DjMaintenanceRecord();
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(3);
                djMaintenanceRecord.setStewardState(1);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            }
            return ServerResponse.createBySuccessMessage("处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("处理失败");
        }
    }

    /**
     * 查询督导列表列表
     * @param pageDTO
     * @param name
     * @return
     */
    public ServerResponse queryMemberList(PageDTO pageDTO, String name) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(Member.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Member.WORKER_TYPE, 12)
                    .andEqualTo(Member.DATA_STATUS, 0);
            if (!CommonUtil.isEmpty(name)) {
                criteria.andLike(Member.NAME,"%"+ name +"%");
            }
            List<Member> members = iMemberMapper.selectByExample(example);

            if (members.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo = new PageInfo(members);
            return ServerResponse.createBySuccess("查询成功", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

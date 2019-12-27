package com.dangjia.acg.service.engineer;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.*;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordContentMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordResponsiblePartyMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.task.IMasterTaskStackMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordContent;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordProduct;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordResponsibleParty;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.TaskStack;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.*;

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

    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IComplainMapper iComplainMapper;

    @Autowired
    private IMasterTaskStackMapper iMasterTaskStackMapper;
    @Autowired
    private DjMaintenanceRecordContentMapper djMaintenanceRecordContentMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private MasterProductTemplateService imasterProductTemplateService;

    @Autowired
    private IHouseFlowMapper iHouseFlowMapper;

    @Autowired
    private TaskStackService taskStackService;

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;

    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigMessageService configMessageService ;



    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveMaintenanceRecord(String userToken,String houseId, String workerTypeSafeOrderId,
                                                String remark,String images){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        //判断当前房子下是否有正在处理中的质保
        List<DjMaintenanceRecord>  maintenanceRecordList=djMaintenanceRecordMapper.selectMaintenanceRecoredByHouseId(houseId);
        if(maintenanceRecordList!=null&&maintenanceRecordList.size()>0){
            return ServerResponse.createByErrorMessage("已有质保流程在处理中！");
        }
        Member member = (Member) object;//业主信息
        //添加质保信息
        DjMaintenanceRecord djMaintenanceRecord=new DjMaintenanceRecord();
        djMaintenanceRecord.setHouseId(houseId);
        djMaintenanceRecord.setMemberId(member.getId());
        djMaintenanceRecord.setOwnerName(member.getName());
        djMaintenanceRecord.setOwnerMobile(member.getMobile());
        djMaintenanceRecord.setWorkerTypeSafeOrderId(workerTypeSafeOrderId);
        //查询保险订单对应的工种
        WorkerTypeSafeOrder workerTypeSafeOrder=workerTypeSafeOrderMapper.selectByPrimaryKey(workerTypeSafeOrderId);
        djMaintenanceRecord.setWorkerTypeId(workerTypeSafeOrder.getWorkerTypeId());
        djMaintenanceRecordMapper.insert(djMaintenanceRecord);
        //添加质保对应的图片、备注信息
        DjMaintenanceRecordContent djMaintenanceRecordContent=new DjMaintenanceRecordContent();
        djMaintenanceRecordContent.setMaintenanceRecordId(djMaintenanceRecord.getId());
        djMaintenanceRecordContent.setRemark(remark);
        djMaintenanceRecordContent.setImage(images);
        djMaintenanceRecordContent.setMemberId(member.getId());
        djMaintenanceRecordContent.setType(3);
        djMaintenanceRecordContentMapper.insert(djMaintenanceRecordContent);
        return ServerResponse.createBySuccess("申请成功","");
    }



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

            //维保商品列表
            List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                    djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(id);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductDTOS);

            ///维保责任方
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

            if (!CommonUtil.isEmpty(djMaintenanceRecordDTOS.getWorkerImage())) {
                //工匠上传图片
                djMaintenanceRecordDTOS.setWorkerImages(this.getImage(djMaintenanceRecordDTOS.getWorkerImage()));
            }

            if (!CommonUtil.isEmpty(djMaintenanceRecordDTOS.getStewardImage())) {
                //业主上传图片
                djMaintenanceRecordDTOS.setOwnerImages(this.getImage(djMaintenanceRecordDTOS.getOwnerImage()));
            }

            if (!CommonUtil.isEmpty(djMaintenanceRecordDTOS.getStewardImage())) {
                //管家上传图片
                djMaintenanceRecordDTOS.setStewardImages(this.getImage(djMaintenanceRecordDTOS.getStewardImage()));
            }

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
                    Double amountDeducted=(djMaintenanceRecordResponsibleParty.getProportion()/100)*(djMaintenanceRecord1.getSincePurchaseAmount()+djMaintenanceRecord1.getEnoughAmount());
                    if(djMaintenanceRecordResponsibleParty.getResponsiblePartyType()==1){
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
                        //消息推送
                        if(member.getRetentionMoney().intValue() < 0){
                            //工匠缴纳质保金通知
                            pushNews(8,member,djMaintenanceRecordResponsibleParty,djMaintenanceRecord1);
                        }

                        //责任人通知
                        pushNews(9,member,djMaintenanceRecordResponsibleParty,djMaintenanceRecord1);

                        //查询管家是否定损
                        Example example1 = new Example(HouseFlowApply.class);
                        example1.createCriteria().andEqualTo(DjMaintenanceRecordContent.MAINTENANCE_RECORD_ID, djMaintenanceRecord1.getId())
                                .andEqualTo(DjMaintenanceRecordContent.DATA_STATUS, 0)
                                .andEqualTo(DjMaintenanceRecordContent.TYPE,2);
                        List<DjMaintenanceRecordContent> dmrc = djMaintenanceRecordContentMapper.selectByExample(example1);
                        if(dmrc != null && dmrc.size() > 0){
                            //管家定损后通知
                            pushNews(10,member,djMaintenanceRecordResponsibleParty,djMaintenanceRecord1);
                        }
                    }
                });
                djMaintenanceRecord=new DjMaintenanceRecord();
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(2);
                djMaintenanceRecord.setPaymentDate(new Date());
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


    private void pushNews(Integer type,
                          Member member,
                          DjMaintenanceRecordResponsibleParty djMaintenanceRecordResponsibleParty,
                          DjMaintenanceRecord djMaintenanceRecord1){
        TaskStack taskStack =new TaskStack();
        if(type == 8){
            WorkerType w = workerTypeMapper.selectByPrimaryKey(member.getWorkerType());
            taskStack.setName(w.getName() + "缴纳质保金");
            taskStack.setType(type);
            taskStack.setData(djMaintenanceRecordResponsibleParty.getId());
        }else if(type == 9){
            taskStack.setName("维保责任划分通知");
            taskStack.setType(type);
            taskStack.setData(djMaintenanceRecordResponsibleParty.getMaintenanceRecordId());
        }else if(type == 10){
            taskStack.setName("管家定损后通知");
            taskStack.setType(type);
            taskStack.setData(djMaintenanceRecordResponsibleParty.getMaintenanceRecordId());
        }
        taskStack.setMemberId(djMaintenanceRecordResponsibleParty.getResponsiblePartyId());
        taskStack.setHouseId(djMaintenanceRecord1.getHouseId());
        taskStack.setImage("icon/sheji.png");
        taskStack.setState(0);
        iMasterTaskStackMapper.insert(taskStack);


    }

    /**
     * 查询督导列表
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


    /**
     * 处理申诉
     * @param supervisorId
     * @return
     */
    public ServerResponse upDateMaintenanceInFo(String supervisorId,
                                                Integer stewardSubsidy,
                                                String serviceRemark,
                                                String userId,
                                                String id,
                                                Integer handleType) {
        try {
            DjMaintenanceRecord djMaintenanceRecord = new DjMaintenanceRecord();
            if(handleType != null && handleType == 3){
                //确定处理
                djMaintenanceRecord.setUserId(userId);
                djMaintenanceRecord.setSupervisorId(supervisorId);
                djMaintenanceRecord.setStewardSubsidy(stewardSubsidy);
                djMaintenanceRecord.setServiceRemark(serviceRemark);
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(1);
                djMaintenanceRecord.setCreateDate(null);
                djMaintenanceRecord.setHandleType(handleType);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
                return ServerResponse.createBySuccess("提交成功");
            }else if(handleType != null && handleType == 4){
                //结束流程
                djMaintenanceRecord.setUserId(userId);
                djMaintenanceRecord.setServiceRemark(serviceRemark);
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(2);
                djMaintenanceRecord.setCreateDate(null);
                djMaintenanceRecord.setHandleType(handleType);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
                return ServerResponse.createBySuccess("提交成功");
            }
            return ServerResponse.createByErrorMessage("提交失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 修改消息状态
     * @param id
     */
    public ServerResponse updateTaskStackData(String id){
        try {
            TaskStack taskStack = new TaskStack();
            taskStack.setCreateDate(null);
            taskStack.setId(id);
            taskStack.setState(1);
            iMasterTaskStackMapper.updateByPrimaryKeySelective(taskStack);
            return ServerResponse.createByErrorMessage("提交失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 查询维保责任记录
     * @param memberId
     * @return
     */
    public ServerResponse queryDimensionRecord(String memberId){
        try {
            List<DimensionRecordDTO> dimensionRecordDTOS =  djMaintenanceRecordResponsiblePartyMapper.queryDimensionRecord(memberId);
            return ServerResponse.createBySuccess("查询成功", dimensionRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询维保详情
     * @param mrId
     * @return
     */
    public ServerResponse queryDimensionRecordInFo(String mrId){
        try {

            DimensionRecordDTO dimensionRecordDTOS =  djMaintenanceRecordResponsiblePartyMapper.queryDimensionRecordInFo(mrId,3);

            //查询房子信息
            Example example = new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlowApply.WORKER_ID, dimensionRecordDTOS.getResponsiblePartyId())
                    .andEqualTo(HouseFlowApply.DATA_STATUS, 0)
                    .andEqualTo(HouseFlowApply.APPLY_TYPE,2);
            example.orderBy(HouseFlowApply.CREATE_DATE).desc();
            List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example);
            String dateStr = "";
            if(houseFlowApplies != null && houseFlowApplies.size() > 0){
                dateStr =  DateUtil.dateToString(houseFlowApplies.get(0).getCreateDate(),"yyyy-MM-dd");
            }

            dimensionRecordDTOS.setStr( "您于" + dateStr + "申请整体完工的工地" + "“"+dimensionRecordDTOS.getHouseName() + "”," +
                    "业主申请了质保,经管家实地查看,平台合适确定,您需要负担" + dimensionRecordDTOS.getProportion() + "%的责任," +
                    "已从您的滞留金中扣除总维保金额的" + dimensionRecordDTOS.getProportion() + "%,请悉知,如有疑问可申诉。");

            //维保商品列表
            List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                    djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(dimensionRecordDTOS.getMrId());
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                djMaintenanceRecordProductDTO.setValueNameArr(imasterProductTemplateService.getNewValueNameArr(djMaintenanceRecordProductDTO.getValueIdArr()));
                djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
            });
            dimensionRecordDTOS.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductDTOS);




            //查询申诉状态
            example = new Example(Complain.class);
            example.createCriteria().andEqualTo(Complain.MEMBER_ID, dimensionRecordDTOS.getResponsiblePartyId())
                    .andEqualTo(Complain.DATA_STATUS, 0)
                    .andEqualTo(Complain.HOUSE_ID,dimensionRecordDTOS.getHouseId())
                    .andEqualTo(Complain.COMPLAIN_TYPE,10);
            example.orderBy(HouseFlowApply.CREATE_DATE).desc();
            List<Complain> complains = iComplainMapper.selectByExample(example);
            //0-申诉 1-申诉中  2-已完成
            if(complains != null && complains.size() > 0){
                dimensionRecordDTOS.setType(complains.get(0).getStatus() == 0? 1 : 2);
            }else{
                dimensionRecordDTOS.setType(0);
            }


            return ServerResponse.createBySuccess("查询成功", dimensionRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 新增工匠申诉
     * @param responsiblePartyId
     * @return
     */
    public ServerResponse insertResponsibleParty(String responsiblePartyId,
                                                 String houseId,
                                                 String description,
                                                 String image){
        try {
            Complain complain = new Complain();
            complain.setComplainType(10);
            complain.setStatus(0);
            complain.setHouseId(houseId);
            complain.setMemberId(responsiblePartyId);
            complain.setDescription(description);
            complain.setImage(image);
            iComplainMapper.insert(complain);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "保存失败");
        }
    }


    /**
     * 查询工匠申诉
     * @param responsiblePartyId
     * @return
     */
    public ServerResponse queryResponsibleParty(String responsiblePartyId,String houseId){
        try {
            ComplainDataDTO complainInFoDTO =  djMaintenanceRecordResponsiblePartyMapper.queryResponsibleParty(responsiblePartyId,houseId);
            if(complainInFoDTO != null){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                complainInFoDTO.setHead(imageAddress + complainInFoDTO.getHead());
                //1-投诉中 2-已完成
                complainInFoDTO.setType(complainInFoDTO.getStatus() == 0 ? 1: 2);
                complainInFoDTO.setImages(getImage(complainInFoDTO.getImage()));
                return ServerResponse.createBySuccess("查询待抢单列表", complainInFoDTO);
            }else{
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }


    /**
     * 查询工匠缴纳质保金
     * @param data
     * @return
     */
    public ServerResponse toQualityMoney(String data){
        try {
            ToQualityMoneyDTO toQualityMoneyDTO = djMaintenanceRecordResponsiblePartyMapper.toQualityMoney(data);
            if(toQualityMoneyDTO != null){
                String dateStr =  DateUtil.dateToString(toQualityMoneyDTO.getCreateDate(),"yyyy-MM-dd");
                String workerName = workerTypeMapper.selectByPrimaryKey(toQualityMoneyDTO.getWorkerTypeId()).getName();
                toQualityMoneyDTO.setStr("“" + toQualityMoneyDTO.getHouseName() + "”于" +dateStr+
                "申请了"+workerName+"质保,经大管家实地勘察后,确定你责任占比"+toQualityMoneyDTO.getProportion() +
                        "%,因您质保不足则需支付质保金。");

                toQualityMoneyDTO.setToQualityAmount(toQualityMoneyDTO.getSincePurchaseAmount() + toQualityMoneyDTO.getEnoughAmount());
                toQualityMoneyDTO.setNeedAmount(toQualityMoneyDTO.getPaymentAmount() - toQualityMoneyDTO.getRetentionMoney());
            }

            return ServerResponse.createBySuccess("查询成功", toQualityMoneyDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }

    /**
     * 查询工匠抢单详情
     * @param data
     * @return
     */
        public ServerResponse queryRobOrderInFo(String userToken,String workerId,String houseId,String data){
        try {

            DimensionRecordDTO dimensionRecordDTOS =  djMaintenanceRecordResponsiblePartyMapper.queryDimensionRecordInFo(data,3);

            //维保记录内容
            Example example = new Example(DjMaintenanceRecordContent.class);
            example.createCriteria().andEqualTo(DjMaintenanceRecordContent.MAINTENANCE_RECORD_ID, dimensionRecordDTOS.getMrId())
                    .andEqualTo(DjMaintenanceRecordContent.DATA_STATUS, 0);
            example.orderBy(DjMaintenanceRecordContent.CREATE_DATE).desc();
            List<DjMaintenanceRecordContent> dmrc = djMaintenanceRecordContentMapper.selectByExample(example);
            List<Map<String,Object>> list = new ArrayList<>();
            Map<String,Object> map = null;
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (DjMaintenanceRecordContent d: dmrc) {
                Member member = iMemberMapper.selectByPrimaryKey(d.getMemberId());
                if(d.getType() == 3){
                    dimensionRecordDTOS.setCreateDate(d.getCreateDate());
                    dimensionRecordDTOS.setOwnerName(member.getName());
                }
                map = new HashMap<>();

                map.put("name",member.getName());
                map.put("head",imageAddress + member.getHead());
                map.put("createDate",d.getCreateDate());
                map.put("memberType",d.getType());//类型 1:工匠 2:大管家 3：业主
                map.put("remark",d.getRemark());
                map.put("images",getImage(d.getImage()));
                if(d.getType() == 2){
                    //工匠先进场
                    dimensionRecordDTOS.setPageType(2);
                    //维保商品列表
                    List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                            djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(dimensionRecordDTOS.getMrId());
                    djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                        djMaintenanceRecordProductDTO.setValueNameArr(imasterProductTemplateService.getNewValueNameArr(djMaintenanceRecordProductDTO.getValueIdArr()));
                        djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
                    });
                    map.put("djMaintenanceRecordProductDTOS",djMaintenanceRecordProductDTOS);
                }else{
                    dimensionRecordDTOS.setPageType(1);//工匠先进场
                }
                list.add(map);
            }

            example = new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, dimensionRecordDTOS.getHouseId())
                    .andEqualTo(HouseFlow.DATA_STATUS, 0)
                    .andEqualTo(HouseFlow.WORKER_TYPE_ID,dimensionRecordDTOS.getWorkerTypeId());
            example.orderBy(HouseFlow.CREATE_DATE).desc();
            List<HouseFlow> houseFlows = iHouseFlowMapper.selectByExample(example);
            if(houseFlows.get(0).getWorkerId().equals(workerId)){
                dimensionRecordDTOS.setPrimaryType(1);//原工匠
            }else{
                dimensionRecordDTOS.setPrimaryType(2);//非原工匠
            }


            dimensionRecordDTOS.setList(list);
            return ServerResponse.createBySuccess("查询成功",dimensionRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
    }






    /**
     * 查询工匠质保金是否弹框
     * type 0- 不弹框 1-弹框
     * @param memberId
     * @return
     */
    public Integer queryRetentionMoney(String memberId){
        Member member = iMemberMapper.selectByPrimaryKey(memberId);
        Integer type = 0; //0- 不弹框 1-弹框
        if(member != null && member.getRetentionMoney().intValue() < 0){
            type = 1;
            return type;
        }
        return type;
    }
    /**
     * 确认申请验收
     * @param houseId
     * @return
     */
//    public ServerResponse applicationAcceptance(String houseId) {
//        try {
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
//        }
//    }

    /**
     * 缴纳质保金列表
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryGuaranteeMoneyList(PageDTO pageDTO,String userId,String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront=basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息!");
            }
            List<ResponsiblePartyDTO> list=djMaintenanceRecordResponsiblePartyMapper.queryGuaranteeMoneyList(storefront.getId());
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 缴纳质保金详情
     */
    public ServerResponse queryGuaranteeMoneyDetail(String userId,String cityId,String id) {
        try {
            Storefront storefront=basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息!");
            }
            ResponsiblePartyDetailDTO responsiblePartyDetailDTO=djMaintenanceRecordResponsiblePartyMapper.queryGuaranteeMoneyDetail(id);
            if (responsiblePartyDetailDTO!=null)
            {
                responsiblePartyDetailDTO.setRetentionMoney(storefront.getRetentionMoney());//现有滞留金

                responsiblePartyDetailDTO.setPaidRetentionMoney((responsiblePartyDetailDTO.getTotalAmount() *
                        Double.parseDouble(responsiblePartyDetailDTO.getProportion())) / 100 - storefront.getRetentionMoney());//需交滞留金

                String workerTypeId = responsiblePartyDetailDTO.getWorkerTypeId();
                String workerTypeName = null;
                if (workerTypeId != null) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);//查询工种
                    workerTypeName = workerType != null ? workerType.getName() : "";
                    responsiblePartyDetailDTO.setWorkerTypeName(workerTypeName);//工种名称
                }
                StringBuffer str = new StringBuffer();
                str.append(responsiblePartyDetailDTO.getAddress() + "于").
                        append(responsiblePartyDetailDTO.getCreateDate()+ "申请了"+workerTypeName+"质保，经大管家实地勘察后，确认你责任占比")
                        .append(responsiblePartyDetailDTO.getProportion() + "%，您需要缴纳质保金后才能进行店铺的其他操作，有任何疑问请及时联系当家客服。");
                responsiblePartyDetailDTO.setContent(str.toString());//缴纳详情
                responsiblePartyDetailDTO.setNeedRetentionMoney(responsiblePartyDetailDTO.getTotalAmount() * Double.parseDouble(responsiblePartyDetailDTO.getProportion())/100);
                responsiblePartyDetailDTO.setProportion(responsiblePartyDetailDTO.getProportion()+"%");//责任占比
            }
            return ServerResponse.createBySuccess("查询成功", responsiblePartyDetailDTO);//所需质保金
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已解决
     *
     * @return
     */
    public ServerResponse resolved(String userToken, String remark,String houseId,String image,String id,String workerTypeSafeOrderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息
            String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
            House house = houseMapper.selectByPrimaryKey(houseId);

            DjMaintenanceRecordContent record =new DjMaintenanceRecordContent();
            record.setMaintenanceRecordId(id);
            record.setRemark(remark);
            record.setImage(image);
            record.setMemberId(member.getId());
            record.setType(1);

            //            configMessageService.addConfigMessage(AppType.SALE, member.getId(), "开工提醒",
//                    "您申请的维修已经解决【" + house.getHouseName() + "】", 0, url
//                            + Utils.getCustomerDetails(customer.getMemberId(), djAlreadyRobSingle1.get(0).getId(), 1, "4"));


            djMaintenanceRecordContentMapper.insertSelective(record);

            return ServerResponse.createBySuccessMessage("已解决");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * (自购金额确认)发送给业主
     *
     * @return
     */
    public ServerResponse sendingOwners(String userToken,String houseId,String remark,String enoughAmount) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息
            StringBuffer sb=new StringBuffer();
            sb.append(enoughAmount).append(",").append(remark);
            taskStackService.inserTaskStackInfo(houseId,member.getId(),"(自购金额确认)发送给业主",null,10,sb.toString());
            return ServerResponse.createBySuccessMessage("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("(自购金额确认)发送给业主异常");
        }
    }

    /**
     *管家审核维修
     * @param userToken
     * @param remark
     * @param houseId
     * @param image
     * @param id
     * @param state
     * @param workerTypeSafeOrderId
     * @return
     */
    public ServerResponse auditMaintenance(String userToken, String remark, String houseId, String image, String id, Integer state, String workerTypeSafeOrderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息
            DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(id);
            djMaintenanceRecord.setRemark(remark);
            djMaintenanceRecord.setHouseId(houseId);
            djMaintenanceRecord.setState(state);
            djMaintenanceRecord.setWorkerTypeSafeOrderId(workerTypeSafeOrderId);
            djMaintenanceRecord.setMemberId(member.getId());
            djMaintenanceRecord.setStewardState(2);//管家处理状态 1：待处理 2：已处理
            djMaintenanceRecord.setWorkerTypeId(null);
            int i=djMaintenanceRecordMapper.updateByPrimaryKey(djMaintenanceRecord);
            if(i<=0)
                return ServerResponse.createByErrorMessage("审核失败");
            return ServerResponse.createBySuccessMessage("审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("管家审核维修异常");
        }
    }


    /**
     *提交质保处理
     * @param userToken
     * @param remark
     * @param houseId
     * @param image
     * @param id
     * @param state
     * @param workerTypeSafeOrderId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse submitQualityAssurance(String userToken, String remark,
                                                 String houseId, String image,
                                                 String id, Integer state,
                                                 String productId,Double price,Double shopCount,
                                                 String workerTypeSafeOrderId) {
        try {
            //获取业主信息
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//业主信息

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            WorkerTypeSafeOrder workerTypeSafeOrder=workerTypeSafeOrderMapper.selectByPrimaryKey(id);
            Date createDate = workerTypeSafeOrder.getCreateDate();//人工保险订单的创建时间
            Date today = new Date(); // new Date()为获取当前系统时间//获取当前时间
            if(today.getTime()>createDate.getTime())
            {
                   //质保过期
                return ServerResponse.createByErrorMessage("质保过期");
            }

            //维保商品
            DjMaintenanceRecordProduct djMaintenanceRecordProduct=new DjMaintenanceRecordProduct();
            djMaintenanceRecordProduct.setProductId(productId);
            djMaintenanceRecordProduct.setMaintenanceRecordId(id);
            djMaintenanceRecordProduct.setShopCount(shopCount);
            djMaintenanceRecordProduct.setPrice(price);
            djMaintenanceRecordProductMapper.insertSelective(djMaintenanceRecordProduct);
            // 定则
            return ServerResponse.createBySuccessMessage("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交质保处理异常");
        }
    }
}

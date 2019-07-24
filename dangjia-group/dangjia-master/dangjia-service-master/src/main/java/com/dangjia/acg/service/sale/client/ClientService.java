package com.dangjia.acg.service.sale.client;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.sale.client.OrdersCustomerDTO;
import com.dangjia.acg.dto.sale.client.SaleClueDTO;
import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.dto.sale.store.MonthlyTargetDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialBuildingMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialRangeMapper;
import com.dangjia.acg.mapper.sale.stroe.MonthlyTargetMappper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/20
 * Time: 10:01
 */
@Service
public class ClientService {
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private ClueTalkMapper clueTalkMapper;
    @Autowired
    private MonthlyTargetMappper monthlyTargetMappper;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private IModelingVillageMapper iModelingVillageMapper;
    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;

    /**
     * 录入客户
     * @param clue
     * @param userToken
     * @return
     */
    public ServerResponse enterCustomer(Clue clue,String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        Clue groupBy = clueMapper.getGroupBy(clue.getPhone(), user.getId());
        if(null!=groupBy){//如果客户已录入过则把录入的房子变为意向房子
            groupBy.setAddress(groupBy.getAddress()+","+clue.getAddress());
            clueMapper.updateByPrimaryKeySelective(groupBy);
            return ServerResponse.createBySuccessMessage("提交成功,已存在该线索录入为意向房子");
        }else {
            groupBy = clueMapper.getGroupBy(clue.getPhone(), null);
            if(DateUtil.getDiffDays(new Date(),groupBy.getReportDate())<7){
                return ServerResponse.createBySuccess("该客户已被报备,剩余时间",groupBy.getReportDate());
            }
        }
        clue.setStage(0);
        clue.setDataStatus(0);
        clue.setUserId(user.getId());
        if(clueMapper.insert(clue)>0){
            return ServerResponse.createBySuccessMessage("提交成功");
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }


    /**
     * 编辑客户
     * @param clue
     * @return
     */
    public ServerResponse updateCustomer(Clue clue) {
        if(clueMapper.updateByPrimaryKeySelective(clue)>0){
            return ServerResponse.createBySuccessMessage("提交成功");
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }


    /**
     * 客户页
     * @param userToken
     * @return
     */
    public ServerResponse clientPage(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        Map<String, Object> map=new HashedMap();
        map.put("跟进列表",clueMapper.clientPage("0",user.getId()));
        map.put("已下单客户",clueMapper.clientPage("1",user.getId()));
        map.put("已竣工客户",clueMapper.clientPage("2",user.getId()));
        MonthlyTargetDTO monthlyTargetDTO=new MonthlyTargetDTO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String date=dateFormat.format(new Date());
        monthlyTargetDTO.setModifyDate(date);
        monthlyTargetDTO.setComplete(clueMapper.Complete(user.getId(),date));
        Example example=new Example(MonthlyTarget.class);
        example.createCriteria().andEqualTo(MonthlyTarget.USER_ID,user.getId()).andCondition("date_format(h.modify_date,'%Y-%m')= "+date);
        List<MonthlyTarget> monthlyTargets = monthlyTargetMappper.selectByExample(example);
        monthlyTargetDTO.setTargetNumber(monthlyTargets.size()>0?monthlyTargets.get(0).getTargetNumber():0);
        map.put("每月目标",monthlyTargetDTO);
        example=new Example(ResidentialRange.class);
        example.createCriteria().andEqualTo(ResidentialRange.USER_ID,user.getId());
        List<ResidentialRange> residentialRanges = residentialRangeMapper.selectByExample(example);
        example=new Example(ModelingVillage.class);
        List<ResidentialRangeDTO> list=new ArrayList<>();
        for (ResidentialRange residentialRange : residentialRanges) {
            ResidentialRangeDTO residentialRangeDTO=new ResidentialRangeDTO();
            residentialRangeDTO.setVillageId(residentialRange.getVillageId());
            residentialRangeDTO.setVillagename(iModelingVillageMapper.selectByPrimaryKey(residentialRange.getVillageId()).getName());
            String[] buildingId = residentialRange.getBuildingId().split(",");
            example=new Example(ResidentialBuilding.class);
            example.createCriteria().andIn(ResidentialBuilding.ID, Arrays.asList(buildingId));
            residentialRangeDTO.setList(residentialBuildingMapper.selectByExample(example));
            list.add(residentialRangeDTO);
        }
        map.put("外场销售范围",list);
       return ServerResponse.createBySuccess("查询成功",map);
    }


    /**
     * 跟进列表
     * @param userToken
     * @param label
     * @param pageDTO
     * @param time
     * @param stage
     * @return
     */
    public ServerResponse followList(String userToken, PageDTO pageDTO,String label, String time, Integer stage , String searchKey) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member user = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Clue> clues = clueMapper.followList(label, time, stage, searchKey ,user.getId());
            PageInfo pageResult = new PageInfo(clues);
            List<SaleClueDTO> list=new ArrayList<>();
            for (Clue clue : clues) {
                SaleClueDTO saleClueDTO=new SaleClueDTO();
                String[] labelIds = clue.getLabelId().split(",");
                List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                saleClueDTO.setId(clue.getId());
                saleClueDTO.setOwername(clue.getOwername());
                saleClueDTO.setPhone(clue.getPhone());
                saleClueDTO.setReportDate(clue.getReportDate());
                saleClueDTO.setCreateDate(clue.getCreateDate());
                saleClueDTO.setModifyDate(clue.getModifyDate());
                saleClueDTO.setList(labelByIds);
                Date maxDate = clueTalkMapper.getMaxDate(clue.getId());
                saleClueDTO.setCommunicationDate(null!=maxDate?maxDate : null);
                list.add(saleClueDTO);
            }
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 已下单竣工列表
     * @param userToken
     * @param pageDTO
     * @param visitState
     * @param time
     * @param searchKey
     * @return
     */
    public ServerResponse ordersCustomer( String userToken,String visitState,PageDTO pageDTO,String searchKey, String time) {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member user = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrdersCustomerDTO> ordersCustomerDTOS = clueMapper.ordersCustomer(user.getId(), visitState, searchKey, time);
            PageInfo pageResult = new PageInfo(ordersCustomerDTOS);
            return ServerResponse.createBySuccess("查询成功",pageResult);
    }
}

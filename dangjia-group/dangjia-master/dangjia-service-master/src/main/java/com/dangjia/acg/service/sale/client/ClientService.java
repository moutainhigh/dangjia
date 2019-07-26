package com.dangjia.acg.service.sale.client;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.controller.user.MainUserController;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.sale.client.OrdersCustomerDTO;
import com.dangjia.acg.dto.sale.client.SaleClueDTO;
import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.dto.sale.store.MonthlyTargetDTO;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialBuildingMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialRangeMapper;
import com.dangjia.acg.mapper.sale.stroe.MonthlyTargetMappper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.user.MainUser;
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
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 录入客户
     * @param clue
     * @param userToken
     * @return
     */
    public ServerResponse enterCustomer(Clue clue,String userToken) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, user.getId())
                .andEqualTo(Store.DATA_STATUS, 0);
        List<Store> storeList = iStoreMapper.selectByExample(example);
        if (storeList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        Store store = storeList.get(0);
        Clue groupBy = clueMapper.getGroupBy(clue.getPhone(), user.getId());
        if(null!=groupBy){//如果客户已录入过则把录入的房子变为意向房子
            groupBy.setAddress(groupBy.getAddress()+","+clue.getAddress());
            clueMapper.updateByPrimaryKeySelective(groupBy);
            return ServerResponse.createBySuccessMessage("提交成功,已存在该线索录入为意向房子");
        }else {
            groupBy = clueMapper.getGroupBy(clue.getPhone(), null);
            if(null!=groupBy&&DateUtil.getDiffDays(new Date(),groupBy.getReportDate())<7){
                return ServerResponse.createBySuccess("该客户已被报备,剩余时间",groupBy.getReportDate());
            }
        }
        clue.setStage(0);
        clue.setDataStatus(0);
        clue.setUserId(user.getId());
        clue.setStoreId(store.getId());
        clue.setCusService(user.getId());
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
    public ServerResponse clientPage(String userToken,String storeId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        Map<String, Object> map=new HashedMap();
        Example example=new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID,user.getId());
        if(iStoreMapper.selectByExample(example).size()<=0) {//判断用户是否为店长
            map.put("followList", clueMapper.clientPage("0", user.getId(),null));
            map.put("placeOrder", clueMapper.clientPage("1", user.getId(),null));
            map.put("completion", clueMapper.clientPage("2", user.getId(),null));
            MonthlyTargetDTO monthlyTargetDTO = new MonthlyTargetDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            String date = dateFormat.format(new Date());
            monthlyTargetDTO.setModifyDate(date);
            monthlyTargetDTO.setComplete(clueMapper.Complete(user.getId(), date));
            example = new Example(MonthlyTarget.class);
            example.createCriteria().andEqualTo(MonthlyTarget.USER_ID, user.getId()).andCondition("date_format(modify_date,'%Y-%m')= " + date);
            List<MonthlyTarget> monthlyTargets = monthlyTargetMappper.selectByExample(example);
            monthlyTargetDTO.setTargetNumber(monthlyTargets.size() > 0 ? monthlyTargets.get(0).getTargetNumber() : 0);
            map.put("monthlyTarget", monthlyTargetDTO);
            example = new Example(ResidentialRange.class);
            example.createCriteria().andEqualTo(ResidentialRange.USER_ID, user.getId());
            List<ResidentialRange> residentialRanges = residentialRangeMapper.selectByExample(example);
            List<ResidentialRangeDTO> residentialRangeDTOList=new ArrayList<>();
            for (ResidentialRange residentialRange : residentialRanges) {
                ResidentialRangeDTO residentialRangeDTO = new ResidentialRangeDTO();
                String[] buildingId = residentialRange.getBuildingId().split(",");
                example = new Example(ResidentialBuilding.class);
                example.createCriteria().andIn(ResidentialBuilding.ID, Arrays.asList(buildingId));
                ModelingVillage modelingVillage = iModelingVillageMapper.selectByPrimaryKey(residentialRange.getVillageId());
                residentialRangeDTO.setVillageId(modelingVillage.getId());
                residentialRangeDTO.setVillagename(modelingVillage.getName());
                residentialRangeDTO.setList(residentialBuildingMapper.selectByExample(example));
                residentialRangeDTOList.add(residentialRangeDTO);
            }
            map.put("outField", residentialRangeDTOList);
        }else{
            List<StoreUserDTO> storeUsers = iStoreUserMapper.getStoreUsers(storeId, null,null);
            map.put("followList", clueMapper.clientPage("0", null,storeUsers));
            map.put("placeOrder", clueMapper.clientPage("1", null,storeUsers));
            map.put("completion", clueMapper.clientPage("2", null,storeUsers));
            map.put("sleepingCustomer",clueMapper.sleepingCustomer(storeUsers));

        }
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
            Object object = constructionService.getAccessToken(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            AccessToken accessToken = (AccessToken) object;
            if (CommonUtil.isEmpty(accessToken.getUserId())) {
                return ServerResponse.createbyUserTokenError();
            }
            MainUser user = userMapper.getNameById(accessToken.getUserId());
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Clue> clues = clueMapper.followList(label, time, stage, searchKey ,user.getId());
            PageInfo pageResult = new PageInfo(clues);
            List<SaleClueDTO> list=new ArrayList<>();
            for (Clue clue : clues) {
                SaleClueDTO saleClueDTO=new SaleClueDTO();
                if(!CommonUtil.isEmpty(clue.getLabelId())) {
                    String[] labelIds = clue.getLabelId().split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    saleClueDTO.setList(labelByIds);
                }
                saleClueDTO.setId(clue.getId());
                saleClueDTO.setOwername(clue.getOwername());
                saleClueDTO.setPhone(clue.getPhone());
                saleClueDTO.setReportDate(clue.getReportDate());
                saleClueDTO.setCreateDate(clue.getCreateDate());
                saleClueDTO.setModifyDate(clue.getModifyDate());
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
            Object object = constructionService.getAccessToken(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            AccessToken accessToken = (AccessToken) object;
            if (CommonUtil.isEmpty(accessToken.getUserId())) {
                return ServerResponse.createbyUserTokenError();
            }
            MainUser user = userMapper.getNameById(accessToken.getUserId());
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrdersCustomerDTO> ordersCustomerDTOS = clueMapper.ordersCustomer(user.getId(), visitState, searchKey, time);
            PageInfo pageResult = new PageInfo(ordersCustomerDTOS);
            return ServerResponse.createBySuccess("查询成功",pageResult);
    }
}

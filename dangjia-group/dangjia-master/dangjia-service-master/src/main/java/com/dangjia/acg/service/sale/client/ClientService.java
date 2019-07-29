package com.dangjia.acg.service.sale.client;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.GaoDeUtils;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.sale.client.CustomerIndexDTO;
import com.dangjia.acg.dto.sale.client.OrdersCustomerDTO;
import com.dangjia.acg.dto.sale.client.SaleClueDTO;
import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.dto.sale.store.MonthlyTargetDTO;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialBuildingMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialRangeMapper;
import com.dangjia.acg.mapper.sale.stroe.MonthlyTargetMappper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.clue.ClueTalk;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
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
    @Autowired
    private SaleService saleService;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private ICustomerRecordMapper iCustomerRecordMapper;

    /**
     * 录入客户
     *
     * @param clue
     * @param userToken
     * @return
     */
    public ServerResponse enterCustomer(Clue clue, String userToken) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        Clue groupBy = clueMapper.getGroupBy(clue.getPhone(), null);
        if (null != groupBy && DateUtil.getDiffDays(new Date(), groupBy.getReportDate()) < 7) {
            return ServerResponse.createBySuccess("该客户已被报备,剩余时间", groupBy.getReportDate());
        }
        //如果客户已录入过则把录入的房子变为意向房子
        groupBy = clueMapper.getGroupBy(clue.getPhone(), user.getId());
        if (null != groupBy) {
            if (!groupBy.getAddress().contains(clue.getAddress())) {
                groupBy.setAddress(groupBy.getAddress() + "," + clue.getAddress());
                clueMapper.updateByPrimaryKeySelective(groupBy);
                return ServerResponse.createBySuccessMessage("提交成功,已存在该线索录入为意向房子");
            } else {
                return ServerResponse.createByErrorMessage("请勿重复录入");
            }
        }
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo(Member.MOBILE, clue.getPhone());
        List<Member> members = iMemberMapper.selectByExample(example);
        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;
        if (members.size() > 0) {
            //有沟通记录
            List<ClueTalk> clueTalkList = clueTalkMapper.getTalkByClueId(clue.getId());
            if (clueTalkList.size() != 0) {
                Date date = clueTalkMapper.getMaxDate(clue.getId());
                for (ClueTalk clueTalk : clueTalkList) {
                    //操作dj_member_customer_record表
                    CustomerRecord customerRecord = new CustomerRecord();
                    customerRecord.setUserId(clue.getCusService());
                    customerRecord.setMemberId(members.get(0).getId());
                    customerRecord.setDescribes(clueTalk.getTalkContent());
                    //customerRecord.setModifyDate(clueTalk.getModifyDate());
                    customerRecord.setCreateDate(clueTalk.getCreateDate());
                    iCustomerRecordMapper.insert(customerRecord);
                    //操作dj_member_customer表
                    if (date.compareTo(clueTalk.getModifyDate()) == 0) {
                        Customer customer = new Customer();
                        customer.setUserId(clue.getCusService());
                        customer.setMemberId(members.get(0).getId());
                        customer.setCurrRecordId(customerRecord.getId());
                        //customer.setModifyDate(clueTalk.getModifyDate());
                        customer.setCreateDate(clueTalk.getCreateDate());
                        customer.setStage(1);
                        customer.setStoreId(clue.getStoreId());
                        iCustomerMapper.insert(customer);
                    }
                    //改变线索沟通表的数据状态
                    clueTalk.setDataStatus(1);
                    clueTalkMapper.updateByPrimaryKeySelective(clueTalk);
                }
            } else {
                Customer customer = new Customer();
                customer.setUserId(clue.getCusService());
                customer.setMemberId(members.get(0).getId());
                //customer.setModifyDate(clueTalk.getModifyDate());
                customer.setCreateDate(clue.getCreateDate());
                customer.setStage(1);
                customer.setStoreId(clue.getStoreId());
                iCustomerMapper.insert(customer);
            }
            //改变线索表的数据状态
            clue.setDataStatus(0);
            clue.setStoreId(store.getId());
            clue.setCusService(user.getId());
            clue.setClueType(0);
            clue.setStage(4);
            clue.setMemberId(members.get(0).getId());
            clueMapper.insert(clue);
            if (clueMapper.insert(clue) > 0) {
                return ServerResponse.createBySuccessMessage("提交成功");
            }
        } else {
            clue.setStage(0);
            clue.setDataStatus(0);
            clue.setStoreId(store.getId());
            clue.setCusService(user.getId());
            clue.setClueType(0);
            if (clueMapper.insert(clue) > 0) {
                return ServerResponse.createBySuccessMessage("提交成功");
            }
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }


    /**
     * 跨域下单
     *
     * @param clue
     * @param userToken
     * @param cityId
     * @return
     */
    public ServerResponse crossDomainOrder(Clue clue, String userToken, String cityId, String villageId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        Clue groupBy = clueMapper.getGroupBy(clue.getPhone(), user.getId());
        if (null != groupBy) {
            groupBy.setAddress(groupBy.getAddress() + "," + clue.getAddress());
            clueMapper.updateByPrimaryKeySelective(groupBy);
            return ServerResponse.createBySuccessMessage("提交成功,已存在该线索录入为意向房子");
        } else {
            Example example = new Example(Store.class);
            example.createCriteria().andEqualTo(Store.CITY_ID, cityId);
            List<Store> stores = iStoreMapper.selectByExample(example);
            ModelingVillage modelingVillage = iModelingVillageMapper.selectByPrimaryKey(villageId);
            for (Store store : stores) {
                //如果转出的该客户为自己门店范围内未录入的（野生），则记录进店长的线索阶段（给店长系统推送），然后仅分配给内场
                if (GaoDeUtils.isInPolygon(modelingVillage.getLocationx() + "," + modelingVillage.getLocationy(), store.getScopeItude())) {
                    clue.setCusService(store.getUserId());
                    clue.setStoreId(store.getId());
                    clue.setClueType(1);
                    clueMapper.insert(clue);
                    break;
                } else {

                }
            }
        }
        return null;
    }

    /**
     * 编辑客户
     *
     * @param clue
     * @return
     */
    public ServerResponse updateCustomer(Clue clue) {
        if (clueMapper.updateByPrimaryKeySelective(clue) > 0) {
            return ServerResponse.createBySuccessMessage("提交成功");
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }


    /**
     * 客户页
     *
     * @param userToken
     * @return
     */
    public ServerResponse clientPage(String userToken) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        Map<String, Object> map = new HashedMap();
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, user.getId());
        if (iStoreMapper.selectByExample(example).size() <= 0) {//判断用户是否为店长
            map.put("followList", clueMapper.clientPage("0", user.getId(), null));
            map.put("placeOrder", clueMapper.clientPage("1", user.getId(), null));
            map.put("completion", clueMapper.clientPage("2", user.getId(), null));
            MonthlyTargetDTO monthlyTargetDTO = new MonthlyTargetDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            String date = dateFormat.format(new Date());
            monthlyTargetDTO.setModifyDate(date);
            monthlyTargetDTO.setComplete(clueMapper.Complete(user.getId(), date));
            List<MonthlyTarget> monthlyTargets = getMonthlyTargetList(user.getId());
            monthlyTargetDTO.setTargetNumber(monthlyTargets.size() > 0 ? monthlyTargets.get(0).getTargetNumber() : 0);
            map.put("monthlyTarget", monthlyTargetDTO);
            map.put("outField", getResidentialRangeDTOList(user.getId()));
        } else {
            object = saleService.getStore(accessToken.getUserId());
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Store store = (Store) object;
            List<StoreUserDTO> storeUsers = iStoreUserMapper.getStoreUsers(store.getId(), null, null);
            map.put("followList", clueMapper.clientPage("0", null, storeUsers));
            map.put("placeOrder", clueMapper.clientPage("1", null, storeUsers));
            map.put("completion", clueMapper.clientPage("2", null, storeUsers));
            List<CustomerIndexDTO> customerIndexDTOS = clueMapper.sleepingCustomer(store.getId(), null, "desc", null);
            map.put("sleepingCustomer", customerIndexDTOS.size() > 0 ? customerIndexDTOS.get(0) : null);
            List<CustomerIndexDTO> customerIndexDTOS1 = iCustomerMapper.waitDistribution(user.getId(), null, "desc");
            map.put("waitDistribution", customerIndexDTOS1.size() > 0 ? customerIndexDTOS1.get(0) : null);
            map.put("storeId", store.getId());
            map.put("grabSheet", iCustomerMapper.grabSheet(store.getId()));
        }
        return ServerResponse.createBySuccess("查询成功", map);
    }

    /**
     * 获取当前月份的目标
     *
     * @param userId
     * @return
     */
    public List<MonthlyTarget> getMonthlyTargetList(String userId) {
        Example example = new Example(MonthlyTarget.class);
        example.createCriteria()
                .andEqualTo(MonthlyTarget.USER_ID, userId)
                .andBetween(MonthlyTarget.TARGET_DATE, DateUtil.getTimesMonthmorning(), DateUtil.getTimesMonthnight());
        return monthlyTargetMappper.selectByExample(example);
    }

    /**
     * 获取销售范围
     *
     * @param userId
     * @return
     */
    public List<ResidentialRangeDTO> getResidentialRangeDTOList(String userId) {
        Example example = new Example(ResidentialRange.class);
        example.createCriteria().andEqualTo(ResidentialRange.USER_ID, userId);
        List<ResidentialRange> residentialRanges = residentialRangeMapper.selectByExample(example);
        List<ResidentialRangeDTO> residentialRangeDTOList = new ArrayList<>();
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
        return residentialRangeDTOList;
    }

    /**
     * 跟进列表
     *
     * @param userToken
     * @param label
     * @param pageDTO
     * @param time
     * @param stage
     * @return
     */
    public ServerResponse followList(String userToken, PageDTO pageDTO, String label, String time, Integer stage, String searchKey) {
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
            List<Clue> clues = clueMapper.followList(label, time, stage, searchKey, user.getId());
            List<SaleClueDTO> list = new ArrayList<>();
            for (Clue clue : clues) {
                SaleClueDTO saleClueDTO = new SaleClueDTO();
                if (!CommonUtil.isEmpty(clue.getLabelId())) {
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
                saleClueDTO.setCommunicationDate(null != maxDate ? maxDate : null);
                list.add(saleClueDTO);
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 已下单竣工列表
     *
     * @param userToken
     * @param pageDTO
     * @param visitState
     * @param time
     * @param searchKey
     * @return
     */
    public ServerResponse ordersCustomer(String userToken, String visitState, PageDTO pageDTO, String searchKey, String time, Integer type, String userId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<OrdersCustomerDTO> ordersCustomerDTOS = new ArrayList<>();
        if (!CommonUtil.isEmpty(visitState)) {
            Example example = new Example(Store.class);
            example.createCriteria().andEqualTo(Store.USER_ID, user.getId());
            if (iStoreMapper.selectByExample(example).size() <= 0) {
                ordersCustomerDTOS = clueMapper.ordersCustomer(user.getId(), visitState, searchKey, time, null, null);
            } else {
                ordersCustomerDTOS = clueMapper.ordersCustomer(null, visitState, searchKey, time, store.getId(), userId);
            }
        } else {
            List<CustomerIndexDTO> customerIndexDTOS = new ArrayList<>();
            if (null != type && type == 1) {//待分配客户
                customerIndexDTOS = iCustomerMapper.waitDistribution(user.getId(), searchKey, time);
            }
            if (null != type && type == 2) {//沉睡客户
                customerIndexDTOS = clueMapper.sleepingCustomer(store.getId(), searchKey, time, userId);
            }
            for (CustomerIndexDTO customerIndexDTO : customerIndexDTOS) {
                OrdersCustomerDTO ordersCustomerDTO = new OrdersCustomerDTO();
                if (!CommonUtil.isEmpty(customerIndexDTO.getLabelIdArr())) {
                    String[] labelIds = customerIndexDTO.getLabelIdArr().split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    ordersCustomerDTO.setList(labelByIds);
                }
                ordersCustomerDTO.setMemberId(customerIndexDTO.getId());
                ordersCustomerDTO.setMobile(customerIndexDTO.getPhone());
                ordersCustomerDTO.setName(customerIndexDTO.getName());
                ordersCustomerDTO.setCreateDate(customerIndexDTO.getCreateDate());
                ordersCustomerDTO.setModifyDate(customerIndexDTO.getModifyDate());
                ordersCustomerDTO.setUserName(customerIndexDTO.getUserName());
                ordersCustomerDTOS.add(ordersCustomerDTO);
            }
        }
        PageInfo pageResult = new PageInfo(ordersCustomerDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }
}

package com.dangjia.acg.service.clue;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.common.util.excel.ImportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.other.ClueDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.user.UserRoleMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.clue.ClueTalk;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberLabel;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.user.UserRoleKey;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ClueService {

    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private ClueTalkMapper clueTalkMapper;
    @Autowired
    private ICustomerRecordMapper iCustomerRecordMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IStoreUserMapper iStoreUserMapper;

    /**
     * 获取所有线索
     *
     * @return
     */
    public ServerResponse getAll(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Clue> clues = clueMapper.getAll();
            if (clues == null || clues.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(clues);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询线索list
     */
    public ServerResponse getClueList(Integer stage, String values, String memberId, String childId, String beginDate,
                                      String endDate, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(Clue.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Clue.DATA_STATUS, 0);
            if (stage != null) {
                criteria.andEqualTo(Clue.STAGE, stage);
            } else {
                criteria.andCondition(" stage IN (0,1,2,3) ");
            }
            if (!CommonUtil.isEmpty(values)) {
                criteria.andCondition(" CONCAT(owername,phone,wechat,address) like CONCAT('%','" + values + "','%')");
            }
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
                criteria.andBetween(Clue.CREATE_DATE, beginDate, endDate);
            }
            if (!CommonUtil.isEmpty(memberId)) {
                criteria.andEqualTo(Clue.CUS_SERVICE, memberId);
            }
            if (!CommonUtil.isEmpty(childId)) {
                criteria.andEqualTo(Clue.LABEL_ID, childId);
            }
            
            criteria.andEqualTo(Clue.PHASE_STATUS, 0);
            criteria.andEqualTo(Clue.TURN_STATUS,0);
            example.orderBy(Clue.MODIFY_DATE).desc();
            List<Clue> clues = clueMapper.selectByExample(example);
            if (clues == null || clues.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(clues);
            List<ClueDTO> clueDTOList = new ArrayList<>();
            for (Clue c : clues) {
                ClueDTO clueDTO = new ClueDTO();
                clueDTO.setClueId(c.getId());
                clueDTO.setPhaseStatus(c.getPhaseStatus());
                BeanUtils.beanToBean(c, clueDTO);
                if (c.getLabelId() != null) {
                    String[] split = c.getLabelId().split(",");
                    StringBuilder sb = new StringBuilder();
                    String ids[][] = new String[split.length][2];
                    for (int i = 0; i < split.length; i++) {
                        MemberLabel memberLabel = iMemberLabelMapper.selectByPrimaryKey(split[i]);
                        if(null != memberLabel){
                            sb.append(memberLabel.getParentName()).append("-").append(memberLabel.getName()).append(",");
                            ids[i][0] = memberLabel.getParentId();
                            ids[i][1] = memberLabel.getId();
                        }
                    }
                    sb.delete(sb.lastIndexOf(","), sb.length());
                    clueDTO.setLabelName(sb.toString());
                    clueDTO.setLabelIds(ids);
                }
                clueDTOList.add(clueDTO);
            }
            pageResult.setList(clueDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 添加线索
     */

    public int addClue(Clue clue) {
        return clueMapper.insert(clue);

    }

    /**
     * 通过阶段状态查询线索
     */
    public ServerResponse getByStage(int stage, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Clue> clues = clueMapper.getByStage(stage);
            if (clues == null || clues.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(clues);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    public ServerResponse updateCus(String cusService, String phone, String chat, String userId, String childId, String id) {
        try {
            Clue clue = clueMapper.selectByPrimaryKey(id);
            if (!CommonUtil.isEmpty(cusService)) {
                Example example = new Example(UserRoleKey.class);
                example.createCriteria().andEqualTo(UserRoleKey.USER_ID, userId);
                List<UserRoleKey> userRoleKeys = userRoleMapper.selectByExample(example);
                boolean flage = false;
                for (UserRoleKey u : userRoleKeys) {
                    if ("668854901553829215229".equals(u.getRoleId())) {
                        clue.setCusService(cusService);
                        flage = true;
                        break;
                    }
                }
                if (!flage) {
                    return ServerResponse.createByErrorMessage("您暂无权限变更");
                }
            }
            if (!CommonUtil.isEmpty(phone) && Validator.isMobileNo(phone)) {
                clue.setPhone(phone);
            }
            if (!CommonUtil.isEmpty(chat)) {
                clue.setWechat(chat);
            }
            if (!CommonUtil.isEmpty(childId)) {
                clue.setLabelId(childId);
            }
            clueMapper.updateByPrimaryKeySelective(clue);
            return ServerResponse.createBySuccessMessage("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新失败");
        }

    }

    /**
     * exl表格导入线索
     */
    public ServerResponse importExcelClue(String userId, MultipartFile file) {
        try {

            ImportExcel clue = new ImportExcel(file, 0, 0);
            List<Clue> clueList = clue.getDataList(Clue.class, 0);
            int num = 0;
            for (Clue c : clueList) {
                if (Validator.isMobileNo(c.getPhone())) {
                    c.setCusService(userId);
                    c.setStage(0);
                    List<Clue> clues = clueMapper.getByPhone(c.getPhone());
                    for (Clue clue1 : clues) {
                        Member member = iMemberMapper.getByPhone(c.getPhone());
                        //表示从来没有过线索和注册过
                        if (clue1 == null && member == null) {
                            clueMapper.insert(c);
                            num++;
                        }
                    }
                }
            }
            if (num > 0) {
                return ServerResponse.createBySuccessMessage("导入成功");
            } else {
                return ServerResponse.createByErrorMessage("导入失败,请检查数据格式");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("生成失败");
        }

    }

    /**
     * 放弃跟进/加入黑名单
     */
    public ServerResponse giveUp(String id, int type) {
        try {
            Clue clue = clueMapper.selectByPrimaryKey(id);
            if (type == 2) {
                clue.setStage(2);
                clue.setCusService("");
            } else {
                clue.setStage(3);
            }
            clueMapper.updateByPrimaryKeySelective(clue);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 转客户
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse sendUser(String id, String phone, String longitude, String latitude) {
        try {
            //线索阶段手机号码会给多个销售人员录入   2019/8/9 销售端线索可为多个销售人员录入 所以应用list
            List<Clue> clues = clueMapper.getByPhone(phone);
            //表示线索表存在线索
            if(clues.size()>0) {
                for (Clue clue : clues) {
                    if (clue.getStage() == 0 || clue.getStage() == 1) {
                        //有沟通记录
                        List<ClueTalk> clueTalkList = clueTalkMapper.getTalkByClueId(clue.getId());
                        if (clueTalkList.size() != 0) {
                            Date date = clueTalkMapper.getMaxDate(clue.getId());
                            for (ClueTalk clueTalk : clueTalkList) {
                                //操作dj_member_customer_record表
                                CustomerRecord customerRecord = new CustomerRecord();
                                customerRecord.setUserId(clue.getCusService());
                                customerRecord.setMemberId(id);
                                customerRecord.setDescribes(clueTalk.getTalkContent());
                                //customerRecord.setModifyDate(clueTalk.getModifyDate());
                                customerRecord.setCreateDate(clueTalk.getCreateDate());
                                iCustomerRecordMapper.insert(customerRecord);
                                //操作dj_member_customer表
                                if (date.compareTo(clueTalk.getModifyDate()) == 0) {
                                    Customer customer = new Customer();
                                    customer.setUserId(clue.getCusService());
                                    customer.setMemberId(id);
                                    customer.setCurrRecordId(customerRecord.getId());
                                    //customer.setModifyDate(clueTalk.getModifyDate());
                                    customer.setCreateDate(clueTalk.getCreateDate());
                                    customer.setStage(1);
                                    customer.setPhaseStatus(1);
                                    customer.setStoreId(clue.getStoreId());
                                    customer.setTips("1");
                                    customer.setLabelIdArr(clue.getLabelId());
                                    iCustomerMapper.insert(customer);
                                }
                                //改变线索沟通表的数据状态
                                clueTalk.setDataStatus(1);
                                clueTalkMapper.updateByPrimaryKeySelective(clueTalk);
                            }
                        } else {
                            Customer customer = new Customer();
                            customer.setUserId(clue.getCusService());
                            customer.setMemberId(id);
                            //customer.setModifyDate(clueTalk.getModifyDate());
                            customer.setCreateDate(clue.getCreateDate());
                            customer.setStage(1);
                            customer.setPhaseStatus(1);
                            customer.setStoreId(clue.getStoreId());
                            customer.setTips("1");
                            customer.setLabelIdArr(clue.getLabelId());
                            iCustomerMapper.insert(customer);
                        }
                        //改变线索表的数据状态
//                clue.setDataStatus(1);
                        clue.setStage(1);
                        clue.setMemberId(id);
                        clue.setPhaseStatus(1);
                        clueMapper.updateByPrimaryKeySelective(clue);
                    }
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }
            Clue clue=new Clue();
            clue.setStage(0);
            clue.setDataStatus(0);
            clue.setClueType(0);
            clue.setTurnStatus(0);
            clue.setPhaseStatus(1);
            clue.setPhone(phone);
            clue.setMemberId(id);
            clueMapper.insert(clue);
            Customer customer = new Customer();
            customer.setMemberId(id);
            customer.setDataStatus(0);
            customer.setStage(0);
            customer.setPhaseStatus(1);
            iCustomerMapper.insert(customer);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    public ServerResponse addH5Clue(String userId, String phone) {
        Example example = new Example(Clue.class);
        example.createCriteria().andEqualTo(Clue.CUS_SERVICE, userId).andEqualTo(Clue.PHONE, phone);
        List<Clue> clueList = clueMapper.selectByExample(example);
        if (clueList.size() > 0) {
            return ServerResponse.createByErrorMessage("线索已存在");
        }
        example=new Example(StoreUser.class);
        example.createCriteria().andEqualTo(StoreUser.USER_ID,userId)
                .andEqualTo(StoreUser.DATA_STATUS,0);
        List<StoreUser> storeUsers = iStoreUserMapper.selectByExample(example);
        if(!storeUsers.isEmpty()){
            Clue clue = new Clue();
            clue.setCusService(userId);
            clue.setPhone(phone);
            clue.setPhaseStatus(0);
            clue.setStoreId(storeUsers.get(0).getStoreId());
            clue.setStage(0);
            clue.setDataStatus(0);
            clue.setClueType(0);
            clue.setTurnStatus(0);
            Store store = iStoreMapper.selectByPrimaryKey(storeUsers.get(0).getStoreId());
            if(null==store){
                return ServerResponse.createByErrorMessage("该门店不存在");
            }
            clue.setCityId(store.getCityId());
            clueMapper.insert(clue);
            return ServerResponse.createBySuccessMessage("操作成功");
        }
        return ServerResponse.createByErrorMessage("操作失败");
    }
}



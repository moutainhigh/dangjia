package com.dangjia.acg.service.clue;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.common.util.excel.ImportExcel;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.clue.ClueTalk;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

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

    /**
     * 获取所有线索
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse getAll(Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Clue> clues = clueMapper.getAll();
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
    public ServerResponse getClueList(Integer stage, String values,String memberId,String beginDate,String endDate, Integer pageNum, Integer pageSize) {
        try {
            Example example = new Example(Clue.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Clue.DATA_STATUS, 0);
            if (stage != null) {
                criteria.andEqualTo(Clue.STAGE, stage);
            } else {
                criteria.andCondition(" stage IN (0,1) ");
            }
            //criteria.andCondition(" stage IN (0,1) ");
            if (!CommonUtil.isEmpty(values)) {
                criteria.andCondition(" CONCAT(owername,phone,wechat,address) like CONCAT('%','" + values + "','%')");
            }
            if(beginDate!=null && beginDate!="" && endDate!=null && endDate!=""){
                if(beginDate.equals(endDate)){
                    beginDate=beginDate+" "+"00:00:00";
                    endDate=endDate+" "+"23:59:59";
                }
                criteria.andBetween(Clue.CREATE_DATE,beginDate,endDate);
            }
            if(memberId!=null && memberId!=""){
                criteria.andEqualTo(Clue.CUS_SERVICE,memberId);
            }
            example.orderBy(Clue.MODIFY_DATE).desc();
            PageHelper.startPage(pageNum, pageSize);
            List<Clue> clues = clueMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(clues);
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
    public ServerResponse getByStage(int stage, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Clue> clues = clueMapper.getByStage(stage);
            PageInfo pageResult = new PageInfo(clues);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    public ServerResponse updateCus(String cusService, String id) {
        try {
            Clue clue = new Clue();
            clue.setId(id);
            clue.setCusService(cusService);
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
            int num=0;
            for (Clue c : clueList) {
                if (Validator.isMobileNo(c.getPhone())) {
                    c.setCusService(userId);
                    Clue clue1 = clueMapper.getByPhone(c.getPhone());
                    Member member = iMemberMapper.getByPhone(c.getPhone());
                    //表示从来没有过线索和注册过
                    if (clue1 == null && member == null) {
                        clueMapper.insert(c);
                        num++;
                    }
                }
            }
            if(num>0){
                return ServerResponse.createBySuccessMessage("导入成功");
            }else {
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
            Clue clue = new Clue();
            clue.setId(id);
            if (type == 2) {
                clue.setStage(2);
                clue.setCusService("");
            } else if (type == 3) {
                clue.setStage(3);
            } else {
                clue.setStage(1);
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
    public ServerResponse sendUser(Member member, String phone) {
        try {
            Clue clue = clueMapper.getByPhone(phone);
            //表示线索表存在线索
            if (clue != null) {
                //有沟通记录
                List<ClueTalk> clueTalkList = clueTalkMapper.getTalkByClueId(clue.getId());
                if (clueTalkList.size() != 0) {
                    Date date = clueTalkMapper.getMaxDate(clue.getId());
                    for (ClueTalk clueTalk : clueTalkList) {
                        //操作dj_member_customer_record表
                        CustomerRecord customerRecord = new CustomerRecord();
                        customerRecord.setUserId(clue.getCusService());
                        customerRecord.setMemberId(member.getId());
                        customerRecord.setDescribes(clueTalk.getTalkContent());
                        //customerRecord.setModifyDate(clueTalk.getModifyDate());
                        customerRecord.setCreateDate(clueTalk.getCreateDate());
                        iCustomerRecordMapper.insert(customerRecord);
                        //操作dj_member_customer表
                        if (date.compareTo(clueTalk.getModifyDate()) == 0) {
                            Customer customer = new Customer();
                            customer.setUserId(clue.getCusService());
                            customer.setMemberId(member.getId());
                            customer.setCurrRecordId(customerRecord.getId());
                            //customer.setModifyDate(clueTalk.getModifyDate());
                            customer.setCreateDate(clueTalk.getCreateDate());
                            customer.setStage(1);
                            iCustomerMapper.insert(customer);
                        }
                        //改变线索沟通表的数据状态
                        clueTalk.setDataStatus(1);
                        clueTalkMapper.updateByPrimaryKeySelective(clueTalk);
                    }
                } else {
                    Customer customer = new Customer();
                    customer.setUserId(clue.getCusService());
                    customer.setMemberId(member.getId());
                    //customer.setModifyDate(clueTalk.getModifyDate());
                    customer.setCreateDate(clue.getCreateDate());
                    customer.setStage(1);
                    iCustomerMapper.insert(customer);
                }
                //改变线索表的数据状态
//                clue.setDataStatus(1);
                clue.setStage(4);
                clueMapper.updateByPrimaryKeySelective(clue);
                //操作dj_member表
                member.setCreateDate(clue.getCreateDate());
                iMemberMapper.updateByPrimaryKeySelective(member);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }
}


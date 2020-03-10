package com.dangjia.acg.service.feedback;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.excel.ExportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.feedback.ExpotUserFeedbackDTO;
import com.dangjia.acg.dto.feedback.UserFeedbackDTO;
import com.dangjia.acg.export.actuary.TActuaryGoods;
import com.dangjia.acg.mapper.feedback.UserFeedbackItemMapper;
import com.dangjia.acg.mapper.feedback.UserFeedbackMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.feedback.UserFeedback;
import com.dangjia.acg.modle.feedback.UserFeedbackItem;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ljl
 * 用户反馈逻辑层
 */
@Service
public class UserFeedbackService {

    @Autowired
    private UserFeedbackMapper userFeedbackMapper;

    @Autowired
    private UserFeedbackItemMapper userFeedbackItemMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IMemberMapper iMemberMapper;

    @Autowired
    private ConfigUtil configUtil;

    /**
     * 新增用户反馈
     * @param userId
     * @param appType
     * @param image
     * @param remark
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addFeedbackInFo(String userToken, String userId,
                                          Integer appType,String image,String remark) {
        try {

            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;

            if (CommonUtil.isEmpty(appType)) {
                return ServerResponse.createByErrorMessage("appType不能为空");
            }

            if (CommonUtil.isEmpty(image)) {
                return ServerResponse.createByErrorMessage("image不能为空");
            }

            if (CommonUtil.isEmpty(remark)) {
                return ServerResponse.createByErrorMessage("remark不能为空");
            }

            //新增反馈详情
            UserFeedbackItem userFeedbackItem = new UserFeedbackItem();
            userFeedbackItem.setUserId(member.getId());
            userFeedbackItem.setImage(image);
            userFeedbackItem.setRemark(remark);
            userFeedbackItemMapper.insert(userFeedbackItem);

            //新增反馈列表
            UserFeedback userFeedback = new UserFeedback();
            userFeedback.setFeedbackId(userFeedbackItem.getId());
            userFeedback.setAppType(appType);
            userFeedback.setUserId(member.getId());
            userFeedback.setFeedbackType(0);
            userFeedback.setCreateDate(userFeedbackItem.getCreateDate());
            userFeedbackMapper.insert(userFeedback);

            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 查询用户反馈
     * @return
     */
    public ServerResponse queryFeedbackInFo(PageDTO pageDTO, Integer appType, Integer feedbackType,
                                            String beginDate, String endDate) {
        try {

            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<UserFeedbackDTO> userFeedbacks = userFeedbackMapper.queryFeedbackInFo(appType,feedbackType,beginDate,endDate);
            if(userFeedbacks != null && userFeedbacks.size()> 0){
                userFeedbacks.forEach(userFeedback -> {
                    Member member = iMemberMapper.selectByPrimaryKey(userFeedback.getUserId());
                    if(member != null){
                        userFeedback.setUserName(member.getName());
                        userFeedback.setMobile(member.getMobile());
                    }
                });
            }

            PageInfo pageResult = new PageInfo(userFeedbacks);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询用户反馈详情
     * @return
     */
    public ServerResponse queryFeedbackItemInFo(String id,String feedbackId) {
        try {
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }
            if (CommonUtil.isEmpty(feedbackId)) {
                return ServerResponse.createByErrorMessage("feedbackId不能为空");
            }

            UserFeedbackDTO userFeedbackDTO = new UserFeedbackDTO();
            UserFeedbackItem userFeedbackItem = userFeedbackItemMapper.selectByPrimaryKey(feedbackId);
            if(userFeedbackItem != null){
                List<String> strList = new ArrayList<>();
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                if (!CommonUtil.isEmpty(userFeedbackItem.getImage())) {
                    List<String> result = Arrays.asList(userFeedbackItem.getImage().split(","));
                    for (int i = 0; i < result.size(); i++) {
                        String str = address + result.get(i);
                        strList.add(str);
                    }
                    userFeedbackDTO.setImage(strList);
                }
                userFeedbackDTO.setRemark(userFeedbackItem.getRemark());
                userFeedbackDTO.setCreateDate(userFeedbackItem.getCreateDate());
                Member member = iMemberMapper.selectByPrimaryKey(userFeedbackItem.getUserId());
                if(member != null){
                    userFeedbackDTO.setUserName(member.getName());
                    userFeedbackDTO.setMobile(member.getMobile());
                }
            }

            //修改查看状态
            UserFeedback userFeedback = new UserFeedback();
            userFeedback.setId(id);
            userFeedback.setFeedbackType(1);
            userFeedback.setCreateDate(null);
            userFeedbackMapper.updateByPrimaryKeySelective(userFeedback);
            return ServerResponse.createBySuccess("查询成功", userFeedbackDTO);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 导出反馈xlsx
     * @param response
     * @param appType
     * @param feedbackType
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse exportFeedbackInFo(HttpServletResponse response,Integer appType, Integer feedbackType,
                                             String beginDate, String endDate) {
        try {

            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }

            List<ExpotUserFeedbackDTO> tActuaryGoodsList = new ArrayList<>();//反馈基础数据结果集

            List<UserFeedbackDTO> userFeedbacks = userFeedbackMapper.queryFeedbackInFo(appType,feedbackType,beginDate,endDate);
            if(userFeedbacks != null && userFeedbacks.size() > 0){
                userFeedbacks.forEach(userFeedback -> {
                    ExpotUserFeedbackDTO expotUserFeedbackDTO = new ExpotUserFeedbackDTO();
                    expotUserFeedbackDTO.setCreateDate(userFeedback.getCreateDate());
                    //1:业主端，2:工匠端
                    if(userFeedback.getAppType() == 1){
                        expotUserFeedbackDTO.setAppTypeName("业主端");
                    }else if(userFeedback.getAppType() == 2){
                        expotUserFeedbackDTO.setAppTypeName("工匠端");
                    }
                    Member member = iMemberMapper.selectByPrimaryKey(userFeedback.getUserId());
                    if(member != null){
                        expotUserFeedbackDTO.setUserName(member.getName());
                        expotUserFeedbackDTO.setMobile(member.getMobile());
                    }

                    //反馈状态：0-未查看 1-已查看
                    if(userFeedback.getFeedbackType() == 1){
                        expotUserFeedbackDTO.setFeedbackTypeName("已查看");
                    }else{
                        expotUserFeedbackDTO.setFeedbackTypeName("未查看");
                    }
                    tActuaryGoodsList.add(expotUserFeedbackDTO);
                });
            }

            ExportExcel exportExcel = new ExportExcel();//创建表格实例
            exportExcel.setDataList("反馈信息", ExpotUserFeedbackDTO.class, tActuaryGoodsList);
            exportExcel.write(response, userFeedbacks.get(0).getId()+ ".xlsx");
            return ServerResponse.createBySuccessMessage("导出Excel成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("导出Excel失败");
        }
    }


}

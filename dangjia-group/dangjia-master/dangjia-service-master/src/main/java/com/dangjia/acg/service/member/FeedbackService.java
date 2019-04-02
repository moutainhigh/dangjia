package com.dangjia.acg.service.member;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.member.IFeedbackMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Feedback;
import com.dangjia.acg.service.core.WorkerTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class FeedbackService {
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IFeedbackMapper iFeedbackMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    /**
     * 获取反馈列表
     *
     * @param feedback
     * @return
     */
    public ServerResponse getFeedbacks(HttpServletRequest request, PageDTO pageDTO, Feedback feedback) {
        Example example = new Example(Feedback.class);
        String url = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        example.orderBy(Group.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Feedback> list = iFeedbackMapper.selectByExample(example);
        List<Map> listMap = new ArrayList<>();
        PageInfo pageResult = new PageInfo(list);
        for (Feedback feedbacknew : list) {
            if (!CommonUtil.isEmpty(feedbacknew.getImageurl())) {
                String[] imageurls = StringUtils.split(feedbacknew.getImageurl(), ",");
                for (int i = 0; i < imageurls.length; i++) {
                    if (!CommonUtil.isEmpty(imageurls[i])) {
                        imageurls[i] = url + imageurls[i];
                    }
                }
                feedbacknew.setImageurl(StringUtils.join(imageurls, ","));
            }
            Map map = BeanUtils.beanToMap(feedbacknew);
            map.put("memberName", memberMapper.selectByPrimaryKey(feedbacknew.getMemberId()).getNickName());
            listMap.add(map);
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    public ServerResponse addFeedback(HttpServletRequest request, Feedback customer) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        if (userToken != null) {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken != null && accessToken.getMember() != null) {
                customer.setMemberId(accessToken.getMemberId());
                customer.setMobile(accessToken.getMember().getMobile());
            }
        }
        customer.setState(0);
        if (!CommonUtil.isEmpty(customer.getWorkerTypeId())) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(customer.getWorkerTypeId());
            customer.setWorkerTypeName(workerType == null ? "" : workerType.getName());
        }
        iFeedbackMapper.insertSelective(customer);
        return ServerResponse.createBySuccessMessage("反馈成功");
    }


}

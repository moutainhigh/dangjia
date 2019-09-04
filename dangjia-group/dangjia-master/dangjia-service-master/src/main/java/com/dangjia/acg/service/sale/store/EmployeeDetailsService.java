package com.dangjia.acg.service.sale.store;

import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.sale.MonthlyTargetMappper;
import com.dangjia.acg.mapper.sale.ResidentialRangeMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class EmployeeDetailsService {

    @Autowired
    private MonthlyTargetMappper monthlyTargetMappper;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private UserMapper userMapper;

    public ServerResponse setMonthlyTarget(String userId, String time, Integer target) {
        MainUser u = userMapper.selectByPrimaryKey(userId);
        if (u == null) {
            return ServerResponse.createByErrorMessage("请选择员工");
        }
        if (CommonUtil.isEmpty(time)) {
            return ServerResponse.createByErrorMessage("请选择目标月份");
        }
        Date targetDate = DateUtil.toDate(time);
        if (CommonUtil.isEmpty(target)) {
            return ServerResponse.createByErrorMessage("请设定目标量");
        }
        Example example = new Example(MonthlyTarget.class);
        example.createCriteria()
                .andEqualTo(MonthlyTarget.USER_ID, u.getId())
                .andBetween(MonthlyTarget.TARGET_DATE, DateUtil.getMonthFirst(targetDate), DateUtil.getMonthLast(targetDate));
        List<MonthlyTarget> monthlyTargets = monthlyTargetMappper.selectByExample(example);
        if (monthlyTargets.size() > 0) {
            MonthlyTarget monthlyTarget = new MonthlyTarget();
            monthlyTarget.setId(monthlyTargets.get(0).getId());
            monthlyTarget.setUserId(u.getId());
            monthlyTarget.setTargetDate(targetDate);
            monthlyTarget.setTargetNumber(target);
            monthlyTarget.setDataStatus(0);
            if (monthlyTargetMappper.updateByPrimaryKeySelective(monthlyTarget) > 0) {
                if (!CommonUtil.isEmpty(u.getMemberId()))
                    configMessageService.addConfigMessage(AppType.SALE, u.getMemberId(), "月目标提醒",
                            "您本月有新的下单目标，请及时查看", 5);
                return ServerResponse.createBySuccessMessage("制定成功");
            }
            return ServerResponse.createByErrorMessage("制定失败");
        }
        MonthlyTarget monthlyTarget = new MonthlyTarget();
        monthlyTarget.setUserId(u.getId());
        monthlyTarget.setTargetDate(targetDate);
        monthlyTarget.setTargetNumber(target);
        monthlyTarget.setDataStatus(0);
        if (monthlyTargetMappper.insert(monthlyTarget) > 0) {
            if (!CommonUtil.isEmpty(u.getMemberId()))
                configMessageService.addConfigMessage(AppType.SALE, u.getMemberId(), "月目标提醒",
                        "您本月有新的下单目标，请及时查看", 5);
            return ServerResponse.createBySuccessMessage("制定成功");
        }
        return ServerResponse.createByErrorMessage("制定失败");
    }


    public ServerResponse setSalesRange(String userId, String buildingId) {
        Example example = new Example(ResidentialRange.class);
        example.createCriteria().andEqualTo(ResidentialRange.USER_ID,userId);
        List<ResidentialRange> list = residentialRangeMapper.selectByExample(example);
        if (list.size() > 0||CommonUtil.isEmpty(buildingId)) {
            residentialRangeMapper.deleteByExample(example);
        }
        ResidentialRange residentialRange = new ResidentialRange();
        residentialRange.setBuildingId(buildingId);
        residentialRange.setUserId(userId);
        residentialRange.setDataStatus(0);
        if (residentialRangeMapper.insert(residentialRange) > 0) {
            return ServerResponse.createBySuccessMessage("配置成功");
        }
        return ServerResponse.createByErrorMessage("配置失败");
    }


    public ServerResponse delMonthlyTarget(String monthlyTargetId) {
        if (monthlyTargetMappper.deleteByPrimaryKey(monthlyTargetId) > 0) {
            return ServerResponse.createBySuccessMessage("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }


}

package com.dangjia.acg.service.sale.store;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.sale.stroe.MonthlyTargetMappper;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

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

    public ServerResponse setMonthlyTarget(String userId, String time, Integer target) {
        if (CommonUtil.isEmpty(userId)) {
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
                .andEqualTo(MonthlyTarget.USER_ID, userId)
                .andBetween(MonthlyTarget.TARGET_DATE, DateUtil.getMonthFirst(targetDate), DateUtil.getMonthLast(targetDate));
        if (monthlyTargetMappper.selectByExample(example).size() > 0) {
            return ServerResponse.createByErrorMessage("该员工当月目标已存在");
        }
        MonthlyTarget monthlyTarget = new MonthlyTarget();
        monthlyTarget.setUserId(userId);
        monthlyTarget.setTargetDate(targetDate);
        monthlyTarget.setTargetNumber(target);
        monthlyTarget.setDataStatus(0);
        if (monthlyTargetMappper.insert(monthlyTarget) > 0) {
            return ServerResponse.createBySuccess("制定成功");
        }
        return ServerResponse.createByErrorMessage("制定失败");
    }
}

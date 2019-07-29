package com.dangjia.acg.service.sale.store;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.sale.stroe.MonthlyTargetMappper;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
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

    public ServerResponse setMonthlyTarget(String userId, Date time, Integer target) {
        Example example=new Example(MonthlyTarget.class);
        example.createCriteria().andEqualTo(MonthlyTarget.USER_ID,userId)
                .andEqualTo(MonthlyTarget.MODIFY_DATE,time);
        if(monthlyTargetMappper.selectByExample(example).size()>0){
            return ServerResponse.createByErrorMessage("该员工当月目标已存在");
        }
        MonthlyTarget monthlyTarget=new MonthlyTarget();
        monthlyTarget.setUserId(userId);
        monthlyTarget.setModifyDate(time);
        monthlyTarget.setTargetNumber(target);
        monthlyTarget.setDataStatus(0);
        if(monthlyTargetMappper.insert(monthlyTarget)>0){
            return ServerResponse.createBySuccess("制定成功");
        }
        return ServerResponse.createByErrorMessage("制定失败");
    }
}

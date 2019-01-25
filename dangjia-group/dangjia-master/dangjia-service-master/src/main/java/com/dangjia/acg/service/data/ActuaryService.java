package com.dangjia.acg.service.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ActuaryService {

    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper userMapper;

    /**
     * 待业主支付精算数据
     * @return
     */
    public ServerResponse getActuaryWaitPay(){
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("budgetOk", 0).andEqualTo("designerOk", 3);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", listResult(houseList));
    }

    /**
     * 待提交精算
     */
    public ServerResponse getActuaryCommit(){
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("budgetOk", 1).andEqualTo("designerOk", 3);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", listResult(houseList));
    }

    /**
     * 待业主确认精算
     */
    public ServerResponse getActuaryConfirm(){
        Example example = new Example(House.class);
        example.createCriteria().andCondition("(budget_ok =2 || budget_ok = 4)").andEqualTo("designerOk", 3);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", listResult(houseList));
    }

    /**
     * 已完成精算
     */
    public ServerResponse getActuaryComplete(){
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("budgetOk", 3).andEqualTo("designerOk", 3);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", listResult(houseList));
    }

    /**
     * 统计精算数据
     */
    public ServerResponse getStatistics(){
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("designerOk", 3);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", mapResult(houseList));
    }

    /**
     * 按日期统计
     */
    public ServerResponse getStatisticsByDate(String startDate, String endDate){
        //将时分秒转换为年月日
        Date start =  DateUtil.toDate(startDate);
        Date end = DateUtil.toDate(endDate);
        List<House> houseList = houseMapper.getStatisticsByDate(start,end);
        return ServerResponse.createBySuccess("查询成功", mapResult(houseList));
    }

    private  Map<String, Object> mapResult(List<House> houseList){
        int sum1 = 0,sum2 = 0,sum3 = 0,sum4 = 0;
        for(House house : houseList){
            if(house.getBudgetOk() == 0){
                sum1++;
            }
            if(house.getBudgetOk() == 1){
                sum2++;
            }
            if(house.getBudgetOk() == 2){
                sum3++;
            }
            if(house.getBudgetOk() == 3){
                sum4++;
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("actuaryNumber", houseList.size());//获取精算接单数量
        map.put("actuaryPayNumber", sum1);//待业主支付数量
        map.put("actuaryUploadNumber", sum2);//待上传精算数量
        map.put("actuaryConfirmeNumber", sum3);//待确认精算数量
        map.put("actuarycompletedNumber", sum4);//已完成精算数量
        return map;
    }
    //提出重复代码
    private List<Map<String, Object>> listResult(List<House> houseList){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for(House house : houseList){
            Map<String, Object> map=new HashMap<String, Object>();
            Member user = userMapper.selectByPrimaryKey(house.getMemberId());
            map.put("houseName", house.getHouseName());
            map.put("customSort", house.getCustomSort());
            map.put("name", user.getNickName());
            map.put("mobile",user.getMobile());
            map.put("square", house.getSquare());
            map.put("houseId", house.getId());
            map.put("budgetOk", house.getBudgetOk());
            list.add(map);
        }
        return list;
    }
}

package com.dangjia.acg.service.data;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.house.HouseListDTO;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.house.House;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IDesignBusinessOrderMapper designBusinessOrderMapper;


    /**
     * 查询房子精算数据
     *
     * @return
     */
    public ServerResponse getActuaryAll(HttpServletRequest request, PageDTO pageDTO, String name, String budgetOk, String workerKey) {
        String cityId = request.getParameter(Constants.CITY_ID);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String dataStatus = "0";//正常数据
        if (Integer.parseInt(budgetOk) < 0) {
            //当类型小于0时，则查询移除的数据
            dataStatus = "1";
            budgetOk = "";
        }
        List<HouseListDTO> houseList = houseMapper.getActuaryAll(cityId, budgetOk, name, workerKey, dataStatus);
        PageInfo pageResult = new PageInfo(houseList);
        for (HouseListDTO houseListDTO : houseList) {
            houseListDTO.setShowUpdata(0);
            if (houseListDTO.getDecorationType() == 2) {
                if (houseListDTO.getBudgetOk() == 1 && houseListDTO.getDesignerOk() != 3) {
                    houseListDTO.setShowUpdata(1);
                } else if (houseListDTO.getDesignerOk() == 3) {
                    //3设计图完成后有需要改设计的
                    Example example = new Example(DesignBusinessOrder.class);
                    Example.Criteria criteria = example.createCriteria()
                            .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                            .andEqualTo(DesignBusinessOrder.HOUSE_ID, houseListDTO.getHouseId())
                            .andEqualTo(DesignBusinessOrder.STATUS, 1)
                            .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
                    criteria.andEqualTo(DesignBusinessOrder.TYPE, 3);
                    List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                    if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                        DesignBusinessOrder order = designBusinessOrders.get(0);
                        if (order.getOperationState() == 0) {
                            houseListDTO.setShowUpdata(1);
                        }
                    }
                }
            }
        }
        pageResult.setList(houseList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 统计精算数据
     */
    public ServerResponse getStatistics() {
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.DESIGNER_OK, 3)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", mapResult(houseList));
    }

    /**
     * 按日期统计
     */
    public ServerResponse getStatisticsByDate(String startDate, String endDate) {
        //将时分秒转换为年月日
        Date start = DateUtil.toDate(startDate);
        Date end = DateUtil.toDate(endDate);
        List<House> houseList = houseMapper.getStatisticsByDate(start, end);
        return ServerResponse.createBySuccess("查询成功", mapResult(houseList));
    }

    private Map<String, Object> mapResult(List<House> houseList) {
        int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0;
        for (House house : houseList) {
            if (house.getBudgetOk() == 0) {
                sum1++;
            }
            if (house.getBudgetOk() == 1) {
                sum2++;
            }
            if (house.getBudgetOk() == 2) {
                sum3++;
            }
            if (house.getBudgetOk() == 3) {
                sum4++;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("actuaryNumber", houseList.size());//获取精算接单数量
        map.put("actuaryPayNumber", sum1);//待业主支付数量
        map.put("actuaryUploadNumber", sum2);//待上传精算数量
        map.put("actuaryConfirmeNumber", sum3);//待确认精算数量
        map.put("actuarycompletedNumber", sum4);//已完成精算数量
        return map;
    }
}

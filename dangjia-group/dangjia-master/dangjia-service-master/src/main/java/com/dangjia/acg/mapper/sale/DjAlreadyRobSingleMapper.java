package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 抢单 DAO
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/8/14
 * Time: 10:01
 */
@Repository
public interface DjAlreadyRobSingleMapper extends Mapper<DjAlreadyRobSingle> {


    List<DjAlreadyRobSingle> selectArr(Map<String,Object> map);


    void upDateDataStatus(Map<String,Object> map);

}

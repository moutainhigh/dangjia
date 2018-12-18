package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerTechnology;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工艺说明
 *
 * @ClassName: technologyDao
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface ITechnologyMapper extends Mapper<Technology> {

    /**根据商品id查询商品关联节点工艺 人工材料共用*/
    List<Technology> queryTechnologyList(@Param("goodsId") String goodsId);

    void deleteById(String id);

    List<Technology> query(@Param("workerTypeId") String workerTypeId ,@Param("name") String name,@Param("materialOrWorker")Integer materialOrWorker);

    //新增人工商品关联工艺
    void insertWokerTechnology(WorkerTechnology workerTechnology);

    //删除人工商品关联工艺
    void deleteWokerTechnologyByWgId(@Param("worker_goods_id") String worker_goods_id);

    //根据商品id查询人工商品关联工艺关系
    List<WorkerTechnology> queryWokerTechnologyByWgId(@Param("worker_goods_id") String worker_goods_id);
    
    //根据商品id查询人工商品关联工艺实体
    List<Technology> queryTechnologyByWgId(@Param("worker_goods_id") String worker_goods_id);

    Technology queryById(@Param("id") String id);
    //根据内容模糊搜索工艺 
    List<Technology> queryByName(@Param("name") String name);

}

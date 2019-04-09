package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.Technology;
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
    /**
     * 根据商品 goodsId 查询商品对应的多个工艺
     *
     * @param goodsId 人工id ,服务货品productId  :  根据 materialOrWorker字段决定：  0:服务productId;  1:人工商品
     * @return
     */
    List<Technology> queryTechnologyList(@Param("goodsId") String goodsId);

    /**
     * 管家巡查验收工艺
     */
    List<Technology> patrolList(@Param("goodsId") String goodsId);

    List<Technology> workerPatrolList(@Param("goodsId") String goodsId);

    void deleteById(String id);

    List<Technology> query(@Param("workerTypeId") String workerTypeId, @Param("name") String name, @Param("materialOrWorker") Integer materialOrWorker);

    List<Technology> getByName(@Param("workerTypeId") String workerTypeId,
                               @Param("name") String name,
                               @Param("materialOrWorker") Integer materialOrWorker,
                               @Param("goodsId") String goodsId);

    //根据商品id查询人工商品关联工艺实体
    List<Technology> queryTechnologyByWgId(@Param("goodsId") String goodsId);

    Technology queryById(@Param("id") String id);

    //根据内容模糊搜索工艺
    List<Technology> queryByName(@Param("name") String name);

}

package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.GoodsGroup;
import com.dangjia.acg.modle.basics.GroupLink;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface IGoodsGroupMapper extends Mapper<GoodsGroup> {

	/**
	 * 获取所有关联组
	 */
	List<GoodsGroup> getAllList(@Param("name") String name,@Param("state") Integer state,@Param("cityId")String cityId);

	List<GoodsGroup> selectByName(@Param("name") String name,@Param("cityId") String cityId);

	void addGoodsGroup(GoodsGroup goodsGroup);

	List<Map<String, Object>> getParentTopList(@Param("cityId") String cityId);

	List<Map<String, Object>> getChildList(String id);

	List<Map<String, Object>> getGoodsList(String id);

	List<Map<String, Object>> getProductList(String id);
//    //新增关联组和货品关系
//	void addGroupLink(GroupLink groupLink);
//	//根据关联组id删除货品关联关系
//	void deleteGroupLink(@Param("groupId") String groupId);
//    //根据关联组id和货品id修改关联关系
//	void updateGroupLink(GroupLink groupLink);
    //根据关联组id查询关联货品关系
	List<Map<String, Object>> queryMapGroupLinkByGid(String groupId);
	List<GroupLink> queryGroupLinkByGid(@Param("groupId") String groupId,@Param("cityId") String cityId);
	//根据关联组id和货品id查询关联关系
	List<GroupLink> queryGroupLinkByGidAndPid(@Param("groupId")String groupId , @Param("productId")String productId);

//	//根据关联组id和货品id查询关联关系
//	GroupLink queryGroupLinkByGroupIdAndPid(@Param("groupId")String groupId , @Param("productId")String productId);

//	//根据货品id查询关联关系
//	List<GroupLink> queryGroupLinkByPid(@Param("productId")String productId);
//	//根据商品id，查询对应的所有货品
//	List<GroupLink> queryGroupLinkByGoodsId(@Param("goodsId")String goodsId);
//	//根据货品id查询关联关系
//	List<GroupLink> queryGroupLinkByPidAndGid(@Param("productId")String productId,@Param("goodsId")String goodsId);
	//根据货品id修改所有关联组货品可切换性
	void updateGLinkByPid(@Param("productId")String productId,@Param("isSwitch")Integer isSwitch);

//	//根据关联组id删除货品关联关系
//	void deleteGroupLinkById(@Param("id") String id);

}

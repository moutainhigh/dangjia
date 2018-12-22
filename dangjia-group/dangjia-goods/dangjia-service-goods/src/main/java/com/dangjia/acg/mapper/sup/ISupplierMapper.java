package com.dangjia.acg.mapper.sup;

import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 *
 * @类 名： SupplierDao
 * @功能描述： 供应商dao
 * @作者信息： zmj
 * @创建时间： 2018-9-17上午11:10:44
 */
@Repository
public interface ISupplierMapper extends Mapper<Supplier> {

	/**根据电话号码查询*/
	Supplier byTelephone(@Param("telephone")String telephone);

	void deleteById(String id);
	List<Supplier> query();

	//	按照名字模糊查询所有供应商
	List<Supplier> querySupplierListLikeByName(@Param("name") String name);

	void insertSupplierProduct(SupplierProduct supplierGoods);
	void updateSupplierProduct(SupplierProduct supplierGoods);
	//根据供应商查询自己所供应的商品
	List<Product> querySupplierProduct(String supplier_id);
	//根据供应商查询在供应的商品种类
	Integer getSupplierProductByGoodId(String goods_id);
	//根据供应商查询在供应的货品种类
	Integer getSupplierProductByProductId(String supplier_id);
	//根据供应商查询在供应的库存小于50的商品
	Integer getSupplierProductByStock(String supplier_id);
	//根据供应商、商品、属性查询对应关系
	SupplierProduct querySupplierProductRelation(@Param("productId") String productId, @Param("supplierId") String supplierId);
}

package com.dangjia.acg.mapper.shell;

import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IHomeShellProductMapper extends Mapper<HomeShellProduct> {
    /**
     * 查询商品列表
     * @param productType 商品分类 1实物商品，虚拟商品
     * @param searchKey 商品名称/商品编码
     * @return
     */
    List<HomeShellProductDTO> queryHomeShellProductList(@Param("productType") String productType, @Param("searchKey") String searchKey);

    /**
     * 当家贝商城
     * @param productType
     * @return
     */
    List<HomeShellProductDTO> serachShellProductList(@Param("productType") String productType);
}

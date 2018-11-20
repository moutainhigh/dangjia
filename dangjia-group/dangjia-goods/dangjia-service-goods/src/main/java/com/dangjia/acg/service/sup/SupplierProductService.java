package com.dangjia.acg.service.sup;

import com.dangjia.acg.mapper.sup.ISupplierProductMapper;
import com.dangjia.acg.modle.sup.SupplierProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/24 0024
 * Time: 11:13
 */
@Service
public class SupplierProductService {

    @Autowired
    private ISupplierProductMapper iSupplierProductMapper;

    public void test(String productId){
        Example example = new Example(SupplierProduct.class);
        example.createCriteria().andEqualTo("productId",productId);
        iSupplierProductMapper.deleteByExample(example);
    }
}

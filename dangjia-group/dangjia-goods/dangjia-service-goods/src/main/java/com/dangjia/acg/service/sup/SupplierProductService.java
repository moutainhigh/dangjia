package com.dangjia.acg.service.sup;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.sup.SupplierDTO;
import com.dangjia.acg.mapper.sup.ISupplierMapper;
import com.dangjia.acg.mapper.sup.ISupplierProductMapper;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private ISupplierMapper iSupplierMapper;


    public void test(String productId) {
        Example example = new Example(SupplierProduct.class);
        example.createCriteria().andEqualTo("productId", productId);
        iSupplierProductMapper.deleteByExample(example);
    }

    /**
     * 查询供应商
     */
    public ServerResponse supplierList(String productId) {
        try {
            List<SupplierProduct> supplierProductList = iSupplierProductMapper.querySupplierProduct(null,productId);
            List<SupplierDTO> supplierDTOList = new ArrayList<>();
            for (SupplierProduct supplierProduct : supplierProductList) {
                Supplier supplier = iSupplierMapper.selectByPrimaryKey(supplierProduct.getSupplierId());
                if (supplier != null) {
                    SupplierDTO supplierDTO = new SupplierDTO();
                    supplierDTO.setSupplierId(supplierProduct.getSupplierId());
                    supplierDTO.setSupplierPrice(supplierProduct.getPrice());//供应价
                    supplierDTO.setName(supplier.getName());
                    supplierDTOList.add(supplierDTO);
                }
            }
            return ServerResponse.createBySuccess("查询成功", supplierDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据货品查询相应供应商
     *
     * @param pageDTO
     * @param productId
     * @return
     */
    public ServerResponse querySupplierProductByPid(PageDTO pageDTO, String productId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupplierProduct> spList = iSupplierProductMapper.querySupplierProduct(null,productId);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (int i = 0; i < spList.size(); i++) {
                SupplierProduct supplierProduct = spList.get(i);
                Map<String, Object> map = new HashMap<>();
                map.put("supplierId", supplierProduct.getSupplierId());//供应商id
                Supplier supplier = iSupplierMapper.selectByPrimaryKey(supplierProduct.getSupplierId());
                map.put("supplierName", supplier == null ? "" : supplier.getName());//供应商名称
                map.put("supplierTel", supplier == null ? "" : supplier.getTelephone());//供应电话
                map.put("supplierPrice", supplierProduct.getPrice());//供应价格
                if (i == 0) {
                    map.put("isDefault", 0);//默认选中最低价0：选中；1：不选中
                } else {
                    map.put("isDefault", 1);
                }
                mapList.add(map);
            }

            PageInfo pageResult = new PageInfo(spList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}

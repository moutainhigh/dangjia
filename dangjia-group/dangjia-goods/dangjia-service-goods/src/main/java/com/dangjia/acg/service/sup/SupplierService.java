package com.dangjia.acg.service.sup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.mapper.sup.ISupplierMapper;
import com.dangjia.acg.mapper.sup.ISupplierProductMapper;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class SupplierService {
    @Autowired
    private ISupplierMapper iSupplierMapper;
    @Autowired
    private ISupplierProductMapper iSupplierProductMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private IGoodsMapper goodsMapper;

    /**
     * 查询指定供应商
     */
    public Supplier getSupplier(String productId) {
        return iSupplierMapper.selectByPrimaryKey(productId);
    }

    /**
     * 供应商登录
     */
    public ServerResponse byTelephone(String telephone) {
        if (CommonUtil.isEmpty(telephone)) {
            return ServerResponse.createByErrorMessage("请输入手机号");
        }
        List<Supplier> suppliers = getSupplierList(null, telephone);
        if (suppliers == null || suppliers.size() == 0) {
            return ServerResponse.createByErrorMessage("查询失败，未查到供应记录");
        } else {
            Supplier supplier = suppliers.get(0);
            return ServerResponse.createBySuccess("查询成功", supplier.getId());
        }
    }

    private List<Supplier> getSupplierList(String name, String telephone) {
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(name) && !CommonUtil.isEmpty(telephone)) {
            criteria.andEqualTo(Supplier.NAME, name);
            criteria.orEqualTo(Supplier.TELEPHONE, telephone);
        } else if (!CommonUtil.isEmpty(name)) {
            criteria.andEqualTo(Supplier.NAME, name);
        } else if (!CommonUtil.isEmpty(telephone)) {
            criteria.andEqualTo(Supplier.TELEPHONE, telephone);
        }
        return iSupplierMapper.selectByExample(example);
    }

    /**
     * 新增供应商
     */
    public ServerResponse insertSupplier(String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state) {
        List<Supplier> list = getSupplierList(name, telephone);
        if (list.size() > 0)
            return ServerResponse.createByErrorMessage("该手机号或该供应商名已存在");
        Supplier supplier = new Supplier();
        supplier.setName(name);//名称
        supplier.setAddress(address);//地址
        supplier.setTelephone(telephone);//联系电话
        supplier.setCheckPeople(checkPeople);//联系人姓名
        supplier.setGender(gender);//1男 2女   0 未选
        supplier.setEmail(email);
        supplier.setNotice(notice);
        supplier.setSupplierLevel(supplierLevel);//级别
        supplier.setState(state);//供应商状态  1正常供货 2停止供货
        iSupplierMapper.insert(supplier);
        return ServerResponse.createBySuccess("新增成功", supplier);
    }

    /**
     * 修改供应商
     */
    public ServerResponse updateSupplier(String id, String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplier_level, Integer state) {
        Supplier supplier = iSupplierMapper.selectByPrimaryKey(id);
        if (supplier == null) {
            return ServerResponse.createByErrorMessage("此供应商不存");
        }
        if (!CommonUtil.isEmpty(name) && supplier.getName().equals(name)) {
            name = null;
        }
        if (!CommonUtil.isEmpty(telephone) && supplier.getTelephone().equals(telephone)) {
            telephone = null;
        }
        if (!CommonUtil.isEmpty(name) || !CommonUtil.isEmpty(telephone)) {
            List<Supplier> list = getSupplierList(name, telephone);
            if (list.size() > 0)
                return ServerResponse.createByErrorMessage("该手机号或该供应商名已存在");
        }
        if (!CommonUtil.isEmpty(name)) {
            supplier.setName(name);//名称
        }
        if (!CommonUtil.isEmpty(address)) {
            supplier.setAddress(address);//地址
        }
        if (!CommonUtil.isEmpty(telephone)) {
            supplier.setTelephone(telephone);//联系电话
        }
        if (!CommonUtil.isEmpty(checkPeople)) {
            supplier.setCheckPeople(checkPeople);//联系人姓名
        }
        if (!CommonUtil.isEmpty(gender)) {
            supplier.setGender(gender);//1男 2女   0 未选
        }
        if (!CommonUtil.isEmpty(email)) {
            supplier.setEmail(email);
        }
        if (!CommonUtil.isEmpty(notice)) {
            supplier.setNotice(notice);
        }
        if (!CommonUtil.isEmpty(supplier_level)) {
            supplier.setSupplierLevel(supplier_level);//级别
        }
        if (!CommonUtil.isEmpty(state)) {
            supplier.setState(state);//供应商状态  1正常供货 2停止供货
        }
        supplier.setModifyDate(new Date());
        iSupplierMapper.updateByPrimaryKeySelective(supplier);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    /**
     * 按照名字模糊查询所有供应商
     */
    public ServerResponse<PageInfo> querySupplierListLikeByName(PageDTO pageDTO, String name) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(name))
            criteria.andLike(Supplier.NAME, "%" + name + "%");
        example.orderBy(Supplier.CREATE_DATE).desc();
        List<Supplier> supplierList = iSupplierMapper.selectByExample(example);
        if (supplierList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(supplierList);
        List<Map<String, Object>> mapList = new ArrayList<>();//返回list
        for (Supplier supplier : supplierList) {
            Map<String, Object> map = BeanUtils.beanToMap(supplier);
            //查找所有的货品 供应商
            List<SupplierProduct> pList = iSupplierProductMapper.querySupplierProduct(supplier.getId(), null);
            Set<String> goodsSet = new HashSet<>();
            Set<String> productSet = new HashSet<>();
            Integer countStock = 0;
            for (SupplierProduct product : pList) {
                //根据供应商、商品、属性查询对应关系
                goodsSet.add(product.getGoodsId());
                productSet.add(product.getId());
                if (product.getStock() < 50) {
                    countStock++;
                }
            }
            map.put("countGoods", goodsSet.size());//供应的货品种类
            map.put("countAttribute", productSet.size());//供应的商品种类
            map.put("countStock", countStock);//库存小于50的
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 查询所有商品供应关系0:仅供应商品;1:所有商品
     */
    public ServerResponse querySupplierProduct(int type, String supplierId, String likeProductName, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> listMap = new ArrayList<>();
            if (type == 0) {//仅供应商品
                List<Product> pList = iSupplierMapper.querySupplierProduct(supplierId, likeProductName, 1);
                PageInfo pageResult = new PageInfo(pList);
                for (Product product : pList) {
                    Map<String, Object> gmap = new HashMap<>();
                    gmap.put("pId", product.getId());//商品id
                    Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
                    gmap.put("goodsName", goods == null ? "" : goods.getName());//商品名称
                    gmap.put("productName", product.getName());//货品名称
                    gmap.put("isSupply", 1);//是否供应；0停供，1供应
                    SupplierProduct supplierProduct = iSupplierMapper.querySupplierProductRelation(product.getId(), supplierId);
                    if (supplierProduct == null) {
                        gmap.put("price", 0);//供应价格
                        gmap.put("stock", 0);//库存
                    } else {
                        gmap.put("price", supplierProduct.getPrice());//供应价格
                        gmap.put("stock", supplierProduct.getStock());//库存
                    }
                    gmap.put("aveString", product.getValueNameArr());//属性选项选中值名称集合
                    listMap.add(gmap);
                }
                pageResult.setList(listMap);
                return ServerResponse.createBySuccess("查询成功", pageResult);
            } else {//所有商品
                List<Product> pList = iProductMapper.queryByLikeName(likeProductName);//查询所有货品
                PageInfo pageResult = new PageInfo(pList);
                for (Product product : pList) {
                    Map<String, Object> gmap = new HashMap<>();
                    gmap.put("pId", product.getId());//商品id
                    Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
                    gmap.put("goodsName", goods == null ? "" : goods.getName());//商品名称
                    gmap.put("productName", product.getName());//商品名称
                    SupplierProduct supplierProduct = iSupplierMapper.querySupplierProductRelation(product.getId(), supplierId);
                    if (supplierProduct == null) {
                        gmap.put("isSupply", 0);//是否供应；0停供，1供应
                        gmap.put("price", 0);//供应价格
                        gmap.put("stock", 0);//库存
                    } else {
                        gmap.put("isSupply", supplierProduct.getIsSupply());//是否供应；0停供，1供应
                        gmap.put("price", supplierProduct.getPrice());//供应价格
                        gmap.put("stock", supplierProduct.getStock());//库存
                    }
                    gmap.put("aveString", product.getValueNameArr());//属性选项选中值名称集合
                    listMap.add(gmap);
                }
                pageResult.setList(listMap);
                return ServerResponse.createBySuccess("查询成功", pageResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 保存供应商与货品供应关系
     */
    public ServerResponse saveSupplierProduct(String arrString) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(arrString);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String productId = obj.getString("productId");
                String supplierId = obj.getString("supplierId");
                if (!StringUtils.isNotBlank(supplierId))
                    return ServerResponse.createByErrorMessage("supplierId参数不能为空");
                if (!StringUtils.isNotBlank(productId))
                    return ServerResponse.createByErrorMessage("supplierId参数不能为空");
            }
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String productId = obj.getString("productId");
                String supplierId = obj.getString("supplierId");
                Double price = Double.parseDouble(obj.getString("price"));
                Double stock = Double.parseDouble(obj.getString("stock"));
                Integer isSupply = Integer.parseInt(obj.getString("isSupply"));
                //根据供应商、商品、属性查询对应关系
                SupplierProduct supplierProduct = iSupplierMapper.querySupplierProductRelation(productId, supplierId);
                SupplierProduct sp = new SupplierProduct();
                if (supplierProduct == null) {//新增
                    sp.setProductId(productId);
                    sp.setSupplierId(supplierId);
                    sp.setPrice(price);//价格
                    sp.setStock(stock);//库存
                    sp.setIsSupply(isSupply);//是否供应；0停供，1供应
                    iSupplierMapper.insertSupplierProduct(sp);
                } else {//保存
                    sp.setProductId(productId);
                    sp.setSupplierId(supplierId);
                    sp.setPrice(price);//价格
                    sp.setStock(stock);//库存
                    sp.setIsSupply(isSupply);//是否供应；0停供，1供应
                    sp.setModifyDate(new Date());
                    iSupplierMapper.updateSupplierProduct(sp);
                }
                List<SupplierProduct> supplierProducts = iSupplierProductMapper.querySupplierProduct(null, sp.getProductId());
                //更新 对应 product 的平均价格
                Product oldProduct = iProductMapper.selectByPrimaryKey(sp.getProductId());
                if (supplierProducts.size() > 0) {
                    Double priceSum = 0.0;
                    for (SupplierProduct supplierProduct1 : supplierProducts) {
                        priceSum = priceSum + supplierProduct1.getPrice();
                    }
                    oldProduct.setCost(priceSum / supplierProducts.size());
                    iProductMapper.updateByPrimaryKeySelective(oldProduct);
                }
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

}

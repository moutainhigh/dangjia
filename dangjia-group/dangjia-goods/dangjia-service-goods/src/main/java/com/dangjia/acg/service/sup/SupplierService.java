package com.dangjia.acg.service.sup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @类 名： SupplierServiceImpl
 * @功能描述： TODO
 * @作者信息： zmj
 * @创建时间： 2018-9-17下午3:28:20
 */
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
    private static Logger LOG = LoggerFactory.getLogger(SupplierService.class);

    /**
     * 供应商登录
     */
    public ServerResponse byTelephone(String telephone) {
        try {
            Supplier supplier = iSupplierMapper.byTelephone(telephone);
            if (supplier == null) {
                return ServerResponse.createByErrorMessage("查询失败");
            } else {
                return ServerResponse.createBySuccess("查询成功", supplier.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 新增供应商
     * <p>Title: insertSupplier</p>
     * <p>Description: </p>
     *
     * @param name
     * @param address
     * @param telephone
     * @param checkPeople
     * @param gender
     * @param email
     * @param notice
     * @param supplierLevel
     * @param state
     */
    public ServerResponse insertSupplier(String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state) {
        try {
            List<Supplier> list = iSupplierMapper.queryByName(name);
            if (list.size() > 0)
                return ServerResponse.createByErrorMessage("该供应商已存在");

            List<Supplier> telephoneList = iSupplierMapper.queryByTelephone(telephone);
            if (telephoneList.size() > 0)
                return ServerResponse.createByErrorMessage("手机号已存在");

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
            supplier.setCreateDate(new Date());
            supplier.setModifyDate(new Date());
            iSupplierMapper.insert(supplier);

            return ServerResponse.createBySuccess("新增成功", supplier);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 修改供应商
     * <p>Title:</p>
     * <p>Description: </p>
     *
     * @param name
     * @param address
     * @param telephone
     * @param checkPeople
     * @param gender
     * @param email
     * @param notice
     * @param supplier_level
     * @param state
     */
    public ServerResponse updateSupplier(String id, String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplier_level, Integer state) {
        try {
            Supplier t = iSupplierMapper.selectByPrimaryKey(id);
            if (t == null) {
                return ServerResponse.createByErrorMessage("不存在此供应商,修改失败");
            }

            if (!t.getName().equals(name))//如果修改了名称 就判断，修改的名字 是否已经存在
            {
                List<Supplier> list = iSupplierMapper.queryByName(name);
                if (list.size() > 0)
                    return ServerResponse.createByErrorMessage("该供应商名字已存在");
            }

            Supplier supplier = new Supplier();
            supplier.setId(id);
            supplier.setName(name);//名称
            supplier.setAddress(address);//地址
            if (StringUtils.isNotBlank(telephone)) { //不为空时，
                if (!t.getTelephone().equals(telephone))//如果修改了电话 就判断，修改的电话 是否已经存在
                {
                    List<Supplier> telephoneList = iSupplierMapper.queryByTelephone(telephone);
                    if (telephoneList.size() > 0)
                        return ServerResponse.createByErrorMessage("手机号已存在");
                    supplier.setTelephone(telephone);//联系电话
                }
            }
            supplier.setCheckPeople(checkPeople);//联系人姓名
            supplier.setGender(gender);//1男 2女   0 未选
            supplier.setEmail(email);
            supplier.setNotice(notice);
            supplier.setSupplierLevel(supplier_level);//级别
            supplier.setState(state);//供应商状态  1正常供货 2停止供货
            supplier.setModifyDate(new Date());
            iSupplierMapper.updateByPrimaryKeySelective(supplier);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 查询所有供应商
     */
    public ServerResponse<PageInfo> querySupplierList(PageDTO pageDTO) {
        try {
            if (pageDTO.getPageNum() == null) {
                pageDTO.setPageNum(1);
            }
            if (pageDTO.getPageSize() == null) {
                pageDTO.setPageSize(10);
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();//返回list
            List<Supplier> supplierList = iSupplierMapper.query();
            for (Supplier supplier : supplierList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", supplier.getId());
                map.put("name", supplier.getName());//供应商名称
                map.put("address", supplier.getAddress());//地址
                map.put("telephone", supplier.getTelephone());//联系电话
                map.put("checkPeople", supplier.getCheckPeople());//联系人
                map.put("email", supplier.getEmail());//电子邮件
                map.put("notice", supplier.getNotice());//发货须知
                map.put("supplierLevel", supplier.getSupplierLevel());//供应商级别
                map.put("gender", supplier.getGender());//联系人性别 1男 2女
                //查找所有的货品 供应商
                List<Product> pList = iSupplierMapper.querySupplierProduct(supplier.getId(), "", -1);
                Set goodsSet = new HashSet();
                for (Product product : pList)
                {
                    goodsSet.add(product.getGoodsId());
                    LOG.info("product name:" + product.getName() + " getGoodsId:" + product.getGoodsId() + " productID:" + product.getId() + " siez:" + goodsSet.size());
                }
                   map.put("countGoods", goodsSet.size());//供应的货品种类
                Integer countAttribute = iSupplierMapper.getSupplierProductByProductId(supplier.getId());
                map.put("countAttribute", countAttribute == null ? "0" : countAttribute);//供应的商品种类
                map.put("countStock", iSupplierMapper.getSupplierProductByStock(supplier.getId()));//库存小于50的
                map.put("state", supplier.getState());//供应商状态  1正常供货 2停止供货
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("createDate", sdf.format(supplier.getCreateDate() == null ? new Date() : supplier.getCreateDate()));
                mapList.add(map);
            }
            PageInfo pageResult = new PageInfo(supplierList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 按照名字模糊查询所有供应商
     */
    public ServerResponse<PageInfo> querySupplierListLikeByName(PageDTO pageDTO, String name) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();//返回list

            List<Supplier> supplierList = iSupplierMapper.querySupplierListLikeByName(name);
            for (Supplier supplier : supplierList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", supplier.getId());
                map.put("name", supplier.getName());//供应商名称
                map.put("address", supplier.getAddress());//地址
                map.put("telephone", supplier.getTelephone());//联系电话
                map.put("checkPeople", supplier.getCheckPeople());//联系人
                map.put("email", supplier.getEmail());//电子邮件
                map.put("notice", supplier.getNotice());//发货须知
                map.put("supplierLevel", supplier.getSupplierLevel());//供应商级别
                map.put("gender", supplier.getGender());//联系人性别 1男 2女

                //查找所有的货品 供应商
                List<Product> pList = iSupplierMapper.querySupplierProduct(supplier.getId(), "", -1);
                Set<String> goodsSet = new HashSet();
                for (Product product : pList)
                    goodsSet.add(product.getGoodsId());
                map.put("countGoods", goodsSet.size());//供应的货品种类
                map.put("countAttribute", iSupplierMapper.getSupplierProductByProductId(supplier.getId()) == null ? "0" : iSupplierMapper.getSupplierProductByProductId(supplier.getId()));//供应的商品种类
                map.put("countStock", iSupplierMapper.getSupplierProductByStock(supplier.getId()));//库存小于50的
                map.put("state", supplier.getState());//供应商状态  1正常供货 2停止供货
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("createDate", sdf.format(supplier.getCreateDate() == null ? new Date() : supplier.getCreateDate()));
                mapList.add(map);
            }
            PageInfo pageResult = new PageInfo(supplierList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询所有商品供应关系0:仅供应商品;1:所有商品
     */
    public ServerResponse querySupplierProduct(int type, String supplierId, String categoryId, String likeProductName, Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Map<String, Object>> listMap = new ArrayList<>();
            if (type == 0) {//仅供应商品
                List<Product> pList = iSupplierMapper.querySupplierProduct(supplierId, likeProductName, 1);
                PageInfo pageResult = new PageInfo(pList);
                for (Product product : pList) {
                    Map<String, Object> gmap = new HashMap<String, Object>();
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
//                List<Product> pList = iProductMapper.query(categoryId);//查询所有货品
//                List<Product> pList = iSupplierMapper.querySupplierProduct("", likeProductName, -1);
                List<Product> pList = iProductMapper.queryByLikeName(likeProductName);//查询所有货品

//                LOG.info("size: " + pList.size() + " supplierId:" + supplierId + " likeProductName:" + likeProductName);
                PageInfo pageResult = new PageInfo(pList);
                for (Product product : pList) {
                    Map<String, Object> gmap = new HashMap<String, Object>();
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

//    /**
//     * 保存供应商与货品供应关系
//     */
//    public ServerResponse saveSupplierProduct(String productId, String supplierId,
//                                              Double price, Double stock, Integer isSupply) {
//        try {
//            //根据供应商、商品、属性查询对应关系
//            SupplierProduct supplierProduct = iSupplierMapper.querySupplierProductRelation(productId, supplierId);
//            if (supplierProduct == null) {//新增
//                SupplierProduct sp = new SupplierProduct();
//                sp.setProductId(productId);
//                sp.setSupplierId(supplierId);
//                sp.setPrice(price);//价格
//                sp.setStock(stock);//库存
//                sp.setIsSupply(isSupply);//是否供应；0停供，1供应
//                sp.setCreateDate(new Date());
//                sp.setModifyDate(new Date());
//                iSupplierMapper.insertSupplierProduct(sp);
//            } else {//保存
//                SupplierProduct sp = new SupplierProduct();
//                sp.setProductId(productId);
//                sp.setSupplierId(supplierId);
//                sp.setPrice(price);//价格
//                sp.setStock(stock);//库存
//                sp.setIsSupply(isSupply);//是否供应；0停供，1供应
//                sp.setModifyDate(new Date());
//                iSupplierMapper.updateSupplierProduct(sp);
//            }
//            return ServerResponse.createBySuccessMessage("保存成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ServerResponse.createByErrorMessage("保存失败");
//        }
//    }

    /**
     * 保存供应商与货品供应关系
     */
    public ServerResponse saveSupplierProduct(String arrString) {
        try {
//            String productId, String supplierId,
//                    Double price, Double stock, Integer isSupply
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
                List<SupplierProduct> supplierProducts = iSupplierProductMapper.querySupplierProduct(sp.getProductId());
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

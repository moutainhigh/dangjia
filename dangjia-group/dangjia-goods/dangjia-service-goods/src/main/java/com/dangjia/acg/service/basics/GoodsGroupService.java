package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IGoodsGroupMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.ILabelMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.basics.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 关联组业务层
 *
 * @author Ronalcheng
 */
@Service
public class GoodsGroupService {
    @Autowired
    private IGoodsGroupMapper iGoodsGroupMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private ConfigUtil configUtil;

    protected static final Logger LOG = LoggerFactory.getLogger(GoodsGroupService.class);

    /*
     * 添加关联组和货品关联关系
     */
    public ServerResponse addGroupLink(String goodsGroupId, String listOfProductId) {
        try {
            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(goodsGroupId);
            if (goodsGroup == null) {
                return ServerResponse.createByErrorMessage("查无此关联组");
            }
            JSONArray pidList = JSONArray.parseArray(listOfProductId);
            for (int i = 0; i < pidList.size(); i++) {//循环添加商品关系
                JSONObject obj = pidList.getJSONObject(i);
                GroupLink groupLink = new GroupLink();
                groupLink.setGroupId(goodsGroupId);//关联组id
                groupLink.setGroupName(goodsGroup.getName());//关联组名称
                Product product = iProductMapper.getById(obj.getString("productId"));//查询货品
                if (product == null) {
                    continue;
                }
                groupLink.setProductId(obj.getString("productId"));//货品id
                groupLink.setProductName(product.getName());//货品名称
                groupLink.setIsSwitch(0);//可切换性0:可切换；不可切换
                groupLink.setGoodsId(product.getGoodsId());
                groupLink.setGoodsName(iGoodsMapper.selectByPrimaryKey(product.getGoodsId()) == null ? "" : iGoodsMapper.selectByPrimaryKey(product.getGoodsId()).getName());
                iGoodsGroupMapper.addGroupLink(groupLink);//新增关联组货品关系
                List<GroupLink> groupLinkList = iGoodsGroupMapper.queryGroupLinkByPid(product.getId());
                if (groupLinkList.size() >= 2) {//根据货品查询关联关系，超过两条则都修改为不可切换
                    iGoodsGroupMapper.updateGLinkByPid(product.getId(), 1);
                }
            }
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }

    }

    /*
     * 根据关联组id查询货品关联关系
     */
    public ServerResponse getGoodsGroupById(String goodsGroupId) {
        try {
            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(goodsGroupId);
            if (goodsGroup == null) {
                return ServerResponse.createByErrorMessage("查无此关联组");
            }
            Map<String, Object> gMap = new HashMap<>();
            gMap.put("id", goodsGroup.getId());
            gMap.put("name", goodsGroup.getName());
            gMap.put("state", goodsGroup.getState());
            gMap.put("createDate", goodsGroup.getCreateDate().getTime());
            gMap.put("modifyDate", goodsGroup.getModifyDate().getTime());
            List<GroupLink> listGlink = iGoodsGroupMapper.queryGroupLinkByGid(goodsGroupId);
            List<Map<String, Object>> glList = new ArrayList<Map<String, Object>>();
            for (GroupLink groupLink : listGlink) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("glId", groupLink.getId());
                obj.put("groupId", groupLink.getGroupId());
                obj.put("groupName", groupLink.getGroupName());
                obj.put("productId", groupLink.getProductId());
                obj.put("productName", groupLink.getProductName());
                obj.put("goodsId", groupLink.getGoodsId());
                obj.put("goodsName", groupLink.getGoodsName());
                obj.put("isSwitch", groupLink.getIsSwitch());//可切换性0:可切换；不可切换
                obj.put("createDate", groupLink.getCreateDate().getTime());
                glList.add(obj);
            }
            gMap.put("glList", glList);
            return ServerResponse.createBySuccess("查询成功", gMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改货品关联组关系
     *
     * @param listOfProductId
     * @param goodsGroupId
     * @param state
     * @param name
     * @return
     */
    public ServerResponse updateGroupLink(String listOfProductId, String goodsGroupId, int state, String name) {
        try {
            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(goodsGroupId);

            List<GoodsGroup> goodsGroups = iGoodsGroupMapper.selectByName(name);//要修改的name
            if (goodsGroups.size() > 1)
                return ServerResponse.createByErrorMessage("不能修改，该关联组已存在");

            goodsGroup.setState(state);
            goodsGroup.setName(name);
            goodsGroup.setModifyDate(new Date());
            iGoodsGroupMapper.updateByPrimaryKey(goodsGroup);
            iGoodsGroupMapper.deleteGroupLink(goodsGroupId);
            JSONArray goodsList = JSONArray.parseArray(listOfProductId);
            for (int i = 0; i < goodsList.size(); i++) {//循环添加商品关系
                JSONObject job = goodsList.getJSONObject(i);
                String id = job.getString("productId");//得到值
                Product product = iProductMapper.getById(id);//根据商品id查询商品对象
                List<GroupLink> listG = iGoodsGroupMapper.queryGroupLinkByGidAndPid(goodsGroupId, id);//根据关联组id和货品id查询关联关系
                if ((listG != null && listG.size() > 0) || product == null) {
                    continue;
                }
                GroupLink groupLink = new GroupLink();
                groupLink.setGroupId(goodsGroupId);//关联组id
                groupLink.setGroupName(name);//关联组名称
                groupLink.setProductId(id);//货品id
                groupLink.setProductName(product.getName());//货品名称
                groupLink.setGoodsId(product.getGoodsId());
                groupLink.setGoodsName(iGoodsMapper.selectByPrimaryKey(product.getGoodsId()) == null ? "" : iGoodsMapper.selectByPrimaryKey(product.getGoodsId()).getName());
                iGoodsGroupMapper.addGroupLink(groupLink);//新增关联组货品关系
                List<GroupLink> groupLinkList = iGoodsGroupMapper.queryGroupLinkByPid(product.getId());
                if (groupLinkList.size() >= 2) {//根据货品查询关联关系，超过两条则都修改为不可切换
                    iGoodsGroupMapper.updateGLinkByPid(product.getId(), 1);
                }
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 查询所有关联组
     *
     * @return
     */
    public ServerResponse<PageInfo> getAllList(Integer pageNum, Integer pageSize, String name, Integer state) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<GoodsGroup> gList = iGoodsGroupMapper.getAllList(name, state);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (GoodsGroup goodsGroup : gList) {
                Map<String, Object> obj = new HashMap<String, Object>();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", goodsGroup.getId());
                map.put("name", goodsGroup.getName());
                map.put("state", goodsGroup.getState());
                map.put("createDate", goodsGroup.getCreateDate().getTime());
                map.put("modifyDate", goodsGroup.getModifyDate().getTime());
                List<GroupLink> mapList = iGoodsGroupMapper.queryGroupLinkByGid(goodsGroup.getId());
                obj.put("goodsGroup", map);
                obj.put("mapList", mapList);
                list.add(obj);
            }
            PageInfo pageResult = new PageInfo(gList);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    /**
     * 模糊查询商品关联组的商品及下属货品
     * 去除条件 ：自购 ，服务，禁用，查询 product 为空
     *
     * @param pageDTO
     * @param categoryId
     * @param name
     * @return
     */
    public ServerResponse queryGoodsGroupListByCategoryLikeName(PageDTO pageDTO, String categoryId, String name) {
        try {
            LOG.info("queryGoodsGroupListByCategoryLikeName type :");
            if (pageDTO.getPageNum() == null) {
                pageDTO.setPageNum(1);
            }
            if (pageDTO.getPageSize() == null) {
                pageDTO.setPageSize(10);
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//            List<Goods> goodsList = iGoodsMapper.queryGoodsListByCategoryLikeName(categoryId, name);

            // 去除商品是 服务类型的 或者 是自购的
            List<Goods> goodsList = iGoodsMapper.queryGoodsGroupListByCategoryLikeName(categoryId, name,"0","2");

            LOG.info("goodsList:::" + goodsList.size());
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (Goods goods : goodsList) {
                List<Map<String, Object>> mapList = new ArrayList<>();
                List<Product> productList = iProductMapper.queryByGoodsId(goods.getId());
                for (Product p : productList) {
                    LOG.info("p :" + p);
                    if (p.getImage() == null)
                        continue;
                    String[] imgArr = p.getImage().split(",");
                    String imgStr = "";
                    String imgUrlStr = "";
                    for (int i = 0; i < imgArr.length; i++) {
                        if (i == imgArr.length - 1) {
                            imgStr += address + imgArr[i];
                            imgUrlStr += imgArr[i];
                        } else {
                            imgStr += address + imgArr[i] + ",";
                            imgUrlStr += imgArr[i] + ",";
                        }
                    }
                    p.setImage(imgStr);
                    Map<String, Object> map = CommonUtil.beanToMap(p);
                    map.put("imageUrl", imgUrlStr);
                    if (!StringUtils.isNotBlank(p.getLabelId())) {
                        map.put("labelId", "");
                        map.put("labelName", "");
                    } else {
                        map.put("labelId", p.getLabelId());
                        Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                        if (label.getName() != null)
                            map.put("labelName", label.getName());
                    }
                    mapList.add(map);
                }
                LOG.info("mapList:::" + mapList.size());
//                if (mapList.size() > 0)// 有product的商品 才返回给前端
//                {
                Map<String, Object> gMap = CommonUtil.beanToMap(goods);
                gMap.put("productList", mapList);
                gMapList.add(gMap);
//                }
            }
            PageInfo pageResult = new PageInfo(goodsList);
            pageResult.setList(gMapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 新增关联组
     *
     * @param goodsGroup
     * @return
     */
    public ServerResponse addGoodsGroup(GoodsGroup goodsGroup) {
        try {
            List<GoodsGroup> goodsGroups = iGoodsGroupMapper.selectByName(goodsGroup.getName());
            if (goodsGroups.size() > 0)
                return ServerResponse.createByErrorMessage("该关联组已存在");
            iGoodsGroupMapper.addGoodsGroup(goodsGroup);
            return ServerResponse.createBySuccess("新增成功", goodsGroup.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
        }
    }

    /**
     * 修改关联组
     *
     * @param goodsGroup
     * @return
     */
    public ServerResponse updateGoodsGroup(GoodsGroup goodsGroup) {
        try {
            goodsGroup.setModifyDate(new Date());
            iGoodsGroupMapper.updateByPrimaryKeySelective(goodsGroup);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    /*
     * 查找所有顶级分类列表
     */
    public ServerResponse getGoodsCategoryList() {
        try {
            List<Map<String, Object>> goodsCategoryList = iGoodsGroupMapper.getParentTopList();
            return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    /*
     * 查找所有顶级分类的子类列表
     * 根据分类id=parent_id
     */
    public ServerResponse getChildrenGoodsCategoryList(String id) {
        try {
            List<Map<String, Object>> goodsCategoryList = iGoodsGroupMapper.getChildList(id);
            if (goodsCategoryList.size() != 0) {
                return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
            } else {
                throw new BaseException(ServerCode.WRONG_PARAM, "无子类");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    /*
     * 根据分类id查找商品
     */
    public ServerResponse getGoodsListByCategoryId(String id) {
        try {
            List<Map<String, Object>> goodsList = iGoodsGroupMapper.getGoodsList(id);
            if (goodsList.size() != 0) {
                return ServerResponse.createBySuccess("查询成功", goodsList);
            } else {
                throw new BaseException(ServerCode.WRONG_PARAM, "无子类");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    /*
     * 根据商品id查找货品
     */
    public ServerResponse getProductListByGoodsId(String id) {
        try {
            List<Map<String, Object>> productList = iGoodsGroupMapper.getProductList(id);
            if (productList.size() != 0) {
                return ServerResponse.createBySuccess("查询成功", productList);
            } else {
                throw new BaseException(ServerCode.WRONG_PARAM, "无货品");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    /*
     * 根据关联组id删除关联组和货品关联关系
     */
    public ServerResponse deleteGoodsGroupById(String goodsGroupId) {
        try {
            if (true) {
                return ServerResponse.createByErrorMessage("不能执行删除操作");
            }

            List<GroupLink> mapList = iGoodsGroupMapper.queryGroupLinkByGid(goodsGroupId);
            List<Product> productList = new ArrayList<>();
            for (GroupLink groupLink : mapList) {
                productList.add(iProductMapper.selectByPrimaryKey(groupLink.getProductId()));
            }
            iGoodsGroupMapper.deleteByPrimaryKey(goodsGroupId);
            iGoodsGroupMapper.deleteGroupLink(goodsGroupId);
            for (Product product : productList) {
                List<GroupLink> groupLinkList = iGoodsGroupMapper.queryGroupLinkByPid(product.getId());
                if (groupLinkList.size() >= 2) {//根据货品查询关联关系，超过两条则都修改为不可切换
                    iGoodsGroupMapper.updateGLinkByPid(product.getId(), 1);
                } else {
                    iGoodsGroupMapper.updateGLinkByPid(product.getId(), 0);
                }
            }
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }

}
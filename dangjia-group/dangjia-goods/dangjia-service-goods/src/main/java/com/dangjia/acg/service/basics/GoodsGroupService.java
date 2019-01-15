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
import com.dangjia.acg.mapper.basics.*;
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
    private IGroupLinkMapper iGroupLinkMapper;
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
            if (true)
                return ServerResponse.createByErrorMessage("接口弃用");
//            GoodsGroup goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(goodsGroupId);
//
//            List<GoodsGroup> goodsGroups = iGoodsGroupMapper.selectByName(name);//要修改的name
//            if (goodsGroups.size() > 1)
//                return ServerResponse.createByErrorMessage("不能修改，该关联组已存在");
//
//            goodsGroup.setState(state);
//            goodsGroup.setName(name);
//            goodsGroup.setModifyDate(new Date());
//            iGoodsGroupMapper.updateByPrimaryKey(goodsGroup);
//            iGoodsGroupMapper.deleteGroupLink(goodsGroupId);
//            JSONArray goodsList = JSONArray.parseArray(listOfProductId);
//            for (int i = 0; i < goodsList.size(); i++) {//循环添加商品关系
//                JSONObject job = goodsList.getJSONObject(i);
//                String id = job.getString("productId");//得到值
//                Product product = iProductMapper.getById(id);//根据商品id查询商品对象
//                List<GroupLink> listG = iGoodsGroupMapper.queryGroupLinkByGidAndPid(goodsGroupId, id);//根据关联组id和货品id查询关联关系
//                if ((listG != null && listG.size() > 0) || product == null) {
//                    continue;
//                }
//                GroupLink groupLink = new GroupLink();
//                groupLink.setGroupId(goodsGroupId);//关联组id
//                groupLink.setGroupName(name);//关联组名称
//                groupLink.setProductId(id);//货品id
//                groupLink.setProductName(product.getName());//货品名称
//                groupLink.setGoodsId(product.getGoodsId());
//                groupLink.setGoodsName(iGoodsMapper.selectByPrimaryKey(product.getGoodsId()) == null ? "" : iGoodsMapper.selectByPrimaryKey(product.getGoodsId()).getName());
//                iGoodsGroupMapper.addGroupLink(groupLink);//新增关联组货品关系
////                List<GroupLink> groupLinkList = iGoodsGroupMapper.queryGroupLinkByPid(product.getId());
//                List<GroupLink> groupLinkList = iGoodsGroupMapper.queryGroupLinkByPidAndGid(product.getId(), groupLink.getGoodsId());
//                LOG.info("groupLinkList size::" + groupLinkList.size() + " productId:" + product.getId() + " goodsId:" + groupLink.getGoodsId());
//                if (groupLinkList.size() >= 2) {//根据货品查询关联关系，超过两条则都修改为不可切换
//                    iGoodsGroupMapper.updateGLinkByPid(product.getId(), 1);
//                }
//            }
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
                List<Map<String, Object>> mapList = iGoodsGroupMapper.queryMapGroupLinkByGid(goodsGroup.getId());
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
            List<Goods> goodsList = iGoodsMapper.queryGoodsGroupListByCategoryLikeName(categoryId, name, "0", "2");
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (Goods goods : goodsList) {
                List<Map<String, Object>> mapList = new ArrayList<>();
                List<Product> productList = iProductMapper.queryByGoodsId(goods.getId());
                for (Product p : productList) {
//                    LOG.info("p :" + p);
                    if (p.getType() == 0)//去除禁用的
                        continue;
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
                Map<String, Object> gMap = CommonUtil.beanToMap(goods);
                gMap.put("productList", mapList);
                gMapList.add(gMap);
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
     * @param jsonStr
     * @return
     */
    public ServerResponse addGoodsGroup(String jsonStr) {
        try {
//            LOG.info("jsonStr :" + jsonStr);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String groupId = jsonObject.getString("id");
            String groupName = jsonObject.getString("name");
            Integer groupState = jsonObject.getInteger("state");
            String addProductIds = jsonObject.getString("addProductIds");

            boolean isUpdateProduct = false; //是否 有添加或删除 product  ,或者 修改 不启用状态
            boolean isUpdateState = false; //是否 修改 不启用状态

            GoodsGroup goodsGroup = null;  //数据库里存在的 父标签对象
            if (!StringUtils.isNotBlank(groupId))//没有id则新增
            {
                if (iGoodsGroupMapper.selectByName(groupName).size() > 0)
                    return ServerResponse.createByErrorMessage("该关联组已存在");
                goodsGroup = new GoodsGroup();
                goodsGroup.setName(groupName);
                goodsGroup.setState(groupState);
                //初始化 所有可切换的 关联组id
                goodsGroup.setSwitchArr("");
                iGoodsGroupMapper.insertSelective(goodsGroup);
//                iGoodsGroupMapper.addGoodsGroup(goodsGroup);
            } else {//修改
                goodsGroup = iGoodsGroupMapper.selectByPrimaryKey(groupId);
                if (goodsGroup == null)
                    return ServerResponse.createByErrorMessage("该关联组不存在");

                List<GoodsGroup> goodsGroups = iGoodsGroupMapper.selectByName(groupName);//要修改的name
                if (goodsGroups.size() > 1)
                    return ServerResponse.createByErrorMessage("不能修改，该关联组已存在");
                goodsGroup.setName(groupName);
                goodsGroup.setModifyDate(new Date());
                if (!goodsGroup.getState().equals(groupState)) {
                    isUpdateState = true;
                    goodsGroup.setState(groupState);
                }
                iGoodsGroupMapper.updateByPrimaryKey(goodsGroup);
            }


            //添加该关联组 对应的所有的可切换的关联组id
            String[] addProductIdsArr = {};
            if (StringUtils.isNotBlank(addProductIds)) {
                addProductIdsArr = addProductIds.split(",");
                for (int i = 0; i < addProductIdsArr.length; i++) {
                    String productId = addProductIdsArr[i];
                    Product product = iProductMapper.selectByPrimaryKey(productId);//根据商品id查询商品对象
                    if (product == null)
                        return ServerResponse.createByErrorMessage("该product不存在");
                    GroupLink groupLink = new GroupLink();
                    groupLink.setGroupId(goodsGroup.getId());//关联组id
                    groupLink.setGroupName(goodsGroup.getName());//关联组名称
                    groupLink.setProductId(product.getId());//货品id
                    groupLink.setProductName(product.getName());//货品名称
                    groupLink.setGoodsId(product.getGoodsId());
                    groupLink.setIsSwitch(1);//默认不可切换
                    groupLink.setGoodsName(iGoodsMapper.selectByPrimaryKey(product.getGoodsId()) == null ? "" : iGoodsMapper.selectByPrimaryKey(product.getGoodsId()).getName());
                    iGoodsGroupMapper.addGroupLink(groupLink);//新增关联组货品关系
//                    iGoodsGroupMapper.insertSelective(groupLink);//新增关联组货品关系
                    isUpdateProduct = true;
                }
            }

            // deleteProductIds
//          iGoodsGroupMapper.deleteGroupLink(goodsGroupId);
            String deleteProductIds = jsonObject.getString("deleteProductIds");
            String[] deleteProductIdArr = {};
            if (StringUtils.isNotBlank(deleteProductIds)) {
                deleteProductIdArr = jsonObject.getString("deleteProductIds").split(",");
                for (int i = 0; i < deleteProductIdArr.length; i++) {
                    GroupLink deleteGL = iGoodsGroupMapper.queryGroupLinkByGroupIdAndPid(goodsGroup.getId(), deleteProductIdArr[i]);
                    iGoodsGroupMapper.deleteGroupLinkById(deleteGL.getId());
                    isUpdateProduct = true;
                }
            }

            /****************设置 关联组的可切换SwitchArr属性 及 组成员的product 是否可切换 is_switch 属性************/
            String strOldSwitch = goodsGroup.getSwitchArr();
            if (isUpdateProduct) {
                //      *******************设置新的*********************
                // 重新找出最新的可以切换的 关联组
                //设置最新的关联组中所有 product 是否切换
                String strNewSwitchArr = setSwitchArrByGoodsGroup(goodsGroup);
//                LOG.info("strNewSwitchArr:" + strNewSwitchArr);
                setSwitchByGoodsGroups(strNewSwitchArr);
            }

            //如果有老的可切换的关联组 ，并且当前组成员 有改动，就要重新计算更新 老关联组的成员 是否可以切换
            if ((StringUtils.isNotBlank(strOldSwitch) && isUpdateProduct) || isUpdateState) { //或者 修改了 不启用状态 ，就重新计算 关联组 下的所有货品是否可切换
                String[] oldSwitchArr = strOldSwitch.split(",");
                if (oldSwitchArr.length > 1) {//注意：因为每个关联组 可切换的包括自己，所以，必须2个以上才可以 切换 ，只有 1个不能切换
                    String oldGroupId = ""; //老的group,非当前groupId的 要重新 计算匹配 是否 可以切换、
                    for (int i = 0; i < oldSwitchArr.length; i++) {
                        if (!oldSwitchArr[i].equals(goodsGroup.getId()))
                            oldGroupId = oldSwitchArr[i];
                    }
                    GoodsGroup oldGoodsGroup = iGoodsGroupMapper.selectByPrimaryKey(oldGroupId);
                    // 重新找出最新的可以切换的 关联组
                    String strOldSwitchArr = setSwitchArrByGoodsGroup(oldGoodsGroup);
//                    LOG.info("strOldSwitchArr:" + strOldSwitchArr);
                    //找出 老的 可切换的所有关联组
                    setSwitchByGoodsGroups(strOldSwitchArr);
                }
            }

            return ServerResponse.createBySuccess("操作成功", goodsGroup.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "操作失败");
        }
    }

    /**
     * 找出关联组的所有product 是否可以切换
     * 重新设置 指定多个关联组中的product 是否可切换
     *
     * @param switchArrGroupId
     */
    private void setSwitchByGoodsGroups(String switchArrGroupId) {
        try {
            if (StringUtils.isNotBlank(switchArrGroupId)) {
                String[] groupIdArr = switchArrGroupId.split(",");

                //设置 关联组中，所有product 是否可切换
                List<String> productIdLists = new ArrayList<>();

                //设置 关联组中，所有 不可切换的 goodsId
                List<String> goodsIdNoSwitchLists = new ArrayList<>();

                //switchArrGroupId 所有关联组的成员
                List<GroupLink> groupLinkLists = new ArrayList<>();

                for (String groupId : groupIdArr) {
                    List<GroupLink> groupLinkList = iGoodsGroupMapper.queryGroupLinkByGid(groupId);
                    for (GroupLink groupLink : groupLinkList) {
                        groupLinkLists.add(groupLink);
                        productIdLists.add(groupLink.getProductId());
                    }
                }

                for (GroupLink groupLink : groupLinkLists) {
                    groupLink.setIsSwitch(1);
                    //如果productId 只有一个，没有重复的，就是可切换的 , 并且只是有2个组可以切换才行
                    if (Collections.frequency(productIdLists, groupLink.getProductId()) == 1 && groupIdArr.length >= 2) {
                        groupLink.setIsSwitch(0);
                        iGroupLinkMapper.updateByPrimaryKeySelective(groupLink);
                        LOG.info("productIdLists groupLink :" + groupLink.getProductName() + " setIsSwitch:" + groupLink.getIsSwitch());
                    } else {//不能切换的goodsId
                        if (!goodsIdNoSwitchLists.contains(groupLink.getGoodsId()))
                            goodsIdNoSwitchLists.add(groupLink.getGoodsId());
                    }
                }

                for (GroupLink groupLink : groupLinkLists) {
                    //查找 关联组成员 为 不能可切换的 goodsId 下的所有成员 , 设置为 不可切换
                    if (Collections.frequency(goodsIdNoSwitchLists, groupLink.getGoodsId()) > 0) {
                        groupLink.setIsSwitch(1);
                        //设置关联组中
                        LOG.info("goodsIdNoSwitchLists groupLink :" + groupLink.getProductName() + " setIsSwitch:" + groupLink.getIsSwitch());
                        iGroupLinkMapper.updateByPrimaryKeySelective(groupLink);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "操作失败");
        }
    }

    /**
     * 查找并刷新存储SwitchArr 所有组中，和 goodsGroup 可切换的所有关联组id
     * （所有组里面的goodsId 如果是完全相同，就是可切换）
     *
     * @param goodsGroup
     * @return
     */
    private String setSwitchArrByGoodsGroup(GoodsGroup goodsGroup) {
        List<GroupLink> nowGroupLinkList = iGoodsGroupMapper.queryGroupLinkByGid(goodsGroup.getId());
        List<String> goodsIdSameByGroupList = new ArrayList<>();//存放 goods 完全一样的关联组

        List<String> nowGoodsIdList = new ArrayList<>();//存放 goods 完全一样的关联组
        for (GroupLink groupLink : nowGroupLinkList)
            nowGoodsIdList.add(groupLink.getGoodsId());

        //从所有组里中找 gooods 个数相同的 关联组
        //遍历 所有组里面的goods 如果是完全相同，就可以切换
        List<GoodsGroup> srcGoodsGroups = iGoodsGroupMapper.getAllList(null, 1);//查所有关联组
        for (GoodsGroup srcGl : srcGoodsGroups) {
//            if (srcGl.getId().equals(goodsGroup.getId()))
//                continue;//如果是当前自己就不添加了
            List<GroupLink> srcGroupLinkList = iGoodsGroupMapper.queryGroupLinkByGid(srcGl.getId());
            //数据库元数据 和 正在操作的 关联组 的 商品数量相同
            if (srcGroupLinkList.size() == nowGroupLinkList.size()) {
                boolean isSwitch = true; //默认可以切换
                for (GroupLink srcGroupLink : srcGroupLinkList) {
                    //如果 数据库里关联组元数据中goodsId， 在当前关联组中只要有一个不存在，就不能切换
                    if (!nowGoodsIdList.contains(srcGroupLink.getGoodsId())) {
//                        LOG.info("srcGroupLink.getGoodsId() 该关联组不能切换 ：" + srcGroupLink.getGoodsId() + srcGroupLink.getProductName());
                        isSwitch = false;
                        break;
                    }
                }
                if (isSwitch)
                    goodsIdSameByGroupList.add(srcGl.getId());//存放 goods 完全相同的 关联组id
            }
        }

        String retSwitchArr = "";
        for (String strGroupId : goodsIdSameByGroupList) {
            if (retSwitchArr == "")
                retSwitchArr = strGroupId;
            else
                retSwitchArr = retSwitchArr + "," + strGroupId;
        }

        for (String groupId : goodsIdSameByGroupList) {
            GoodsGroup group = iGoodsGroupMapper.selectByPrimaryKey(groupId);
            group.setSwitchArr(retSwitchArr);
            iGoodsGroupMapper.updateByPrimaryKeySelective(group);
        }
        return retSwitchArr;
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
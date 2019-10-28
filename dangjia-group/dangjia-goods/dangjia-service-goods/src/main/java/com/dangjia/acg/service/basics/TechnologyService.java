package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.actuary.ISearchBoxMapper;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.mapper.product.IBasicsProductTemplateMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.SearchBox;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @类 名： TechnologyServiceImpl
 * @功能描述： 工艺service实现类
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class TechnologyService {
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private ActuaryOperationService actuaryOperationService;
    @Autowired
    private ITechnologyMapper iTechnologyMapper;
    @Autowired
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;
    //@Autowired
    //private IWorkerGoodsMapper iWorkerGoodsMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    @Autowired
    private ISearchBoxMapper searchBoxMapper;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IGoodsMapper goodsMapper;

    /**
     * 添加多个人工工艺和添加服务节点
     *
     * @param jsonStr          多个工艺对象list的json
     * @param workerTypeId     工序id : 如果是 服务product类型的 传递 空 。查询的时候忽略 该字段
     * @param materialOrWorker 0包工包料 工艺;1:人工工艺
     * @param goodsId          根据 materialOrWorker字段决定：  0:服务productId;  1:人工商品
     * @return
     */
    public String insertTechnologyList(String jsonStr, String workerTypeId, Integer materialOrWorker, String goodsId) {
        try {
            if (!StringUtils.isNotBlank(jsonStr))
                return "1";
            JSONArray technologyArr = JSONArray.parseArray(jsonStr);
            for (int i = 0; i < technologyArr.size(); i++) {//遍历工艺
                JSONObject obj = technologyArr.getJSONObject(i);
                String id = obj.getString("id");//
                String name = obj.getString("name");//
                for (int j = i + 1; j < technologyArr.size(); j++) {//不能重复
                    JSONObject objJ = technologyArr.getJSONObject(j);
                    String nameJ = objJ.getString("name");//
                    if (nameJ.equals(name))
                        return "工艺名称不能有重复的";
                }
                List<Technology> technologyList = iTechnologyMapper.getByName(workerTypeId, name, materialOrWorker, goodsId);
                if (!StringUtils.isNotBlank(id)) {//为空 ： 就是新增
                    if (technologyList.size() > 0)
                        return "工艺名称已经存在";
                } else { //修改
                    Technology technology = iTechnologyMapper.selectByPrimaryKey(id);
                    if (!technology.getName().equals(name)) {//是修改了名字 ，
                        if (technologyList.size() > 0)
                            return "工艺名称已经存在";
                    }
                }
            }
            for (int j = 0; j < technologyArr.size(); j++) {//遍历工艺
                JSONObject obj = technologyArr.getJSONObject(j);
                String id = obj.getString("id");//
                String name = obj.getString("name");//
                String content = obj.getString("content");//
                Integer type = obj.getInteger("type");//
                String image = obj.getString("image");//
                String sampleImage = obj.getString("sampleImage");//
                Technology t = new Technology();
                t.setName(name);
                t.setContent(content);
                t.setGoodsId(goodsId);//根据 materialOrWorker字段决定：  0:服务productId;  1:人工商品
                t.setImage(image);
                t.setSampleImage(sampleImage);
                t.setType(type);
                //0服务工艺;1:人工工艺
                if (materialOrWorker == 1) { //1人工工艺
                    t.setWorkerTypeId(workerTypeId);
//                    t.setType(type);
                    t.setMaterialOrWorker(1);
                } else {//0服务工艺
//                    t.setType(1);
                    t.setMaterialOrWorker(0);
                }
                if (!StringUtils.isNotBlank(id)) {//为空 ： 就是新增
                    iTechnologyMapper.insertSelective(t);
                } else { //修改
                    Technology technology = iTechnologyMapper.selectByPrimaryKey(id);
                    technology.setName(t.getName());
                    technology.setContent(t.getContent());
                    technology.setGoodsId(t.getGoodsId());
                    technology.setImage(t.getImage());
                    technology.setSampleImage(t.getSampleImage());
                    technology.setWorkerTypeId(t.getWorkerTypeId());
                    technology.setType(t.getType());
                    technology.setMaterialOrWorker(t.getMaterialOrWorker());
                    technology.setModifyDate(new Date());
                    iTechnologyMapper.updateByPrimaryKeySelective(technology);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "操作工艺失败";
        }
    }

    /**
     * 添加人工工艺和添加服务节点
     */
    public ServerResponse insertTechnology(Technology technology) {
        try {
            List<Technology> technologyList = iTechnologyMapper.query(technology.getWorkerTypeId(), technology.getName(), technology.getMaterialOrWorker());
            if (technologyList.size() > 0) {
                return ServerResponse.createByErrorMessage("工艺名称不能重复");
            }
            if (technology.getMaterialOrWorker()!=null&&technology.getMaterialOrWorker() != 1) {
                technology.setType(1);
                technology.setMaterialOrWorker(0);
            }else{
                technology.setMaterialOrWorker(1);
            }
            iTechnologyMapper.insertSelective(technology);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    //修改工艺说明
    public ServerResponse updateTechnology(Technology technology) {
        try {
            if (!StringUtils.isNotBlank(technology.getName()))
                return ServerResponse.createByErrorMessage("名称不能为空");
            Technology t = iTechnologyMapper.selectByPrimaryKey(technology.getId());
            if (t == null) {
                return ServerResponse.createByErrorMessage("不存在此工艺,修改失败");
            }
            if (!t.getName().equals(technology.getName())) {//如果修改了名称 就判断，修改的名字 是否已经存在
                List<Technology> technologyList = iTechnologyMapper.query(t.getWorkerTypeId(), technology.getName(), t.getMaterialOrWorker());
                if (technologyList.size() > 0)
                    return ServerResponse.createByErrorMessage("工艺名称已存在");
            }
            if (technology.getMaterialOrWorker()!=null&&technology.getMaterialOrWorker() != 1) {
                technology.setType(1);
                technology.setMaterialOrWorker(0);
            }else{
                technology.setMaterialOrWorker(1);
            }
            iTechnologyMapper.updateByPrimaryKeySelective(technology);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    //删除工艺说明
    public ServerResponse deleteTechnology(String id) {
        try {
            iTechnologyMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除工艺说明成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除工艺说明失败");
        }
    }

    //根据工种查询所有工艺说明
    public ServerResponse queryTechnology(PageDTO pageDTO, String workerTypeId, String name, Integer materialOrWorker) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Technology> tList = iTechnologyMapper.query(workerTypeId, name, materialOrWorker);
            PageInfo pageResult = new PageInfo(tList);
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Technology t : tList) {
                Map<String, Object> map = BeanUtils.beanToMap(t);
                /*Example example = new Example(WorkerGoods.class);
                example.createCriteria().andCondition(" FIND_IN_SET( '"+t.getId()+"', technology_ids)");
                List<WorkerGoods> wList = iWorkerGoodsMapper.selectByExample(example);*/
                Example example = new Example(DjBasicsProductTemplate.class);
                example.createCriteria().andCondition(" FIND_IN_SET( '"+t.getId()+"', technology_ids)");
                List<DjBasicsProductTemplate> wList = iBasicsProductTemplateMapper.selectByExample(example);
                String workerTypeName = "";
                ServerResponse response = workerTypeAPI.getWorkerType(t.getWorkerTypeId());
                if (response.isSuccess()) {
                    workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
                }
                map.put("workerTypeName", workerTypeName);
                map.put("workerNum", wList.size());
                map.put("workerList", wList);
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                if (t.getImage() != null) {
                    String[] imgArr = t.getImage().split(",");
                    StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                }
                map.put("image", imgStr.toString());
                map.put("imageUrl", imgUrlStr.toString());
                mapList.add(map);
            }
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据工艺明细
    public ServerResponse getTechnology(String technologyId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Technology t = iTechnologyMapper.selectByPrimaryKey(technologyId);
            Map<String, Object> map = BeanUtils.beanToMap(t);
            /*Example example = new Example(WorkerGoods.class);
            example.createCriteria().andEqualTo(WorkerGoods.SHOW_GOODS,1)
                    .andCondition(" FIND_IN_SET( '"+t.getId()+"', technology_ids)");*/
          //  List<WorkerGoods> wList = iWorkerGoodsMapper.selectByExample(example);
            List<DjBasicsProductTemplate> wList=iBasicsProductTemplateMapper.queryProductByTechnologyIds(t.getId());
            List<Map<String, Object>> mapList = new ArrayList<>();
            String workerTypeName = "";
            ServerResponse response = workerTypeAPI.getWorkerType(t.getWorkerTypeId());
            if (response.isSuccess()) {
                workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
            }
            map.put("workerTypeName", workerTypeName);
            map.put("workerNum", wList.size());
            for (DjBasicsProductTemplate w : wList) {
                Map<String, Object> wmap = BeanUtils.beanToMap(w);
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                if (w.getImage() != null) {
                    String[] imgArr = w.getImage().split(",");
                    StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                }
                wmap.put("image", imgStr.toString());
                wmap.put("imageUrl", imgUrlStr.toString());
                mapList.add(wmap);
            }
            map.put("workerList", mapList);
            StringBuilder imgStr = new StringBuilder();
            StringBuilder imgUrlStr = new StringBuilder();
            if (t.getImage() != null) {
                String[] imgArr = t.getImage().split(",");
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
            }
            map.put("image", imgStr.toString());
            map.put("imageUrl", imgUrlStr.toString());
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    //根据名称查询所有工艺（名称去重）
    public ServerResponse queryByName(String name,String workerTypeId) {
        try {
            List<Technology> tList = iTechnologyMapper.queryByName(name, workerTypeId);
            return ServerResponse.createBySuccess("查询成功", tList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    //根据商品id查询人工商品关联工艺实体
    public ServerResponse queryTechnologyByWgId(String workerGoodsId) {
        try {

            //WorkerGoods wg = iWorkerGoodsMapper.selectByPrimaryKey(workerGoodsId);
            DjBasicsProductTemplate dw=iBasicsProductTemplateMapper.selectByPrimaryKey(workerGoodsId);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Technology> tList = iTechnologyMapper.queryTechnologyByWgId(dw.getTechnologyIds());
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Technology t : tList) {
                Map<String, Object> map =BeanUtils.beanToMap(t);
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                if (t.getImage() != null) {
                    String[] imgArr = t.getImage().split(",");
                    StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                }
                map.put("image", imgStr.toString());
                map.put("imageUrl", imgUrlStr.toString());
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据内容模糊搜索
    public ServerResponse queryByName(String name, PageDTO pageDTO, String cityId, int type) {
        JSONArray arr = new JSONArray();
        PageInfo pageResult = null;
        try {
            Example example = new Example(SearchBox.class);
            example.createCriteria().andEqualTo("content", name);
            List<SearchBox> slist = searchBoxMapper.selectByExample(example);
            if (slist.size() > 0) {
                SearchBox s = slist.get(0);
                s.setNumber(s.getNumber() + 1);
                s.setModifyDate(new Date());
                searchBoxMapper.updateByPrimaryKeySelective(s);
            } else {
                SearchBox serchBox = new SearchBox();
                serchBox.setContent(name);
                serchBox.setNumber(1);
                searchBoxMapper.insertSelective(serchBox);
            }

           /* if (type == 2) {
                //根据内容模糊搜索人工
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<WorkerGoods> wList = iWorkerGoodsMapper.queryByName(name, null);
                pageResult = new PageInfo<>(wList);
                for (WorkerGoods wg : wList) {
                    JSONObject object = new JSONObject();
                    object.put("image", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + wg.getImage());
                    object.put("price", wg.getPrice() + "/" + wg.getUnitName());
                    object.put("name", wg.getName());
                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.GOODSDETAIL, "", cityId, "商品详情") + "&gId=" + wg.getId() + "&type=" + DjConstants.GXType.RENGGONG;
                    object.put("url", url);//0:工艺；1：商品；2：人工
                    arr.add(object);
                }
            }else{*/
                //根据内容模糊搜索商品
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<Product> pList = iProductMapper.serchBoxName(name);
                pageResult = new PageInfo<>(pList);
                for (Product product : pList) {
                    if(product.getType()==0){
                        type=DjConstants.GXType.CAILIAO;
                    }else{
                        type=DjConstants.GXType.FUWU;
                    }
                    String convertUnitName = iUnitMapper.selectByPrimaryKey(product.getConvertUnit()).getName();
                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                            String.format(DjConstants.YZPageAddress.GOODSDETAIL, "", cityId, "商品详情") +
                            "&gId=" + product.getId() +
                            "&type=" + type;
                    JSONObject object = new JSONObject();
                    object.put("image", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + product.getImage());
                    object.put("price", product.getPrice() + "/" + convertUnitName);
                    object.put("name", product.getName());
                    object.put("url", url);//0:工艺；1：商品；2：人工
                    arr.add(object);
                }
           // }

            pageResult.setList(arr);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 查询热门搜索
     */
    public ServerResponse getHeatSearchBox(String cityId) {
        try {
            return ServerResponse.createBySuccess("查询成功", searchBoxMapper.getHeatSearchBox(cityId));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    public List<BudgetMaterial> getBudgetMaterialList(String houseId) {
        Example example = new Example(BudgetMaterial.class);
        example.createCriteria()
                .andEqualTo(BudgetMaterial.HOUSE_ID, houseId)
                .andEqualTo(BudgetMaterial.DELETE_STATE, 2);
        return budgetMaterialMapper.selectByExample(example);
    }

    public Product getProduct(String productId) {
        return productMapper.selectByPrimaryKey(productId);
    }

    public String getAttributes(String productId) {
        return actuaryOperationService.getAttributes(productId);
    }

    public List<BudgetMaterial> getInIdsBudgetMaterialList(String budgetIds) {
        if (budgetIds == null) {
            budgetIds = "";
        }
        String[] ids = budgetIds.split(",");
        Example example = new Example(BudgetMaterial.class);
        example.createCriteria().andIn(BudgetMaterial.ID, Arrays.asList(ids));
        return budgetMaterialMapper.selectByExample(example);
    }

    public void updateBudgetMaterial(String json) {
        BudgetMaterial budgetMaterial = new Gson().fromJson(json, new TypeToken<BudgetMaterial>() {
        }.getType());
        budgetMaterialMapper.updateByPrimaryKeySelective(budgetMaterial);
    }
}

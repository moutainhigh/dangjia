package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.product.ICategoryLabelMapper;
import com.dangjia.acg.modle.product.CategoryLabel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.*;

/**
 * @类 名： LabelServiceImpl.java
 * @功能描述： 商品标签Service实现类
 * @作者信息： ysl
 * @创建时间： 2018-12-11下午2:29:07
 */

@Service
public class CategoryLabelService {
    /**
     * 注入LabelDao接口
     */
    @Autowired
    private ICategoryLabelMapper iCategoryLabelMapper;


    protected static final Logger LOG = LoggerFactory.getLogger(CategoryLabelService.class);

    //查询所有的标签
    public ServerResponse<PageInfo> getAllCategoryLabel(PageDTO pageDTO) {
        try {
            if (pageDTO == null) {
                pageDTO = new PageDTO();
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<CategoryLabel> labelList = iCategoryLabelMapper.getCategoryLabel();
            for (CategoryLabel categoryLabel : labelList) {
                Map<String, Object> map = new HashMap<>();
                if (!StringUtils.isNotBlank(categoryLabel.getId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");
                } else {
                    map.put("labelId", categoryLabel.getId());
                    map.put("labelName", categoryLabel.getName());
                }
                mapList.add(map);
            }
            PageInfo pageResult = new PageInfo(labelList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询所有的标签(分类下拉选项,不需要分页）
    public ServerResponse<PageInfo> getAllCategoryLabelList() {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<CategoryLabel> labelList = iCategoryLabelMapper.getCategoryLabel();
            for (CategoryLabel categoryLabel : labelList) {
                Map<String, Object> map = new HashMap<>();
                if (!StringUtils.isNotBlank(categoryLabel.getId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");
                } else {
                    map.put("labelId", categoryLabel.getId());
                    map.put("labelName", categoryLabel.getName());
                }
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //新增商品标签
    public ServerResponse insert(String labelName) {
        try {
            if (iCategoryLabelMapper.getCategoryLabelByName(labelName).size() > 0)
                return ServerResponse.createByErrorMessage("标签名称已存在");

            CategoryLabel categoryLabel = new CategoryLabel();
            categoryLabel.setName(labelName);
            iCategoryLabelMapper.insert(categoryLabel);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    //修改类别标签
    public ServerResponse update(String labelId, String labelName) {
        try {
            CategoryLabel oldLabel = iCategoryLabelMapper.selectByPrimaryKey(labelId);
            if (oldLabel == null)
                return ServerResponse.createByErrorMessage("没有该类别标签");

            if (!oldLabel.getName().equals(labelName)) {
                if (iCategoryLabelMapper.getCategoryLabelByName(labelName).size() > 0)
                    return ServerResponse.createByErrorMessage("标签名称已存在");
            }
//            oldLabel.setId(labelId);
            oldLabel.setName(labelName);
            oldLabel.setModifyDate(new Date());
            iCategoryLabelMapper.updateByPrimaryKeySelective(oldLabel);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 保存类别的标签
     *
     * @param productArr
     * @return
     */
    public ServerResponse saveCategoryLabel(String productArr) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(productArr);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String labelId = obj.getString("labelId");//标签id
                CategoryLabel categoryLabel = iCategoryLabelMapper.selectByPrimaryKey(labelId);
                if (categoryLabel == null)
                    return ServerResponse.createByErrorMessage("标签不存在");
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }

    }


    //根据id查询类别标签
    public ServerResponse selectCategoryLabelById(String id) {
        try {
            CategoryLabel categoryLabel = iCategoryLabelMapper.selectByPrimaryKey(id);
            Map<String, Object> map = new HashMap<>();
            map.put("id", categoryLabel.getId());
            map.put("name", categoryLabel.getName());
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据id删除商品标签
    public ServerResponse deleteCategoryLabelById(String labelId) {
        try {
            iCategoryLabelMapper.deleteByPrimaryKey(labelId);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }
}

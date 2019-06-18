package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.ILabelMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @类 名： LabelServiceImpl.java
 * @功能描述： 商品标签Service实现类
 * @作者信息： ysl
 * @创建时间： 2018-12-11下午2:29:07
 */

@Service
public class LabelService {
    /**
     * 注入LabelDao接口
     */
    @Autowired
    private ILabelMapper iLabelMapper;

    @Autowired
    private IProductMapper iProductMapper;

    protected static final Logger LOG = LoggerFactory.getLogger(LabelService.class);

    //查询所有的标签
    public ServerResponse<PageInfo> getAllLabel(PageDTO pageDTO) {
        try {
            if (pageDTO == null) {
                pageDTO = new PageDTO();
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<Label> labelList = iLabelMapper.getLabel();
            for (Label label : labelList) {
                Map<String, Object> map = new HashMap<>();
                if (!StringUtils.isNotBlank(label.getId())) {
                    map.put("labelId", "");
                    map.put("labelName", "");
                } else {
                    map.put("labelId", label.getId());
                    map.put("labelName", label.getName());
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

    //新增商品标签
    public ServerResponse insert(String labelName) {
        try {
            if (iLabelMapper.getLabelByName(labelName).size() > 0)
                return ServerResponse.createByErrorMessage("标签名称已存在");

            Label label = new Label();
            label.setName(labelName);
            iLabelMapper.insert(label);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    //修改商品标签
    public ServerResponse update(String labelId, String labelName) {
        try {
            Label oldLabel = iLabelMapper.selectByPrimaryKey(labelId);
            if (oldLabel == null)
                return ServerResponse.createByErrorMessage("没有该商品标签");

            if (!oldLabel.getName().equals(labelName)) {
                if (iLabelMapper.getLabelByName(labelName).size() > 0)
                    return ServerResponse.createByErrorMessage("标签名称已存在");
            }
//            oldLabel.setId(labelId);
            oldLabel.setName(labelName);
            oldLabel.setModifyDate(new Date());
            iLabelMapper.updateByPrimaryKeySelective(oldLabel);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 保存货品的标签
     *
     * @param productArr
     * @return
     */
    public ServerResponse saveProductLabel(String productArr) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(productArr);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String labelId = obj.getString("labelId");//标签id
                Label label = iLabelMapper.selectByPrimaryKey(labelId);
                if (label == null)
                    return ServerResponse.createByErrorMessage("标签不存在");
            }

            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String productId = obj.getString("productId");//货品id
                String labelId = obj.getString("labelId");//标签id

                Product product = iProductMapper.selectByPrimaryKey(productId);
                product.setModifyDate(new Date());
                product.setLabelId(labelId);
                iProductMapper.updateByPrimaryKeySelective(product);
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存失败");
        }

    }


    //根据id查询商品标签
    public ServerResponse selectById(String id) {
        try {
            Label label = iLabelMapper.selectByPrimaryKey(id);
            Map<String, Object> map = new HashMap<>();
            map.put("id", label.getId());
            map.put("name", label.getName());
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据id删除商品标签
    public ServerResponse deleteById(String labelId) {
        try {
            iLabelMapper.deleteByPrimaryKey(labelId);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }
}

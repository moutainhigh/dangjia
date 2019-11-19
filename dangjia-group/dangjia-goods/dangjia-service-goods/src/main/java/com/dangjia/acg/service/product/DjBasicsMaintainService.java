package com.dangjia.acg.service.product;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.product.DjBasicsMaintainMapper;
import com.dangjia.acg.modle.product.DjBasicsMaintain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class DjBasicsMaintainService {
    @Autowired
    private DjBasicsMaintainMapper djBasicsMaintainMapper;


    /**
     * 查询配置关键词名称
     *
     * @param name
     * @return
     */
    public ServerResponse queryMatchWord(String name,String cityId) {
        Example example = new Example(DjBasicsMaintain.class);
        if (!CommonUtil.isEmpty(name)) {
            example.createCriteria().andLike(DjBasicsMaintain.SEARCH_ITEM, "%" + name + "%")
            .andEqualTo(DjBasicsMaintain.CITY_ID,cityId);
            List<DjBasicsMaintain> list = djBasicsMaintainMapper.selectByExample(example);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", list);
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }


    /**
     * djBasicsMaintain/queryMatchWord
     * 添加关键词
     *
     * @param keywordName
     * @param searchItem
     * @return
     */
    public ServerResponse addKeywords(String keywordName, String searchItem,String cityId) {
        try {
            Example example = new Example(DjBasicsMaintain.class);
            example.createCriteria().andEqualTo(DjBasicsMaintain.KEYWORD_NAME, keywordName)
                    .andEqualTo(DjBasicsMaintain.DATA_STATUS, 0).andEqualTo(DjBasicsMaintain.CITY_ID,cityId);
            List<DjBasicsMaintain> djBasicsMaintains = djBasicsMaintainMapper.selectByExample(example);
            if(CommonUtil.isEmpty(keywordName)){
                return ServerResponse.createByErrorMessage("关键词名称不能为空");
            }
            if(CommonUtil.isEmpty(searchItem)){
                return ServerResponse.createByErrorMessage("搜索词不能为空");
            }
            if (djBasicsMaintains.size() > 0)
                return ServerResponse.createByErrorMessage("该关键词名称已存在");
            List<String> strings = Arrays.asList(searchItem.split(","));
            //判断集合是否有重复元素
            long count = strings.stream().distinct().count();
            if (count < strings.size())
                return ServerResponse.createByErrorMessage("搜索词重复");
            if (djBasicsMaintainMapper.duplicateRemoval(null,cityId,searchItem).size() > 0) {
                return ServerResponse.createByErrorMessage("搜索词已存在");
            }
            DjBasicsMaintain djBasicsMaintain = new DjBasicsMaintain();
            djBasicsMaintain.setKeywordName(keywordName);
            djBasicsMaintain.setSearchItem(searchItem);
            djBasicsMaintain.setDataStatus(0);
            djBasicsMaintain.setCityId(cityId);
            if (djBasicsMaintainMapper.insert(djBasicsMaintain) > 0)
                return ServerResponse.createBySuccessMessage("添加成功");
            return ServerResponse.createByErrorMessage("添加失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }


    /**
     * 编辑关键词
     *
     * @param id
     * @param keywordName
     * @param searchItem
     * @return
     */
    public ServerResponse updateKeywords(String id, String keywordName, String searchItem,String cityId) {
        try {
            DjBasicsMaintain djBasicsMaintain = djBasicsMaintainMapper.selectByPrimaryKey(id);
            if (!djBasicsMaintain.getKeywordName().equals(keywordName)) {
                Example example = new Example(DjBasicsMaintain.class);
                example.createCriteria().andEqualTo(DjBasicsMaintain.KEYWORD_NAME, keywordName)
                        .andEqualTo(DjBasicsMaintain.DATA_STATUS, 0)
                        .andEqualTo(DjBasicsMaintain.CITY_ID,cityId);
                if (djBasicsMaintainMapper.selectByExample(example).size() > 0)
                    return ServerResponse.createByErrorMessage("该关键词名称已存在");
            }
            if(CommonUtil.isEmpty(keywordName)){
                return ServerResponse.createByErrorMessage("关键词名称不能为空");
            }
            if(CommonUtil.isEmpty(searchItem)){
                return ServerResponse.createByErrorMessage("搜索词不能为空");
            }
            String[] searchItems = searchItem.split(",");
            List<String> strings = Arrays.asList(searchItems);
            //判断集合是否有重复元素
            long count = strings.stream().distinct().count();
            if (count < strings.size()) {
                return ServerResponse.createByErrorMessage("搜索词重复");
            }
            if (djBasicsMaintainMapper.duplicateRemoval(id,cityId,searchItem).size() > 0) {
                return ServerResponse.createByErrorMessage("搜索词已存在");
            }
            djBasicsMaintain.setKeywordName(keywordName);
            djBasicsMaintain.setSearchItem(searchItem);
            djBasicsMaintain.setDataStatus(0);
            djBasicsMaintain.setCityId(cityId);
            djBasicsMaintainMapper.updateByPrimaryKeySelective(djBasicsMaintain);
            return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }


    /**
     * 关联标签
     *
     * @param id
     * @param labelIds
     * @return
     */
    public ServerResponse addRelatedTags(String id, String labelIds) {
        try {
            DjBasicsMaintain djBasicsMaintain = djBasicsMaintainMapper.selectByPrimaryKey(id);
            djBasicsMaintain.setLabelIds(labelIds);
            djBasicsMaintain.setModifyDate(new Date());
            djBasicsMaintainMapper.updateByPrimaryKeySelective(djBasicsMaintain);
            return ServerResponse.createBySuccessMessage("关联标签成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }


    /**
     * 删除关键词
     *
     * @param id
     * @return
     */
    public ServerResponse delKeywords(String id) {
        try {
            djBasicsMaintainMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除关键词成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除关键词失败");
        }
    }

    /**
     * 查询关键词
     *
     * @param id
     * @return
     */
    public ServerResponse queryKeywords(String id,String cityId) {
        if (!CommonUtil.isEmpty(id))
            return ServerResponse.createBySuccess("查询成功", djBasicsMaintainMapper.selectByPrimaryKey(id));
        Example example = new Example(DjBasicsMaintain.class);
        example.createCriteria().andEqualTo(DjBasicsMaintain.DATA_STATUS, 0).
                andEqualTo(DjBasicsMaintain.CITY_ID,cityId);
        example.orderBy(DjBasicsMaintain.CREATE_DATE).desc();
        List<DjBasicsMaintain> djBasicsMaintains = djBasicsMaintainMapper.selectByExample(example);
        if (djBasicsMaintains.size() <= 0)
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        return ServerResponse.createBySuccess("查询成功", djBasicsMaintains);
    }
}

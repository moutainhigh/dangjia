package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.recommend.*;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.recommend.LatticeContent;
import com.dangjia.acg.modle.recommend.LatticeStyle;
import com.dangjia.acg.support.recommend.util.Cattle;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 方格内容服务类
 * @author: luof
 * @date: 2020-3-13
 */
@Service
public class LatticeContentService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(LatticeContentService.class);

    @Autowired
    private ILatticeContentMapper latticeContentMapper;

    @Autowired
    private ILatticeCodingMapper latticeCodingMapper;

    @Autowired
    private ILatticeContentTypeMapper latticeContentTypeMapper;

    @Autowired
    private ILatticeStyleMapper latticeStyleMapper;

    @Autowired
    private StorefrontProductMapper storefrontProductMapper;

    @Autowired
    private StoreActivityMapper storeActivityMapper;

    /**
     * @Description: 查询方格内容列表
     * @author: luof
     * @date: 2020-3-13
     */
    public ServerResponse queryList(){

        List<LatticeContent> latticeContentList = latticeContentMapper.selectAll();

        if ( latticeContentList == null || latticeContentList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }

        for( LatticeContent content : latticeContentList ){
            List<Integer> valueList = Cattle.splitScope(content.getAreaScope());
            List<String> codingNameList = latticeCodingMapper.queryCodingNameList(valueList);
            content.setCodingNameList(codingNameList);
        }

        return ServerResponse.createBySuccess("查询成功", latticeContentList);
    }

    /**
     * @Description: 查询方格内容单个
     * @author: luof
     * @date: 2020-3-14
     */
    public ServerResponse querySingle(String id){
        LatticeContent latticeContent = latticeContentMapper.selectByPrimaryKey(id);
        if ( latticeContent == null ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        List<Integer> valueList = Cattle.splitScope(latticeContent.getAreaScope());
        List<String> codingNameList = latticeCodingMapper.queryCodingNameList(valueList);
        latticeContent.setCodingNameList(codingNameList);
        return ServerResponse.createBySuccess("查询成功", latticeContent);
    }

    /**
     * @Description: 保存方格内容
     * @author: luof
     * @date: 2020-3-14
     */
    public ServerResponse save(String contentListJsonStr){

        logger.debug("保存方格内容 参数:"+contentListJsonStr);

        // 检查参数的json格式
        List<LatticeContent> contentList = Cattle.checkContentListJsonStr(contentListJsonStr);
        if( contentList == null ){
            logger.error("参数JSON串 错误");
            return ServerResponse.createByErrorMessage("参数JSON串 错误");
        }

        List<String> codingNameListAll = new ArrayList<String>();

        // 检查格子内容
        for( LatticeContent content : contentList ){

            // 检查必填
            String cr = checkParam(content);
            if( cr != null ){
                return ServerResponse.createByErrorMessage("组合块参数错误:"+cr);
            }

            // 检查内容类型
            if( !checkContentType(content) ){
                return ServerResponse.createByErrorMessage("组合块["+content.getAreaName()+"]不能添加此内容类型");
            }

            // 检查范围值有效
            if( !checkScope(content) ){
                return ServerResponse.createByErrorMessage("组合块["+content.getAreaName()+"]格子不完整");
            }
            codingNameListAll.addAll(content.getCodingNameList());

            logger.debug("组合块["+content.getAreaName()+"] 参数通过检查");
        }

        // 判断 重复
        if( !Cattle.repeat(codingNameListAll) ){
            return ServerResponse.createByErrorMessage("组合块有重合");
        }
        // 判断 全部格子完整
        if( !Cattle.wholeLatticeAll(codingNameListAll) ){
            return ServerResponse.createByErrorMessage("全部格子不完整");
        }
        logger.debug("方格内容 通过检查");

        // 全部删除
        int total = latticeContentMapper.queryTotal();
        if( total > 0 ){
            if( latticeContentMapper.deleteAll() != total ){
                return ServerResponse.createByErrorMessage("原数据删除不完整");
            }
            logger.debug("原数据删除 成功");
        }

        // 批量新增
        int ar = latticeContentMapper.addBatch(contentList);
        if( ar != contentList.size() ){
            return ServerResponse.createByErrorMessage("新数据保存不完整");
        }
        logger.debug("新数据保存 成功");

        return ServerResponse.createBySuccess("新数据保存 成功");
    }

    /** 检查范围值有效 */
    public boolean checkScope(LatticeContent content){

        // 格子数量
        List<Integer> valueList = Cattle.splitScope(content.getAreaScope());
        LatticeStyle style = latticeStyleMapper.selectByPrimaryKey(content.getStyleId());
        if( style.getRowNumber() * style.getColNumber() != valueList.size() ){
            logger.debug("组合块[\"+content.getAreaName()+\"]格子数量错误");
            return false;
        }
        List<String> codingNameList = latticeCodingMapper.queryCodingNameList(valueList);
        if( !Cattle.whole(codingNameList, style.getRowNumber(), style.getColNumber()) ){
            logger.debug("组合块[\"+content.getAreaName()+\"]格子不完整");
            return false;
        }

        content.setCodingNameList(codingNameList);
        return true;
    }

    /** 检查内容类型 */
    private boolean checkContentType(LatticeContent content){

        Integer value = latticeContentTypeMapper.queryTypeValue(content.getTypeId());
        Integer values = latticeStyleMapper.queryTypeValues(content.getStyleId());

        return Cattle.twoContain(value, values);
    }

    /** 检查参数 */
    private String checkParam(LatticeContent content){

        String areaName = content.getAreaName();
        if( areaName == null || areaName.equals("") ){
            return "areaName 不能为空";
        }

        Integer areaScope = content.getAreaScope();
        if( areaScope == null || areaScope.intValue() < 1 ){
            return "方块组["+areaName+"] areaScope 不能为空";
        }

        String styleId = content.getStyleId();
        if( styleId == null || styleId.equals("") ){
            return "方块组["+areaName+"] styleId 不能为空";
        }
        if( !latticeStyleMapper.existsWithPrimaryKey(styleId) ){
            return "方块组["+areaName+"] styleId 超出范围";
        }
        String typeId = content.getTypeId();
        if( typeId == null || typeId.equals("") ){
            return "方块组["+areaName+"] typeId 不能为空";
        }
        if( !latticeContentTypeMapper.existsWithPrimaryKey(typeId) ){
            return "方块组["+areaName+"] typeId 超出范围";
        }

        if( typeId.equals("1") ){
            String image = content.getImage();
            if( image == null || image.equals("") ){
                return "方块组["+areaName+"] image 不能为空";
            }
        }else{
            String contentId = content.getContentId();
            if( contentId == null || contentId.equals("") ){
                return "方块组["+areaName+"] contentId 不能为空";
            }
        }

        return null;
    }

    /**
     * @Description: 查询可选内容列表
     * @author: luof
     * @date: 2020-3-14
     */
    public ServerResponse queryOptionalContentList(String typeId, String keyword, PageDTO pageDTO){

        try {

            if( typeId == null || typeId.equals("") ){
                return ServerResponse.createByErrorMessage("typeId 不能为空");
            }

            List<LatticeContent> latticeContentList = new ArrayList<LatticeContent>();

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if( typeId.equals("2") ){
                List<StorefrontProductDTO> list = storefrontProductMapper.queryProductGroundByKeyWord(keyword);
                if( list == null || list.size() < 1 ){
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
                }
                for (StorefrontProductDTO dto : list) {
                    LatticeContent content = new LatticeContent();
                    content.setContentId(dto.getId());
                    content.setContentName(dto.getProductName());
                    latticeContentList.add(content);
                }
            }

            else {
                if( typeId.equals("3") || typeId.equals("4") ){
                    List<DjStoreActivity> list = null;
                    if( typeId.equals("3") ){
                        list = storeActivityMapper.queryList(1, keyword);
                    }else{
                        list = storeActivityMapper.queryList(2, keyword);
                    }
                    if( list == null || list.size() < 1 ){
                        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
                    }
                    for (DjStoreActivity activity : list) {
                        LatticeContent content = new LatticeContent();
                        content.setContentId(activity.getId());
                        content.setContentName(activity.getActivityDescription());
                        latticeContentList.add(content);
                    }
                }else {
                    return ServerResponse.createByErrorMessage("typeId 错误");
                }

            }

            PageInfo pageResult = new PageInfo(latticeContentList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch(Exception e){
            logger.error("查询可选内容列表 错误");
            return ServerResponse.createByErrorMessage("查询可选内容列表 错误");
        }
    }
}

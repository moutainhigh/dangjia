package com.dangjia.acg.controller.recommend;

import com.dangjia.acg.api.LatticeOperationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.LatticeCoding;
import com.dangjia.acg.modle.recommend.LatticeContentType;
import com.dangjia.acg.modle.recommend.LatticeStyle;
import com.dangjia.acg.service.recommend.LatticeCodingService;
import com.dangjia.acg.service.recommend.LatticeContentService;
import com.dangjia.acg.service.recommend.LatticeContentTypeService;
import com.dangjia.acg.service.recommend.LatticeStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 方格操作接口类
 * @author: luof
 * @date: 2020-3-13
 */
@RestController
public class LatticeOperationController implements LatticeOperationAPI {

    @Autowired
    private LatticeStyleService latticeStyleService;

    @Autowired
    private LatticeContentTypeService latticeContentTypeService;

    @Autowired
    private LatticeContentService latticeContentService;

    @Autowired
    private LatticeCodingService latticeCodingService;

    /**
     * @Description: 查询方格类型列表
     * @author: luof
     * @date: 2020-3-13
     */
    @Override
    @ApiMethod
    public ServerResponse queryLatticeStyleList() {
        return latticeStyleService.queryList();
    }

    /**
     * @Description: 查询方格内容类型列表
     * @author: luof
     * @date: 2020-3-13
     */
    @Override
    @ApiMethod
    public ServerResponse queryLatticeContentTypeList() {
        return latticeContentTypeService.queryList();
    }

    /**
     * @Description: 查询方格内容列表
     * @author: luof
     * @date: 2020-3-13
     */
    @Override
    @ApiMethod
    public ServerResponse queryLatticeContentList() {
        return latticeContentService.queryList();
    }

    /**
     * @Description: 查询方格内容单个
     * @author: luof
     * @date: 2020-3-14
     */
    @Override
    @ApiMethod
    public ServerResponse queryLatticeContentSingle(String id){
        return latticeContentService.querySingle(id);
    }

    /**
     * @Description: 保存方格内容
     * @author: luof
     * @date: 2020-3-14
     */
    @Override
    @ApiMethod
    public ServerResponse saveLatticeContent(String contentListJsonStr){
        return latticeContentService.save(contentListJsonStr);
    }

    /**
     * @Description: 查询方格编号列表
     * @author: luof
     * @date: 2020-3-13
     */
    @Override
    @ApiMethod
    public ServerResponse queryLatticeCodingList() {
        return latticeCodingService.queryList();
    }

    /**
     * @Description: 查询可选内容列表
     * @author: luof
     * @date: 2020-3-14
     */
    @Override
    @ApiMethod
    public ServerResponse queryOptionalContentList(String typeId, String keyword, PageDTO pageDTO) {
        return latticeContentService.queryOptionalContentList(typeId, keyword, pageDTO);
    }

}

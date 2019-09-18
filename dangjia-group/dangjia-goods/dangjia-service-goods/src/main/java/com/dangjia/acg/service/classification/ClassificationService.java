package com.dangjia.acg.service.classification;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IGoodsCategoryMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 分类模块
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/19 4:37 PM
 */
@Service
public class ClassificationService {
    @Autowired
    private IGoodsCategoryMapper iGoodsCategoryMapper;
   // @Autowired
    //private IWorkerGoodsMapper iWorkerGoodsMapper;
    @Autowired
    private ConfigUtil configUtil;

    public ServerResponse getGoodsCategoryList() {
        Example example = new Example(GoodsCategory.class);
        example.createCriteria()
                .andEqualTo(GoodsCategory.PARENT_TOP, "1")
                .andEqualTo(GoodsCategory.DATA_STATUS, 0);
        example.orderBy(GoodsCategory.SORT).asc();
        List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.selectByExample(example);
        if (goodsCategoryList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (GoodsCategory goodsCategory : goodsCategoryList) {
            String imageUrl = goodsCategory.getImage();
            goodsCategory.setImage(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }

    public ServerResponse getProductList(PageDTO pageDTO, String categoryId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HomeProductDTO> homeProductDTOS = iGoodsCategoryMapper.getProductList(categoryId);
        if (homeProductDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(homeProductDTOS);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (HomeProductDTO homeProductDTO : homeProductDTOS) {
            String imageUrl = homeProductDTO.getImage();
            homeProductDTO.setImage(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        pageResult.setList(homeProductDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

   /* public ServerResponse getWorkerGoodsList(PageDTO pageDTO, String workerTypeId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(WorkerGoods.class);
        example.createCriteria()
                .andEqualTo(WorkerGoods.WORKER_TYPE_ID, workerTypeId)
                .andEqualTo(WorkerGoods.SHOW_GOODS, 1)
                .andEqualTo(WorkerGoods.DATA_STATUS, 0);
        example.orderBy(WorkerGoods.ISTOP).desc();
        example.orderBy(WorkerGoods.CREATE_DATE).desc();
        List<WorkerGoods> workerGoodsList = iWorkerGoodsMapper.selectByExample(example);
        if (workerGoodsList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(workerGoodsList);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (WorkerGoods workerGoods : workerGoodsList) {
            String imageUrl = workerGoods.getImage();
            workerGoods.setImage(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        pageResult.setList(workerGoodsList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }*/
}

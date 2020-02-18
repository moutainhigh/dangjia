package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.ProductAppDTO;
import com.dangjia.acg.dto.product.ProductWorkerDTO;
import com.dangjia.acg.dto.repair.BudgetWorkerDTO;
import com.dangjia.acg.mapper.actuary.IBudgetWorkerMapper;
import com.dangjia.acg.mapper.basics.IProductWorkerMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.product.DjBasicsProductTemplateService;
import com.dangjia.acg.service.product.app.GoodsProductTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:12
 */
@Service
public class FillWorkerService {

    private static Logger logger = LoggerFactory.getLogger(FillWorkerService.class);
    @Autowired
    private IBudgetWorkerMapper budgetWorkerMapper;
    @Autowired
    private IProductWorkerMapper workerGoodsMapper;

    @Autowired
    private DjBasicsProductTemplateService djBasicsProductService;
    @Autowired
    private GoodsProductTemplateService goodsProductTemplateService;
    @Autowired
    private ConfigUtil configUtil;
    protected static final Logger LOG = LoggerFactory.getLogger(FillWorkerService.class);

    /**
     * @param type 0 精算内 1 商品库
     *             <p>
     *             补人工,退人工共用此接口(精算内)
     */
    public ServerResponse repairBudgetWorker(int type, String workerTypeId, String houseId, PageDTO pageDTO, String cityId, String orderSource) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        if (StringUtil.isEmpty(workerTypeId)) {
            return ServerResponse.createByErrorMessage("workerTypeId不能为空");
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<BudgetWorkerDTO> budgetWorkerDTOList = new ArrayList<>();
        PageInfo pageResult;
        try {
            if (type == 0 && !"1".equals(workerTypeId) && !"2".equals(workerTypeId)) {//精算内
                Example example = new Example(BudgetMaterial.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(BudgetMaterial.WORKER_TYPE_ID, workerTypeId);
                criteria.andEqualTo(BudgetMaterial.HOUSE_ID, houseId);
                criteria.andEqualTo(BudgetMaterial.PRODUCT_TYPE, "2");
                criteria.andNotEqualTo(BudgetMaterial.DELETE_STATE, "1");
                criteria.andEqualTo(BudgetMaterial.CITY_ID, cityId);
                List<BudgetMaterial> budgetWorkerList = budgetWorkerMapper.selectByExample(example);
                pageResult = new PageInfo(budgetWorkerList);
                for (BudgetMaterial budgetWorker : budgetWorkerList) {
                    BudgetWorkerDTO budgetWorkerDTO = new BudgetWorkerDTO();
                    DjBasicsProductTemplate workerGoods = djBasicsProductService.queryDataByProductId(budgetWorker.getProductId());  //通过商品id去关联规格
                    String valueIdArr = workerGoods.getValueIdArr();
                    if (!CommonUtil.isEmpty(valueIdArr)) {
                        String valueNameArr = djBasicsProductService.getNewValueNameArr(valueIdArr);
                        budgetWorkerDTO.setValueNameArr(valueNameArr);
                    }
                    budgetWorkerDTO.setWorkerGoodsId(budgetWorker.getProductId());
                    budgetWorkerDTO.setWorkerTypeId(budgetWorker.getWorkerTypeId());
                    budgetWorkerDTO.setWorkerGoodsSn(budgetWorker.getProductSn());
                    budgetWorkerDTO.setName(budgetWorker.getProductName());
                    budgetWorkerDTO.setPrice(budgetWorker.getPrice());
                    budgetWorkerDTO.setShopCount(budgetWorker.getShopCount() - budgetWorker.getBackCount() + budgetWorker.getRepairCount());
                    budgetWorkerDTO.setTotalPrice(budgetWorker.getPrice() * budgetWorkerDTO.getShopCount());
                    budgetWorkerDTO.setUnitName(budgetWorker.getUnitName());
                    budgetWorkerDTO.setImage(address + workerGoods.getImage());
                    budgetWorkerDTOList.add(budgetWorkerDTO);
                }
            } else {

                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<ProductWorkerDTO> workerGoodsList = workerGoodsMapper.getProductWorker(workerTypeId, houseId, orderSource);
                pageResult = new PageInfo(workerGoodsList);
                for (ProductWorkerDTO workerGoods : workerGoodsList) {
                    BudgetWorkerDTO budgetWorkerDTO = new BudgetWorkerDTO();
                    DjBasicsProductTemplate djBasicsProduct = djBasicsProductService.queryDataByProductId(workerGoods.getId());  //通过商品id去关联规格
                    if (djBasicsProduct != null) {
                        String valueIdArr = djBasicsProduct.getValueIdArr();
                        if (!CommonUtil.isEmpty(valueIdArr)) {
                            String valueNameArr = djBasicsProductService.getNewValueNameArr(valueIdArr);
                            budgetWorkerDTO.setValueNameArr(valueNameArr);
                        }
                    }
                    budgetWorkerDTO.setWorkerGoodsId(workerGoods.getId());
                    budgetWorkerDTO.setWorkerTypeId(workerGoods.getWorkerTypeId());
                    budgetWorkerDTO.setWorkerGoodsSn(workerGoods.getProductSn());
                    budgetWorkerDTO.setName(workerGoods.getName());
                    budgetWorkerDTO.setPrice(workerGoods.getPrice());
                    budgetWorkerDTO.setTotalPrice(workerGoods.getPrice());
                    budgetWorkerDTO.setUnitName(workerGoods.getUnitName());//单位
                    budgetWorkerDTO.setImage(address + workerGoods.getImage());
                    budgetWorkerDTOList.add(budgetWorkerDTO);
                }
            }
            pageResult.setList(budgetWorkerDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     *  查询符合条件的人工商品大类
     *
     * @param workerId
     * @param cityId
     * @return
     */
   /* public ServerResponse getWorkerProductCategoryList( String workerId, String cityId){
        logger.info("查询符合条件的人工商品大类,cityId={},workerId={}",cityId,workerId);
         workerGoodsMapper.getWorkerProductCategoryList(workerId,cityId);
        return null;
    }*/


    /**
     * 查询符合条件的人工商品
     *
     * @param workerId
     * @param searchKey
     * @param pageDTO
     * @param cityId
     * @return
     */
    public ServerResponse getWorkerProductList(String workerId, String houseId, String searchKey, PageDTO pageDTO, String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ProductAppDTO> workerProductList = workerGoodsMapper.getWorkerProductList(workerId, houseId, searchKey, cityId);
            if (workerProductList == null || workerProductList.size() <= 0) {
                return ServerResponse.createByErrorMessage("查无数据！");
            }
            goodsProductTemplateService.getProductList(workerProductList);
            PageInfo pageResult = new PageInfo(workerProductList);
            pageResult.setList(workerProductList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}

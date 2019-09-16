package com.dangjia.acg.service.product;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.mapper.product.DjBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 9:54
 */
@Service
public class DjBasicsGoodsService {
    private static Logger LOG = LoggerFactory.getLogger(DjBasicsGoodsService.class);
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;

    /**
     * 货品打标签
     * @param goodsId
     * @param labels
     * @return
     */
    public ServerResponse addLabels(String goodsId, String labels) {
        DjBasicsGoods djBasicsGoods=new DjBasicsGoods();
        djBasicsGoods.setId(goodsId);
        djBasicsGoods.setLabelIds(labels);
        djBasicsGoodsMapper.updateByPrimaryKeySelective(djBasicsGoods);
        Example example=new Example(DjBasicsProduct.class);
        example.createCriteria().andEqualTo(DjBasicsProduct.GOODS_ID,goodsId);
        DjBasicsProduct djBasicsProduct=new DjBasicsProduct();
        djBasicsProduct.setLabelId(labels);
        djBasicsProductMapper.updateByExampleSelective(djBasicsProduct,example);
        return ServerResponse.createBySuccessMessage("货品打标签成功");
    }
    /**
     * 保存货品信息
     * <p>Title: saveBasicsGoods</p>
     * <p>Description: </p>
     *
     * @return
     */
    public ServerResponse saveBasicsGoods(BasicsGoodsDTO basicsGoodsDTO) {
        try {
            String name = basicsGoodsDTO.getName();
            String unitId = basicsGoodsDTO.getUnitId();
            String categoryId = basicsGoodsDTO.getCategoryId();
            int type = basicsGoodsDTO.getType();
            if (!StringUtils.isNotBlank(name))
                return ServerResponse.createByErrorMessage("名字不能为空");

            List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByName(name);
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("名字不能重复");

            if (!StringUtils.isNotBlank(unitId))
                return ServerResponse.createByErrorMessage("单位id不能为空");

            if (!StringUtils.isNotBlank(categoryId))
                return ServerResponse.createByErrorMessage("分类不能为空");

            if (type < -1)
                return ServerResponse.createByErrorMessage("性质不能为空");

            BasicsGoods goods = getBasicsGoods(basicsGoodsDTO);
            iBasicsGoodsMapper.insert(goods);
            return ServerResponse.createBySuccess("新增成功", goods.getId());
        } catch (Exception e) {
            LOG.error("新增货品失败：",e);
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }
    public ServerResponse updateBasicsGoods(BasicsGoodsDTO basicsGoodsDTO){
        try {
            String id = basicsGoodsDTO.getId();
            String name = basicsGoodsDTO.getName();
            BasicsGoods oldBasicsGoods = iBasicsGoodsMapper.selectByPrimaryKey(id);
            if (!oldBasicsGoods.getName().equals(name)) {
                List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByName(name);
                if (goodsList.size() > 0)
                    return ServerResponse.createByErrorMessage("该货品已存在");
            }
            BasicsGoods goods = getBasicsGoods(basicsGoodsDTO);
            goods.setId(basicsGoodsDTO.getId());
            iBasicsGoodsMapper.updateByPrimaryKeySelective(goods);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            LOG.error("修改货品失败：",e);
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }
    /**
     * 对象转换
     *
     * @return
     */
    private BasicsGoods getBasicsGoods(BasicsGoodsDTO basicsGoodsDTO) {
        BasicsGoods goods = new BasicsGoods();
        goods.setName(basicsGoodsDTO.getName());
        goods.setOtherName(basicsGoodsDTO.getOtherName());//别名
        goods.setCategoryId(basicsGoodsDTO.getCategoryId());//分类
        goods.setBuy(basicsGoodsDTO.getBuy());//购买性质
        goods.setSales(basicsGoodsDTO.getSales());//退货性质
        goods.setUnitId(basicsGoodsDTO.getUnitId());//单位
        goods.setType(basicsGoodsDTO.getType());//goods性质
        goods.setCreateDate(new Date());
        goods.setModifyDate(new Date());
        goods.setIsInflueDecorationProgress(basicsGoodsDTO.getIsInflueDecorationProgress());
        goods.setIrreversibleReasons(basicsGoodsDTO.getIrreversibleReasons());
        goods.setIstop(basicsGoodsDTO.getIstop());
        goods.setBrandId(basicsGoodsDTO.getBrandId());
        goods.setIsElevatorFee(basicsGoodsDTO.getIsElevatorFee());
        goods.setIndicativePrice(basicsGoodsDTO.getIndicativePrice());
        goods.setLabelIds(basicsGoodsDTO.getLabelIds());
        return goods;
    }

}

package com.dangjia.acg.service.product;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 产品逻辑处理层
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class DjBasicsProductService {

    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;

    /**
     * 查询商品信息
     * @param name
     * @return
     */
    public ServerResponse queryProductData(String name){
        Example example = new Example(DjBasicsProduct.class);
        if(!CommonUtil.isEmpty(name)){
            example.createCriteria().andLike(DjBasicsProduct.NAME, "%" + name + "%");
            List<DjBasicsProduct> list = djBasicsProductMapper.selectByExample(example);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", list);
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }
}

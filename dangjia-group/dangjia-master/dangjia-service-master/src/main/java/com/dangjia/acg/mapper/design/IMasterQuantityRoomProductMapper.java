package com.dangjia.acg.mapper.design;

import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.modle.design.DesignQuantityRoomProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author fzh
 * @descrilbe 推荐商品
 */
@Repository
public interface IMasterQuantityRoomProductMapper extends Mapper<DesignQuantityRoomProduct> {

    List<ActuarialProductAppDTO> getRoomProductList(@Param("houseId") String houseId, @Param("type") int type);

}

package com.dangjia.acg.mapper.other;

import com.dangjia.acg.modle.other.BankCard;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**银行卡绑定
 * zmj
 */
@Repository
public interface IBankCardMapper extends Mapper<BankCard> {
}


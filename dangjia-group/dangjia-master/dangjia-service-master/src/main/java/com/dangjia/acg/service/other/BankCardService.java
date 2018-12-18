package com.dangjia.acg.service.other;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.worker.IWorkerBankCardMapper;
import com.dangjia.acg.modle.other.BankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2018/12/04
 * Time: 16:16
 */
@Service
public class BankCardService {

    @Autowired
    private IBankCardMapper bankCardMapper;
    @Autowired
    private IWorkerBankCardMapper workerBankCardMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 获取所有银行卡类型
     * @param bankCard
     * @return
     */
    public List<BankCard>  getBankCards(HttpServletRequest request, BankCard bankCard) {
        Example example = new Example(BankCard.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(bankCard.getBankName())) {
            criteria.andLike(BankCard.BANK_NAME, "%"+bankCard.getBankName()+"%");
        }
        criteria.andEqualTo(BankCard.DATA_STATUS,0);
        List<BankCard> list = bankCardMapper.selectByExample(example);
        for (BankCard v:list){
            v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return list;
    }
    /**
     * 删除
     * @param id
     * @return
     */
    public ServerResponse delBankCard(HttpServletRequest request, String id) {
        BankCard bankCard=new BankCard();
        bankCard.setId(id);
        bankCard.setDataStatus(1);
        if(this.bankCardMapper.updateByPrimaryKeySelective(bankCard)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     * @param bankCard
     * @return
     */
    public ServerResponse editBankCard(HttpServletRequest request, BankCard bankCard) {
        if(this.bankCardMapper.updateByPrimaryKeySelective(bankCard)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param bankCard
     * @return
     */
    public ServerResponse addBankCard(HttpServletRequest request,BankCard bankCard) {
        if(this.bankCardMapper.insertSelective(bankCard)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}

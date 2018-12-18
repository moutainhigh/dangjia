package com.dangjia.acg.controller.app.other;

import com.dangjia.acg.api.data.BankCardAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.service.other.BankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 16:29
 */
@RestController
public class BankCardController implements BankCardAPI {

    @Autowired
    private BankCardService bankCardService;

    /**
     * 获取所有银行卡类型
     * @param bankCard
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBankCards(HttpServletRequest request, BankCard bankCard) {
        return ServerResponse.createBySuccess("ok",bankCardService.getBankCards(request,bankCard));
    }
    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delBankCard(HttpServletRequest request, String id) {
        return bankCardService.delBankCard(request,id);
    }

    /**
     * 修改
     * @param bankCard
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editBankCard(HttpServletRequest request, BankCard bankCard) {
        return bankCardService.editBankCard(request,bankCard);
    }
    /**
     * 新增
     * @param bankCard
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addBankCard(HttpServletRequest request,BankCard bankCard) {
        return bankCardService.editBankCard(request,bankCard);
    }
}

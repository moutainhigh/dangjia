package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.other.BankCard;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:01
 */
@FeignClient("dangjia-service-master")
@Api(value = "银行卡配置接口", description = "银行卡配置接口")
public interface BankCardAPI {

    @PostMapping("/data/bank/card/list")
    @ApiOperation(value = "获取所有银行卡类型", notes = "获取所有银行卡类型")
    ServerResponse getBankCards(HttpServletRequest request, BankCard bankCard);
    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/data/bank/card/del")
    @ApiOperation(value = "删除银行卡类型", notes = "删除银行卡类型")
    ServerResponse delBankCard(HttpServletRequest request, String id);

    /**
     * 修改
     * @param bankCard
     * @return
     */
    @PostMapping("/data/bank/card/edit")
    @ApiOperation(value = "修改银行卡类型", notes = "修改银行卡类型")
    ServerResponse editBankCard(HttpServletRequest request, BankCard bankCard);
    /**
     * 新增
     * @param bankCard
     * @return
     */
    @PostMapping("/data/bank/card/add")
    @ApiOperation(value = "新增银行卡类型", notes = "新增银行卡类型")
    ServerResponse addBankCard(HttpServletRequest request,BankCard bankCard);
}

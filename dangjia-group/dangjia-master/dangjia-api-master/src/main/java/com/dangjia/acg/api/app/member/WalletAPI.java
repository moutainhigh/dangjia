package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 16:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "个人钱包", description = "个人钱包")
public interface WalletAPI {

    /**
     * 完成验证提现
     */
    @PostMapping("app/member/wallet/checkFinish")
    @ApiOperation(value = "完成验证提现", notes = "完成验证提现")
    ServerResponse checkFinish(@RequestParam("userToken") String userToken,
                               @RequestParam("paycode") String paycode,
                               @RequestParam("money") Double money,
                               @RequestParam("workerBankCardId") String workerBankCardId,
                               @RequestParam("roleType") Integer roleType);

    /**
     * 提现验证码
     */
    @PostMapping("app/member/wallet/getPaycode")
    @ApiOperation(value = "提现验证码", notes = "提现验证码")
    ServerResponse getPaycode(@RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param money     必选 string 提现金额
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/提现模块
     * @title 验证金额
     * @description 验证金额
     * @method POST
     * @url master/app/member/wallet/verificationAmount
     * @return_param type int 0:通过，1：弹框提示
     * @return_param depositMoney double 实际提现金额
     * @return_param rateMoney double 手续费（为0不显示）
     * @return_param ruleMessage string 费率描述（为空不显示）
     * @return_param message string 到账时间描述
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/12/17 7:51 PM
     */
    @PostMapping("app/member/wallet/verificationAmount")
    @ApiOperation(value = "验证金额", notes = "验证金额")
    ServerResponse verificationAmount(@RequestParam("userToken") String userToken,
                                      @RequestParam("money") Double money);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/提现模块
     * @title 获取提现信息
     * @description 获取提现信息
     * @method POST
     * @url master/app/member/wallet/getWithdraw
     * @return_param mobile string 电话
     * @return_param surplusMoney double 可取余额
     * @return_param message string 头部提示信息(返回值则显示）
     * @return_param messageUrl string 头部提示信息跳转地址
     * @return_param brandCardDTOList List<BrandCardDTO> 用户银行卡
     * @return_param -- -- --
     * @return_param brandCardDTOList——brandName string 银行名
     * @return_param brandCardDTOList——workerBankCardId string 银行关联工人
     * @return_param brandCardDTOList——bkMaxAmt string 最大取现金额
     * @return_param brandCardDTOList——bkMinAmt string 最小取现金额
     * @return_param brandCardDTOList——bankCardImage string 图片路径
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/12/17 4:30 PM
     */
    @PostMapping("app/member/wallet/getWithdraw")
    @ApiOperation(value = "获取提现信息", notes = "获取提现信息")
    ServerResponse getWithdraw(@RequestParam("userToken") String userToken);

    /**
     * showdoc
     *
     * @param userToken      必选 string userToken
     * @param workerDetailId 必选 string 流水ID
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/提现模块
     * @title 流水详情
     * @description 流水详情
     * @method POST
     * @url master/app/member/wallet/getExtractDetail
     * @return_param id string 订单号码
     * @return_param typeName string 流水详情描述
     * @return_param image string 图标
     * @return_param name string 付款详情
     * @return_param money string 流水金额
     * @return_param createDate string 流水时间yyyy-MM-dd HH:mm
     * @return_param rateMoney double 手续费为空或为0不显示
     * @return_param depositMoney double 实际提现的钱为空或为0不显示
     * @return_param depositState int -1不显示，0未处理,1同意,2不同意(驳回)
     * @return_param memo string 提现备注
     * @return_param depositImage string 提现回执单图片
     * @return_param reason string 提现不同意理由
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/12/17 7:04 PM
     */
    @PostMapping("app/member/wallet/getExtractDetail")
    @ApiOperation(value = "流水详情", notes = "流水详情")
    ServerResponse getExtractDetail(@RequestParam("userToken") String userToken,
                                    @RequestParam("workerDetailId") String workerDetailId);

    /**
     * 支出 收入
     */
    @PostMapping("app/member/wallet/workerDetail")
    @ApiOperation(value = "支出 收入", notes = "支出 收入")
    ServerResponse workerDetail(@RequestParam("userToken") String userToken,
                                @RequestParam("type") int type,
                                @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 钱包信息, 查询余额
     */
    @PostMapping("app/member/wallet/walletInformation")
    @ApiOperation(value = "钱包信息, 查询余额", notes = "钱包信息, 查询余额")
    ServerResponse walletInformation(@RequestParam("userToken") String userToken);
}

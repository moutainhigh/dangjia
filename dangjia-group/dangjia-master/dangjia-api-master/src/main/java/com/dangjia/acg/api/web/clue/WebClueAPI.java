package com.dangjia.acg.api.web.clue;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.member.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("dangjia-service-master")
@Api(value = "线索功能", description = "线索功能")
public interface WebClueAPI {
    /**
     * 获取所有线索
     */
    @PostMapping("web/clue/clueMessage/getAllClue")
    @ApiOperation(value = "获取所有线索", notes = "获取所有线索")
    ServerResponse getAll(@RequestParam("pageDTO") PageDTO pageDTO);


    /**
     * 添加线索
     */
    @PostMapping("web/clue/addClue")
    @ApiOperation(value = "添加线索", notes = "添加线索")
    int addClue(@RequestParam("clue") Clue clue);

    /**
     * 查询线索
     */
    @PostMapping("web/clue/getClueList")
    @ApiOperation(value = "查询线索", notes = "查询线索")
    ServerResponse getClueList(@RequestParam("stage") Integer stage,
                               @RequestParam("values") String values,
                               @RequestParam("memberId") String memberId,
                               @RequestParam("childId") String childId,
                               @RequestParam("beginDate") String beginDate,
                               @RequestParam("endDate") String endDate,
                               @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 通过状态查找线索
     */
    @PostMapping("web/clue/clueMessage/viewByStage")
    @ApiOperation(value = "通过状态查找线索", notes = "通过状态查找线索")
    ServerResponse getByStage(@RequestParam("stage") int stage,
                              @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 修改客服
     */
    @PostMapping("web/clue/updateCus")
    @ApiOperation(value = "根据id修改信息", notes = "根据id修改信息")
    ServerResponse updateCus(@RequestParam("cusService") String cusService,
                             @RequestParam("phone") String phone,
                             @RequestParam("chat") String chat,
                             @RequestParam("userId") String userId,
                             @RequestParam("childId") String childId,
                             @RequestParam("id") String id);

    /**
     * xls导入线索
     *
     * @param userId
     * @param file
     * @return
     */
    @PostMapping("web/clue/importExce")
    @ApiOperation(value = "xls导入线索", notes = "xls导入线索")
    ServerResponse importExcelClue(@RequestParam("userId") String userId, @RequestParam("file") MultipartFile file);

    /**
     * 放弃跟进/加入黑名单
     */
    @PostMapping("web/clue/giveUp")
    @ApiOperation(value = "放弃跟进/加入黑名单", notes = "放弃跟进/加入黑名单")
    ServerResponse giveUp(@RequestParam("id") String id, @RequestParam("type") int type);

    /**
     * 转客户
     */
    @PostMapping("web/clue/sendUser")
    @ApiOperation(value = "转客户", notes = "转客户")
    ServerResponse sendUser(@RequestParam("member") Member member,
                            @RequestParam("phone") String phone,
                            @RequestParam("longitude") String longitude,
                            @RequestParam("latitude") String latitude);


    /**
     * showdoc
     *
     * @param userId 必选 string 销售员ID
     * @param phone  必选 string 客户手机号
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/线索模块
     * @title 分享页添加线索
     * @description 分享页添加线索
     * @method POST
     * @url master/clue/addH5Clue
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/7/27 11:50 AM
     */
    @PostMapping("clue/addH5Clue")
    @ApiOperation(value = "分享页添加线索", notes = "分享页添加线索")
    ServerResponse addH5Clue(@RequestParam("userId") String userId,
                             @RequestParam("phone") String phone);
}

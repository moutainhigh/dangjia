package com.dangjia.acg.controller.web.clue;

import com.dangjia.acg.api.web.clue.WebClueAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;

import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.clue.ClueService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class WebClueController implements WebClueAPI {

    @Autowired
   private ClueService clueService;

    /**
     * 得到所有
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAll(PageDTO pageDTO) {

        return clueService.getAll(pageDTO.getPageNum(),pageDTO.getPageSize());
    }

    /**
     * 查询线索
     * @param values
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getClueList (Integer stage,String values, PageDTO pageDTO){
        return clueService.getClueList(stage,values,pageDTO.getPageNum(),pageDTO.getPageSize());
    }

    @Override
    @ApiMethod
    public ServerResponse getByStage(int stage,PageDTO pageDTO) {
        return clueService.getByStage(stage,pageDTO.getPageNum(),pageDTO.getPageSize());
    }

    @Override
    @ApiMethod
    public ServerResponse updateCus(String cusService, String id) {
        return clueService.updateCus(cusService,id);
    }

    @Override
    @ApiMethod
    public int addClue( Clue clue) {
        return clueService.addClue(clue);
    }

    @Override
    @ApiMethod
    public ServerResponse importExcelClue(String userId, MultipartFile file){
        return clueService.importExcelClue(userId,file);
    }

    @Override
    @ApiMethod
    public ServerResponse giveUp(String id,int type) {
        return clueService.giveUp(id,type);
    }

    @Override
    @ApiMethod
     public ServerResponse sendUser(Member member,String phone){
            return clueService.sendUser(member,phone);
    }

}

package com.dangjia.acg.controller.web.clue;

import com.dangjia.acg.api.web.clue.WebClueAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
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
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAll(PageDTO pageDTO) {
        return clueService.getAll(pageDTO);
    }

    /**
     * 查询线索
     *
     * @param values
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getClueList(Integer stage, String values, String memberId, String childId, String beginDate, String endDate, PageDTO pageDTO) {
        return clueService.getClueList(stage, values, memberId, childId, beginDate, endDate, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse getByStage(int stage, PageDTO pageDTO) {
        return clueService.getByStage(stage, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCus(String cusService, String phone, String chat, String userId, String childId, String id) {
        return clueService.updateCus(cusService, phone, chat, userId, childId, id);
    }

    @Override
    @ApiMethod
    public int addClue(Clue clue) {
        return clueService.addClue(clue);
    }

    @Override
    @ApiMethod
    public ServerResponse importExcelClue(String userId, MultipartFile file) {
        return clueService.importExcelClue(userId, file);
    }

    @Override
    @ApiMethod
    public ServerResponse giveUp(String id, int type) {
        return clueService.giveUp(id, type);
    }

    @Override
    @ApiMethod
    public ServerResponse sendUser(String id, String phone, String longitude, String latitude) {
        return clueService.sendUser(id, phone, longitude, latitude);
    }

    @Override
    @ApiMethod
    public ServerResponse addH5Clue(String userId, String phone) {
        return clueService.addH5Clue(userId, phone);
    }
}

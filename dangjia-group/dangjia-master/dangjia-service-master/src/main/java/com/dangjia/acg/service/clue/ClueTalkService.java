package com.dangjia.acg.service.clue;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.clue.ClueTalk;
import com.dangjia.acg.modle.user.MainUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClueTalkService {

    @Autowired
    private ClueTalkMapper clueTalkMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClueMapper clueMapper;

    /**
     * 通过线索ID获取沟通记录
     */
    public ServerResponse getTalkByClueId(String clueId, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ClueTalk> clueTalks = clueTalkMapper.getTalkByClueId(clueId);
            if (clueTalks == null || clueTalks.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(clueTalks);
            for (ClueTalk c : clueTalks) {
                MainUser mainUser = userMapper.getNameById(c.getUserId());
                String name = mainUser.getUsername();
                c.setUserId(name);
            }
            pageResult.setList(clueTalks);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 新增沟通记录
     */
    public ServerResponse addTalk(String clueId, String talkContent, String userId) {
        try {
            ClueTalk clueTalk = new ClueTalk();
            clueTalk.setClueId(clueId);
            clueTalk.setTalkContent(talkContent);
            clueTalk.setUserId(userId);
            clueTalkMapper.insert(clueTalk);
            Clue clue = clueMapper.selectByPrimaryKey(clueId);
            if (clue.getStage() == 0) {
                clue.setStage(1);
            }
            clue.setModifyDate(clueTalk.getModifyDate());
            clueMapper.updateByPrimaryKeySelective(clue);
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }
}


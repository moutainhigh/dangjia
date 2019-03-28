package com.dangjia.acg.service.clue;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;

import com.dangjia.acg.modle.clue.ClueTalk;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClueTalkService {

    @Autowired
    private ClueTalkMapper clueTalkMapper;
    /**
     * 通过线索ID获取沟通记录
     */
    public ServerResponse getTalkByClueId(String clueId,Integer pageNum, Integer pageSize){

        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<ClueTalk> clueTalks=clueTalkMapper.getTalkByClueId(clueId);
            PageInfo pageResult = new PageInfo(clueTalks);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 新增沟通记录
     */
    public ServerResponse addTalk(String clueId,String talkContent){
        try {
            ClueTalk clueTalk=new ClueTalk();
            clueTalk.setClueId(clueId);
            clueTalk.setTalkContent(talkContent);
            clueTalkMapper.insert(clueTalk);
            return ServerResponse.createByErrorMessage("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }
}

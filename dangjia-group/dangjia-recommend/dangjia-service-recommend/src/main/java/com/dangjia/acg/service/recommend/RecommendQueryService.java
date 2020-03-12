package com.dangjia.acg.service.recommend;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.support.recommend.dto.RecommendComposeChunk;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import com.dangjia.acg.support.recommend.service.BrowseCaseRecommend;
import com.dangjia.acg.support.recommend.service.BrowseGoodsRecommend;
import com.dangjia.acg.support.recommend.service.LabelAttribRecommend;
import com.dangjia.acg.support.recommend.util.Cattle;
import com.dangjia.acg.support.recommend.util.RecommendConfigItem;
import com.dangjia.acg.support.recommend.util.RecommendSource;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 推荐查询服务类
 * @author: luof
 * @date: 2020-3-10
 */
@Service
public class RecommendQueryService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(RecommendQueryService.class);

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private IMemberMapper memberMapper;

    @Autowired
    private RecommendTargetService recommendTargetService;

    @Autowired
    private RecommendConfigService recommendConfigService;

    @Autowired
    private BrowseGoodsRecommend browseGoodsRecommend;

    @Autowired
    private BrowseCaseRecommend browseCaseRecommend;

    @Autowired
    private LabelAttribRecommend labelAttribRecommend;

    /**
     * @Description: 查询推荐
     * @author: luof
     * @date: 2020-3-10
     */
    public ServerResponse queryRecommend(String userToken, PageDTO pageDTO) {

        RecommendComposeChunk recommendComposeChunk = null;

        try{

            // 检验 userToken
            ServerResponse serverResponse = checkUserToken(userToken);
            if( !serverResponse.isSuccess() ){
                return serverResponse;
            }
            Member member = (Member)serverResponse.getResultObj();
            String memberId = member.getId();
            logger.debug("当次推荐接收人:name["+member.getName()+"],memberId["+memberId+"]");

            // 组装 推荐块
            recommendComposeChunk = assembleRecommendChunk();

            // 加载推荐 根据浏览商品
            loadRecommendTargetBySource(recommendComposeChunk, pageDTO, memberId, RecommendSource.browse_goods.getCode());
            // 加载推荐 根据浏览案例
            loadRecommendTargetBySource(recommendComposeChunk, pageDTO, memberId, RecommendSource.browse_case.getCode());
            // 过滤重复 第一次
            Cattle.filterRepeatOne(recommendComposeChunk);
            // 加载推荐 根据标签属性
            loadRecommendTargetBySource(recommendComposeChunk, pageDTO, memberId, RecommendSource.label_attrib.getCode());
            // 过滤重复 第二次
            Cattle.filterRepeatTwo(recommendComposeChunk);
            // 补齐数据
            replenish(recommendComposeChunk, pageDTO);

            logger.debug("当次推荐信息:recommendList["+ JSON.toJSONString(recommendComposeChunk.getTotalRecommendList())+"]");
            if( recommendComposeChunk.getTotalRecommendList().size() < 1 ){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
            }

            PageInfo pageResult = new PageInfo(recommendComposeChunk.getTotalRecommendList());
            return  ServerResponse.createBySuccess(pageResult);
        }catch(Exception e){
            logger.error("查询推荐 异常:", e);
            // 异常情况 查询推荐"默认项itemSubId:1"
            if( recommendComposeChunk != null ) pageDTO.setPageSize(recommendComposeChunk.getRecommendTotal());
            return recommendTargetService.queryPage(pageDTO, new ArrayList<String>(Arrays.asList("1")), null);
        }

    }

    // 补齐数据
    private void replenish(RecommendComposeChunk recommendComposeChunk, PageDTO pageDTO){

        int total = recommendComposeChunk.getRecommendTotal();
        List<RecommendTargetInfo> totalRecommendList = recommendComposeChunk.getTotalRecommendList();

        // 不需要补齐
        if( total == totalRecommendList.size() ){
            return;
        }

        if( total > totalRecommendList.size() ){
            // 差额数量
            int lack = total - totalRecommendList.size();
            logger.debug("当次推荐补齐条数:"+lack);
            pageDTO.setPageSize(lack);
            ServerResponse<PageInfo> serverResponse = recommendTargetService.queryPage(pageDTO, new ArrayList<String>(Arrays.asList("1")), null);
            if( serverResponse.isSuccess() ){
                List<RecommendTargetInfo> lackList = serverResponse.getResultObj().getList();
                if( lack == total ){
                    recommendComposeChunk.setTotalRecommendList(lackList);
                }else{
                    // 过滤重复
                    List<RecommendTargetInfo> lackListCopy = Cattle.filterRepeat(totalRecommendList, lackList);
                    recommendComposeChunk.getTotalRecommendList().addAll(lackListCopy);
                }
            }
        }
    }

    // 加载推荐
    private void loadRecommendTargetBySource(RecommendComposeChunk recommendComposeChunk,
                                             PageDTO pageDTO,
                                             String memberId,
                                             int source){

        // 条数
        int number = Cattle.getNumberBySource(recommendComposeChunk, source);
        pageDTO.setPageSize(number);

        // 加载推荐信息
        ServerResponse serverResponse = null;
        if( RecommendSource.browse_goods.getCode() == source ){
            serverResponse = browseGoodsRecommend.loadRecommendTarget(memberId, pageDTO);
        }else if( RecommendSource.browse_case.getCode() == source ){
            serverResponse = browseCaseRecommend.loadRecommendTarget(memberId, pageDTO);
        }else if( RecommendSource.label_attrib.getCode() == source ){
            serverResponse = labelAttribRecommend.loadRecommendTarget(memberId, pageDTO);
        }

        logger.debug("本次加载["+RecommendSource.getInstance(source).getDesc()+"]推荐数据结果:"+JSON.toJSONString(serverResponse));

        // 数据处理
        Cattle.loadDataHandle(serverResponse, recommendComposeChunk, source, number);
    }

    // 组装 推荐块
    private RecommendComposeChunk assembleRecommendChunk(){

        RecommendComposeChunk recommendComposeChunk = new RecommendComposeChunk();

        // 查询配置 推荐数量
        int recommendNumber = recommendConfigService.queryConfigValue(RecommendConfigItem.recommend_number.getCode());

        // TODO 浏览商品推荐数量 暂不计算
        recommendComposeChunk.setBrowseGoodsRecommendNumber(0);
        // TODO 浏览案例推荐数量 暂不计算
        recommendComposeChunk.setBrowseCaseRecommendNumber(0);

        recommendComposeChunk.setLabelAttribRecommendNumber(recommendNumber);

        recommendComposeChunk.setRecommendTotal(recommendNumber);
        logger.debug("当次推荐条数:BrowseGoods["+recommendComposeChunk.getBrowseGoodsRecommendNumber()+"],BrowseCase["+recommendComposeChunk.getBrowseCaseRecommendNumber()+"],LabelAttrib["+recommendComposeChunk.getLabelAttribRecommendNumber()+"]");

        return recommendComposeChunk;
    }

    // 检验 userToken
    private ServerResponse checkUserToken(String userToken){
        if(CommonUtil.isEmpty(userToken)){
            return ServerResponse.createByErrorMessage("[userToken]为空!");
        }else{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createbyUserTokenError();
            }
            Member member = memberMapper.selectByPrimaryKey(accessToken.getMemberId());
            return ServerResponse.createBySuccess(member);
        }
    }

}

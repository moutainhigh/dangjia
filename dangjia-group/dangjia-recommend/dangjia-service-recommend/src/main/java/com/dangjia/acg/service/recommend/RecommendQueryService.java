package com.dangjia.acg.service.recommend;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
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
import com.dangjia.acg.support.recommend.util.RecommendSource;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            Member member = null;
            // 检验 userToken
            ServerResponse serverResponse = checkUserToken(userToken, member);
            if( serverResponse != null ){
                return serverResponse;
            }
            String memberId = member.getId();

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

            return null;
        }catch(Exception e){
            logger.error("查询推荐 异常:", e);
            // 异常情况 查询推荐"默认项itemSubId:1"
            if( recommendComposeChunk != null ) pageDTO.setPageSize(recommendComposeChunk.getRecommendTotal());
            return recommendTargetService.queryPage(pageDTO, "1", null);
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

        // 数据处理
        Cattle.loadDataHandle(serverResponse, recommendComposeChunk, source, number);
    }

    // 组装 推荐块
    private RecommendComposeChunk assembleRecommendChunk(){

        RecommendComposeChunk recommendComposeChunk = new RecommendComposeChunk();

        // 查询配置 推荐数量
        int recommendNumber = recommendConfigService.queryConfigValue(CONFIG_CODE_RECOMMEND_NUMBER);

        // TODO 浏览商品推荐数量 暂不计算
        recommendComposeChunk.setBrowseGoodsRecommendNumber(0);
        // TODO 浏览案例推荐数量 暂不计算
        recommendComposeChunk.setBrowseCaseRecommendNumber(0);

        recommendComposeChunk.setLabelAttribRecommendNumber(recommendNumber);

        recommendComposeChunk.setRecommendTotal(recommendNumber);

        return recommendComposeChunk;
    }
    private static final String CONFIG_CODE_RECOMMEND_NUMBER = "recommend_number";

    // 检验 userToken
    private ServerResponse checkUserToken(String userToken, Member member){
        if(CommonUtil.isEmpty(userToken)){
            return ServerResponse.createByErrorMessage("[userToken]为空!");
        }else{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createbyUserTokenError();
            }
            member = memberMapper.selectByPrimaryKey(accessToken.getMemberId());
            return null;
        }
    }

}

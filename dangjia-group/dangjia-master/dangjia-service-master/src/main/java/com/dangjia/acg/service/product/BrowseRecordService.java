package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.app.AppActuaryOperationAPI;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.BrowseRecordDTO;
import com.dangjia.acg.mapper.product.IBrowseRecordMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BrowseRecord;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrowseRecordService {

    @Autowired
    private IBrowseRecordMapper iBrowseRecordMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private DjBasicsProductAPI djBasicsProductAPI;

    @Autowired
    private AppActuaryOperationAPI appActuaryOperationAPI;
    /**
     * @param request
     * @param userToken
     * @return
     */
    public ServerResponse queryBrowseRecord(HttpServletRequest request, String userToken,String vistsType) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String memberId=member.getId();
            //图片路径前缀
           // String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<BrowseRecord> list=iBrowseRecordMapper.queryBrowseRecord(memberId,vistsType);
            List<BrowseRecordDTO>  browseRecordList=new ArrayList<BrowseRecordDTO>();
            for(BrowseRecord browseRecord:list)
            {
               String productId= browseRecord.getProductId();
               //自动判断是人工、服务、商品
                BrowseRecordDTO browseRecordDTO=new BrowseRecordDTO();
                browseRecordDTO.setMemberId(browseRecord.getMemberId());
                browseRecordDTO.setVisitsNum(browseRecord.getVisitsNum());
                browseRecordDTO.setVistsType(browseRecord.getVistsType());
                browseRecordDTO.setProductId(productId);
                browseRecordDTO.setObject(JSONObject.parseObject(appActuaryOperationAPI.getCommo(request,productId,null).getResultObj().toString()));
                browseRecordList.add(browseRecordDTO);
            }
            return ServerResponse.createBySuccess("查询浏览记录成功!",browseRecordList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取用户浏览商品痕迹失败!");
        }
    }

    /**
     * @param request
     * @param userToken
     * @return
     */
    public ServerResponse addBrowseRecord(HttpServletRequest request, String userToken, String productId ,String vistsType ) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Example example = new Example(BrowseRecord.class);
            example.createCriteria().andEqualTo(BrowseRecord.PRODUCT_ID, productId);
            List<BrowseRecord> list = iBrowseRecordMapper.selectByExample(example);
            if (list.size()>0)
            {
                BrowseRecord record= list.get(0);
                if(record.getProductId().equals(productId))
                {
                    record.setVisitsNum(String.valueOf(Integer.parseInt(record.getVisitsNum())+1));
                    record.setMemberId(member.getId());
                    record.setVistsType(vistsType);
                    iBrowseRecordMapper.updateByPrimaryKeySelective(record);
                    return ServerResponse.createBySuccess("已经存在记录，修改成功!");
                }
            }
            BrowseRecord browseRecord=new BrowseRecord();
            browseRecord.setMemberId(member.getId());//用户编号
            browseRecord.setProductId(productId);
            browseRecord.setVisitsNum("1");
            browseRecord.setVistsType(vistsType);
            iBrowseRecordMapper.insert(browseRecord);

            return ServerResponse.createBySuccess("添加用户浏览记录成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,记录用户浏览商品痕迹失败!");
        }
    }
}

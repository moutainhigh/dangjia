package com.dangjia.acg.service.config;

import com.dangjia.acg.mapper.config.IConfigAdvertMapper;
import com.dangjia.acg.mapper.house.IHouseChoiceCaseMapper;
import com.dangjia.acg.modle.config.ConfigAdvert;
import com.dangjia.acg.modle.house.HouseChoiceCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigAdvertScheduler {

    private IConfigAdvertMapper configAdvertMapper;

    private IHouseChoiceCaseMapper houseChoiceCaseMapper;

    @Autowired
    public ConfigAdvertScheduler(IHouseChoiceCaseMapper houseChoiceCaseMapper, IConfigAdvertMapper configAdvertMapper) {
        this.configAdvertMapper = configAdvertMapper;
        this.houseChoiceCaseMapper = houseChoiceCaseMapper;
    }

    @Scheduled(cron = "10 * * * * ? ")
    public void selectTimingAd() {
        // 将超时的广告设置为不展示
        List<String> timeOutAd = configAdvertMapper.getTimeOutAd();
        if (timeOutAd.size() > 0) {
            for (String n : timeOutAd) {
                ConfigAdvert configAdvert = configAdvertMapper.selectByPrimaryKey(n);
                configAdvert.setToShow(0);
                configAdvertMapper.updateByPrimaryKeySelective(configAdvert);
            }
        }

        // 将符合时间的广告设置为定时展示
        List<String> timingAd = configAdvertMapper.getTimingAd();
        if (timingAd.size() > 0) {
            for (String n : timingAd) {
                ConfigAdvert configAdvert = configAdvertMapper.selectByPrimaryKey(n);
                configAdvert.setToShow(3);
                configAdvertMapper.updateByPrimaryKeySelective(configAdvert);
            }
        }
    }

    @Scheduled(cron = "15 * * * * ? ")
    public void selectTimingFeatured() {
        // 将符合时间的精选案例设置为不展示
        List<String> timeOutFeatured = houseChoiceCaseMapper.getTimeOutAd();
        if (timeOutFeatured.size() > 0) {
            for (String n : timeOutFeatured) {
                HouseChoiceCase houseChoiceCase = houseChoiceCaseMapper.selectByPrimaryKey(n);
                houseChoiceCase.setToShow(0);
                houseChoiceCaseMapper.updateByPrimaryKeySelective(houseChoiceCase);
            }
        }

        // 将符合时间的精选案例设置为展示
        List<String> timingFeatured = houseChoiceCaseMapper.getTimingAd();
        if (timingFeatured.size() > 0) {
            for (String n : timingFeatured) {
                HouseChoiceCase houseChoiceCase = houseChoiceCaseMapper.selectByPrimaryKey(n);
                houseChoiceCase.setToShow(3);
                houseChoiceCaseMapper.updateByPrimaryKeySelective(houseChoiceCase);
            }
        }

    }
}

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
            ConfigAdvert configAdvert = new ConfigAdvert();
            configAdvert.setToShow("0");
            for (String n : timeOutAd) {
                configAdvert.setId(n);
                configAdvertMapper.updateByPrimaryKey(configAdvert);
            }
        }

        // 将符合时间的广告设置为展示
        List<String> timingAd = configAdvertMapper.getTimingAd();
        if (timingAd.size() > 0) {
            ConfigAdvert configAdvert = new ConfigAdvert();
            configAdvert.setToShow("1");
            for (String n : timingAd) {
                configAdvert.setId(n);
                configAdvertMapper.updateByPrimaryKey(configAdvert);
            }
        }
    }

    @Scheduled(cron = "15 * * * * ? ")
    public void selectTimingFeatured() {
        List<String> timeOutAd = houseChoiceCaseMapper.getTimeOutAd();
        if (timeOutAd.size() > 0) {
            HouseChoiceCase houseChoiceCase = new HouseChoiceCase();
            houseChoiceCase.setToShow("0");
            for (String n : timeOutAd) {
                houseChoiceCase.setId(n);
                houseChoiceCaseMapper.updateByPrimaryKey(houseChoiceCase);
            }
        }

        // 将符合时间的广告设置为展示
        List<String> timingAd = houseChoiceCaseMapper.getTimingAd();
        if (timingAd.size() > 0) {
            HouseChoiceCase houseChoiceCase = new HouseChoiceCase();
            houseChoiceCase.setToShow("1");
            for (String n : timingAd) {
                houseChoiceCase.setId(n);
                houseChoiceCaseMapper.updateByPrimaryKey(houseChoiceCase);
            }
        }

    }
}

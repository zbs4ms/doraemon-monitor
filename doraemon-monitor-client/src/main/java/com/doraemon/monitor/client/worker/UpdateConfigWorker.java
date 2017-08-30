package com.doraemon.monitor.client.worker;

import com.doraemon.monitor.client.Service.UpdateConfigService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 拉取配置信息
 * Created by zbs on 2017/7/18.
 */
@Component
@Log4j
public class UpdateConfigWorker {

    @Autowired
    UpdateConfigService updateConfigService;

    /**
     * 每1分钟轮训一次,从客户端拉取配置信息
     */
    //@Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "${usability.updateCron}")
    public void update() {
        try {
            updateConfigService.update();
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}

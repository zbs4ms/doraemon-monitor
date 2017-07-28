package com.doraemon.monitor.client.worker;

import com.doraemon.monitor.client.Service.UpdateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 拉取配置信息
 * Created by zbs on 2017/7/18.
 */
@Component
public class UpdateConfigWorker {

    @Autowired
    UpdateConfigService updateConfigService;

    /**
     * 每1分钟轮训一次,从客户端拉取配置信息
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void update() throws Exception {
        updateConfigService.update();
    }
}

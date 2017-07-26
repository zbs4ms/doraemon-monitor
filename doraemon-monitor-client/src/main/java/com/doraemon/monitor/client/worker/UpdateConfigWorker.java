package com.doraemon.monitor.client.worker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doraemon.monitor.client.Service.UpdateConfigService;
import com.doraemon.monitor.client.controller.protocol.ClientPro;
import com.google.common.base.Preconditions;
import com.us.base.util.http.HttpAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zbs on 2017/7/18.
 */
@Component
public class UpdateConfigWorker {

    @Autowired
    UpdateConfigService updateConfigService;

    /**
     * 每1分钟轮训一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void update() throws Exception {
        updateConfigService.update();
    }
}

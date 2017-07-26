package com.doraemon.monitor.client.worker;

import com.alibaba.fastjson.JSON;
import com.doraemon.monitor.client.util.Common;
import com.us.base.util.http.HttpAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zbs on 2017/7/6.
 */
@Component
public class SendMessageWorker {

    @Autowired
    ConcurrentLinkedQueue<List> concurrentLinkedQueue;

    /**
     * 每5分钟轮训一次
     */
    @Scheduled(cron="0/5 * * * * ?")
    public void send() throws Exception {
        List list = new ArrayList();
        while (!concurrentLinkedQueue.isEmpty()){
             list.addAll(concurrentLinkedQueue.poll());
        }
        if(list.size()>0) {
            String param = "message="+JSON.toJSONString(list);
            System.out.println(HttpAgent.create().sendPost(Common.ADD_MESSAGE_URL, param));
        }
    }
}

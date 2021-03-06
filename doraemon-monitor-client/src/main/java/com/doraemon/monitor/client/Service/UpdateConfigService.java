package com.doraemon.monitor.client.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doraemon.monitor.client.Main;
import com.doraemon.monitor.client.controller.protocol.ClientPro;
import com.doraemon.monitor.client.util.Common;
import com.google.common.base.Preconditions;
import com.us.base.util.http.HttpAgent;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by zbs on 2017/7/18.
 */
@Service
@Log4j
public class UpdateConfigService {

    private ClientPro clientPro;

    public synchronized void update() throws Exception {

        String param = "ip=" + Main.LOCAL_IP;
        String response = HttpAgent.create().sendGet(Common.UPDATE_CONFIG_URL, param);
        log.info("向服务器端请求配置文件"+Common.UPDATE_CONFIG_URL+"?"+param+"\n返回数据:"+response);
        JSONObject jsonObject = JSON.parseObject(response);
        Preconditions.checkState((Boolean) jsonObject.get("success"), "获取终端服务器失败." + jsonObject.get("data"));
        String content = String.valueOf(Preconditions.checkNotNull(jsonObject.get("data"), "获取终端服务器信息为空."));
        List<ClientPro> list = JSONObject.parseArray(content, ClientPro.class);
        clientPro = list.get(0);
    }

    public ClientPro getClientPro(){
        return clientPro;
    }
}

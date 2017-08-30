package com.doraemon.monitor.client.worker;

import com.doraemon.monitor.client.Service.UpdateConfigService;
import com.doraemon.monitor.client.controller.protocol.ClientPro;
import com.doraemon.monitor.client.controller.protocol.MessagePro;
import com.doraemon.monitor.client.controller.protocol.TerminalPro;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ping 终端信息
 * Created by zbs on 2017/7/6.
 */
@Component
@Log4j
public class PingWorker {

    @Autowired
    ConcurrentLinkedQueue<List> concurrentLinkedQueue;
    @Autowired
    UpdateConfigService updateConfigService;


    /**
     * 每1分钟轮训一次,根据配置文件ping终端,收集信息
     */
    //@Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "${usability.pingCron}")
    public void ping() {
        try {
            if (updateConfigService == null)
                log.error("从服务端无法获取到配置文件信息.");
            List<MessagePro> messageProList = new ArrayList<>();
            ClientPro clientPro = updateConfigService.getClientPro();
            if (clientPro == null)
                return;
            for (TerminalPro terminalPro : clientPro.getTerminalList()) {
                InetAddress address = InetAddress.getByName(terminalPro.getTerminalIp());
                MessagePro messagePro = new MessagePro();
                messagePro.setStatus(getStatus(address, terminalPro.getDeviceType()));
                messagePro.setTime(new Date());
                messagePro.setIp(terminalPro.getTerminalIp());
                messagePro.setDeviceType(terminalPro.getDeviceType());
                messageProList.add(messagePro);
            }
            concurrentLinkedQueue.add(messageProList);
        }catch (Exception e){
            log.error(e);
        }
    }


    private String getStatus(InetAddress address, String type) throws IOException {
        switch (type) {
            case "WAN":
                if (address.isReachable(100))
                    return "0";
                if (address.isReachable(200))
                    return "1";
                return "-1";
            case "LAN":
                if (address.isReachable(10))
                    return "0";
                if (address.isReachable(20))
                    return "1";
                return "-1";
            case "AP":
                if (address.isReachable(20))
                    return "0";
                if (address.isReachable(40))
                    return "1";
                return "-1";
            default:
                new Exception("无效的设备类型.");
        }
        return "-1";
    }
}

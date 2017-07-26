package com.doraemon.monitor.controller.protocol;

import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.Terminal;
import lombok.Data;

import java.util.Date;

/**
 * Created by zbs on 2017/7/18.
 */
@Data
public class TerminalPro {

    private String ip;

    private String clientNick;

    private String region;

    private String shopId;

    private String terminalNick;

    private String deviceType;

    private Date offTime;

    private int warningNum;

    private String status;

    public TerminalPro(Client client, Terminal terminal){
        this.setIp(client.getIp());
        this.setClientNick(client.getNick());
        this.setRegion(client.getRegion());
        this.setShopId(client.getShopId());
        this.setTerminalNick(terminal.getNick());
        this.setDeviceType(terminal.getDeviceType());
        this.setOffTime(terminal.getOffTime());
        this.setWarningNum(terminal.getWarningNum());
        this.setStatus(terminal.getStatus());
    }
}

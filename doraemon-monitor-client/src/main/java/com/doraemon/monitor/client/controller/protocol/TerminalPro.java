package com.doraemon.monitor.client.controller.protocol;

import lombok.Data;

@Data
public class TerminalPro {

    private String nick;

    private String clientIp;

    private String terminalIp;

    private String deviceType;

    public String getDeviceType(){
        if(terminalIp == null)
            return null;
        String[] mark = terminalIp.split("[.]");
        switch (mark[3]){
            case "1":
                return "WAN";
            default:
                return "LAN";
        }
    }

}
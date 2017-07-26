package com.doraemon.monitor.client.controller.protocol;

import lombok.Data;

@Data
public class TerminalPro {

    private String nick;

    private String clientIp;

    private String terminalIp;

    private String deviceType;

}
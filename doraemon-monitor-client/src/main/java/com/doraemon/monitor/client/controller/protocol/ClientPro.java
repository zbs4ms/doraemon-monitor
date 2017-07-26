package com.doraemon.monitor.client.controller.protocol;

import lombok.Data;

import java.util.List;

@Data
public class ClientPro {

    private String ip;

    private String nick;

    private String region;

    private String shopId;

    private List<TerminalPro> terminalList;

}
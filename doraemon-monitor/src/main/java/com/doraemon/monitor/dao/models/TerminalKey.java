package com.doraemon.monitor.dao.models;

import lombok.Data;

import javax.persistence.Id;

@Data
public class TerminalKey {

    @Id
    private String clientIp;
    @Id
    private String terminalIp;

    public TerminalKey(){}
    public TerminalKey(String clientIp,String terminalIp){
        this.clientIp = clientIp;
        this.terminalIp = terminalIp;
    }

}
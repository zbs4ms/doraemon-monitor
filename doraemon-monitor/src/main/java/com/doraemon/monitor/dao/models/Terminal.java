package com.doraemon.monitor.dao.models;

import lombok.Data;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "terminal")
public class Terminal extends TerminalKey {

    private String nick;

    private String deviceType;

    private Date offTime;

    private Integer warningNum;

    private String status;

    private String phone;

    @Transient  //终端可用值
    private BigDecimal terminalUsability;

    public Terminal(){}

    public Terminal(TerminalKey terminalKey){
        this.setClientIp(terminalKey.getClientIp());
        this.setTerminalIp(terminalKey.getTerminalIp());
    }
}
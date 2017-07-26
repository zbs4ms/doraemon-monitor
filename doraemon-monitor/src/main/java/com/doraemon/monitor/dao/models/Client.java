package com.doraemon.monitor.dao.models;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.math.BigDecimal;
import java.util.List;

@Data
@Table(name = "client")
public class Client {
    @Id
    private String ip;

    private String nick;

    private String region;

    private String shopId;

    @Transient
    private List<Terminal> terminalList;

    @Transient  //门店可用值
    private BigDecimal clientUsability;

}
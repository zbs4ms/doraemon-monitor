package com.doraemon.monitor.dao.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/7/26.
 */
@Data
@Table(name = "client_usability")
public class ClientUsability {
    @Id
    Long id;
    Date statisticalTime;
    String timeType;
    String clientIp;
    BigDecimal usability;

    @Transient
    List<TerminalUsability> terminalUsabilityList;

    public ClientUsability(){}
    public ClientUsability(Long id,Date statisticalTime,String timeType,String clientIp,BigDecimal usability){
        this.id = id;
        this.statisticalTime = statisticalTime;
        this.timeType = timeType;
        this.clientIp = clientIp;
        this.usability = usability;
    }
}

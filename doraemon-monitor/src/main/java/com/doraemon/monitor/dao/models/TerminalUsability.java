package com.doraemon.monitor.dao.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zbs on 2017/7/26.
 */
@Data
@Table(name = "terminal_usability")
public class TerminalUsability {

    @Id
    Long id;
    Date statisticalTime;
    String timeType;
    String clientIp;
    String terminalIp;
    BigDecimal usability;

    public TerminalUsability() {
    }

    public TerminalUsability(Long id, Date statisticalTime, String timeType, String clientIp, String terminalIp, BigDecimal usability) {
        this.id = id;
        this.statisticalTime = statisticalTime;
        this.timeType = timeType;
        this.clientIp = clientIp;
        this.terminalIp = terminalIp;
        this.usability = usability;
    }
}

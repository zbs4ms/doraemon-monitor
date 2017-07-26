package com.doraemon.monitor.dao.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.IdClass;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

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
}

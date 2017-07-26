package com.doraemon.monitor.dao.models;

import lombok.Data;
import javax.persistence.Id;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "monitor_log")
public class MonitorLog {
    @Id
    private Long id;

    private String clientIp;

    private String terminalIp;

    private String status;

    private Date createTime;

}
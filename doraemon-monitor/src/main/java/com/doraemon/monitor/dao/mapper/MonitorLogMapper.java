package com.doraemon.monitor.dao.mapper;

import com.doraemon.monitor.dao.models.MonitorLog;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface MonitorLogMapper extends MyMapper<MonitorLog>{

    @Select({" select * from monitor_log where " +
            " client_ip=#{clientIp} " +
            " and date(create_time) between #{startDate} " +
            " and #{stopDate} " +
            " order by create_time "})
    List<MonitorLog> selectByDate(Map<String,Object> map);

    @Select({" select * from monitor_log where " +
            " date(create_time) between #{startDate} " +
            " and #{stopDate} " +
            " order by create_time "})
    List<MonitorLog> selectByDateAll(Map<String,Object> map);

    @Select({"select * from monitor_log where create_time>=#{startDate} and create_time<=#{stopDate} order by create_time "})
    List<MonitorLog> selectAllByDate(Map<String,Object> map);
}
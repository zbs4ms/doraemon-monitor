package com.doraemon.monitor.dao.mapper;

import com.doraemon.monitor.dao.models.Client;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientMapper extends MyMapper<Client> {

    @Select({"select region from client GROUP BY region"})
    List<String> queryAllRegion();

}
package com.doraemon.monitor.dao.mapper;

import com.doraemon.monitor.dao.models.ClientUsability;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Update;

/**
 * Created by zbs on 2017/7/26.
 */
public interface ClientUsabilityMapper extends MyMapper<ClientUsability> {

    @Update({"update client_usability set usability=#{usability} where id=#{id}"})
    int updateUsability(ClientUsability clientUsability);
}

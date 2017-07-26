package com.doraemon.monitor.dao.mapper;

import com.doraemon.monitor.dao.models.TerminalUsability;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Update;

/**
 * Created by zbs on 2017/7/26.
 */
public interface TerminalUsabilityMapper extends MyMapper<TerminalUsability>{

    @Update({"update terminal_usability set usability=#{usability} where id=#{id}"})
    int updateUsability(TerminalUsability terminalUsability);
}

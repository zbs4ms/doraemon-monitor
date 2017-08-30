package com.doraemon.monitor.dao.mapper;

import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.dao.models.TerminalKey;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TerminalMapper extends MyMapper<Terminal> {

    /**
     * 更新终端数据从断开恢复到正常
     * @param terminalkey
     * @return
     */
    @Update({"update terminal set off_time=null,warning_num=0 where client_ip=#{clientIp} and terminal_ip=#{terminalIp}"})
    int recovery(TerminalKey terminalkey);

    @Update({"update terminal set warning_num=1 where client_ip=#{clientIp} and terminal_ip=#{terminalIp}"})
    int disconnect(TerminalKey terminalKey);

    @Update({"update terminal set warning_num=warning_num+1 where client_ip=#{clientIp} and terminal_ip=#{terminalIp}"})
    int warning(TerminalKey terminalKey);

    @Select({"select * from terminal where client_ip=#{clientIp}"})
    List<Terminal> selectByclientIp(String clientIp);

    //add by csrr 去除掉线时间为空的。。
    @Select({"select * from terminal where client_ip=#{clientIp} and off_time is not null"})
    List<Terminal> selectByClientIpOffTime(String clientIp);

    @Select({"select * from terminal where client_ip=#{clientIp}"})
    List<Terminal> selectByClientIp(String clientIp);

    @Select({"select * from terminal where client_ip=#{clientIp} and terminal_ip=#{terminalIp}"})
    Terminal selectByClientIpAndTerminalIp(Terminal terminal);
}

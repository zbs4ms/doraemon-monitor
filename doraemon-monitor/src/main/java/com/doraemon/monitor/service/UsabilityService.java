package com.doraemon.monitor.service;

import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.ClientUsabilityMapper;
import com.doraemon.monitor.dao.mapper.TerminalMapper;
import com.doraemon.monitor.dao.mapper.TerminalUsabilityMapper;
import com.doraemon.monitor.dao.models.*;
import com.doraemon.monitor.util.DateTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/7/27.
 */
@Service
@Log4j
public class UsabilityService {

    @Autowired
    ClientUsabilityMapper clientUsabilityMapper;

    @Autowired
    TerminalUsabilityMapper terminalUsabilityMapper;

    @Autowired
    ClientMapper clientMapper;

    @Autowired
    TerminalMapper terminalMapper;

    /**
     * 更新或者创建client_usability数据
     *
     * @param timeType
     * @param clientIp
     * @param time
     * @param usability
     */
    public void updateOrInsertClientUsability(String timeType, String clientIp, Date time, BigDecimal usability) {
        ClientUsability clientUsability = new ClientUsability(null, time, timeType, clientIp, null);
        ClientUsability results = clientUsabilityMapper.selectOne(clientUsability);
        //判断是否有数据,有数据进行更新操作,没有数据进行插入数据操作
        if (results == null) {
            log.info("客户端可用性表中[插入]数据: 开始时间=" + time + " 可用性=" + usability);
            clientUsability.setUsability(usability);
            clientUsabilityMapper.insert(clientUsability);
        } else {
            log.info("客户端可用性表中[更新]数据: 开始时间=" + time + " 可用性=" + usability);
            results.setUsability(usability);
            clientUsabilityMapper.updateUsability(results);
        }
    }


    /**
     * 更新或者创建terminal_usability数据
     *
     * @param timeType
     * @param clientIp
     * @param terminalIp
     * @param time
     * @param usability
     */
    public void updateOrInsertTerminalUsability(String timeType, String clientIp, String terminalIp, Date time, BigDecimal usability) {
        TerminalUsability terminalUsability = new TerminalUsability(null, time, timeType, clientIp, terminalIp, null);
        TerminalUsability results = terminalUsabilityMapper.selectOne(terminalUsability);
        if (results == null) {
            terminalUsability.setUsability(usability);
            log.info("终端可用性表中[插入]数据: 开始时间=" + time + " 可用性=" + usability);
            terminalUsabilityMapper.insert(terminalUsability);
        } else {
            results.setUsability(usability);
            log.info("终端可用性表中[更新]数据: 开始时间=" + time + " 可用性=" + usability);
            terminalUsabilityMapper.updateUsability(results);
        }
    }

    public List<Client> selectClientUsability(String clientIp, DateTool.DateBean dateBean, String timeType) throws Exception {
        ClientUsability clientUsability = new ClientUsability();
        clientUsability.setTimeType(timeType);
        clientUsability.setStatisticalTime(dateBean.getStartDate());
        if(clientIp!=null)
            clientUsability.setClientIp(clientIp);
        List<ClientUsability> clientUsabilityList = clientUsabilityMapper.select(clientUsability);
        if(clientUsabilityList == null || clientUsabilityList.size()<1)
            return null;
        List<Client> clientList = new ArrayList<>();
        for(ClientUsability usability : clientUsabilityList){
            Client client = clientMapper.selectByPrimaryKey(usability.getClientIp());
            client.setClientUsability(usability.getUsability());
            List<Terminal> terminalList = selectTerminalUsabilityByClientIp(usability.getClientIp(),dateBean,timeType);
            client.setTerminalList(terminalList);
            clientList.add(client);
        }
        return clientList;
    }

    private List<Terminal> selectTerminalUsabilityByClientIp(String clientIp, DateTool.DateBean dateBean, String timeType) throws Exception {
        if(clientIp == null)
            throw new Exception("客户端IP不能为空.");
        TerminalUsability terminalUsability = new TerminalUsability();
        terminalUsability.setClientIp(clientIp);
        terminalUsability.setTimeType(timeType);
        terminalUsability.setStatisticalTime(dateBean.getStartDate());
        List<TerminalUsability> terminalUsabilityList = terminalUsabilityMapper.select(terminalUsability);
        if(terminalUsabilityList == null || terminalUsabilityList.size()<1)
            return null;
        List<Terminal> terminalList = new ArrayList<>();
        for(TerminalUsability usability : terminalUsabilityList){
            Terminal terminal = terminalMapper.selectByPrimaryKey(new Terminal(new TerminalKey(clientIp,usability.getTerminalIp())));
            terminal.setTerminalUsability(usability.getUsability());
            terminalList.add(terminal);
        }
        return terminalList;
    }
}

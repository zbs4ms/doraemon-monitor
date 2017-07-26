package com.doraemon.monitor.worker;


import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.ClientUsabilityMapper;
import com.doraemon.monitor.dao.mapper.TerminalUsabilityMapper;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.ClientUsability;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.dao.models.TerminalUsability;
import com.doraemon.monitor.service.CountService;
import com.doraemon.monitor.util.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/7/26.
 */
@Component
public class UsabilityWorker {

    @Autowired
    CountService countService;
    @Autowired
    ClientMapper clientMapper;
    @Autowired
    ClientUsabilityMapper clientUsabilityMapper;
    @Autowired
    TerminalUsabilityMapper terminalUsabilityMapper;
//
//    @Scheduled(cron = "* 0/1 * * * ? ")
//    public void lastYearUsability() throws Exception {
//        DateTool.DateBean dateBean = DateTool.create().getLastYear();
//        update(dateBean,"Y");
//    }
//
//    @Scheduled(cron = "0/1 * * * * ? ")
//    public void lastMonthUsability() throws Exception {
//        DateTool.DateBean dateBean = DateTool.create().getLastWeek();
//        update(dateBean,"M");
//    }
//
//    @Scheduled(cron = "0/1 * * * * ? ")
//    public void lastWeeksUsability() throws Exception {
//        DateTool.DateBean dateBean = DateTool.create().getLastWeek();
//        update(dateBean,"W");
//    }


    @Scheduled(cron = "0/1 * * * * ? ")
    public void lastDayUsability() throws Exception {
        DateTool.DateBean dateBean = DateTool.create().getLastDay();
        update(dateBean,"D");
    }

    private void update(DateTool.DateBean dateBean,String timeType) throws Exception {
        List<Client> clients = clientMapper.selectAll();
        if(clients == null)
            return;
        for(Client cli : clients) {
            Client client = countService.totalClientErrorTimeByIpNotNull(cli.getIp(),dateBean.getStartDate(),dateBean.getStopDate());
            if(client == null)
                return;
            updateClientUsability(timeType,client.getIp(),dateBean.getStartDate(),client.getClientUsability());
            for(Terminal terminal : client.getTerminalList()){
                updateTerminalUsability(timeType,client.getIp(),terminal.getTerminalIp(),dateBean.getStartDate(),terminal.getTerminalUsability());
            }
        }
    }

    private void updateTerminalUsability(String timeType, String clientIp,String terminalIp,Date time, BigDecimal usability){
        TerminalUsability terminalUsability = new TerminalUsability();
        terminalUsability.setClientIp(clientIp);
        terminalUsability.setStatisticalTime(time);
        terminalUsability.setTimeType(timeType);
        terminalUsability.setTerminalIp(terminalIp);
        TerminalUsability results = terminalUsabilityMapper.selectOne(terminalUsability);
        terminalUsability.setUsability(usability);
        if(results == null){
            terminalUsabilityMapper.insert(terminalUsability);
        }else{
            terminalUsability.setId(results.getId());
            terminalUsabilityMapper.updateUsability(terminalUsability);
        }
    }

    private void updateClientUsability(String timeType, String clientIp, Date time, BigDecimal usability){
        ClientUsability clientUsability = new ClientUsability();
        clientUsability.setClientIp(clientIp);
        clientUsability.setStatisticalTime(time);
        clientUsability.setTimeType(timeType);
        ClientUsability results = clientUsabilityMapper.selectOne(clientUsability);
        if(results == null) {
            clientUsability.setUsability(usability);
            clientUsabilityMapper.insert(clientUsability);
        }else {
            results.setUsability(usability);
            clientUsabilityMapper.updateUsability(results);
        }
    }
}

package com.doraemon.monitor.worker;


import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.ClientUsabilityMapper;
import com.doraemon.monitor.dao.mapper.TerminalUsabilityMapper;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.service.CountService;
import com.doraemon.monitor.service.UsabilityService;
import com.doraemon.monitor.util.DateTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zbs on 2017/7/26.
 */
@Component
@Log4j
public class UsabilityWorker {

    @Autowired
    CountService countService;
    @Autowired
    ClientMapper clientMapper;
    @Autowired
    ClientUsabilityMapper clientUsabilityMapper;
    @Autowired
    TerminalUsabilityMapper terminalUsabilityMapper;
    @Autowired
    UsabilityService usabilityService;

    /**
     * 统计上年的数据,每年一月一号凌晨一点执行
     *
     * @throws Exception
     */
   // @Scheduled(cron = "0 0 1 1 1 ?")
    @Scheduled(cron = "*/30 * * * * ?")
    public void lastYearUsability() throws Exception {
        DateTool.DateBean dateBean = DateTool.create().getLastYear();
        log.info("统计上年的数据,时间段:  开始时间=" + dateBean.getStartDate() + "  结束时间:" + dateBean.getStopDate());
        statisticalAndCommit(dateBean, "Y");
    }

    /**
     * 统计上个月的数据,每月一号凌晨一点执行
     *
     * @throws Exception
     */
   // @Scheduled(cron = "0 0 1 1 * ?")
    @Scheduled(cron = "*/20 * * * * ?")
    public void lastMonthUsability() throws Exception {
        DateTool.DateBean dateBean = DateTool.create().getLastWeek();
        log.info("统计上个月的数据,时间段:  开始时间=" + dateBean.getStartDate() + "  结束时间:" + dateBean.getStopDate());
        statisticalAndCommit(dateBean, "M");
    }

    /**
     * 统计上周的数据,每周一凌晨一点执行
     *
     * @throws Exception
     */
   // @Scheduled(cron = "0 0 1 ? * MON")
    @Scheduled(cron = "*/10 * * * * ?")
    public void lastWeeksUsability() throws Exception {
        DateTool.DateBean dateBean = DateTool.create().getLastWeek();
        log.info("统计上周的数据,时间段:  开始时间=" + dateBean.getStartDate() + "  结束时间:" + dateBean.getStopDate());
        statisticalAndCommit(dateBean, "W");
    }

    /**
     * 统计昨天的数据,每天凌晨一点执行
     *
     * @throws Exception
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void lastDayUsability() throws Exception {
        DateTool.DateBean dateBean = DateTool.create().getLastDay();
        log.info("统计昨天的数据,时间段:  开始时间=" + dateBean.getStartDate() + "  结束时间:" + dateBean.getStopDate());
        statisticalAndCommit(dateBean, "D");
    }

    /**
     * 统计并保存数据
     *
     * @param dateBean
     * @param timeType
     * @throws Exception
     */
    public void statisticalAndCommit(DateTool.DateBean dateBean, String timeType) throws Exception {
        //查询全部的客户端
        List<Client> clientList = clientMapper.selectAll();
        if (clientList == null || clientList.size() < 1)
            return;
        for (Client client : clientList) {
            //获取统计的数据
            Client statisticalData = countService.totalClientErrorTimeByIpNotNull(client.getIp(), dateBean.getStartDate(), dateBean.getStopDate());
            if (statisticalData == null)
                continue;
            //保存或更新统计数据
            usabilityService.updateOrInsertClientUsability(timeType, client.getIp(), dateBean.getStartDate(), statisticalData.getClientUsability());
            if (statisticalData.getTerminalList() == null || statisticalData.getTerminalList().size() < 1)
                continue;
            for (Terminal terminal : statisticalData.getTerminalList()) {
                //保存或更新统计数据
                usabilityService.updateOrInsertTerminalUsability(timeType, client.getIp(), terminal.getTerminalIp(), dateBean.getStartDate(), terminal.getTerminalUsability());
            }
        }
    }
}

package com.doraemon.monitor.service;

import com.doraemon.monitor.controller.protocol.PagePro;
import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.MonitorLogMapper;
import com.doraemon.monitor.dao.mapper.TerminalMapper;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.MonitorLog;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.util.BigDecimalCount;
import com.doraemon.monitor.util.DateTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by zbs on 2017/7/19.
 */
@Service
@Log4j
public class CountService {

    @Autowired
    MonitorLogMapper monitorLogMapper;
    @Autowired
    TerminalMapper terminalMapper;
    @Autowired
    ClientMapper clientMapper;
    @Autowired
    ConfigService configService;
    @Autowired
    MessageSerive messageSerive;


    //忽略的时间差,不纳入断开时间统计的时间差,单位分钟
    private static int ignore_time_difference = 5;


    /**
     * 初始化断开时间统计集合
     * @param monitorLog
     * @param clientErrorTime
     * @param terminalErrorTime
     */
    private void initDisconnectTimeCollection(MonitorLog monitorLog,Map<String, BigDecimal> clientErrorTime,Map<String, BigDecimal> terminalErrorTime){
        if (clientErrorTime.get(monitorLog.getClientIp()) == null)
            clientErrorTime.put(monitorLog.getClientIp(), BigDecimal.ZERO);
        if (terminalErrorTime.get(getTerminalKey(monitorLog)) == null)
            terminalErrorTime.put(getTerminalKey(monitorLog), BigDecimal.ZERO);
    }


    public Client totalClientErrorTimeByIpNotNull(String clientIp, Date startDate, Date stopDate) throws Exception {
        if (clientIp == null)
            throw new Exception("client ip 不能为空.");
        Client client = clientMapper.selectByPrimaryKey(clientIp);
        if (client == null)
            return null;
        DisconnectTimeBean errorTime = statisticalDisconnectTime(clientIp, startDate, stopDate);
        if (errorTime == null)
            return null;
        Map<String, BigDecimal> clientErrorTime = errorTime.getClientErrorTime();
        Map<String, BigDecimal> terminalErrorTime = errorTime.getTerminalErrorTime();
        Terminal selectTerminal = new Terminal();
        selectTerminal.setClientIp(client.getIp());
        List<Terminal> terminalList = terminalMapper.select(selectTerminal);
        //可用性统计 --- 客户端
        BigDecimal totalTime = clientErrorTime.get(client.getIp());
        BigDecimal deviceNumber = new BigDecimal(terminalList.size());
        BigDecimal days = new BigDecimal(DateTool.create().diffDay(startDate, stopDate));
        client.setClientUsability(usability(totalTime, deviceNumber, days));
        for (Terminal terminal : terminalList) {
            //可用性统计 --- 终端
            totalTime = terminalErrorTime.get(getTerminalKey(client.getIp(), terminal.getTerminalIp()));
            days = new BigDecimal(DateTool.create().diffDay(startDate, stopDate));
            terminal.setTerminalUsability(usability(totalTime, new BigDecimal(1), days));
        }
        client.setTerminalList(terminalList);
        return client;
    }

    public List<Client> totalClientErrorTime(String clientIp, Date startDate, Date stopDate) {
        List<Client> clientList = clientIp == null ?
                clientMapper.selectAll() :
                new ArrayList<>(Arrays.asList(clientMapper.selectByPrimaryKey(clientIp)));
        if (clientList == null || clientList.size() < 1)
            return null;
        DisconnectTimeBean errorTime = statisticalDisconnectTime(clientIp, startDate, stopDate);
        Map<String, BigDecimal> clientErrorTime = errorTime.getClientErrorTime();
        Map<String, BigDecimal> terminalErrorTime = errorTime.getTerminalErrorTime();
        for (Client client : clientList) {
            client.setClientUsability(clientErrorTime.get(client.getIp()));
            Terminal selectTerminal = new Terminal();
            selectTerminal.setClientIp(client.getIp());
            List<Terminal> terminalList = terminalMapper.select(selectTerminal);
            for (Terminal terminal : terminalList) {
                terminal.setTerminalUsability(terminalErrorTime.get(getTerminalKey(client.getIp(), terminal.getTerminalIp())));
            }
            client.setTerminalList(terminalList);
        }
        return clientList;
    }

    public PageInfo<Client> totalClientErrorTime(String clientIp, Date startDate, Date stopDate, PagePro pagePro) {
        PageHelper.startPage(pagePro.getPage(), pagePro.getRow());
        List<Client> clientList = totalClientErrorTime(clientIp, startDate, stopDate);
        return new PageInfo<>(clientList);
    }





    /**
     * 可用值计算公式
     *
     * @param errorTime
     * @param deviceNumber
     * @param days
     * @return
     */
    private BigDecimal usability(BigDecimal errorTime, BigDecimal deviceNumber, BigDecimal days) {
        BigDecimal one = errorTime;
        if (one.equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;
        BigDecimal two = BigDecimalCount.multiply(deviceNumber, new BigDecimal(600), days);
        if (two.equals(BigDecimal.ZERO)) {
            log.warn("[警告]可用值计算公式中分母为0. 异常时间=" + errorTime + ",设备台数=" + deviceNumber + ",统计天数=" + days);
            return BigDecimal.ZERO;
        }
        BigDecimal value = one.divide(two, 2, BigDecimal.ROUND_HALF_EVEN);
        log.info("统计可用性:异常时间=" + errorTime + ",设备台数=" + deviceNumber + ",统计天数=" + days + ",可用性:" + one + "/" + two + "=" + value);
        return value;
    }

    /**
     * 获取到时间段内,全部设备或者指定设备的断开时间
     * @param clientIp
     * @param startDate
     * @param stopDate
     * @return
     */
    private DisconnectTimeBean statisticalDisconnectTime(String clientIp, Date startDate, Date stopDate) {
        //按客户端IP和时间查询出所有的日志
        List<MonitorLog> monitorLogList = messageSerive.queryByIpAndTime(clientIp, startDate, stopDate);
        if (monitorLogList == null || monitorLogList.size() < 1)
            return null;
        //发生异常的设备集合
        Map<String, MonitorLog> errorTerminal = new HashMap<>();
        //统计了客户端异常时间集合
        Map<String, BigDecimal> clientErrorTime = new HashMap<>();
        //统计了终端异常时间的集合
        Map<String, BigDecimal> terminalErrorTime = new HashMap<>();
        for (MonitorLog monitorLog : monitorLogList) {
            //初始化这些异常时间的集合,设置好map中的key
            initDisconnectTimeCollection(monitorLog,clientErrorTime,terminalErrorTime);
            //如果发现了有状态为断开的日志
            if (monitorLog.getStatus().equals("-1")) {
                //在异常设备集合中没有,把信息加入到异常的设备集合中
                if (errorTerminal.get(getTerminalKey(monitorLog)) == null)
                    errorTerminal.put(getTerminalKey(monitorLog), monitorLog);
                continue;
            }
            //否则进行恢复判定,如果在异常的设备集合中有断开记录的恶化
            totalDisconnectTime(errorTerminal, clientErrorTime, terminalErrorTime, monitorLog);
        }
        //如果最后还有没被移除掉的,也就是统计的时候还未恢复的,按当前时间来计算断开的时间总数.
        for (Map.Entry<String, MonitorLog> entry : errorTerminal.entrySet()) {
            MonitorLog monitorLog = entry.getValue();
            total(clientErrorTime, terminalErrorTime, monitorLog.getClientIp(), getTerminalKey(monitorLog), timeDifference(startDate, new Date()));
            errorTerminal.remove(getTerminalKey(monitorLog));
        }
        return new DisconnectTimeBean(clientErrorTime, terminalErrorTime);
    }

    /**
     * 获取MonitorLog中的terminal的命名,规则为 clientIp_terminalIp
     *
     * @param monitorLog
     * @return
     */
    private String getTerminalKey(MonitorLog monitorLog) {
        return monitorLog.getClientIp() + "_" + monitorLog.getTerminalIp();
    }

    private String getTerminalKey(String clientIp, String terminalIp) {
        return clientIp + "_" + terminalIp;
    }

    /**
     * 统计异常时间
     *
     * @param errorTerminal
     * @param clientErrorTime
     * @param terminalErrorTime
     * @param monitorLog
     */
    private void totalDisconnectTime(Map<String, MonitorLog> errorTerminal, Map<String, BigDecimal> clientErrorTime, Map<String, BigDecimal> terminalErrorTime, MonitorLog monitorLog) {
        if (errorTerminal.get(getTerminalKey(monitorLog)) != null) {
            Date errorStartTime = errorTerminal.get(getTerminalKey(monitorLog)).getCreateTime();
            Date errorEndTime = monitorLog.getCreateTime();
            total(clientErrorTime, terminalErrorTime, monitorLog.getClientIp(), getTerminalKey(monitorLog), timeDifference(errorStartTime, errorEndTime));
            errorTerminal.remove(getTerminalKey(monitorLog));
        }
    }

    /**
     * 计算2个时间的时间差,如果小于了5分钟,设置时间差为0
     *
     * @param errorStartTime
     * @param errorEndTime
     * @return
     */
    private BigDecimal timeDifference(Date errorStartTime, Date errorEndTime) {
        //如果不大于5分钟算0分钟
        long diffTime = DateTool.create().diffMinute(errorStartTime, errorEndTime) < ignore_time_difference ?
                0L : DateTool.create().diffMinute(errorStartTime, errorEndTime);
        return new BigDecimal(diffTime);
    }

    /**
     * 合计门店的异常时间 和 合计终端的异常时间
     *
     * @param clientDisconnectCollection
     * @param terminalDisconnectCollection
     * @param clientIp
     * @param terminalKey
     * @param timeDifference
     */
    private void total(Map<String, BigDecimal> clientDisconnectCollection, Map<String, BigDecimal> terminalDisconnectCollection, String clientIp, String terminalKey, BigDecimal timeDifference) {
        //合计门店的异常时间
        clientDisconnectCollection.put(clientIp, BigDecimalCount.add(clientDisconnectCollection.get(clientIp), timeDifference));
        //合计终端的异常时间
        terminalDisconnectCollection.put(terminalKey, BigDecimalCount.add(terminalDisconnectCollection.get(terminalKey), timeDifference));
    }

    @Data
    class DisconnectTimeBean {
        Map<String, BigDecimal> clientErrorTime;
        Map<String, BigDecimal> terminalErrorTime;

        public DisconnectTimeBean() {
        }

        public DisconnectTimeBean(Map<String, BigDecimal> clientErrorTime, Map<String, BigDecimal> terminalErrorTime) {
            this.clientErrorTime = clientErrorTime;
            this.terminalErrorTime = terminalErrorTime;
        }

    }
}
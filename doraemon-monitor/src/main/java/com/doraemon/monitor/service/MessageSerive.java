package com.doraemon.monitor.service;

import com.doraemon.monitor.controller.protocol.MessagePro;
import com.doraemon.monitor.controller.protocol.PagePro;
import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.MonitorLogMapper;
import com.doraemon.monitor.dao.mapper.TerminalMapper;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.MonitorLog;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.dao.models.TerminalKey;
import com.doraemon.monitor.util.Common;
import com.doraemon.monitor.util.HttpAgent;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zbs on 2017/7/4.
 */
@Service
@Slf4j
public class MessageSerive {

    @Autowired
    ClientMapper clientMapper;
    @Autowired
    TerminalMapper terminalMapper;
    @Autowired
    MonitorLogMapper monitorLogMapper;

    /**
     * 增加日志
     *
     * @param message
     * @param ip
     */
    public void add(List<MessagePro> message, String ip) throws Exception {
        //todo:参数校验没写
        Client client = clientMapper.selectByPrimaryKey(ip);
        Preconditions.checkState(client != null, "该IP没有注册.IP->>"+ip);
        for (MessagePro messagePro : message) {
            validMessage(messagePro, ip,client);
            saveMessage(messagePro, ip);
        }
    }


    /**
     * 校验是否异常
     * @param messagePro
     * @param ip
     */
    private void validMessage(MessagePro messagePro, String ip,Client client) throws Exception {
        if(messagePro == null || ip == null)
            return;
        Terminal selectTerminal = new Terminal();
        selectTerminal.setClientIp(ip);
        selectTerminal.setTerminalIp(messagePro.getIp());
        Terminal terminal = terminalMapper.selectByClientIpAndTerminalIp(selectTerminal);
        if(terminal == null)
            return;
        //如果状态不相等,更新状态
        if(!String.valueOf(messagePro.getStatus()).equals(terminal.getStatus())){
            Terminal updateStatus = new Terminal();
            updateStatus.setClientIp(ip);
            updateStatus.setTerminalIp(messagePro.getIp());
            updateStatus.setStatus(String.valueOf(messagePro.getStatus()));
            terminalMapper.updateByPrimaryKeySelective(updateStatus);
        }
        TerminalKey updateTerminalKey = new TerminalKey();
        updateTerminalKey.setTerminalIp(messagePro.getIp());
        updateTerminalKey.setClientIp(ip);
        switch (messagePro.getStatus()){
            case "-1":
                //第一次断开更新断开时间
                if(terminal.getOffTime() == null) {
                    Terminal updateTerminal = new Terminal(updateTerminalKey);
                    updateTerminal.setOffTime(messagePro.getTime());
                    terminalMapper.disconnect(updateTerminal);
                }else{
                    //如果告警次数没有超过3次,进行告警,并增加1次告警记录
                    if(terminal.getWarningNum()<=1){
                        terminalMapper.warning(updateTerminalKey);
                        String shopId = client.getShopId();
                        String msgType = Common.SMS_TYPE;
                        String phone = terminal.getPhone();
                        String data = "测试告警短信";
                        //todo:怎么告警呢?
                        String param = "shopId="+shopId+"&msgType="+msgType+"&phone="+phone+"&data="+data;
                        log.info("调用短信接口进行告警:",Common.SMS_URL+"?"+param);
                        String result =  HttpAgent.create().sendPost(Common.SMS_URL,param);
                        log.info("短信接口返回数据:"+result);
                    }
                }
                break;
            default:
                if(terminal.getOffTime() != null)
                    terminalMapper.recovery(updateTerminalKey);
                break;
        }
    }



    /**
     * 保存日志
     *
     * @param messagePro
     * @param ip
     */
    private void saveMessage(MessagePro messagePro,String ip) {
        MonitorLog monitorLog = new MonitorLog();
        monitorLog.setCreateTime(messagePro.getTime());
        monitorLog.setStatus(messagePro.getStatus());
        monitorLog.setTerminalIp(messagePro.getIp());
        monitorLog.setClientIp(ip);
        monitorLogMapper.insert(monitorLog);
    }

    /**
     * 查询全部
     *
     * @return
     */
    public PageInfo<MonitorLog> queryAll(PagePro pagePro) {
        PageHelper.startPage(pagePro.getPage(), pagePro.getRow(),"create_time DESC");
        return new PageInfo<>(monitorLogMapper.selectAll());
    }

    /**
     * 根据客户端IP查询日志
     *
     * @param ip
     * @return
     */
    public PageInfo<MonitorLog> queryByIp(String ip,PagePro pagePro) {
        PageHelper.startPage(pagePro.getPage(), pagePro.getRow(),"create_time DESC");
        MonitorLog monitorLog = new MonitorLog();
        monitorLog.setClientIp(ip);
        return new PageInfo<>(monitorLogMapper.select(monitorLog));
    }

    /**
     * 按时间进行查找
     * @param clientIp
     * @param startDate
     * @param stopDate
     * @return
     */
    public List<MonitorLog> queryByIpAndTime(String clientIp, Date startDate, Date stopDate){
        Map<String,Object> map = new HashMap<>();
        map.put("stopDate",stopDate);
        map.put("startDate",startDate);
        if(clientIp != null && !clientIp.equals("")) {
            map.put("clientIp", clientIp);
            return monitorLogMapper.selectByDate(map);
        }
        return monitorLogMapper.selectByDateAll(map);
    }

}

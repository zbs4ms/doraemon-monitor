package com.doraemon.monitor.service;

import com.doraemon.monitor.controller.base.Paging;
import com.doraemon.monitor.controller.protocol.SubIpsPro;
import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.TerminalMapper;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.util.Helpers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zbs on 2017/7/4.
 */
@Service
@Log4j
public class ConfigService {

    @Autowired
    ClientMapper clientMapper;
    @Autowired
    TerminalMapper terminalMapper;

    public List<String> queryAllRegion(){
         return clientMapper.queryAllRegion();
    }

    /**
     * 根据IP查询client,没ip代表查询全部
     *
     * @param ip
     * @return
     */
    public List<Client> queryClient(String ip, String region) {
        if(ip == null)
            return null;
        Map<String,Object> queryClient = new HashMap<>();
        queryClient.put("ip",ip);
        queryClient.put("regions",region == null || "".equals(region) ? null : region.split(","));
        List<Client> clientList = clientMapper.selectByIdAndRegion(queryClient);
        setTerminal(clientList);
        return clientList.size()==0 ? null : clientList;
    }

    /**
     * 对client中的terminal赋值
     * @param clientList
     */
    private void setTerminal(List<Client> clientList) {
        if (clientList == null)
            return;
        for (Client client : clientList)
            client.setTerminalList(queryTerminal(client.getIp()));
    }

    /**
     * 查询终端--分页
     * @param ip
     * @param region
     * @param paging
     * @return
     */
    public PageInfo<Client> queryClientPageInfo(String ip, String region,Paging paging){
        log.info("查询终端. ip:"+ip+" region:"+region);
        if(!Helpers.isNullOrEmpty(paging))
            PageHelper.startPage(paging.getPageSize(),paging.getPageNum(),paging.getOrderBy());
        return new PageInfo<>(queryClient(ip,region));
    }

    /**
     * 根据ip查Terminal
     *
     * @param ip
     * @return
     */
    private List<Terminal> queryTerminal(String ip) {
        //by csrr
        List<Terminal> list = terminalMapper.selectByClientIp(ip);
        return list;
    }

    /**
     * 添加监控列表
     *
     * @param subIps
     * @param ip
     * @param nick
     */
    @Transactional
    public void add(List<SubIpsPro> subIps, String ip, String nick, String region, String shopId) throws Exception {
        Preconditions.checkState(subIps != null && subIps.size() > 0, "子IP列表不能为空.");
        Preconditions.checkState(ip != null && !ip.equals(""), "客户端IP不能为空.");
        Preconditions.checkState(nick != null && !nick.equals(""), "昵称不能为空.");
        //查询 client 是否存在
        Preconditions.checkState(queryClient(ip,null) == null, "该ip已经存在,请勿重复添加.");
        //添加client
        Client newClient = new Client();
        newClient.setIp(ip);
        newClient.setNick(nick);
        newClient.setRegion(region);
        newClient.setShopId(shopId);
        //保存 client
        Preconditions.checkState(clientMapper.insert(newClient) == 1, "保存client失败.");
        for (SubIpsPro subIpsPro : subIps) {
            Terminal newTerminal = new Terminal();
            newTerminal.setNick(subIpsPro.getNick());
            newTerminal.setTerminalIp(subIpsPro.getIp());
            newTerminal.setDeviceType(getType(subIpsPro.getIp()));
            newTerminal.setClientIp(newClient.getIp());
            newTerminal.setPhone(subIpsPro.getPhone());
            //保存 terminal
            Preconditions.checkState(terminalMapper.insert(newTerminal) == 1, "保存terminal失败.");
        }
    }

    private String getType(String ip) {
        if(ip == null)
            return null;
        String[] ips = ip.split(".");
        if(ips == null || ips.length != 4)
            return null;
        switch (ips[3]){
            case "1":
                return "WAN";
            default:
                return "LAN";
        }
    }

    /**
     * 删除
     *
     * @param subIps
     * @param ip
     */
    public void delete(List<String> subIps, String ip) {
        Client deleteClient = new Client();
        Terminal deleteTerminal = new Terminal();
        if (subIps == null || subIps.size() < 1) {
            //删除ip的全部
            deleteClient.setIp(ip);
            clientMapper.delete(deleteClient);
            deleteTerminal.setClientIp(ip);
            terminalMapper.delete(deleteTerminal);
        } else {
            //删除ip下的某些subIp
            for (String subIp : subIps) {
                deleteTerminal.setTerminalIp(subIp);
                terminalMapper.delete(deleteTerminal);
            }
        }
    }

    /**
     * 更新列表
     *
     * @param subIps
     * @param ip
     * @param nick
     */
    @Transactional
    public void update(Map<String, String> subIps, String ip, String nick) {
        Preconditions.checkState(ip != null && !ip.equals(""), "客户端IP不能为空.");
        Preconditions.checkState((nick != null && !nick.equals("")) || (subIps != null && subIps.size() > 0), "昵称和子IP列表不能同时为空.");
        Client updateClient = new Client();
        updateClient.setIp(ip);
        //更新client的昵称
        if (nick != null && !nick.equals("")) {
            updateClient.setNick(nick);
            clientMapper.updateByPrimaryKey(updateClient);
        }
        //如果没有其他更新就返回
        if (subIps == null || subIps.equals(""))
            return;
        //如果需要更新子IP信息,就开始更新
        for (Map.Entry<String, String> entry : subIps.entrySet()) {
            Terminal terminal = new Terminal();
            terminal.setClientIp(ip);
            terminal.setTerminalIp(entry.getKey());
            terminal.setNick(entry.getValue());
            Terminal selectTerminal = terminalMapper.selectOne(terminal);
            //查询 terminal 是否存在,存在修改,否则新增
            if (selectTerminal == null) {
                terminalMapper.insert(terminal);
            } else {
                terminalMapper.updateByPrimaryKeySelective(terminal);
            }
        }

    }

}

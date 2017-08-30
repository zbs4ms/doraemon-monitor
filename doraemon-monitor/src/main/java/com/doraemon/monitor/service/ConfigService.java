package com.doraemon.monitor.service;

import com.doraemon.monitor.controller.base.Paging;
import com.doraemon.monitor.controller.protocol.SubIpsPro;
import com.doraemon.monitor.dao.mapper.ClientMapper;
import com.doraemon.monitor.dao.mapper.TerminalMapper;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.dao.models.TerminalKey;
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
            saveTerminal(subIpsPro,newClient.getIp());
        }
    }

    public void saveTerminal(SubIpsPro subIpsPro,String clientIp) throws Exception {
        Terminal newTerminal = new Terminal();
        newTerminal.setNick(subIpsPro.getNick());
        newTerminal.setTerminalIp(subIpsPro.getIp());
        newTerminal.setDeviceType(getType(subIpsPro.getIp()));
        newTerminal.setClientIp(clientIp);
        newTerminal.setPhone(subIpsPro.getPhone());
        //保存 terminal
        Preconditions.checkState(terminalMapper.insert(newTerminal) == 1, "保存terminal失败.");
    }

    private String getType(String ip) throws Exception {
        if(ip == null || ip.split("[.]").length!=4)
            throw new Exception("无效的IP ip="+ip);
        String[] ips = ip.split("[.]");
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
    public void update(List<SubIpsPro> subIps, String ip, String nick) throws Exception {
        Preconditions.checkState(ip != null && !ip.equals(""), "客户端IP不能为空.");
        Preconditions.checkState((nick != null && !nick.equals("")) || (subIps != null && subIps.size() > 0), "昵称和子IP列表不能同时为空.");
        //更新client
        Client selectClient = clientMapper.selectByPrimaryKey(ip);
        if(selectClient == null || "".equals(selectClient.getIp()))
             throw new Exception("该客户端不存在 + ip:"+ip);
        selectClient.setNick(nick);
        if(nick != null && !"".equals(nick) && !nick.equals(selectClient.getNick())) {
            Preconditions.checkState(clientMapper.updateByPrimaryKeySelective(selectClient) == 1, "更新客户端昵称失败!");
        }
        //更新terminal
        if(subIps == null || subIps.size() < 1)
            return;
        for(SubIpsPro subIpsPro : subIps){
            TerminalKey terminalKey = new TerminalKey(ip,subIpsPro.getIp());
            Terminal oldTerminal = terminalMapper.selectByPrimaryKey(terminalKey);
            if(oldTerminal == null || oldTerminal.getTerminalIp() == null){
                saveTerminal(subIpsPro,selectClient.getIp());
            } else {
                oldTerminal.setNick(subIpsPro.getNick());
                oldTerminal.setPhone(subIpsPro.getPhone());
                terminalMapper.updateByPrimaryKeySelective(oldTerminal);
            }

        }
    }

}

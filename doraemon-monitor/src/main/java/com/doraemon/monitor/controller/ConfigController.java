package com.doraemon.monitor.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doraemon.monitor.controller.base.BaseController;
import com.doraemon.monitor.controller.protocol.SubIpsPro;
import com.doraemon.monitor.controller.protocol.TerminalPro;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.Terminal;
import com.doraemon.monitor.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.list.TransformedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 配置相关接口
 * Created by zbs on 2017/7/4.
 */
@RestController
@RequestMapping("/config")
@Slf4j
@Api(description = "配置相关接口")
public class ConfigController extends BaseController {

    @Autowired
    ConfigService configService;

    @ApiOperation(value = "增加配置")
    @RequestMapping(value = "addConfig", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject add(@ApiParam(value = "子IP列表[{'ip':'**';'nick':'**';'phone';'****'},{'ip':'**';'nick':'**';'phone';'****'}]", required = true) @RequestParam(value = "subIps", required = true) String subIps,
                          @ApiParam(value = "客户端IP", required = true) @RequestParam(value = "ip", required = true) String ip,
                          @ApiParam(value = "客户端别名", required = true) @RequestParam(value = "nick", required = true) String nick,
                          @ApiParam(value = "客户端区域", required = true) @RequestParam(value = "region", required = true) String region,
                          @ApiParam(value = "客户端门店ID", required = true) @RequestParam(value = "shopId", required = true) String shopId) throws Exception {

        List<SubIpsPro> subIpsList = JSONArray.parseArray(subIps,SubIpsPro.class);
        configService.add(subIpsList,ip,nick,region,shopId);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "修改配置(只能修改子IP列表 和 别名)")
    @RequestMapping(value = "updateConfig", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject update(@ApiParam(value = "子IP列表", required = false) @RequestParam(value = "subIps", required = false) Map<String, String> subIps,
                             @ApiParam(value = "客户端IP", required = true) @RequestParam(value = "ip", required = true) String ip,
                             @ApiParam(value = "客户端别名", required = false) @RequestParam(value = "nick", required = false) String nick) throws Exception {
        configService.update(subIps,ip,nick);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "删除组配置")
    @RequestMapping(value = "deleteConfig", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject deleteConfig(@ApiParam(value = "子IP列表",required = true) @RequestParam(value = "subIps",required = true) List<String> subIps,
                                   @ApiParam(value = "客户端IP",required = true) @RequestParam(value = "ip",required = true) String ip) throws Exception {
        configService.delete(subIps,ip);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "查询全部的地区配置")
    @RequestMapping(value = "queryAllRegion", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllRegion() throws Exception {
        List<String> regionList = configService.queryAllRegion();
        return ResponseWrapper().addData(regionList).ExeSuccess();
    }

    @ApiOperation(value = "查询配置(什么都不传入默认查询全/....部)")
    @RequestMapping(value = "queryConfig", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryConfig(
            @ApiParam(value = "客户端IP",required = false) @RequestParam(value = "ip",required = false) String ip) throws Exception {
        List<Client> clientList = configService.queryClient(ip,null);
        return ResponseWrapper().addData(clientList).ExeSuccess();
    }

    @ApiOperation(value = "查询终端(什么都不传入默认查询全/....部)")
    @RequestMapping(value = "queryTerminalList", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryTerminalList(
            @ApiParam(value = "客户端IP",required = false) @RequestParam(value = "ip",required = false) String ip,
            @ApiParam(value = "客户端区域",required = false) @RequestParam(value = "region",required = false) String region) throws Exception {
        List<Client> clientList = configService.queryClient(ip,region);
<<<<<<< HEAD
        log.info("查询出的终端原始信息+"+ JSON.toJSONString(clientList));
        if(clientList == null)
            return ResponseWrapper().addData(null).ExeSuccess();
        List<TerminalPro> terminalProList = new ArrayList<>();
=======
        List<TerminalPro> terminalProList = new ArrayList<>();
        log.info("查询出的终端原始信息+"+ JSON.toJSONString(terminalProList));

>>>>>>> 9d0173691914b40a8dc75846201150459fb5972b
        for(Client client : clientList){
            for(Terminal terminal : client.getTerminalList()){
                TerminalPro terminalPro = new TerminalPro(client,terminal);
                terminalProList.add(terminalPro);
            }
        }
<<<<<<< HEAD
//
//        if(terminalProList.size() == 0){
//            log.info("列表为空，构造数据...");
//            Client client = new Client();
//            Terminal terminal = new Terminal();
//            TerminalPro terminalPro = new TerminalPro(client,terminal);
//            terminalProList.add(terminalPro);
//        }else {
//            log.info("列表长度："+terminalProList.size());
//        }
=======

        if(terminalProList.size() == 0 || clientList == null){
            log.info("列表为空，构造数据...");
            Client client = new Client();
            Terminal terminal = new Terminal();
            TerminalPro terminalPro = new TerminalPro(client,terminal);
            terminalProList.add(terminalPro);
        }else {
            log.info("列表长度："+terminalProList.size());
        }
>>>>>>> 9d0173691914b40a8dc75846201150459fb5972b
        return ResponseWrapper().addData(terminalProList).ExeSuccess();
    }
}

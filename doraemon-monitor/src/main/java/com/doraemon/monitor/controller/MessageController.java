package com.doraemon.monitor.controller;

import com.alibaba.fastjson.JSONObject;
import com.doraemon.monitor.controller.base.BaseController;
import com.doraemon.monitor.controller.protocol.MessagePro;
import com.doraemon.monitor.controller.protocol.PagePro;
import com.doraemon.monitor.dao.models.Client;
import com.doraemon.monitor.dao.models.MonitorLog;
import com.doraemon.monitor.service.CountService;
import com.doraemon.monitor.service.MessageSerive;
import com.doraemon.monitor.util.DateTool;
import com.github.pagehelper.PageInfo;
import com.us.base.util.tool.IpTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 消息报文包
 * Created by zbs on 2017/7/4.
 */
@RestController
@RequestMapping("/message")
@Slf4j
@Api(description = "消息报文包接口")
public class MessageController extends BaseController {

    @Autowired
    MessageSerive messageSerive;
    @Autowired
    CountService countService;

    @ApiOperation(value = "传入报文")
    @RequestMapping(value = "addMessage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addMessage(@ApiParam(value = "子IP情况 map的key为子IP", required = true) @RequestParam(value = "message", required = true) String message,HttpServletRequest request) throws Exception {
        //获取请求者的IP
        String ip = IpTool.getIp(request);
        List<MessagePro> messageProList = JSONObject.parseArray(message,MessagePro.class);
        messageSerive.add(messageProList,ip);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "查询报文(什么都不传入默认查询全部)")
    @RequestMapping(value = "queryMessage", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryMessage(@ApiParam(value = "客户端IP",required = false) @RequestParam(value = "ip",required = false) String ip,
                                   @ApiParam(value = "页数",required = false) @RequestParam(value = "page",required = false) Integer page,
                                   @ApiParam(value = "多少条",required = false) @RequestParam(value = "row",required = false) Integer row) throws Exception {
        PageInfo<MonitorLog> monitorPage= null;

        if(ip != null) {
            monitorPage = messageSerive.queryByIp(ip,PagePro.create(page,row));
        }else{
            monitorPage = messageSerive.queryAll(PagePro.create(page,row));
        }
        return ResponseWrapper().addData(monitorPage).ExeSuccess();
    }

    @ApiOperation(value = "查询设备可用性(什么都不传入默认查询全部)")
    @RequestMapping(value = "queryUsability", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryMessage(@ApiParam(value = "客户端IP",required = false) @RequestParam(value = "ip",required = false) String ip,
                                   @ApiParam(value = "时间类型(Y/M/W/D)",required = true) @RequestParam(value = "dateType",required = true) String dateType,
                                   @ApiParam(value = "页数",required = false) @RequestParam(value = "page",required = false) Integer page,
                                   @ApiParam(value = "多少条",required = false) @RequestParam(value = "row",required = false) Integer row) throws Exception {
        DateTool.DateBean dateBean = null;
        switch (dateType){
            case "Y":
                dateBean = DateTool.create().getLastYear();
                break;
            case "M":
                dateBean = DateTool.create().getLastMonth();
                break;
            case "W":
                dateBean = DateTool.create().getLastWeek();
                break;
            case "D":
                dateBean = DateTool.create().getLastDay();
                break;
            default:
                throw new Exception("错误的类型");
        }
        PageInfo<Client> monitorPage = countService.totalClientErrorTime(ip,dateBean.getStartDate(),dateBean.getStopDate(),PagePro.create(page,row));
        return ResponseWrapper().addData(monitorPage).ExeSuccess();
    }
}

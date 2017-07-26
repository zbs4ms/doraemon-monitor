package com.doraemon.monitor.controller.base;

import com.alibaba.fastjson.JSONObject;
import com.doraemon.monitor.util.ControllerResult;
import com.google.common.base.Preconditions;
import com.us.base.util.Common;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by zbs on 16/7/1.
 */
public class BaseController {

    /**
     * 取得Json结果包装器(带日志记录的),用于构json格式的统一消息返回包装.
     *
     * @return
     */
    protected ControllerResult ResponseWrapper() {
        return new ControllerResult(getCurrentRequest(), this.getClass());
    }


    @ExceptionHandler
    public
    @ResponseBody
    JSONObject exceptionHandle(Exception e) {
        if (e == null) {
            return ResponseWrapper().addMessage("系统错误!").ExeFaild();
        } else if (e.getMessage() == null||e.getMessage().length()>30) {
            return ResponseWrapper().addError(e).addMessage("系统错误!").ExeFaild();
        } else {
            return ResponseWrapper().addError(e).addMessage(e.getMessage()).ExeFaild();
        }
    }

    protected Long getCurrentUserId() {
        //TODO 如果不能直接取道用户ID,尝试取token之后再去redis中查询
        return Long.valueOf(String.valueOf(Preconditions.checkNotNull(getCurrentRequest().getAttribute(Common.USER_ID), "没有查到用户ID")));
    }

    protected String getToken() {
        return String.valueOf(Preconditions.checkNotNull(getCurrentRequest().getAttribute(Common.TOKEN), "没有查到用户的token"));
    }

    protected HttpServletRequest getCurrentRequest(){
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        return request;
    }

    /**
     * 返回header里面的城市名
     * @return
     */
    protected String getCurrentCity(){

        return getCurrentRequest().getHeader(Common.REQUEST_HEADER_CITY);
    }

    /**
     * 返回当前域名
     * @return
     */
    protected String getServerName(){
        HttpServletRequest request = getCurrentRequest();
        return request.getScheme()+"://"+request.getServerName();
    }

    /**
     * 返回全路径名
     * @return
     */
    protected String getServicePoth(){
        HttpServletRequest request = getCurrentRequest();
        return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
    }
}

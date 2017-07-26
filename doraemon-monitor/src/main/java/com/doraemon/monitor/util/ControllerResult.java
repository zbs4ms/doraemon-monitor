package com.doraemon.monitor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.us.base.util.Common;
import com.us.base.util.tool.IpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wang on 16/8/30.
 */
public class ControllerResult extends Result {
    private Logger logger = null;
    private String userId = null;
    private String userIp = null;

    public ControllerResult() {
    }

    public ControllerResult(HttpServletRequest request, Class clazz) {
        if (request == null || clazz == null) {
            return;
        }
        this.logger = LoggerFactory.getLogger(clazz);

        this.userId = request.getHeader(Common.USER_ID);
        this.userIp = IpTool.getIp(request);
        this.logger.info("User Id:" + userId + " IP:" + userIp + " params: " + getRequestParams(request));
    }

    private String getRequestParams(HttpServletRequest request) {
        JSONObject json = (JSONObject) JSON.toJSON(request.getParameterMap());
        return json.toJSONString();
    }

    /**
     * 填充log日志 info
     */
    public Result addMessage(String message) {
        if (logger != null) {
            super.addMessage(message);
        } else {
            logger.info(userId, message);
        }
        return this;
    }

    /**
     * 填充log日志 error
     */
    public Result addError(Exception e) {

        if (logger == null) {
            super.addMessage(e.getMessage());
        } else {
            logger.error(userId, e);
        }
        return this;
    }


    /**
     * 填充log日志 debug
     */
    public Result addDebug(String message) {
        if (logger != null) {
            logger.debug(userId, message);
        }
        return this;
    }


}

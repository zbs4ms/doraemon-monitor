package com.doraemon.monitor.client.controller.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 监控报文格式
 * Created by zbs on 2017/7/4.
 */
@ApiModel(value = "监控报文格式")
@Data
public class MessagePro {
    @ApiModelProperty(value="终端IP")
    String ip;
    @ApiModelProperty(value = "状态")
    String status;
    @ApiModelProperty(value = "时间")
    Date time;
    @ApiModelProperty(value = "设备类型")
    String deviceType;
}

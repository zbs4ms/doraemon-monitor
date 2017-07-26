package com.doraemon.monitor.controller.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zbs on 2017/7/14.
 */
@Data
@ApiModel(value = "设备信息")
public class SubIpsPro {
    @ApiModelProperty(value = "设备IP")
    String ip;
    @ApiModelProperty(value = "设备昵称")
    String nick;
    @ApiModelProperty(value = "设备类型(WAN/LAN/AP)")
    String type;
    @ApiModelProperty(value = "店铺手机号码")
    String phone;
}

package com.doraemon.monitor.dao.config;

import com.us.base.mybatis.SbmDataConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * -- 必须存在 --  封装的数据库配置读取
 * Created by zbs on 16/7/1.
 */
@Configuration
@EnableTransactionManagement
public class DataConfig extends SbmDataConfig {}

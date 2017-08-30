package com.doraemon.monitor.client;

import com.doraemon.monitor.client.Service.UpdateConfigService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zbs on 2017/7/6.
 */
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@Log4j
public class Main implements CommandLineRunner {

    @Autowired
    UpdateConfigService updateConfigService;
    public static String LOCAL_IP = null;


    public static void main(String[] args) throws Exception {
        //todo: 测试用
        args[0]="z001";
        if(args == null || args.length<1 ||args[0].equals(""))
            throw new Exception("必须指定本机的标识是.");
        log.info("本机标标识为 ---> "+args[0]+"  长度:"+args.length);
        LOCAL_IP = args[0];
        SpringApplication app = new SpringApplication(Main.class);
        app.run();
    }


    @Override
    public void run(String... strings) throws Exception {
        updateConfigService.update();
        System.out.println("服务启动完毕.");
    }

    @Bean
    public ConcurrentLinkedQueue<List> concurrentLinkedQueue() {
        return new ConcurrentLinkedQueue();
    }
}

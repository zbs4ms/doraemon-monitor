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

    public static void main(String[] args) throws Exception {
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

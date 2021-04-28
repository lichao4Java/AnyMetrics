package org.anymetrics.boot;

import org.anymetrics.core.task.PipelineTaskManage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
}

@Component
class StartTaskRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        PipelineTaskManage.start();
    }
}
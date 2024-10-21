package org.c4marathon.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
public class AsyncConfig {

    @Bean
    public Executor customAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10); // 큐 크기를 넘어가면 최대 10개의 스레드를 생성해서 처리한다.
        executor.setQueueCapacity(100); //최초 5개의 쓰레드에서 처리하다가, 속도가 밀릴 경우 100 크기의 큐에서 대기한다.
        executor.setThreadNamePrefix("mail-thread");
        executor.initialize();
        return executor;
    }
}

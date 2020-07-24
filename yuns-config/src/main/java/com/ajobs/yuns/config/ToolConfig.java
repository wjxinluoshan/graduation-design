package com.ajobs.yuns.config;

import com.ajobs.yuns.tool.RSAKeyPairGenerator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

  @Bean("RSAKeyGenerator")
  public RSAKeyPairGenerator rsaKeyPairGenerator() {
    return new RSAKeyPairGenerator();
  }

  @Bean
  public ExecutorService createFixedThreadPool() {
    return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
       new Double(Runtime.getRuntime().availableProcessors() / 0.1).intValue(),
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(1_000_000),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

}

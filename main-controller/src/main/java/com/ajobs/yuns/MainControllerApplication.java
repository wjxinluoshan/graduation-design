package com.ajobs.yuns;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 外部tomcat
 */
//@SpringBootApplication
//public class YunsApplication extends SpringBootServletInitializer {
//
//  @Override
//  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//    return application.sources(YunsApplication.class);
//  }
//
//  public static void main(String[] args) {
//    SpringApplication.run(YunsApplication.class, args);
//  }
//
//}

/**
 * spring boot
 */
@SpringBootApplication
public class MainControllerApplication {
  public static void main(String[] args) {
    SpringApplication.run(MainControllerApplication.class, args);
  }
}


package com.ajobs.yuns.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DisPatcherServletConfig implements WebMvcConfigurer {


  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    int i=0;
    registry.jsp("/WEB-INF/classes/static/jsp/", ".jsp");
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(500);
    threadPoolTaskExecutor.initialize();
    configurer.setTaskExecutor(threadPoolTaskExecutor);
  }

  /**
   * 处理异常不会出现错误信息，不介意 httpstatus=500 使用
   */
//  @Override
//  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
//    resolvers.add((httpServletRequest, httpServletResponse, o, e) -> {
//          ModelAndView modelAndView = new ModelAndView();
//          modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
//          modelAndView.setViewName("500");
//          return modelAndView;
//        }
//    );
//  }
}

package org.graduate.configuration;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({
        // 在这里注册所有需要属性绑定的配置类
        BaiduLogoConfiguration.class})
public class ManAgentConfiguration {
}

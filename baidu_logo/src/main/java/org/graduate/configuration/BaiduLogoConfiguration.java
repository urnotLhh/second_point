package org.graduate.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("baidu.logo")
@ComponentScan("org.graduate.server")
public class BaiduLogoConfiguration {
}

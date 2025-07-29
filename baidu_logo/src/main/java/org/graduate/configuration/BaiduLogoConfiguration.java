package org.graduate.configuration;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("baidu.logo")
@Data
public class BaiduLogoConfiguration {
    public String appkey;
    public String secret_key;
}

package org.graduate.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
//@Import({BaiduRagAgentConfiguration.class})
@ComponentScan(value = {"org.graduate"})
public class ManAgentConfiguration {
}

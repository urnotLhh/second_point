package org.graduate.configuration;

import org.graduate.server.imple.RagAgentServerImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BaiduRagAgentProperites.class)
@ComponentScan(value = {"org.graduate"})
public class BaiduRagAgentConfiguration {
    @Bean
    RagAgentServerImpl getRagAgentServer(BaiduRagAgentProperites baiduRagAgentProperites) {
        return new RagAgentServerImpl(baiduRagAgentProperites);
    }
}

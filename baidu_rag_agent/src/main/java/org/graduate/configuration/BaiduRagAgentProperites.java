package org.graduate.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("baidu.rag.agent")
@Data
public class BaiduRagAgentProperites {
    private String appId;
    private String secret_key;
}

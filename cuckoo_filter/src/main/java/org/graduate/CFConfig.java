package org.graduate;

import io.rebloom.client.cf.CFReserveOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.bloom.CFReserveParams;

@Configuration
public class CFConfig {
    @Bean("defaultCFReserveParams")
    public CFReserveParams defaultParam(){
        // 创建参数配置对象
        CFReserveParams params = new CFReserveParams()
                .bucketSize(4)         // 每个桶中可存放的指纹数（默认4）
                .maxIterations(20)     // 插入时的最大重试次数（默认20）
                .expansion(2);         // 扩容时的增长因子（默认1=禁止扩容）

        return params;
    }

    @Bean("defaultCFReserveOptions")
    public CFReserveOptions cfReserveOptions(){
        CFReserveOptions params = CFReserveOptions.builder()
                .withBucketSize(2)
                .withMaxIterations(20)
                .withExpansion(1).build();
        return params;

    }
}

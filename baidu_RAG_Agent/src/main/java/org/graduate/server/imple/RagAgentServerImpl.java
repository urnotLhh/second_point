package org.graduate.server.imple;

import com.baidubce.appbuilder.console.appbuilderclient.AppBuilderClient;
import com.baidubce.appbuilder.model.appbuilderclient.AppBuilderClientIterator;
import com.baidubce.appbuilder.model.appbuilderclient.AppBuilderClientResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graduate.configuration.BaiduRagAgentProperites;
import org.graduate.server.RagAgentServer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Data
@Component
@AllArgsConstructor
@Slf4j
public class RagAgentServerImpl implements RagAgentServer {
    BaiduRagAgentProperites baiduRagAgentProperites;

    @Override
    public String getAnswerFromRagAgent(String query) {
        // 设置环境中的TOKEN，以下TOKEN请替换为您的个人TOKEN，个人TOKEN可通过该页面【获取鉴权参数】或控制台页【密钥管理】处获取

        System.setProperty("APPBUILDER_TOKEN", baiduRagAgentProperites.getSecret_key());
        // 从AppBuilder控制台【个人空间】-【应用】网页获取已发布应用的ID
        String appId = baiduRagAgentProperites.getAppId();
        AppBuilderClient builder = new AppBuilderClient(appId);
        String conversationId = null;
        AppBuilderClientIterator itor = null;
        try {
            conversationId = builder.createConversation();
            itor = builder.run(query, conversationId, new String[] {}, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (itor == null) {
            log.error("getAnswerFromRagAgent error");
        }
        StringBuilder answer = new StringBuilder();
        int count = 0;
        while (itor.hasNext()) {
            System.out.println("count:" + count++);
            AppBuilderClientResult response = itor.next();
            answer.append(response.getAnswer());
        }
        System.out.println(answer);
        return answer.toString();
    }
}

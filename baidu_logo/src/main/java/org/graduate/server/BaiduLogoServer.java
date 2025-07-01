package org.graduate.server;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.concurrent.TimeUnit;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.net.URLEncoder;


/**
 * 需要添加依赖
 * <!-- https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp -->
 * <dependency>
 *     <groupId>com.squareup.okhttp3</groupId>
 *     <artifactId>okhttp</artifactId>
 *     <version>4.12.0</version>
 * </dependency>
 */

public class BaiduLogoServer{
    public static final String API_KEY = "p824bbFqXp4pEKTAPap3bIt4";
    public static final String SECRET_KEY = "ftsDDIdhAzVPGC63yWuN6iWZh5ktXqOV";

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

    public void logo(String filePath) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        // image 可以通过 getFileContentAsBase64("C:\fakepath\pic3.png") 方法获取,如果Content-Type是application/x-www-form-urlencoded时,第二个参数传true
        // 尝试直接作为文件路径
        String absoluteFilePath = new File(filePath).getAbsolutePath();
        System.out.println(absoluteFilePath);

        RequestBody body = RequestBody.create(mediaType, String.format("image=%s&custom_lib=false", getFileContentAsBase64(absoluteFilePath, true)));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/image-classify/v2/logo?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());

    }

    /**
     * 获取文件base64编码
     *
     * @param path      文件路径
     * @param urlEncode 如果Content-Type是application/x-www-form-urlencoded时,传true
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    private String getFileContentAsBase64(String path, boolean urlEncode) throws IOException {
        byte[] b = Files.readAllBytes(Paths.get(path));
        String base64 = Base64.getEncoder().encodeToString(b);
        if (urlEncode) {
            base64 = URLEncoder.encode(base64, "utf-8");
        }
        return base64;
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

}
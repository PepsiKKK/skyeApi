package com.skye.skyeApiClientSdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.skye.skyeApiClientSdk.model.User;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.skye.skyeApiClientSdk.utils.SignUtils.genSign;

/**
 * 客户端层，负责与用户交互、处理用户请求，以及调用服务端提供的 API 接口等任务的部分
 * 负责调用第三方接口
 */
public class SkyeApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";
    private String accessKey;
    private String secretKey;

    public SkyeApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        return result;
    }

    public String getNameByPost(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        return result;
    }

    private String getJsonString(User user){
        return JSONUtil.toJsonStr(user);
    }

    private Map<String, String> getHeaderMap(String body, String method){
        Map<String, String> hashMap = new HashMap<>();

        //1、ak
        hashMap.put("accessKey", accessKey);

        // 2、不能直接传递sk
        // hashMap.put("secretKey", secretKey);

        // 3、生成随机数(生成一个包含100个随机数字的字符串)
        hashMap.put("nonce", RandomUtil.randomNumbers(4));

        // 4、请求体内容
        hashMap.put("body", body);

        // 当前时间戳
        // System.currentTimeMillis()返回当前时间的毫秒数。通过除以1000，可以将毫秒数转换为秒数，以得到当前时间戳的秒级表示
        // String.valueOf()方法用于将数值转换为字符串。在这里，将计算得到的时间戳（以秒为单位）转换为字符串
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("method", method);

        // 生成签名
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    public String getUserNameByPost(User user) {

        // 将用户对象转换为JSON字符串
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/json")
                // 添加请求头
                .addHeaders(getHeaderMap(json, "post"))
                // 设置请求体
                .body(json)
                // 发送POST请求
                .execute();
        // 打印响应状态码
        System.out.println(httpResponse.getStatus());
        // 打印响应体内容
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }

    public String invokeInterface(String params, String url, String method) throws UnsupportedEncodingException {
        HttpResponse httpResponse = ((HttpRequest) HttpRequest.post(GATEWAY_HOST + url).header("Accept-Charset", "UTF-8").addHeaders(this.getHeaderMap(params, method))).body(params).execute();
        return JSONUtil.formatJsonStr(httpResponse.body());
    }
}

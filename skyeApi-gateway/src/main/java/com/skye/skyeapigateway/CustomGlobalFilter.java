package com.skye.skyeapigateway;

import com.skye.common.model.entity.InterfaceInfo;
import com.skye.common.model.entity.User;
import com.skye.common.service.InnerInterfaceInfoService;
import com.skye.common.service.InnerUserInterfaceInfoService;
import com.skye.common.service.InnerUserService;
import com.skye.skyeApiClientSdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.SpringProperties;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    //白名单
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        // 1、用户发送请求到API网关
        /*
        当到达这个方法时已经发送到了API网关
         */

        // 2、请求日志
        /*
        exchange(路由交换机)：
        请求的信息、响应的信息、响应体、请求体都能从这里拿到。
        chain(责任链模式)：所有过滤器是按照从上到下的顺序依次执行，形成了一个链条。
        所以这里用了一个chain，如果当前过滤器对请求进行了过滤后发现可以放行，就要调用责任链中的next方法，相当于直接找到下一个过滤器，这里称为filter。有时候我们需要在责任链中使用 next，而在这里它使用了 filter 来找到下一个过滤器，从而正常地放行请求
         */
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识：{}", request.getId());
        String path = INTERFACE_HOST + request.getPath().value();
        log.info("请求路径：{}", path);
        String method = request.getMethod().toString();
        log.info("请求方法：{}", method);
        log.info("请求参数：{}", request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：{}", sourceAddress);
        log.info("请求来源地址：{}", request.getRemoteAddress());

        // 3、黑白名单
        /*
        这里选择白名单
        拦截方法：直接将响应状态码设置为403（禁止访问）
        返回：Mono对象：响应式编程的一种对象，类似前端的Promise，
        直接返回Mono，不包含响应参数，相当于告诉程序请求处理完成了
         */
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //进行判断
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }

        // 4、用户鉴权aksk是否合法
        //从请求头中获取参数
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // 1、实际情况应该是去数据库中查是否已分配给用户
        //获取一个user对象
        User invokeUser = null;
        try {
            //远程调用方法，获取用户信息
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error:{}", e.getMessage());
        }
        if (invokeUser == null){
            return handleNoAuth(response);
        }

        // 2、校验随机数，模拟一下，直接判断nonce是否大于10000
        if (Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(response);
        }

        // 3、时间和当前时间不能超过5分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if (Math.abs(currentTime - Long.parseLong(timestamp)) > FIVE_MINUTES) {
            return handleNoAuth(response);
        }

        // 4、判断签名是否正确，实际上是通过ak查找sk
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (!sign.equals(serverSign) || sign == null) {
            return handleNoAuth(response);
        }
        //通过

        // 5、请求的模拟接口是否存在
        /*
        不建议在网关中引入MyBatis等依赖，因为主项目以及有了，可能会造成重复
        解决方案：
        1、有现成的访问数据库的方法或者操作数据库的接口
        2、使用远程调用的方式调用那个可以操作数据库的项目提供的接口
            调用方式 HTTP请求：常见的库HTTPClient、RestTemplate、Feign
            调用方式 RPC框架：Dubbo、gRPC、Motan、gRPC
        3、使用Redis等缓存中间件，将模拟接口的信息缓存起来（这一条是AI自动生成）
         */
        //获取一个空的接口对象
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }


        //使用增强response
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
/*        // 6、请求转发，调用模拟接口
        //异步操作
        Mono<Void> filter = chain.filter(exchange);

        // 7、响应日志
        log.info("响应：{}", response.getStatusCode());

        if (response.getStatusCode() == HttpStatus.OK) {
            // 8、调用成功，调用次数+1 is2xxSuccessful()
            //invokeCount
        } else {
            // 9、调用失败，返回错误信息
            return handleInvokeError(response);
        }
        return filter;*/
    }

    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 统一处理禁止访问（403）的方法
     * @param response
     * @return
     */
    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 统一处理调用失败（500）的方法
     * @param response
     * @return
     */
    private Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            // 获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应的状态码
            HttpStatus statusCode = originalResponse.getStatusCode();

            // 判断状态码是否为200 OK(按道理来说,现在没有调用,是拿不到响应码的,对这个保持怀疑 沉思.jpg)
            if(statusCode == HttpStatus.OK) {
                // 创建一个装饰后的响应对象(开始穿装备，增强能力)
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    // 重写writeWith方法，用于处理响应体的数据
                    // 这段方法就是只要当我们的模拟接口调用完成之后,等它返回结果，
                    // 就会调用writeWith方法,我们就能根据响应结果做一些自己的处理
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 判断响应体是否是Flux类型
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 返回一个处理后的响应体
                            // (这里就理解为它在拼接字符串,它把缓冲区的数据取出来，一点一点拼接好)
                            return super.writeWith(fluxBody.map(dataBuffer -> {

                                //调用成功，接口调用次数+1
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }

                                // 读取响应体的内容并转换为字节数组
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                //打印日志
                                log.info("响应结果：{}", data);
                                // 将处理后的内容重新包装成DataBuffer并返回
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("网关处理响应异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 对于200 OK的请求,将装饰后的响应对象传递给下一个过滤器链,并继续处理(设置repsonse对象为装饰过的)
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 对于非200 OK的请求，直接返回，进行降级处理
            return chain.filter(exchange);
        }catch (Exception e){
            // 处理异常情况，记录错误日志
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }
}
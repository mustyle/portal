package org.fool.portal.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientPool {
    private static PoolingHttpClientConnectionManager manager = null;
    private static CloseableHttpClient httpClient = null;

    public static synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            // 注册访问协议相关的Socket工厂
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
                    .build();

            // HttpConnection工厂：配置写请求/解析响应处理器
            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory
                    = new ManagedHttpClientConnectionFactory(DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);

            // DNS解析器
            DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;

            // 创建池化连接管理器
            manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connectionFactory, dnsResolver);

            // 默认为Socket配置
            SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

            manager.setDefaultSocketConfig(defaultSocketConfig);

            manager.setMaxTotal(300);   // 设置整个连接池最大的连接数
            manager.setDefaultMaxPerRoute(200); // 每个路由最大连接数
            // 在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
            manager.setValidateAfterInactivity(5 * 1000);

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(2 * 1000)  // 设置连接超时时间
                    .setSocketTimeout(5 * 1000) // 设置等待超时时间
                    .setConnectionRequestTimeout(2000)  // 设置从连接池获取连接的等待超时时间
                    .build();

            httpClient = HttpClients.custom()
                    .setConnectionManager(manager)
                    .setConnectionManagerShared(false)  // 连接池不是共享模式
                    .evictIdleConnections(60, TimeUnit.SECONDS) // 定期回收空闲连接
                    .evictExpiredConnections()  // 定期回收过期连接
                    .setConnectionTimeToLive(60, TimeUnit.SECONDS)  // 连接存活时间，如果不设置，则根据长连接信息决定
                    .setDefaultRequestConfig(defaultRequestConfig) // 设置默认请求配置
                    .setConnectionReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE) // 连接重用策略
                    .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE) // 长连接配置
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false)) // 设置重试次数，默认是3次：当前是禁用掉（根据需要开启）
                    .build();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    httpClient.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        return httpClient;
    }
}

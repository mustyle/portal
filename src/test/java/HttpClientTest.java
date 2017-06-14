import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fool.portal.util.HttpClientPool;
import org.fool.portal.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientTest {
    @Test
    public void testGet() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet("http://www.baidu.com");

        CloseableHttpResponse response = httpClient.execute(httpGet);

        HttpEntity entity = response.getEntity();

        String html = EntityUtils.toString(entity);

        System.out.println(html);

        response.close();
        httpClient.close();
    }

    @Test
    public void testPost() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://localhost:8080/portal/posttest");
//        // form parameters by post without json
//        List<NameValuePair> formList = new ArrayList<>();
//        formList.add(new BasicNameValuePair("username", "zhangsan"));
//        formList.add(new BasicNameValuePair("password", "123456"));
//
//        StringEntity entity = new UrlEncodedFormEntity(formList, "utf-8");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", "zhangsan");
        paramMap.put("password", "123456");

        StringEntity entity = new StringEntity(JsonUtil.objectToJson(paramMap), ContentType.APPLICATION_JSON);

        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        HttpEntity httpEntity = response.getEntity();

        String result = EntityUtils.toString(httpEntity);

        System.out.println(result);

        response.close();
        httpClient.close();
    }

    @Test
    public void testGetPool() {
        CloseableHttpClient httpClient = HttpClientPool.getHttpClient();

        HttpResponse response = null;

        try {
            HttpGet httpGet = new HttpGet("http://www.baidu.com");
            response = httpClient.execute(httpGet);

            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            } else {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println(result);
            }
        } catch (Exception e) {
            if(response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}

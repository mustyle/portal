import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JedisTest {
    @Test
    public void testJedisSingle() {
//        JedisShardInfo jedisShardInfo = new JedisShardInfo("192.168.5.200", 6379);
//        jedisShardInfo.setPassword("123456");

//        Jedis jedis = new Jedis(jedisShardInfo);

        Jedis jedis = new Jedis("192.168.5.200", 6379);
        jedis.set("test", "Hello Jedis");

        String value = jedis.get("test");

        System.out.println(value);

        jedis.close();
    }

    @Test
    public void testJedisPool() {
//        JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig(), "192.168.5.200", 6379, 30000, "123456");

        JedisPool jedisPool = new JedisPool("192.168.5.200", 6379);

        Jedis jedis = jedisPool.getResource();

        String value = jedis.get("test");

        System.out.println(value);

        jedis.close();

        jedisPool.close();
    }

    @Test
    public void testJedisCluster() throws IOException {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.5.200", 7001));
        nodes.add(new HostAndPort("192.168.5.200", 7002));
        nodes.add(new HostAndPort("192.168.5.200", 7003));
        nodes.add(new HostAndPort("192.168.5.200", 7004));
        nodes.add(new HostAndPort("192.168.5.200", 7005));
        nodes.add(new HostAndPort("192.168.5.200", 7006));

        JedisCluster jedisCluster = new JedisCluster(nodes);
        jedisCluster.set("username", "zhangsan");
        jedisCluster.set("password", "123456");

        System.out.println(jedisCluster.get("username"));
        System.out.println(jedisCluster.get("password"));

        jedisCluster.close();
    }

    @Test
    public void testSpringJedisSingle() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-jedis-single.xml");
        JedisPool pool = (JedisPool) context.getBean("jedisPool");
        Jedis jedis = pool.getResource();

        String string = jedis.get("test");
        System.out.println(string);

        jedis.close();
        pool.close();
        context.close();
    }

    @Test
    public void testSpringJedisCluster() throws IOException {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-jedis-cluster.xml");
        JedisCluster jedisCluster = (JedisCluster) context.getBean("jedisCluster");
        String string = jedisCluster.get("username");
        System.out.println(string);

        jedisCluster.close();
        context.close();
    }
}

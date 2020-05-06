package zbh.study.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MqConfig {
    public static final String BUYBUY_NORMAL_QUEUE = "buybuy.normal_queue";
    public static final String BUYBUY_DEALY_QUEUE = "buybuy.delay_queue";
    public static final String BUYBUY_DEAD_QUEUE = "buybuy.dead_queue";
    public static final String BUYBUY_NORMAL_EXCHANGE = "buybuy.normal_exchange";
    public static final String BUYBUY_DEAD_EXCHANGE = "buybuy.dead_exchange";
    public static final String NORMAL_KEY = "normal_key";
    public static final String DELAY_KEY = "delay_key";
    public static final String DEAD_KEY = "dead_key";
    @Bean
    public Queue queue() {
        return new Queue(BUYBUY_NORMAL_QUEUE, true);
    }
    @Bean
    public Queue delay_queue() {
        Map<String,Object> map=new HashMap<>();
        //设置TTL为2分钟过期时间
        map.put("x-message-ttl", 120000);
        //x-dead-letter-exchange参数是设置该队列的死信交换器（DLX）
        map.put("x-dead-letter-exchange", BUYBUY_DEAD_EXCHANGE);
        //x-dead-letter-routing-key参数是给这个DLX指定路由键，确保死信到达对应死信队列
        map.put("x-dead-letter-routing-key", DEAD_KEY);
        return new Queue(BUYBUY_DEALY_QUEUE, true, false, false,map);
    }
    @Bean
    public Queue dead_queue() {
        return new Queue(BUYBUY_DEAD_QUEUE, true);
    }
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(BUYBUY_NORMAL_EXCHANGE);
    }

    @Bean
    public DirectExchange dead_exchange() {
        return new DirectExchange(BUYBUY_DEAD_EXCHANGE);
    }
    @Bean
    public Binding normal(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(NORMAL_KEY);
    }
    @Bean
    public Binding delay(Queue delay_queue, DirectExchange exchange){
        return BindingBuilder.bind(delay_queue).to(exchange).with(DELAY_KEY);
    }
    @Bean
    public Binding dead(Queue dead_queue, DirectExchange dead_exchange){
        return BindingBuilder.bind(dead_queue).to(dead_exchange).with(DEAD_KEY);
    }
}

package zbh.study.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: wuhui
 * @time: 2019/9/10 21:27
 * @desc:
 */
@Configuration
public class MqConfig {
    public static final String SECKILL_QUEUE = "seckill.queue";
    @Bean
    public Queue queue() {
        return new Queue(SECKILL_QUEUE, true);
    }
}

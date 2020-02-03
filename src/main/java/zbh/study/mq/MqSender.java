package zbh.study.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MqSender {
    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMessage(BuyMessage message) {
        String msg = JSON.toJSONString(message);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MqConfig.SECKILL_QUEUE,msg);
    }
}

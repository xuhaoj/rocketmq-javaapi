package jackxu;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author jackxu
 * 消息发送规则,发送到某一个message queue
 */
public class ProducerSelector {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("my_test_producer_group");
        producer.setNamesrvAddr("192.168.44.163:9876;192.168.44.164:9876");
        producer.start();

        for (int i = 0; i < 6; i++) {
            try {
                // tags 用于过滤消息 keys 索引键,多个用空格隔开,RocketMQ可以根据这些key快速检索到消息
                Message msg = new Message("q-2-1",
                        "TagA",
                        "2673",
                        ("RocketMQ " + String.format("%05d", i)).getBytes());

                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                }, i);
                System.out.println(String.format("%05d", i) + sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }
}
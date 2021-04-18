package jackxu;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author jackxu
 * 同步发送
 */
public class Producer {

    public static void main(String[] args) throws MQClientException {
        //生产者组
        DefaultMQProducer producer = new DefaultMQProducer("jackxu_producer_group");
        //生产者需用通过NameServer获取所有broker的路由信息，多个用分号隔开，这个跟Redis哨兵一样
        producer.setNamesrvAddr("39.103.144.86:9876;42.192.77.73:9876");
        //启动
        producer.start();

        for (int i = 0; i < 10; i++) {
            try {
                /*Message(String topic, String tags, String keys, byte[] body)
                 Message代表一条信息，第一个参数是topic，这是主题
                第二个参数是tags，这是可选参数，用于消费端过滤消息
                第三个参数是keys，这也是可选参数，如果有多个，用空格隔开。RocketMQ可以根据这些key快速检索到消息，相当于
                消息的索引，可以设置为消息的唯一编号（主键）。*/
                Message msg = new Message("jackxu_test_topic", "TagA", "6666", ("RocketMQ Test message " + i).getBytes());
                //SendResult是发送结果的封装，包括消息状态，消息id，选择的队列等等，只要不抛异常，就代表发送成功
                SendResult sendResult = producer.send(msg);
                System.out.println("第" + i + "条send结果: " + sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        producer.shutdown();
    }

}
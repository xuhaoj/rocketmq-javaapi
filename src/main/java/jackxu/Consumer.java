package jackxu;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 暂时没看懂
 */
public class Consumer {
    private static final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws MQClientException {
        // 创建消费者
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("my_test_consumer_group");
        consumer.setNamesrvAddr("192.168.44.162:9876");
                consumer.start();
        // 从指定topic中拉取所有消息队列
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("test_topic_1");
        for (MessageQueue mq : mqs) {
            System.out.println("Consume from the queue: " + mq);
            SINGLE_MQ: while (true) {
                try {
                    // 获取消息的offset，指定从store中获取
                    long offset = consumer.fetchConsumeOffset(mq, true);
                    PullResult pullResult =
                            consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);

                    if (null != pullResult.getMsgFoundList()) {
                        for (MessageExt messageExt : pullResult.getMsgFoundList()) {
                            System.out.print(new String(messageExt.getBody()));
                            System.out.print(pullResult);
                            System.out.println(messageExt);
                        }
                    }

                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            // TODO
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        consumer.shutdown();
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offseTable.put(mq, offset);
    }

    // 需要消费者自己记录消费的偏移量  因为消息在broker上是持久的 (
    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offseTable.get(mq);
        if (offset != null){
            return offset;
        }
        return 0;
    }

}

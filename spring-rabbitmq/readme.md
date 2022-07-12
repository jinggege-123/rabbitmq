
##环境搭建
生产方和消费方一样
引入依赖
~~~xml
<dependency>
    <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ~~~
添加rabbitmq配置

~~~yaml
server:
  port: 8021
spring:
  #给项目来个名字
  application:
    name: rabbitmq-provider
  #配置rabbitMq 服务器
  rabbitmq:
    host: 11.101.4.244
    port: 5672
    username: guest
    password: guest
    #虚拟host 可以不设置,使用server默认host
#    virtual-host: JCcccHost
#生产方的消息确认
#    publisher-returns: true
#    publisher-confirm-type: correlated
~~~

##五种模式
### 1、work queue
特点：只有队列没有交换机

生产方：
- 1、只申明队列
~~~java
@Configuration
public class WorkConfig {
    @Bean
    public Queue workQueue(){
        return new Queue("work",true);
    }
}
~~~
- 2、投递消息
~~~java
@RestController
public class WorkController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping("/work")
    public String work(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "work!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        rabbitTemplate.convertAndSend("work",map);
        return "work";
    }
}
~~~
消费方：
~~~java
@Component
@RabbitListener(queues = "work")
public class Work1Receive {

    @RabbitHandler

    public void work(Map map) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2000);
        System.out.println("work1111******:"+map);
    }
}
~~~
###2、Direct (直连)
特点：申明交换机和队列
生产方：
-1、指定交换机和队列
~~~java
@Component
public class DirectConfig {

    @Bean
    public Queue directQueue(){
        return new Queue("directQueue",true);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("directExchange",true,false);
    }

    @Bean
    public Binding bindDirect(){
        return BindingBuilder.bind(directQueue()).to(directExchange()).with("routing");
    }

}
~~~
-2、投递消息
~~~java
@RestController
public class DirectController {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/direct")
    public String send(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("directExchange", "routing", map);
        return "ok";
    }
}
~~~
消费方：
~~~java
@Component
//@RabbitListener(queues = "directQueue")
public class DirectAReceive {

//    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "directQueue",durable = "true"),
            exchange = @Exchange(value = "directExchange", durable = "true"),
            key = "routing"
    ))
    public void receive(Map map) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        System.out.println("direct-A-*****"+map.toString());
    }
}
~~~
###3、Topic(主题)
生产方：
~~~java
@Configuration
public class TopicConfig {

    private static final String man = "topic.man";
    private static final String women = "topic.women";

    @Bean
    public Queue manQueue(){
        return new Queue(TopicConfig.man);
    }
    @Bean
    public Queue womenQueue(){
        return new Queue(TopicConfig.women);
    }


    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topicExchange");
    }

    @Bean
    public Binding bindingExchange1(){
        return  BindingBuilder.bind(manQueue()).to(topicExchange()).with(man);
    }

    @Bean
    public Binding bindingExchange2(){
        return  BindingBuilder.bind(womenQueue()).to(topicExchange()).with("topic.#");
    }
}
~~~
- 投递消息
~~~java
@RestController
public class TopicController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/man")
    public String man(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "man!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        rabbitTemplate.convertAndSend("topicExchange","topic.man",map);
        return "man";
    }

    @GetMapping("/women")
    public String women(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "women!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        rabbitTemplate.convertAndSend("topicExchange","topic.women",map);
        return "women";
    }
}
~~~
消费方：
~~~java
@Component
//@RabbitListener(queues = "topic.man")
public class TopicReceive {

//    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "topic.man"),
            exchange = @Exchange(value = "topicExchange",type = "topic"),
            key = "topic.man"
    ))
    public void man(Map map){
        System.out.println("man@@@@@@:"+map.toString());
    }
}
~~~
~~~java
@Component
//@RabbitListener(queues = "topic.women")
public class TopicAllReceive {

//    @RabbitHandler
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "topic.women"),
        exchange = @Exchange(value = "topicExchange" ,type = "topic"),
        key = "topic.#"
))
    public void man(Map map){
        System.out.println("women****:"+map.toString());
    }
}
~~~

###4、Fanout（扇形）
生产方：
~~~java
@Configuration
public class FanoutConfig {

    @Bean
    public Queue queueA(){
        return new Queue("queue.A");
    }
    @Bean
    public Queue queueB(){
        return new Queue("queue.B");
    }
    @Bean
    public Queue queueC(){
        return new Queue("queue.C");
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanoutExchange");
    }

    @Bean
    public Binding bindingA(){
        return BindingBuilder.bind(queueA()).to(fanoutExchange());
    }
    @Bean
    public Binding bindingB(){
        return BindingBuilder.bind(queueB()).to(fanoutExchange());
    }

    @Bean
    public Binding bindingC(){
        return BindingBuilder.bind(queueC()).to(fanoutExchange());
    }
}
~~~
-投递消息
~~~java
@RestController
public class FanoutController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/fanout")
    public String fanout(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "fanout!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        rabbitTemplate.convertAndSend("fanoutExchange",null,map);
        return "fanout";
    }
}
~~~

消费方：
- 消费者1：
~~~java
@Component
//@RabbitListener(queues = "queue.A")
public class FanoutAReceive {

//    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.A"),
            exchange = @Exchange(value = "fanoutExchange", type = "fanout")
//不需要指定key
    ))
    public void receiveA(Map map){
        System.out.println("queueA***:"+map.toString());
    }
}
~~~
- 消费者2：
~~~java
@Component
//@RabbitListener(queues = "queue.B")
public class FanoutBReceive {

//    @RabbitHandler
//不需要指定key
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.B"),
            exchange = @Exchange(value = "fanoutExchange", type = "fanout")

    ))
    public void receiveA(Map map){
        System.out.println("queueB***:"+map.toString());
    }
}

~~~
- 消费者3：
~~~java
@Component
//@RabbitListener(queues = "queue.C")
public class FanoutCReceive {

//    @RabbitHandler
//不需要指定key
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.C"),
            exchange = @Exchange(value = "fanoutExchange", type = "fanout")
    ))
    public void receiveA(Map map){
        System.out.println("queueC***:"+map.toString());
    }
}
~~~

###5、header

## 能者多劳
消费方：
~~~yaml
spring:
  #配置rabbitMq 服务器
  rabbitmq:
# 进行消息分配（能者多劳）
    listener:
      simple:
  # 消费端最大并发数
        max-concurrency: 5
  # 消费端最小并发数
        concurrency: 1
 # 一次处理的消息数量
        prefetch: 1
~~~
##消息确认
### 生产方确认方式：
~~~yaml
spring:
  rabbitmq:
#   设置发布确认方式 
    publisher-returns: true
    publisher-confirm-type: correlated
~~~

~~~java
@Configuration
public class RabbitmqConfig {
    @Bean
    public RabbitTemplate returnRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
    //true:交换机无法将消息进行路由时，会将该消息返回给生产者
    //false:如果发现消息无法进行路由，则直接丢弃;默认false
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
                System.out.println("ConfirmCallback:     "+"确认情况："+b);
                System.out.println("ConfirmCallback:     "+"原因："+s);
            }
        });

        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage re) {

                System.out.println("ReturnCallback:     "+"消息："+re.getMessage());
                System.out.println("ReturnCallback:     "+"回应码："+re.getReplyCode());
                System.out.println("ReturnCallback:     "+"回应信息："+re.getReplyText());
                System.out.println("ReturnCallback:     "+"交换机："+re.getExchange());
                System.out.println("ReturnCallback:     "+"路由键："+re.getRoutingKey());
            }
        });
        return rabbitTemplate;
    }

}
~~~
### 消费方手动ack



消费方：
- 1、全局（不推荐）：
~~~yaml
spring:
  #配置rabbitMq 服务器
  rabbitmq:
# 进行消息分配（能者多劳）
    listener:
      simple:
        acknowledge-mode: manual
~~~
- 2、 @RabbitListener指定  ackmode="MANUAL"
~~~java
@RabbitListener(bindings=@QueueBinding(
            value =@Queue(value = "acksQueue",durable = "false"),
            exchange = @Exchange(value = "acksExchange" ,type = "direct",durable = "false"),
            key = "acksing"
    ),ackMode = "MANUAL")
~~~
例子：
生产方：
~~~java
@Configuration
public class DirectAckConfig {

    @Bean
    public Queue ackQueue(){
        return new Queue("acksQueue",false);
//        return QueueBuilder.durable("ackQueue").build();
    }

    @Bean
    public DirectExchange ackDirectExchange(){
        return new DirectExchange("acksExchange",false,false);
//        return ExchangeBuilder.directExchange("ackExchange").build();
    }

    @Bean
    public Binding bindings(){
        return BindingBuilder.bind(ackQueue()).to(ackDirectExchange()).with("acksing");
    }
}
~~~
- 投递消息
~~~java
@RestController
public class DirectAckController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/ack/{id}")
    public String acks(@PathVariable Integer id){
        Map<String,String> map = new HashMap<>();
        map.put("id",id.toString());
        map.put("name","jinggege");
        map.put("age","16");
        rabbitTemplate.convertAndSend("acksExchange","acksing",map);
        return "ack";
    }
}
~~~
消费方：
~~~java
@Component
public class DirectAckReceive {

    @RabbitListener(bindings=@QueueBinding(
            value =@Queue(value = "acksQueue",durable = "false"),
            exchange = @Exchange(value = "acksExchange" ,type = "direct",durable = "false"),
            key = "acksing"
    ),ackMode = "MANUAL")
    public void ackReceive(Map<String,String> map, Message msg, Channel channel) throws IOException {
        long id = msg.getMessageProperties().getDeliveryTag();
        try {
            if(Integer.parseInt(map.get("id"))%2==0){
                //          System.out.println("body****:"+body);
            TimeUnit.SECONDS.sleep(3);
                System.out.println("***偶数消息发送"+map.toString());

                channel.basicAck(id,true);
            }else{
                System.out.println("***偶数消息回退");
                channel.basicReject(id,true);
            }


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("***消息回退");
            channel.basicReject(id,true);
        }

    }
}
~~~

##死信队列
### 1、死信队列产生原因
- 1、消息 TTL 过期
   消息设置TTL：
 `而消息设置TTL方式，消息即使过期，也不一定会被马上丢弃，因为因为 RabbitMQ 只会检查第一个消息是否过期，如果过期则丢到死信队列，如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行。
  
           另外，还需要注意的一点是，如果 不设置 TTL，表示消息永远不会过期，如果将 TTL 设置为 0，则表示除非此时可以直接投递该消息到消费者，否则该消息将会被丢弃。
        `
~~~java
Message msg = new Message(s.getBytes(StandardCharsets.UTF_8));
//参数四 MessagePostProcessor：用于在执行消息转换后添加/修改标头或属性。 
//它还可以用于在侦听器容器和AmqpTemplate接收消息时修改入站消息。
rabbitTemplate.convertAndSend("MqSendService-One","One",msg,correlationData->{
    correlationData.getMessageProperties().setExpiration("1000");
    return correlationData;
});

//也可在创建消息时指定
 msg.getMessageProperties().setExpiration("1000");
~~~    
   队列设置TTL：
   如果设置了队列的 TTL 属性，那么一旦消息过期，就会被队列丢弃(如果配置了死信队列被丢到死信队列中)，
~~~java
@Bean
public DirectExchange directExchange(){
    Map<String, Object> args = new HashMap<>(3);
    //声明队列的 TTL
    args.put("x-message-ttl", 10000);
    //参数介绍
    //1.交换器名 2.是否持久化 3.自动删除 4.其他参数
    return new DirectExchange("MqSendService-One",false,false,args);
}


@Bean
public Queue directQueue(){
    //需要的属性可以通过构建者不断添加
    Queue queue = QueueBuilder.noDurable("TTL_Queue").ttl(100).build();
    return queue;
}
~~~
- 2、队列达到最大长度(队列满了，无法再添加数据到 mq 中)
- 3、消息被拒绝(basic.reject 或 basic.nack)并且 requeue=false

生产方：
- 1、创建死信交换机及队列并绑定
- 2、创建普通交换机及队列并绑定
- 3、为普通队列绑定私信交换机
~~~java
@Configuration
public class DeadConfig {

    private final static String deadExchange= "dead_exchange";
    private final static String deadQueue = "dead_queue";
    private final static String dead_key= "dead";

    @Bean
    public Queue deadQueue(){
        return QueueBuilder.durable(DeadConfig.deadQueue).build();
    }

    @Bean
    public DirectExchange deadExchange(){
        return ExchangeBuilder.directExchange(DeadConfig.deadExchange).durable(true).build();
    }

    @Bean
    public Binding deadBinding(){
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(dead_key);
    }

    private final static String normalExchange= "normal_exchange";
    private final static String normalQueue = "normal_queue";
    private final static String normal_key= "normal";

    @Bean
    public Queue normalQueue(){
        return QueueBuilder.durable(normalQueue)
                .deadLetterExchange(deadExchange)
                .deadLetterRoutingKey(dead_key)
                .ttl(3000)
                .maxLength(10)
                .autoDelete()
                .build();
    }

    @Bean
    public DirectExchange normalExchange(){
        return ExchangeBuilder.directExchange(normalExchange).durable(true).build();
    }

    @Bean
    public Binding normalBinding(){
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(normal_key);
    }

}
~~~
- 4、给普通队列推送消息：
~~~java
@RestController
public class DelayController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/delay")
    public String delay(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID());
        map.put("name","jinggege");
        map.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss:dd")));
        rabbitTemplate.convertAndSend("normal_exchange","normal",map);
        return "delay";
    }
}
~~~
消费方：
- 1、接收消息
~~~java
@Component
public class DelayReceive {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "normal_queue"
                        ,durable = "true"
                        ,autoDelete = "true"
                        ,arguments = {
                        @Argument(name = "x-dead-letter-exchange",value = "dead_exchange"),
                        @Argument(name = "x-dead-letter-routing-key",value = "dead"),
                        @Argument(name = "x-message-ttl",value = "3000"),
                        @Argument(name = "x-max-length",value = "10")
                        }
                    ),
            exchange = @Exchange(value = "normal_exchange",durable = "true"),
            key = "normal"
    ),ackMode = "MANUAL")
    public void delay(Map map, Message msg, Channel channel) throws InterruptedException, IOException {
        long id = msg.getMessageProperties().getDeliveryTag();
        try {
            System.out.println("延迟队列："+map.toString());
            TimeUnit.SECONDS.sleep(10);
            System.out.println("延迟队列处理反馈");
//            拒绝后会进入死信队列
            channel.basicReject(id,false);
        }catch (Exception e){
            e.printStackTrace();
            channel.basicReject(id,false);
        }
    }
}
~~~

##延迟队列
RabbitMQ的延迟队列可以通过设置TTL的时间再配合设置死信队列的参数达到。
例：创建一个队列并设置TTL时间，但无人监听消费，那么当TTL时间达到，该消息就会进入死信队列，这时设置一个监听死信队列的消 费者，从而达到延迟消费的效果。

##优先级队列
RabbitMQ支持为队列设置优先级，从而达到优先级高的队列中消息被优先消费。
~~~java
 @Bean
    public Queue normalQueue(){
        return QueueBuilder.durable(normalQueue)
                .deadLetterExchange(deadExchange)
                .deadLetterRoutingKey(dead_key)
                .ttl(3000)
                .maxLength(10)
                .autoDelete()
//                设置队列优先级
//                .maxPriority(12)
                .build();
    }
~~~

##惰性队列
默认情况下，当生产者将消息发送到 RabbitMQ 的时候，队列中的消息会尽可能的存储在内存之中，这样可以更加快速的将消息发送给消费者。即使是持久化的消息，在被写入磁盘的同时也会在内存中驻留一份备份。

惰性队列会尽可能的将消息存入磁盘中，而在消费者消费到相应的消息时才会被加载到内存中，它的一个重要的设计目标是 支持更多的消息存储。当消费者由于各种各样的原因（比如消费者下线、宕机亦或者是由于维护而关闭等）而致使长时间内不能消费消息造成堆积时，惰性队列就很有必要了。
~~~java
@Bean
    public Queue normalQueue(){
        return QueueBuilder.durable(normalQueue)
                .deadLetterExchange(deadExchange)
                .deadLetterRoutingKey(dead_key)
                .ttl(3000)
                .maxLength(10)
                .autoDelete()
//                设置队列优先级
//                .maxPriority(12)
//                设置为惰性队列
//                .lazy()
                .build();
    }
~~~


##备用交换机
- 1、前言
`有了消息回退的功能我们可以感知到消息的投递情况，但是对于这些无法路由到的消息我们可能只能做一个记录的功能，
 然后再手动处理；并且消息回退会增加生产者的复杂性；那么现在如何想要实现不增加生产者的复杂性，
 并保证消息不丢失呢？因为消息是不可达的，所以显然无法通过死信队列机制实现。所以通过这种备用交换机的机制可以实现。`

-2、实现原理
`它是通过在声明交换机的时候，为该交换机设置一个备用的交换机；
当主交换机接收一条消息不可达后，会将该消息转发到备用交换机，
它在将这些消息发到自己绑定的队列，一般备用交换机的类型都设置为 Fanout（广播类型）。
这样我们可以统一设置一个消费者监听该交换机下的队列对其进行统一处理。`

- 3、实现
mandatory 参数与备份交换机可以一起使用的时候，如果两者同时开启，谁优先级高，经测试备份交换机优先级高
~~~java
@Configuration
public class RabbitDirectConfig {
    @Bean
    public Queue alternateQueue(){
        //参数介绍
        //1.队列名 2.是否持久化 3.是否独占 4.自动删除 5.其他参数
        Queue queue = QueueBuilder.durable("alternateQueue")
            .autoDelete()
            .build();
        return queue;
    }

    @Bean
    public FanoutExchange alternateExchange(){
        return new FanoutExchange("Alternate_Exchange",true,false,null);
    }

    @Bean
    public DirectExchange directExchange(){
        //        ExchangeBuilder exchange = ExchangeBuilder.directExchange("MqSendService-One")
        //                .durable(false)
        //                .autoDelete()
        //                .withArgument("alternate-exchange", "Alternate_Exchange");
        //参数介绍
        //1.交换器名 2.是否持久化 3.自动删除 4.其他参数
        Map<String,Object> args = new HashMap<>(3);
        args.put("alternate-exchange","Alternate_Exchange");
        return new DirectExchange("MqSendService-One",false,false,args);
    }

    @Bean
    public Binding bingAlternateExchange(){
        return BindingBuilder.bind(alternateQueue())   //绑定队列
            .to(alternateExchange());      //队列绑定到哪个交换器
    }

    @Bean
    public Binding bingExchange(){
        return BindingBuilder.bind(directQueue())   //绑定队列
            .to(directExchange())       //队列绑定到哪个交换器
            .with("One");        //路由key,必须指定
    }
}

~~~




###解决RabbitMQ Management API returned status code 500 问题
~~~shell
#进入容器
docker exec -it 容器名 /bin/bash
#cd到目录/etc/rabbitmq/conf.d/
执行
echo management_agent.disable_metrics_collector = false > management_agent.disable_metrics_collector.conf
退出重启容器
~~~
消费方：
~~~java
~~~

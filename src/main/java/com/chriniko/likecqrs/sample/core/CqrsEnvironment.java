package com.chriniko.likecqrs.sample.core;

import com.chriniko.likecqrs.sample.serde.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class CqrsEnvironment {

    private final Consumer<String, JsonNode> consumer;

    private final ScheduledExecutorService scheduler;

    public CqrsEnvironment() {
        consumer = createConsumer();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            consumer.wakeup();
        }));
    }

    public void start() {
        Runnable task = () -> {
            try {
                ConsumerRecords<String, JsonNode> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, JsonNode> consumerRecord : consumerRecords) {
                    String key = consumerRecord.key();
                    JsonNode document = consumerRecord.value();
                    long timestamp = consumerRecord.timestamp();

                    readSide().upsert(timestamp, key, document);
                }

                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        System.err.println("could not commit offsets");
                    }
                });
            } catch (WakeupException e) {
                System.out.println("will shutdown kafka consumer...");
                consumer.close();
            } catch(Exception e){
                System.err.println("critical error, message: " + e.getMessage());
                e.printStackTrace();
            }
        };

        scheduler.scheduleWithFixedDelay(task, 400, 200, TimeUnit.MILLISECONDS);
    }

    public abstract WriteSide writeSide();

    public abstract ReadSide readSide();

    private Consumer<String, JsonNode> createConsumer() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "CqrsEnvironment---"+ UUID.randomUUID().toString());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonNodeDeserializer.class.getName());

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, JsonNode> c = new KafkaConsumer<>(props);
        c.subscribe(Collections.singletonList("posts"));

        return c;
    }

}

package com.chriniko.likecqrs.sample.core;

import com.chriniko.likecqrs.sample.serde.JsonNodeSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.enterprise.context.Dependent;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Dependent
public class KafkaWriteSide implements WriteSide {

    private static final String KAFKA_CONTACT_POINT = "localhost:9092";
    private final KafkaProducer<String, JsonNode> producer;

    public KafkaWriteSide() {
        final Properties properties = new Properties();

        setupBootstrapAndSerializers(properties);
        setupBatchingAndCompression(properties);
        setupRetriesInFlightTimeout(properties);

        // set number of acknowledgments - acks - default is all
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // send blocks up to 3 seconds
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);

        producer = new KafkaProducer<>(properties);
    }


    // Note: delete == value is null for provided key.
    @Override
    public CompletableFuture<Long> delete(String collectionName, String key) {
        CompletableFuture<Long> cf = new CompletableFuture<>();
        ProducerRecord<String, JsonNode> producerRecord = new ProducerRecord<>(collectionName, key, null);
        return send(cf, producerRecord);
    }

    @Override
    public CompletableFuture<Long> insert(String collectionName, String key, JsonNode value) {
        CompletableFuture<Long> cf = new CompletableFuture<>();
        ProducerRecord<String, JsonNode> producerRecord = new ProducerRecord<>(collectionName, key, value);
        return send(cf, producerRecord);
    }

    private CompletableFuture<Long> send(CompletableFuture<Long> cf, ProducerRecord<String, JsonNode> producerRecord) {
        producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                cf.completeExceptionally(exception);
            } else {
                cf.complete(metadata.timestamp());
            }
        });
        return cf;
    }

    private void setupBootstrapAndSerializers(Properties properties) {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTACT_POINT);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "WriteSide---" + UUID.randomUUID().toString());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonNodeSerializer.class.getName());
    }

    private void setupBatchingAndCompression(Properties properties) {
        // holds up to 64mb default is 32mb for all partition buffers
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33_554_432 * 2);
        // batch per partition, holds up to 64k per partition, default is 16k
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384 * 4);
        // wait up to 500 ms to batch to Kafka
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        // set compression type to snappy
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    }

    private void setupRetriesInFlightTimeout(Properties properties) {
        // only one in-flight message per kafka broker connection
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        // we get request timeout in 15 seconds, default is 30 seconds
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);
        // set the number of retries
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        // only retry after one second
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
    }
}

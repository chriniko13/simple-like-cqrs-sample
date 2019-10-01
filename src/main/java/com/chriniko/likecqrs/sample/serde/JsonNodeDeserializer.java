package com.chriniko.likecqrs.sample.serde;

import com.chriniko.likecqrs.sample.error.ProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class JsonNodeDeserializer implements Deserializer<JsonNode> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public JsonNode deserialize(String topic, byte[] data) {

        if (data == null) {
            return null;
        }

        try {
            return mapper.readTree(data);
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }
}

package com.chriniko.likecqrs.sample.core;

import com.chriniko.likecqrs.sample.error.ProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Dependent
@Named("elastic-search")
public class ElasticSearchReadSide implements ReadSide {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Client client;

    public ElasticSearchReadSide() {
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster").build();

        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

            Settings indexSettings = Settings.builder()
                    .put("number_of_shards", 5)
                    .put("number_of_replicas", 1)
                    .build();

            CreateIndexRequest indexRequest = new CreateIndexRequest(indexName(), indexSettings);
            if (!client.admin().indices().exists(new IndicesExistsRequest(indexName())).actionGet().isExists()) {
                client.admin().indices().create(indexRequest).actionGet();
            }

        } catch (UnknownHostException e) {
            throw new ProcessingException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> client.close()));
    }

    @Override
    public String indexName() {
        return "prod_posts";
    }

    @Override
    public String type() {
        return "posts";
    }

    @Override
    public Collection<JsonNode> findAll() {

        SearchHits searchHits = client
                .search(
                        new SearchRequest()
                                .indices(indexName())
                                .types(type())
                                .source(
                                        SearchSourceBuilder
                                                .searchSource()
                                                .from(0)
                                                .size(1_000)
                                                .query(
                                                        new RegexpQueryBuilder("author", ".*")
                                                )
                                                .sort(new ScoreSortBuilder())
                                )
                )
                .actionGet()
                .getHits();

        return map(searchHits);
    }

    @Override
    public void upsert(long timestamp, String key, JsonNode payload) {

        if (payload != null && !NullNode.getInstance().equals(payload)) {

            try {
                byte[] serializedPayload = mapper.writeValueAsBytes(payload);

                IndexRequest request = new IndexRequest(indexName());
                request.id(key);
                request.type(type());
                request.source(serializedPayload, XContentType.JSON);

                IndexResponse indexResponse = client.index(request).actionGet();
                System.out.println("response id: " + indexResponse.getId() + ", name: " + indexResponse.getResult().name());
            } catch (JsonProcessingException e) {
                throw new ProcessingException(e);
            }

        } else {

            DeleteResponse deleteResponse = client.prepareDelete(indexName(), type(), key).get();
            System.out.println("deleteResponse: " + deleteResponse);
        }
    }

    private Collection<JsonNode> map(SearchHits searchHits) {
        final List<JsonNode> postsAsJsonNode = new ArrayList<>(searchHits.getHits().length);

        for (SearchHit searchHit : searchHits.getHits()) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            ObjectNode objectNode = mapper.createObjectNode();

            objectNode.put("id", (String) sourceAsMap.get("id"));
            objectNode.put("author", (String) sourceAsMap.get("author"));
            objectNode.put("description", (String) sourceAsMap.get("description"));
            objectNode.put("text", (String) sourceAsMap.get("text"));
            objectNode.put("createdAt", (String) sourceAsMap.get("createdAt"));
            objectNode.put("updatedAt", (String) sourceAsMap.get("updatedAt"));


            postsAsJsonNode.add(objectNode);
        }

        return postsAsJsonNode;
    }
}

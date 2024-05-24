package net.aschemann.maven.gpt.common;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingStoreConfiguration {
    @Value("${embeddingstore.index:maven-gpt}")
    private String indexName;

    @Value("${embeddingstore.url:http://localhost:9200}")
    private String url;

    @Bean
    @Primary
    public EmbeddingStore<TextSegment> createTextSegmentEmbeddingStore() {
        EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl(url)
                .indexName(indexName)
                .dimension(384)
                .build();
        return embeddingStore;
    }

    @Bean
    @Primary
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}

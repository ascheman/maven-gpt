package net.aschemann.maven.gpt.common;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingStoreConfiguration {
    @Value("${embeddingstore.table:maven_gpt}")
    private String table;

    @Value("${embeddingstore.host:localhost}")
    private String host;

    @Value("${embeddingstore.port:5432}")
    private int port;

    @Value("${embeddingstore.database:embeddings}")
    private String database;

    @Value("${embeddingstore.user:postgres}")
    private String user;

    @Value("${embeddingstore.password:postgres}")
    private String password;

    @Bean
    @Primary
    public EmbeddingStore<TextSegment> createTextSegmentEmbeddingStore() {
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                .host(host)
                .port(port)
                .database(database)
                .user(user)
                .password(password)
                .table(table)
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

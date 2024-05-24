package net.aschemann.maven.gpt.cli;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import net.aschemann.maven.gpt.common.ConfluenceHtmlDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocumentsRecursively;

@SpringBootApplication
@ComponentScan(basePackages = {"net.aschemann.maven.gpt.cli", "net.aschemann.maven.gpt.common"})
public class CreateEmbeddingStore implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CreateEmbeddingStore.class);

    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;

    @Value("${embeddingstore.batchSize:5}")
    private int batchSize;

    public CreateEmbeddingStore(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(CreateEmbeddingStore.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.debug("Creating content retriever");
        ConfluenceHtmlDocumentParser parser = new ConfluenceHtmlDocumentParser("div[id=content]");
        List<Document> documents = loadDocumentsRecursively("download/cwiki/", parser
//                , new WildcardFileFilter("Maven*")
//                , new WildcardFileFilter("Build+vs+Consumer+POM*")
        );

        logger.debug("Loaded {} documents", documents.size());
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
//                .documentSplitter(DocumentSplitters.recursive( 1000, 200))
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();

        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            List<Document> batch = documents.subList(i, end);
            embeddingStoreIngestor.ingest(batch);
            logger.debug("Ingested batch of size {}/{} ({}/{} batches)",
                    batch.size(), documents.size(), i / batchSize + 1, documents.size() / batchSize);
        }
        logger.info("Ingested all documents");

        System.exit(0);
    }
}

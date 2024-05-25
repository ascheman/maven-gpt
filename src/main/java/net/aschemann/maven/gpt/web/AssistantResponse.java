package net.aschemann.maven.gpt.web;

import java.util.List;

public class AssistantResponse {
    private final String result;
    private final List<Metadata> metadata;
    private final int messageStoreSize;

    public record Metadata(String url) {
    }

    public AssistantResponse(String baseurl, String result,
                             List<dev.langchain4j.data.document.Metadata> metadata,
                             int messageStoreSize) {
        this.result = result;
        this.messageStoreSize = messageStoreSize;
        this.metadata = metadata.stream().map(m -> {
                    String filename = m.getString("file_name");
                    return new Metadata(String.format("%s/%s", baseurl, filename)
                    );
                }
        ).distinct().toList();
    }

    public String getResult() {
        return result;
    }

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public int getMessageStoreSize() {
        return messageStoreSize;
    }
}
package net.aschemann.maven.gpt.common;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConfluenceHtmlDocumentParser implements DocumentParser {
    private String cssQuery;

    public ConfluenceHtmlDocumentParser(String cssQuery) {
        this.cssQuery = cssQuery;
    }

    @Override
    public Document parse(InputStream inputStream) {
        try {
            String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Element element = Jsoup.parse(html).selectFirst(cssQuery);
            if (element != null) {
                return new Document(element.text());
            } else {
                return new Document(html);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading input stream", e);
        }
    }

}
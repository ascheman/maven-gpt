package net.aschemann.maven.gpt.web;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This is an example of using an {@link AiService}, a high-level LangChain4j API.
 */
@RestController
class AssistantController {
    private static final Logger logger = LoggerFactory.getLogger(AssistantController.class);

    @Value("${assistant.memory.maxsize:10}")
    private String memoryMaxsize;

    @Value("${assistant.confluence.baseurl:https://cwiki.apache.org/confluence/display/MAVEN}")
    private String baseurl;


    private final ContentRetriever contentRetriever;
    private final ChatLanguageModel chatLanguageModel;

    AssistantController(ContentRetriever contentRetriever, ChatLanguageModel chatLanguageModel) {
        this.contentRetriever = contentRetriever;
        this.chatLanguageModel = chatLanguageModel;
    }

    @GetMapping("/start")
    public void startNewSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @GetMapping("/ask")
    public AssistantResponse ask(@RequestParam(value = "message", defaultValue = "What is Maven") String message, HttpSession session) {
        logger.info("Received request: {}", message);

        ChatMemory memory = getChatMemory(session);
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(memory)
                .contentRetriever(contentRetriever)
                .build();

        var response = assistant.chat(message);
        List<Metadata> metadataList = response.sources().stream().map(content ->
                content.textSegment().metadata()).toList();

        logger.info("Assistant response: {}, sources: {}", response.content(), response.sources());
        return new AssistantResponse(baseurl, response.content(), metadataList, memory.messages().size());
    }

    private ChatMemory getChatMemory(HttpSession session) {
        ChatMemory memory = (ChatMemory) session.getAttribute("memory");
        if (memory == null) {
            logger.debug("Creating new ChatMemory for session '{}'", session.getId());
            memory = MessageWindowChatMemory.withMaxMessages(Integer.parseInt(memoryMaxsize));
            session.setAttribute("memory", memory);
        } else {
            logger.debug("Found existing Memory for session '{}' with #{} messages",
                    session.getId(),
                    memory.messages().size()
            );
        }
        return memory;
    }
}

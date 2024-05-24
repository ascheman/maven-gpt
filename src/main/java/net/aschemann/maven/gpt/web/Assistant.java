package net.aschemann.maven.gpt.web;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.spring.AiService;

@AiService
interface Assistant {

    @SystemMessage("""
            You are a polite assistant.
            Our users want to retrieve information about Apache Maven.
            They are already experts in Maven.
            Hence, it is not necessary to explain the basics to them.
            Try to give a brief summary as a first paragraph.
            Then give as many details as you have about the solution in subsequent paragraphs.
            Don't make paragraphs to long but separate different aspects in respective paragraphs.
            Do only answer when you are sure about the answer.""")
    Result<String> chat(String userMessage);
}
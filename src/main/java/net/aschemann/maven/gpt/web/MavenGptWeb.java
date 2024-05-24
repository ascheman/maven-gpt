package net.aschemann.maven.gpt.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"net.aschemann.maven.gpt.web", "net.aschemann.maven.gpt.common"})
public class MavenGptWeb {

    public static void main(String[] args) {
        SpringApplication.run(MavenGptWeb.class, args);
    }

}

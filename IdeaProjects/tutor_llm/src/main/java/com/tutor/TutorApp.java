package com.tutor;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.util.Scanner;

public class TutorApp {
    public static void main(String[] args) {
        var model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:1b")
                .temperature(0.7)
                .build();
        var memory = MessageWindowChatMemory.withMaxMessages(10);

        var chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();
        System.out.println("\"\uD83E\uDDE0 Tutor de Inglês - powered by LLaMA 3.2 via Ollama\"");
        System.out.println("Digite 'sair' para encerrar.\n");

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Você: ");
            String pergunta = scanner.nextLine();
            if (pergunta.equalsIgnoreCase("sair")) break;

            String resposta = chain.execute(pergunta);
            System.out.println("Tutor: " + resposta + "\n");
        }
    }
}

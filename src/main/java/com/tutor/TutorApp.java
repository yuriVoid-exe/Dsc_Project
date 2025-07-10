package com.tutor;

import com.tutor.config.ModelConfig;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.Scanner;

 /**
 * Classe principal que inicializa o Tutor de Inglês.
 * Usa LangChain4j com modelo local via Ollama.
 */

public class TutorApp {

    public static void main(String[] args) {

        // Recupera o modelo de linguagem configurado
        ChatLanguageModel model = ModelConfig.getModel();

        // Define a mémoria de curto prazo do chat
        var memory = MessageWindowChatMemory.withMaxMessages(20);

        // Cria a cadeia de conversação com o modelo e a memória
        var chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();

        // Mensagem inicial
        System.out.println("Tutor de Inglês - powered by LLaMA 3.2 via Ollama");
        System.out.println("Digite 'sair' para encerrar.\n");

        // Leitura do usuário
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Você: ");
            String pergunta = scanner.nextLine();

            if (pergunta.equalsIgnoreCase("sair")) {
                System.out.println("Até logo!");
                break;
            }

            // Processa a entrada com LangChain4j + modelo
            String resposta = chain.execute(pergunta);
            System.out.println("Tutor: " + resposta + "\n");
        }
    }
}

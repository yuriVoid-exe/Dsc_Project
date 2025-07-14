package com.tutor;

import com.tutor.config.ModelConfig;
import com.tutor.prompt.PromptTemplates;
import com.tutor.config.EmbeddingConfig;
import dev.langchain4j.model.embedding.EmbeddingModel;
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
        ChatLanguageModel model = ModelConfig.createChatLanguageModel();

        // Define a mémoria de curto prazo do chat
        var memory = MessageWindowChatMemory.withMaxMessages(20);

        // Define o prompt com o perfil do aluno (iniciante, intermediario, avançado)
        memory.add(PromptTemplates.tutorPromptBeginner());

        // Cria a cadeia de conversação com o modelo e a memória
        var chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();
        // Dentro do main() em TutorApp.java

        System.out.println("\n--- Testando o Modelo de Embedding ---");
        try {
            EmbeddingModel embeddingModel = EmbeddingConfig.createEmbeddingModel();

            // Cria um embedding para uma frase
            float[] vector = embeddingModel.embed("Hello, how are you?").content().vector();

            System.out.println("Vetor de embedding gerado com sucesso!");
            System.out.println("Dimensão do vetor: " + vector.length); // Deve imprimir 384
            // System.out.println("Exemplo de vetor: " + java.util.Arrays.toString(java.util.Arrays.copyOf(vector, 5))); // Mostra os 5 primeiros valores

        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao inicializar o modelo de embedding:");
            e.printStackTrace();
        }

        // Mensagem inicial
        System.out.println("Tutor de Inglês-para Brasileiros");
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

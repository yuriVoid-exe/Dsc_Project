package com.tutor;

import com.tutor.config.ModelConfig;
import com.tutor.prompt.PromptTemplates;
import com.tutor.config.EmbeddingConfig;
import com.tutor.rag.DocumentLoader;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.List;
import java.util.Scanner;

 /**
 * Classe principal que inicializa o Tutor de Inglês.
 * Usa LangChain4j com modelo local via Ollama.
 */

public class TutorApp {

    public static void main(String[] args) {

        // Recupera o modelo de linguagem configurado
        ChatLanguageModel model = ModelConfig.createChatLanguageModel();

        // Instancia o modelo de embedding
        EmbeddingModel embeddingModel = EmbeddingConfig.createEmbeddingModel(); // Inicializa e testa o modelo de embedding
        System.out.println("[SETUP] Modelos de Chat e Embedding carregados com sucesso.");

        // Define a mémoria de curto prazo do chat
        var memory = MessageWindowChatMemory.withMaxMessages(20);

        // Defini o perfil do aluno aqui
        List<ChatMessage> mcp = PromptTemplates.getBeginnerMcp();

        // <<<<<< BLOCO DE TESTE DO DOCUMENTLOADER AQUI

        System.out.println("\n--- [SETUP] Carregando a base de conhecimento (Documentos) ---");
        List<TextSegment> segments = null; // Declaramos a lista aqui para uso futuro
        try {
            // O caminho para a pasta data a partir da raiz do projeto.
            String dataDirectory = "data";
            segments = DocumentLoader.loadAndSplitDocuments(dataDirectory);

            if (segments != null && !segments.isEmpty()) {
                System.out.println("[SETUP] Base de conhecimento carregada. Total de segmentos: " + segments.size());
                System.out.println("--- Exemplo de Segmento ---");
                TextSegment firstSegment = segments.get(0);
                System.out.println("Origem: " + firstSegment.metadata().get("file_name"));
                System.out.println("Conteúdo: \"" + firstSegment.text().substring(0, Math.min(100, firstSegment.text().length())) + "...\"");
            } else {
                System.out.println("[SETUP] WARN: Nenhuma base de conhecimento foi carregada.");
            }

        } catch (Exception e) {
            System.err.println("[SETUP] ERRO CRÍTICO: Falha ao carregar a base de conhecimento. O sistema RAG não funcionará.");
            e.printStackTrace();
            // Em um cenário real, você poderia decidir encerrar a aplicação aqui se o RAG for essencial.
        }
        // <<<<<< FIM DO BLOCO DE TESTE

        // ====================================================================================
        // ETAPA DE CONFIGURAÇÃO DO CHAT INTERATIVO
        // ====================================================================================
        System.out.println("\n--- Configurando o assistente de conversação ---");


        // Define o prompt com o perfil do aluno (iniciante, intermediario, avançado)
        mcp.forEach(memory::add);
        System.out.println("[SETUP] Master Control Prompt (MCP) para 'Intermediário' carregado na memória.");

        // Cria a cadeia de conversação com o modelo e a memória
        var chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();

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

package com.tutor;

import com.tutor.config.ModelConfig;
import com.tutor.service.TutorAgent;
import com.tutor.service.TutorTools;
import com.tutor.service.RouterAgent;
import com.tutor.prompt.PromptTemplates;
import com.tutor.config.EmbeddingConfig;
import com.tutor.rag.QueryTransformer;
import com.tutor.rag.QdrantVectorStoreManager;
import com.tutor.rag.RetrieverService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate; // Import necessário
import com.tutor.rag.TransformingRetriever;
import dev.langchain4j.chain.ConversationalChain;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal que inicializa e executa o Tutor de Inglês com RAG.
 * A classe main orquestra a inicialização dos componentes e a construção
 * da cadeia de conversação com recuperação de conhecimento.
 */
public class TutorApp {

    public static void main(String[] args) {
        // Carrega componentes Fundamentais (Modelo de LLM e modelo de Embedding).
        System.out.println("[1/4] Carregando modelos de IA...");
        ChatLanguageModel model = ModelConfig.createChatLanguageModel();
        EmbeddingModel embeddingModel = EmbeddingConfig.createEmbeddingModel();
        System.out.println("      ... Modelos carregados com sucesso.");

        System.out.println("[2/4] Conectando e preparando a base de conhecimento (Qdrant)...");
        EmbeddingStore<TextSegment> embeddingStore = QdrantVectorStoreManager.getEmbeddingStore();
        System.out.println("      ... Base de conhecimento pronta para uso.");

        // ====================================================================================
        // ETAPA 2: CONSTRUIR OS PIPELINES ESPECIALIZADOS
        // ====================================================================================
        System.out.println("[2/4] Construindo pipelines especializados...");

        // Usaremos uma única memória compartilhada para manter o contexto entre os pipelines.
        MessageWindowChatMemory sharedMemory = MessageWindowChatMemory.withMaxMessages(20);
        List<ChatMessage> mcp = PromptTemplates.getBeginnerMcp();
        mcp.forEach(sharedMemory::add);

        // 2.1. Pipeline de Conversa Simples (para saudações e bate-papo)
        ConversationalChain conversationalChain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(sharedMemory)
                .build();
        System.out.println("      ... Pipeline de Conversa Simples pronto.");

        // 2.2. Pipeline de RAG (para perguntas que exigem conhecimento)
        Retriever<TextSegment> baseRetriever = RetrieverService.createRetriever(embeddingStore, embeddingModel);
        QueryTransformer queryTransformer = new QueryTransformer();
        Retriever<TextSegment> transformingRetriever = new TransformingRetriever(baseRetriever, queryTransformer);
        PromptTemplate ragPromptTemplate = PromptTemplates.getCompatibleRagSynthesisProtocolOptimized();

        ConversationalRetrievalChain ragChain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(sharedMemory)
                .retriever(transformingRetriever)
                .promptTemplate(ragPromptTemplate)
                .build();
        System.out.println("      ... Pipeline de RAG pronto.");

        // ====================================================================================
        // ETAPA 3: CONSTRUIR O ROTEADOR
        // ====================================================================================
        System.out.println("[3/4] Construindo o roteador de intenção...");
        RouterAgent router = RouterAgent.create();
        System.out.println("      ... Roteador pronto.");

        System.out.println("[4/4] Inicialização concluída.");

        // --- INÍCIO DA INTERAÇÃO ---
        System.out.println("\n==================================================");
        System.out.println("  Tutor de Inglês inicializado. Estou pronto!");
        System.out.println("==================================================\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Você: ");
                String pergunta = scanner.nextLine();

                if (pergunta.equalsIgnoreCase("sair")) {
                    System.out.println("Até logo!");
                    break;
                }

                // ETAPA DE ROTEAMENTO: O cérebro da decisão
                String route = router.route(pergunta);
                System.out.println("[ROUTER] Decisão: " + route);

                System.out.print("Tutor: ");
                String resposta;
                if ("rag".equalsIgnoreCase(route.trim())) {
                    // Usa o pipeline de RAG para perguntas complexas
                    resposta = ragChain.execute(pergunta);
                } else {
                    // Usa o pipeline de conversa simples para todo o resto
                    resposta = conversationalChain.execute(pergunta);
                }

                System.out.println(resposta + "\n");
            }
        }
    }
}
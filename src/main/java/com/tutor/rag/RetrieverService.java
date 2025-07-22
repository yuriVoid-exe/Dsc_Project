package com.tutor.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.embedding.EmbeddingStore;

/**
 * Serviço responsável por criar e configurar o Retriever.
 * O Retriever é o componente que busca informações relevantes no EmbeddingStore (Qdrant)
 * com base na pergunta do usuário.
 */
public class RetrieverService {

    /**
     * Cria um Retriever que se conecta ao nosso EmbeddingStore (Qdrant).
     *
     * @param embeddingStore A instância do nosso banco de dados vetorial (Qdrant).
     * @param embeddingModel O modelo de embedding para converter a pergunta do usuário em um vetor.
     * @return Uma instância de Retriever<TextSegment> pronta para uso.
     */
    public static Retriever<TextSegment> createRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {

        int maxResults = 3;

        // <<<< AUMENTANDO O LIMIAR DE SIMILARIDADE >>>>
        // Um valor mais alto (e.g., 0.7) exige que o texto recuperado seja
        // muito mais parecido com a pergunta. Isso ajuda a filtrar
        // resultados irrelevantes para perguntas genéricas como "ola".
        double minScore = 0.7;

        System.out.println("[CONFIG] Criando Retriever com maxResults=" + maxResults + " e minScore=" + minScore);

        return EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, maxResults, minScore);
    }

}
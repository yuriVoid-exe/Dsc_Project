package com.tutor.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.retriever.Retriever;
import java.util.List;

/**
 * Implementa o Padrão de Projeto "Decorator" para a interface Retriever.
 * Esta classe "envolve" um retriever existente para adicionar uma funcionalidade extra:
 * a transformação da consulta do usuário antes que a busca vetorial seja realizada.
 *
 * O objetivo principal é resolver o problema de perguntas vagas ou mal formuladas,
 * tornando a busca RAG (Retrieval-Augmented Generation) mais eficaz e precisa.
 */
public class TransformingRetriever implements Retriever<TextSegment> {

    // O retriever original (base) que executa a busca vetorial real no Qdrant.
    private final Retriever<TextSegment> retriever;

    // O componente que usa um LLM para reescrever a consulta do usuário.
    private final QueryTransformer queryTransformer;

    /**
     * Construtor que recebe as dependências necessárias (Injeção de Dependência).
     *
     * @param retriever O retriever subjacente que fará a busca no banco de dados vetorial.
     * @param queryTransformer O serviço que reescreverá a consulta do usuário.
     */
    public TransformingRetriever(Retriever<TextSegment> retriever, QueryTransformer queryTransformer) {
        this.retriever = retriever;
        this.queryTransformer = queryTransformer;
    }

    /**
     * Este método é o ponto central da classe e sobrescreve o comportamento padrão do Retriever.
     * Ele é chamado pela cadeia de RAG (ex: ConversationalRetrievalChain) toda vez que uma busca é necessária.
     *
     * O fluxo de execução é:
     * 1. Interceptar a consulta original do usuário.
     * 2. Passá-la para o QueryTransformer para otimização.
     * 3. Usar a consulta otimizada para buscar no retriever base.
     *
     * @param query O texto da pergunta original enviada pelo usuário.
     * @return Uma lista de segmentos de texto relevantes encontrados com base na consulta transformada.
     */
    @Override
    public List<TextSegment> findRelevant(String query) {
        // Passo 1: Usa o QueryTransformer para reescrever a consulta.
        // Por exemplo, "quero uma aula" pode se tornar "lição de inglês para iniciantes, tópicos de conversação".
        String transformedQuery = queryTransformer.execute(query);

        // Logs de depuração são úteis para entender o que o sistema está fazendo internamente.
        // Eles nos permitem ver como a consulta está sendo modificada em tempo de execução.
        System.out.println("[DEBUG] Consulta Original: '" + query + "'");
        System.out.println("[DEBUG] Consulta Transformada: '" + transformedQuery + "'");

        // Passo 2: Executa a busca real usando o retriever base, mas com a nova consulta otimizada.
        // A cadeia de RAG que chamou este método receberá apenas o resultado final,
        // sem saber que a consulta foi transformada.
        return retriever.findRelevant(transformedQuery);
    }
}
package com.tutor.service;

import dev.langchain4j.agent.tool.Tool; // <<<< ESTE É O IMPORT CORRETO
import dev.langchain4j.chain.ConversationalRetrievalChain;

/**
 * Define as "ferramentas" que nosso agente tutor pode usar.
 * Cada método anotado com @Tool representa uma capacidade distinta que o LLM pode escolher executar.
 */
public class TutorTools {

    private final ConversationalRetrievalChain ragChain;

    public TutorTools(ConversationalRetrievalChain ragChain) {
        this.ragChain = ragChain;
    }

    /**
     * Esta ferramenta é usada para buscar na base de conhecimento.
     * A descrição ajuda o LLM (o roteador) a decidir quando usar esta ferramenta.
     * @param query A pergunta do usuário a ser respondida usando a base de conhecimento.
     * @return A resposta sintetizada a partir do RAG.
     */
    @Tool("Use esta ferramenta para responder a perguntas que exigem conhecimento específico sobre gramática, vocabulário, ou lições de inglês. Exemplos de perguntas: 'o que é...', 'como se diz...', 'me dê uma aula sobre...', 'qual a diferença entre...'")
    public String useKnowledgeBase(String query) {
        System.out.println("\n[ROUTER] Decidiu usar a base de conhecimento (RAG).");
        return ragChain.execute(query);
    }
}
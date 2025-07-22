package com.tutor.service;

import com.tutor.config.ModelConfig;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

/**
 * Um agente de IA especializado em rotear a pergunta do usuário para a cadeia correta.
 * Ele não responde à pergunta, apenas a classifica.
 */
public interface RouterAgent {

    @SystemMessage("""
            Sua tarefa é classificar a pergunta de um usuário em uma de duas categorias: "rag" ou "conversa".
            - Use a categoria "rag" se a pergunta for sobre um tópico específico de inglês (gramática, vocabulário, regras, etc.).
            - Use a categoria "conversa" se a pergunta for uma saudação, um bate-papo geral ou uma pergunta simples que não requer conhecimento específico.
            
            Responda APENAS com a palavra "rag" ou a palavra "conversa". NADA MAIS.
            
            Exemplos:
            Usuário: o que é present perfect?
            rag
            
            Usuário: ola, tudo bem?
            conversa
            
            Usuário: me dê uma aula sobre preposições
            rag
            """)
    String route(String userMessage);

    /**
     * Método fábrica para criar uma instância do RouterAgent.
     */
    static RouterAgent create() {
        // Usamos um modelo com baixa temperatura para garantir consistência na classificação.
        ChatLanguageModel model = ModelConfig.createChatLanguageModel();
        return AiServices.create(RouterAgent.class, model);
    }
}
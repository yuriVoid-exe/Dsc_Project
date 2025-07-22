package com.tutor.rag;

import com.tutor.config.ModelConfig;
import dev.langchain4j.chain.Chain;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;

/**
 * Responsável por transformar a pergunta de um usuário em uma consulta de busca
 * mais otimizada para o sistema de recuperação de informações (RAG).
 */
public class QueryTransformer implements Chain<String, String> {

    private final Chain<String, String> chain;

    public QueryTransformer() {
        ChatLanguageModel model = ModelConfig.createChatLanguageModel();
        // Um prompt que instrui o LLM a agir como um especialista em buscas.
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Sua tarefa é transformar a pergunta de um usuário em 2 ou 3 consultas de busca semântica, separadas por vírgula. Retorne APENAS as consultas, nada mais.\n\n" +
                        "Usuário: {{it}}\n" +
                        "Consultas:"
        );
        this.chain = (userQuery) -> model.generate(promptTemplate.apply(userQuery).text());
    }

    @Override
    public String execute(String userQuery) {
        return this.chain.execute(userQuery);
    }
}
package com.tutor.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;

/**
 * Classe de configuração responsável por criar e fornecer a instância do modelo de linguagem (LLM).
 * ESTA VERSÃO FOI REATORADA PARA USAR A CLASSE CENTRAL 'AppConfig'.
 * Ela não carrega mais arquivos, apenas solicita as configurações necessárias,
 * seguindo o Princípio da Responsabilidade Única.
 */
public class ModelConfig {

    // Não há mais blocos estáticos ou variáveis 'properties' aqui.
    // Toda a lógica de carregamento foi movida para o AppConfig.

    /**
     * Cria e retorna uma instância configurada do modelo de chat com o Ollama.
     * As configurações são obtidas da classe central AppConfig.
     *
     * @return ChatLanguageModel (interface genérica do LangChain4j).
     */
    public static ChatLanguageModel createChatLanguageModel() {

        // 1. Pede as configurações para o AppConfig central.
        //    O AppConfig já lida com valores padrão e lança erros se chaves obrigatórias faltarem.
        String baseUrl = AppConfig.get("tutor.llm.ollama.base-url", "http://localhost:11434");
        String modelName = AppConfig.get("tutor.llm.ollama.model-name");
        int timeout = AppConfig.getInt("tutor.llm.ollama.timeout");
        double temperature = AppConfig.getDouble("tutor.llm.model.temperature");


        // 2. Loga as informações que serão usadas (ótimo para depuração).
        System.out.println("[CONFIG] Configurando o modelo LLM com os seguintes parâmetros:");
        System.out.println("  - Base URL: " + baseUrl);
        System.out.println("  - Model Name: " + modelName);
        System.out.println("  - Timeout: " + timeout + "s");
        System.out.println("  - Temperature: " + temperature);


        // 3. Constrói o objeto do modelo.
        //    O código de validação (if modelName == null) não é mais necessário aqui,
        //    pois o AppConfig.get(key) já garante que a propriedade existe,
        //    ou lança uma exceção clara.
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(timeout))
                .temperature(temperature)
                .build();
    }

    // O método getProperty() não é mais necessário aqui, pois
    // qualquer parte do código que precisar de uma propriedade
    // deve pedi-la diretamente ao AppConfig.
}
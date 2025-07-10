package com.tutor.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class ModelConfig {
    // Configurações padrão do modelo
    private static final String DEFAULT_MODEL = "llama3.2:1b";
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final double DEFAULT_TEMPERATURE = 0.5;

    private static ChatLanguageModel modelInstance;

    /**
     * Retorna uma instância singleton do modelo configurado com o Ollam.
     * @return ChatLanguageModel (interface genérica LangChain4j)
     */
    public static ChatLanguageModel getModel() {
        if (modelInstance == null) {
            synchronized (ModelConfig.class) {
                if (modelInstance == null) {
                    modelInstance = OllamaChatModel.builder()
                            .baseUrl(DEFAULT_BASE_URL)
                            .modelName(DEFAULT_MODEL)
                            .temperature(DEFAULT_TEMPERATURE)
                            .build();
                }
            }
        }
        return modelInstance;
    }

    /**
     * Permite redefinir o modelo em tempo de execução (se necessário).
     * @param modelName Nome do modelo Ollama (ex: llama3.2:1b)
     * @param temperature Temperatura para controlar a aleatoriedade
     */
    public static void reconfigure(String modelName, double temperature) {
        modelInstance = OllamaChatModel.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .modelName(DEFAULT_MODEL)
                .temperature(DEFAULT_TEMPERATURE)
                .build();
    }

    // Sobrecarga: reset para configurações padrão
    public static void resetToDefault() {
        reconfigure(DEFAULT_MODEL, DEFAULT_TEMPERATURE);
    }
}
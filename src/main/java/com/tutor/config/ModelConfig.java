package com.tutor.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

/**
 * Classe de configuração responsável por criar e fornecer a instância do modelo de linguagem (LLM).
 *
 * ESTA VERSÃO UTILIZA UMA ABORDAGEM DE CAMINHO DE ARQUIVO DIRETO.
 * Ela localiza o 'application.properties' de forma explícita a partir do diretório
 * raiz do projeto, tornando-a mais robusta em ambientes de desenvolvimento onde
 * a configuração do classpath pode ser inconsistente.
 */
public class ModelConfig {

    // Define o caminho relativo para o arquivo de configuração a partir da raiz do projeto.
    private static final String CONFIG_FILE_RELATIVE_PATH = "src/main/java/resources/application.properties";
    private static final Properties properties = new Properties();

    // Bloco estático para carregar as propriedades uma única vez.
    static {
        // 1. Determina o caminho absoluto para o arquivo de configuração.
        Path configPath = Paths.get(CONFIG_FILE_RELATIVE_PATH).toAbsolutePath();

        System.out.println("[INFO] Procurando arquivo de configuração em caminho absoluto: " + configPath);

        // 2. Verifica se o arquivo realmente existe no caminho esperado.
        if (Files.exists(configPath)) {
            // Foi utilizado o try-with-resources para garantir que o InputStream seja fechado.
            try (InputStream input = new FileInputStream(configPath.toFile())) {
                properties.load(input);
                System.out.println("[INFO] Arquivo de configuração '" + CONFIG_FILE_RELATIVE_PATH + "' carregado com sucesso.");
            } catch (IOException e) {
                // Se o arquivo existe mas não pode ser lido, é um erro fatal.
                throw new RuntimeException("ERRO CRÍTICO: Falha ao ler o arquivo de configuração existente em: " + configPath, e);
            }
        } else {
            // 3. Se o arquivo não existe, lança um erro claro e instrutivo.
            throw new RuntimeException("ERRO CRÍTICO: Arquivo de configuração não encontrado no caminho esperado: " + configPath + "\n" +
                    "Verifique se:\n" +
                    "  1. O arquivo 'application.properties' existe em 'src/main/resources'.\n" +
                    "  2. Você está executando a aplicação a partir do diretório raiz do projeto (ex: 'tutor-llm/').");
        }
    }

    /**
     * Cria e retorna uma instância configurada do modelo de chat com o Ollama.
     *
     * @return ChatLanguageModel (interface genérica do LangChain4j).
     */
    public static ChatLanguageModel createChatLanguageModel() {
        String baseUrl = properties.getProperty("tutor.llm.ollama.base-url", "http://localhost:11434");
        String modelName = properties.getProperty("tutor.llm.ollama.model-name");
        String timeoutStr = properties.getProperty("tutor.llm.ollama.timeout", "120");
        String tempStr = properties.getProperty("tutor.llm.model.temperature", "0.2");

        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A propriedade obrigatória 'tutor.llm.ollama.model-name' não foi encontrada ou está vazia no arquivo de configuração."
            );
        }

        System.out.println("[INFO] Configurando o modelo LLM com os seguintes parâmetros:");
        System.out.println("  - Base URL: " + baseUrl);
        System.out.println("  - Model Name: " + modelName);
        System.out.println("  - Timeout: " + timeoutStr + "s");
        System.out.println("  - Temperature: " + tempStr);

        long timeout = Long.parseLong(timeoutStr);
        double temperature = Double.parseDouble(tempStr);

        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(timeout))
                .temperature(temperature)
                .build();
    }

    /**
     * Método utilitário para obter uma propriedade específica.
     * @param key A chave da propriedade.
     * @return O valor da propriedade como String, ou null se não for encontrada.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
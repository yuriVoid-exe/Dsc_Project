package com.tutor.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Ponto central de configuração para toda a aplicação.
 * Carrega as propriedades de um arquivo .env (se existir) ou de um
 * application.properties no classpath, e as disponibiliza de forma estática.
 * Esta classe é o único lugar que deve saber como os arquivos de configuração são lidos.
 */
public final class AppConfig {
    private static final String EXTERNAL_CONFIG_FILE = ".env";
    private static final String CLASSPATH_CONFIG_FILE = "application.properties";
    private static final Properties properties;

    // Construtor privado para evitar instanciação.
    private AppConfig() {}

    // BLoco estático para carregar as propriedades uma única vez.
    static  {
        properties = new Properties();

        Path externalPath = Paths.get(EXTERNAL_CONFIG_FILE);
        if (Files.exists(externalPath)) {
            try (InputStream input = new FileInputStream(externalPath.toFile())) {
                properties.load(input);
                System.out.println("[CONFIG] Carregado arquivo de configuração externo: " + externalPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("[CONFIG] WARN: Erro ao ler o arquivo .env: " + e.getMessage());
            }
        }

        if (properties.isEmpty()) { // Apenas tenta o classpath se o .env não foi carregado
            try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_FILE)) {
                if (input != null) {
                    properties.load(input);
                    System.out.println("[CONFIG] Carregado arquivo de configuração do classpath: " + CLASSPATH_CONFIG_FILE);
                } else {
                    // Se nenhum arquivo de configuração for encontrado, é um problema.
                    throw new RuntimeException("ERRO CRÍTICO: Nenhum arquivo de configuração (.env ou application.properties) foi encontrado.");
                }
            } catch (IOException e) {
                throw new RuntimeException("ERRO CRÍTICO: Falha ao carregar " + CLASSPATH_CONFIG_FILE, e);
            }
        }
    }
    /**
     * Obtém uma propriedade de configuração como String.
     *
     * @param key A chave da propriedade.
     * @return O valor da propriedade.
     * @throws IllegalArgumentException se a chave não for encontrada.
     */
    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Propriedade de configuração obrigatória não encontrada: '" + key + "'");
        }
        return value;
    }

    /**
     * Obtém uma propriedade de configuração como String, com um valor padrão.
     *
     * @param key          A chave da propriedade.
     * @param defaultValue O valor a ser retornado se a chave não for encontrada.
     * @return O valor da propriedade ou o valor padrão.
     */
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Obtém uma propriedade de configuração como um inteiro.
     *
     * @param key A chave da propriedade.
     * @return O valor da propriedade como int.
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Obtém uma propriedade de configuração como um double.
     *
     * @param key A chave da propriedade.
     * @return O valor da propriedade como double.
     */
    public static double getDouble(String key) {
        return Double.parseDouble(get(key));
    }
}

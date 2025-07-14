package com.tutor.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2QuantizedEmbeddingModel;

 /**
 * Classe de configuração responsável por criar e fornecer a instância do Modelo de Embedding.
 * O modelo de embedding é o componente que transforma texto (palavras, frases, parágrafos)
 * em uma representação numérica (vetores), permitindo a busca por similaridade semântica.
 */
 public class EmbeddingConfig {
     private static EmbeddingModel embeddingModel;

     public static EmbeddingModel createEmbeddingModel() {
         if(embeddingModel == null) {
             System.out.println("[INFO] Inicializando o modelo de embedding (AllMiniLmL6V2)...");
             System.out.println("[INFO] Isso pode levar alguns instantes na primeira execução, pois o modelo será baixado.");

             // A classe AllMiniLmL6V2EmbeddingModel do LangChain4j cuida de tudo:
             // 1. Verifica se o modelo já está em cache local.
             // 2. Se não estiver, baixa o modelo (cerca de 90MB) e o armazena em um diretório de cache do usuário.
             // 3. Carrega o modelo na memória para uso.

             embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
             System.out.println("[INFO] Modelo de embedding carregado com sucesso.");
         }
      return embeddingModel;
     }
 }

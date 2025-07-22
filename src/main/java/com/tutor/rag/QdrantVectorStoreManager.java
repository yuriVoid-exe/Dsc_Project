package com.tutor.rag;

import com.tutor.config.AppConfig;
import com.tutor.config.EmbeddingConfig;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

import java.util.List;
import java.util.concurrent.ExecutionException;

public final class QdrantVectorStoreManager {

    private QdrantVectorStoreManager() {}

    private static final String COLLECTION_NAME = AppConfig.get("qdrant.collection.name");

    /**
     * Ponto de entrada principal. Garante que o Qdrant esteja pronto e retorna
     * um EmbeddingStore funcional para o retriever.
     * @return Uma instância de EmbeddingStore conectada ao Qdrant.
     */
    public static EmbeddingStore<TextSegment> getEmbeddingStore() {
        String host = AppConfig.get("qdrant.host");
        int port = AppConfig.getInt("qdrant.port");

        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(host, port, false).build()
        );

        try {
            List<String> collectionNames = client.listCollectionsAsync().get();
            boolean collectionExists = collectionNames.contains(COLLECTION_NAME);

            if (!collectionExists) {
                // A coleção não existe, então a criamos e disparamos a ingestão de dados.
                createCollectionAndIngestData(client);
            } else {
                System.out.println("[QDRANT] Conectado à coleção existente: '" + COLLECTION_NAME + "'.");
            }
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            throw new RuntimeException("Falha ao inicializar a coleção no Qdrant", e);
        }

        // Retorna uma instância do EmbeddingStore para uso pelo Retriever.
        return QdrantEmbeddingStore.builder()
                .collectionName(COLLECTION_NAME)
                .client(client)
                .build();
    }

    /**
     * Cria a coleção no Qdrant e, em seguida, executa o processo de ingestão.
     * Este método é chamado apenas uma vez.
     */
    private static void createCollectionAndIngestData(QdrantClient client) throws ExecutionException, InterruptedException {
        // 1. Cria a coleção
        System.out.println("[QDRANT] Coleção '" + COLLECTION_NAME + "' não encontrada. Criando...");
        int vectorDimension = 384; // Dimensão para o modelo all-MiniLM-L6-v2
        client.createCollectionAsync(COLLECTION_NAME,
                VectorParams.newBuilder().setDistance(Distance.Cosine).setSize(vectorDimension).build()
        ).get();
        System.out.println("[QDRANT] Coleção criada com sucesso. Iniciando ingestão de dados...");

        // 2. Prepara o Ingestor
        EmbeddingStore<TextSegment> embeddingStoreForIngestion = QdrantEmbeddingStore.builder()
                .collectionName(COLLECTION_NAME)
                .client(client)
                .build();
        EmbeddingModel embeddingModel = EmbeddingConfig.createEmbeddingModel();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentLoader.createRecursiveSplitter())
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStoreForIngestion)
                .build();

        // 3. Carrega os documentos brutos
        List<Document> documents = DocumentLoader.loadDocuments("data");
        if (documents.isEmpty()) {
            System.out.println("[INGEST] Nenhum documento encontrado para ingestão.");
            return;
        }

        // 4. Executa a ingestão
        // O Ingestor agora lida com todo o processo de forma segura.
        ingestor.ingest(documents);
        System.out.println("[INGEST] Ingestão de " + documents.size() + " documentos concluída.");
    }
}
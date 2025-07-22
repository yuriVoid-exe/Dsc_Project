package com.tutor.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por carregar documentos brutos de uma fonte de dados e
 * por fornecer uma estratégia de divisão (splitting).
 */
public final class DocumentLoader {

    private DocumentLoader() {} // Classe utilitária, não deve ser instanciada.

    /**
     * Carrega todos os documentos suportados (.txt, .pdf) de um diretório.
     * Retorna os documentos brutos, sem dividi-los, prontos para o Ingestor.
     * @param directoryPath O caminho para o diretório 'data/'.
     * @return Uma lista de objetos Document.
     */
    public static List<Document> loadDocuments(String directoryPath) {
        System.out.println("[DOCLOADER] Carregando documentos brutos de: " + directoryPath);
        DocumentParser pdfParser = new ApachePdfBoxDocumentParser();
        DocumentParser txtParser = new TextDocumentParser();
        List<Document> documents = new ArrayList<>();
        Path dataDir = Paths.get(directoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir)) {
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".pdf")) {
                    documents.add(FileSystemDocumentLoader.loadDocument(filePath, pdfParser));
                } else if (fileName.endsWith(".txt")) {
                    documents.add(FileSystemDocumentLoader.loadDocument(filePath, txtParser));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler o diretório de dados: " + directoryPath, e);
        }
        System.out.println("[DOCLOADER] Total de documentos brutos carregados: " + documents.size());
        return documents;
    }

    /**
     * Cria e retorna uma instância do DocumentSplitter recursivo.
     * Esta estratégia é ideal para manter o contexto semântico ao dividir o texto.
     * @return Uma instância de DocumentSplitter.
     */
    public static DocumentSplitter createRecursiveSplitter() {
        // Hiperparâmetros da divisão
        int maxSegmentSizeInChars = 1000; // Dividir por caracteres é mais determinístico
        int maxOverlapInChars = 100;
        return DocumentSplitters.recursive(maxSegmentSizeInChars, maxOverlapInChars);
    }
}
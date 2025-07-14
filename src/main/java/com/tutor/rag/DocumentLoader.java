package com.tutor.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por carregar documentos de um diretório, processá-los e dividi-los em segmentos (chunks).
 * Esta classe é um componente chave do pipeline de ingestão de dados para o sistema RAG.
 */
public class DocumentLoader {
    /**
     * Carrega e divide documentos de um diretório especificado.
     * Suporta arquivos .txt e .pdf.
     *
     * @param directoryPath O caminho para o diretório 'data/'.
     * @return Uma lista de TextSegment, pronta para ser 'embeddada'.
     */
    public static List<TextSegment> loadAndSplitDocuments(String directoryPath) {
        System.out.println("[INFO] Iniciando o carregamento e processamento de documentos de: \" + directoryPath");

        // Define os parsers para cada tipo de arquivo.
        DocumentParser pdfParser = new ApachePdfBoxDocumentParser();
        DocumentParser txtParser = new TextDocumentParser();

        List<Document> allDocuments = new ArrayList<>();
        Path dataDir = Paths.get(directoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir)) {
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString().toLowerCase();
                System.out.println("[INFO] Encontrado arquivo: \" + fileName");

                Document document = null;
                if (fileName.endsWith(".pdf")) {
                    document = FileSystemDocumentLoader.loadDocument(filePath, pdfParser);
                    System.out.println("[INFO]   -> Processado como PDF.");
                } else if (fileName.endsWith(".txt")) {
                    document = FileSystemDocumentLoader.loadDocument(filePath, txtParser);
                    System.out.println("[INFO]   -> Processado como TXT.");
                } else {
                    System.out.println("[WARN]   -> Arquivo ignorado. Tipo não suportado: " + fileName);
                }

                if (document != null) {
                    allDocuments.add(document);
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao ler o diretório de dados: " + directoryPath);
            throw new RuntimeException(e);
        }

        if (allDocuments.isEmpty()) {
            System.out.println("[WARN] Nenhum documento suportado foi encontrado no diretório. A base de conhecimento estará vazia.");
            return new ArrayList<>();
        }

        int maxSegmentSize = 300;
        int maxOverlap = 50;
        var splitter = DocumentSplitters.recursive(maxSegmentSize, maxOverlap);

        List<TextSegment> segments = splitter.splitAll(allDocuments);
        System.out.println("[INFO] Documentos divididos em " + segments.size() + " segmentos.");
        return segments;
    }
}
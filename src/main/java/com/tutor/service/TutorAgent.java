package com.tutor.service;

/**
 * A interface que define nosso Agente Tutor.
 * O LangChain4j usará esta interface para criar um agente de conversação.
 */
public interface TutorAgent {

    String chat(String userMessage);
}
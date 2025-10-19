package com.alugaserra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * Serviço responsável por interagir com o provedor de armazenamento de arquivos (AWS S3).
 */
@Service
public class StorageService {

    @Autowired
    private S3Presigner s3Presigner;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    /**
     * Gera uma URL pré-assinada para permitir o upload de um arquivo diretamente para o S3.
     * @param fileName O nome original do arquivo.
     * @return A URL pré-assinada para o upload.
     */
    public String generatePresignedUrl(String fileName) {
        // Gera um nome de arquivo único para evitar colisões no S3
        String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;

        // Monta a requisição para o S3
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();

        // Monta a requisição de pré-assinatura
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // A URL será válida por 5 minutos
                .putObjectRequest(objectRequest)
                .build();

        // Gera e retorna a URL
        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
}


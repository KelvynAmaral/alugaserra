package com.alugaserra.controller;

import com.alugaserra.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller para gerenciar operações de armazenamento de arquivos, como a geração de URLs para upload.
 */
@RestController
@RequestMapping("/api/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    /**
     * Gera uma URL pré-assinada para upload.
     * O frontend deve chamar este endpoint antes de tentar enviar uma foto para o S3.
     * @param fileName O nome do arquivo que será enviado (ex: "foto-da-sala.jpg").
     * @return Um JSON contendo a URL de upload temporária.
     */
    @PostMapping("/upload-url")
    public ResponseEntity<Map<String, String>> generateUploadUrl(@RequestParam String fileName) {
        String url = storageService.generatePresignedUrl(fileName);

        // Retorna a URL em um formato JSON: { "uploadUrl": "http://..." }
        Map<String, String> response = Map.of("uploadUrl", url);

        return ResponseEntity.ok(response);
    }
}


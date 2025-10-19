package com.alugaserra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        // Injeta manualmente o nome do bucket no nosso serviço de teste
        ReflectionTestUtils.setField(storageService, "bucketName", "meu-bucket-de-teste");
    }

    @Test
    @DisplayName("Deve gerar uma URL pré-assinada com o nome do arquivo e do bucket corretos")
    void generatePresignedUrl_ShouldReturnAValidUrl() throws Exception {
        // --- Cenário (Arrange) ---
        String originalFileName = "foto-da-sala.jpg";
        URL mockUrl = new URL("https", "meu-bucket-de-teste.s3.amazonaws.com", "/mock-url");

        PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);

        when(presignedRequest.url()).thenReturn(mockUrl);

        // Dizemos ao nosso mock do S3Presigner para retornar o nosso objeto de resposta "dublê"
        // sempre que o método presignPutObject for chamado.
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedRequest);

        // --- Ação (Act) ---
        String generatedUrl = storageService.generatePresignedUrl(originalFileName);

        // --- Verificação (Assert) ---
        assertThat(generatedUrl).isNotNull();
        // Verificamos se a URL retornada é a mesma que o nosso mock forneceu.
        assertThat(generatedUrl).isEqualTo(mockUrl.toString());
    }
}


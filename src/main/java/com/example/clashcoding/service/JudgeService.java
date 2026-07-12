package com.example.clashcoding.service;

import com.example.clashcoding.dto.ExecuteDTO;
import com.example.clashcoding.dto.FileDTO;
import com.example.clashcoding.response.ExecuteResponse;
import com.example.clashcoding.response.RunResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class JudgeService {
    @Value("${judge.url}")
    private String judgeURL;

    private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public ExecuteResponse execute(String code, String language, String input) throws Exception{

        ExecuteDTO dto = new ExecuteDTO();
        dto.setLanguage(language);
        dto.setVersion("*");
        dto.setFiles(List.of(new FileDTO(code)));
        dto.setStdin(input);

        String json = mapper.writeValueAsString(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(judgeURL))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), ExecuteResponse.class);
    }
}

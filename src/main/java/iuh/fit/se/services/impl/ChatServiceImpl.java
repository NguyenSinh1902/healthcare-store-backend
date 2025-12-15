package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.gemini.GeminiRequest;
import iuh.fit.se.dtos.gemini.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ChatServiceImpl {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ChatServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    public String chatWithGemini(String userMessage) {
        String finalUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .toUriString();

        GeminiRequest request = new GeminiRequest(userMessage);

        try {
            GeminiResponse response = restTemplate.postForObject(finalUrl, request, GeminiResponse.class);

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
            return "AI đang bận, vui lòng thử lại sau.";

        } catch (HttpClientErrorException e) {
            System.err.println("Google API Error: " + e.getResponseBodyAsString());
            return "Lỗi kết nối AI: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }
}
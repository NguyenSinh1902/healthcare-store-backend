package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.gemini.GeminiRequest;
import iuh.fit.se.dtos.gemini.GeminiResponse;
import iuh.fit.se.entities.product.Product; // Import Product
import iuh.fit.se.repositories.ProductRepository; // Import Repo
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository; // Inject Repo

    // Inject ProductRepository vào Constructor
    public ChatServiceImpl(ProductRepository productRepository) {
        this.restTemplate = new RestTemplate();
        this.productRepository = productRepository;
    }

    private String getProductContext() {
        //Lấy danh sách sản phẩm (đang bán) từ DB
        //Nếu shop có hàng nghìn món thì chỉ nên lấy tên, giá và số lượng để tiết kiệm token
        List<Product> products = productRepository.findByActiveTrue();

        if (products.isEmpty()) {
            return "Hiện tại cửa hàng chưa có sản phẩm nào.";
        }

        // Tạo chuỗi văn bản mô tả danh sách sản phẩm
        return products.stream()
                .map(p -> String.format("- %s (Giá: %.0f VND, Tồn kho: %d, Thương hiệu: %s)",
                        p.getNameProduct(), p.getPrice(), p.getStockQuantity(), p.getBrand()))
                .collect(Collectors.joining("\n"));
    }

    public String chatWithGemini(String userMessage) {
        String finalUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .toUriString();

        // Lấy dữ liệu sản phẩm từ DB
        String productData = getProductContext();

        // Tạo Prompt (Kịch bản cho AI)
        String systemInstruction = String.format("""
            Bạn là nhân viên tư vấn nhiệt tình của shop HealthCare.
            Dưới đây là danh sách sản phẩm hiện có tại shop:
            %s
            
            Quy tắc trả lời:
            1. Chỉ trả lời dựa trên thông tin sản phẩm ở trên.
            2. Nếu khách hỏi sản phẩm không có trong danh sách, hãy báo là shop chưa kinh doanh.
            3. Trả lời ngắn gọn, thân thiện, dùng biểu tượng cảm xúc (emoji).
            4. Luôn báo giá chính xác theo danh sách.
            
            Câu hỏi của khách: %s
            """, productData, userMessage);

        GeminiRequest request = new GeminiRequest(systemInstruction);

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
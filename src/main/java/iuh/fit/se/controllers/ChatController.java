package iuh.fit.se.controllers;

import iuh.fit.se.services.impl.ChatServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Nhớ cấu hình cái này cho khớp với SecurityConfig
public class ChatController {

    private final ChatServiceImpl chatService;

    public ChatController(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askGemini(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");

        String answer = chatService.chatWithGemini(question);

        return ResponseEntity.ok(Map.of("answer", answer));
    }
}
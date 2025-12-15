package iuh.fit.se.dtos.gemini;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    private List<Content> contents = new ArrayList<>();

    public GeminiRequest(String prompt) {
        this.contents.add(new Content(prompt));
    }

    // Getters Setters
    public List<Content> getContents() { return contents; }

    // Inner classes
    public static class Content {
        private List<Part> parts = new ArrayList<>();

        public Content(String text) {
            this.parts.add(new Part(text));
        }
        public List<Part> getParts() { return parts; }
    }

    public static class Part {
        private String text;
        public Part(String text) { this.text = text; }
        public String getText() { return text; }
    }
}
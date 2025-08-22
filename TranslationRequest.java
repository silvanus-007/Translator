package webapp.model;

import java.util.List;

public class TranslationRequest {
    private String sessionId;
    private String originalFileName;
    private String fileType;
    private List<String> originalContent;
    private List<String> translatedContent;
    private String sourceLanguage;
    private String targetLanguage;

    public TranslationRequest() {}

    public TranslationRequest(String originalFileName, String fileType, 
                            List<String> originalContent, List<String> translatedContent) {
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.originalContent = originalContent;
        this.translatedContent = translatedContent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<String> getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(List<String> originalContent) {
        this.originalContent = originalContent;
    }

    public List<String> getTranslatedContent() {
        return translatedContent;
    }

    public void setTranslatedContent(List<String> translatedContent) {
        this.translatedContent = translatedContent;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
package webapp.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import webapp.model.TranslationRequest;
import webapp.service.FileGenerationService;
import webapp.service.FileParsingService;

@Controller
@RequestMapping("/")
public class FileTranslatorController {

    private static final Logger logger = LoggerFactory.getLogger(FileTranslatorController.class);

    @Autowired
    private FileParsingService fileParsingService;

    @Autowired
    private FileGenerationService fileGenerationService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            String fileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(fileName);
            
            if (!isValidFileType(fileExtension)) {
                response.put("success", false);
                response.put("message", "Unsupported file format. Please upload .docx, .pptx, .xlsx, .idml, .md, .rtf, or .txt files");
                return ResponseEntity.badRequest().body(response);
            }

            List<String> extractedContent = fileParsingService.parseFile(file, fileExtension);
            
            response.put("success", true);
            response.put("fileName", fileName);
            response.put("fileType", fileExtension);
            response.put("content", extractedContent);
            response.put("message", "File uploaded and parsed successfully");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing file upload", e);
            response.put("success", false);
            response.put("message", "Error processing file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/translate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processTranslation(@RequestBody TranslationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Store translation data for file generation
            request.setSessionId(generateSessionId());
            
            response.put("success", true);
            response.put("sessionId", request.getSessionId());
            response.put("message", "Translation processed successfully");
            
            
            translationCache.put(request.getSessionId(), request);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing translation", e);
            response.put("success", false);
            response.put("message", "Error processing translation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/download/{sessionId}")
    public ResponseEntity<byte[]> downloadTranslatedFile(@PathVariable String sessionId) {
        try {
            TranslationRequest request = translationCache.get(sessionId);
            if (request == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileBytes = fileGenerationService.generateTranslatedFile(request);
            
            String fileName = "translated_" + request.getOriginalFileName();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(fileBytes.length);
            
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error generating download file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

   
    private static final Map<String, TranslationRequest> translationCache = new HashMap<>();

    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + Math.random();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidFileType(String extension) {
        return extension.equals("docx") || extension.equals("pptx") || 
               extension.equals("xlsx") || extension.equals("idml") || 
               extension.equals("md") || extension.equals("rtf") || 
               extension.equals("txt");
    }
}

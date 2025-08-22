package webapp.service;


import webapp.model.*;
import webapp.util.OkapiUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FileGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(FileGenerationService.class);

    public byte[] generateTranslatedFile(TranslationRequest request) throws IOException {
        logger.info("Generating translated file for: {}", request.getOriginalFileName());
        
        String fileExtension = getFileExtension(request.getOriginalFileName());
        
        switch (fileExtension.toLowerCase()) {
            case "docx":
                return generateDocx(request.getTranslatedContent());
            case "txt":
            case "md":
                return generateTextFile(request.getTranslatedContent());
            case "pptx":
                return generatePptx(request.getTranslatedContent());
            case "xlsx":
                return generateXlsx(request.getTranslatedContent());
            case "rtf":
                return OkapiUtil.generateRtf(request.getOriginalContent(), request.getTranslatedContent());
            case "idml":
                return OkapiUtil.generateIdml(request.getOriginalContent(), request.getTranslatedContent());
            default:
                throw new IllegalArgumentException("Unsupported file format for generation: " + fileExtension);
        }
    }

    private byte[] generateDocx(List<String> translatedContent) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            for (String content : translatedContent) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(content);
            }
            
            document.write(out);
            return out.toByteArray();
        }
    }

    private byte[] generateTextFile(List<String> translatedContent) {
        StringBuilder sb = new StringBuilder();
        for (String content : translatedContent) {
            sb.append(content).append(System.lineSeparator());
        }
        return sb.toString().getBytes();
    }

    private byte[] generatePptx(List<String> translatedContent) throws IOException {
        try (XMLSlideShow slideShow = new XMLSlideShow();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
           
            for (int i = 0; i < translatedContent.size(); i++) {
                XSLFSlide slide = slideShow.createSlide();
                XSLFTextShape textShape = slide.createTextBox();
                textShape.setText(translatedContent.get(i));
                textShape.setAnchor(new java.awt.geom.Rectangle2D.Double(50, 50, 600, 400));
            }
            
            slideShow.write(out);
            return out.toByteArray();
        }
    }

    private byte[] generateXlsx(List<String> translatedContent) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            XSSFSheet sheet = workbook.createSheet("Translated Content");
            
            for (int i = 0; i < translatedContent.size(); i++) {
                XSSFRow row = sheet.createRow(i);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue(translatedContent.get(i));
            }
            
            sheet.autoSizeColumn(0);
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
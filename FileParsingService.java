package webapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import webapp.util.OkapiUtil;

@Service
public class FileParsingService {

    private static final Logger logger = LoggerFactory.getLogger(FileParsingService.class);

    public List<String> parseFile(MultipartFile file, String fileExtension) throws IOException {
        logger.info("Parsing file: {} with extension: {}", file.getOriginalFilename(), fileExtension);
        
        List<String> content = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            switch (fileExtension.toLowerCase()) {
                case "docx":
                    content = parseDocx(inputStream);
                    break;
                case "pptx":
                    content = parsePptx(inputStream);
                    break;
                case "xlsx":
                    content = parseXlsx(inputStream);
                    break;
                case "txt":
                case "md":
                    content = parseTextFile(inputStream);
                    break;
                case "rtf":
                    content = parseRtf(inputStream);
                    break;
                case "idml":
                    content = parseIdml(inputStream);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
            }
        }
        
        logger.info("Extracted {} content segments", content.size());
        return content;
    }

    private List<String> parseDocx(InputStream inputStream) throws IOException {
        List<String> content = new ArrayList<>();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText().trim();
                if (!text.isEmpty()) {
                    content.add(text);
                }
            }
        }
        return content;
    }

    private List<String> parsePptx(InputStream inputStream) throws IOException {
        List<String> content = new ArrayList<>();
        try (XMLSlideShow slideShow = new XMLSlideShow(inputStream)) {
            for (XSLFSlide slide : slideShow.getSlides()) {
                for (XSLFTextShape shape : slide.getPlaceholders()) {
                    String text = shape.getText().trim();
                    if (!text.isEmpty()) {
                        content.add(text);
                    }
                }
            }
        }
        return content;
    }

    private List<String> parseXlsx(InputStream inputStream) throws IOException {
        List<String> content = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    StringBuilder rowText = new StringBuilder();
                    for (Cell cell : row) {
                        if (cell != null) {
                            String cellValue = cell.toString().trim();
                            if (!cellValue.isEmpty()) {
                                if (rowText.length() > 0) {
                                    rowText.append(" | ");
                                }
                                rowText.append(cellValue);
                            }
                        }
                    }
                    if (rowText.length() > 0) {
                        content.add(rowText.toString());
                    }
                }
            }
        }
        return content;
    }

    private List<String> parseTextFile(InputStream inputStream) throws IOException {
        List<String> content = new ArrayList<>();
        try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    content.add(line);
                }
            }
        }
        return content;
    }

    private List<String> parseRtf(InputStream inputStream) throws IOException {
       
        return OkapiUtil.extractTextFromRtf(inputStream);
    }

    private List<String> parseIdml(InputStream inputStream) throws IOException {
       
        return OkapiUtil.extractTextFromIdml(inputStream);
    }
}

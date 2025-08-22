package webapp.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.idml.IDMLFilter;
import net.sf.okapi.filters.plaintext.PlainTextFilter;

public class OkapiUtil {

    private static final Logger logger = LoggerFactory.getLogger(OkapiUtil.class);

    
    public static List<String> extractTextFromRtf(InputStream inputStream) {
        List<String> content = new ArrayList<>();
        try {
            
            PlainTextFilter filter = new PlainTextFilter();
            RawDocument rawDoc = new RawDocument(inputStream, "UTF-8", LocaleId.ENGLISH);
            
            filter.open(rawDoc);
            
            while (filter.hasNext()) {
                Event event = filter.next();
                if (event.getEventType() == EventType.TEXT_UNIT) {
                    ITextUnit textUnit = event.getTextUnit();
                    if (textUnit != null && textUnit.getSource() != null) {
                        String text = textUnit.getSource().toString();
                        if (text != null && !text.trim().isEmpty()) {
                            content.add(text);
                        }
                    }
                }
            }
            
            filter.close();
            
            if (content.isEmpty()) {
                content.add("RTF content processed (RTF filter deprecated - consider using Apache Tika)");
            }

        } catch (Exception e) {
            logger.error("Error extracting RTF content", e);
            content.add("Error extracting RTF content: " + e.getMessage());
        }
        return content;
    }

    public static List<String> extractTextFromIdml(InputStream inputStream) {
        List<String> content = new ArrayList<>();
        try {
            IDMLFilter filter = new IDMLFilter();
            RawDocument rawDoc = new RawDocument(inputStream, "UTF-8", LocaleId.ENGLISH);
            
            filter.open(rawDoc);
            
            while (filter.hasNext()) {
                Event event = filter.next();
                if (event.getEventType() == EventType.TEXT_UNIT) {
                    ITextUnit textUnit = event.getTextUnit();
                    if (textUnit != null && textUnit.getSource() != null) {
                        String text = textUnit.getSource().toString();
                        if (text != null && !text.trim().isEmpty()) {
                            content.add(text);
                        }
                    }
                }
            }
            
            filter.close();

        } catch (Exception e) {
            logger.error("Error extracting IDML content", e);
            content.add("Error extracting IDML content: " + e.getMessage());
        }
        return content;
    }
    public static byte[] generateRtf(List<String> originalContent, List<String> translatedContent) {
        try {
            StringBuilder rtfContent = new StringBuilder();
            
            rtfContent.append("{\\rtf1\\ansi\\deff0 ");
            rtfContent.append("{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}}");
            rtfContent.append("\\f0\\fs24 ");

            
            for (String content : translatedContent) {
                if (content != null && !content.trim().isEmpty()) {
                   
                    String escapedContent = content.replace("\\", "\\\\")
                                                  .replace("{", "\\{")
                                                  .replace("}", "\\}")
                                                  .replace("\n", "\\par ");
                    rtfContent.append(escapedContent).append("\\par ");
                }
            }

            rtfContent.append("}");
            return rtfContent.toString().getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("Error generating RTF content", e);
            return ("Error generating RTF: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        }
    }

   
    public static byte[] generateIdml(List<String> originalContent, List<String> translatedContent) {
        try {
           
            StringBuilder idmlContent = new StringBuilder();
            
            idmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            idmlContent.append("<idPkg:Document xmlns:idPkg=\"http://ns.adobe.com/AdobeInDesign/idml/1.0/packaging\">\n");
            idmlContent.append("  <Story>\n");
            idmlContent.append("    <StoryPreference OpticalMarginAlignment=\"false\"/>\n");
            idmlContent.append("    <InCopyExportOption IncludeGraphicProxies=\"true\"/>\n");
            idmlContent.append("    <ParagraphStyleRange>\n");
            
            for (String content : translatedContent) {
                if (content != null && !content.trim().isEmpty()) {
                    
                    String escapedContent = content.replace("&", "&amp;")
                                                  .replace("<", "&lt;")
                                                  .replace(">", "&gt;")
                                                  .replace("\"", "&quot;")
                                                  .replace("'", "&apos;");
                    idmlContent.append("      <Content>").append(escapedContent).append("</Content>\n");
                }
            }
            
            idmlContent.append("    </ParagraphStyleRange>\n");
            idmlContent.append("  </Story>\n");
            idmlContent.append("</idPkg:Document>\n");
            
            return idmlContent.toString().getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("Error generating IDML content", e);
            return ("Error generating IDML: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        }
    }
    
    
    public static List<String> extractText(InputStream inputStream, String fileType) {
        if (fileType == null) {
            return new ArrayList<>();
        }
        
        switch (fileType.toLowerCase()) {
            case "rtf":
                return extractTextFromRtf(inputStream);
            case "idml":
                return extractTextFromIdml(inputStream);
            default:
                logger.warn("Unsupported file type: {}", fileType);
                List<String> content = new ArrayList<>();
                content.add("Unsupported file type: " + fileType);
                return content;
        }
    }
}
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Document Translator</title>
<style>
* {
margin: 0;
padding: 0;
box-sizing: border-box;
font-family: "Segoe UI", Arial, sans-serif;
}

body {
min-height: 100vh;
display: flex;
justify-content: center;
align-items: center;
background: linear-gradient(135deg, #1e3c72, #2a5298);
color: #333;
}

.container {
background: #fff;
padding: 40px;
border-radius: 15px;
box-shadow: 0px 10px 25px rgba(0, 0, 0, 0.2);
width: 100%;
max-width: 600px;
text-align: center;
}

.container h1 {
font-size: 28px;
font-weight: bold;
margin-bottom: 20px;
color: #1e3c72;
}

.upload-box {
border: 2px dashed #1e3c72;
border-radius: 10px;
padding: 30px;
margin: 20px 0;
transition: 0.3s;
}

.upload-box:hover {
background: #f0f4ff;
border-color: #2a5298;
}

input[type="file"] {
margin: 10px 0;
padding: 8px;
border: 1px solid #ddd;
border-radius: 5px;
width: 100%;
}

.btn {
display: inline-block;
padding: 12px 20px;
background: #1e3c72;
color: #fff;
border: none;
border-radius: 8px;
cursor: pointer;
font-size: 16px;
transition: 0.3s;
margin: 10px 5px;
}

.btn:hover {
background: #2a5298;
}

.btn:disabled {
background: #ccc;
cursor: not-allowed;
}

.extract {
border: 1px solid #e0e0e0;
border-radius: 10px;
padding: 20px;
margin: 20px 0;
text-align: left;
}

.extract h2 {
color: #1e3c72;
margin-bottom: 15px;
font-size: 20px;
text-align: center;
}

#extractedTextArea {
width: 100%;
min-height: 150px;
padding: 12px;
border: 1px solid #ddd;
border-radius: 8px;
font-family: Arial, sans-serif;
font-size: 14px;
line-height: 1.5;
resize: vertical;
}

.error-message {
background: #ffebee;
color: #c62828;
padding: 15px;
border-radius: 8px;
margin: 15px 0;
border: 1px solid #ffcdd2;
}

.success-message {
background: #e8f5e8;
color: #2e7d32;
padding: 15px;
border-radius: 8px;
margin: 15px 0;
border: 1px solid #c8e6c9;
}

.file-info {
background: #f5f5f5;
padding: 10px;
border-radius: 5px;
margin: 10px 0;
font-size: 14px;
color: #666;
}

.button-group {
margin: 20px 0;
}

@media (max-width: 500px) {
.container {
padding: 25px;
max-width: 90%;
}
.container h1 {
font-size: 22px;
}
}
button{
    padding: 12px 24px;
    background-color: #1e3c72;
    color: wheat;
    font-size: 16px;
    font-weight: bold;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;

}
button:hover{
    background-color: #0056b3;
}

</style>
<script>
function clearText() {
    document.getElementById('extractedTextArea').value = '';
}

function copyText() {
    var textArea = document.getElementById('extractedTextArea');
    textArea.select();
    textArea.setSelectionRange(0, 99999); // For mobile devices
    
    try {
        document.execCommand('copy');
        alert('Text copied to clipboard!');
    } catch (err) {
        alert('Unable to copy text. Please select and copy manually.');
    }
}

// Auto-resize textarea
function autoResize() {
    var textArea = document.getElementById('extractedTextArea');
    textArea.style.height = 'auto';
    textArea.style.height = Math.max(150, textArea.scrollHeight) + 'px';
}

window.onload = function() {
    autoResize();
}
</script>
</head>
<body>
<div class="container">
<h1>📄 Document Translator</h1>

<!-- Display error message if any -->
<% String errorMessage = (String) request.getAttribute("errorMessage");
   if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="error-message">
        <strong>Error:</strong> <%= errorMessage %>
    </div>
<% } %>

<!-- Display success message -->
<% Boolean success = (Boolean) request.getAttribute("success");
   if (success != null && success) { %>
    <div class="success-message">
        <strong>Success:</strong> Text extracted successfully!
    </div>
<% } %>

<form action="upload" method="post" enctype="multipart/form-data">
    <div class="upload-box">
        <p>Select a document to upload</p>
        <input type="file" name="file" accept=".txt,.html,.htm,.pdf,.doc,.docx,.xlsx,.pptx,.rtf" required>
        <div class="file-info">
            Supported formats: TXT, HTML, PDF, DOC, DOCX, XLSX, PPTX, RTF
        </div>
    </div>
    
    <div class="extract">
        <h2>Extracted Text</h2>
        <textarea id="extractedTextArea" placeholder="Extracted text will appear here..." oninput="autoResize()"><% 
            String extractedText = (String) request.getAttribute("extractedText");
            if (extractedText != null && !extractedText.isEmpty()) {
                out.print(extractedText.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;"));
            }
        %></textarea>
        
        <div class="button-group">
            <button type="button" class="btn" onclick="copyText()" 
                    <%= (extractedText == null || extractedText.isEmpty()) ? "disabled" : "" %>>
                📋 Copy Text
            </button>
            <button type="button" class="btn" onclick="clearText()"
                    <%= (extractedText == null || extractedText.isEmpty()) ? "disabled" : "" %>>
                🗑 Clear
            </button>
            
            <button onclick="DownloadFile()">DOWNLOAD FILE</button>
        </div>
    </div>
    
    <button type="submit" class="btn">📤 Upload & Extract Text</button>
</form>
</div>
</body>
</html>
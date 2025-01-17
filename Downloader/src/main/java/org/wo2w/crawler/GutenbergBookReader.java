package org.wo2w.crawler;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;


public class GutenbergBookReader implements BookReader {

    @Override
    public InputStream fetchBookContent(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        URLConnection connection = url.openConnection();
        return connection.getInputStream(); // Devuelve el contenido como InputStream
    }

    @Override
    public void downloadFile(String fileURL, Storage storage, String lang) throws IOException {

        InputStream contentStream = fetchBookContent(fileURL);

        String content = convert(contentStream);

        if (checkLanguage(content, lang)) {
            String contentCLean = getBookContent(content);

            String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
            fileName = fileName.substring(2);

            storage.write(contentCLean, fileName);
            System.out.println("File downloaded in: " + fileName);
        }

    }

    @Override
    public Boolean checkLanguage(String content, String Language) {
        return Objects.equals(getBookLanguage(content), Language);
    }


    public String convert(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public String getBookContent(String content){
        String startMarker = "*** START OF THE PROJECT GUTENBERG EBOOK";
        String endMarker = "*** END OF THE PROJECT GUTENBERG EBOOK";

        int startIndex = content.indexOf(startMarker);
        int endIndex = content.indexOf(endMarker);

        if (startIndex != -1 && endIndex != -1) {
            String cleanContent = content.substring(startIndex + startMarker.length(), endIndex).trim();
            return cleanContent;
        }

        return content;

    }

    public String getBookLanguage(String content) {
        String languageMarker = "Language:";
        String startMarker = "*** START OF THIS PROJECT GUTENBERG EBOOK";

        int languageIndex = content.indexOf(languageMarker);
        int startIndex = content.indexOf(startMarker);


        if (languageIndex != -1 && (startIndex == -1 || languageIndex < startIndex)) {

            int languageEndIndex = content.indexOf("\n", languageIndex);
            if (languageEndIndex != -1) {
                String languageLine = content.substring(languageIndex, languageEndIndex).trim();

                return languageLine.replace(languageMarker, "").trim();
            }
        }

        return "Unknown";
    }
}



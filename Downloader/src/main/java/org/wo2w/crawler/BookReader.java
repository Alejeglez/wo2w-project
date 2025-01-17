package org.wo2w.crawler;

import java.io.IOException;
import java.io.InputStream;

public interface BookReader {

    InputStream fetchBookContent(String fileURL) throws IOException;
    void downloadFile(String fileURL, Storage storage, String lang) throws IOException;
    Boolean checkLanguage(String content, String lang);
}
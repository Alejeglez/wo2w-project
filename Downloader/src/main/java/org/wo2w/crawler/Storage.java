package org.wo2w.crawler;

import java.io.IOException;

public interface Storage {

    void write(String content, String filename) throws IOException;
}

package org.wo2w.datamartReader;

import java.io.IOException;

public interface Storage {

    void write(String content, String filename) throws IOException;

    boolean fileExists(String filename);

    boolean isTTLExpired(String filename);
}

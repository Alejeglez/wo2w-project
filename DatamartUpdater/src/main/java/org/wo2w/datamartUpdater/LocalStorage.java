package org.wo2w.datamartUpdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalStorage implements Storage{


    @Override
    public String read(String key) {
        try (BufferedReader br = Files.newBufferedReader(Path.of(key), StandardCharsets.ISO_8859_1)) {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

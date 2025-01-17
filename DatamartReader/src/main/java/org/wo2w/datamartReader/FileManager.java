package org.wo2w.datamartReader;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

public class FileManager {

    public static boolean checkFile(Storage storage, String key) {
        if (storage.fileExists(key)){
            System.out.println("El archivo existe pero no se ha comprobado si ha expirado ttl.");
            if (!storage.isTTLExpired(key)) {
                System.out.println("El archivo ya existe y su ttl no ha expirado");
                return true;
            }
        }
        return false;
    }

    public static void writeFile(HashMap<String, Object> paths, Storage storage, String key) throws IOException {
        Gson gson = new Gson();
        String jsonPaths = gson.toJson(paths);
        storage.write(jsonPaths, key);
        System.out.println("El archivo se ha escrito o sobreescrito");
    }

}

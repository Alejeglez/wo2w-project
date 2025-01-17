package org.wo2w.datamartUpdater;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;

public class DatalakeReader {
    public static void readFilesAndInsertNodes(String folderPath, GraphDatabaseWriter graph) {
        try {
            // Obtener los archivos de la carpeta
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath));

            // Iterar sobre los archivos
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    try (BufferedReader br = Files.newBufferedReader(entry, StandardCharsets.ISO_8859_1)) {
                        String line;

                        while ((line = br.readLine()) != null) {
                            String word = line.split(" ")[0];
                            int occurrences = Integer.parseInt(line.split(" ")[1]);

                            // Crear e inicializar el HashMap
                            HashMap<String, Object> nodeProperties = new HashMap<>();
                            nodeProperties.put("value", word);
                            nodeProperties.put("occurrences", occurrences);
                            graph.insertOrUpdateNode(new Node("Word", nodeProperties), "value");
                        }
                    } catch (IOException e) {
                        System.err.println("Error al leer el archivo: " + entry.toString());
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al acceder a los archivos en el directorio: " + folderPath);
            e.printStackTrace();
        }
    }
}
package org.wo2w.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchDownloader {

    private final Storage storage;
    private final GutenbergBookReader gutenbergBookReader;
    private final int batchSize;
    private final int startId;
    private final int endId;

    public BatchDownloader(int startId, int endId, int batchSize, GutenbergBookReader gutenbergBookReader, Storage storage) {
        this.startId = startId;
        this.endId = endId;
        this.batchSize = batchSize;
        this.gutenbergBookReader = gutenbergBookReader;
        this.storage = storage;
    }

    public String urlSetter(int id) {
        return "https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + ".txt";
    }

    public void downloadBatches(String lang) {
        int currentId = startId;

        while (currentId <= endId) {
            int nextBatchEnd = Math.min(currentId + batchSize - 1, endId);
            List<String> urls = getUrlsForBatch(currentId, nextBatchEnd);

            if (urls.isEmpty()) {
                System.out.println("No hay URLs válidas en el rango: " + currentId + " - " + nextBatchEnd);
            } else {
                System.out.println("Descargando batch: IDs " + currentId + " - " + nextBatchEnd);
                downloadUrls(urls, lang);
            }

            currentId += batchSize;
        }

        System.out.println("Descarga en batch completada.");
    }

    private List<String> getUrlsForBatch(int start, int end) {
        List<String> urls = new ArrayList<>();
        for (int id = start; id <= end; id++) {
            urls.add(urlSetter(id));
        }
        return urls;
    }

    private void downloadUrls(List<String> urls, String lang) {
        for (String url : urls) {
            try {
                gutenbergBookReader.downloadFile(url, storage, lang);
                System.out.println("Descargado: " + url);
            } catch (IOException e) {
                System.err.println("Error al descargar: " + url);
                e.printStackTrace();
            }
        }
    }
}

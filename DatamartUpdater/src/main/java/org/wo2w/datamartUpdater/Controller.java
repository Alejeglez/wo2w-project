package org.wo2w.datamartUpdater;

import java.util.Map;

public class Controller {

    private final GraphDatabaseWriter neo4jDatabase;
    private final Storage storage;
    private final WordRelationshipProcessor wordRelationshipProcessor;

    public Controller(GraphDatabaseWriter neo4jDatabase, Storage storage) {
        this.neo4jDatabase = neo4jDatabase;
        this.storage = storage;
        this.wordRelationshipProcessor = new WordRelationshipProcessor(neo4jDatabase);
    }

    public void run(String keyFile){
        Node node = new Node("Word", getPropertiesFromText(keyFile, storage.read(keyFile + ".txt")));
        neo4jDatabase.insertOrUpdateNode(node, "value");
        wordRelationshipProcessor.createRelationshipsForWord(node);
    }


    public Map<String, Object> getPropertiesFromText(String keyFile, String line){
        line = line.trim();

        int occurrences = Integer.parseInt(line);

        return Map.of(
                "value", keyFile,
                "occurrences", occurrences
        );
    }

}

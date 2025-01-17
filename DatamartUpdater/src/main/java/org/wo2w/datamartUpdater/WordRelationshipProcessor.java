package org.wo2w.datamartUpdater;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordRelationshipProcessor {

    private final GraphDatabaseWriter graphDatabase;
    private final Set<Node> words;

    public WordRelationshipProcessor(GraphDatabaseWriter graphDatabase) {
        this.graphDatabase = graphDatabase;
        this.words = getCurrentWords();
    }

    public Set<Node> getCurrentWords() {
        Set<Map<String, Object>> nodeProperties = graphDatabase.getAllNodesProperties();
        Set<Node> wordNodes = new HashSet<>();

        for (Map<String, Object> properties : nodeProperties) {
            Node wordNode = new Node("Word",properties);
            wordNodes.add(wordNode);
        }

        return wordNodes;
    }

    public void createRelationshipsForWord(Node node) {
        if (words.isEmpty()) {
            System.out.println("No hay nada en la lista!");
        } else {
            for (Node nodeTarget : this.words) {
                if (isOneLetterDifference(node, nodeTarget)) {
                    Object occurrencesNodeObj = node.getProperties().get("occurrences");
                    int occurrencesNode = 0;
                    if (occurrencesNodeObj instanceof Integer) {
                        occurrencesNode = (Integer) occurrencesNodeObj;
                    } else if (occurrencesNodeObj instanceof Long) {
                        occurrencesNode = ((Long) occurrencesNodeObj).intValue();
                    } else {
                        throw new IllegalArgumentException("El tipo de 'occurrences' no es compatible: " + occurrencesNodeObj.getClass());
                    }

                    Object occurrencesNodeTargetObj = nodeTarget.getProperties().get("occurrences");
                    int occurrencesNodeTarget = 0;
                    if (occurrencesNodeTargetObj instanceof Integer) {
                        occurrencesNodeTarget = (Integer) occurrencesNodeTargetObj;
                    } else if (occurrencesNodeTargetObj instanceof Long) {
                        occurrencesNodeTarget = ((Long) occurrencesNodeTargetObj).intValue();
                    } else {
                        throw new IllegalArgumentException("El tipo de 'occurrences' no es compatible: " + occurrencesNodeTargetObj.getClass());
                    }
                    int averageOccurrences = (occurrencesNode + occurrencesNodeTarget) / 2;
                    HashMap<String, Object> propertiesRelationship = new HashMap<>();
                    propertiesRelationship.put("averageOccurrences", averageOccurrences);
                    graphDatabase.createRelationship(node, nodeTarget, "SIMILAR", "value", propertiesRelationship);
                    graphDatabase.createRelationship(nodeTarget, node, "SIMILAR", "value", propertiesRelationship);
                }
            }
        }
        words.add(node);
    }

    private boolean isOneLetterDifference(Node word1, Node word2) {

        String valueWord1 = (String) word1.getProperties().get("value");
        String valueWord2 = (String) word2.getProperties().get("value");


        // Caso 1: Las palabras tienen la misma longitud
        if (valueWord1.length() == valueWord2.length()) {
            int differences = 0;
            for (int i = 0; i < valueWord1.length(); i++) {
                if (valueWord1.charAt(i) != valueWord2.charAt(i)) {
                    differences++;
                    if (differences > 1) {
                        return false;
                    }
                }
            }
            return differences == 1;
        }

        if (Math.abs(valueWord1.length() - valueWord2.length()) == 1) {
            if (valueWord1.length() > valueWord2.length()) {
                String temp = valueWord1;
                valueWord1 = valueWord2;
                valueWord2 = temp;
            }

            int i = 0, j = 0, differences = 0;
            while (i < valueWord1.length() && j < valueWord2.length()) {
                if (valueWord1.charAt(i) != valueWord2.charAt(j)) {
                    differences++;
                    if (differences > 1) {
                        return false;
                    }
                    j++;
                } else {
                    i++;
                    j++;
                }
            }
            return differences + (valueWord2.length() - j) == 1;
        }

        return false;
    }



}

package org.wo2w.datamartReader;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;

public class Neo4jDatabaseReader implements GraphDatabaseReader{

    private final Driver driver;

    public Neo4jDatabaseReader(Driver driver) {
        this.driver = driver;
    }

    @Override
    public HashMap<String, Object> findAllPaths(String property, String cost, String startValue, String endValue, String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary;

        try (Session session = driver.session()) {

            String query  = QueryBuilder.getQueryAllPaths(property, cost, startValue, endValue, nodeType, relationshipType);

            Result result = session.run(query);

            List<Record> allRecords = processNeo4jResult(result);

            resultDictionary = createDictionaryPaths(cost, allRecords);
        }

        return resultDictionary;
    }

    @Override
    public HashMap<String, Object> getMaxDistanceGraph(String property, String cost, String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary;

        try (Session session = driver.session()) {

            String query  = QueryBuilder.getQueryMaxDistancePath(property, cost, nodeType, relationshipType);

            Result result = session.run(query);

            List<Record> allRecords = processNeo4jResult(result);

            Record firstResult = allRecords.get(0);
            double totalWeight = firstResult.get("totalWeight").asDouble();
            System.out.println("Total Weight: " + totalWeight);
            System.out.println(firstResult.get("nodeValues"));

            resultDictionary = createDictionaryPaths(cost, allRecords);
            resultDictionary.put("totalWeight", totalWeight);
        }

        return resultDictionary;
    }


    private static List<Record> processNeo4jResult(Result result) {
        List<Record> allRecords = new ArrayList<>();
        while (result.hasNext()) {
            allRecords.add(result.next());
        }
        return allRecords;
    }

    @Override
    public HashMap<String, Object> getNodesWithMaxConnections(String property, String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary;

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryNodesWithMoreConnections(property, nodeType, relationshipType);

            Result result = session.run(query);

            List<Record> allRecords = processNeo4jResult(result);
            resultDictionary = createDictionaryNodes(property, allRecords);

        }
        return resultDictionary;
    }

    @Override
    public HashMap<String, Object> getNodesByConnectivityDegree(int connections, String property, String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary;

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryNodesByConnectivityDegree(connections*2, property, nodeType, relationshipType);

            Result result = session.run(query);

            List<Record> allRecords = processNeo4jResult(result);
            resultDictionary = createDictionaryNodes(property, allRecords);

        }
        return resultDictionary;

    }

    @Override
    public HashMap<String, Object> getIsolatedNodes(String property, String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary;

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryIsolatedNodes(property, nodeType, relationshipType);

            Result result = session.run(query);

            List<Record> allRecords = processNeo4jResult(result);
            resultDictionary = createDictionaryNodes(property, allRecords);

        }
        return resultDictionary;
    }

    @Override
    public HashMap<String, Object> getNodesForCluster(String property, String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary;

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryNodesForCluster(property, nodeType, relationshipType);

            Result result = session.run(query);

            List<Record> allRecords = processNeo4jResult(result);
            resultDictionary = createDictionaryNodesCluster(property, allRecords);
        }
        return resultDictionary;
    }

    private static  HashMap<String, Object> createDictionaryNodesCluster(String property, List<Record> allRecords) {

        HashMap<String, Object> nodesDictionary = new HashMap<>();

        List<Object> allNodes = new ArrayList<>();

        for (Record record: allRecords){
            String nodeValue = record.get("nodeValue").asString();
            List<Object> connectedNodeValues = record.get("connectedNodeValues").asList();

            HashMap<String, Object> nodeDictionary = new HashMap<>();
            nodeDictionary.put("value", nodeValue);
            nodeDictionary.put("connectedNodes", connectedNodeValues);
            allNodes.add(nodeDictionary);
        }
        nodesDictionary.put("nodes", allNodes);
        return nodesDictionary;
    }


    private static HashMap<String, Object> createDictionaryNodes(String property, List<Record> allRecords) {

        HashMap<String, Object> nodesDictionary = new HashMap<>();

        String value;
        String numberOfConnections;

        for (Record record : allRecords) {
            // Verificar si los valores son nulos antes de intentar convertirlos en listas
            List<Object> nodeValues = record.containsKey("nodeValues") ? record.get("nodeValues").asList() : new ArrayList<>();
            List<Object> connections = record.containsKey("connectionCounts") ? record.get("connectionCounts").asList() : new ArrayList<>();

            List<Object> nodes = new ArrayList<>();

            for (int i = 0; i < nodeValues.size(); i++) {
                HashMap<Object, String> node = new HashMap<>();

                value = (String) nodeValues.get(i);

                // Verifica si la conexión es nula y asigna 0 si es necesario
                Long connectionsCount = (connections.size() > i && connections.get(i) != null) ? (Long) connections.get(i) : 0L;
                numberOfConnections = String.valueOf(connectionsCount / 2);

                node.put(property, value);
                node.put("connections", numberOfConnections);

                nodes.add(node);
            }
            nodesDictionary.put("nodes", nodes);
        }

        return nodesDictionary;
    }


    private static HashMap<String, Object> createDictionaryPaths(String cost, List<Record> allRecords) {
        HashMap<String, Object> pathsDictionary = new HashMap<>();
        Set<Object> allPaths = new HashSet<>();

        String from = null;
        String to = null;
        String averageOccurrences;

        for (Record record: allRecords){
            List<Object> nodeValues = record.get("nodeValues").asList();
            List<Object> relationshipValues = record.get("relationshipValues").asList();

            List<Object> paths = new ArrayList<>();
            for (int i = 0; i < nodeValues.size()-1; i++) {
                HashMap<Object, String> relationship = new HashMap<>();
                from = (String) nodeValues.get(i);
                to = (String) nodeValues.get(i+1);
                averageOccurrences = String.valueOf(relationshipValues.get(i));

                relationship.put("from", from);
                relationship.put("to", to);
                relationship.put(cost, averageOccurrences);

                paths.add(relationship);
            }

            allPaths.add(paths);
        }

        if (!allPaths.isEmpty()) {
            List<Object> firstPath = (List<Object>) new ArrayList<>(allPaths).get(0);
            HashMap<Object, String> firstRelationship = (HashMap<Object, String>) firstPath.get(0);
            from = firstRelationship.get("from");

            List<Object> lastPath = (List<Object>) new ArrayList<>(allPaths).get(allPaths.size() - 1);
            HashMap<Object, String> lastRelationship = (HashMap<Object, String>) lastPath.get(lastPath.size() - 1);
            to = lastRelationship.get("to");
        }

        pathsDictionary.put("from", from);
        pathsDictionary.put("to", to);
        pathsDictionary.put("paths", allPaths);

        return pathsDictionary;
    }



}

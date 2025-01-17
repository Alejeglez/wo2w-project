package org.wo2w.datamartUpdater;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Neo4jDatabaseWriter implements GraphDatabaseWriter {

    public String uri;
    public String user;
    public String password;
    private final Driver driver;

    public Neo4jDatabaseWriter(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void insertOrUpdateNode(Node node, String primaryKeyField) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                StringBuilder query = new StringBuilder();
                query.append("MERGE (n:")
                        .append(node.getLabel())
                        .append(" {")
                        .append(primaryKeyField)
                        .append(": $").append(primaryKeyField)
                        .append("}) ");

                query.append("ON CREATE SET ");
                appendProperties(query, "n", node.getProperties(), true);
                query.append(" ON MATCH SET ");
                appendProperties(query, "n", node.getProperties(), false);

                Map<String, Object> parameters = new HashMap<>(node.getProperties());
                parameters.put(primaryKeyField, node.getProperties().get(primaryKeyField));

                tx.run(query.toString(), org.neo4j.driver.Values.parameters(flattenMap(parameters)));
                return null;
            });
        }
    }

    private void appendProperties(StringBuilder query, String alias, Map<String, Object> properties, boolean includeOnlyNew) {
        for (String key : properties.keySet()) {
            if (includeOnlyNew) {
                query.append(alias).append(".").append(key).append(" = $").append(key).append(", ");
            } else {
                query.append(alias).append(".").append(key).append(" = $").append(key).append(", ");
            }
        }
        if (!properties.isEmpty()) {
            query.setLength(query.length() - 2);
        }
    }

    private Object[] flattenMap(Map<String, Object> map) {
        return map.entrySet()
                .stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .toArray();
    }

    @Override
    public void createRelationship(Node node, Node nodeTarget, String relationshipType, String keyField, Map<String, Object> propertiesRelationship) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                StringBuilder query = new StringBuilder("MATCH (n1:" + node.getLabel() + "), (n2:" + nodeTarget.getLabel() + ") ")
                        .append("WHERE n1.").append(keyField).append(" = $value1 AND n2.").append(keyField).append(" = $value2 ")
                        .append("OPTIONAL MATCH (n1)-[r:" + relationshipType + "]->(n2) ")
                        .append("DELETE r ")
                        .append("CREATE (n1)-[newRel:" + relationshipType + " {");

                for (String key : propertiesRelationship.keySet()) {
                    query.append(key).append(": $").append(key).append(", ");
                }

                if (!propertiesRelationship.isEmpty()) {
                    query.setLength(query.length() - 2);
                }

                query.append("}]->(n2)");

                Map<String, Object> parameters = new HashMap<>(propertiesRelationship);
                parameters.put("value1", node.getProperties().get(keyField));
                parameters.put("value2", nodeTarget.getProperties().get(keyField));

                tx.run(query.toString(), org.neo4j.driver.Values.parameters(flattenMap(parameters)));
                return null;
            });
        }
    }

    public void close() {
        driver.close();
    }

    public Set<Map<String, Object>> getAllNodesProperties() {
        Set<Map<String, Object>> nodeProperties = new HashSet<>();

        try (Session session = driver.session()) {
            String query = "MATCH (n) RETURN n";
            Result result = session.run(query);

            while (result.hasNext()) {
                Record record = result.next();
                Value nodeValue = record.get("n");

                Map<String, Object> properties = nodeValue.asMap();

                nodeProperties.add(properties);
            }
        }

        return nodeProperties;
    }

}

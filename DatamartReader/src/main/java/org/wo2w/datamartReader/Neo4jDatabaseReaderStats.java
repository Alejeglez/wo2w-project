package org.wo2w.datamartReader;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.HashMap;

public class Neo4jDatabaseReaderStats implements GraphDatabaseReaderStats{

    private final Driver driver;

    public Neo4jDatabaseReaderStats(Driver driver) {
        this.driver = driver;
    }

    @Override
    public HashMap<String, Object> getTotalNodesCount(String nodeType) {

        HashMap<String, Object> resultDictionary = new HashMap<>();

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryTotalNodesCount(nodeType);

            Result result = session.run(query);

            if (result.hasNext()) {
                resultDictionary.put("measure", "totalNodes");
                resultDictionary.put("value", result.next().get("totalNodes").asInt());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultDictionary;
    }

    @Override
    public HashMap<String, Object> getTotalConnectionsCount(String nodeType, String relationshipType) {

        HashMap<String, Object> resultDictionary = new HashMap<>();

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryTotalConnectionsCount(nodeType, relationshipType);

            Result result = session.run(query);

            if (result.hasNext()) {
                resultDictionary.put("measure", "totalConnections");
                resultDictionary.put("value", result.next().get("totalConnections").asInt());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultDictionary;
    }


    @Override
    public HashMap<String, Object> getAverageNodeDegreeCount(String nodeType) {
        HashMap<String, Object> resultDictionary = new HashMap<>();

        try (Session session = driver.session()) {

            String query = QueryBuilder.getQueryAverageNodeDegreeCount(nodeType);

            Result result = session.run(query);

            if (result.hasNext()) {
                resultDictionary.put("measure", "averageNodeDegree");
                resultDictionary.put("value", result.next().get("averageNodeDegree").asInt());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultDictionary;
    }

    @Override
    public HashMap<String, Object> getIsolatedNodesCount(String nodeType) {
        HashMap<String, Object> resultDictionary = new HashMap<>();

        try (Session session = driver.session()) {

            String query = QueryBuilder.getIsolatedNodesCount(nodeType);

            Result result = session.run(query);

            if (result.hasNext()) {
                resultDictionary.put("measure", "isolatedNodesCount");
                resultDictionary.put("value", result.next().get("isolatedNodesCount").asInt());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultDictionary;
    }

    @Override
    public HashMap<String, Object> getConnectedNodesCount(String nodeType) {
        HashMap<String, Object> resultDictionary = new HashMap<>();

        try (Session session = driver.session()) {

            String query = QueryBuilder.getConnectedNodesCount(nodeType);

            Result result = session.run(query);

            if (result.hasNext()) {
                resultDictionary.put("measure", "connectedNodesCount");
                resultDictionary.put("value", result.next().get("connectedNodesCount").asInt());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultDictionary;
    }
}

package org.wo2w.datamartReader;

public class QueryBuilder {

    public static String getQueryAllPaths(String property, String cost, String startValue, String endValue, String nodeType, String relationshipType){
        String operation  = "MATCH p = (n:" + nodeType + " {" + property + ": '" + startValue + "'})-[:" + relationshipType + "*]-(m:" + nodeType + " {" + property + ": '" + endValue + "'})\n";
        String condition = "WHERE ALL(node IN nodes(p) WHERE size([n IN nodes(p) WHERE n = node]) = 1)";
        String valueToReturn = "RETURN nodes(p) AS nodos, relationships(p) AS relaciones, \n" +
                "       [n IN nodes(p) | n." + property + "] AS nodeValues, \n" +
                "       [r IN relationships(p) | r." + cost + "] AS relationshipValues";

        return operation + condition + valueToReturn;
    }

    public static String getQueryMaxDistancePath(String property, String cost, String nodeType, String relationshipType) {
        String operation = "MATCH p = (start:" + nodeType + ")-[:" + relationshipType + "*]->(end:" + nodeType + ")";
        String condition = "WHERE ALL(node IN nodes(p) WHERE size([n IN nodes(p) WHERE n = node]) = 1)";
        String reduce = "WITH p, " +
                "     reduce(totalWeight = 0, r IN relationships(p) | totalWeight + (1.0 / r." + cost + ")) AS totalWeight\n";

        String valueToReturn = "RETURN nodes(p) AS nodos, " +
                "relationships(p) AS relaciones, \n" +
                "[n IN nodes(p) | n." + property + "] AS nodeValues, \n" +
                "[r IN relationships(p) | r." + cost + "] AS relationshipValues, \n" +
                "totalWeight\n";

        String order = "ORDER BY totalWeight DESC\n";
        String limit = "LIMIT 1";

        return operation + condition + reduce + valueToReturn + order + limit;
    }

    public static String getQueryNodesWithMoreConnections(String property, String nodeType, String relationshipType) {
        String operation = "MATCH p = (w:"+ nodeType +")-[r:" + relationshipType + "]-()\n";
        String condition = "WITH w." + property + " AS nodeValue, COUNT(r) AS connections\n";
        String order = "ORDER BY connections DESC\n";
        String limit = "LIMIT 10\n";
        String conditionFormat = "WITH nodeValue, connections\n";
        String valueToReturn = "RETURN COLLECT(nodeValue) AS nodeValues, COLLECT(connections) AS connectionCounts\n";

        return operation + condition + order + limit + conditionFormat + valueToReturn;
    }

    public static String getQueryNodesByConnectivityDegree(int connections, String property, String nodeType, String relationshipType) {
        String operation = "MATCH p = (w:" + nodeType + ")-[r:" + relationshipType + "]-()";
        String condition = "WITH w." + property + " AS nodeValue, COUNT(r) AS connections\n";
        String filter = "WHERE connections = " + connections + "\n";
        String valueToReturn = "RETURN COLLECT(nodeValue) AS nodeValues, COLLECT(connections) AS connectionCounts\n";

        return operation + condition + filter + valueToReturn;
    }

    public static String getQueryIsolatedNodes(String property, String nodeType, String relationshipType) {
        String operation = "MATCH p =  (w:"+ nodeType +")\n";
        String filter = "WHERE NOT (w)-[:" + relationshipType + "]-()\n";
        String condition = "WITH w." + property + " AS nodeValue\n";
        String valueToReturn = "RETURN COLLECT(nodeValue) AS nodeValues";

        return operation + filter + condition + valueToReturn;
    }

    public static String getQueryNodesForCluster(String property, String nodeType, String relationshipType) {
        String operation = "MATCH p = (w:" + nodeType + ")-[r:" + relationshipType + "]->(n)\n";
        String collect= "WITH w." + property + " AS nodeValue, COLLECT(n." + property + ") AS connectedNodeValues\n";
        String filter =   "WHERE SIZE(connectedNodeValues) > 1\n";
        String valueToReturn = "RETURN nodeValue, connectedNodeValues\n";

        return operation + collect  + filter + valueToReturn;
    }

    public static String getQueryTotalNodesCount(String nodeType){
        String operation = "MATCH (n:" + nodeType + ")";
        String valueToReturn = "RETURN count(n) AS totalNodes";
        return operation + valueToReturn;
    }

    public static String getQueryTotalConnectionsCount(String nodeType, String relationshipType) {
        String operation = "MATCH (n:" + nodeType + ")-[r:" + relationshipType + "]->(m:" + nodeType + ") ";
        String valueToReturn = "RETURN count(r) AS totalConnections";
        return operation + valueToReturn;
    }

    public static String getQueryAverageNodeDegreeCount(String nodeType) {
        String operation = "MATCH (n:" + nodeType + ")";
        String valueToReturn = "RETURN avg(size([(n)-->() | 1])) AS averageNodeDegree";
        return operation + valueToReturn;
    }

    public static String getIsolatedNodesCount(String nodeType){
        String operation = "MATCH (n:" + nodeType + ")";
        String condition =  "WHERE NOT (n)--()";
        String valueToReturn = "RETURN COUNT(n) AS isolatedNodesCount";

        return operation + condition + valueToReturn;
    }

    public static String getConnectedNodesCount(String nodeType) {
        String operation = "MATCH (n:" + nodeType + ")";
        String condition =  "WHERE (n)--()";
        String valueToReturn = "RETURN COUNT(n) AS connectedNodesCount";

        return operation + condition + valueToReturn;
    }

}

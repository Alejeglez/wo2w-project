package org.wo2w.datamartReader;

public class Main {

    public static void main(String[] args) {

        checkArgs(args, 1, "initial");
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current execution directory: " + currentDirectory);
        try{
            String method = args[0];

            SecretsManagerNeo4j secretsManagerNeo4J = new SecretsManagerNeo4j("config/application.properties");
            Neo4jDatabaseReader neo4jDatabaseReader = new Neo4jDatabaseReader(Neo4jDatabaseDriverFactory.createNeo4jDatabaseDriver(secretsManagerNeo4J.getUri(), secretsManagerNeo4J.getUsername(), secretsManagerNeo4J.getPassword()));
            Neo4jDatabaseReaderStats neo4jDatabaseReaderStats = new Neo4jDatabaseReaderStats(Neo4jDatabaseDriverFactory.createNeo4jDatabaseDriver(secretsManagerNeo4J.getUri(), secretsManagerNeo4J.getUsername(), secretsManagerNeo4J.getPassword()));
            Controller controller = new Controller(neo4jDatabaseReader, S3ClientFactory.createS3Client(), "s3-wo2w-results");
            ControllerStats controllerStats = new ControllerStats(neo4jDatabaseReaderStats, S3ClientFactory.createS3Client(), "s3-wo2w-stats");
            switch (method) {
                case "findPaths" -> {
                    checkArgs(args, 3, method);
                    String startNode = args[1];
                    String endNode = args[2];
                    controller.findPaths(startNode, endNode);
                }
                case "findMaxDistancePath" -> controller.findMaxDistancePath();
                case "findNodesWithHighestConnections" -> controller.findNodesWithHighestConnections();
                case "findNodesWithConnectivityDegree" -> {
                    checkArgs(args, 2, method);
                    int degree = Integer.parseInt(args[1]);
                    controller.findNodesWithConnectivityDegree(degree);
                }
                case "findIsolatedNodes" -> controller.findIsolatedNodes();
                case "findNodesCluster" -> controller.findNodesCluster();
                case "findTotalNodesCount" -> controllerStats.findTotalNodesCount();
                case "findTotalConnectionsCount" -> controllerStats.findTotalConnectionsCount();
                case "findAverageNodeDegreeCount" -> controllerStats.findAverageNodeDegreeCount();
                case "findIsolatedNodesCount" -> controllerStats.findIsolatedNodesCount();
                case "findConnectedNodesCount" -> controllerStats.findConnectedNodesCount();
                default -> System.out.println("Error: Invalid method specified");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkArgs(String[] args, int number, String method) {
        if (args.length < number) {
            int missingArgs = number - args.length;
            System.out.println("Error: Insufficient arguments provided for method \"" + method + "\".");
            System.out.println("Missing arguments: " + missingArgs);
            System.out.println("Usage: java -jar DatamartReader.jar <method> args[]");
            System.exit(1);
        } else {
            System.out.println("Arguments verified successfully for method \"" + method + "\".");
        }
    }
}

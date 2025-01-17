package org.wo2w.datamartUpdater;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Por favor, proporciona al menos una palabra como argumento.");
            return;
        }

        SecretManagerNeo4j secretManagerNeo4j = new SecretManagerNeo4j("config/application.properties");
        GraphDatabaseWriter neo4jDatabase = new Neo4jDatabaseWriter(secretManagerNeo4j.getUri(), secretManagerNeo4j.getUsername(), secretManagerNeo4j.getPassword());

        Controller controller = new Controller(neo4jDatabase, new S3Storage(S3ClientFactory.createS3Client(), "s3-wo2w-words"));

        for (String file : args) {
            System.out.println("Insertando palabra: " + file);
            controller.run(file);
        }

        neo4jDatabase.close();
    }
}

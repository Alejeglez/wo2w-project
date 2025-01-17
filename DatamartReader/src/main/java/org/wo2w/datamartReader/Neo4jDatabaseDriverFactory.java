package org.wo2w.datamartReader;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDatabaseDriverFactory {

    public static Driver createNeo4jDatabaseDriver(String uri, String user, String password){
        return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
}

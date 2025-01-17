package org.wo2w.datamartUpdater;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SecretManagerNeo4j {

    private final String fileSecrets;
    private final Properties propertiesSecrets;

    public SecretManagerNeo4j(String fileSecrets) {
        this.fileSecrets = fileSecrets;
        this.propertiesSecrets = loadSecrets();
    }

    private Properties loadSecrets() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileSecrets)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el archivo de configuración: " + fileSecrets, e);
        }
        return properties;
    }

    public String getUri() {
        return propertiesSecrets.getProperty("neo4j.uri");
    }

    public String getUsername() {
        return propertiesSecrets.getProperty("neo4j.username");
    }

    public String getPassword() {
        return propertiesSecrets.getProperty("neo4j.password");
    }
}

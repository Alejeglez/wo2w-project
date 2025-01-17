package org.wo2w.datamartUpdater;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private String label;
    private Map<String, Object> properties;

    public Node(String label, Map<String, Object> properties) {
        this.label = label;
        this.properties = properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public String getLabel() {
        return this.label;
    }

    public Object getField(String field){
        return properties.get(field);
    }


}

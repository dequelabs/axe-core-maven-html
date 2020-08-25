package com.deque.html.axecore.results;

import java.util.ArrayList;
import java.util.List;

public class Check {
    private String id;
    private String impact;
    private String message;
    private Object data;
    private List<Node> relatedNodes = new ArrayList<Node>();

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(final String impact) {
        this.impact = impact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public List<Node> getRelatedNodes() {
        return relatedNodes;
    }

    public void setRelatedNodes(final List<Node> relatedNodes) {
        this.relatedNodes = relatedNodes;
    }
}

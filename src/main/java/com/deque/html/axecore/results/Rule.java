package com.deque.html.axecore.results;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
    private String id;
    private String description;
    private String help;
    private String helpUrl;
    private String impact;
    private List<String> tags = new ArrayList<String>();
    private List<CheckedNode> nodes = new ArrayList<CheckedNode>();
    private String url;
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(final String help) {
        this.help = help;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(final String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(final String impact) {
        this.impact = impact != null ? impact : "";
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public List<CheckedNode> getNodes() {
        return nodes;
    }

    public void setNodes(final List<CheckedNode> nodes) {
        this.nodes = nodes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", help='" + help + '\'' +
                ", helpUrl='" + helpUrl + '\'' +
                ", impact='" + impact + '\'' +
                ", tags=" + tags +
                ", nodes=" + nodes +
                ", url='" + url + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}

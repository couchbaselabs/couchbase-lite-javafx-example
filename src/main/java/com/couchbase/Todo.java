package com.couchbase;

import java.util.*;

public class Todo {

    private String documentId;
    private String title;
    private String description;

    Todo(String documentId, String title, String description) {
        this.documentId = documentId;
        this.title = title;
        this.description = description;
    }

    Todo(String title, String description) {
        this.documentId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return this.documentId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

}

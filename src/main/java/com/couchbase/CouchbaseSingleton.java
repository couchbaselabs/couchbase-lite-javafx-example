package com.couchbase;

import com.couchbase.lite.Database;
import com.couchbase.lite.JavaContext;
import com.couchbase.lite.Manager;
import com.couchbase.lite.replicator.Replication;

import java.net.URL;

public class CouchbaseSingleton {

    private Manager manager;
    private Database database;
    private Replication pushReplication;
    private Replication pullReplication;

    private static final CouchbaseSingleton instance = new CouchbaseSingleton();

    private CouchbaseSingleton() {
        try {
            this.manager = new Manager(new JavaContext("data"), Manager.DEFAULT_OPTIONS);
            this.database = this.manager.getDatabase("fx-project");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CouchbaseSingleton getInstance() {
        return instance;
    }

    public void startReplication(URL gateway, boolean continuous) {
        this.pushReplication = this.database.createPushReplication(gateway);
        this.pullReplication = this.database.createPullReplication(gateway);
        this.pushReplication.setContinuous(continuous);
        this.pullReplication.setContinuous(continuous);
    }

    public void test() {
        System.out.println("This is a test");
    }

}

package example;

import com.couchbase.client.java.Cluster;

public class App {
    public static void main(String[] args) {
        Cluster sharedCluster = Cluster.connect("127.0.0.1", "Administrator", "password");
        System.out.println(sharedCluster);
    }
}

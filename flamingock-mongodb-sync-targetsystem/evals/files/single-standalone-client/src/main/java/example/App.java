package example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class App {
    public static void main(String[] args) {
        MongoClient sharedMongoClient = MongoClients.create("mongodb://localhost:27017");
        System.out.println(sharedMongoClient);
    }
}

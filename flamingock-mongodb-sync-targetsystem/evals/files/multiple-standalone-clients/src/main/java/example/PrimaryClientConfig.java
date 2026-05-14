package example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class PrimaryClientConfig {
    MongoClient primaryMongoClient = MongoClients.create("mongodb://primary:27017");
}

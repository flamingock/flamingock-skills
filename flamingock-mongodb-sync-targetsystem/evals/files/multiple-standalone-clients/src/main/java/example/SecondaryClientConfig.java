package example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class SecondaryClientConfig {
    MongoClient reportingMongoClient = MongoClients.create("mongodb://reporting:27017");
}

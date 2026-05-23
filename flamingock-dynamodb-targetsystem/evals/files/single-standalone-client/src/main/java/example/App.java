package example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class App {
    public static void main(String[] args) {
        DynamoDbClient sharedDynamoDbClient = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .build();
        System.out.println(sharedDynamoDbClient);
    }
}

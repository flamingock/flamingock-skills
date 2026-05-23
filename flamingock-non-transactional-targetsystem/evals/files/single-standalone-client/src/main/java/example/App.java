package example;

import software.amazon.awssdk.services.s3.S3Client;

public class App {
    public static void main(String[] args) {
        S3Client sharedS3Client = S3Client.builder().build();
        System.out.println(sharedS3Client);
    }
}

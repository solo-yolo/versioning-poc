package io.github.solo.yolo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.MongoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    Javers javers(MongoClient mongo) {
        JaversRepository javersMongoRepository =
                new MongoRepository(mongo.getDatabase("javers"));

        return JaversBuilder.javers()
                .registerJaversRepository(javersMongoRepository)
                .build();
    }

    @Bean
    public MongoClient mongo() {
        return MongoClients.create();
    }

    @Bean
    public JsonConverter jsonConverter() {
        return new JsonConverterBuilder().build();
    }
}

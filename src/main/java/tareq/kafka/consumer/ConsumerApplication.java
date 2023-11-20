package tareq.kafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

/**
 * Created by Tareq Sefati on 10-Nov-23
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        System.getProperties().put( "server.port", 8081); //change server port programmatically
        //System.setProperty("server.port", "8082"); //change server port programmatically
        SpringApplication.run(ConsumerApplication.class, args);
    }
}

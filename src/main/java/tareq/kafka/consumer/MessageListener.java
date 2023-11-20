package tareq.kafka.consumer;

import com.tareq.batchProcessing.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Created by Tareq Sefati on 10-Nov-23
 */
@Service
public class MessageListener {

    @Autowired
    private ConsumerCustomerRepository repository;
    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvents(Customer customer) {
        System.out.println("Consumer consume the data: " + customer.toString());
        ConsumerCustomerEntity entity = generateConsumerEntity(customer);
        repository.save(entity);
    }

    private ConsumerCustomerEntity generateConsumerEntity(Customer producerEntity) {
        ConsumerCustomerEntity consumerEntity = new ConsumerCustomerEntity();

        consumerEntity.setDob(producerEntity.getDob());
        consumerEntity.setCountry(producerEntity.getCountry());
        consumerEntity.setContactNo(producerEntity.getContactNo());
        consumerEntity.setEmail(producerEntity.getEmail());
        consumerEntity.setId(producerEntity.getId());
        consumerEntity.setGender(producerEntity.getGender());
        consumerEntity.setFirstName(producerEntity.getFirstName());
        consumerEntity.setLastName(producerEntity.getLastName());

        return consumerEntity;
    }
}

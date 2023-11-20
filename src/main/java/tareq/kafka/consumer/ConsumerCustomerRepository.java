package tareq.kafka.consumer;

import com.tareq.batchProcessing.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerCustomerRepository extends JpaRepository<ConsumerCustomerEntity, Integer> {

}

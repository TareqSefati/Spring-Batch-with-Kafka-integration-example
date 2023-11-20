package com.tareq.batchProcessing.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tareq.batchProcessing.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

/**
 * Created by Tareq Sefati on 22-Oct-23
 */
public class StepSkipListener implements SkipListener<Customer, Number> {


    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);

    @Override // item reader
    public void onSkipInRead(Throwable throwable) {
        logger.info("A failure on read {} ", throwable.getMessage());
    }

    @Override // item writter
    public void onSkipInWrite(Number item, Throwable throwable) {
        logger.info("A failure on write {} , {}", throwable.getMessage(), item);
    }

    //@SneakyThrows
    @Override // item processor
    public void onSkipInProcess(Customer customer, Throwable throwable) {
        try {
            logger.info("Item {}  was skipped due to the exception  {}", new ObjectMapper().writeValueAsString(customer),
                    throwable.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
package com.tareq.batchProcessing.config;

import com.tareq.batchProcessing.entity.Customer;
import com.tareq.batchProcessing.repository.CustomerRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Tareq Sefati on 22-Oct-23
 */

@Component
//Batch data will be written using ItemWriter<Customer> implementations in Database by saveAll() method.
public class CustomerWriter implements ItemWriter<Customer> {

    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        System.out.println("Writer Thread "+Thread.currentThread().getName());
        customerRepository.saveAll(chunk.getItems());
    }
}

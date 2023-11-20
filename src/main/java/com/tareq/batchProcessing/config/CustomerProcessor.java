package com.tareq.batchProcessing.config;

import com.tareq.batchProcessing.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by Tareq Sefati on 22-Oct-23
 */
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
//        if(customer.getCountry().equals("United States")) {
//            return customer;
//        }else{
//            return null;
//        }
//        int age = Integer.parseInt(customer.getAge());
//        if (age >= 18) {
//            return customer;
//        }
//        return null;
        return customer;
    }
}

package com.tareq.batchProcessing.repository;

import com.tareq.batchProcessing.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}

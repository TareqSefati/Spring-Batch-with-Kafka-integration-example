package com.tareq.batchProcessing.config;

import com.tareq.batchProcessing.entity.Customer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Created by Tareq Sefati on 22-Oct-23
 */
@Configuration
public class CustomerReader implements ItemReader<Customer> {


    private Resource fileResource;

    public Resource getFileResource() {
        return fileResource;
    }

    public void setFileResource(Resource fileResource) {
        this.fileResource = fileResource;
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(getFileResource());
        itemReader.setName("customerCsvFileReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader.read();
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> mapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
        mapper.setLineTokenizer(lineTokenizer);

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);
        mapper.setFieldSetMapper(fieldSetMapper);

        return mapper;
    }
}

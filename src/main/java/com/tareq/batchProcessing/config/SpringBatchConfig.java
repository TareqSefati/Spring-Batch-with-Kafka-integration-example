package com.tareq.batchProcessing.config;

import com.tareq.batchProcessing.entity.Customer;
import com.tareq.batchProcessing.listener.StepSkipListener;
import com.tareq.batchProcessing.partition.ColumnRangePartitioner;
import com.tareq.batchProcessing.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

/**
 * Created by Tareq Sefati on 21-Oct-23
 */
@Configuration
public class SpringBatchConfig {
    @Autowired
    private CustomerRepository customerRepository;
//    @Autowired
//    private CustomerWriter customerWriter;

    @Autowired
    private KafkaTemplate<Integer, Customer> template;
    @Value("${kafka.topic.name}")
    private String topicName;

    //private TaskletStep slaveStepBuilder;

    @Bean
    //Following method is responsible for read data from source for batch processing.
    @StepScope //'@StepScope'-> by default scope of every bean is singleton. but we need this reader scope as a step execution not singleton.
               // create a new instance of it for each step execution.
    public FlatFileItemReader<Customer> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle) {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(new File(pathToFIle)));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
//        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    // Code for implement partition
    public ColumnRangePartitioner partitioner() {
        return new ColumnRangePartitioner();
    }

    @Bean
    // Code for implement partition
    public PartitionHandler partitionHandler(FlatFileItemReader<Customer> itemReader, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(4);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
        taskExecutorPartitionHandler.setStep(slaveStep(itemReader, jobRepository, transactionManager));
        return taskExecutorPartitionHandler;
    }

    @Bean
    //Batch data will be written using this RepositoryItemWriter<Customer> in Database one by one item.
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }


//    @Bean
//    public Step masterStep(FlatFileItemReader<Customer> itemReader, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new StepBuilder("masterStep", jobRepository)
//                .partitioner(slaveStep(itemReader, jobRepository, transactionManager).getName(), partitioner())
//                .partitionHandler(partitionHandler(itemReader, jobRepository, transactionManager))
//                .build();
//    }
    @Bean
    public Step slaveStep(FlatFileItemReader<Customer> itemReader, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("slaveStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(itemReader)
                .processor(processor())
                .writer(kafkaItemWriter())
                .faultTolerant()
                //.skipLimit(100)
                //.skip(NumberFormatException.class)
                //.noSkip(IllegalArgumentException.class)
                .listener(skipListener())
                .skipPolicy(skipPolicy())
                .taskExecutor(taskExecutor())
                .build();
        //return slaveStepBuilder;
    }

    @Bean
    //Batch data will be written in KafkaItemWriter which will produce data
    public KafkaItemWriter<Integer, Customer> kafkaItemWriter(){
        template.setDefaultTopic(topicName);
        return new KafkaItemWriterBuilder<Integer, Customer>()
                .kafkaTemplate(template)
                .itemKeyMapper(Customer::getId)
                .build();
    }


    @Bean
    public Job runJob(FlatFileItemReader<Customer> itemReader, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importCustomer", jobRepository).flow(slaveStep(itemReader, jobRepository, transactionManager)).end().build();
    }


    @Bean
    public SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }

    @Bean
    public SkipListener skipListener() {
        return new StepSkipListener();
    }

    //SimpleAsyncTaskExecutor task executor
//    @Bean
//    public TaskExecutor taskExecutor() {
//        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
//        taskExecutor.setConcurrencyLimit(10);
//        return taskExecutor;
//    }

    //ThreadPoolTaskExecutor task executor
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setQueueCapacity(4);
        return taskExecutor;
    }

}


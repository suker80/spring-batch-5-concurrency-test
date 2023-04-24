package com.example.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration{

    @Bean
    public JdbcCursorItemReader<SampleData> jdbcCursorItemReader(DataSource dataSource) {
        JdbcCursorItemReader<SampleData> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);

        reader.setSql("SELECT id, value FROM sample_data");
        reader.setRowMapper((rs, rowNum) -> new SampleData(rs.getInt("id"), rs.getString("value")));
        return reader;
    }

    @Bean
    public ItemProcessor<SampleData, SampleData> sampleDataProcessor() {
        return item -> {
//            Thread.sleep(100);
            return item;
        };
    }

    @Bean
    public JdbcPagingItemReader<SampleData> jdbcPagingItemReader(DataSource dataSource,PagingQueryProvider createQueryProvider) {
        JdbcPagingItemReader<SampleData> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(2);
        reader.setQueryProvider(createQueryProvider);
        reader.setRowMapper((rs, rowNum) -> new SampleData(rs.getInt("id"), rs.getString("value")));
        return reader;
    }

    @Bean
    public ItemWriter<SampleData> sampleDataWriter() {
        return items -> items.forEach(
                (item)->{
                    System.out.println(Thread.currentThread().getName() + "     " + item);
                }
        );
    }

    @Bean
    public Step sampleDataStep(
            JdbcCursorItemReader<SampleData> jdbcCursorItemReader,
            JdbcPagingItemReader<SampleData> jdbcPagingItemReader,
            ItemProcessor<SampleData, SampleData> sampleDataProcessor,
            ItemWriter<SampleData> sampleDataWriter,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.afterPropertiesSet();
        return new StepBuilder("sampleDataStep", jobRepository)
                .<SampleData, SampleData>chunk(1, platformTransactionManager)
//                .reader(jdbcCursorItemReader)
                .reader(jdbcPagingItemReader)
                .processor(sampleDataProcessor)
                .writer(sampleDataWriter)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job sampleDataJob(Step sampleDataStep, JobRepository jobRepository) {
        System.out.println("샘플 잡 생성");

        return new JobBuilder("job1", jobRepository).start(sampleDataStep).build();
    }
    @Bean
    public Job sampleDataJob2(Step sampleDataStep, JobRepository jobRepository) {
        System.out.println("샘플 잡 생성");

        return new JobBuilder("job2", jobRepository).start(sampleDataStep).build();
    }


    @Bean
    public PagingQueryProvider createQueryProvider(DataSource dataSource) throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id,value");
        queryProvider.setFromClause("from sample_data");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }


}

package com.tareq.batchProcessing.config;

import org.springframework.batch.core.step.skip.NonSkippableReadException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

import java.sql.BatchUpdateException;

/**
 * Created by Tareq Sefati on 22-Oct-23
 */
public class ExceptionSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        return throwable instanceof NumberFormatException ;
//        return false;
//        if (throwable instanceof NonSkippableReadException){
//            return true;
//        } else return false;
    }
}

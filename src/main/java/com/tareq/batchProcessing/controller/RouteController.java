package com.tareq.batchProcessing.controller;

import com.tareq.batchProcessing.config.CustomerReader;
import com.tareq.batchProcessing.entity.FileModel;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareq Sefati on 20-Oct-23
 */
@Controller
@RequestMapping("/file")
public class RouteController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Autowired
    private CustomerReader customerReader;

    private final String TEMP_STORAGE = System.getProperty("user.home");
    @GetMapping
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @PostMapping("/upload")
    public ModelAndView uploadFiles(@RequestParam("files") MultipartFile[] files, ModelAndView mv) {
        try {
            List<FileModel> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                String contentType = file.getContentType();
                String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                String fileName = file.getOriginalFilename();

                FileModel fileModel = new FileModel();
                fileModel.setFileContent(fileContent);
                fileModel.setFileName(fileName);
                fileModel.setFileType(contentType);

                fileList.add(fileModel);

                System.out.println("Taking resources number: #" + fileList.size());
                File fileToImport = new File(TEMP_STORAGE+ File.separator + fileName);
                file.transferTo(fileToImport);

                //customerReader.setFileResource(file.getResource());

                JobParameters jobParameters = new JobParametersBuilder()
                        .addString("fullPathFileName", TEMP_STORAGE+ File.separator + fileName)
                        .addLong("startAt", System.currentTimeMillis()).toJobParameters();

                JobExecution execution = jobLauncher.run(job, jobParameters);

                if (execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)){
                    Files.deleteIfExists(Paths.get(TEMP_STORAGE+ File.separator + fileName));
                }

                System.out.println("Job Launcher running for resources number: #" + fileList.size());
            }
            //System.out.println(fileList);
            mv.addObject("allFiles", fileList);
            mv.setViewName("allFileView");
            //here execute the job...

//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
//            jobLauncher.run(job, jobParameters);

            return mv;
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | IOException e) {
            e.printStackTrace();
            mv.setViewName("ErrorPage");
            return mv;
        }
    }
}

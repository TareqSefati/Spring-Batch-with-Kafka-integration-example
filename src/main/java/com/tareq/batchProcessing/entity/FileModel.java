package com.tareq.batchProcessing.entity;

import jakarta.persistence.*;

/**
 * Created by Tareq Sefati on 21-Oct-23
 */
@Entity
@Table(name = "file_model")
public class FileModel {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_content")
    @Lob
    private String fileContent;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "source_path")
    private String sourcePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileContent='" + fileContent + '\'' +
                ", fileType='" + fileType + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                '}';
    }
}

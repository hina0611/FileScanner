package com.example.hinakhan.filescanner;

import java.io.Serializable;

/**
 * Utility class to hold statistics for a file.
 * Created by hinakhan on 4/26/16.
 */
public class FileStats implements Serializable {

    private String filename; //Name of file
    private Long fileSize; // Size of file

    /**
     * Default constructor.
     */
    public FileStats() {

    }

    /**
     * Constructor with parameters.
     * @param filename
     * @param fileSize
     */
    public FileStats(String filename, Long fileSize) {
        this.filename = filename;
        this.fileSize = fileSize;
    }

    /**
     * Get file name.
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set file name.
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Get file size.
     * @return
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * Set file size.
     * @param fileSize
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeInKiloBytes() {
        return String.valueOf(this.getFileSize()/1024);
    }

}

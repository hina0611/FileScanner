package com.example.hinakhan.filescanner;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class to compute and collect statistics for a scan.
 * Stats:
 *  Names and sizes of 10 biggest files
 *  Average file size
 *  5 most frequent file extensions (with their frequencies)
 * Created by hinakhan on 4/24/16.
 */
public class ScanStatistics implements Serializable {

    public static int MAX_BIGGEST_FILES = 10; //Max Number of files to report data on by size
    public static int MAX_FREQUENT_FILE_EXTENSIONS = 5; //Max Number of file extensions to report data about

    private Integer totalFiles; //total number of files scanned
    private Long totalFileSize; //sum of all file sizes
    private boolean completedScan; //Flag indicating if scan was completed or cancelled
    private ArrayList<FileStats> biggestFiles; //Array of biggest files
    private HashMap<String, Integer> fileExtensionsFrequency; //Map for frequently used file extensions

    /**
     * Default Constructor.
     */
    public ScanStatistics() {
        this.totalFiles = 0;
        this.totalFileSize = 0L;
        this.completedScan = true;
        this.biggestFiles = new ArrayList<FileStats>();
        this.fileExtensionsFrequency = new HashMap<String, Integer>();
    }

    /**
     * Was Scan completed
     * @return
     */
    public boolean isCompletedScan() {
        return completedScan;
    }

    /**
     * Set if scan was completed.
     * @param completedScan
     */
    public void setCompletedScan(boolean completedScan) {
        this.completedScan = completedScan;
    }

    /**
     * Total number of files scanned.
     * @return
     */
    public Integer getTotalFiles() {
        return totalFiles;
    }

    /**
     * Set total number of files scanned.
     * @param totalFiles
     */
    public void setTotalFiles(Integer totalFiles) {
        this.totalFiles = totalFiles;
    }

    /**
     * Sum of sizes of all files scanned.
     * @return
     */
    public Long getTotalFileSize() {
        return totalFileSize;
    }

    /**
     * Set sum of sizes of all files scanned.
     * @param totalFileSize
     */
    public void setTotalFileSize(Long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    /**
     * Average file size in Kb.
     * @return
     */
    public String getAverageFileSize() {
        return String.valueOf(totalFileSize/(totalFiles * 1024));
    }

    /**
     * List of files sorted by size descending.
     * @return
     */
    public ArrayList<FileStats> getBiggestFiles() {
        return biggestFiles;
    }

    /**
     * Sets list os biggest files scanned.
     * @param biggestFiles
     */
    public void setBiggestFiles(ArrayList<FileStats> biggestFiles) {
        this.biggestFiles = biggestFiles;
    }

    /**
     * List of most frequently used file extensions.
     * @param numValues
     * @return
     */
    public Map<String, Integer> getFrequentedFileExtensions(int numValues) {
        Map<String, Integer> sortedMap = FileHelper.sortByComparator(fileExtensionsFrequency);

        Map<String, Integer> results = new LinkedHashMap<String, Integer>();
        if (!sortedMap.isEmpty()) {
            for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
                if (results.size() == numValues) {
                    break;
                }
            }
        }


        return results;
    }

    /**
     * Update scan statistics.
     * @param file
     */
    public void updateStatistics(File file) {
        String fileName = file.getName();
        long fileSize = file.length();

        //Increment file count
        this.setTotalFiles(this.getTotalFiles()+1);

        //Increment total file size
        this.setTotalFileSize(this.getTotalFileSize() + fileSize);

        //Update file extension stats
        int check = fileName.lastIndexOf('.');
        if (check > 0) {
            //Extract file extension from name
            String extension = fileName.substring(fileName.lastIndexOf("."));
            if (extension != null) {
                //Increment count for file extension
                Integer frequency = fileExtensionsFrequency.get(extension);
                if (frequency == null) {
                    frequency = 0;
                }
                fileExtensionsFrequency.put(extension, frequency + 1);
            }
        }

        //Create new file stats object
        FileStats fileStats = new FileStats(fileName, fileSize);

        if (this.getBiggestFiles().isEmpty()) {
            //Add filestats to array if empty
            this.getBiggestFiles().add(fileStats);
        } else {
            for (int i = 0; i < this.getBiggestFiles().size(); i++) {
                FileStats existingFileStats = this.getBiggestFiles().get(i);
                long existingFileSize = existingFileStats.getFileSize();
                if (fileSize > existingFileSize) {
                    //found file larger than one that exists in set
                    //Add it at this index to create sorted set
                    this.getBiggestFiles().add(i, fileStats);
                    break;
                } else if ((fileSize == existingFileSize) && (this.getBiggestFiles().size() < MAX_BIGGEST_FILES)) {
                    //Same sized file but we have less than max biggest files in list
                    this.getBiggestFiles().add(i, fileStats);
                    break;
                }
            }
        }

    }


}


package com.example.hinakhan.filescanner;

import android.os.Environment;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for common file operations.
 * Created by hinakhan on 4/24/16.
 */
public class FileHelper {

    /**
     * Checks if external storage is available for read and write
     * @return Returns true if we have a read+write exernal storage, false otherwise.
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     * @return Returns true if we have an external storage that can be read from, false otherwise.
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Comparator class to sort a Map.
     * @param unsortedMap<String, Integer>
     * @return Sorted Map<String, Integer>
     */
    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortedMap) {
        //Convert to list
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortedMap.entrySet());

        // Sort by value in descending order ie: highest value entry at index zero
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        //Insert sorted entries in linked list
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


}

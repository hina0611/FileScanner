package com.example.hinakhan.filescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

/**
 * Main activity class.
 */
public class MainActivity extends Activity {

    public final static String SCAN_STATISTICS = "com.example.hinakhan.filescanner.SCAN_STATISTICS";
    private Button btnCheckExternalMedia, btnStartScan, btnStopScan;
    private Button btnToSave, btnToFind;
    private ProgressBar mProgress;
    private ExternalMediaScannerTask scannerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create new async task to scan external media
        scannerTask = new ExternalMediaScannerTask();

        btnStartScan = (Button) findViewById(R.id.btnForStartScanning);
        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disable start scan when scan is in progress
                btnStartScan.setEnabled(false);

                //Render stop scan button
                btnStopScan.setVisibility(View.VISIBLE);

                //Set progress bar to visible
                mProgress.setVisibility(View.VISIBLE);

                //Launch task to scan external media
                scannerTask.execute("*");
            }
        });

        btnStopScan = (Button)findViewById(R.id.btnToStopScanning);
        btnStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notify user
                Toast.makeText(getApplicationContext(), "Clicked stop scan btn", Toast.LENGTH_LONG).show();

                //Cancel scanner task
                scannerTask.cancel(true);
            }
        });

        //progress bar
        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        if (checkForExternalMedia()) {
            //Hide stop scan button
            btnStopScan.setVisibility(View.INVISIBLE);
        } else {
            //Hide Scan buttons
            setStateForScanButtons(View.INVISIBLE);

            //Notify user to plug in SD card
            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();

            //Display button to rescan for SD card
            btnCheckExternalMedia.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handle user pressing back button
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (scannerTask != null) {
                scannerTask.cancel(true);
                Log.d("FileScanner", "Cancelled scanner task");
            }
        }

        return super.onKeyDown(keyCode, event);
    }



    /**
     * Checks if there is a readable + writable external media plugged into the phone.
     * @return Returns true if valid extrnal media is plugged in else returns false.
     */
    private boolean checkForExternalMedia() {
        return FileHelper.isExternalStorageWritable();
    }

    /**
     * Sets view state for scan options
     *
     * @param viewState
     */
    private void setStateForScanButtons(int viewState) {
        btnToSave.setVisibility(viewState);
        btnToFind.setVisibility(viewState);
        btnStartScan.setVisibility(viewState);
    }

    //Inner class for scanning external media
    class ExternalMediaScannerTask extends AsyncTask<String, Integer, ScanStatistics> {

        /**
         * Display progress bar before starting scan process
         */
        protected void onPreExecute() {
            super.onPreExecute();

            //Notify User.
            Toast.makeText(getApplicationContext(), "Starting Scan", Toast.LENGTH_SHORT).show();
        }

        public void updateProgress(Integer counter) {
            publishProgress(counter);
        }

        /**
         * @param fileExtension
         * @return
         */
        protected ScanStatistics doInBackground(String... fileExtension) {
            ScanStatistics statistics = new ScanStatistics();

            File stResult = new File(Environment.getExternalStorageDirectory().toString());
            long startTime = System.currentTimeMillis();
            for(String dirname : stResult.list()) {
                // Escape early if cancel() is called
                if (isCancelled()) {
                    statistics.setCompletedScan(false);
                    Log.d("FileScanner", "Cancelling async task");
                    break;
                }

                //Scan directory
                String absolutePathToDir = Environment.getExternalStorageDirectory().toString() + "/" + dirname;
                Log.d("FileScanner", "Scanning directory " + absolutePathToDir);
                File file = new File(absolutePathToDir);
                traverseDirectory(file, statistics, startTime);
            }


            return statistics;
        }

        /**
         * Traverses directory computing scan statistics.
         * @param dir
         * @param statistics
         * @param startTime
         */
        protected void traverseDirectory(File dir, ScanStatistics statistics, long startTime) {
            File[]  files = dir.listFiles();
            if(files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        traverseDirectory(file, statistics, startTime);
                    } else {
                        statistics.updateStatistics(file); //Update scan statistics

                        //Publish stats every 50 milliseconds
                        if ((System.currentTimeMillis() - startTime)%50 == 0) {
                            this.updateProgress(statistics.getTotalFiles());
                        }
                    }
                }
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            int numFilesScanned = progress[0];
            Log.d("FileScanner", "Scanned " + numFilesScanned + " files");
            mProgress.setProgress(numFilesScanned*10);
        }

        protected void onPostExecute(ScanStatistics scanStatistics) {
            //Invoke super class
            super.onPostExecute(scanStatistics);

            if (scanStatistics.isCompletedScan()) {
                //Display scan completion message
                Toast.makeText(getApplicationContext(), "Scan completed", Toast.LENGTH_SHORT).show();

                //Launch Activity to display stats
                displayStatistics(scanStatistics);

            } else {
                //Display scan cancellation message
                Toast.makeText(getApplicationContext(), "Scan was cancelled", Toast.LENGTH_SHORT).show();

                //Enable Scan button for next run
                btnStartScan.setEnabled(true);
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            //Perform clean up task
        }

        protected void displayStatistics(ScanStatistics scanStatistics) {
            Intent intent = new Intent(MainActivity.this, DisplayStatisticsActivity.class);
            intent.putExtra(SCAN_STATISTICS, scanStatistics);
            startActivity(intent);
        }

    }
}
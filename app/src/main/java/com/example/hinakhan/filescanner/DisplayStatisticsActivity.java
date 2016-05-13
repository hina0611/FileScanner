package com.example.hinakhan.filescanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

/**
 * Displays Scan Statistics.
 */
public class DisplayStatisticsActivity extends Activity {

    private TableLayout tblScanStatistics;
    private Button btnShareStats, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_statistics);

        btnShareStats = (Button)findViewById(R.id.btnShareStats);
        btnShareStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareStats(getIntent());
            }
        });

        btnHome = (Button)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayStatisticsActivity.this, MainActivity.class));
            }
        });

        displayStats(getIntent());
    }

    protected void displayStats(Intent intent) {
        ScanStatistics scanStatistics = (ScanStatistics)intent.getSerializableExtra(MainActivity.SCAN_STATISTICS);
        if (scanStatistics != null) {
            Log.d("FileScanner", "Scan Statistics: " + scanStatistics.toString());
            tblScanStatistics = (TableLayout)findViewById(R.id.tblScanStatistics);

            addStats(tblScanStatistics, getResources().getString(R.string.files_scanned), scanStatistics.getTotalFiles());
            addStats(tblScanStatistics, getResources().getString(R.string.avg_file_size), scanStatistics.getAverageFileSize());

            StringBuffer sbuf = new StringBuffer();
            Map<String, Integer> frequentFileExtensions = scanStatistics.getFrequentedFileExtensions(ScanStatistics.MAX_FREQUENT_FILE_EXTENSIONS);
            for (Map.Entry<String, Integer> entry : frequentFileExtensions.entrySet()) {
                sbuf.append(entry.getKey()).append("(").append(entry.getValue()).append(")").append("\n");
            }
            addStats(tblScanStatistics, getResources().getString(R.string.frequent_file_extensions), sbuf.toString());

            sbuf = new StringBuffer();
            int counter = 0;
            for (FileStats fileStats : scanStatistics.getBiggestFiles()) {
                sbuf.append(fileStats.getFilename()).append("(").append(fileStats.getFileSizeInKiloBytes()).append(" Kb)").append("\n");
                counter++;
                if (counter >= ScanStatistics.MAX_BIGGEST_FILES) {
                    break;
                }
            }
            addStats(tblScanStatistics, getResources().getString(R.string.biggest_files), sbuf.toString());
        }

    }

    protected void addStats(TableLayout tblLayout, String key, Object value) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);
        tableRow.setBackgroundColor(Color.parseColor("#CEEFF0"));

        TextView tv = new TextView(this);
        tv.setText(key);
        tv.setTextColor(Color.parseColor("#3E4040"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tableRow.addView(tv);

        tv = new TextView(this);
        tv.setText(value.toString());
        tv.setTextColor(Color.parseColor("#A35B3C"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tableRow.addView(tv);

        tblLayout.addView(tableRow);
    }

    /**
     * Launch Intent for sharing scan statistics.
     * @param intent
     */
    private void shareStats(Intent intent) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/html");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getStatsAsHTML(intent));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan Statistics");
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Scan Statistics");
        startActivity(Intent.createChooser(shareIntent, "Share Statistics"));
    }

    private String getStatsAsHTML(Intent intent) {
        ScanStatistics scanStatistics = (ScanStatistics)intent.getSerializableExtra(MainActivity.SCAN_STATISTICS);
        StringBuffer sbuf = new StringBuffer();

        if (scanStatistics != null) {
            sbuf.append("<tr><td>Files Scanned</td><td>").append(scanStatistics.getTotalFiles()).append("</td></tr>");
            sbuf.append("<tr><td>Average File Size</td><td>").append(scanStatistics.getAverageFileSize()).append("</td></tr>");

            sbuf.append("<tr><td>Frequent File Extensions</td><td>");
            Map<String, Integer> frequentFileExtensions = scanStatistics.getFrequentedFileExtensions(ScanStatistics.MAX_FREQUENT_FILE_EXTENSIONS);
            for (Map.Entry<String, Integer> entry : frequentFileExtensions.entrySet()) {
                sbuf.append(entry.getKey()).append("(").append(entry.getValue()).append(")").append("<br/>");
            }
            sbuf.append("</td></tr>");

            sbuf.append("<tr><td>Biggest Files</td><td>");
            int counter = 0;
            for (FileStats fileStats : scanStatistics.getBiggestFiles()) {
                sbuf.append(fileStats.getFilename()).append("(").append(fileStats.getFileSizeInKiloBytes()).append(" Kb)").append("<br/>");
                counter++;
                if (counter >= ScanStatistics.MAX_BIGGEST_FILES) {
                    break;
                }
            }
            sbuf.append("</td></tr>");
        }

        return  sbuf.toString();
    }

}

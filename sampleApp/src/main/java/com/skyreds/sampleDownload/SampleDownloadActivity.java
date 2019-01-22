package com.skyreds.sampleDownload;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.skyreds.ytextractor.VideoMeta;
import com.skyreds.ytextractor.YouTubeExtractor;
import com.skyreds.ytextractor.YtFile;

public class SampleDownloadActivity extends Activity {

    private static String youtubeLink;

    private LinearLayout mainLayout;

    private Button btnCreate;
    private EditText edt_idVideo;
    static String YT_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_download);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        btnCreate = (Button) findViewById(R.id.btn_Create);
        edt_idVideo = (EditText) findViewById(R.id.edt_idVideo);

        btnCreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = edt_idVideo.getText().toString().trim();
                youtubeLink =YT_URL + s;

                getYoutubeDownloadUrl(youtubeLink);
            }
        });

//        // Check how it was started and if we can get the youtube link
//        if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
//                && getIntent().getType() != null && "text/plain".equals(getIntent().getType())) {
//
//            String ytLink = getIntent().getStringExtra(Intent.EXTRA_TEXT);
//
//            if (ytLink != null
//                    && (ytLink.contains("://youtu.be/") || ytLink.contains("youtube.com/watch?v="))) {
//                youtubeLink = ytLink;
//                // We have a valid link
//                getYoutubeDownloadUrl(youtubeLink);
//            } else {
//                Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
//                finish();
//            }
//        } else if (savedInstanceState != null && youtubeLink != null) {
//            getYoutubeDownloadUrl(youtubeLink);
//        } else {
//            finish();
//        }
    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish();
                    return;
                }
                // Iterate over itags
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        addButtonToMainLayout(vMeta.getTitle(), ytFile);
                    }
                }
            }
        }.extract(youtubeLink, true, false);
    }

    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
        // Display some buttons and let the user choose the format
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
        Button btn = new Button(this);
        btn.setText("Tạo phòng thu");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
                } else {
                    filename = videoTitle + "." + ytfile.getFormat().getExt();
                }
                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
                Intent i = new Intent(SampleDownloadActivity.this,DownloadActivity.class);
                i.putExtra("url",ytfile.getUrl());
                i.putExtra("title",videoTitle);
                i.putExtra("filename",filename);
                startActivity(i);
            }
        });
        mainLayout.addView(btn);
    }


    //activity mới



}

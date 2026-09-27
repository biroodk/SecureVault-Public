package com.securevault.app;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

public class VaultViewerActivity extends Activity {

    public static final String EXTRA_FILE_PATH =
            "vault_viewer_file_path";

    public static final String EXTRA_FILE_NAME =
            "vault_viewer_file_name";

    public static final String EXTRA_MIME_TYPE =
            "vault_viewer_mime_type";

    private File temporaryFile;
    private MediaPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String filePath =
                getIntent().getStringExtra(
                        EXTRA_FILE_PATH
                );

        String fileName =
                getIntent().getStringExtra(
                        EXTRA_FILE_NAME
                );

        String mimeType =
                getIntent().getStringExtra(
                        EXTRA_MIME_TYPE
                );

        if (filePath == null ||
                filePath.trim().isEmpty()) {

            finish();
            return;
        }

        temporaryFile =
                new File(filePath);

        if (!temporaryFile.exists()) {

            finish();
            return;
        }

        if (fileName == null ||
                fileName.trim().isEmpty()) {

            fileName = temporaryFile.getName();
        }

        if (mimeType == null ||
                mimeType.trim().isEmpty()) {

            mimeType = "*/*";
        }

        showViewer(
                fileName,
                mimeType
        );
    }

    private void showViewer(
            String fileName,
            String mimeType
    ) {

        String lowerMime =
                mimeType.toLowerCase();

        if (lowerMime.startsWith("image/")) {

            showImageViewer(fileName);

        } else if (lowerMime.startsWith("video/")) {

            showVideoViewer(fileName);

        } else if (lowerMime.startsWith("audio/")) {

            showAudioViewer(fileName);

        } else {

            showUnsupportedViewer(
                    fileName,
                    mimeType
            );
        }
    }

    private LinearLayout createBaseLayout(
            String title
    ) {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setGravity(
                Gravity.CENTER
        );

        root.setBackgroundColor(
                android.graphics.Color.rgb(
                        8,
                        12,
                        22
                )
        );

        TextView titleView =
                new TextView(this);

        titleView.setText(title);
        titleView.setTextColor(
                android.graphics.Color.WHITE
        );
        titleView.setTextSize(18);
        titleView.setGravity(
                Gravity.CENTER
        );

        titleView.setPadding(
                20,
                25,
                20,
                20
        );

        root.addView(
                titleView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        return root;
    }

    private void showImageViewer(
            String fileName
    ) {

        LinearLayout root =
                createBaseLayout(fileName);

        ImageView image =
                new ImageView(this);

        image.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        image.setImageURI(
                Uri.fromFile(temporaryFile)
        );

        root.addView(
                image,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button close =
                new Button(this);

        close.setText("Close");

        close.setOnClickListener(
                v -> finish()
        );

        root.addView(
                close,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(root);
    }

    private void showVideoViewer(
            String fileName
    ) {

        LinearLayout root =
                createBaseLayout(fileName);

        VideoView video =
                new VideoView(this);

        video.setVideoURI(
                Uri.fromFile(temporaryFile)
        );

        MediaController controller =
                new MediaController(this);

        controller.setAnchorView(video);

        video.setMediaController(
                controller
        );

        root.addView(
                video,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button close =
                new Button(this);

        close.setText("Close");

        close.setOnClickListener(
                v -> finish()
        );

        root.addView(
                close,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(root);

        video.start();
    }

    private void showAudioViewer(
            String fileName
    ) {

        LinearLayout root =
                createBaseLayout(fileName);

        TextView icon =
                new TextView(this);

        icon.setText("🎵");
        icon.setTextSize(70);
        icon.setGravity(
                Gravity.CENTER
        );

        root.addView(
                icon,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button play =
                new Button(this);

        play.setText("▶ Play");

        play.setOnClickListener(
                v -> {

                    try {

                        if (audioPlayer != null) {

                            if (audioPlayer.isPlaying()) {

                                audioPlayer.pause();
                                play.setText("▶ Play");

                            } else {

                                audioPlayer.start();
                                play.setText("⏸ Pause");
                            }

                            return;
                        }

                        audioPlayer =
                                new MediaPlayer();

                        audioPlayer.setDataSource(
                                temporaryFile.getAbsolutePath()
                        );

                        audioPlayer.setOnCompletionListener(
                                mp -> play.setText("▶ Play")
                        );

                        audioPlayer.prepare();
                        audioPlayer.start();

                        play.setText("⏸ Pause");

                    } catch (Exception e) {

                        play.setText("▶ Play");
                    }
                }
        );

        root.addView(
                play,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        Button close =
                new Button(this);

        close.setText("Close");

        close.setOnClickListener(
                v -> finish()
        );

        root.addView(
                close,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(root);
    }

    private void showUnsupportedViewer(
            String fileName,
            String mimeType
    ) {

        LinearLayout root =
                createBaseLayout(fileName);

        TextView message =
                new TextView(this);

        message.setText(
                "This file type cannot currently be previewed inside Folder Locker.\n\n" +
                        "Type: " + mimeType
        );

        message.setTextColor(
                android.graphics.Color.WHITE
        );

        message.setTextSize(16);
        message.setGravity(
                Gravity.CENTER
        );

        message.setPadding(
                30,
                30,
                30,
                30
        );

        root.addView(
                message,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button close =
                new Button(this);

        close.setText("Close");

        close.setOnClickListener(
                v -> finish()
        );

        root.addView(
                close,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(root);
    }

    @Override
    protected void onDestroy() {

        if (audioPlayer != null) {

            try {
                if (audioPlayer.isPlaying()) {
                    audioPlayer.stop();
                }
            } catch (Exception ignored) {
            }

            try {
                audioPlayer.release();
            } catch (Exception ignored) {
            }

            audioPlayer = null;
        }

        if (temporaryFile != null &&
                temporaryFile.exists()) {

            temporaryFile.delete();
        }

        super.onDestroy();
    }
}

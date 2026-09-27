package com.securevault.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameActivity extends Activity {

    private WebView gameWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(5, 5, 12));

        // Back button
        Button backButton = new Button(this);
        backButton.setText("←  Back to Folder Locker");
        backButton.setTextColor(Color.WHITE);
        backButton.setTextSize(14);
        backButton.setAllCaps(false);
        backButton.setGravity(Gravity.CENTER);

        backButton.setBackgroundColor(Color.rgb(20, 15, 40));

        backButton.setOnClickListener(v -> finish());

        root.addView(
                backButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                )
        );

        // Game WebView
        gameWebView = new WebView(this);

        WebSettings settings = gameWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        gameWebView.setBackgroundColor(Color.BLACK);
        gameWebView.setWebViewClient(new WebViewClient());

        gameWebView.loadUrl(
                "file:///android_asset/game/Game.html"
        );

        root.addView(
                gameWebView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        setContentView(root);
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (gameWebView != null) {
            gameWebView.destroy();
        }

        super.onDestroy();
    }
}


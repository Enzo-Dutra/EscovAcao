package com.app.escovacao;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class VideosActivity extends AppCompatActivity {

    private final String URL_VIDEO_ESCOVAR = "https://www.youtube.com/watch?v=BBvHrSNR7f4";
    private final String URL_VIDEO_FIO = "https://www.youtube.com/watch?v=ubfwHBoVh3w";

    private WebView mWebView;
    private Button btnVideoEscovar;
    private Button btnVideoFio;
    private ImageButton btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        // 1. Conectar componentes
        mWebView = findViewById(R.id.webview_videos);
        btnVideoEscovar = findViewById(R.id.bt_video_escovar);
        btnVideoFio = findViewById(R.id.bt_video_fio);
        btnVoltar = findViewById(R.id.bt_videos_voltar);

        // 2. Configurar o WebView
        configurarWebView();

        // 3. Configurar os cliques
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a tela e volta ao Menu
            }
        });

        btnVideoEscovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(URL_VIDEO_ESCOVAR);
            }
        });

        btnVideoFio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(URL_VIDEO_FIO);
            }
        });
    }

    private void configurarWebView() {
        // Habilita o JavaScript (necessário para o YouTube)
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Garante que o vídeo toque dentro do app
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    // Pausa o vídeo se o usuário sair da tela
    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    // Retoma o vídeo se o usuário voltar para a tela
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }
}
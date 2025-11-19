package com.app.escovacao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ParabensFioActivity extends AppCompatActivity {

    private ImageView imgMascote;
    private TextView textTitulo, textSubtitulo;
    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parabens_fio);

        // 1. Conectar componentes
        imgMascote = findViewById(R.id.img_parabens_mascote);
        textTitulo = findViewById(R.id.text_parabens_titulo);
        textSubtitulo = findViewById(R.id.text_parabens_subtitulo); // ID do XML
        btnContinuar = findViewById(R.id.bt_parabens_continuar);

        // 2. Pegar os dados da "mala" (Intent)
        boolean contou = getIntent().getBooleanExtra("CONTOU_DESSA_VEZ", false);
        String skin = getIntent().getStringExtra("SKIN_EQUIPADA");

        // 3. Atualizar o visual
        atualizarVisualMascote(skin);

        // 4. *** NOVAS MENSAGENS (LIÇÃO 16) ***
        if (contou) {
            // A contagem valeu!
            textTitulo.setText("Bom Trabalho!");
            textSubtitulo.setText("Você passou fio hoje!\nContabilizado para o progresso.");
        } else {
            // A contagem NÃO valeu (limite de 1/dia)
            textTitulo.setText("Que Dedicação!");
            textSubtitulo.setText("Limite de 1/dia atingido. Esta não contou, mas parabéns por manter a higiene!");
        }

        // 5. Configurar o botão "Continuar"
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Volta para o Menu
            }
        });
    }

    // Função para mostrar a skin correta
    private void atualizarVisualMascote(String skin) {
        if (skin == null) {
            imgMascote.setImageResource(R.drawable.ic_mascote_feliz);
            return;
        }

        if (skin.equals("espelho")) {
            imgMascote.setImageResource(R.drawable.ic_mascote_espelho);
        } else if (skin.equals("pasta")) {
            imgMascote.setImageResource(R.drawable.ic_mascote_pasta);
        } else if (skin.equals("bigode")) {
            imgMascote.setImageResource(R.drawable.ic_mascote_bigode);
        } else if (skin.equals("escova")) {
            imgMascote.setImageResource(R.drawable.ic_mascote_escova);
        } else {
            imgMascote.setImageResource(R.drawable.ic_mascote_feliz);
        }
    }
}
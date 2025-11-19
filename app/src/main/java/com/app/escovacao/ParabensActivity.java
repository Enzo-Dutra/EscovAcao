package com.app.escovacao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; // <-- NOVO IMPORT
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ParabensActivity extends AppCompatActivity {

    // 1. Declarar Variáveis (com ImageView)
    private TextView textTitulo, textStreak;
    private Button btnContinuar;
    private ImageView imgMascote; // <-- NOVO (LIÇÃO 16)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parabens);

        // 2. Conectar componentes
        textTitulo = findViewById(R.id.text_parabens_titulo);
        textStreak = findViewById(R.id.text_parabens_streak);
        btnContinuar = findViewById(R.id.bt_parabens_continuar);
        imgMascote = findViewById(R.id.img_parabens_mascote); // <-- NOVO (LIÇÃO 16)

        // 3. Pegar os dados da "mala" (Intent)
        int novoStreak = getIntent().getIntExtra("NOVO_STREAK", 0);
        long totalEscovacoes = getIntent().getLongExtra("TOTAL_ESCOVACOES", 0);
        boolean contou = getIntent().getBooleanExtra("CONTOU_DESSA_VEZ", false);
        String skin = getIntent().getStringExtra("SKIN_EQUIPADA"); // <-- NOVO (LIÇÃO 16)

        // 4. Atualizar o Mascote
        atualizarVisualMascote(skin);

        // 5. *** NOVAS MENSAGENS (LIÇÃO 16) ***
        if (contou) {
            // A contagem valeu!
            textTitulo.setText("Parabéns!");
            textStreak.setText("Sequência de " + novoStreak + " dias!");
        } else {
            // A contagem NÃO valeu (limite de 3/dia)
            textTitulo.setText("Que Dedicação!");
            textStreak.setText("Limite de 3/dia atingido. Esta não contou para o progresso, mas sua saúde bucal agradece!");
        }

        // 6. Configurar o botão "Continuar"
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Volta para o Menu
            }
        });
    }

    // *** NOVA FUNÇÃO (LIÇÃO 16) ***
    // (Copiada do MenuActivity)
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
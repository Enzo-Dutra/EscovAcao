package com.app.escovacao;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InventarioActivity extends AppCompatActivity {

    private ImageButton btnVoltar;
    private ImageView imgSkinPadrao, imgSkin10, imgSkin50, imgSkin100, imgSkin200;
    private TextView textSkin10, textSkin50, textSkin100, textSkin200;
    private LinearLayout layoutSkinPadrao, layoutSkin10, layoutSkin50, layoutSkin100, layoutSkin200;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef;
    private String skinEquipadaAtualmente = "feliz"; // Padrão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        // Conectar Variáveis (IDs do XML)
        btnVoltar = findViewById(R.id.bt_inventario_voltar);

        imgSkinPadrao = findViewById(R.id.img_skin_padrao);
        imgSkin10 = findViewById(R.id.img_skin_10);
        imgSkin50 = findViewById(R.id.img_skin_50);
        imgSkin100 = findViewById(R.id.img_skin_100);
        imgSkin200 = findViewById(R.id.img_skin_200);

        // (Não precisamos dos TextViews de skin no Java, mas OK)
        textSkin10 = findViewById(R.id.text_skin_10);
        textSkin50 = findViewById(R.id.text_skin_50);
        textSkin100 = findViewById(R.id.text_skin_100);
        textSkin200 = findViewById(R.id.text_skin_200);

        layoutSkinPadrao = findViewById(R.id.layout_skin_padrao);
        layoutSkin10 = findViewById(R.id.layout_skin_10);
        layoutSkin50 = findViewById(R.id.layout_skin_50);
        layoutSkin100 = findViewById(R.id.layout_skin_100);
        layoutSkin200 = findViewById(R.id.layout_skin_200);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userDocRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a tela e volta para o Progresso
            }
        });

        // Adiciona clique na skin padrão (sempre desbloqueada)
        layoutSkinPadrao.setOnClickListener(v -> equiparSkin("feliz"));

        carregarProgresso();
    }

    private void carregarProgresso() {
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {

                        long totalEscovacoes = snapshot.getLong("totalEscovacoes") != null ? snapshot.getLong("totalEscovacoes") : 0;

                        if (snapshot.getString("skinEquipada") != null) {
                            skinEquipadaAtualmente = snapshot.getString("skinEquipada");
                        }

                        // Destaca a skin padrão
                        if ("feliz".equals(skinEquipadaAtualmente)) {
                            layoutSkinPadrao.setBackgroundColor(Color.parseColor("#D3EAF6"));
                        }

                        // --- Lógica para DESBLOQUEAR e EQUIPAR ---
                        // (Usando seus novos nomes de skin)

                        // Skin 1 (10 = Espelho)
                        if (totalEscovacoes >= 10) {
                            imgSkin10.setImageResource(R.drawable.ic_mascote_espelho); // <-- SUA SKIN
                            textSkin10.setText("Espelho (Desbloqueado!)");
                            layoutSkin10.setOnClickListener(v -> equiparSkin("espelho")); // <-- NOME DA SKIN
                            if ("espelho".equals(skinEquipadaAtualmente)) {
                                layoutSkin10.setBackgroundColor(Color.parseColor("#D3EAF6"));
                            }
                        }

                        // Skin 2 (50 = Pasta)
                        if (totalEscovacoes >= 50) {
                            imgSkin50.setImageResource(R.drawable.ic_mascote_pasta); // <-- SUA SKIN
                            textSkin50.setText("Pasta (Desbloqueado!)");
                            layoutSkin50.setOnClickListener(v -> equiparSkin("pasta")); // <-- NOME DA SKIN
                            if ("pasta".equals(skinEquipadaAtualmente)) {
                                layoutSkin50.setBackgroundColor(Color.parseColor("#D3EAF6"));
                            }
                        }

                        // Skin 3 (100 = Bigode)
                        if (totalEscovacoes >= 100) {
                            imgSkin100.setImageResource(R.drawable.ic_mascote_bigode); // <-- SUA SKIN
                            textSkin100.setText("Bigode (Desbloqueado!)");
                            layoutSkin100.setOnClickListener(v -> equiparSkin("bigode")); // <-- NOME DA SKIN
                            if ("bigode".equals(skinEquipadaAtualmente)) {
                                layoutSkin100.setBackgroundColor(Color.parseColor("#D3EAF6"));
                            }
                        }

                        // Skin 4 (200 = Escova)
                        if (totalEscovacoes >= 200) {
                            imgSkin200.setImageResource(R.drawable.ic_mascote_escova); // <-- SUA SKIN
                            textSkin200.setText("Escova (Desbloqueado!)");
                            layoutSkin200.setOnClickListener(v -> equiparSkin("escova")); // <-- NOME DA SKIN
                            if ("escova".equals(skinEquipadaAtualmente)) {
                                layoutSkin200.setBackgroundColor(Color.parseColor("#D3EAF6"));
                            }
                        }
                    }
                } else {
                    Toast.makeText(InventarioActivity.this, "Erro ao carregar progresso.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void equiparSkin(String nomeDaSkin) {
        Map<String, Object> data = new HashMap<>();
        data.put("skinEquipada", nomeDaSkin); // Salva o nome da skin (ex: "bigode")

        userDocRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InventarioActivity.this, "Skin equipada!", Toast.LENGTH_SHORT).show();
                        recreate(); // Reinicia a tela para mostrar o destaque azul
                    }
                });
    }
}
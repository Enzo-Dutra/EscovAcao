package com.app.escovacao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class MenuActivity extends AppCompatActivity {

    private TextView textBoasVindas, textStreakNumero;
    private ImageView imgMascote;
    private Button btnEscovar, btnPassarFio, btnVideos, btnProgresso;
    private ImageButton btnSair;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userDocRef;
    private ListenerRegistration dataListener;

    // *** NOVO (LIÇÃO 15) ***
    // Variável para guardar a skin atual
    private String skinEquipadaAtualmente = "feliz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textBoasVindas = findViewById(R.id.text_boas_vindas);
        textStreakNumero = findViewById(R.id.text_streak_numero);
        imgMascote = findViewById(R.id.img_mascote);
        btnSair = findViewById(R.id.bt_sair);

        btnEscovar = findViewById(R.id.bt_menu_escovar);
        btnPassarFio = findViewById(R.id.bt_menu_passar_fio);
        btnVideos = findViewById(R.id.bt_menu_videos);
        btnProgresso = findViewById(R.id.bt_menu_progresso);

        carregarDadosUsuario();

        // --- Configurar cliques (ATUALIZADO) ---

        btnSair.setOnClickListener(v -> fazerLogout());
        btnVideos.setOnClickListener(v -> startActivity(new Intent(MenuActivity.this, VideosActivity.class)));
        btnProgresso.setOnClickListener(v -> startActivity(new Intent(MenuActivity.this, ProgressoActivity.class)));

        // *** ATUALIZAÇÃO (LIÇÃO 15) ***
        // (Envia a skin atual para o Timer)
        btnEscovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TimerActivity.class);
                intent.putExtra("SKIN_EQUIPADA", skinEquipadaAtualmente); // <-- A MÁGICA
                startActivity(intent);
            }
        });

        // *** ATUALIZAÇÃO (LIÇÃO 15) ***
        // (Envia a skin atual para o Passar Fio)
        btnPassarFio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PassarFioActivity.class);
                intent.putExtra("SKIN_EQUIPADA", skinEquipadaAtualmente); // <-- A MÁGICA
                startActivity(intent);
            }
        });
    }

    private void carregarDadosUsuario() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual != null) {
            String nomeUsuario = usuarioAtual.getDisplayName();
            textBoasVindas.setText((nomeUsuario != null && !nomeUsuario.isEmpty()) ? "Olá, " + nomeUsuario + "!" : "Olá!");
        }
    }

    private void iniciarListenerDeDados() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) return;

        userDocRef = db.collection("usuarios").document(usuarioAtual.getUid());

        dataListener = userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) return;

                if (snapshot != null && snapshot.exists()) {
                    Long streak = snapshot.getLong("streakAtual");
                    textStreakNumero.setText(streak != null ? String.valueOf(streak) : "0");

                    String skinEquipada = snapshot.getString("skinEquipada");

                    // *** ATUALIZAÇÃO (LIÇÃO 15) ***
                    // Salva a skin na variável
                    skinEquipadaAtualmente = skinEquipada != null ? skinEquipada : "feliz";

                    atualizarVisualMascote(skinEquipadaAtualmente);

                } else {
                    textStreakNumero.setText("0");
                    imgMascote.setImageResource(R.drawable.ic_mascote_feliz);
                }
            }
        });
    }

    // Esta função (que já existe) define qual imagem mostrar
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

    private void fazerLogout() {
        mAuth.signOut();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        iniciarListenerDeDados();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dataListener != null) {
            dataListener.remove();
        }
    }
}
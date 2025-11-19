package com.app.escovacao;

import android.content.Intent; // Novo import
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Novo import
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProgressoActivity extends AppCompatActivity {

    // 1. Declarar Variáveis (simplificado)
    private ImageButton btnVoltar;
    private TextView textTotalEscovacoes;
    private TextView textTotalFio;
    private Button btnIrParaInventario; // Nosso novo botão

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progresso);

        // 2. Conectar Variáveis
        btnVoltar = findViewById(R.id.bt_progresso_voltar);
        textTotalEscovacoes = findViewById(R.id.text_progresso_total_escovacoes);
        textTotalFio = findViewById(R.id.text_progresso_total_fio);
        btnIrParaInventario = findViewById(R.id.bt_ir_para_inventario);

        // 3. Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 4. Configurar cliques dos botões
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a tela e volta ao Menu
            }
        });

        btnIrParaInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre a nova tela de Inventário
                Intent intent = new Intent(ProgressoActivity.this, InventarioActivity.class);
                startActivity(intent);
            }
        });

        // 5. Carregar os dados do Firebase
        carregarProgresso();
    }

    private void carregarProgresso() {
        String uid = mAuth.getCurrentUser().getUid();
        if (uid == null) return;

        DocumentReference userDocRef = db.collection("usuarios").document(uid);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {

                        long totalEscovacoes = snapshot.getLong("totalEscovacoes") != null ? snapshot.getLong("totalEscovacoes") : 0;
                        long totalFio = snapshot.getLong("totalPassadasFio") != null ? snapshot.getLong("totalPassadasFio") : 0;

                        textTotalEscovacoes.setText("Total de Escovações: " + totalEscovacoes);
                        textTotalFio.setText("Total de Passadas de Fio: " + totalFio);

                        // TODA A LÓGICA DE SKINS FOI REMOVIDA DAQUI

                    } else {
                        textTotalEscovacoes.setText("Total de Escovações: 0");
                        textTotalFio.setText("Total de Passadas de Fio: 0");
                    }
                } else {
                    Toast.makeText(ProgressoActivity.this, "Erro ao carregar progresso.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
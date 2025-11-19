package com.app.escovacao;

import android.content.Intent;
import android.media.Ringtone; // *** NOVO IMPORT ***
import android.media.RingtoneManager; // *** NOVO IMPORT ***
import android.net.Uri; // *** NOVO IMPORT ***
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimerActivity extends AppCompatActivity {

    private static final long TEMPO_TOTAL_EM_MS = 120000;

    private TextView textContagem;
    private Button btnIniciarPausar;
    private Button btnCancelar;
    private ImageButton btnVoltar;
    private ImageView imgMascote;
    private String skinEquipadaAtualmente = "feliz";

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRodando;
    private long mTempoRestanteEmMs = TEMPO_TOTAL_EM_MS;

    // *** NOVO (LIÇÃO 20 REVISADA) ***
    private Ringtone mRingtoneVitoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        textContagem = findViewById(R.id.text_timer_contagem);
        btnIniciarPausar = findViewById(R.id.bt_timer_iniciar_pausar);
        btnCancelar = findViewById(R.id.bt_timer_cancelar);
        btnVoltar = findViewById(R.id.bt_timer_voltar);
        imgMascote = findViewById(R.id.img_timer_mascote);

        // *** NOVO (LIÇÃO 20 REVISADA) ***
        // Pega o som de notificação padrão do celular
        try {
            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mRingtoneVitoria = RingtoneManager.getRingtone(getApplicationContext(), notificationSoundUri);
        } catch (Exception e) {
            e.printStackTrace(); // Se der erro, o som não toca, mas o app não quebra
        }

        btnIniciarPausar.setOnClickListener(v -> { if (mTimerRodando) pausarTimer(); else iniciarTimer(); });
        btnCancelar.setOnClickListener(v -> cancelarTimer());
        btnVoltar.setOnClickListener(v -> finish());

        atualizarTextoTimer();

        String skin = getIntent().getStringExtra("SKIN_EQUIPADA");
        skinEquipadaAtualmente = (skin != null) ? skin : "feliz";
        atualizarVisualMascote(skinEquipadaAtualmente);
    }

    private void atualizarVisualMascote(String skin) {
        if (skin == null) { imgMascote.setImageResource(R.drawable.ic_mascote_feliz); return; }
        if (skin.equals("espelho")) { imgMascote.setImageResource(R.drawable.ic_mascote_espelho); }
        else if (skin.equals("pasta")) { imgMascote.setImageResource(R.drawable.ic_mascote_pasta); }
        else if (skin.equals("bigode")) { imgMascote.setImageResource(R.drawable.ic_mascote_bigode); }
        else if (skin.equals("escova")) { imgMascote.setImageResource(R.drawable.ic_mascote_escova); }
        else { imgMascote.setImageResource(R.drawable.ic_mascote_feliz); }
    }

    private void iniciarTimer() {
        mCountDownTimer = new CountDownTimer(mTempoRestanteEmMs, 1000) {
            @Override public void onTick(long millisUntilFinished) { mTempoRestanteEmMs = millisUntilFinished; atualizarTextoTimer(); }

            @Override public void onFinish() {
                mTimerRodando = false;
                btnIniciarPausar.setText("Iniciar");

                // *** NOVO (LIÇÃO 20 REVISADA) ***
                // Toca o som!
                if (mRingtoneVitoria != null) {
                    mRingtoneVitoria.play();
                }

                // Salva o progresso (como antes)
                salvarProgresso();
            }
        }.start();
        mTimerRodando = true;
        btnIniciarPausar.setText("Pausar");
    }

    // (O resto do seu código... salvarProgresso, irParaTelaParabens, etc...
    // continua EXATAMENTE IGUAL. Não precisa mudar nada abaixo daqui)

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // *** NOVO (LIÇÃO 20 REVISADA) ***
        // Para o som se a tela for fechada
        if (mRingtoneVitoria != null && mRingtoneVitoria.isPlaying()) {
            mRingtoneVitoria.stop();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    // --- (O RESTO DO SEU CÓDIGO) ---
    // (Vou colar eles aqui por segurança)

    private void salvarProgresso() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) { finish(); return; }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("usuarios").document(uid);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int novoStreak = 1;
                long novoTotalEscovacoes = 1;
                long novoEscovacoesHoje = 1;
                boolean contou = true;
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        long streakAntigo = snapshot.getLong("streakAtual") != null ? snapshot.getLong("streakAtual") : 0;
                        long totalEscovacoesAntigo = snapshot.getLong("totalEscovacoes") != null ? snapshot.getLong("totalEscovacoes") : 0;
                        long escovacoesHojeAntigo = snapshot.getLong("escovacoesHoje") != null ? snapshot.getLong("escovacoesHoje") : 0;
                        Timestamp dataAntiga = snapshot.getTimestamp("ultimaEscovacao");
                        if (dataAntiga != null && isSameDay(dataAntiga)) {
                            if (escovacoesHojeAntigo < 3) {
                                novoEscovacoesHoje = escovacoesHojeAntigo + 1;
                                novoTotalEscovacoes = totalEscovacoesAntigo + 1;
                                novoStreak = (int) streakAntigo;
                                contou = true;
                            } else {
                                novoEscovacoesHoje = escovacoesHojeAntigo;
                                novoTotalEscovacoes = totalEscovacoesAntigo;
                                novoStreak = (int) streakAntigo;
                                contou = false;
                            }
                        } else {
                            novoEscovacoesHoje = 1;
                            novoTotalEscovacoes = totalEscovacoesAntigo + 1;
                            contou = true;
                            if (dataAntiga != null && isYesterday(dataAntiga)) { novoStreak = (int) streakAntigo + 1; }
                            else { novoStreak = 1; }
                        }
                    }
                }
                Map<String, Object> dadosNovos = new HashMap<>();
                dadosNovos.put("ultimaEscovacao", FieldValue.serverTimestamp());
                dadosNovos.put("streakAtual", novoStreak);
                dadosNovos.put("totalEscovacoes", novoTotalEscovacoes);
                dadosNovos.put("escovacoesHoje", novoEscovacoesHoje);
                final int streakFinal = novoStreak;
                final long totalFinal = novoTotalEscovacoes;
                final boolean contouFinal = contou;
                userDocRef.set(dadosNovos, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                irParaTelaParabens(streakFinal, totalFinal, contouFinal);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TimerActivity.this, "Erro ao salvar. Verifique a internet.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });
    }

    private void irParaTelaParabens(int streakCalculado, long totalCalculado, boolean contou) {
        Intent intent = new Intent(TimerActivity.this, ParabensActivity.class);
        intent.putExtra("NOVO_STREAK", streakCalculado);
        intent.putExtra("TOTAL_ESCOVACOES", totalCalculado);
        intent.putExtra("CONTOU_DESSA_VEZ", contou);
        intent.putExtra("SKIN_EQUIPADA", skinEquipadaAtualmente);
        startActivity(intent);
        finish();
    }

    private void pausarTimer() { mCountDownTimer.cancel(); mTimerRodando = false; btnIniciarPausar.setText("Continuar"); }
    private void cancelarTimer() { if (mCountDownTimer != null) mCountDownTimer.cancel(); mTimerRodando = false; mTempoRestanteEmMs = TEMPO_TOTAL_EM_MS; atualizarTextoTimer(); btnIniciarPausar.setText("Iniciar"); }
    private void atualizarTextoTimer() { int min = (int) (mTempoRestanteEmMs / 1000) / 60; int seg = (int) (mTempoRestanteEmMs / 1000) % 60; String tempo = String.format(Locale.getDefault(), "%02d:%02d", min, seg); textContagem.setText(tempo); }
    private boolean isSameDay(Timestamp dataAntiga) { Calendar calAntiga = Calendar.getInstance(); calAntiga.setTime(dataAntiga.toDate()); Calendar calAgora = Calendar.getInstance(); return calAntiga.get(Calendar.YEAR) == calAgora.get(Calendar.YEAR) && calAntiga.get(Calendar.DAY_OF_YEAR) == calAgora.get(Calendar.DAY_OF_YEAR); }
    private boolean isYesterday(Timestamp dataAntiga) { Calendar calAntiga = Calendar.getInstance(); calAntiga.setTime(dataAntiga.toDate()); calAntiga.add(Calendar.DAY_OF_YEAR, 1); Calendar calAgora = Calendar.getInstance(); return calAntiga.get(Calendar.YEAR) == calAgora.get(Calendar.YEAR) && calAntiga.get(Calendar.DAY_OF_YEAR) == calAgora.get(Calendar.DAY_OF_YEAR); }
}
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class PassarFioActivity extends AppCompatActivity {

    private static final long TEMPO_TOTAL_EM_MS = 60000;

    private TextView textContagem;
    private Button btnIniciarPausar;
    private Button btnCancelar;
    private ImageButton btnVoltar;
    private ImageView imgMascote;

    private String skinEquipadaAtualmente = "feliz";

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRodando;
    private long mTempoRestanteEmMs = TEMPO_TOTAL_EM_MS;

    // *** NOVO (LIÇÃO 20.1) ***
    private Ringtone mRingtoneVitoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passar_fio);

        textContagem = findViewById(R.id.text_fio_contagem);
        btnIniciarPausar = findViewById(R.id.bt_fio_iniciar_pausar);
        btnCancelar = findViewById(R.id.bt_fio_cancelar);
        btnVoltar = findViewById(R.id.bt_fio_voltar);
        imgMascote = findViewById(R.id.img_fio_mascote);

        // *** NOVO (LIÇÃO 20.1) ***
        // Pega o som de notificação padrão do celular
        try {
            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mRingtoneVitoria = RingtoneManager.getRingtone(getApplicationContext(), notificationSoundUri);
        } catch (Exception e) {
            e.printStackTrace();
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

                // *** NOVO (LIÇÃO 20.1) ***
                // Toca o som!
                if (mRingtoneVitoria != null) {
                    mRingtoneVitoria.play();
                }

                salvarProgressoFio();
            }
        }.start();
        mTimerRodando = true;
        btnIniciarPausar.setText("Pausar");
    }

    private void salvarProgressoFio() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) { finish(); return; }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("usuarios").document(uid);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean contou = false;

                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Map<String, Object> dadosNovos = new HashMap<>();

                    if (snapshot != null && snapshot.exists()) {
                        Timestamp dataFioAntiga = snapshot.getTimestamp("ultimaPassadaFio");
                        if (dataFioAntiga == null || !isSameDay(dataFioAntiga)) {
                            contou = true;
                            long totalFioAntigo = snapshot.getLong("totalPassadasFio") != null ? snapshot.getLong("totalPassadasFio") : 0;
                            dadosNovos.put("totalPassadasFio", totalFioAntigo + 1);
                            dadosNovos.put("ultimaPassadaFio", FieldValue.serverTimestamp());
                        }
                    } else {
                        contou = true;
                        dadosNovos.put("totalPassadasFio", 1);
                        dadosNovos.put("ultimaPassadaFio", FieldValue.serverTimestamp());
                    }
                    if (contou) {
                        userDocRef.set(dadosNovos, SetOptions.merge());
                    }
                }

                irParaTelaParabensFio(contou);
            }
        });
    }

    private void irParaTelaParabensFio(boolean contou) {
        Intent intent = new Intent(PassarFioActivity.this, ParabensFioActivity.class);
        intent.putExtra("CONTOU_DESSA_VEZ", contou);
        intent.putExtra("SKIN_EQUIPADA", skinEquipadaAtualmente);
        startActivity(intent);
        finish();
    }

    // --- Funções de Ajuda (Timer e Datas) ---
    private void pausarTimer() { mCountDownTimer.cancel(); mTimerRodando = false; btnIniciarPausar.setText("Continuar"); }
    private void cancelarTimer() { if (mCountDownTimer != null) mCountDownTimer.cancel(); mTimerRodando = false; mTempoRestanteEmMs = TEMPO_TOTAL_EM_MS; atualizarTextoTimer(); btnIniciarPausar.setText("Iniciar"); }
    private void atualizarTextoTimer() { int min = (int) (mTempoRestanteEmMs / 1000) / 60; int seg = (int) (mTempoRestanteEmMs / 1000) % 60; String tempo = String.format(Locale.getDefault(), "%02d:%02d", min, seg); textContagem.setText(tempo); }
    private boolean isSameDay(Timestamp dataAntiga) { Calendar calAntiga = Calendar.getInstance(); calAntiga.setTime(dataAntiga.toDate()); Calendar calAgora = Calendar.getInstance(); return calAntiga.get(Calendar.YEAR) == calAgora.get(Calendar.YEAR) && calAntiga.get(Calendar.DAY_OF_YEAR) == calAgora.get(Calendar.DAY_OF_YEAR); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // *** NOVO (LIÇÃO 20.1) ***
        if (mRingtoneVitoria != null && mRingtoneVitoria.isPlaying()) {
            mRingtoneVitoria.stop();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
}
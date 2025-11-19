package com.app.escovacao; // (Use o SEU nome de pacote)

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class SplashAnimadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_animado);

        LottieAnimationView animacao = findViewById(R.id.lottie_splash);

        // Adiciona um "ouvinte" para saber quando a animação termina
        animacao.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Animação começou
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Animação TERMINOU!
                chamarProximaTela();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Animação cancelada
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Animação repetindo (não vai acontecer se loop=false)
            }
        });
    }

    private void chamarProximaTela() {
        // Decida qual tela vem depois: MainActivity (Login) ou MenuActivity
        // Vou usar MainActivity como exemplo:
        Intent intent = new Intent(SplashAnimadoActivity.this, MainActivity.class);
        startActivity(intent);

        // IMPORTANTE: Fecha a tela de animação
        // para que o usuário não possa "voltar" para ela
        finish();
    }
}
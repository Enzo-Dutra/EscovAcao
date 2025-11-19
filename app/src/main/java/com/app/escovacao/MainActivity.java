package com.app.escovacao; // Seu pacote

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Importar ImageButton
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // 1. Declarar Variáveis
    private EditText editTextEmail, editTextSenha;
    private Button btnEntrar;
    private TextView textIrParaCadastro;
    private ImageButton btnGoogle; // Variável para o botão Google

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Conecta ao seu XML

        // 2. Inicializar o Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 3. Conectar Variáveis com os IDs do XML
        editTextEmail = findViewById(R.id.txtEmailLogin);
        editTextSenha = findViewById(R.id.txtSenhaLogin);
        btnEntrar = findViewById(R.id.bt_login_entrar);
        textIrParaCadastro = findViewById(R.id.text_ir_para_cadastro);

        // 4. Configurar Clique do botão Entrar (Email/Senha)
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerLogin();
            }
        });

        // 5. Configurar Clique para ir ao Cadastro
        textIrParaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre a CadastroActivity
                Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }

    // 7. Função de Login com Email/Senha
    private void fazerLogin() {
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();

        // Validação
        if (email.isEmpty()) {
            editTextEmail.setError("E-mail é obrigatório");
            editTextEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            editTextSenha.setError("Senha é obrigatória");
            editTextSenha.requestFocus();
            return;
        }

        // Tenta fazer o login no Firebase
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sucesso!
                            Toast.makeText(MainActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();

                            // *** CÓDIGO ATIVADO! ***
                            // 1. Prepara a intenção de ir da Tela Atual (Main) para a Tela de Menu
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);

                            // 2. Executa a intenção
                            startActivity(intent);

                            // 3. Fecha a tela de login
                            // Isso é CRUCIAL! Impede o usuário de "voltar" para o login
                            finish();

                        } else {
                            // Falha!
                            Toast.makeText(MainActivity.this, "Erro ao fazer login: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
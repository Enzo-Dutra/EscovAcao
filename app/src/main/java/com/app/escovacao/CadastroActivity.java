package com.app.escovacao; // Verifique seu pacote!

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CadastroActivity extends AppCompatActivity {

    // 1. Declarar Variáveis
    // Nomes das variáveis que correspondem aos seus novos IDs
    private EditText txtNomeCadastro, txtEmailCadastro, txtSenhaCadastro, txtConfirmarSenhaCadastro;
    private Button btnCadastrar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Conecta este Java com o SEU layout de cadastro
        setContentView(R.layout.activity_cadastro);

        // 2. Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();

        // 3. Conectar Variáveis com os SEUS IDs do XML
        txtNomeCadastro = findViewById(R.id.txtNomeCadastro);
        txtEmailCadastro = findViewById(R.id.txtEmailCadastro);
        txtSenhaCadastro = findViewById(R.id.txtSenhaCadastro);
        txtConfirmarSenhaCadastro = findViewById(R.id.txtConfirmarSenhaCadastro);
        btnCadastrar = findViewById(R.id.bt_cadastrar); // ID do seu botão

        // 4. Configurar Clique do Botão Cadastrar
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerCadastro();
            }
        });
    }

    private void fazerCadastro() {
        // Pegamos os textos digitados
        String nome = txtNomeCadastro.getText().toString().trim();
        String email = txtEmailCadastro.getText().toString().trim();
        String senha = txtSenhaCadastro.getText().toString().trim();
        String confirmarSenha = txtConfirmarSenhaCadastro.getText().toString().trim();

        // 5. Validação dos Campos (igual antes, mas com seus IDs)
        if (nome.isEmpty()) {
            txtNomeCadastro.setError("Nome é obrigatório");
            txtNomeCadastro.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            txtEmailCadastro.setError("E-mail é obrigatório");
            txtEmailCadastro.requestFocus();
            return;
        }
        if (senha.isEmpty()) {
            txtSenhaCadastro.setError("Senha é obrigatória");
            txtSenhaCadastro.requestFocus();
            return;
        }
        if (senha.length() < 6) {
            txtSenhaCadastro.setError("A senha deve ter no mínimo 6 caracteres");
            txtSenhaCadastro.requestFocus();
            return;
        }
        if (!senha.equals(confirmarSenha)) {
            txtConfirmarSenhaCadastro.setError("As senhas não conferem");
            txtConfirmarSenhaCadastro.requestFocus();
            return;
        }

        // 6. Se tudo estiver OK, criar o usuário no Firebase
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sucesso!
                            Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                            // BÔNUS: Salvar o NOME do usuário no perfil do Firebase
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nome)
                                        .build();
                                user.updateProfile(profileUpdates);
                            }

                            // Fecha a tela de cadastro e volta para o Login
                            finish();

                        } else {
                            // Falha!
                            Toast.makeText(CadastroActivity.this, "Erro ao cadastrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
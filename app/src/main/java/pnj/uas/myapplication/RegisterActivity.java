package pnj.uas.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText edtEmail,edtPassword;
    TextView signin;
    Button btnRegister;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signin = findViewById(R.id.tvSignin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Login ..");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String email = edtEmail.getText().toString().trim();
               String password = edtPassword.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Email Harus Valid");
                    edtEmail.requestFocus();
                }
                if(password.length()<6){
                    edtPassword.setError("Password minimal 6 karakter !");
                    edtPassword.requestFocus();
                }
                if(password.length()>0 && email.length()>0){
                    regist(email,password);
                }else {
                    Toast.makeText(RegisterActivity.this, "Email dan Password Harus Diisi !", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void regist(String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.show();
                            Toast.makeText(RegisterActivity.this, "Registrasi sukses !", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registrasi Gagal !", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }
}
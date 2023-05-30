package pnj.uas.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail,edtPassword;
    Button btnLogin;
    TextView tvSignUp;
    ProgressDialog progressDialog;
    SignInButton btnGoogle;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Login ..");
        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        edtEmail = findViewById(R.id.email1);
        edtPassword = findViewById(R.id.password1);
        btnGoogle = findViewById(R.id.btngoogle);
        btnGoogle.setOnClickListener(view -> {
            signInWithGoogle();
        });
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Email harus valid!");
                    edtEmail.requestFocus();
                }
                if(password.length()<6){
                    edtPassword.setError("Password minimal 6 karakter");
                    edtPassword.requestFocus();
                }
                if(password.length()>0 && email.length()>0){
                    Login(email,password);
                }else {
                    Toast.makeText(LoginActivity.this, "Email dan Password Harus Diisi !", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
        //sign in with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signInWithGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 10);
    }
    private void Login(String email,String password){

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.show();
                            Toast.makeText(LoginActivity.this, "Login Berhasil !", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Gagal, Email atau password salah !", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result returned from launching the intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode == 10){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            //Google SIGN IN was susccessful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d("GOOGLE SIGN IN","firebaseAuthWithGoogle:"+account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        }catch (ApiException e){
            //google sign in failed
            Log.w("GOOGLE SIGN IN ","Google Sign In Failed", e);
        }
    }

}
private void firebaseAuthWithGoogle(String idToken){
    AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Sign In success
                        Log.d("GOOGLE SIGN IN","signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                    }else{
                        //If sign in fails, display message to user
                        Log.w("GOOGLE SIGN IN",task.getException());

                    }
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            });

}

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        }
    }
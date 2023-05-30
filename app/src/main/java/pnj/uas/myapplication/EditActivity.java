package pnj.uas.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    ImageView avatar;
    EditText edtName,edtAddress;
    Button btnSave;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan ..");
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);

        avatar = findViewById(R.id.ivavatar1);
        avatar.setOnClickListener(view -> {
            selectImage();
        });

        btnSave = findViewById(R.id.btnsave);
        btnSave.setOnClickListener(view -> {
            if(edtName.getText().length()>0 && edtAddress.getText().length()>0){
                upload(edtName.getText().toString(),edtAddress.getText().toString());
            }else {
                Toast.makeText(getApplicationContext(), "Silahkan isi semua data", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        if(intent!=null){
            id = intent.getStringExtra("id");
            edtName.setText(intent.getStringExtra("name"));
            edtAddress.setText(intent.getStringExtra("address"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("avatar")).into(avatar);
        }

    }
    private void savedata(String name, String address, String avatar) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("address", address);
        user.put("avatar", avatar);

        progressDialog.show();
        if (id!=null) {
            db.collection("users").document(id)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Berhasil!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    //Untuk menampilkan dialog apakah ingin mengambil foto, pilih dari device, dan batal
    private void selectImage(){
        final CharSequence[] items = {"Ambil Foto","Pilih dari device","Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items,(dialog,item)-> {
            if(items[item].equals("Ambil Foto")){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,10);
            } else if(items[item].equals("Pilih dari device")){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),20);
            }else if(items[item].equals("Batal")){
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Fungsi untuk menerima data dari galeri
        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
            //Yang membedakan dia menerima Urinya
            final Uri path = data.getData();
            Thread thread = new Thread(()-> {
                try {
                    // dan mengambil pathnya ke inputstream
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    // lalu di decode menjadi bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //inisialisasi bitmap ke avatar
                    avatar.post(()->{
                        avatar.setImageBitmap(bitmap);
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        //Menerima data dari kamera dengan request code 10
        if(requestCode == 10 && resultCode == RESULT_OK){
            //dari getExtras dan bundle
            final Bundle extras = data.getExtras();
            Thread thread = new Thread(() ->{
                //get data berupa bitmap
                Bitmap bitmap = (Bitmap) extras.get("data");
                //inisialisasi bitmap ke avatar
                avatar.post(() ->{
                    avatar.setImageBitmap(bitmap);
                });
            });
            thread.start();
        }
    }
        private void upload(String name, String address){
        progressDialog.show();
        // Get the data from an ImageView as bytes
        avatar.setDrawingCacheEnabled(true);
        avatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images").child("IMG"+new Date().getTime()+".jpeg");
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getMetadata()!=null){
                    if(taskSnapshot.getMetadata().getReference()!=null){
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.getResult()!=null) {
                                    savedata(name, address, task.getResult().toString());
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
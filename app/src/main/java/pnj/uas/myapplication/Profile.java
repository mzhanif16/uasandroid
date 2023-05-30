package pnj.uas.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import pnj.uas.myapplication.adapter.UserAdapter;
import pnj.uas.myapplication.model.User;

public class Profile extends Fragment{

    private RecyclerView rvuser;
    private FloatingActionButton btnadd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<User> list = new ArrayList<>();
    UserAdapter userAdapter;
    ProgressDialog progressDialog;

    public Profile() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvuser = view.findViewById(R.id.rvuser);
        btnadd = view.findViewById(R.id.btnadd);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mengambil data...");
        userAdapter = new UserAdapter(getActivity().getApplicationContext(),list);
        userAdapter.setDialog(new UserAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                final CharSequence[] dialogItem = {"Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                            Intent intent = new Intent(getActivity().getApplicationContext(), EditActivity.class);
                            intent.putExtra("id",list.get(pos).getId());
                            intent.putExtra("name",list.get(pos).getName());
                            intent.putExtra("address",list.get(pos).getAddress());
                            intent.putExtra("avatar",list.get(pos).getAvatar());
                            startActivity(intent);
                            break;
                            case 1:
                            deleteData(list.get(pos).getId(),list.get(pos).getAvatar());
                            break;
                        }

                    }
                });
                dialog.show();
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),RecyclerView.VERTICAL,false);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL);
        rvuser.setLayoutManager(layoutManager);
        rvuser.addItemDecoration(decoration);
        rvuser.setAdapter(userAdapter);

        btnadd.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity().getApplicationContext(),EditActivity.class));
    });
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    private void deleteData(String id, String avatar){
        progressDialog.show();
        db.collection("users").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(),"Data gagal dihapus", Toast.LENGTH_SHORT).show();
                            }else {
                                FirebaseStorage.getInstance().getReferenceFromUrl(avatar).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        getData();
                                    }
                                });
                            }
                    }
                });
    }

    private void getData(){
        progressDialog.show();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                User user = new User(document.getString("name"),document.getString("address"),document.getString("avatar"));
                                user.setId(document.getId());
                                list.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(),"Data gagal ditampilkan",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
package pnj.uas.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class Home extends Fragment {
    ProgressDialog progressDialog;
    Button btnlokasi,btnlogout;
    FirebaseAuth mAuth;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Logout ..");
        btnlogout = view.findViewById(R.id.btnLogout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Ya","Tidak"};
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Apakah anda ingin Logout?");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(items,(dialog,itemm)->{
                    if(items[itemm].equals("Ya")){
                        progressDialog.show();
                        logout();
                        startActivity(new Intent(getActivity().getApplicationContext(),LoginActivity.class));
                    }else if(items[itemm].equals("Tidak")){
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        btnlokasi = view.findViewById(R.id.btnlokasi);
        btnlokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(),MapsActivity.class));
            }
        });
    }
    private void logout(){
        FirebaseAuth.getInstance().signOut();
        mAuth.signOut();
    }
}
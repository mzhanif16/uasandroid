package pnj.uas.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import pnj.uas.myapplication.R;
import pnj.uas.myapplication.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyviewHolder> {
    Context context;
    List<User> list;
    Dialog dialog;

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }

    public UserAdapter(Context context, List<User> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_user,parent,false);
        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.address.setText(list.get(position).getAddress());
        Glide.with(context).load(list.get(position).getAvatar()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{
        TextView name,address;
        ImageView avatar;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvname);
            address = itemView.findViewById(R.id.tvaddress);
            avatar = itemView.findViewById(R.id.ivavatar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dialog!=null){
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}

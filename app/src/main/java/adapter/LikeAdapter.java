package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.riseapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.CommentModel;
import models.LikeModel;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> implements View.OnClickListener {

    ArrayList<LikeModel> model;
    private View.OnClickListener listener;
    private Context context;

    public LikeAdapter(Context context, ArrayList<LikeModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false);
        context = parent.getContext();
        view.setOnClickListener(this);
        return new LikeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, int position) {
        Picasso.get().load(model.get(position).getAvatar()).resize(30, 30).into(holder.avatar);
        holder.name.setText(model.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return model.size();
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView avatar;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.lk_avatar);
            name = itemView.findViewById(R.id.lk_name);
        }
    }
}

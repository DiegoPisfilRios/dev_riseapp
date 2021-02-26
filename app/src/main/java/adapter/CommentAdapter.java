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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<CommentModel> model;
    private View.OnClickListener listener;
    private Context context;

    public CommentAdapter(Context context, ArrayList<CommentModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        context = parent.getContext();
        view.setOnClickListener(this);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
        Picasso.get().load(model.get(position).getAvatar()).resize(30, 30).into(holder.avatar);
        holder.name.setText(model.get(position).getName()+" "+model.get(position).getSurname());
        holder.comment.setText(model.get(position).getComment());
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

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    /********************************************************************************/
    public class viewHolder extends RecyclerView.ViewHolder {
        RoundedImageView avatar;
        TextView name;
        TextView comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.cm_avatar);
            name = itemView.findViewById(R.id.cm_name);
            comment = itemView.findViewById(R.id.cm_comment);
        }
    }
    /********************************************************************************/
}


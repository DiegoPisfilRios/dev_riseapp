package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.riseapp.DetailsActivity;
import com.example.riseapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.PostModel;

public class MyPublicAdapter  extends RecyclerView.Adapter<MyPublicAdapter.ViewHolder> implements View.OnClickListener {

    ArrayList<PostModel> model;
    private View.OnClickListener listener;
    private Context context;

    public MyPublicAdapter(Context ct, ArrayList<PostModel> model){
        context = ct;
        this.model = model;
    }

    @NonNull
    @Override
    public MyPublicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        context = parent.getContext();
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPublicAdapter.ViewHolder holder, final int position) {
        holder.nlikes.setText("" + model.get(position).getNlikes());
        holder.ncomments.setText("" + model.get(position).getNcomments());
        String file = model.get(position).getFile();
        holder.description.setText(model.get(position).getDescription());
        RoundedImageView image = holder.picture;
        Picasso.get().load(file).into(image);

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( v.getContext(), DetailsActivity.class);
                intent.putExtra("_id", model.get(position).get_id());
                intent.putExtra("data", model.get(position).getInJSONOject().toString());
                v.getContext().startActivity(intent);
            }
        });
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

        RoundedImageView picture;
        TextView description, nlikes, ncomments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            nlikes = itemView.findViewById(R.id.nlikes);
            ncomments = itemView.findViewById(R.id.nComments);
            picture = itemView.findViewById(R.id.picture);
        }
    }
}

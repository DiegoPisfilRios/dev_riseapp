package adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.riseapp.DetailsActivity;
import com.example.riseapp.ProfileActivity;
import com.example.riseapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import API.ServerHelper;
import models.PublicModel;
import utilities.StorageLocal;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.MyViewHolder> {

    ArrayList<PublicModel> model;
    private OnItemClickListener listener;
    private Context context;
    private StorageLocal storageLocal;

    public PublicAdapter(Context ct, ArrayList<PublicModel> model) {
        context = ct;
        this.model = model;
        storageLocal = new StorageLocal(ct);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String user = model.get(position).getUser();
        String description = model.get(position).getDescription();
        int likes = model.get(position).getNlikes();
        int comments = model.get(position).getNcomments();
        String file = model.get(position).getFile();
        String ago = model.get(position).getAgo();

        try {
            final JSONObject object = new JSONObject(user);
            holder.user.setText(object.getString("name"));
            holder.user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                        intent.putExtra("_id", object.getString("_id"));
                        v.getContext().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                        intent.putExtra("_id", object.getString("_id"));
                        v.getContext().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            Picasso.get().load(object.getString("avatar")).resize(40, 40).into(holder.avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.ago.setText(ago);
        holder.description.setText(description);
        holder.nlikes.setText("" + likes);
        holder.ncomments.setText("" + comments);

        if ((position + 1) == getItemCount()) {
            holder.view.setVisibility(View.INVISIBLE);
        }

        RoundedImageView image = holder.picture;
        Picasso.get().load(file).into(image);

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra("_id", model.get(position).get_id());
                intent.putExtra("data", model.get(position).getInJSONOject().toString());
                v.getContext().startActivity(intent);//, options.toBundle());
            }
        });

        try {
            JSONObject data_user = new JSONObject(storageLocal.readLocalData("my_data"));
            JSONArray arraylikes = model.get(position).getLikes();

            for (int i = 0; i < model.get(position).getNlikes(); i++) {
                JSONObject likep = new JSONObject(arraylikes.get(i).toString());

                if (likep.getString("user").equals(data_user.getString("_id"))) {
                    holder.chckLike.setChecked(true);
                    setColorChek(holder, position);
                    model.get(position).setSt_like(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return model.size();
    }


    public void setColorChek(@NonNull final MyViewHolder holder, int position) {
        if (holder.chckLike.isChecked()) {
            holder.chckLike.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#F23B5F")));

            model.get(position).setSt_like(true);
        } else {
            holder.chckLike.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#B7B7B7")));
            model.get(position).setSt_like(false);
        }
    }

    public interface OnItemClickListener {
        void onItemDelete(int position);
    }

    public void setOnClickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user, description, nlikes, ncomments, ago;
        ImageView avatar;
        View view;
        RoundedImageView picture;
        CheckBox chckLike;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            user = itemView.findViewById(R.id.nameUser);
            description = itemView.findViewById(R.id.description);
            nlikes = itemView.findViewById(R.id.nlikes);
            ncomments = itemView.findViewById(R.id.nComments);
            view = itemView.findViewById(R.id.line);
            picture = itemView.findViewById(R.id.picture);
            avatar = itemView.findViewById(R.id.avatar);
            chckLike = itemView.findViewById(R.id.chklike);
            ago = itemView.findViewById(R.id.ago);

            chckLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    int likes = Integer.parseInt(nlikes.getText().toString());
                    PublicModel publicModel = new PublicModel(v.getContext());

                    publicModel.putLikePublic(model.get(position).get_id(), new ServerHelper.ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                System.out.println(response.getString("msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(JSONObject error) {
                            System.out.println("NO LIKE: " + error);
                        }
                    });



                    if(model.get(position).isSt_like() == false){
                        likes++;
                    }else {
                        if (likes > 0) {
                            likes--;
                        }
                    }
                    nlikes.setText("" + likes);

                    setColorChek(position);

                    if (listener != null) {
                        int index = position;
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onItemDelete(index);
                        }
                    }
                }
            });
        }
        public void setColorChek( int position) {
            if (chckLike.isChecked()) {
                chckLike.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#F23B5F")));
                model.get(position).setSt_like(true);
            } else {
                chckLike.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#B7B7B7")));
                model.get(position).setSt_like(false);
            }
        }
    }
}

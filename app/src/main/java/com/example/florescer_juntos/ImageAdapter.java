package com.example.florescer_juntos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.florescer_juntos.Model.Post;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Post> mPosts;

    public ImageAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Post postCurent = mPosts.get(position);
        holder.nomeView.setText(postCurent.getIdUsuario().toString());
        Glide.with(mContext)
                .load(postCurent.getImageUrl()).centerInside()
                .into(holder.imageView);
        holder.descView.setText(postCurent.getDescricao());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView nomeView, descView;
        public ImageView imageView;

        public ImageViewHolder(View itemView){
            super(itemView);

            nomeView = itemView.findViewById(R.id.tvNomeUsuario);
            descView = itemView.findViewById(R.id.tvPostDescricao);
            imageView = itemView.findViewById(R.id.ivImagePost);
        }
    }

}

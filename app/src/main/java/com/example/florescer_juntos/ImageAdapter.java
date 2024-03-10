package com.example.florescer_juntos;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private static List<Post> mPosts;
    private static String mUserId;
    private static OnItemClickListener mListener;

    public ImageAdapter(Context mContext, List<Post> mPosts, String userId) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        this.mUserId = userId;
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
        holder.nomeView.setText(postCurent.getIdUsuario());
        Glide.with(mContext)
                .load(postCurent.getImageUrl()).placeholder(R.mipmap.ic_launcher)
                .centerInside()
                .into(holder.imageView);
        holder.descView.setText(postCurent.getDescricao());
        String tipo = "Tipo: "+postCurent.getTipoPlanta();
        holder.tipoView.setText(tipo);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView nomeView, descView, tipoView;
        public ImageView imageView;

        public ImageViewHolder(View itemView){
            super(itemView);

            nomeView = itemView.findViewById(R.id.tvNomeUsuario);
            descView = itemView.findViewById(R.id.tvPostDescricao);
            imageView = itemView.findViewById(R.id.ivImagePost);
            tipoView = itemView.findViewById(R.id.tvPostTipo);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v){
            if (mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Escolha uma ação");
            MenuItem save = menu.add(Menu.NONE, 1, 1, "Salvar post");
            save.setOnMenuItemClickListener(this);
            // Verificar se o ID do usuário logado corresponde ao ID do usuário associado ao post
            if (mPosts.get(getAdapterPosition()).getIdUsuario().equals(mUserId)) {
                MenuItem delete = menu.add(Menu.NONE, 2, 2, "Deletar post");
                delete.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            if (mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    if(item.getItemId()==1){
                        mListener.onSaveClick(position);
                        return true;
                    } else if (item.getItemId()==2) {
                        mListener.onDeleteClick(position);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onSaveClick(int position);

        void onDeleteClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}

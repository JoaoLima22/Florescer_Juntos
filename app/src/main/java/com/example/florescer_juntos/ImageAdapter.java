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
import android.widget.Toast;

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
        // Quem mostra os dados
        Post postCurent = mPosts.get(position);
        holder.nomeView.setText(postCurent.getEmailUsuario());
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

            // Instancio/linko os elementos
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

            if (mUserId.equals("000")){
                MenuItem deleteOff = menu.add(Menu.NONE, 5, 5, "Deletar post");
                deleteOff.setOnMenuItemClickListener(this);
            } else {
                MenuItem save = menu.add(Menu.NONE, 1, 1, "Salvar post");
                save.setOnMenuItemClickListener(this);

                if (mPosts.get(getAdapterPosition()).getIdUsuario().equals(mUserId)) {
                    MenuItem editar = menu.add(Menu.NONE, 3, 3, "Editar post");
                    editar.setOnMenuItemClickListener(this);
                    MenuItem delete = menu.add(Menu.NONE, 4, 4, "Deletar post");
                    delete.setOnMenuItemClickListener(this);
                } else {
                    MenuItem ver = menu.add(Menu.NONE, 2, 2, "Ver perfil");
                    ver.setOnMenuItemClickListener(this);
                }
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
                        mListener.onVerPerfilClick(position);
                        return true;
                    } else if (item.getItemId()==3) {
                        mListener.onEditarClick(position);
                        return true;
                    } else if (item.getItemId()==4) {
                        mListener.onDeleteClick(position);
                        return true;
                    } else if (item.getItemId()==5) {
                        mListener.onDeleteOffClick(position);
                        return true;
                    }

                    //Adicionar mais aqui
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onSaveClick(int position);

        void onVerPerfilClick(int position);

        void onEditarClick(int position);

        void onDeleteClick(int position);

        void onDeleteOffClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}

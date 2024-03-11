package com.example.florescer_juntos;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florescer_juntos.Model.Comentario;
import java.util.List;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ImageViewHolder> {
    private Context mContext;
    private static List<Comentario> mComentarios;
    private static String mUserId;
    private static OnItemClickListener mListener;

    public ComentarioAdapter(Context mContext, List<Comentario> mComentarios, String userId) {
        this.mContext = mContext;
        this.mComentarios = mComentarios;
        this.mUserId = userId;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.comentario_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Quem mostra os dados
        Comentario comentario = mComentarios.get(position);
        holder.nomeView.setText(comentario.getEmailUsuario());
        holder.descView.setText(comentario.getTexto());
    }

    @Override
    public int getItemCount() {
        return mComentarios.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView nomeView, descView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            // Instancio/linko os elementos
            nomeView = itemView.findViewById(R.id.nomeUsuario);
            descView = itemView.findViewById(R.id.textoComent);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Escolha uma ação");

            if (mUserId.equals("-NscKymtcrxWxhqItHyj")) {
                MenuItem deleteOff = menu.add(Menu.NONE, 1, 1, "Deletar comentário");
                deleteOff.setOnMenuItemClickListener(this);
            }
            if (mComentarios.get(getAdapterPosition()).getIdUsuario().equals(mUserId)) {
                MenuItem deleteOff = menu.add(Menu.NONE, 1, 1, "Deletar comentário");
                deleteOff.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (item.getItemId() == 1) {
                        mListener.onDeleteComentarioClick(position);
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

        void onDeleteComentarioClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

package com.catalogo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.catalogo.R;
import com.catalogo.model.Jogo;
import com.catalogo.model.Opcoes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JogoAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<Jogo> jogos = new ArrayList<>();

    public JogoAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setJogos(List<Jogo> novos) {
        jogos.clear();
        if (novos != null) {
            jogos.addAll(novos);
        }
        notifyDataSetChanged();
    }

    public Jogo getJogo(int position) {
        return jogos.get(position);
    }

    @Override
    public int getCount() {
        return jogos.size();
    }

    @Override
    public Object getItem(int position) {
        return jogos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return jogos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_jogo, parent, false);
            holder = new ViewHolder();
            holder.txtTitulo = convertView.findViewById(R.id.txtTitulo);
            holder.txtDetalhe = convertView.findViewById(R.id.txtDetalhe);
            holder.txtStatus = convertView.findViewById(R.id.txtStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Jogo jogo = jogos.get(position);
        holder.txtTitulo.setText(jogo.getTitulo());
        holder.txtDetalhe.setText(jogo.getPlataforma() + " · " + jogo.getAno() + " · " + jogo.getGenero());
        holder.txtStatus.setText(textoStatus(jogo));
        holder.txtStatus.setTextColor(context.getColor(corDoStatus(jogo.getStatus())));
        return convertView;
    }

    private String textoStatus(Jogo jogo) {
        if (jogo.getNota() == null) {
            return jogo.getStatus() + " · sem nota";
        }
        String nota = String.format(Locale.forLanguageTag("pt-BR"), "%.1f", jogo.getNota());
        return jogo.getStatus() + " · nota " + nota;
    }

    private int corDoStatus(String status) {
        if (Opcoes.STATUS_ZERADO.equals(status)) {
            return R.color.cor_zerado;
        }
        if ("Jogando".equals(status)) {
            return R.color.cor_jogando;
        }
        return R.color.cor_quero_jogar;
    }

    private static class ViewHolder {
        TextView txtTitulo;
        TextView txtDetalhe;
        TextView txtStatus;
    }
}

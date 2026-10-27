package com.catalogo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.catalogo.db.DBHelper;
import com.catalogo.model.Jogo;
import com.catalogo.model.Opcoes;

import java.util.ArrayList;
import java.util.List;

public class JogoDAO {

    private final DBHelper helper;

    public JogoDAO(Context context) {
        helper = new DBHelper(context.getApplicationContext());
    }

    public long inserir(Jogo jogo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insertOrThrow(DBHelper.TABELA_JOGO, null, valores(jogo));
    }

    public int atualizar(Jogo jogo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(
                DBHelper.TABELA_JOGO,
                valores(jogo),
                DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(jogo.getId())}
        );
    }

    public int excluir(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(
                DBHelper.TABELA_JOGO,
                DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public Jogo buscarPorId(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DBHelper.TABELA_JOGO,
                null,
                DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                return ler(cursor);
            }
            return null;
        }
    }

    public List<Jogo> listar(String busca, String status) {
        StringBuilder where = new StringBuilder("1=1");
        ArrayList<String> args = new ArrayList<>();

        if (busca != null && !busca.trim().isEmpty()) {
            where.append(" AND ").append(DBHelper.COL_TITULO).append(" LIKE ?");
            args.add("%" + busca.trim() + "%");
        }
        if (status != null && !status.isEmpty() && !Opcoes.FILTRO_TODOS.equals(status)) {
            where.append(" AND ").append(DBHelper.COL_STATUS).append(" = ?");
            args.add(status);
        }

        String ordem = "CASE WHEN " + DBHelper.COL_NOTA + " IS NULL THEN 1 ELSE 0 END, "
                + DBHelper.COL_NOTA + " DESC, "
                + DBHelper.COL_TITULO + " COLLATE NOCASE ASC";

        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Jogo> jogos = new ArrayList<>();
        try (Cursor cursor = db.query(
                DBHelper.TABELA_JOGO,
                null,
                where.toString(),
                args.toArray(new String[0]),
                null,
                null,
                ordem
        )) {
            while (cursor.moveToNext()) {
                jogos.add(ler(cursor));
            }
        }
        return jogos;
    }

    public Double mediaZerados() {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT AVG(" + DBHelper.COL_NOTA + ") FROM " + DBHelper.TABELA_JOGO
                        + " WHERE " + DBHelper.COL_STATUS + " = ? AND " + DBHelper.COL_NOTA + " IS NOT NULL",
                new String[]{Opcoes.STATUS_ZERADO}
        )) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getDouble(0);
            }
            return null;
        }
    }

    private ContentValues valores(Jogo jogo) {
        ContentValues valores = new ContentValues();
        valores.put(DBHelper.COL_TITULO, jogo.getTitulo());
        valores.put(DBHelper.COL_PLATAFORMA, jogo.getPlataforma());
        valores.put(DBHelper.COL_GENERO, jogo.getGenero());
        valores.put(DBHelper.COL_ANO, jogo.getAno());
        valores.put(DBHelper.COL_STATUS, jogo.getStatus());
        if (jogo.getNota() == null) {
            valores.putNull(DBHelper.COL_NOTA);
        } else {
            valores.put(DBHelper.COL_NOTA, jogo.getNota());
        }
        valores.put(DBHelper.COL_COMENTARIO, jogo.getComentario() == null ? "" : jogo.getComentario());
        return valores;
    }

    private Jogo ler(Cursor cursor) {
        Jogo jogo = new Jogo();
        jogo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
        jogo.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITULO)));
        jogo.setPlataforma(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PLATAFORMA)));
        jogo.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_GENERO)));
        jogo.setAno(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ANO)));
        jogo.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_STATUS)));
        int indiceNota = cursor.getColumnIndexOrThrow(DBHelper.COL_NOTA);
        if (cursor.isNull(indiceNota)) {
            jogo.setNota(null);
        } else {
            jogo.setNota(cursor.getDouble(indiceNota));
        }
        jogo.setComentario(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_COMENTARIO)));
        return jogo;
    }
}

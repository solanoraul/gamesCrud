package com.catalogo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "jogoteca.db";
    public static final int VERSAO = 1;

    public static final String TABELA_JOGO = "jogo";
    public static final String COL_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_PLATAFORMA = "plataforma";
    public static final String COL_GENERO = "genero";
    public static final String COL_ANO = "ano";
    public static final String COL_STATUS = "status";
    public static final String COL_NOTA = "nota";
    public static final String COL_COMENTARIO = "comentario";

    private static final String SQL_CRIAR_JOGO =
            "CREATE TABLE " + TABELA_JOGO + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TITULO + " TEXT NOT NULL, "
                    + COL_PLATAFORMA + " TEXT NOT NULL, "
                    + COL_GENERO + " TEXT NOT NULL, "
                    + COL_ANO + " INTEGER NOT NULL, "
                    + COL_STATUS + " TEXT NOT NULL, "
                    + COL_NOTA + " REAL, "
                    + COL_COMENTARIO + " TEXT"
                    + ")";

    public DBHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CRIAR_JOGO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_JOGO);
        onCreate(db);
    }
}

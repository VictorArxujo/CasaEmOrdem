package br.com.casaemordem.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Criação e versionamento do banco local do aplicativo. */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String NOME_BANCO = "casaemordem.db";
    public static final int VERSAO = 1;

    public static final String TABELA = "tarefa";
    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_AMBIENTE = "ambiente";
    public static final String COL_FREQUENCIA = "frequencia";
    public static final String COL_ULTIMA_EXECUCAO = "ultima_execucao";
    public static final String COL_EXECUCAO_ANTERIOR = "execucao_anterior";
    public static final String COL_CRIADA_EM = "criada_em";

    private static final String CRIAR_TABELA =
            "CREATE TABLE " + TABELA + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NOME + " TEXT NOT NULL, "
                    + COL_AMBIENTE + " TEXT NOT NULL, "
                    + COL_FREQUENCIA + " TEXT NOT NULL, "
                    + COL_ULTIMA_EXECUCAO + " INTEGER NOT NULL DEFAULT 0, "
                    + COL_EXECUCAO_ANTERIOR + " INTEGER NOT NULL DEFAULT 0, "
                    + COL_CRIADA_EM + " INTEGER NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase bancoDeDados) {
        bancoDeDados.execSQL(CRIAR_TABELA);
        inserirTarefasDeExemplo(bancoDeDados);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bancoDeDados, int versaoAntiga, int versaoNova) {
        bancoDeDados.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(bancoDeDados);
    }

    /** Carga inicial para que o aplicativo não abra vazio na primeira execução. */
    private void inserirTarefasDeExemplo(SQLiteDatabase bancoDeDados) {
        long agora = System.currentTimeMillis();
        inserirExemplo(bancoDeDados, "Lavar a louça", "Cozinha", "DIARIA", agora);
        inserirExemplo(bancoDeDados, "Trocar a roupa de cama", "Quarto", "SEMANAL", agora);
        inserirExemplo(bancoDeDados, "Lavar o banheiro", "Banheiro", "SEMANAL", agora);
        inserirExemplo(bancoDeDados, "Limpar a geladeira", "Cozinha", "MENSAL", agora);
        inserirExemplo(bancoDeDados, "Regar as plantas", "Área externa", "DIARIA", agora);
    }

    private void inserirExemplo(SQLiteDatabase bancoDeDados, String nome, String ambiente,
                                String frequencia, long agora) {
        bancoDeDados.execSQL(
                "INSERT INTO " + TABELA + " (" + COL_NOME + ", " + COL_AMBIENTE + ", "
                        + COL_FREQUENCIA + ", " + COL_ULTIMA_EXECUCAO + ", "
                        + COL_EXECUCAO_ANTERIOR + ", " + COL_CRIADA_EM + ") VALUES (?, ?, ?, 0, 0, ?)",
                new Object[]{nome, ambiente, frequencia, agora});
    }
}

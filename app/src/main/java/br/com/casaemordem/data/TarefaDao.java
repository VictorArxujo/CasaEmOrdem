package br.com.casaemordem.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.casaemordem.model.Frequencia;
import br.com.casaemordem.model.Tarefa;
import br.com.casaemordem.util.Datas;

/** Acesso às tarefas gravadas no banco local: CRUD, filtros do painel e check-in. */
public class TarefaDao {
    private final DatabaseHelper helper;

    public TarefaDao(Context context) {
        this.helper = new DatabaseHelper(context.getApplicationContext());
    }

    // ------------------------------------------------------------- CRUD

    public long inserir(Tarefa tarefa) {
        SQLiteDatabase banco = helper.getWritableDatabase();
        long id = banco.insert(DatabaseHelper.TABELA, null, paraContentValues(tarefa));
        tarefa.setId(id);
        return id;
    }

    public int atualizar(Tarefa tarefa) {
        SQLiteDatabase banco = helper.getWritableDatabase();
        return banco.update(DatabaseHelper.TABELA, paraContentValues(tarefa),
                DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(tarefa.getId())});
    }

    public int excluir(long id) {
        SQLiteDatabase banco = helper.getWritableDatabase();
        return banco.delete(DatabaseHelper.TABELA,
                DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Tarefa buscarPorId(long id) {
        SQLiteDatabase banco = helper.getReadableDatabase();
        Cursor cursor = banco.query(DatabaseHelper.TABELA, null,
                DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            return cursor.moveToFirst() ? paraTarefa(cursor) : null;
        } finally {
            cursor.close();
        }
    }

    public List<Tarefa> listarTodas() {
        return consultar(null, null);
    }

    public List<Tarefa> listarPorAmbiente(String ambiente) {
        return consultar(DatabaseHelper.COL_AMBIENTE + " = ?", new String[]{ambiente});
    }

    // --------------------------------------------------- painel e check-in

    /**
     * Tarefas que o painel do dia exibe: as pendentes ou atrasadas, mais as que
     * já foram marcadas hoje, para que o usuário possa desfazer o check-in.
     */
    public List<Tarefa> listarDoDia() {
        List<Tarefa> doDia = new ArrayList<>();
        for (Tarefa tarefa : listarTodas()) {
            if (tarefa.estaPendente() || tarefa.foiConcluidaHoje()) {
                doDia.add(tarefa);
            }
        }
        Collections.sort(doDia, new Comparator<Tarefa>() {
            @Override
            public int compare(Tarefa primeira, Tarefa segunda) {
                int diferenca = prioridade(primeira) - prioridade(segunda);
                if (diferenca != 0) return diferenca;
                return primeira.getNome().compareToIgnoreCase(segunda.getNome());
            }
        });
        return doDia;
    }

    /** Atrasadas no topo, concluídas no fim. */
    private int prioridade(Tarefa tarefa) {
        if (tarefa.foiConcluidaHoje()) return 2;
        if (tarefa.estaAtrasada()) return 0;
        return 1;
    }

    /** Registra a conclusão guardando a execução anterior, o que permite desfazer. */
    public void registrarExecucao(Tarefa tarefa) {
        tarefa.setExecucaoAnterior(tarefa.getUltimaExecucao());
        tarefa.setUltimaExecucao(Datas.hoje());
        atualizar(tarefa);
    }

    public void desfazerExecucao(Tarefa tarefa) {
        tarefa.setUltimaExecucao(tarefa.getExecucaoAnterior());
        tarefa.setExecucaoAnterior(Tarefa.NUNCA_EXECUTADA);
        atualizar(tarefa);
    }

    public int contarPendentes(List<Tarefa> tarefas) {
        int total = 0;
        for (Tarefa tarefa : tarefas) {
            if (!tarefa.foiConcluidaHoje()) total++;
        }
        return total;
    }

    public int contarPendentesDoAmbiente(String ambiente) {
        int total = 0;
        for (Tarefa tarefa : listarPorAmbiente(ambiente)) {
            if (tarefa.estaPendente()) total++;
        }
        return total;
    }

    public int contarTotalDoAmbiente(String ambiente) {
        return listarPorAmbiente(ambiente).size();
    }

    // ------------------------------------------------------------ apoio

    private List<Tarefa> consultar(String selecao, String[] argumentos) {
        SQLiteDatabase banco = helper.getReadableDatabase();
        Cursor cursor = banco.query(DatabaseHelper.TABELA, null, selecao, argumentos,
                null, null, DatabaseHelper.COL_NOME + " COLLATE NOCASE ASC");
        List<Tarefa> tarefas = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                tarefas.add(paraTarefa(cursor));
            }
        } finally {
            cursor.close();
        }
        return tarefas;
    }

    private ContentValues paraContentValues(Tarefa tarefa) {
        ContentValues valores = new ContentValues();
        valores.put(DatabaseHelper.COL_NOME, tarefa.getNome());
        valores.put(DatabaseHelper.COL_AMBIENTE, tarefa.getAmbiente());
        valores.put(DatabaseHelper.COL_FREQUENCIA, tarefa.getFrequencia().name());
        valores.put(DatabaseHelper.COL_ULTIMA_EXECUCAO, tarefa.getUltimaExecucao());
        valores.put(DatabaseHelper.COL_EXECUCAO_ANTERIOR, tarefa.getExecucaoAnterior());
        valores.put(DatabaseHelper.COL_CRIADA_EM, tarefa.getCriadaEm());
        return valores;
    }

    private Tarefa paraTarefa(Cursor cursor) {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        tarefa.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOME)));
        tarefa.setAmbiente(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMBIENTE)));
        tarefa.setFrequencia(Frequencia.porNome(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FREQUENCIA))));
        tarefa.setUltimaExecucao(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ULTIMA_EXECUCAO)));
        tarefa.setExecucaoAnterior(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXECUCAO_ANTERIOR)));
        tarefa.setCriadaEm(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CRIADA_EM)));
        return tarefa;
    }
}

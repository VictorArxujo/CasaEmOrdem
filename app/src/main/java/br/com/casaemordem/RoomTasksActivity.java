package br.com.casaemordem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.casaemordem.data.TarefaDao;
import br.com.casaemordem.model.Tarefa;
import br.com.casaemordem.ui.Navegacao;
import br.com.casaemordem.ui.TarefaAdapter;

/** Tarefas de um ambiente específico, abertas a partir da tela de Ambientes. */
public class RoomTasksActivity extends Activity {
    public static final String EXTRA_AMBIENTE = "br.com.casaemordem.EXTRA_AMBIENTE";

    private TarefaDao dao;
    private TarefaAdapter adapter;
    private final List<Tarefa> tarefas = new ArrayList<>();
    private String ambiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_tasks);

        ambiente = getIntent().getStringExtra(EXTRA_AMBIENTE);
        if (ambiente == null) {
            finish();
            return;
        }

        dao = new TarefaDao(this);
        ((TextView) findViewById(R.id.roomTitle)).setText(ambiente);

        adapter = new TarefaAdapter(this, tarefas, false, this::alternarConclusao);
        ListView lista = findViewById(R.id.roomTaskList);
        lista.setAdapter(adapter);
        lista.setEmptyView(findViewById(R.id.roomEmptyView));
        lista.setOnItemClickListener((pai, view, posicao, id) ->
                abrirEdicao(adapter.getItem(posicao).getId()));

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.addTaskButton).setOnClickListener(view -> abrirCadastro());
        Navegacao.configurar(this, Navegacao.ABA_AMBIENTES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void carregar() {
        tarefas.clear();
        tarefas.addAll(dao.listarPorAmbiente(ambiente));
        adapter.notifyDataSetChanged();

        TextView resumo = findViewById(R.id.roomSubtitle);
        int pendentes = dao.contarPendentesDoAmbiente(ambiente);
        resumo.setText(tarefas.isEmpty()
                ? "Nenhuma tarefa cadastrada neste ambiente."
                : tarefas.size() + (tarefas.size() == 1 ? " tarefa  ·  " : " tarefas  ·  ")
                        + pendentes + " pendente" + (pendentes == 1 ? "" : "s"));
    }

    private void alternarConclusao(Tarefa tarefa, boolean concluida) {
        if (concluida) {
            dao.registrarExecucao(tarefa);
        } else {
            dao.desfazerExecucao(tarefa);
        }
        carregar();
    }

    /** O ambiente da tela já segue para o formulário, dispensando a escolha manual. */
    private void abrirCadastro() {
        Intent intent = new Intent(this, NewTaskActivity.class);
        intent.putExtra(NewTaskActivity.EXTRA_AMBIENTE, ambiente);
        startActivityForResult(intent, MainActivity.REQUISICAO_TAREFA);
    }

    private void abrirEdicao(long idDaTarefa) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        intent.putExtra(NewTaskActivity.EXTRA_TAREFA_ID, idDaTarefa);
        startActivityForResult(intent, MainActivity.REQUISICAO_TAREFA);
    }

    @Override
    protected void onActivityResult(int requisicao, int resultado, Intent dados) {
        super.onActivityResult(requisicao, resultado, dados);
        if (requisicao == MainActivity.REQUISICAO_TAREFA && resultado == RESULT_OK && dados != null) {
            String mensagem = dados.getStringExtra(NewTaskActivity.EXTRA_MENSAGEM);
            if (mensagem != null) {
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
            }
        }
        carregar();
    }
}

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
import br.com.casaemordem.util.Datas;

/** Painel do dia: lista as tarefas pendentes ou atrasadas e recebe o check-in. */
public class MainActivity extends Activity {
    public static final int REQUISICAO_TAREFA = 101;

    private TarefaDao dao;
    private TarefaAdapter adapter;
    private final List<Tarefa> tarefasDoDia = new ArrayList<>();

    private TextView resumoDoDia;
    private TextView dicaDoDia;
    private TextView contadorConcluidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new TarefaDao(this);
        resumoDoDia = findViewById(R.id.todaySummary);
        dicaDoDia = findViewById(R.id.todayHint);
        contadorConcluidas = findViewById(R.id.completedCount);

        TextView dataDeHoje = findViewById(R.id.todayDate);
        dataDeHoje.setText(Datas.formatarDiaPorExtenso(System.currentTimeMillis()));

        adapter = new TarefaAdapter(this, tarefasDoDia, true, this::alternarConclusao);
        ListView lista = findViewById(R.id.taskList);
        lista.setAdapter(adapter);
        lista.setEmptyView(findViewById(R.id.emptyView));
        lista.setOnItemClickListener((pai, view, posicao, id) ->
                abrirEdicao(adapter.getItem(posicao).getId()));

        findViewById(R.id.addTaskButton).setOnClickListener(view -> abrirCadastro());
        Navegacao.configurar(this, Navegacao.ABA_HOJE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    /** Relê o banco para refletir alterações feitas nas outras telas. */
    private void carregar() {
        tarefasDoDia.clear();
        tarefasDoDia.addAll(dao.listarDoDia());
        adapter.notifyDataSetChanged();
        atualizarResumo();
    }

    private void atualizarResumo() {
        int total = tarefasDoDia.size();
        int pendentes = dao.contarPendentes(tarefasDoDia);
        int concluidas = total - pendentes;

        if (total == 0) {
            resumoDoDia.setText("Nada pendente\npor aqui");
            dicaDoDia.setText("Cadastre uma tarefa para começar a organizar a casa.");
        } else if (pendentes == 0) {
            resumoDoDia.setText("Tudo em ordem\npor hoje");
            dicaDoDia.setText("Você concluiu todas as tarefas do dia. Bom descanso!");
        } else {
            resumoDoDia.setText(pendentes + (pendentes == 1 ? " tarefa\npara cuidar" : " tarefas\npara cuidar"));
            dicaDoDia.setText("Um passo de cada vez já faz diferença.");
        }
        contadorConcluidas.setText(concluidas + " de " + total + " feitas");
    }

    /** Check-in: grava a data da execução no banco e recarrega o painel. */
    private void alternarConclusao(Tarefa tarefa, boolean concluida) {
        if (concluida) {
            dao.registrarExecucao(tarefa);
        } else {
            dao.desfazerExecucao(tarefa);
        }
        carregar();
    }

    private void abrirCadastro() {
        startActivityForResult(new Intent(this, NewTaskActivity.class), REQUISICAO_TAREFA);
    }

    private void abrirEdicao(long idDaTarefa) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        intent.putExtra(NewTaskActivity.EXTRA_TAREFA_ID, idDaTarefa);
        startActivityForResult(intent, REQUISICAO_TAREFA);
    }

    /** Recebe de volta a mensagem do cadastro e atualiza a lista. */
    @Override
    protected void onActivityResult(int requisicao, int resultado, Intent dados) {
        super.onActivityResult(requisicao, resultado, dados);
        if (requisicao == REQUISICAO_TAREFA && resultado == RESULT_OK && dados != null) {
            String mensagem = dados.getStringExtra(NewTaskActivity.EXTRA_MENSAGEM);
            if (mensagem != null) {
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
            }
        }
        carregar();
    }
}

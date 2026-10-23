package br.com.casaemordem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import br.com.casaemordem.data.TarefaDao;
import br.com.casaemordem.model.Frequencia;
import br.com.casaemordem.model.Tarefa;
import br.com.casaemordem.ui.Navegacao;
import br.com.casaemordem.util.Datas;

/**
 * Formulário de tarefa. A mesma tela atende o cadastro e a edição: quando recebe
 * um identificador, carrega a tarefa do banco e habilita a exclusão.
 */
public class NewTaskActivity extends Activity {
    public static final String EXTRA_AMBIENTE = "br.com.casaemordem.EXTRA_AMBIENTE";
    public static final String EXTRA_TAREFA_ID = "br.com.casaemordem.EXTRA_TAREFA_ID";
    public static final String EXTRA_MENSAGEM = "br.com.casaemordem.EXTRA_MENSAGEM";

    private TarefaDao dao;
    private Tarefa tarefaEmEdicao;

    private EditText campoNome;
    private Spinner campoAmbiente;
    private Spinner campoFrequencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        dao = new TarefaDao(this);
        campoNome = findViewById(R.id.taskName);
        campoAmbiente = findViewById(R.id.roomSpinner);
        campoFrequencia = findViewById(R.id.frequencySpinner);

        campoAmbiente.setAdapter(criarAdapter(getResources().getStringArray(R.array.rooms)));
        campoFrequencia.setAdapter(criarAdapter(Frequencia.rotulos()));

        long idRecebido = getIntent().getLongExtra(EXTRA_TAREFA_ID, 0L);
        if (idRecebido > 0) {
            prepararEdicao(idRecebido);
        } else {
            prepararCadastro();
        }

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.saveTaskButton).setOnClickListener(view -> salvar());
        findViewById(R.id.deleteTaskButton).setOnClickListener(view -> confirmarExclusao());
        Navegacao.configurar(this, -1);
    }

    private ArrayAdapter<String> criarAdapter(String[] itens) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, itens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    /** Cadastro novo, já posicionado no ambiente de origem quando houver. */
    private void prepararCadastro() {
        ((TextView) findViewById(R.id.formTitle)).setText("Nova tarefa");
        ((TextView) findViewById(R.id.formSubtitle)).setText("O que precisa ser feito?");
        findViewById(R.id.deleteTaskButton).setVisibility(View.GONE);
        findViewById(R.id.lastRunLabel).setVisibility(View.GONE);
        selecionar(campoAmbiente, getIntent().getStringExtra(EXTRA_AMBIENTE));
    }

    private void prepararEdicao(long idDaTarefa) {
        tarefaEmEdicao = dao.buscarPorId(idDaTarefa);
        if (tarefaEmEdicao == null) {
            prepararCadastro();
            return;
        }
        ((TextView) findViewById(R.id.formTitle)).setText("Editar tarefa");
        ((TextView) findViewById(R.id.formSubtitle)).setText("Altere os dados ou exclua a tarefa.");
        campoNome.setText(tarefaEmEdicao.getNome());
        selecionar(campoAmbiente, tarefaEmEdicao.getAmbiente());
        selecionar(campoFrequencia, tarefaEmEdicao.getFrequencia().getRotulo());

        TextView ultimaExecucao = findViewById(R.id.lastRunLabel);
        ultimaExecucao.setVisibility(View.VISIBLE);
        ultimaExecucao.setText(tarefaEmEdicao.jaFoiExecutada()
                ? "Última execução: " + Datas.formatarCurto(tarefaEmEdicao.getUltimaExecucao())
                : "Esta tarefa ainda não foi realizada.");
    }

    private void selecionar(Spinner campo, String valor) {
        if (valor == null) return;
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) campo.getAdapter();
        for (int posicao = 0; posicao < adapter.getCount(); posicao++) {
            if (valor.equals(String.valueOf(adapter.getItem(posicao)))) {
                campo.setSelection(posicao);
                return;
            }
        }
    }

    private void salvar() {
        String nome = campoNome.getText().toString().trim();
        if (nome.isEmpty()) {
            campoNome.setError("Informe o nome da tarefa");
            campoNome.requestFocus();
            return;
        }

        String ambiente = String.valueOf(campoAmbiente.getSelectedItem());
        Frequencia frequencia = Frequencia.porRotulo(String.valueOf(campoFrequencia.getSelectedItem()));

        String mensagem;
        if (tarefaEmEdicao == null) {
            dao.inserir(new Tarefa(nome, ambiente, frequencia));
            mensagem = "Tarefa adicionada";
        } else {
            tarefaEmEdicao.setNome(nome);
            tarefaEmEdicao.setAmbiente(ambiente);
            tarefaEmEdicao.setFrequencia(frequencia);
            dao.atualizar(tarefaEmEdicao);
            mensagem = "Tarefa atualizada";
        }
        encerrarCom(mensagem);
    }

    private void confirmarExclusao() {
        if (tarefaEmEdicao == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Excluir tarefa")
                .setMessage("Deseja excluir \"" + tarefaEmEdicao.getNome() + "\"? "
                        + "Esta ação não pode ser desfeita.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir", (dialogo, botao) -> {
                    dao.excluir(tarefaEmEdicao.getId());
                    encerrarCom("Tarefa excluída");
                })
                .show();
    }

    /** Devolve o resultado para a tela que abriu o formulário. */
    private void encerrarCom(String mensagem) {
        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_MENSAGEM, mensagem);
        setResult(RESULT_OK, resultado);
        finish();
    }
}

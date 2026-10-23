package br.com.casaemordem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.casaemordem.data.TarefaDao;
import br.com.casaemordem.ui.AmbienteAdapter;
import br.com.casaemordem.ui.Navegacao;

/** Ambientes da casa, cada um com o total de tarefas e quantas estão pendentes. */
public class RoomsActivity extends Activity {
    private TarefaDao dao;
    private AmbienteAdapter adapter;
    private final List<AmbienteAdapter.ResumoAmbiente> ambientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        dao = new TarefaDao(this);
        adapter = new AmbienteAdapter(this, ambientes);

        ListView lista = findViewById(R.id.roomList);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener((pai, view, posicao, id) ->
                abrirAmbiente(adapter.getItem(posicao).nome));

        Navegacao.configurar(this, Navegacao.ABA_AMBIENTES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void carregar() {
        ambientes.clear();
        int pendentesNoTotal = 0;
        for (String ambiente : getResources().getStringArray(R.array.rooms)) {
            int total = dao.contarTotalDoAmbiente(ambiente);
            int pendentes = dao.contarPendentesDoAmbiente(ambiente);
            pendentesNoTotal += pendentes;
            ambientes.add(new AmbienteAdapter.ResumoAmbiente(ambiente, total, pendentes));
        }
        adapter.notifyDataSetChanged();

        TextView resumo = findViewById(R.id.roomsSummary);
        resumo.setText(pendentesNoTotal == 0
                ? "Nenhuma tarefa pendente na casa."
                : pendentesNoTotal + (pendentesNoTotal == 1
                        ? " tarefa pendente na casa." : " tarefas pendentes na casa."));
    }

    private void abrirAmbiente(String ambiente) {
        Intent intent = new Intent(this, RoomTasksActivity.class);
        intent.putExtra(RoomTasksActivity.EXTRA_AMBIENTE, ambiente);
        startActivity(intent);
    }
}

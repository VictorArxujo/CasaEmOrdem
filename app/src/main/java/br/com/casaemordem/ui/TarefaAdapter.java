package br.com.casaemordem.ui;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import br.com.casaemordem.R;
import br.com.casaemordem.model.Tarefa;

/** Lista de tarefas com check-in. Usada tanto no painel do dia quanto na tela de ambiente. */
public class TarefaAdapter extends BaseAdapter {

    /** Avisa a Activity de que o usuário marcou ou desmarcou uma tarefa. */
    public interface AoAlternarConclusao {
        void alternar(Tarefa tarefa, boolean concluida);
    }

    private final Context contexto;
    private final List<Tarefa> tarefas;
    private final AoAlternarConclusao ouvinte;
    private final boolean exibirAmbiente;

    public TarefaAdapter(Context contexto, List<Tarefa> tarefas, boolean exibirAmbiente,
                         AoAlternarConclusao ouvinte) {
        this.contexto = contexto;
        this.tarefas = tarefas;
        this.exibirAmbiente = exibirAmbiente;
        this.ouvinte = ouvinte;
    }

    @Override
    public int getCount() {
        return tarefas.size();
    }

    @Override
    public Tarefa getItem(int posicao) {
        return tarefas.get(posicao);
    }

    @Override
    public long getItemId(int posicao) {
        return tarefas.get(posicao).getId();
    }

    @Override
    public View getView(int posicao, View reaproveitada, ViewGroup pai) {
        View linha = reaproveitada;
        if (linha == null) {
            linha = LayoutInflater.from(contexto).inflate(R.layout.item_tarefa, pai, false);
        }

        final Tarefa tarefa = getItem(posicao);
        CheckBox marcador = linha.findViewById(R.id.itemCheck);
        TextView nome = linha.findViewById(R.id.itemNome);
        TextView detalhe = linha.findViewById(R.id.itemDetalhe);
        TextView situacao = linha.findViewById(R.id.itemSituacao);

        nome.setText(tarefa.getNome());
        detalhe.setText(exibirAmbiente
                ? tarefa.getAmbiente() + "  ·  " + tarefa.getFrequencia().getRotulo()
                : tarefa.getFrequencia().getRotulo());
        situacao.setText(tarefa.descricaoStatus());

        boolean concluidaHoje = tarefa.foiConcluidaHoje();
        marcador.setChecked(concluidaHoje);
        // O clique é tratado aqui, e não em OnCheckedChangeListener, para que o
        // reaproveitamento de linhas da ListView não dispare o evento sozinho.
        marcador.setEnabled(tarefa.estaPendente() || concluidaHoje);
        marcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                ouvinte.alternar(tarefa, ((CheckBox) botao).isChecked());
            }
        });

        int estilo = nome.getPaintFlags();
        nome.setPaintFlags(concluidaHoje
                ? estilo | Paint.STRIKE_THRU_TEXT_FLAG
                : estilo & ~Paint.STRIKE_THRU_TEXT_FLAG);
        nome.setTextColor(contexto.getResources().getColor(
                concluidaHoje ? R.color.muted : R.color.ink));
        situacao.setTextColor(contexto.getResources().getColor(
                tarefa.estaAtrasada() ? R.color.coral : R.color.muted));

        return linha;
    }
}

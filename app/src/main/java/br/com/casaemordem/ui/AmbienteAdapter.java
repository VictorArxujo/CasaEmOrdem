package br.com.casaemordem.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.casaemordem.R;

/** Lista de ambientes da casa com o resumo de tarefas de cada um. */
public class AmbienteAdapter extends BaseAdapter {

    /** Ambiente e seus totais, montados pela Activity a partir do banco. */
    public static class ResumoAmbiente {
        public final String nome;
        public final int total;
        public final int pendentes;

        public ResumoAmbiente(String nome, int total, int pendentes) {
            this.nome = nome;
            this.total = total;
            this.pendentes = pendentes;
        }
    }

    private final Context contexto;
    private final List<ResumoAmbiente> ambientes;

    public AmbienteAdapter(Context contexto, List<ResumoAmbiente> ambientes) {
        this.contexto = contexto;
        this.ambientes = ambientes;
    }

    @Override
    public int getCount() {
        return ambientes.size();
    }

    @Override
    public ResumoAmbiente getItem(int posicao) {
        return ambientes.get(posicao);
    }

    @Override
    public long getItemId(int posicao) {
        return posicao;
    }

    @Override
    public View getView(int posicao, View reaproveitada, ViewGroup pai) {
        View linha = reaproveitada;
        if (linha == null) {
            linha = LayoutInflater.from(contexto).inflate(R.layout.item_ambiente, pai, false);
        }

        ResumoAmbiente ambiente = getItem(posicao);
        TextView nome = linha.findViewById(R.id.ambienteNome);
        TextView resumo = linha.findViewById(R.id.ambienteResumo);

        nome.setText(ambiente.nome);
        if (ambiente.total == 0) {
            resumo.setText("Nenhuma tarefa cadastrada");
        } else {
            String tarefas = ambiente.total == 1 ? "1 tarefa" : ambiente.total + " tarefas";
            resumo.setText(tarefas + "  ·  " + ambiente.pendentes + " pendente"
                    + (ambiente.pendentes == 1 ? "" : "s"));
        }
        return linha;
    }
}

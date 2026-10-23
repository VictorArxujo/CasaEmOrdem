package br.com.casaemordem.model;

import br.com.casaemordem.util.Datas;

/**
 * Tarefa doméstica. Além dos dados persistidos, concentra as regras que decidem
 * se a tarefa deve aparecer no painel do dia e se está atrasada.
 */
public class Tarefa {
    public static final long NUNCA_EXECUTADA = 0L;

    private long id;
    private String nome;
    private String ambiente;
    private Frequencia frequencia = Frequencia.PONTUAL;
    private long ultimaExecucao = NUNCA_EXECUTADA;
    private long execucaoAnterior = NUNCA_EXECUTADA;
    private long criadaEm = System.currentTimeMillis();

    public Tarefa() {
    }

    public Tarefa(String nome, String ambiente, Frequencia frequencia) {
        this.nome = nome;
        this.ambiente = ambiente;
        this.frequencia = frequencia;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public Frequencia getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Frequencia frequencia) {
        this.frequencia = frequencia;
    }

    public long getUltimaExecucao() {
        return ultimaExecucao;
    }

    public void setUltimaExecucao(long ultimaExecucao) {
        this.ultimaExecucao = ultimaExecucao;
    }

    public long getExecucaoAnterior() {
        return execucaoAnterior;
    }

    public void setExecucaoAnterior(long execucaoAnterior) {
        this.execucaoAnterior = execucaoAnterior;
    }

    public long getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(long criadaEm) {
        this.criadaEm = criadaEm;
    }

    // ------------------------------------------------------------- regras

    public boolean jaFoiExecutada() {
        return ultimaExecucao != NUNCA_EXECUTADA;
    }

    /** Dias decorridos desde a última conclusão, ou -1 se a tarefa nunca foi feita. */
    public int diasDesdeUltimaExecucao() {
        if (!jaFoiExecutada()) return -1;
        return Datas.diasEntre(ultimaExecucao, System.currentTimeMillis());
    }

    public boolean foiConcluidaHoje() {
        return jaFoiExecutada() && diasDesdeUltimaExecucao() == 0;
    }

    /**
     * A tarefa está pendente quando nunca foi feita ou quando o intervalo da
     * recorrência já se completou. Tarefa pontual concluída sai do painel.
     */
    public boolean estaPendente() {
        if (!jaFoiExecutada()) return true;
        if (!frequencia.isRecorrente()) return false;
        return diasDesdeUltimaExecucao() >= frequencia.getIntervaloDias();
    }

    /** Recorrente cujo prazo já passou do intervalo previsto. */
    public boolean estaAtrasada() {
        if (!frequencia.isRecorrente() || !jaFoiExecutada()) return false;
        return diasDesdeUltimaExecucao() > frequencia.getIntervaloDias();
    }

    /** Texto curto de apoio exibido na lista. */
    public String descricaoStatus() {
        if (foiConcluidaHoje()) return "Concluída hoje";
        if (!jaFoiExecutada()) return "Nunca realizada";
        if (estaAtrasada()) {
            int atraso = diasDesdeUltimaExecucao() - frequencia.getIntervaloDias();
            return atraso == 1 ? "Atrasada 1 dia" : "Atrasada " + atraso + " dias";
        }
        int dias = diasDesdeUltimaExecucao();
        if (dias == 1) return "Feita ontem";
        return "Feita em " + Datas.formatarCurto(ultimaExecucao);
    }
}

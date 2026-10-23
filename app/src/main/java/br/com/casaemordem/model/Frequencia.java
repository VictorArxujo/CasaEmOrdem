package br.com.casaemordem.model;

/**
 * Frequência de repetição de uma tarefa. O intervalo em dias é o que alimenta
 * o cálculo de recorrência do painel; o valor zero marca a tarefa como pontual.
 */
public enum Frequencia {
    PONTUAL("Pontual", 0),
    DIARIA("Diária", 1),
    SEMANAL("Semanal", 7),
    QUINZENAL("Quinzenal", 15),
    MENSAL("Mensal", 30);

    private final String rotulo;
    private final int intervaloDias;

    Frequencia(String rotulo, int intervaloDias) {
        this.rotulo = rotulo;
        this.intervaloDias = intervaloDias;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getIntervaloDias() {
        return intervaloDias;
    }

    public boolean isRecorrente() {
        return intervaloDias > 0;
    }

    public static Frequencia porNome(String nome) {
        for (Frequencia frequencia : values()) {
            if (frequencia.name().equals(nome)) return frequencia;
        }
        return PONTUAL;
    }

    public static Frequencia porRotulo(String rotulo) {
        for (Frequencia frequencia : values()) {
            if (frequencia.rotulo.equals(rotulo)) return frequencia;
        }
        return PONTUAL;
    }

    public static String[] rotulos() {
        Frequencia[] valores = values();
        String[] lista = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            lista[i] = valores[i].rotulo;
        }
        return lista;
    }
}

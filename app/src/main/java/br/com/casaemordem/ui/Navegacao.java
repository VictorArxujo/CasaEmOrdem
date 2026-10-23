package br.com.casaemordem.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import br.com.casaemordem.MainActivity;
import br.com.casaemordem.R;
import br.com.casaemordem.RoomsActivity;

/** Liga a barra inferior às telas principais, mantendo uma única instância de cada uma. */
public final class Navegacao {
    public static final int ABA_HOJE = 0;
    public static final int ABA_AMBIENTES = 1;

    private Navegacao() {
    }

    public static void configurar(final Activity tela, final int abaAtiva) {
        TextView hoje = tela.findViewById(R.id.navToday);
        TextView ambientes = tela.findViewById(R.id.navRooms);
        if (hoje == null || ambientes == null) return;

        destacar(tela, hoje, abaAtiva == ABA_HOJE);
        destacar(tela, ambientes, abaAtiva == ABA_AMBIENTES);

        hoje.setOnClickListener(view -> {
            if (abaAtiva != ABA_HOJE) abrir(tela, MainActivity.class);
        });
        ambientes.setOnClickListener(view -> {
            if (abaAtiva != ABA_AMBIENTES) abrir(tela, RoomsActivity.class);
        });
    }

    private static void destacar(Activity tela, TextView aba, boolean ativa) {
        aba.setTextColor(tela.getResources().getColor(ativa ? R.color.forest : R.color.muted));
        aba.setTypeface(null, ativa ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    /** CLEAR_TOP com SINGLE_TOP evita empilhar cópias das telas ao alternar as abas. */
    private static void abrir(Activity origem, Class<?> destino) {
        Intent intent = new Intent(origem, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        origem.startActivity(intent);
    }
}

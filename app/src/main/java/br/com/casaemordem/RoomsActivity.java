package br.com.casaemordem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RoomsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        findViewById(R.id.navToday).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        findViewById(R.id.navRooms).setOnClickListener(view -> { });
        findViewById(R.id.roomKitchen).setOnClickListener(view -> openNewTask("Cozinha"));
        findViewById(R.id.roomBathroom).setOnClickListener(view -> openNewTask("Banheiro"));
        findViewById(R.id.roomBedroom).setOnClickListener(view -> openNewTask("Quarto"));
        findViewById(R.id.roomLiving).setOnClickListener(view -> openNewTask("Sala"));
        findViewById(R.id.roomOutdoor).setOnClickListener(view -> openNewTask("Área externa"));
    }

    private void openNewTask(String room) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        intent.putExtra(NewTaskActivity.EXTRA_ROOM, room);
        startActivity(intent);
    }
}
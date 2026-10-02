package br.com.casaemordem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewTaskActivity extends Activity {
    public static final String EXTRA_ROOM = "br.com.casaemordem.EXTRA_ROOM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        Spinner roomSpinner = findViewById(R.id.roomSpinner);
        String selectedRoom = getIntent().getStringExtra(EXTRA_ROOM);
        if (selectedRoom != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.rooms, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roomSpinner.setAdapter(adapter);
            for (int index = 0; index < adapter.getCount(); index++) {
                if (selectedRoom.equals(adapter.getItem(index).toString())) {
                    roomSpinner.setSelection(index);
                    break;
                }
            }
        }

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.navToday).setOnClickListener(view -> openMain());
        findViewById(R.id.navRooms).setOnClickListener(view ->
                startActivity(new android.content.Intent(this, RoomsActivity.class)));
        findViewById(R.id.saveTaskButton).setOnClickListener(view -> saveTask());
    }

    private void saveTask() {
        EditText taskName = findViewById(R.id.taskName);
        if (taskName.getText().toString().trim().isEmpty()) {
            taskName.setError("Informe o nome da tarefa");
            taskName.requestFocus();
            return;
        }
        Toast.makeText(this, "Tarefa adicionada nesta demonstração", Toast.LENGTH_SHORT).show();
        openMain();
    }

    private void openMain() {
        startActivity(new android.content.Intent(this, MainActivity.class));
        finish();
    }
}
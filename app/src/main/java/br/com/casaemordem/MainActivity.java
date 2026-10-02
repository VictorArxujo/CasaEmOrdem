package br.com.casaemordem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.addTaskButton).setOnClickListener(view -> openNewTask(null));
        findViewById(R.id.navRooms).setOnClickListener(view ->
                startActivity(new Intent(this, RoomsActivity.class)));
        findViewById(R.id.navToday).setOnClickListener(view -> { });

        CheckBox bedTask = findViewById(R.id.taskBed);
        CheckBox kitchenTask = findViewById(R.id.taskKitchen);
        CheckBox bathroomTask = findViewById(R.id.taskBathroom);
        TextView progress = findViewById(R.id.completedCount);
        CheckBox[] tasks = {bedTask, kitchenTask, bathroomTask};
        for (CheckBox task : tasks) {
            task.setOnCheckedChangeListener((button, checked) -> {
                int completed = 0;
                for (CheckBox item : tasks) {
                    if (item.isChecked()) completed++;
                }
                progress.setText(completed + " de 3 feitas");
            });
        }
    }

    private void openNewTask(String room) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        if (room != null) intent.putExtra(NewTaskActivity.EXTRA_ROOM, room);
        startActivity(intent);
    }
}
package io.madcamp.jh.madcamp_assignment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JsoncontactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoncontact);

        Intent intent = getIntent();
        String json = intent.getStringExtra("JSON");

        EditText editText = findViewById(R.id.edit_text);
        editText.setText(json);

        Button buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonUpdate = findViewById(R.id.button_update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                EditText editText = findViewById(R.id.edit_text);
                String json = editText.getText().toString();
                intent.putExtra("JSON", json);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}

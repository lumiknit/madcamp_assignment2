package io.madcamp.jh.madcamp_assignment2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddcontactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        final Button add_button = findViewById(R.id.add_button);
        final EditText name_editText = findViewById(R.id.name_edittext);
        final EditText number_editText = findViewById(R.id.number_edittext);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name_editText.getText().toString().length() == 0 ||
                        number_editText.getText().toString().length() == 0) {
                    /* When some editText was not filled */
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddcontactActivity.this);
                    builder.setMessage("두 항목 모두 작성해주세요.");
                    builder.setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("contact_name",name_editText.getText().toString());
                    resultIntent.putExtra("contact_num", number_editText.getText().toString());
                    setResult(AddcontactActivity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}

package com.example.milkman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    String[] id = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String selected_id_view;
    int flag = 0;
    Button view_text;
    EditText displayText;
    String sentence,total_view;
    ImageButton edit_data_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, id);
        autoCompleteTextView.setAdapter(adapterItems);

        // ID selector
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_id_view = adapterView.getItemAtPosition(i).toString();
                flag = 1;
                autoCompleteTextView.setError(null);

            }
        });

        displayText = findViewById(R.id.data_field);
        displayText.setEnabled(false);
        view_text = findViewById(R.id.data_view_btn);
        edit_data_btn = findViewById(R.id.edit_btn);
        edit_data_btn.setOnClickListener(v->editView());
        view_text.setOnClickListener(v -> createView());

    }

    protected void onPause() {
        super.onPause();
    }
    protected void onStart() {
        super.onStart();
    }
    protected void onRestart() {
        super.onRestart();
    }
    protected void onResume() {
        super.onResume();
    }
    protected void onStop() {
        super.onStop();
    }
    protected void onDestroy() {
        super.onDestroy();
    }

    void editView()
    {
        if(flag==0)
        {
            autoCompleteTextView.setError("Please select the ID to Edit");
            return;
        }
        autoCompleteTextView.setError(null);
        Intent intent = new Intent(ViewActivity.this, EditActivity.class);
        intent.putExtra("ID", selected_id_view);
        startActivity(intent);
    }
    void createView() {
        if(flag==0)
        {
            autoCompleteTextView.setError("Please select the ID");
            return;
        }
        autoCompleteTextView.setError(null);
        total_view="";
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "Milkman/" + selected_id_view + ".txt");
           // List<String> lines = new ArrayList<>();
            if(file.exists()) {
                displayText.setText("");
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    while ((sentence = br.readLine()) != null) { // Reads a line, null indicates end of file
                        //lines.add(sentence);
                        total_view += sentence + "\n";
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                displayText.setText(total_view);
            }
            else {
                Toast.makeText(ViewActivity.this,"No Data found",Toast.LENGTH_SHORT).show();
                displayText.setText("");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
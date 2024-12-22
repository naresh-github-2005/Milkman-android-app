package com.example.milkman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    String selected_edit_id;
    Button delete_data_btn;
    EditText edit_field,input_row_number;
    String sentence,total_view;
    int total_lines;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        selected_edit_id = intent.getStringExtra("ID");

        input_row_number = findViewById(R.id.row_no_field);
        edit_field = findViewById(R.id.edit_data_field);
        edit_field.setEnabled(false);
        displayData();
        delete_data_btn = findViewById(R.id.delete_btn);
        delete_data_btn.setOnClickListener(v->deleteData());
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

    void displayData()
    {
        total_lines=0;
        total_view="";
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "Milkman/" + selected_edit_id + ".txt");
            if(file.exists()) {
                edit_field.setText("");
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    while ((sentence = br.readLine()) != null) {    // Reads a line, null indicates end of file
                        total_lines++;
                        total_view += sentence + "\n";
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                edit_field.setText(total_view);
                System.out.println("lines "+total_lines);
            }
            else {
                Toast.makeText(EditActivity.this,"No Data found to edit",Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    void deleteData()
    {
        String line_no = input_row_number.getText().toString().trim();
        if(line_no==null || line_no.isEmpty())
        {
            input_row_number.setError("Row number is required");
            return;
        }
        if(!line_no.matches("\\d+"))
        {
            input_row_number.setError("Row number must be in Integer");
            return;
        }
        int row_number = Integer.parseInt(line_no);
        if(row_number>(total_lines-11))
        {
            input_row_number.setError("Given input row doesn't exist");
            return;
        }
        else {
            String [] arr_row;
            int lineCount=0;
            float row_amount;
            String stotal = "";
            float total = 0.00f;
            input_row_number.setError(null);
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "Milkman/" + selected_edit_id + ".txt");
                if(total_lines-11==1 && row_number==1)
                {
                    file.delete();
                    edit_field.setText("");
                    Toast.makeText(EditActivity.this, "Customer ID "+selected_edit_id+"\ndata deleted", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(EditActivity.this,ViewActivity.class));
                }
                else {
                    List<String> lines = new ArrayList<>();
                    if (file.exists()) {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            while ((sentence = br.readLine()) != null) { // Reads a line, null indicates end of file
                                lineCount++;
                                if (lineCount < (row_number + 8)) {

                                    if (lineCount > 8) {
                                        arr_row = sentence.split("\\|");
                                        stotal = arr_row[5].trim();
                                        total += Float.parseFloat(stotal);
                                    }
                                    lines.add(sentence);
                                } else if ((row_number + 8) < lineCount) {
                                    if (sentence.charAt(0) != '|')
                                        break;
                                    String new_sno = "", date = "", price = "", quantity = "", amount = "";
                                    int sno;
                                    int lnew_sno, ldate, lprice, lquantity, lamount;

                                    arr_row = sentence.split("\\|");
                                    new_sno = arr_row[1].trim();
                                    sno = Integer.parseInt(new_sno) - 1;
                                    date = arr_row[2].trim();
                                    price = arr_row[3].trim();
                                    quantity = arr_row[4].trim();
                                    amount = arr_row[5].trim();
                                    total += Float.parseFloat(amount);

                                    lnew_sno = new_sno.length();
                                    ldate = date.length();
                                    lprice = price.length();
                                    lquantity = quantity.length();
                                    lamount = amount.length();
                                    lines.add("| " + sno + " ".repeat(7 - lnew_sno) + "| " + date + " ".repeat(15 - ldate) + "| " + price + " ".repeat(14 - lprice) + "| " + quantity + " ".repeat(12 - lquantity) + "| " + " ".repeat(14 - lamount) + amount + "|");
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(EditActivity.this, "No Data found", Toast.LENGTH_SHORT).show();
                    }

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        for (int i = 0; i < lines.size(); i++) {
                            writer.write(lines.get(i));
                            writer.newLine();
                        }
                        String total_amount = String.format("%.2f", total);
                        int total_amount_length = total_amount.length();
                        writer.write("-".repeat(73) + "\n");
                        writer.write(" ".repeat(42) + "| TOTAL :     |" + " ".repeat(15 - total_amount_length) + total_amount + "|\n");
                        writer.write(" ".repeat(42) + "-".repeat(31) + "\n");
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + e.getMessage());
                    }
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            displayData();
            Toast.makeText(EditActivity.this,"Row number "+row_number+"\ndeleted",Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.milkman;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText input_quantity,selectdate,current_price;
    private int mYear,mMonth,mDay;
    ImageButton save_btn,logout_button;
    Button viewData,changePrice;
    String[] id = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String selected_id;
    String quantity_litres,cPrice;
    String chosen_date;
    float cmilkPrice;
    float litres, ml;
    int flag = 0,pageWidth=1200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        input_quantity = findViewById(R.id.quantity_field);
        save_btn = findViewById(R.id.done_btn);
        current_price = findViewById(R.id.price_field);
        viewData = findViewById(R.id.view_btn);
        changePrice = findViewById(R.id.change_price_btn);
        logout_button = findViewById(R.id.logout_btn);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        cmilkPrice = sharedPreferences.getFloat("milk_price",45 );
        cPrice = Float.toString(cmilkPrice);
        current_price.setText(cPrice);

        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,id);
        autoCompleteTextView.setAdapter(adapterItems);

        // ID selector
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_id = adapterView.getItemAtPosition(i).toString();
                flag = 1;
                autoCompleteTextView.setError(null);
            }
        });

        selectdate = findViewById(R.id.editdate);
        selectdate.setText(todayDate());
        selectdate.setOnClickListener(this);

        logout_button.setOnClickListener(v->logout());
        save_btn.setOnClickListener(v-> {
            try {
                saveData();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        viewData.setOnClickListener(v->startActivity(new Intent(MainActivity.this,ViewActivity.class)));
        changePrice.setOnClickListener(v->milkPriceEdit());
    }

    protected void onPause(){ super.onPause(); }
    protected void onStart(){ super.onStart(); }
    protected void onRestart(){ super.onRestart(); }
    protected void onResume(){ super.onResume(); }
    protected void onStop(){ super.onStop(); }
    protected void onDestroy(){ super.onDestroy(); }


    void logout()
    {
        finish();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }
    void milkPriceEdit()
    {
        finish();
        startActivity(new Intent(MainActivity.this,MilkPriceEditActivity.class));
    }
    private String todayDate()
    {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }
    void saveData() throws Exception {
        // Quantity entered
        String quantity = input_quantity.getText().toString().trim();

        if(quantity==null || quantity.isEmpty())
        {
            input_quantity.setError("Quantity is required");
            return;
        }
        if(!quantity.matches("\\d+"))
        {
            input_quantity.setError("Quantity must be in numbers");
            return;
        }
        if(flag==0) {
            autoCompleteTextView.setError("Please Select the ID number");
            return;
        }
        else
        {
            ml=Integer.parseInt(quantity);
            if(ml<=0)
            {
                input_quantity.setError("Quantity can't be 0 or -ve");
                return;
            }
            litres=ml/1000;
            quantity_litres= Float.toString(litres);
            createPDF();
        }
    }

    @Override
    public void onClick(View view) {
        if(view==selectdate){
            final Calendar calendar = Calendar.getInstance();
            mYear=calendar.get(Calendar.YEAR);
            mMonth=calendar.get(Calendar.MONTH);
            mDay=calendar.get(Calendar.DAY_OF_MONTH);

            //show dialog
            DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    selectdate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            },mYear,mMonth,mDay);
            datePickerDialog.show();
        }
    }

    public void createPDF() throws Exception {
        chosen_date = selectdate.getText().toString();
        String current_milk_price = current_price.getText().toString().trim();
        float mprice = Float.parseFloat(current_milk_price);
        String formatted_price = String.format("%.2f",mprice);
        float milkPrice = Float.parseFloat(formatted_price);

        float amount = litres * milkPrice;
        String price_amount = String.format("%.2f",amount);
        float amount_required = Float.parseFloat(price_amount);
        String customer = "Customer ID: " + selected_id;

        File directory = new File(Environment.getExternalStorageDirectory(), "Milkman");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(directory, selected_id + ".txt");
            if (!file.exists()) {
                FileWriter fw1 = new FileWriter(file);
                try {
                    fw1.write("\n" + " ".repeat(30) + "***MILKMAN***\n\n" + customer + "\n\n" + "-".repeat(73) + "\n| S.No   | Date           | Price(/L)     | Quantity(L) | Amount(rps)   |" + "\n" + "-".repeat(73));
                    fw1.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

                int lineCount = 0;
                String sentence;
                String[] arr;
                String stotal = "";
                float total = 0.00f;
                try {
                    List<String> lines = new ArrayList<>();
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        while ((sentence = br.readLine()) != null) { // Reads a line, null indicates end of file

                            if (lineCount > 7) {
                                if (sentence.charAt(0) != '|')
                                    break;
                                arr = sentence.split("\\|");
                                stotal = arr[5].trim();
                                total += Float.parseFloat(stotal);
                            }
                            lineCount++;    // Increment the line counter
                            lines.add(sentence);
                        }
                    }
                    int sno_count = lineCount - 7;
                    String sno = Integer.toString(sno_count);

                    int sno_length = sno.length();
                    int date_length = chosen_date.length();
                    int price_length = formatted_price.length();
                    int quantity_length = quantity_litres.length();
                    int amount_length = price_amount.length();

                    total+=amount_required;
                    System.out.println("totall: "+total);
                    String total_amount = String.format("%.2f",total);
                    int total_amount_length = total_amount.length();

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        for (int i = 0; i < lines.size(); i++) {
                            writer.write(lines.get(i));
                            writer.newLine();
                        }
                        writer.write("| " + sno_count + " ".repeat(7 - sno_length) + "| " + chosen_date + " ".repeat(15 - date_length) + "| " + formatted_price + " ".repeat(14 - price_length) + "| " + quantity_litres + " ".repeat(12 - quantity_length) + "| " + " ".repeat(14 - amount_length) + price_amount + "|\n");
                        writer.write("-".repeat(73)+"\n");
                        writer.write(" ".repeat(42)+"| TOTAL :     |"+" ".repeat(15-total_amount_length)+total_amount+"|\n");
                        writer.write(" ".repeat(42)+"-".repeat(31)+"\n");
                    }
                }catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                }
        Toast.makeText(MainActivity.this, "Data Saved"+"\nQty(L): "+quantity_litres+" ID: "+selected_id, Toast.LENGTH_SHORT).show();
        input_quantity.setText("");
    }
}
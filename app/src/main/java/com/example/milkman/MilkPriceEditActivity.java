package com.example.milkman;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MilkPriceEditActivity extends AppCompatActivity {

    float milkPrice;
    EditText newPrice;
    Button updateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_price_edit);

        newPrice = findViewById(R.id.change_price_field);
        updateButton=findViewById(R.id.update_price_btn);

        updateButton.setOnClickListener(v->updatePrice());
    }

    protected void onPause(){ super.onPause(); }
    protected void onStart(){ super.onStart(); }
    protected void onRestart(){ super.onRestart(); }
    protected void onResume(){ super.onResume(); }
    protected void onStop(){ super.onStop(); }
    protected void onDestroy(){ super.onDestroy(); }

    void updatePrice()
    {
        String nprice=newPrice.getText().toString().trim();

        if(nprice==null || nprice.isEmpty())
        {
            newPrice.setError("Price is required");
            return;
        }
        if(!nprice.matches("\\d*(\\.\\d+)?")) // allow both int and float
        {
            newPrice.setError("Price must be in numerical");
            return;
        }
        else{
            float price = Float.parseFloat(nprice);
            if(price<=0)
            {
                newPrice.setError("Price can't be 0 or -ve");
                return;
            }
            else{
                String formatted = String.format("%.2f",price);
                milkPrice= Float.parseFloat(formatted);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("milk_price", milkPrice);                 // Save an integer with the key "age"
                editor.apply();                           // Commit changes
                Toast.makeText(MilkPriceEditActivity.this, "Price Updated: "+milkPrice, Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(MilkPriceEditActivity.this,MainActivity.class));
            }
        }

    }
}
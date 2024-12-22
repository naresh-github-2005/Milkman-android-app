package com.example.milkman;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LoginActivity extends AppCompatActivity {

    String user="milkman",pass="milkman";
    EditText usernameEditText,passwordEditText;
    Button loginBtn;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_field);
        passwordEditText = findViewById(R.id.password_field);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(v->loginUser());
    }

    protected void onPause(){ super.onPause(); }
    protected void onStart(){ super.onStart(); }
    protected void onRestart(){ super.onRestart(); }
    protected void onStop(){ super.onStop(); }
    protected void onDestroy(){ super.onDestroy(); }

    protected void onResume(){
        super.onResume();
        if(!Utils.isPermissionGranted(this))
        {
            new AlertDialog.Builder(this)
                    .setTitle("All files permission")
                    .setMessage("Due to Android 11 restrictions, this app requires all files permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePermission();
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
        {
            Toast.makeText(this,"Permission already granted",Toast.LENGTH_LONG).show();
        }
    }

    private void takePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,101);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0)
        {
            if(requestCode==101)
            {
                boolean readExt = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(!readExt)
                {
                    takePermission();
                }
            }
        }
    }

    void loginUser()
    {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValidated=validateData( username, password);
        if(!isValidated)
        {
            return;
        }
        usernameEditText.setError(null);
        passwordEditText.setError(null);
        loginHome();
    }

    void loginHome()
    {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

    boolean validateData(String username,String password)
    {
        if(username.length()<3){
            usernameEditText.setError("Username length is invalid [min-4 char]");
            return false;
        }
        if(password.length()<3){
            passwordEditText.setError("Password length is invalid [min-6 char]");
            return false;
        }
        if(!user.equals(username) || !pass.equals(password))
        {
            usernameEditText.setError("Incorrect Username");
            passwordEditText.setError("Incorrect Password");
            return false;
        }
        return true;
    }
}
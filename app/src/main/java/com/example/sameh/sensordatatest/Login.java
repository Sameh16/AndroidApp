package com.example.sameh.sensordatatest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText Email;
    EditText Password;
    Button loginButton;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email  = findViewById(R.id.input_email);
        Password = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        sharedPreferences =  getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        if (sharedPreferences.contains("driverId"))
        {
            String email = sharedPreferences.getString("driverId","");
            String passsword = sharedPreferences.getString("password","");
            Email.setText(email);
            Password.setText(passsword);
            login(email,passsword);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                login(email,password);
            }
        });


    }


    public void login(String email ,String password) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        // TODO: Implement your own authentication logic here.
        LoginService();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // call Log in Service
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("driverId",Email.getText().toString());
        editor.putString("password",Password.getText().toString());
        editor.commit();
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("enter a valid email address");
            valid = false;
        } else {
            Email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            Password.setError("minmum 4 alphanumeric characters");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }

    public void LoginService()
    {
        String app_server_url = "http://seels-application.herokuapp.com/login/"+Email.getText().toString()+"/"+Password.getText().toString()+"";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("true"))
                        {
                            onLoginSuccess();
                        }
                        else
                        {
                            onLoginFailed();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error",error.getMessage());
            }
        }

        );
        SingleTon.getInstance(Login.this).addToRequestQueue(stringRequest);

    }
}

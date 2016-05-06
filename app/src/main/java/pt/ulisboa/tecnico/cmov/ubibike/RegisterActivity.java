package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * Created by Loureiro on 13-04-2016.
 */
public class RegisterActivity  extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText pass;
    private EditText confirm_pass;
    private Button btn;
    public static String P_NAME;
    public static String P_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        confirm_pass = (EditText) findViewById(R.id.confirm_password);
        btn = (Button) findViewById(R.id.create_account_button);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createAccount()) {
                    Toast.makeText(getApplicationContext(), "Account created!",
                            Toast.LENGTH_SHORT).show();
                    callProfileWindow();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void callProfileWindow() {
        Intent intent = new Intent(this,ProfileFragment.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("score", "0");
        startActivity(intent);
    }

    public boolean createAccount() {
        name.setError(null);
        email.setError(null);
        pass.setError(null);
        confirm_pass.setError(null);

        if(name.getText().toString().isEmpty()) {
            name.setError("This field is required");
            name.requestFocus();
            return false;
        }

        if(email.getText().toString().isEmpty()) {
            email.setError("This field is required");
            email.requestFocus();
            return false;
        }

        if(pass.getText().toString().isEmpty()) {
            pass.setError("This field is required");
            pass.requestFocus();
            return false;
        }

        if(confirm_pass.getText().toString().isEmpty()) {
            confirm_pass.setError("This field is required");
            confirm_pass.requestFocus();
            return false;
        }
        else if(!( pass.getText().toString()).equals( confirm_pass.getText().toString())) {
            confirm_pass.setError("Password doesn't match");
            confirm_pass.requestFocus();
            return false;
        }

        return true;
    }
}

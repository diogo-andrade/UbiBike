package pt.ulisboa.tecnico.cmov.ubibike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import pt.ulisboa.tecnico.cmov.ubibike.services.UBIClient;

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
    public static String P_SCORE;
    private UserRegisterTask mRegTask = null;
    private View mProgressView;
    private View mRegFormView;

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
                    showProgress(true);
                    mRegTask = new UserRegisterTask(name.getText().toString(), email.getText().toString(), pass.getText().toString());
                    mRegTask.execute((Void) null);
                }
            }
        });

        mRegFormView=findViewById(R.id.registration_form);
        mProgressView=findViewById(R.id.register_progress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
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

        if( pass.getText().toString().length() <= 4) {
            confirm_pass.setError("Password too short");
            confirm_pass.requestFocus();
            return false;
        }

        P_NAME=name.getText().toString();
        P_EMAIL=email.getText().toString();
        P_SCORE="0";

        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String name, String email, String password) {
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

          /*  try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }*/
        //TODO: uncomment this
          /*  try {
                UBIClient client = new UBIClient("104.196.28.216",8081);
                //String result = client.requestLogin(mEmail, mPassword);

                client.close();

                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }*/

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;

            if (success) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("name", P_NAME);
                intent.putExtra("email", P_EMAIL);
                intent.putExtra("score", P_SCORE);
                startActivity(intent);
                finish();

            } else {
                showProgress(false);
                email.setError("This email is already in use");
                email.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegTask = null;
        }
    }
}

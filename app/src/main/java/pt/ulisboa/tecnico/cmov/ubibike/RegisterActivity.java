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

import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.ubibike.exceptions.ErrorCodeException;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;
import pt.ulisboa.tecnico.cmov.ubibike.services.UBIClient;

/**
 * Created by Loureiro on 13-04-2016.
 */
public class RegisterActivity  extends AppCompatActivity {

    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";
    private static final String EXTRA_EMAIL = "pt.ulisboa.tecnico.cmov.ubibike.EMAIL";
    private static final String EXTRA_SCORE = "pt.ulisboa.tecnico.cmov.ubibike.SCORE";
    private static final String EXTRA_PASSWORD = "pt.ulisboa.tecnico.cmov.ubibike.PASSWORD";

    private EditText nameView;
    private EditText emailView;
    private EditText passView;
    private EditText confirm_passView;
    private Button btn;

    private UserRegisterTask mRegTask = null;
    private View mProgressView;
    private View mRegFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameView = (EditText) findViewById(R.id.name);
        emailView = (EditText) findViewById(R.id.email);
        passView = (EditText) findViewById(R.id.password);
        confirm_passView = (EditText) findViewById(R.id.confirm_password);
        btn = (Button) findViewById(R.id.create_account_button);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createAccount()) {
                    showProgress(true);
                    mRegTask = new UserRegisterTask(nameView.getText().toString(), emailView.getText().toString(), passView.getText().toString());
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
        nameView.setError(null);
        emailView.setError(null);
        passView.setError(null);
        confirm_passView.setError(null);

        if(nameView.getText().toString().isEmpty()) {
            nameView.setError(getString(R.string.error_field_required));
            nameView.requestFocus();
            return false;
        }

        if(emailView.getText().toString().isEmpty()) {
            emailView.setError(getString(R.string.error_field_required));
            emailView.requestFocus();
            return false;
        }

        if(passView.getText().toString().isEmpty()) {
            passView.setError(getString(R.string.error_field_required));
            passView.requestFocus();
            return false;
        }

        if(confirm_passView.getText().toString().isEmpty()) {
            confirm_passView.setError(getString(R.string.error_field_required));
            confirm_passView.requestFocus();
            return false;
        }
        else if(!( passView.getText().toString()).equals( confirm_passView.getText().toString())) {
            confirm_passView.setError("Password doesn't match");
            confirm_passView.requestFocus();
            return false;
        }

        if( passView.getText().toString().length() <= 4) {
            confirm_passView.setError("Password too short");
            confirm_passView.requestFocus();
            return false;
        }

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
        private int mErrorCode;
        private Ubibiker user;

        UserRegisterTask(String name, String email, String password) {
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = new UBIClient().GET("http://10.0.2.2:5000/register?name=" + mName.replaceAll(" ", "%20") + "&email=" + mEmail + "&password=" + mPassword);

                JSONObject mObject = new JSONObject(response.toString());

                String name = mObject.getString("name");
                String email = mObject.getString("email");
                int score = mObject.getInt("points");
                user = new Ubibiker(name, email);
                user.setPoints(score);



            } catch (ErrorCodeException e){
                mErrorCode = e.getCode();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;

            if (success) {
                Toast.makeText(getApplication(), "Your account has been sucessfully created",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra(EXTRA_NAME, user.getName());
                intent.putExtra(EXTRA_EMAIL, user.getEmail());
                intent.putExtra(EXTRA_SCORE, user.getPoints()+ "");
                intent.putExtra(EXTRA_PASSWORD, passView.getText().toString());
                startActivity(intent);
                finish();

            } else {
                showProgress(false);
                if (mErrorCode == 409) {
                    emailView.setError(getString(R.string.error_email_conflict));
                    emailView.requestFocus();
                } else if (mErrorCode == 400) {
                    //Bad Request
                }
            }
        }

        @Override
        protected void onCancelled() {
            mRegTask = null;
        }
    }
}

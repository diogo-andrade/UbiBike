package pt.ulisboa.tecnico.cmov.ubibike;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


public class UbibikerProfileActivity extends AppCompatActivity {

    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";
    private static final String EXTRA_EMAIL = "pt.ulisboa.tecnico.cmov.ubibike.EMAIL";
    Fragment fragment = null;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private String name_profile = "DEFAULT";
    private String email_profile = "DEFAULT";
    private String score_profile = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubibiker_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name_profile=getIntent().getStringExtra(EXTRA_NAME);
        email_profile=getIntent().getStringExtra(EXTRA_EMAIL);

        fragment = new ProfileFragment().newInstance(name_profile, email_profile, score_profile);
        fragmentManager.beginTransaction().add(R.id.ubibiker_layout, fragment).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

package ua.elitasoftware.UzhNU;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import ua.elitasoftware.UzhNU.FacultiesFragment.OnSelectedFaculty;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivity implements OnSelectedFaculty {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSettings();
    }

    private void checkSettings() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultPref = "-|" +getResources().getString(R.string.noFaculty);
        String listPreference = preference.getString(SettingsFragment.LIST_KEY, defaultPref);
        String id = listPreference.substring(0, listPreference.indexOf("|"));
        String caption = listPreference.substring(listPreference.lastIndexOf("|")+1);
        switch (id){
            case "-":
                setContentView(R.layout.main);
                break;
            case "+":
                startActivity(new Intent(this, DownloadActivity.class));
                finish();
                break;
            default:
                openFaculty(Integer.parseInt(id), caption);
                finish();
                break;
        }
    }

    @Override
    public void onSelectFaculty(AdapterView<?> parent, View view, int position, Faculty faculty) {
        FragmentManager fragmentManager = getFragmentManager();
        TimetablesFragment timetablesFragment = (TimetablesFragment) fragmentManager.findFragmentById(R.id.frTimetable);
        if (timetablesFragment == null){
            openFaculty(faculty.getId(), faculty.getName());
        }
    }

    private void openFaculty(Integer id, String title){
        Intent intent =  new Intent(this, TimetableActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}

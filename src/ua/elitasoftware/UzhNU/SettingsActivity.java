package ua.elitasoftware.UzhNU;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.view.Menu;
import android.widget.Toast;
import ua.elitasoftware.UzhNU.SettingsFragment.OnPreferenceClick;

import java.io.File;

public class SettingsActivity extends BaseActivity implements OnPreferenceClick {

    private boolean clearFolder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override
    public void onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case SettingsFragment.CLEAR_ALL_KEY:
                clearDownloadFolder();
                clearFolder = true;
                break;
            case SettingsFragment.FEEDBACK_KEY:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + getString(R.string.devEmail)));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.devSubject));
                startActivity(emailIntent);
                break;
            case SettingsFragment.DATE_KEY:
                DatePick datePick = new DatePick(getFragmentManager());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (clearFolder && DownloadActivity.isActive()) {
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    private void clearDownloadFolder() {
        String folderName = getResources().getString(R.string.folderName);
        File downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + folderName);
        if (removeDirectory(downloadFolder)) {
            Toast.makeText(this, getString(R.string.cleared), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean removeDirectory(File file) {
        if (file == null)
            return true;
        if (!file.exists())
            return true;
        if (file.isFile())
            return false;

        String[] list = file.list();

        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File item = new File(file, list[i]);

                if (item.isDirectory()) {
                    if (!removeDirectory(item))
                        return false;
                } else {
                    if (!item.delete()) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}

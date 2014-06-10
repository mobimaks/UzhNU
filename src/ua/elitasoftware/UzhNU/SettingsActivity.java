package ua.elitasoftware.UzhNU;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.view.Menu;
import android.widget.Toast;
import ua.elitasoftware.UzhNU.SettingsFragment.OnPreferenceClick;

import java.io.File;

public class SettingsActivity extends BaseActivity implements OnPreferenceClick{

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
        switch (preference.getKey()){
            case SettingsFragment.CLEAR_ALL_KEY:
                clearDownloadFolder();
                break;
        }
    }

    private void clearDownloadFolder() {
        String folderName = getResources().getString(R.string.folderName);
        File downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + folderName);
        if (removeDirectory(downloadFolder)){
            Toast.makeText(this, getResources().getString(R.string.cleared), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean removeDirectory(File file){
        if (file == null)
            return true;
        if (!file.exists())
            return true;
        if (file.isFile())
            return false;

        String[] list = file.list();

        if (list != null){
            for (int i = 0; i<list.length; i++){
                File item = new File(file, list[i]);

                if (item.isDirectory()){
                    if (!removeDirectory(item))
                        return  false;
                } else {
                    if (!item.delete()){
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}

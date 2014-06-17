package ua.elitasoftware.UzhNU;

import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import ua.elitasoftware.UzhNU.DownloadsFragment.OnItemClick;

import java.io.File;

public class DownloadActivity extends BaseActivity implements OnItemClick {

    private static boolean active = false;
    private SharedPreferences preferences;

    public static boolean isActive() {
        return active;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloads);
        DownloadsFragment downloadsFragment = (DownloadsFragment) getFragmentManager().findFragmentById(R.id.frDownloads);
        downloadsFragment.openDefaultFolder();
        downloadsFragment.getMainFolder();
        active = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.abDownloads).setVisible(false);
        menu.findItem(R.id.abSortBy).setVisible(true);
        preferences = getPreferences(MODE_PRIVATE);
        String sortBy = preferences.getString(DownloadsFragment.ORDER_BY, DownloadsFragment.SORT_BY_NAME);
        switch (sortBy) {
            case DownloadsFragment.SORT_BY_NAME:
                menu.getItem(0).getSubMenu().getItem(0).setChecked(true);
                break;
            case DownloadsFragment.SORT_BY_DATE:
                menu.getItem(0).getSubMenu().getItem(1).setChecked(true);
                break;
            case DownloadsFragment.SORT_BY_EXTENSION:
                menu.getItem(0).getSubMenu().getItem(2).setChecked(true);
                break;
        }
        ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() != R.id.abGroupSortBy) {
            super.onOptionsItemSelected(item);
            return true;
        }
        SharedPreferences.Editor editor = preferences.edit();
        switch (item.getItemId()) {
            case R.id.abSortByName:
                editor.putString(DownloadsFragment.ORDER_BY, DownloadsFragment.SORT_BY_NAME);
                break;
            case R.id.abSortByDate:
                editor.putString(DownloadsFragment.ORDER_BY, DownloadsFragment.SORT_BY_DATE);
                break;
            case R.id.abSortByExt:
                editor.putString(DownloadsFragment.ORDER_BY, DownloadsFragment.SORT_BY_EXTENSION);
                break;
        }
        item.setChecked(true);
        editor.commit();
        DownloadsFragment downloadsFragment = (DownloadsFragment) getFragmentManager().findFragmentById(R.id.frDownloads);
        downloadsFragment.refresh();
        return true;
    }

    @Override
    public void onItemClick(File item) {
        if (item.isDirectory()) {
            if (item == null || item.list().length == 0) {
                Toast.makeText(this, getString(R.string.emptyFolder), Toast.LENGTH_SHORT).show();
            } else {
                DownloadsFragment downloadsFragment = (DownloadsFragment) getFragmentManager().findFragmentById(R.id.frDownloads);
                downloadsFragment.openFolder(item);
            }
        } else {
            Intent openFile = new Intent(Intent.ACTION_VIEW);
            String extension = item.getName().substring(item.getName().lastIndexOf(".") + 1).toLowerCase();
            openFile.setDataAndType(Uri.fromFile(item), MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
            try {
                startActivity(openFile);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, getResources().getString(R.string.cantOpenFile), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DownloadsFragment downloadsFragment = (DownloadsFragment) getFragmentManager().findFragmentById(R.id.frDownloads);
        File currentFolder = downloadsFragment.getCurrentFolder();
        if (currentFolder.getName().equals(getResources().getString(R.string.folderName).replaceAll("/", ""))) {
            super.onBackPressed();
        } else {
            downloadsFragment.openFolder(currentFolder.getParentFile());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    public File getMainFolder() {
        FragmentManager fm = getFragmentManager();
        DownloadsFragment fragment = (DownloadsFragment) fm.findFragmentById(R.id.frDownloads);
        return fragment.getMainFolder();
    }
}

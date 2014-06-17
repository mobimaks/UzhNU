package ua.elitasoftware.UzhNU;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class BaseActivity extends Activity {

    public static final int TYPE_APP_UPD = 4;
    public static final String LAST_TIME_CHECK = "last_time_checked";
    public static String newVer = "";
    protected BroadcastReceiver receiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //якщо версія KitKat і більше то робим статус бар жовтим
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.yellowUZHNU));
        }

        //прикріпляєм ActionBar
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                //check if downloaded file is 'apk'
                if (MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk").equals(dm.getMimeTypeForDownloadedFile(downloadId))) {
                    finish();
                    Uri myUri = dm.getUriForDownloadedFile(downloadId);
                    Intent openFile = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(myUri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
                    startActivity(openFile);
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        checkNewVersion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.abSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.abDownloads:
                String folderName = getResources().getString(R.string.folderName);
                File downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + folderName);
                if (downloadFolder.list() == null || downloadFolder.list().length == 0) {
                    Toast.makeText(this, getString(R.string.emptyFolder), Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(this, DownloadActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.abAbout:
                new AboutDialog(getFragmentManager());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void checkNewVersion() {
        try {
            if (needCheckForUpd() && (Boolean) new CheckNewVersion().execute().get()) {
                openNewVerDialog();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void openNewVerDialog() {
        String caption = getString(R.string.availableNewVer) + "\n" + getString(R.string.appUpdAsk);
        DownloadDialog dialog = new DownloadDialog(getFragmentManager(), caption, TYPE_APP_UPD);
    }

    protected boolean needCheckForUpd() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastTimeChecked = preferences.getLong(LAST_TIME_CHECK, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -12);
        long time = calendar.getTimeInMillis();
        if (lastTimeChecked < time) {
            preferences.edit().putLong(LAST_TIME_CHECK, System.currentTimeMillis()).commit();
            return true;
        } else return false;

    }

    class CheckNewVersion extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            HandleHTTP handleHTTP = new HandleHTTP();
            String url = "http://mobimaks.ucoz.ru/version.txt";
            String version = handleHTTP.makeRequest(url);
            if (version == null) {
                return false;
            }
            Double versionSite, versionPhone;
            versionSite = Double.parseDouble(version);
            String packageVer;
            try {
                packageVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                versionPhone = Double.parseDouble(packageVer);
                if (versionSite > versionPhone) {
                    newVer = String.valueOf(versionSite);
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
            return false;
        }
    }
}

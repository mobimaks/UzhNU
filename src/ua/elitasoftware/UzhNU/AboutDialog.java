package ua.elitasoftware.UzhNU;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

/**
 * Created by mobimaks on 16.06.2014.
 */
public class AboutDialog extends DialogFragment implements OnLongClickListener {

    private TextView tvAppDev;

    public AboutDialog(FragmentManager fragmentManager) {
        this.show(fragmentManager, "about");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.about, null);

        TextView tvAppName = (TextView) dialogView.findViewById(R.id.tvAboutAppName);
        TextView tvAppVer = (TextView) dialogView.findViewById(R.id.tvAboutAppVer);
        tvAppDev = (TextView) dialogView.findViewById(R.id.tvAboutAppDev);
        tvAppDev.setOnLongClickListener(this);

        tvAppName.setText("© " + getString(R.string.app_name));
        String appVer = "1.00";
        try {
            appVer = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvAppVer.setText(String.format(getString(R.string.aboutVer), appVer));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.about))
                .setNeutralButton(getString(R.string.dialogBtnOk), null)
                .setView(dialogView)
        ;
        setRetainInstance(true);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tvAboutAppDev:
                tvAppDev.setText("Насправді програму розробив mobimaks! ;)");
                break;
        }
        return true;
    }
}

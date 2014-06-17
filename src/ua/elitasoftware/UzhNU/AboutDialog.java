package ua.elitasoftware.UzhNU;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutDialog extends DialogFragment implements OnLongClickListener, OnClickListener {

    private TextView tvAppName;
    private TextView tvAppDev;

    public AboutDialog(FragmentManager fragmentManager) {
        this.show(fragmentManager, "about");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.about, null);

        ((ImageView) dialogView.findViewById(R.id.ivAbout)).setOnClickListener(this);

        tvAppName = (TextView) dialogView.findViewById(R.id.tvAboutAppName);
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

        //
//        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int length = Integer.valueOf(preference.getString("length", "0"));
//        LinearLayout llAboutText = (LinearLayout)dialogView.findViewById(R.id.llAboutText);
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)llAboutText.getLayoutParams();
//        layoutParams.setMargins(dpToPx(length), 0, 0, 0);
//        llAboutText.setLayoutParams(layoutParams);
//
//
//        TextView tvAboutDev = (TextView) dialogView.findViewById(R.id.tvAboutAppDev);
//        int up = Integer.valueOf(preference.getString("up", "0"));
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvAboutDev.getLayoutParams();
//        params.setMargins(0, dpToPx(up), 0, 0);
//        tvAboutDev.setLayoutParams(params);
        //

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.about))
                .setNeutralButton(getString(R.string.dialogBtnOk), null)
                .setView(dialogView)
        ;
        setRetainInstance(true);
        return builder.create();
    }

//    public int dpToPx(int dp) {
//        DisplayMetrics displayMetrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
//        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//        return px;
//    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAbout:
                Intent openSite = new Intent(Intent.ACTION_VIEW);
                openSite.setData(Uri.parse(getString(R.string.uzhnuSite)));
                startActivity(openSite);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tvAboutAppDev:
                int width = tvAppDev.getWidth();
//                int height = tvAppDev.getHeight();
                tvAppDev.setText("Насправді, програму розробив mobimaks! :)\n©ElitaSoftware Corporation");
                tvAppDev.setWidth(width);
//                tvAppDev.setHeight(height);
                break;
        }
        return true;
    }
}

package ua.elitasoftware.UzhNU;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePick extends DialogFragment implements OnDateSetListener {

    final Calendar today = Calendar.getInstance();

    public DatePick(FragmentManager fragmentManager) {
        this.show(fragmentManager, "datePick");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int day = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);

        setRetainInstance(true);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar selectedDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        String textOut;
        if (today.after(selectedDate)) {
            textOut = "Good choice";
        } else {
            textOut = "Bad choice";
        }
        Toast.makeText(getActivity(), textOut, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }
}

package ua.elitasoftware.UzhNU;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class FacultiesFragment extends Fragment implements OnItemClickListener {

    public interface OnSelectedFaculty{
        void onSelectFaculty(AdapterView<?> parent, View view, int position, Faculty faculty);
    }

    private final String URL_FACULTIES_REQUEST = "http://mobimaks.ucoz.ru/faculty.txt";

    //JSON codes
    private final String TAG_ID = "id";
    private final String TAG_CAPTION = "caption";

    private ListView lvFacultiesList;
    private ProgressBar progressBar;
    private FacultiesAdapter adapter;
    private ArrayList<Faculty> faculties = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faculties, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = (ProgressBar)getActivity().findViewById(R.id.pbDownloadFaculties);
        try {
            faculties = (ArrayList<Faculty>)new FacultyRequest().execute(URL_FACULTIES_REQUEST).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        lvFacultiesList = (ListView)getActivity().findViewById(R.id.lvFacultiesList);

        if (faculties != null){
            adapter = new FacultiesAdapter(getActivity(), faculties);
            lvFacultiesList.setAdapter(adapter);
        } else {
            ImageView ivNoInternet = (ImageView)getActivity().findViewById(R.id.ivNoInternet);
            TextView tvNoInternet = (TextView)getActivity().findViewById(R.id.tvNoInternet);
            ivNoInternet.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.VISIBLE);
        }
        lvFacultiesList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OnSelectedFaculty listener = (OnSelectedFaculty)getActivity();
        listener.onSelectFaculty(parent, view, position, faculties.get(position));
    }

    /**
     * AsyncTask
     */
    class FacultyRequest extends AsyncTask<String, Void, Object>{

        private HandleHTTP handleHTTP;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(String... params) {
            handleHTTP = new HandleHTTP();
            String jSONstr;
            if (hasInternet()){
                jSONstr = handleHTTP.makeRequest(params[0]);
                if (jSONstr != null){
                    return fillData(jSONstr);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object faculties) {
            super.onPostExecute(faculties);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private ArrayList<Faculty> fillData(String jSONstr){
            ArrayList<Faculty> faculties = new ArrayList<>();
            Faculty faculty;
            try {
                JSONArray dekanatsArray = new JSONArray(jSONstr);
                for (int i = 0; i < dekanatsArray.length(); i++){
                    JSONObject oneDekanat = dekanatsArray.getJSONObject(i);
                    int id = oneDekanat.getInt(TAG_ID);
                    String name = oneDekanat.getString(TAG_CAPTION);

                    faculty = new Faculty(id, name);

                    faculties.add(faculty);
                }

                //adding faculties to DB
                if (faculties != null)
                    addToDB(faculties);
                return faculties;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void addToDB(ArrayList<Faculty> faculties){
            DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(dbHelper.FACULTIES, null, null);
            String sqlInsert = "insert into " + dbHelper.FACULTIES +" ("+ dbHelper.ID+", " + dbHelper.CAPTION + ") values" ;
            int i;
            for (i = 0; i<faculties.size(); i++) {
                Integer id = faculties.get(i).getId();
                String caption = faculties.get(i).getName().replaceAll("'", "''");
                sqlInsert += " ('"+ id + "', '" + caption + "'),";
            }
            sqlInsert = sqlInsert.substring(0, sqlInsert.length()-1)+";";
            db.execSQL(sqlInsert);
        }


        private boolean hasInternet(){
            ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            if
//            {
//                return InetAddress.getByName("www.google.com").isReachable(1500);
//            }
            return (activeNetwork != null && activeNetwork.isConnected());
//        return (activeNetwork != null && activeNetwork.isConnected()) ? true : false;
        }
    }
}

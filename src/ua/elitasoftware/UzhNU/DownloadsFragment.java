package ua.elitasoftware.UzhNU;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DownloadsFragment extends Fragment implements OnItemClickListener {

    private File[] files;
    public static final String ORDER_BY = "sort";

    //sorting const
    public static final String SORT_BY_DATE = "date";
    public static final String SORT_BY_NAME = "name";
    public static final String SORT_BY_EXTENSION = "extension";

    private File currentFolder;
    private DownloadsAdapter adapter;


    public interface OnItemClick{
        void onItemClick(File item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_downloads, container, false);
    }

    public void refresh(){
        openFolder(getCurrentFolder());
    }

    public void openDefaultFolder(){
        openFolder(getMainFolder());
    }

    public void openFolder(File folder){
        setCurrentFolder(folder);
        files = folder.listFiles();
        if (files == null){
            Toast.makeText(getActivity(), getString(R.string.emptyFolder), Toast.LENGTH_SHORT).show();
        } else {
            //check Shared Preference for sortBy parameter
            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            String sortBy = preferences.getString(ORDER_BY, SORT_BY_NAME);
            //sort files by sortBy parameter

//            long startSort = System.currentTimeMillis();
            sortFiles(files, sortBy);
//            long finishSort = System.currentTimeMillis();
//            Toast.makeText(getActivity(), "Sort time: "+((finishSort-startSort))+" ms", Toast.LENGTH_SHORT).show();

            adapter = new DownloadsAdapter(getActivity().getApplicationContext(), files);
            ListView listView = (ListView)getActivity().findViewById(R.id.lvDownloadList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    //TODO: Try sort in AsyncTask
    private void sortFiles(File[] files, final String sortBy){
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.isDirectory() && rhs.isFile()) {
                //return folder first
                    return -1;
                } else if (lhs.isFile() && rhs.isDirectory()){
                    //return file first
                    return 1;
                } else if ((lhs.isDirectory() && rhs.isDirectory()) || (lhs.isFile() && rhs.isFile())){
                    //if Files have same type, then sort
                    switch (sortBy){
                        case SORT_BY_NAME:
                            return lhs.getName().compareTo(rhs.getName());
                        case SORT_BY_DATE:
                            //DESC sorting by date
                            return String.valueOf(rhs.lastModified()).compareTo(String.valueOf(lhs.lastModified()));
                        case SORT_BY_EXTENSION:
                            return extensionSort(lhs, rhs);
                    }
                }
                return 0;
            }
        });
    }

    private int extensionSort(File lhs, File rhs){
        String ext1 = lhs.getName();
        String ext2 = rhs.getName();
        if (lhs.isDirectory()){
            ext1 += ".";
        }
        if (rhs.isDirectory()){
            ext2 += ".";
        }
        ext1 = ext1.substring(ext1.lastIndexOf("."));
        ext2 = ext2.substring(ext2.lastIndexOf("."));

        int extComparison = ext1.compareTo(ext2);
        if (extComparison != 0)
            return extComparison;
        else
            return lhs.getName().compareTo(rhs.getName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OnItemClick listener = (OnItemClick)getActivity();
        listener.onItemClick((File)adapter.getItem(position));
    }

    public File getCurrentFolder() {
        return currentFolder;
    }

    public File getMainFolder() {
        String folderName = getResources().getString(R.string.folderName);
        File downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + folderName);
        return downloadFolder;
    }

    public void setCurrentFolder(File currentFolder) {
        this.currentFolder = currentFolder;
    }
}
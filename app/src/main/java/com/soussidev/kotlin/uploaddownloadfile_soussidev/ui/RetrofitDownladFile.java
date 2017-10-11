package com.soussidev.kotlin.uploaddownloadfile_soussidev.ui;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.soussidev.kotlin.uploaddownloadfile_soussidev.R;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.model.Download;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.util.DownloadService;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetrofitDownladFile extends Fragment {

    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View currentView;
    private AppCompatTextView text_contient;
    private AppCompatButton btn_download;
    private ProgressBar mProgressBar;

    public RetrofitDownladFile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downlad_file, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitView(view);
        registerReceiver();

    }

    /**
     * @author Soussi
     *
     * @Fun InitView()
     *
     *@Info Initial View
     * @param view
     *
     */

    public void InitView(View view)
    {
        currentView=view;
        btn_download=(AppCompatButton)view.findViewById(R.id.btn_download);
        text_contient=(AppCompatTextView)view.findViewById(R.id.progress_text);
        mProgressBar=(ProgressBar)view.findViewById(R.id.progress);

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    startDownload("18. Louane - Avenir (Radio Edit).mp3");
                } else {
                    requestPermission();
                }
            }
        });
    }

    /**
     * @author Soussi
     *
     * @Fun startDownload()
     *
     *@Info Start download File
     * @param name
     *
     */

    private void startDownload(String name){

        Intent intent = new Intent(getActivity(),DownloadService.class);
        intent.putExtra("Nfile",name);
        getActivity().startService(intent);

    }

    /**
     * @author Soussi
     *
     * @Fun registerReceiver()
     *
     *
     */

    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    /**
     * @author Soussi
     *
     * @Fun BroadcastReceiver()
     *
     *
     *
     */

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(MESSAGE_PROGRESS)){

                Download download = intent.getParcelableExtra("download");
                mProgressBar.setProgress(download.getProgress());
                if(download.getProgress() == 100){

                    text_contient.setText("File Download Complete");

                } else {

                    text_contient.setText(String.format("Downloaded (%d/%d) MB",download.getCurrentFileSize(),download.getTotalFileSize()));

                }
            }
        }
    };

    /**
     * @author Soussi
     *
     * @Fun checkPermission()
     *
     *
     *
     */

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    /**
     * @author Soussi
     *
     * @Fun requestPermission()
     *
     */

    private void requestPermission(){

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);

    }

    /**
     * @author Soussi
     *
     * @Fun onRequestPermissionsResult()
     *
     *@Info Check Permission for Storage is enable
     * @param grantResults
     * @param requestCode
     * @param permissions
     *
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownload("");
                } else {

                    Snackbar.make(currentView,"Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

}

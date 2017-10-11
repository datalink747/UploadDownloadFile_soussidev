package com.soussidev.kotlin.uploaddownloadfile_soussidev.ui;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soussidev.kotlin.uploaddownloadfile_soussidev.R;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.api.ApiService;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.api.RetroClient;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.api.response.Result;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.permission.PermissionsActivity;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.permission.PermissionsChecker;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.util.InternetConnection;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetrofitUploadFragment extends Fragment {
    private static final String[] PERMISSIONS_READ_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private Context mContext;
    private View parentView;
    private ImageView imageView;
    private TextView textView;
    private String imagePath;
    private PermissionsChecker checker;

    public RetrofitUploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrofit_upload, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitView(view);
    }

    /**
     * @author Soussi
     *
     * @Fun InitView()
     *
     *@param view
     *
     */

    public void InitView(View view)
    {
        mContext = getActivity().getApplicationContext();
        parentView = view;
        checker = new PermissionsChecker(getActivity());

        textView = (TextView) view.findViewById(R.id.textView);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePopup();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePopup();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(imagePath)) {

                    /**
                     * Uploading AsyncTask
                     */
                    if (InternetConnection.checkConnection(mContext)) {
                        /******************Retrofit***************/
                        uploadImage();
                    } else {
                        Snackbar.make(parentView, R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();
                    }
                } else {
                    Snackbar.make(parentView, R.string.string_message_to_attach_file, Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        });



    }

/**
 * @author Soussi
 *
 * @Fun uploadImage()
 *
 *@Info Upload Image Client Code
 *
 */


    private void uploadImage() {

        /**
         * Progressbar to Display if you need
         */
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.string_title_upload_progressbar_));
        progressDialog.show();

        //Create Upload Server Client
        ApiService service = RetroClient.getApiService();

        //File creating from selected URL
        File file = new File(imagePath);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        Call<Result> resultCall = service.uploadImage(body);

        // finally, execute the request
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                progressDialog.dismiss();

                // Response Success or Fail
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success"))
                        Snackbar.make(parentView, R.string.string_upload_success, Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(parentView, R.string.string_upload_fail, Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(parentView, R.string.string_upload_fail, Snackbar.LENGTH_LONG).show();
                }

                /**
                 * Update Views
                 */
                imagePath = "";
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * @author Soussi
     *
     * @Fun showImagePopup()
     *
     *@Info Showing Image Picker
     *
     */


    public void showImagePopup() {
        if (checker.lacksPermissions(PERMISSIONS_READ_STORAGE)) {
            startPermissionsActivity(PERMISSIONS_READ_STORAGE);
        } else {
            // File System.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_PICK);

            // Chooser of file system options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.string_choose_image));
            startActivityForResult(chooserIntent, 1010);
        }
    }

    /**
     * @author Soussi
     *
     * @Fun startPermissionsActivity()
     *
     *@Info Verify Permission
     *@param permission
     */

    private void startPermissionsActivity(String[] permission) {
        PermissionsActivity.startActivityForResult(getActivity(), 0, permission);
    }

    /**
     * @author Soussi
     *
     * @Fun onActivityResult()
     *
     *@Info OnResult of Image Picked
     * @param data
     * @param requestCode
     * @param resultCode
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1010) {
            if (data == null) {
                Snackbar.make(parentView, R.string.string_unable_to_pick_image, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
            Uri selectedImageUri = data.getData();
            //--------------------------------------
             //imagePath= FilePath.getPath(getActivity(), selectedImageUri);
            //---------------------------------------


            String[] filePathColumn = {MediaStore.Images.Media.DATA};//MediaStore.Images.Media.DATA

            Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);

                Picasso.with(mContext).load(new File(imagePath))
                        .into(imageView);

                Snackbar.make(parentView, R.string.string_reselect, Snackbar.LENGTH_LONG).show();
                cursor.close();

                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                Snackbar.make(parentView, R.string.string_unable_to_load_image, Snackbar.LENGTH_LONG).show();
            }
        }
    }


}

package com.soussidev.kotlin.uploaddownloadfile_soussidev.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Soussi on 10/10/2017.
 */

public class Result {

    @SerializedName("result")
    @Expose
    private String result;

    /**
     * @return The result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result The result
     */
    public void setResult(String result) {
        this.result = result;
    }
}

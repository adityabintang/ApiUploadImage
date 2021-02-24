package com.bintang.apiuploadimage.upload

import android.os.Message
import com.bintang.apiuploadimage.upload.model.ResponseUpload

interface UploadView {
    fun isEmpty(msg: String)
    fun onSuccessupload(response: ResponseUpload)
    fun onErrorServer(message: String)
}
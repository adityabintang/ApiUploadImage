package com.bintang.apiuploadimage.upload


import android.media.Image
import android.text.TextUtils
import com.bintang.apiuploadimage.upload.model.NetworkClient
import com.bintang.apiuploadimage.upload.model.ResponseUpload

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


class UploadPresenter(val view: UploadView) {
    fun upload(kode_barang: String, nama_barang: String, stock: String, deskripsi: String, image: String  ) {

        if (TextUtils.isEmpty(kode_barang)) {
            view.isEmpty("Tidak boleh Kosong")
        }
        else if (TextUtils.isEmpty(nama_barang)) {
            view.isEmpty("Tidak Boleh Kosong")
        }
        else if (TextUtils.isEmpty(stock)) {
            view.isEmpty("Tidak Boleh Kosong")
        }
        else if (TextUtils.isEmpty(deskripsi)) {
            view.isEmpty("Tidak Boleh Kosong")
        }
        else if (TextUtils.isEmpty(image)) {
            view.isEmpty("Tidak Boleh Kosong")
        }
        else {
            val kode_barang: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), kode_barang)
            val nama_barang: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nama_barang)
            val stock: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), stock)
            val deskripsi: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), deskripsi)
            val file = File(image)
            val image: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val requestFile = MultipartBody.Part.createFormData("image", file.name, image)
            NetworkClient.initService().upload(kode_barang, nama_barang, stock, deskripsi, requestFile).enqueue(object : retrofit2.Callback<ResponseUpload> {
                override fun onResponse(call: Call<ResponseUpload>, response: Response<ResponseUpload>) {
                    if (response.isSuccessful) {
                        response.body()?.let { view.onSuccessupload(it) }
                    }
                }

                override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                    view.onErrorServer(t.localizedMessage)
                }
            })
        }
    }
}
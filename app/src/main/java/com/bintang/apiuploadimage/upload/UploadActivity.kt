package com.bintang.apiuploadimage.upload

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bintang.apiuploadimage.MainActivity
import com.bintang.apiuploadimage.R
import com.bintang.apiuploadimage.upload.model.ResponseUpload
import com.bintang.apiuploadimage.upload.utils.FilePath
import kotlinx.android.synthetic.main.activity_upload.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.log
import kotlin.random.Random

class UploadActivity : AppCompatActivity(), UploadView{
    private var REQUEST_IMAGE_GALLERY = 0
    private var REQUEST_IMAGE_CAMERA = 1
    private var REQUEST_PERMISSION = 2
    private var image_path : String? = null
    private var presenter: UploadPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        presenter = UploadPresenter(this)
        permissionLocation()

        btnUpload.setOnClickListener {
            actionUpload()
        }

        btnTambah.setOnClickListener {
            actionTambah()
        }
    }

    fun permissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_PERMISSION
            )
        }
    }

    private fun actionTambah() {
        val kode_barang = tambahkodebarang?.text.toString()
        val nama_barang = tambahnamabarang?.text.toString()
        val stock = tambahstock?.text.toString()
        val deskripsi = tambahdeskripsi?.text.toString()

        image_path?.let { presenter?.upload(kode_barang, nama_barang, stock, deskripsi, it) }


//        if(TextUtils.isEmpty(kode_barang)) {
////            tambahkodebarang?.error = "Wajib Diisi"
////            tambahkodebarang?.requestFocus()
//        } else if (TextUtils.isEmpty(nama_barang)) {
////            tambahnamabarang?.error = "Wajib Diisi"
////            tambahnamabarang?.requestFocus()
//        } else if(TextUtils.isEmpty(stock)) {
////            tambahstock?.error = "Wajib Diisi"
////            tambahstock?.requestFocus()
//        } else if (TextUtils.isEmpty(deskripsi)) {
////            tambahdeskripsi?.error = "Wajib Diisi"
////            tambahdeskripsi?.requestFocus()
//        }
    }

    private fun actionUpload() {
        AlertDialog.Builder(this)
            .setMessage("Other Apps")
            .setPositiveButton("Galery", DialogInterface.OnClickListener { dialogInterface, i ->
                Intent(Intent.ACTION_GET_CONTENT).also {
                    it.type = "image/*"
                    startActivityForResult(it, REQUEST_IMAGE_GALLERY)
                }
            })
            .setNegativeButton("Camera", DialogInterface.OnClickListener { dialogInterface, i ->
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    startActivityForResult(it, REQUEST_IMAGE_CAMERA)
                }
            }).show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK  && requestCode == REQUEST_IMAGE_GALLERY) {
           resultGallery(data)
        }else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAMERA) {
            resultCamera(data)
        }

    }

    private fun resultCamera(data: Intent?) {

        val image = data?.extras?.get("data")
        val random = Random.nextInt(0, 999999)
        val camera = "Camera$random"

        image_path = persistImage(image as Bitmap, camera)
        action_image.setImageBitmap(BitmapFactory.decodeFile(image_path))


    }

    private fun resultGallery(data: Intent?) {
        val image_bitmap = onSelectFromGalleryResult(data)
        action_image.setImageBitmap(image_bitmap)
    }

    private fun onSelectFromGalleryResult(data: Intent?): Bitmap {
        var bm: Bitmap? = null
        if (data !=null) {
            try {
                image_path = data.data?.let { FilePath.getPath(this, it) }
                Log.d("Galery", image_path ?: "")
                bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
        return bm!!
    }
    private fun persistImage(bitmap: Bitmap, date: String): String {
        val dirFile = filesDir
        val imageFile = File(dirFile, date+ ".png")
        val image_path = imageFile.path

        val os: OutputStream?
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        }catch (e: Exception) {
            Log.e(javaClass.simpleName, getString(R.string.error_writing_bitmap), e)
        }
        return image_path
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION ) {
          Toast.makeText(this, "Allow Permission", Toast.LENGTH_SHORT).show()
        }else {
           Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun isEmpty(msg: String) {
        AlertDialog.Builder(this)
                .setTitle("Peringatan")
                .setMessage("Tidak Boleh Kosong")
                .setNegativeButton("OK", DialogInterface.OnClickListener{ dialogInterface, i ->

                }).show()
    }

    override fun onSuccessupload(response: ResponseUpload) {
        if (response.isSuccess == true) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            response.message
            AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("Gagal")
                    .setNegativeButton("OK", DialogInterface.OnClickListener{ dialogInterface, i ->

                    }).show()
        }
    }

    override fun onErrorServer(message: String) {
        AlertDialog.Builder(this)
                .setTitle("Informasi")
                .setMessage("Error Server")
                .setNegativeButton("OK", DialogInterface.OnClickListener{dialogINterface, i ->

                }).show()
    }
}

package com.ar.androidware.sendimage

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class MainActivity : ComponentActivity() {
    private lateinit var bmp: Bitmap
    private lateinit var img: ImageView
    private lateinit var dialog: ProgressBar
    private lateinit var file: ByteArray
    private lateinit var encode: String
    private val image = registerForActivityResult(ActivityResultContracts.GetContent())
    {
            uri ->
        img.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        img = findViewById(R.id.img)
        dialog = findViewById(R.id.dialog)
        dismissProgressBar()
    }

    fun search(v: View)
    {
        image.launch("image/*")
    }

    fun send(v: View)
    {
        thaImage()
        val urlRegister = "http://192.168.0.95/GetImage/server.php"
        dialog.visibility = View.VISIBLE
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, urlRegister,
            { response ->
                try {
                    val jObj = JSONObject(response)
                    val error: Boolean = jObj.getBoolean("error")
                    if (!error) {
                        Toast.makeText(applicationContext, getString(R.string.ok), Toast.LENGTH_LONG).show()
                    } else {
                        val errorMsg: String = jObj.getString("error_msg")
                        Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_LONG).show()
                    }
                    dismissProgressBar()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    dismissProgressBar()
                }
            },
            { error ->
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                dismissProgressBar()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["img"] = encode
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun dismissProgressBar()
    {
        dialog.visibility = View.GONE
    }

    private fun thaImage()
    {
        // bmp = img.drawable.toBitmap()
        bmp = (img.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        file = baos.toByteArray()
        encode = Base64.encodeToString(file, Base64.DEFAULT)
        baos.close()
    }
}
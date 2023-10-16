package com.ar.androidware.getaudio

import android.Manifest
import android.R.attr.data
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var record: Button
    private var rec: MediaRecorder? = null
    private var already: Boolean = false
    private lateinit var output: File
    private lateinit var dialog: ProgressBar
    private lateinit var encode: String

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        record = findViewById(R.id.record)
        dialog = findViewById(R.id.dialog)
        dismissProgressBar()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun record(v: View)
    {
        if (!already) {
            already = true
            output = File(applicationContext.filesDir, "/audio.mp3")
            record.text = getText(R.string.recording)

            if (applicationContext.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                val permissions = (Manifest.permission.RECORD_AUDIO)
                ActivityCompat.requestPermissions(this, arrayOf(permissions),0)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rec = MediaRecorder(applicationContext)
                    startRecording()
                } else {
                    rec = MediaRecorder()
                    startRecording()
                }
            }
        }
        else {
            record.text = getText(R.string.record)
            rec?.stop()
            rec?.reset()

            var path: String = (applicationContext.filesDir).toString() + "/audio.mp3"
            Log.d("El Path Es: ", "" + path)
            val inputStream: InputStream = FileInputStream(path)
            val arr: ByteArray = readByte(inputStream)
            encode = Base64.encodeToString(arr, Base64.DEFAULT)
            inputStream.close()

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
                    params["img"] = encode
                    return HashMap()
                }
            }
            queue.add(stringRequest)
        }
    }

    @Throws(IOException::class)
    fun readByte(`is`: InputStream): ByteArray {
        val os = ByteArrayOutputStream()
        val buffer = ByteArray(0xFFFF)
        var len = `is`.read(buffer)
        while (len != -1) {
            os.write(buffer, 0, len)
            len = `is`.read(buffer)
        }
        return os.toByteArray()
    }

    private fun startRecording() {
        rec?.setAudioSource(MediaRecorder.AudioSource.MIC)
        rec?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        rec?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        rec?.setOutputFile(output)

        try {
            rec?.prepare()
            rec?.start()
        } catch (e: IOException) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun send()
    {

    }

    private fun dismissProgressBar()
    {
        dialog.visibility = View.GONE
    }
}
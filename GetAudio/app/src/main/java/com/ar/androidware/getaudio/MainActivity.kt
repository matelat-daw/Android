package com.ar.androidware.getaudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException


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

            // val path: String = (applicationContext.filesDir).toString() + "/audio.mp3"
            val path = File(applicationContext.filesDir, "/audio.mp3")
            var bytes: ByteArray? = null

            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(path)
                val buffer = ByteArray(4096)
                var readit = 0
                while (fis.read(buffer).also { readit = it } != -1) {
                    bytes = IOUtils.toByteArray(fis)
                }
            } catch (e: FileNotFoundException) {
                println("File not found: $e")
            } catch (e: IOException) {
                println("Exception reading file: $e")
            } finally {
                try {
                    fis?.close()
                } catch (ignored: IOException) {
                }
            }
            Log.d("La Ruta es: ", "" + path)
            Log.d("El MP3 es: ", "" + bytes)

            encode = Base64.encodeToString(bytes, Base64.DEFAULT)


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

    private fun dismissProgressBar()
    {
        dialog.visibility = View.GONE
    }
}
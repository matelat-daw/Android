package com.ar.androidware.walkietalkie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.READ_CONTACTS)

class MainActivity : AppCompatActivity() {
    private lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        if( applicationContext.checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
        else
        {
            showList()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Takes the user to the success fragment when permission is granted
                showList()
                Log.d("Sí Estoy Dentro.", "Pasa Siempre por aquí.")
            }
            else
            {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showList()
    {
        val parentLayout = findViewById<View>(android.R.id.content)
        Snackbar.make(parentLayout, "This is main activity", Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()

        context = this
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)

        val adapter = ViewAdapter(MockList.getModel(this) as ArrayList<Model>)
        recyclerView.adapter = adapter
    }

    companion object
    {
        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
package com.ar.androidware.walkietalkie

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import java.io.BufferedInputStream


data class Model(val image: Bitmap, val description: String)
var itemList: ArrayList<Model> = ArrayList()

object MockList {

    fun getModel(mainActivity: MainActivity):  List<Model> {
        return getContacts(mainActivity)
    }

    @SuppressLint("Range")
    private fun getContacts(mainActivity: MainActivity): List<Model> {

        val myBmp : Array<Bitmap?> = arrayOfNulls(100)
        var index = 0
        val cr : ContentResolver = mainActivity.contentResolver
        val cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)

        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val image = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, image)
                val photoStream = ContactsContract.Contacts.openContactPhotoInputStream(mainActivity.contentResolver, contactUri)
                if (photoStream != null) {
                    val buf = BufferedInputStream(photoStream)
                    myBmp[index] = BitmapFactory.decodeStream(buf)
                } else {
                    myBmp[index] = BitmapFactory.decodeResource(mainActivity.resources, (R.drawable.male))
                }
                val description = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val itemModel = myBmp[index]?.let { Model(it, description) }
                index++
                if (itemModel != null) {
                    itemList.add(itemModel)
                }
            }
            cur.close()
        }
        return itemList
    }
}
package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.app.LoaderManager
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.loader.content.CursorLoader
import java.io.InputStream
import java.io.Serializable


class ContactsHolder {
    var contact_holder: ArrayList<ContactModel> = ArrayList()

    fun getDataList() : ArrayList<ContactModel> {
        return contact_holder
    }

    fun setDataList(setlist : ArrayList<ContactModel>) {
        contact_holder = setlist
    }

    fun getDataById(position: Int) : ContactModel {
        return contact_holder[position]
    }
}

var ContactHolder = ContactsHolder()

class ContactModel : Serializable{
    var id: String? = null
    var name: String? = null
    var mobileNumber: String? = null
    var photo: Bitmap? = null
    var group: String? = null
    //var photoURI: Uri? = null
}

class ContactsFragment :
    Fragment(),
    ContactsRecyclerAdapter.OnListItemSelectedInterface {

    var contacts_list : ArrayList<ContactModel> = ArrayList()
    var contact_flag : Boolean = false

    // Define global mutable variables
    lateinit var ContactsRecyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val TAG = "ContactsFragment"
    private val READ_REQUEST_CODE = 100

    private fun setupPermissions() {
        val permission = checkSelfPermission(context!!,
            Manifest.permission.READ_CONTACTS)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
        else {
            SetupContactsView (true)
        }
    }

    private fun makeRequest() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
            READ_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i(TAG, "Permission has been granted by user")
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "Permission has been denied by user")
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.getBoolean("CONTACT_UPDATED")) {
            SetupContactsView(false)
        }
        else
            setupPermissions()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (contact_flag)
            outState.putBoolean("CONTACT_UPDATED", true)
        super.onSaveInstanceState(outState)
    }

    private fun SetupContactsView (update_contact: Boolean) {
        if (update_contact) {
            contacts_list = getContacts(requireContext())
            contact_flag = true
            Log.wtf(TAG, "Got Contacts")
            ContactHolder.setDataList(contacts_list)
        }

        activity?.also {
            viewManager = LinearLayoutManager(requireContext())
            viewAdapter = ContactsRecyclerAdapter(requireContext(), this, contacts_list)
            ContactsRecyclerView = it.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
    }

    override fun onItemSelected(view: View, position: Int) {
        val intent = Intent (activity, ContactInformation::class.java)
        intent.putExtra("POS", position)
        startActivity(intent)
    }

    public fun getContacts(ctx : Context) : ArrayList<ContactModel> {
        var list : ArrayList<ContactModel> = ArrayList()
        var contentResolver : ContentResolver = ctx.getContentResolver()
        var cursor : Cursor? = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        var dataCursor : Cursor? = contentResolver.query(ContactsContract.Data.CONTENT_URI, arrayOf(ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DATA1),
            ContactsContract.Data.MIMETYPE + "=?", arrayOf(ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE), null)
        var groups_cursor : Cursor? = contentResolver.query(ContactsContract.Groups.CONTENT_URI, arrayOf(ContactsContract.Groups._ID, ContactsContract.Groups.TITLE), null, null, null)
        var hash_group = HashMap<String, String>()
        var hash_data = HashMap<String, String>()

        if (groups_cursor != null && groups_cursor.count > 0) {
            while (groups_cursor.moveToNext()) {
                val group_title : String = groups_cursor.getString(1)
                val group_id : String = groups_cursor.getString(0)
                hash_group[group_id] = group_title
            }
        }
        if (dataCursor != null && dataCursor.count > 0) {
            while (dataCursor.moveToNext()) {
                val data_id : String = dataCursor.getString(0)
                val group_id : String = dataCursor.getString(1)
                if (hash_group[group_id] != null) {
                    val group_title: String = hash_group[group_id] as String
                    hash_data[data_id] = group_title
                }
            }
        }

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                var id : String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    var cursorInfo : Cursor? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", Array<String>(1){id}, null)
                    var inputStream : InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong()))

                    //var person : Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
                    //var pURI : Uri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)

                    var photo : Bitmap? = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream)
                    }
                    if (cursorInfo!!.moveToNext()) {
                        var info = ContactModel()
                        info.id = id
                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        var raw_number : String = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        info.mobileNumber = raw_number.substring(0, 3) + "-" + raw_number.substring(3, raw_number.lastIndex-3) + "-" + raw_number.substring(raw_number.lastIndex-3, raw_number.length)
                        info.photo = photo
                        //info.photoURI= pURI

                        if (hash_data[id] != null) {
                            var group_name = hash_data[id]
                            info.group = group_name
                        }

                        list.add(info)
                    }

                    cursorInfo.close()
                }
            }
            cursor.close()
        }
        return ArrayList(list.sortedWith(compareBy({it.name})))
    }


}

package com.example.myapplication


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.loader.app.LoaderManager
import android.widget.AdapterView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import androidx.loader.content.CursorLoader
import java.io.InputStream


/*
 * Defines an array that contains column names to move from
 * the Cursor to the ListView.
 */
@SuppressLint("InlinedApi")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
        ContactsContract.Contacts.DISPLAY_NAME
    }
)

// The column index for the _ID column
private const val CONTACT_ID_INDEX: Int = 0
// The column index for the CONTACT_KEY column
private const val CONTACT_KEY_INDEX: Int = 1

// Defines the text expression
@SuppressLint("InlinedApi")
private val SELECTION: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    else
        "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"

// Defines a variable for the search string
private val searchString: String = "placeholder search string"
// Defines the array to hold values that replace the ?
private val selectionArgs = arrayOf<String>(searchString)

/*
 * Defines an array that contains resource ids for the layout views
 * that get the Cursor column contents. The id is pre-defined in
 * the Android framework, so it is prefaced with "android.R.id"
 */
private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)

class ContactModel {
    var id: String? = null
    var name: String? = null
    var mobileNumber: String? = null
    var photo: Bitmap? = null
    var photoURI: Uri? = null
}

class ContactsFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<Cursor> {

    var contacts_list : ArrayList<ContactModel> = ArrayList()
    // Define global mutable variables
    // Define a ListView object
    lateinit var ContactsRecyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0
    // The contact's LOOKUP_KEY
    var contactKey: String? = null
    // A content URI for the selected contact
    var contactUri: Uri? = null
    // An adapter that binds the result Cursor to the ListView
    private val cursorAdapter: SimpleCursorAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*
        for (i in 0..99) {
            contacts_list.add(String.format("TEXT %d", i))
        }*/
        contacts_list = getContacts(requireContext())

        // Gets the RecycleView from the View list of the parent activity
        activity?.also {
            viewManager = LinearLayoutManager(requireContext())
            viewAdapter = ContactsRecyclerAdapter(contacts_list)
            /*
            viewAdapter.setOnItemClickListener(object : ContactsRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int) {
                    // Get the Cursor
                    val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
                        // Move to the selected contact
                        moveToPosition(position)
                        // Get the _ID value
                        contactId = getLong(CONTACT_ID_INDEX)
                        // Get the selected LOOKUP KEY
                        contactKey = getString(CONTACT_KEY_INDEX)
                        // Create the contact's content Uri
                        contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
                        /*
                         * You can use contactUri as the content URI for retrieving
                         * the details for a contact.
                         */
                    }
                }
            })*/
            ContactsRecyclerView = it.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): androidx.loader.content.Loader<Cursor> {
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        selectionArgs[0] = "%$searchString%"
        // Starts the query
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
            )
        } ?: throw IllegalStateException()
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, cursor: Cursor) {
        // Put the result Cursor in the adapter for the ListView
        cursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        cursorAdapter?.swapCursor(null)
    }

    public fun getContacts(ctx : Context) : ArrayList<ContactModel> {
        var list : ArrayList<ContactModel> = ArrayList()
        var contentResolver : ContentResolver = ctx.getContentResolver()
        var cursor : Cursor? = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                var id : String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    var cursorInfo : Cursor? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", Array<String>(1){id}, null)
                    var inputStream : InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong()))

                    var person : Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
                    var pURI : Uri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)

                    var photo : Bitmap? = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream)
                    }
                    while (cursorInfo!!.moveToNext()) {
                        var info : ContactModel = ContactModel()
                        info.id = id
                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        info.photo = photo
                        info.photoURI= pURI
                        list.add(info)
                    }

                    cursorInfo.close()
                }
            }
            cursor.close()
        }
        return list
    }


}

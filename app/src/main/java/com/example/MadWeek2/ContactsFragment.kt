package com.example.MadWeek2


import android.Manifest
import android.app.ActivityOptions
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.example.MadWeek2.Retrofit.ContactService
import com.example.MadWeek2.Retrofit.LoginService
import com.example.MadWeek2.Retrofit.RetrofitClient
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream


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

    fun sortData() {
        contact_holder = ArrayList(contact_holder.sortedWith(compareBy({it.name})))
    }
}

var ContactHolder = ContactsHolder()

class ContactModel{
    var id: String? = null
    var name: String? = null
    var mobileNumber: String? = null
    var photo: Bitmap? = null
    var group: String? = null
    //var photoURI: Uri? = null
}

class ContactsFragment :
    Fragment(),
    ContactsRecyclerAdapter.OnListItemSelectedInterface,
    SwipeRefreshLayout.OnRefreshListener {

    var contacts_list : ArrayList<ContactModel> = ArrayList()
    var first_start = true

    val REQUEST_NEW_CONTACT = 1
    val REQUEST_SHOW_INFO = 2

    val RESULT_SAVED = 1
    val RESULT_DELETED = 2

    // Define global mutable variables
    lateinit var ContactsRecyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val TAG = "ContactsFragment"
    private val PERM_REQUEST_CODE = 100

    var upload : FloatingActionButton? = null
    var download : FloatingActionButton? = null
    var addcontact : FloatingActionButton? = null
    var fab_menu : FloatingActionButton? = null
    var isMenuOpen = false

    lateinit var MyContactService: ContactService
    internal var compositeDisposable = CompositeDisposable()
    var cloud_contact_number : Int? = null
    var contact_buffer : JSONArray? = null

    /* ************************************************************** */

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_contacts, container, false)

        // Setup for server communication
        val retrofit = RetrofitClient.getInstance()
        MyContactService = retrofit.create(ContactService::class.java)

        val swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipe_contacts)
        swipeRefreshLayout.setOnRefreshListener (this)

        fab_menu = rootView!!.findViewById(R.id.fab_menu)
        upload = rootView.findViewById(R.id.fab_upload)
        download = rootView.findViewById(R.id.fab_download)
        addcontact = rootView.findViewById(R.id.fab_add_contacts)

        fab_menu!!.setOnClickListener { view ->
            menuOpen()
        }
        addcontact!!.setOnClickListener { view ->
            var intent = Intent(view.context, Contact_edit::class.java)
            startActivityForResult(intent, REQUEST_NEW_CONTACT)
        }
        upload!!.setOnClickListener { view ->
            val salt = find_login_info()
            if (salt != null) {
                var list = ContactHolder.getDataList()
                contact_update_number (salt, list.size.toString())
                Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_LONG).show()
                for (i in 0..(list.size - 1))
                    contact_upload(salt, i.toString(), list[i].name!!, list[i].mobileNumber!!, list[i].group)
                Toast.makeText(requireContext(), "Upload Done", Toast.LENGTH_LONG).show()
            }
        }
        download!!.setOnClickListener { view ->
            val salt = find_login_info()
            if (salt != null) {
                contact_get_num(salt)
                if (cloud_contact_number != null) {
                    contact_buffer = JSONArray()
                    contact_download(salt)
                }
            }
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (setupPermissions() && first_start) {
            SetupContactsView()
            first_start = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_NEW_CONTACT -> {
                if (resultCode == RESULT_SAVED)
                    SetupContactsView()
            }
            REQUEST_SHOW_INFO -> {
                when (resultCode) {
                    RESULT_SAVED -> SetupContactsView()
                    RESULT_DELETED -> SetupContactsView()
                }
            }
        }
    }

    private fun menuOpen() {
        if (!isMenuOpen) {
            upload!!.animate().translationY(-resources.getDimension(R.dimen.upload))
            download!!.animate().translationY(-resources.getDimension(R.dimen.download))
            addcontact!!.animate().translationY(-resources.getDimension(R.dimen.add_contact))
            isMenuOpen = true
        }
        else {
            upload!!.animate().translationY(0.toFloat())
            download!!.animate().translationY(0.toFloat())
            addcontact!!.animate().translationY(0.toFloat())
            isMenuOpen = false
        }
    }

    override fun onRefresh() {
        SetupContactsView()
        view!!.findViewById<SwipeRefreshLayout>(R.id.swipe_contacts).isRefreshing = false
    }

    fun SetupContactsView () {
        activity?.also {
            viewManager = LinearLayoutManager(requireContext())
            viewAdapter =
                ContactsRecyclerAdapter(requireContext(), this, ContactHolder.getDataList())
            Log.i("SetupContactsView", "print size ${ContactHolder.getDataList().size}")
            ContactsRecyclerView = it.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
            activity?.findViewById<SwipeRefreshLayout>(R.id.swipe_contacts)?.invalidate()
        }
    }

    override fun onItemSelected(view: View, position: Int) {
        val intent = Intent (activity, ContactInformation::class.java)
        intent.putExtra("POS", position)
        startActivityForResult(intent, REQUEST_SHOW_INFO)
    }

    override fun callOnClick(view: View, position: Int) {
        val intent = Intent (Intent.ACTION_CALL, Uri.fromParts("tel", ContactHolder.getDataById(position).mobileNumber, null))
        startActivity(intent)
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupPermissions() : Boolean{
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        )

        if (!hasPermissions(context!!, permissions)) {
            Log.i(TAG, "Permission denied")
            makeRequest()
            return false
        }
        else {
            getcontact_list()
            return true
        }
    }

    private fun makeRequest() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
            PERM_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERM_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i(TAG, "Permission has been granted by user")
                    getcontact_list()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "Permission has been denied by user")
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun getcontact_list() {
        Log.wtf(TAG, "Getting Contacts")
        contacts_list = getContacts(requireContext())

        ContactHolder.setDataList(contacts_list)
    }

    fun getContacts(ctx : Context) : ArrayList<ContactModel> {
        val list : ArrayList<ContactModel> = ArrayList()
        val contentResolver : ContentResolver = ctx.getContentResolver()
        val cursor : Cursor? = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val dataCursor : Cursor? = contentResolver.query(ContactsContract.Data.CONTENT_URI, arrayOf(ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DATA1),
            ContactsContract.Data.MIMETYPE + "=?", arrayOf(ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE), null)
        val groups_cursor : Cursor? = contentResolver.query(ContactsContract.Groups.CONTENT_URI, arrayOf(ContactsContract.Groups._ID, ContactsContract.Groups.TITLE), null, null, null)
        val hash_group = HashMap<String, String>()
        val hash_data = HashMap<String, String>()

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
                val id : String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val cursorInfo : Cursor? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", Array<String>(1){id}, null)
                    val inputStream : InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong()))

                    //var person : Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
                    //var pURI : Uri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)

                    var photo : Bitmap? = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream)
                    }
                    if (cursorInfo!!.moveToNext()) {
                        val info = ContactModel()
                        info.id = id
                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val raw_number : String = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        if(!raw_number.contains("-") && raw_number.length > 7)
                            info.mobileNumber = raw_number.substring(0, 3) + "-" + raw_number.substring(3, raw_number.lastIndex-3) + "-" + raw_number.substring(raw_number.lastIndex-3, raw_number.length)
                        else
                            info.mobileNumber = raw_number
                        info.photo = photo
                        //info.photoURI= pURI

                        if (hash_data[id] != null) {
                            val group_name = hash_data[id]
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

    private fun contact_update_number (salt: String, contact_number: String) {
        compositeDisposable.add(MyContactService.contact_update_number(salt, contact_number)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                val json_object = JSONObject(result)
                val success = json_object.getString("update_success")

                if (success != "success")
                    Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show()
            })
    }

    private fun contact_upload (salt: String, id: String, name: String, mobile_number: String, group: String?) {
        compositeDisposable.add(MyContactService.contact_upload(salt, id, name, mobile_number, group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                val json_object = JSONObject(result)
                val success = json_object.getString("upload_success")
                if (success != "success")
                    Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show()
            })
    }

    private fun contact_download (salt: String) {
        compositeDisposable.add(MyContactService.contact_download(salt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                val json_object = JSONArray(result)
                merge_json_list(json_object)
                //Toast.makeText(requireContext(), ""+result, Toast.LENGTH_SHORT).show()
            })
    }

    private fun contact_get_num (salt: String) {
        compositeDisposable.add(MyContactService.contact_get_num(salt)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                val json_object = JSONObject(result)
                val success = json_object.getString("get_number_success")
                if (success == "success") {
                    cloud_contact_number = json_object.getInt("contact_number")
                }
                //Toast.makeText(requireContext(), ""+result, Toast.LENGTH_SHORT).show()
            })
    }

    private fun find_login_info () : String? {
        val pref : SharedPreferences = activity!!.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return pref.getString("salt", "")
    }

    private fun merge_json_list(jsonarray: JSONArray) {
        var contact : ContactModel? = null
        var list = ContactHolder.getDataList()

        for (i in 0..cloud_contact_number!!-1) {
            var duplicate = false
            var jsonobject = jsonarray.get(i) as JSONObject
            contact = ContactModel()

            contact.name = jsonobject.getString("name")
            contact.mobileNumber = jsonobject.getString("mobile_number")
            contact.group = jsonobject.getString("group")
            for (i in 0..list.size-1) {
                if (list[i].name == contact.name) {
                    duplicate = true
                    break
                }
            }
            if (!duplicate) {
                list.add(contact)
                //Log.wtf("hello", "added contact")
            }
        }

        ContactHolder.setDataList(list)
        ContactHolder.sortData()
        SetupContactsView()
    }

}

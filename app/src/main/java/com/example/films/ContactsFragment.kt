package com.example.films

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

//@SuppressLint("InlinedApi")
private val FROM_COLUMNS: Array<String> = arrayOf(
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)
private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)
private val SELECTION: String =
    "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
private const val CONTACT_ID_INDEX: Int = 0
private const val CONTACT_KEY_INDEX: Int = 1

class ContactsFragment() :
        Fragment(R.layout.fragment_contacts),
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    lateinit var contactsList: ListView
    var contactId: Long = 0
    var contactKey: String? = null
    var contactUri: Uri? = null
    private var cursorAdapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.also {
            contactsList = it.findViewById<ListView>(R.id.contact_list)
            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.contact_item,
                null,
                FROM_COLUMNS,
                TO_IDS,
                0
            )
            contactsList.onItemClickListener = this
            contactsList.adapter = cursorAdapter
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
            moveToPosition(position)
            contactId = getLong(CONTACT_ID_INDEX)
            contactKey = getString(CONTACT_KEY_INDEX)
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
        }
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null, //SELECTION,
                null,
                null
            )
        } ?: throw IllegalStateException()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        cursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter?.swapCursor(null)
    }

}
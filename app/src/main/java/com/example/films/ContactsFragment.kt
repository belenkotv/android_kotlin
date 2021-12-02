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

class ContactsFragment() :
        Fragment(R.layout.fragment_contacts),
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    lateinit var contactsList: ListView
    var contactId: Long = 0
    var contactKey: String? = null
    var contactUri: Uri? = null
    private val cursorAdapter: SimpleCursorAdapter? = null

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
            contactsList = it.findViewById<ListView>(android.id.list)
            // Gets a CursorAdapter
            cursorAdapter = SimpleCursorAdapter(
                    it,
                    R.layout.contact_list_item,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0
            )
            // Sets the adapter for the ListView
            contactsList.adapter = cursorAdapter
        }
    }

}
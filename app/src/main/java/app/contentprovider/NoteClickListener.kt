package app.contentprovider

import android.database.Cursor

interface NoteClickListener {
    fun noteClickItem(cursor: Cursor){

    }

    fun noteRemoveItem(cursor: Cursor?){

    }
}
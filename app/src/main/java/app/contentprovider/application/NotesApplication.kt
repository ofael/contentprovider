package app.contentprovider.application

import android.app.Application
import app.contentprovider.database.NotesDataBaseHelper

class NotesApplication : Application() {

    var NotesHelper : NotesDataBaseHelper? = null
        private set
    override fun onCreate(){
        super.onCreate()
        NotesHelper = NotesDataBaseHelper(this)

    }
}
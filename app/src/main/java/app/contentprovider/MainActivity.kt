package app.contentprovider

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.contentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES
import app.contentprovider.database.NotesProvider.Companion.URI_NOTES
import com.google.android.material.floatingactionbutton.FloatingActionButton

//Precisamos instanciar o LoaderManager, faz a busca em segundo plano no cursor
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    lateinit var notesRecycleView : RecyclerView
    lateinit var noteAdd : FloatingActionButton

    lateinit var adapter : NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd = findViewById(R.id.note_add)
        noteAdd.setOnClickListener{

        }

        adapter = NotesAdapter(object : NoteClickListener{
            override fun noteClickItem(cursor: Cursor) {
                val id: Long = cursor.getLong(cursor.getColumnIndex(_ID))
            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id : Long? = cursor?.getLong((cursor.getColumnIndex(_ID)))
                contentResolver.delete(Uri.withAppendedPath(URI_NOTES, id.toString()), null, null)
            }
        })
        adapter.setHasStableIds(true)

        notesRecycleView = findViewById(R.id.notes_recycle)
        notesRecycleView.layoutManager = LinearLayoutManager(this)
        notesRecycleView.adapter = adapter
    }
    //instanciar aquilo que será buscado
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        CursorLoader(this,URI_NOTES, null, null, null, TITLE_NOTES)

    //pegar os dados recebidos do on creatloader e manipular
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if(data != null){

        }
    }
    //acabar com a pesquisa em segundo plano no LoaderManager
    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("Not yet implemented")
    }
}
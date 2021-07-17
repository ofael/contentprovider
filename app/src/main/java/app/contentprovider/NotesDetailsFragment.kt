package app.contentprovider

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import app.contentprovider.database.NotesDataBaseHelper.Companion.DESCRIPTION_NOTES
import app.contentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES
import app.contentprovider.database.NotesProvider.Companion.URI_NOTES
import com.google.android.material.textfield.TextInputEditText

class NotesDetailsFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var noteEditTitle : TextInputEditText
    private lateinit var noteEditDescription : TextInputEditText
    private var id : Long = 0

    companion object{
        private const val EXTRA_ID = "id"
        fun newInstance(id : Long) : NotesDetailsFragment{
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)

            val notesFragment = NotesDetailsFragment()
            notesFragment.arguments = bundle

            return notesFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.note_detail, null)

        noteEditTitle = view?.findViewById(R.id.note_edt_title) as TextInputEditText
        noteEditDescription = view.findViewById(R.id.note_edt_description) as TextInputEditText

        var newNote = true
        if(arguments != null && arguments?.getLong(EXTRA_ID) != 0L){
            id = arguments?.getLong(EXTRA_ID) as Long

            val uri = Uri.withAppendedPath(URI_NOTES,id.toString())
            val cursor =
                activity?.contentResolver?.query(uri,null,null,null,null)

            if(cursor?.moveToNext() as Boolean){
                newNote = false
                noteEditTitle.setText(cursor.getString(cursor.getColumnIndex(TITLE_NOTES)))
                noteEditDescription.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION_NOTES)))
            }
            cursor.close()
        }
        return AlertDialog.Builder(activity as  Activity)
            .setTitle(if(newNote) "Nova mensagem" else "Editar mensagem")
            .setView((view))
            .setPositiveButton("Salvar", this)
            .setNegativeButton("Cancelar", this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        var values = ContentValues()
        values.put(TITLE_NOTES, noteEditTitle.text.toString())
        values.put(DESCRIPTION_NOTES,noteEditDescription.text.toString())

        if (id != 0L) {
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        } else {
            context?.contentResolver?.insert(URI_NOTES, values)
        }
    }
}
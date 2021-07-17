package app.contentprovider.database

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns._ID
import app.contentprovider.database.NotesDataBaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {
    //mUriMatcher - responsavel por fazer a validação da url de requisação do content provider
    private lateinit var mUriMatcher : UriMatcher
    private lateinit var dbHelper: NotesDataBaseHelper

    //inicialização dos dados
    override fun onCreate(): Boolean {
        //instanciando a variavel vazia
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        //definir os endereços e as identificações que o nosso content provider vai ter
        //addURI("Qual autoridade vamos adicionar a URL", "Definir um path")
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        //outra URL
        mUriMatcher.addURI(AUTHORITY,"notes/#", NOTES_BY_ID )
        if(context != null){
            dbHelper = NotesDataBaseHelper(context as Context)
        }
        return true
    }

    //deletar dados do provider
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        //todo content provider tem que verificar a URL
        //.match usado para verificar se a uri do construtor é valida
        //VERIFICANDO SE A REQUISIÇÃO FOI POR ID
        if(mUriMatcher.match(uri) == NOTES_BY_ID){
            //variavel que permite usar banco de dados, writableDatabase
            val db : SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect : Int = db.delete(TABLE_NOTES, "$_ID=?", arrayOf(uri.lastPathSegment))
            db.close()

            //manusear o nosso content provider
            context?.contentResolver?.notifyChange(uri, null)

            return linesAffect
        }else{
            throw UnsupportedSchemeException("URL Inválida para exclusão!")
        }
    }

    //validar uma url, só serve para arquivos
    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("Uri não implementavel")

    //inserir dados na aplicação
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if(mUriMatcher.match(uri) == NOTES){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id: Long = db.insert(TABLE_NOTES, null, values)
            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            //notificar as mudanças
            context?.contentResolver?.notifyChange(uri, null)
            return insertUri
        }else{
            throw UnsupportedSchemeException("Uri inválida para inserção")
        }
    }

    //select do provider, fazemos as aplicações no content provider etc.., sempre retorna um cursor
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        return when{
            mUriMatcher.match(uri) == NOTES -> {
                val db : SQLiteDatabase = dbHelper.writableDatabase
                //toda requisição do nosso sql vai ser armazenada na nossa variavel cursor
                val cursor =
                    db.query(TABLE_NOTES, projection, selection, selectionArgs, null,null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }

            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db : SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(TABLE_NOTES,projection,"$_ID=?",
                    arrayOf(uri.lastPathSegment),null,null,sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }

            else -> {
                throw  UnsupportedSchemeException("Uri não implementada")
            }
        }
    }

    //atualização do provider
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if(mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val lineAffect =
                db.update(TABLE_NOTES, values, "$_ID=?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return lineAffect

        } else{
            throw UnsupportedSchemeException("Uri não implementada")
        }
    }

    companion object {
        //define o endereço do provider
        const val AUTHORITY = "app.contentprovider.provider"

        //converte em URI a string que vamos passar
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY")
        //nomeando URL de notes, o mesmo que "content://app.contentprovider.provider/notes
        //withAppendedPath cria a "/" do caminho
        val URI_NOTES: Uri = Uri.withAppendedPath(BASE_URI, "notes")
        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}
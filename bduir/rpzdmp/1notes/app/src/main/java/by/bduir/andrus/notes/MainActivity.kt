// Приложение для ведения заметок пользователя.
// Разработать приложение для ведения заметок пользователя (Заголовок и содержание),
// предусмотреть поиск по дате и содержимому записей.
// Реализовать возможность группировки записей по меткам(цвет) или тэгам (#name).
// В случае если создается пустая заметка, вносить в поле Заголовок текущие дату и время.
// Для хранения использовать БД используемой ОС.
// В качестве примера такого приложения EverNote, Google Keep
package by.bduir.andrus.notes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.ClipboardManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Note>()

    private var mSharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSharedPref = this.getSharedPreferences("Data", MODE_PRIVATE)
        when (mSharedPref!!.getString("Sort", "newest")) {
            "newest" -> loadQueryNewest("%")
            "oldest" -> loadQueryOldest("%")
            "ascending" -> loadQueryAscending("%")
            "descending" -> loadQueryDescending("%")
        }
        //Load from DB
        loadQueryAscending("%")
    }

    override fun onResume() {
        super.onResume()
        when (mSharedPref!!.getString("Sort", "newest")) {
            "newest" -> loadQueryNewest("%")
            "oldest" -> loadQueryOldest("%")
            "ascending" -> loadQueryAscending("%")
            "descending" -> loadQueryDescending("%")
        }
    }

    private fun showSearchDialog() {
        val sortOptions = arrayOf("Newest", "Oldest", "Title(Ascending)", "Title(Descending)")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Sort by")
        mBuilder.setIcon(R.drawable.ic_action_sort)
        mBuilder.setSingleChoiceItems(sortOptions, -1) { dialogInterface, i ->
            if (i == 0) {
                // newest first
                Toast.makeText(this, "Newest", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "newest")
                editor.apply()
                loadQueryNewest("%")
            }
            if (i == 1) {
                // oldest first
                Toast.makeText(this, "Oldest", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "oldest")
                editor.apply()
                loadQueryOldest("%")
            }
            if (i == 2) {
                // title ascending
                Toast.makeText(this, "Title(Ascending)", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "ascending")
                editor.apply()
                loadQueryAscending("%")
            }
            if (i == 3) {
                // title descending
                Toast.makeText(this, "Title(Descending)", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "descending")
                editor.apply()
                loadQueryDescending("%")
            }
            dialogInterface.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("Newest", "Oldest", "Title(Ascending)", "Title(Descending)")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Sort by")
        mBuilder.setIcon(R.drawable.ic_action_sort)
        mBuilder.setSingleChoiceItems(sortOptions, -1) { dialogInterface, i ->
            if (i == 0) {
                // newest first
                Toast.makeText(this, "Newest", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "newest")
                editor.apply()
                loadQueryNewest("%")
            }
            if (i == 1) {
                // oldest first
                Toast.makeText(this, "Oldest", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "oldest")
                editor.apply()
                loadQueryOldest("%")
            }
            if (i == 2) {
                // title ascending
                Toast.makeText(this, "Title(Ascending)", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "ascending")
                editor.apply()
                loadQueryAscending("%")
            }
            if (i == 3) {
                // title descending
                Toast.makeText(this, "Title(Descending)", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "descending")
                editor.apply()
                loadQueryDescending("%")
            }
            dialogInterface.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun loadQueryAscending(title: String) {
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        // sort by title
        val cursor = dbManager.query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        // sort by ascending
        if (cursor.moveToFirst())
            do {
                val noteID = cursor.getInt(cursor.getColumnIndex("ID"))
                val noteTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val noteDesc = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(noteID, noteTitle, noteDesc))
            } while (cursor.moveToNext())
        //adapter
        val myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        notesLv.adapter = myNotesAdapter
        //get total number of tasks from ListView
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You have $total note(s) in list..."
        }
    }

    private fun loadQueryDescending(title: String) {
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        // sort by title
        val cursor = dbManager.query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        // sort by descending
        if (cursor.moveToLast())
            do {
                val noteID = cursor.getInt(cursor.getColumnIndex("ID"))
                val noteTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val noteDesc = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(noteID, noteTitle, noteDesc))
            } while (cursor.moveToPrevious())
        //adapter
        val myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        notesLv.adapter = myNotesAdapter
        //get total number of tasks from ListView
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You have $total note(s) in list..."
        }
    }

    private fun loadQueryNewest(title: String) {
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        // sort by title
        val cursor = dbManager.query(projections, "ID like ?", selectionArgs, "ID")
        listNotes.clear()
        // sort by newest
        if (cursor.moveToLast())
            do {
                val noteID = cursor.getInt(cursor.getColumnIndex("ID"))
                val noteTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val noteDesc = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(noteID, noteTitle, noteDesc))
            } while (cursor.moveToPrevious())
        //adapter
        val myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        notesLv.adapter = myNotesAdapter
        //get total number of tasks from ListView
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You have $total note(s) in list..."
        }
    }

    private fun loadQueryOldest(title: String) {
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        // sort by title
        val cursor = dbManager.query(projections, "ID like ?", selectionArgs, "ID")
        listNotes.clear()
        // sort by newest
        if (cursor.moveToFirst())
            do {
                val noteID = cursor.getInt(cursor.getColumnIndex("ID"))
                val noteTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val noteDesc = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(noteID, noteTitle, noteDesc))
            } while (cursor.moveToNext())
        //adapter
        val myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        notesLv.adapter = myNotesAdapter
        //get total number of tasks from ListView
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "You have $total note(s) in list..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        //searchView
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadQueryAscending("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadQueryAscending("%$newText%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null)
            when (item.itemId) {
                R.id.addNote -> startActivity(Intent(this, AddNoteActivity::class.java))
                R.id.action_sort -> showSortDialog()
            }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter(context: Context, private var listNotesAdapter: ArrayList<Note>) :
        BaseAdapter() {
        private var context: Context? = context

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row.xml
            val myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDes
            //delete button click
            myView.deleteBtn.setOnClickListener {
                val dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManager.delete("ID=?", selectionArgs)
                loadQueryAscending("%")
            }
            //edit//update button click
            myView.editBtn.setOnClickListener {
                goToUpdateFun(myNote)
            }
            //copy btn click
            myView.copyBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatenate
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s // add to clipboard
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_SHORT).show()
            }
            //share btn click
            myView.shareBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatenate
                val s = title + "\n" + desc
                //share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    private fun goToUpdateFun(myNote: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID", myNote.nodeID) //put id
        intent.putExtra("name", myNote.nodeName) //ut name
        intent.putExtra("des", myNote.nodeDes) //put description
        startActivity(intent) //start activity
    }
}

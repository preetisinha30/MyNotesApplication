package com.example.notesapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.ui.theme.MyNotesApplicationTheme
import kotlinx.coroutines.launch
import java.util.Date

class NotesActivity : AppCompatActivity() {
    private lateinit var adapter:NotesRVAdapter
    private val noteDatabase by lazy {NoteDatabase.getDatabase(this).noteDao()}
    var toolbar: Toolbar?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        toolbar = findViewById<Toolbar>(R.id.xml_toolbar).also {
            setSupportActionBar(it)
        }
        setRecyclerView()
        observeNotes()
    }

    private fun setRecyclerView() {
        var notes_recyclerview = findViewById<RecyclerView>(R.id.notes_recyclerview)
        notes_recyclerview.layoutManager = LinearLayoutManager(this)
        notes_recyclerview.setHasFixedSize(true)
        /*val divider = DividerItemDecoration(
            this,
            DividerItemDecoration.VERTICAL
        )
        divider.setDrawable(ContextCompat.getDrawable(baseContext, R.drawable.divider_notes)!!)
        notes_recyclerview.addItemDecoration(divider)*/
        //notes_recyclerview.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
        adapter = NotesRVAdapter()
        adapter.setItemListener(object:RecyclerClickListener{
            override fun onItemRemoveClick(position: Int) {
                val notes = adapter.currentList.toMutableList()
                val noteText = notes[position].noteText
                val noteDate = notes[position].dateAdded
                val removeNote = Note(noteDate,noteText?:"")
                //notes.removeAt(position)
                //adapter.submitList(notes)
                lifecycleScope.launch{
                    noteDatabase.deleteNote(removeNote)
                }
            }

            override fun onItemClick(position: Int) {
                val notes = adapter.currentList.toMutableList()
                val intent = Intent(this@NotesActivity,AddNoteActivity::class.java)
                intent.putExtra("note_date_added",notes[position].dateAdded)
                intent.putExtra("note_text",notes[position].noteText)
                editNoteResultLauncher.launch(intent)
            }
        })
        notes_recyclerview.adapter = adapter
    }

    private fun observeNotes(){
        lifecycleScope.launch {
            noteDatabase.getNotes().collect{notesList->
                if(notesList.isNotEmpty()){
                    adapter.submitList(notesList)
                }

            }
        }
    }

    private val newNoteResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == RESULT_OK)
        {
            val notes = adapter.currentList.toMutableList()
            val noteDateAdded = Date()
            val noteText = result.data?.getStringExtra("note_text")
            val newNote = Note(noteDateAdded,noteText ?:"")
            // notes.add(newNote)
            // adapter.submitList(notes)
            lifecycleScope.launch {
                noteDatabase.addNote(newNote)
            }
        }
    }

    private val editNoteResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == RESULT_OK)
        {
            val notes = adapter.currentList.toMutableList()
            val noteDateAdded = result.data?.getSerializableExtra("node_date_text") as Date
            val noteText = result.data?.getStringExtra("note_text")
            val editednote = Note(noteDateAdded,noteText?:"")
            /*for(note in notes)
            {
                if(note.dateAdded == noteDateAdded){
                    note.noteText = noteText ?:""
                }
            }
            adapter.submitList(notes)
            adapter.notifyDataSetChanged()*/
            lifecycleScope.launch{
                noteDatabase.updateNote(editednote)
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_note_menu_item)
        {
            val intent = Intent(this@NotesActivity,AddNoteActivity::class.java)
            newNoteResultLauncher.launch(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notes,menu)
        return super.onCreateOptionsMenu(menu)
    }
}


package com.example.event_list.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.event_list.R
import com.example.event_list.model.EventItem
import com.example.event_list.presenter.EventListView
import com.example.event_list.presenter.EventPresenter
import github.showang.kat.assign
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import me.showang.recyct.RecyctAdapter
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), EventListView {

    companion object {
        const val TAG = "MainActivity"
    }

    private var mAdapter: RecyctAdapter? = null
    private val presenter: EventPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAppbar()
        initItemRecycler()
        initFab()
        presenter.eventListView = this
    }

    override fun onStart() {
        super.onStart()
        if (presenter.eventListView == null) {
            presenter.eventListView = this
            mAdapter?.notifyDataSetChanged()
        }
        if (!presenter.initializing) progress.visibility = View.GONE
    }

    override fun onStop() {
        presenter.eventListView = null
        super.onStop()
    }

    private fun initFab() {
        fab.setOnClickListener {
            EventDetailDialogFragment.createInstance().show(supportFragmentManager, "create")
        }
    }

    private fun initAppbar() = toolbar.let(::setSupportActionBar)

    private fun initItemRecycler() = itemRecyclerView?.apply {
        layoutManager = LinearLayoutManager(context)
        adapter = RecyctAdapter(presenter.eventList).apply {
            register(EventRecyctItem()) { data, index ->
                (data as? EventItem)?.id?.let(EventDetailDialogFragment.Companion::createInstance)
                    ?.show(supportFragmentManager, "$index")
            }
        }.assign(::mAdapter)
    }?.also(ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            presenter.delete(viewHolder.adapterPosition)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            presenter.swapItem(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
    })::attachToRecyclerView)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                presenter.clearEvents()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onEventsUpdate() {
        progress.visibility = View.GONE
        mAdapter?.notifyDataSetChanged()
    }

    override fun onEventItemSwapped(from: Int, to: Int) {
        mAdapter?.notifyItemMoved(from, to)
    }

    override fun onEventInsert(atIndex: Int) {
        mAdapter?.notifyItemInserted(atIndex)
    }

    override fun onEventItemUpdate(atIndex: Int) {
        mAdapter?.notifyItemChanged(atIndex)
    }

    override fun onEventDelete(atIndex: Int, item: EventItem) {
        mAdapter?.notifyItemRemoved(atIndex)
        Toast.makeText(this, "Item \"${item.title}\" removed.", Toast.LENGTH_LONG).show()
    }

}


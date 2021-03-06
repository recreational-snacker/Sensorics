package com.aconno.sensorics.ui

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.adapter.ActionAdapter
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.dagger.actionlist.ActionListComponent
import com.aconno.sensorics.dagger.actionlist.ActionListModule
import com.aconno.sensorics.dagger.actionlist.DaggerActionListComponent
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_action_list.*
import javax.inject.Inject

/**
 * @author aconno
 */
class ActionListFragment : Fragment(), ItemClickListener<Action> {

    private lateinit var actionAdapter: ActionAdapter
    private var snackbar: Snackbar? = null

    @Inject
    lateinit var getAllActionsUseCase: GetAllActionsUseCase

    @Inject
    lateinit var deleteActionUseCase: DeleteActionUseCase

    private val actionListComponent: ActionListComponent by lazy {
        val sensoricsApplication: SensoricsApplication? =
            context?.applicationContext as? SensoricsApplication

        DaggerActionListComponent.builder()
            .appComponent(sensoricsApplication?.appComponent)
            .actionListModule(ActionListModule())
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_action_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionListComponent.inject(this)
        actionAdapter = ActionAdapter(mutableListOf(), this)
        action_list.adapter = actionAdapter

        action_list.itemAnimator = DefaultItemAnimator()
        action_list.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        context?.let {
            val swipeToDeleteCallback = object : SwipeToDeleteCallback(it) {

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val action = actionAdapter.getAction(position)
                    actionAdapter.removeAction(position)

                    snackbar = Snackbar
                        .make(container_fragment, "${action.name} removed!", Snackbar.LENGTH_LONG)
                    snackbar?.setAction("UNDO") {
                        actionAdapter.addActionAtPosition(action, position)
                    }

                    snackbar?.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                                || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE
                                || event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                                || event == Snackbar.Callback.DISMISS_EVENT_MANUAL
                            ) {
                                deleteActionUseCase.execute(action)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe()
                            }
                        }
                    })
                    snackbar?.setActionTextColor(Color.YELLOW)
                    snackbar?.show()
                }
            }
            ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(action_list)
        }

        add_action_button.setOnClickListener {
            snackbar?.dismiss()
            startAddActionActivity()
        }
    }

    private fun startAddActionActivity() {
        context?.let { ActionDetailsActivity.start(it) }
    }

    override fun onResume() {
        super.onResume()
        getAllActionsUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initActionList(actions) }
    }

    private fun initActionList(actions: List<Action>) {
        actionAdapter.setActions(actions)
        if (actions.isEmpty()) {
            action_list_empty_view.visibility = View.VISIBLE
        } else {
            action_list_empty_view.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(item: Action) {
        snackbar?.dismiss()
        context?.let { ActionDetailsActivity.start(it, item.id) }
    }

    companion object {

        fun newInstance(): ActionListFragment {
            return ActionListFragment()
        }
    }
}

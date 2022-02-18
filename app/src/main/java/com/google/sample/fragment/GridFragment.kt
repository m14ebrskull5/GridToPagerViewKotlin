package com.google.sample.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.sample.MainActivity
import com.google.sample.R
import com.google.sample.adapter.GridAdapter

class GridFragment:Fragment() {
    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grid, container, false) as RecyclerView
        view.adapter = GridAdapter(GridFragment@this)
        recyclerView = view
        prepareTransitions()
        postponeEnterTransition()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollToPosition()
    }

    private fun scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                recyclerView.removeOnLayoutChangeListener(this)
                val layoutManager = recyclerView.layoutManager
                val viewAtPosition =
                    layoutManager!!.findViewByPosition(MainActivity.currentPosition)
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)
                ) {
                    recyclerView.post { layoutManager!!.scrollToPosition(MainActivity.currentPosition) }
                }
            }
        })
    }

    private fun prepareTransitions() {
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)
        /**
         * At a high level, here's how to make a fragment transition with shared elements:
         *
         * Assign a unique transition name to each shared element view.
         * Add shared element views and transition names to the FragmentTransaction.
         * Set a shared element transition animation.
         * First, you must assign a unique transition name to each shared element view to allow the
         * views to be mapped from one fragment to the next. Set a transition name on shared ele
         * ments in each fragment layout using ViewCompat.setTransitionName(), which provides compatibility
         * for API levels 14 and above. As an example, the transition name for
         * an ImageView in fragments A and B can be assigned as follows:
         *
         *
         */
        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder: RecyclerView.ViewHolder = recyclerView
                        .findViewHolderForAdapterPosition(MainActivity.currentPosition) ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] =
                        selectedViewHolder.itemView.findViewById(R.id.card_image)
                }
            })
    }
}

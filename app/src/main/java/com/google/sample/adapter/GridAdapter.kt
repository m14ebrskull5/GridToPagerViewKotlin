package com.google.sample.adapter

import android.graphics.drawable.Drawable
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.sample.MainActivity
import com.google.sample.R
import com.google.sample.adapter.ImageData.IMAGE_DRAWABLES
import com.google.sample.fragment.GridFragment
import com.google.sample.fragment.ImagePagerFragment
import java.lang.String
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.Any
import kotlin.Boolean
import kotlin.Int

class GridAdapter(var gridFragment: GridFragment) : RecyclerView.Adapter<GridAdapter.ImageViewHolder>() {
    private val requestManager: RequestManager = Glide.with(gridFragment)
    private val viewHolderListener: ViewHolderListener = ViewHolderListenerImpl(gridFragment)

    class ViewHolderListenerImpl(val fragment: Fragment): GridAdapter.ViewHolderListener {
        private val enterTransitionStarted: AtomicBoolean = AtomicBoolean()
        override fun onLoadCompleted(view: ImageView, adapterPosition: Int) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (MainActivity.currentPosition !== adapterPosition) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            fragment.startPostponedEnterTransition()
        }

        override fun onItemClicked(view: View, adapterPosition: Int) {

            // Update the position.
            MainActivity.currentPosition = adapterPosition

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            (fragment.exitTransition as TransitionSet).excludeTarget(view, true)

            val transitioningView = view.findViewById<ImageView>(R.id.card_image)
            fragment.parentFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true) // Optimize for shared element transition
                .addSharedElement(transitioningView, transitioningView.transitionName)
                .replace(
                    R.id.fragment_container, ImagePagerFragment(), ImagePagerFragment::class.java
                        .getSimpleName()
                )
                .addToBackStack(null)
                .commit()
        }

    }

    interface ViewHolderListener {
        fun onLoadCompleted(view: ImageView, adapterPosition: Int)

        fun onItemClicked(view: View, adapterPosition: Int)
    }

    class ImageViewHolder(
        var itemView: View,
        var requestManager: RequestManager,
        var viewHolderListener: ViewHolderListener
    ) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        private val image: ImageView = itemView.findViewById(R.id.card_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {

            // Let the listener start the ImagePagerFragment.
            viewHolderListener.onItemClicked(v, adapterPosition)
        }

        fun onBind() {
            val adapterPosition = adapterPosition
            setImage(adapterPosition)
            // Set the string value of the image resource as the unique transition name for the view.
            image.setTransitionName(String.valueOf(IMAGE_DRAWABLES.get(adapterPosition)))
        }

        private fun setImage(adapterPosition: Int) {

            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            requestManager
                .load(IMAGE_DRAWABLES[adapterPosition])
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any,
                        target: Target<Drawable?>, isFirstResource: Boolean
                    ): Boolean {
                        viewHolderListener.onLoadCompleted(image, adapterPosition)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolderListener.onLoadCompleted(image, adapterPosition)
                        return false
                    }
                })
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_card, parent, false)

        return ImageViewHolder(view, requestManager, viewHolderListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return IMAGE_DRAWABLES.size
    }

}

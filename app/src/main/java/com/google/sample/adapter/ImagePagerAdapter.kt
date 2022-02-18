package com.google.sample.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.sample.adapter.ImageData.IMAGE_DRAWABLES
import com.google.sample.fragment.ImageFragment
import com.google.sample.fragment.ImagePagerFragment

class ImagePagerAdapter(imagePagerFragment: ImagePagerFragment) : FragmentStatePagerAdapter(
    imagePagerFragment.getChildFragmentManager(),
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return IMAGE_DRAWABLES.size
    }

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(IMAGE_DRAWABLES[position])
    }

}

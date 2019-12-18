package com.liang.switcher

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ViewSwitcher

/**
 * TODO: document your custom view class.
 */
class Switcher @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewSwitcher(context, attrs) {






    abstract class Adapter<V : View> {
        abstract fun onCreateView(parent: ViewGroup, viewType: Int): V?

        abstract fun onBindView(view: V, position: Int)
    }

}

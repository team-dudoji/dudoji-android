package com.dudoji.android.util.modal

import androidx.fragment.app.Fragment

open class ModalFragment : Fragment() {
    protected lateinit var close: () -> Unit
    fun setCloseFun(close: () -> Unit) {
        this.close = close
    }
}
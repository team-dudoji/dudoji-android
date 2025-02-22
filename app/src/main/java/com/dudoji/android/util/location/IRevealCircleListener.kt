package com.dudoji.android.util.location

import com.dudoji.android.model.RevealCircle
import com.dudoji.android.util.listener.IListener

interface IRevealCircleListener: IListener<RevealCircle> {
    fun onRevealCircleAdded(revealCircle: RevealCircle)

    override fun onEvent(revealCircle: RevealCircle) {
        onRevealCircleAdded(revealCircle)
    }
}
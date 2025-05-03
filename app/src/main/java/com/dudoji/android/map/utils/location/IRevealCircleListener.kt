package com.dudoji.android.map.utils.location

import com.dudoji.android.map.domain.RevealCircle
import com.dudoji.android.util.listener.IListener

interface IRevealCircleListener: IListener<RevealCircle> {
    fun onRevealCircleAdded(revealCircle: RevealCircle)

    override fun onEvent(revealCircle: RevealCircle) {
        onRevealCircleAdded(revealCircle)
    }
}
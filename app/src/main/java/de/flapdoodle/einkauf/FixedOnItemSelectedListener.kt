package de.flapdoodle.einkauf

import android.view.View
import android.widget.AdapterView

class FixedOnItemSelectedListener(
    private val delegate: AdapterView.OnItemSelectedListener
) : AdapterView.OnItemSelectedListener {

    private var userInteraction=false

    override fun onNothingSelected(p0: AdapterView<*>?) {
        if (userInteraction) {
            delegate.onNothingSelected(p0)
        }
        userInteraction=true
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (userInteraction) {
            delegate.onItemSelected(p0,p1,p2,p3)
        }

        userInteraction=true
    }

    public fun nextEventIsNotFromUser() {
        userInteraction=false
    }
}
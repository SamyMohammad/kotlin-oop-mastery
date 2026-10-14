package m12_delegation

import common.log
import common.note
import common.section
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/*
 * L03 - Writing your own delegate
 *
 * `var x: T by D` compiles to:
 *     private val x$delegate = D
 *     get() = x$delegate.getValue(this, ::x)
 *     set(v) = x$delegate.setValue(this, ::x, v)
 *
 * So ANY object with these operator functions can be a delegate:
 *     operator fun getValue(thisRef: R, property: KProperty<*>): T
 *     operator fun setValue(thisRef: R, property: KProperty<*>, value: T)
 * Or implement ReadOnlyProperty / ReadWriteProperty for the same thing with an interface.
 */

// Version 1: plain class with operator functions
class Trimmed {
    private var value = ""
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        value = newValue.trim()
    }
}

// Version 2: the interface way, with a parameter
class Clamped(private val range: IntRange, initial: Int) : ReadWriteProperty<Any?, Int> {
    private var value = initial.coerceIn(range)
    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.value = value.coerceIn(range)
        log("  ${property.name} set to ${this.value}")
    }
}

class SignUpForm {
    var name: String by Trimmed()
    var age: Int by Clamped(13..120, initial = 18)
}

fun main() {
    section("1. operator getValue / setValue")
    val form = SignUpForm()
    form.name = "   Sara Ahmed   "
    log("name = '${form.name}'")

    section("2. ReadWriteProperty with parameters")
    form.age = 7
    form.age = 30
    form.age = 400
    log("age = ${form.age}")
    note("The rule lives in ONE reusable delegate, not copied into every setter.")
}

package m12_delegation.exercises.solutions

import m12_delegation.exercises.Inventory
import m12_delegation.exercises.M12Factory
import m12_delegation.exercises.Profile
import m12_delegation.exercises.runChecks
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Class delegation: every Inventory member not overridden is forwarded to `inner`
class AuditingInventory(
    private val inner: Inventory,
    private val log: MutableList<String>,
) : Inventory by inner {
    override fun reserve(item: String, qty: Int): Boolean {
        val ok = inner.reserve(item, qty)
        log += "reserve $item x$qty: $ok"
        return ok
    }
}

// Property delegate: normalizes on every write
class NormalizedEmail : ReadWriteProperty<Any?, String> {
    private var stored = ""
    override fun getValue(thisRef: Any?, property: KProperty<*>) = stored
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        stored = value.trim().lowercase()
    }
}

class SolutionProfile : Profile {
    override var email: String by NormalizedEmail()

    private val changes = mutableListOf<String>()
    override val nicknameChanges: List<String> get() = changes.toList()

    override var nickname: String by Delegates.observable("") { _, old, new ->
        changes += "$old->$new"
    }
}

object FactorySolution : M12Factory {
    override fun auditing(inner: Inventory): Pair<Inventory, List<String>> {
        val log = mutableListOf<String>()
        return AuditingInventory(inner, log) to log
    }

    override fun profile(): Profile = SolutionProfile()
}

fun main() = runChecks(FactorySolution)

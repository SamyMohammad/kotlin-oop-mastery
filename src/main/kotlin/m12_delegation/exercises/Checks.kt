package m12_delegation.exercises

import common.check
import common.runCheckSuite

interface Inventory {
    fun stockOf(item: String): Int
    fun reserve(item: String, qty: Int): Boolean
}

/** Given in-memory inventory. */
class MemoryInventory(private val stock: MutableMap<String, Int>) : Inventory {
    override fun stockOf(item: String) = stock[item] ?: 0
    override fun reserve(item: String, qty: Int): Boolean {
        val have = stockOf(item); if (qty <= 0 || have < qty) return false
        stock[item] = have - qty; return true
    }
}

interface Profile {
    /** Always stored trimmed and lowercased (custom delegate). */
    var email: String
    /** Every change recorded as "old->new" (observable). Initial "". */
    var nickname: String
    val nicknameChanges: List<String>
}

interface M12Factory {
    /** Inventory using class delegation `by` to the given inventory; overrides ONLY reserve to also log
     *  "reserve <item> x<qty>: <true|false>" into the returned list. */
    fun auditing(inner: Inventory): Pair<Inventory, List<String>>
    fun profile(): Profile
}

fun runChecks(f: M12Factory) = runCheckSuite("m12 checks") {
    check("delegates stockOf") { f.auditing(MemoryInventory(mutableMapOf("pen" to 3))).first.stockOf("pen") == 3 }
    check("logs reserve") {
        val (inv, log) = f.auditing(MemoryInventory(mutableMapOf("pen" to 3)))
        inv.reserve("pen", 2); inv.reserve("pen", 2)
        log == listOf("reserve pen x2: true", "reserve pen x2: false")
    }
    check("email normalized") { f.profile().apply { email = "  Sara@Mail.COM " }.email == "sara@mail.com" }
    check("nickname changes") {
        val p = f.profile(); p.nickname = "s"; p.nickname = "sara"
        p.nicknameChanges == listOf("->s", "s->sara")
    }
}

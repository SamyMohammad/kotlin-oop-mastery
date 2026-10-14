package m12_delegation.exercises

/*
 * Module 12 exercises.
 *
 * Tip for auditing():
 *   class AuditingInventory(inner: Inventory, val log: MutableList<String>) : Inventory by inner {
 *       override fun reserve(item: String, qty: Int): Boolean { ... }   // stockOf is delegated for free
 *   }
 *
 * Tip for profile():
 *   - write a small delegate class with getValue/setValue (or ReadWriteProperty) for email
 *   - use Delegates.observable("") { _, old, new -> ... } for nickname
 */

object MyFactory : M12Factory {
    /** Return Pair(yourAuditingInventory, theSameLogListItWritesTo). */
    override fun auditing(inner: Inventory): Pair<Inventory, List<String>> = TODO()

    override fun profile(): Profile = TODO()
}

fun main() = runChecks(MyFactory)

package m13_generics.shop

/*
 * Shop snapshot for module 13: one generic repository for every entity type.
 * The bound `T : Entity` guarantees every stored item has an id.
 */
interface Entity {
    val id: Int
}

data class Product(override val id: Int, val name: String) : Entity
data class Customer(override val id: Int, val email: String) : Entity

interface Repository<T : Entity> {
    fun save(item: T)
    fun byId(id: Int): T?
    fun all(): List<T>
}

class InMemoryRepository<T : Entity> : Repository<T> {
    private val items = linkedMapOf<Int, T>() // keeps insertion order

    override fun save(item: T) {
        items[item.id] = item
    }

    override fun byId(id: Int): T? = items[id]

    override fun all(): List<T> = items.values.toList()
}

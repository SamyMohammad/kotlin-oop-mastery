package m05_interfaces.exercises

/*
 * Module 05 exercises. Interfaces from Checks.kt: Named, Auditable, Validator.
 *
 * Tip for user(): one class `ShopUser(name) : Named, Auditable`. Both interfaces have
 * a default greet(), so the compiler forces you to override it and pick with
 * super<Named>.greet() / super<Auditable>.greet(). Return Pair(u, u) - the same object twice.
 */

object MyFactory : M05Factory {
    /** One object implementing both interfaces, returned twice in the Pair. */
    override fun user(name: String): Pair<Named, Auditable> = TODO()

    /** Use SAM conversion: Validator { input -> ... } */
    override fun egyptPhoneValidator(): Validator = TODO()

    /** A Validator that is valid only when every validator in the list is valid. */
    override fun allOf(validators: List<Validator>): Validator = TODO()
}

fun main() = runChecks(MyFactory)

package m05_interfaces.exercises.solutions

import m05_interfaces.exercises.Auditable
import m05_interfaces.exercises.M05Factory
import m05_interfaces.exercises.Named
import m05_interfaces.exercises.Validator
import m05_interfaces.exercises.runChecks

class ShopUser(name: String) : Named, Auditable {
    override val displayName: String = name

    override fun auditTag(): String = "user:$displayName"

    // Both interfaces bring a default greet(): we must override and choose explicitly
    override fun greet(): String = super<Named>.greet() + " / " + super<Auditable>.greet()
}

object FactorySolution : M05Factory {
    override fun user(name: String): Pair<Named, Auditable> {
        val u = ShopUser(name)
        return u to u
    }

    override fun egyptPhoneValidator(): Validator =
        Validator { it.length == 11 && it.startsWith("01") && it.all(Char::isDigit) }

    override fun allOf(validators: List<Validator>): Validator =
        Validator { input -> validators.all { it.isValid(input) } }
}

fun main() = runChecks(FactorySolution)

package m05_interfaces.exercises

import common.check
import common.runCheckSuite

interface Named { val displayName: String; fun greet(): String = "Hi, $displayName" }
interface Auditable { fun greet(): String = "audited"; fun auditTag(): String }

fun interface Validator { fun isValid(input: String): Boolean }

interface M05Factory {
    /** A class implementing BOTH Named and Auditable. displayName = name; auditTag = "user:<name>";
     *  greet() must return Named's greeting + " / " + Auditable's greeting. */
    fun user(name: String): Pair<Named, Auditable>
    /** Validator via SAM lambda: true when input has exactly 11 digits and starts with "01". */
    fun egyptPhoneValidator(): Validator
    /** Combine validators: valid only when all are valid. Empty list -> always valid. */
    fun allOf(validators: List<Validator>): Validator
}

fun runChecks(f: M05Factory) = runCheckSuite("m05 checks") {
    check("one object, two interfaces") { val (a, b) = f.user("Sara"); a === b }
    check("displayName") { f.user("Sara").first.displayName == "Sara" }
    check("diamond resolved") { f.user("Sara").first.greet() == "Hi, Sara / audited" }
    check("audit tag") { f.user("Sara").second.auditTag() == "user:Sara" }
    check("phone valid") { f.egyptPhoneValidator().isValid("01012345678") }
    check("phone too short") { !f.egyptPhoneValidator().isValid("0101234567") }
    check("phone wrong prefix") { !f.egyptPhoneValidator().isValid("02012345678") }
    check("allOf empty") { f.allOf(emptyList()).isValid("anything") }
    check("allOf combines") {
        val v = f.allOf(listOf(Validator { it.isNotEmpty() }, Validator { it.length < 3 }))
        v.isValid("ab") && !v.isValid("abcd") && !v.isValid("")
    }
}

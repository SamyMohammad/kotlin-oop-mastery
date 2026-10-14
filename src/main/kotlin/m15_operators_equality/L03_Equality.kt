package m15_operators_equality

import common.log
import common.note
import common.section

/*
 * L03 - Equality contracts
 *
 *  a == b    -> a?.equals(b) ?: (b === null)     structural, null-safe
 *  a === b   -> same object in memory             referential
 *
 * Contract:
 *  1. equals is reflexive, symmetric, transitive, consistent, and x.equals(null) == false
 *  2. a == b  ==>  a.hashCode() == b.hashCode()     (break it and HashSet/HashMap misbehave)
 *  3. compareTo should agree with equals: compareTo == 0 <=> equals (otherwise sorted sets surprise you)
 */

class GoodSku(val code: String) {
    override fun equals(other: Any?) = other is GoodSku && other.code == code
    override fun hashCode() = code.hashCode()
}

class BrokenSku(val code: String) {
    override fun equals(other: Any?) = other is BrokenSku && other.code == code
    // hashCode NOT overridden: identity hash, different for equal objects
}

class Score(val name: String, val points: Int) : Comparable<Score> {
    override fun compareTo(other: Score) = points.compareTo(other.points) // ignores name!
    override fun equals(other: Any?) = other is Score && other.name == name && other.points == points
    override fun hashCode() = 31 * name.hashCode() + points
}

fun main() {
    section("1. == vs ===")
    val a = GoodSku("KS-1")
    val b = GoodSku("KS-1")
    log("a == b  -> ${a == b}")
    log("a === b -> ${a === b}")
    val nothing: GoodSku? = null
    log("null == a -> ${nothing == a}   (no NullPointerException)")

    section("2. Breaking the hashCode contract")
    val good = hashSetOf(GoodSku("X"), GoodSku("X"))
    val broken = hashSetOf(BrokenSku("X"), BrokenSku("X"))
    log("good set size   = ${good.size}")
    log("broken set size = ${broken.size}")
    log("broken contains BrokenSku(X)? ${broken.contains(BrokenSku("X"))}")
    note("Equal objects landed in different hash buckets, so the set treats them as different.")

    section("3. compareTo inconsistent with equals")
    val sorted = sortedSetOf(Score("Sara", 10), Score("Omar", 10), Score("Mona", 7))
    log("TreeSet keeps ${sorted.size} scores: ${sorted.map { it.name }}")
    note("Sara and Omar compare as 0, so the TreeSet thinks they are the same element.")
}

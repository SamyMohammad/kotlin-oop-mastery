package m06_polymorphism.shop

/*
 * Shop snapshot for module 06: one interface, three notifiers.
 * The order service calls send() without knowing which channel it talks to.
 */
interface Notifier {
    fun send(to: String, msg: String): String
}

open class SmsNotifier : Notifier {
    override fun send(to: String, msg: String) = "SMS to $to: $msg"
}

class EmailNotifier : Notifier {
    override fun send(to: String, msg: String) = "EMAIL to $to: [Kotlin Shop] $msg"
}

class PushNotifier : Notifier {
    override fun send(to: String, msg: String) = "PUSH to device of $to: $msg"
}

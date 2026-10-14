# Kotlin OOP Cheatsheet

ملخص صفحة واحدة لكل الكورس. كل جزء فيه مثال صغير وأهم القواعد ولينك لملف الشرح الكامل.

## الدرس 00: الصورة الكاملة

```kotlin
class Cart {
    private val lines = mutableListOf<Pair<String, Long>>()
    fun add(name: String, price: Long) { require(price >= 0); lines += name to price }
}
fun fail(msg: String): Nothing = throw IllegalStateException(msg)
```

- الـ object حالة وسلوك وهوية، والقواعد مكانها جواه.
- الـ `Any` أب الكل، والـ `Unit` قيمة حقيقية، والـ `Nothing` معناها مابترجعش.
- ملف الشرح: [00_oop_mental_model.md](00_oop_mental_model.md)

## الدرس 01: الكلاسات والـ objects

```kotlin
class Product(val id: Int, val name: String, priceCents: Long) {
    init { require(priceCents >= 0) }
    var stock = 0
        set(value) { if (value >= 0) field = value }
    val slug by lazy { name.lowercase() }
}
```

- الـ `val` أو `var` في الـ constructor بتعمل property، ومن غيرهم parameter بس.
- الترتيب: الـ properties والـ init blocks من فوق لتحت، والـ secondary في الآخر.
- جوه الـ setter استخدم `field`، والـ `lateinit` لـ var جاية من برّه، والـ `lazy` لـ val محسوبة.
- ملف الشرح: [01_classes_and_objects.md](01_classes_and_objects.md)

## الدرس 02: الـ encapsulation

```kotlin
class Cart {
    private val _items = mutableListOf<CartLine>()
    val items: List<CartLine> get() = _items.toList()
    var count = 0
        private set
}
```

- الهدف حماية الـ invariants، والـ `private` مجرد أداة.
- النوع `List` للقراية بس مش immutable، فرجّع نسخة.
- في الـ ViewModel: النسخة `private` mutable والمعروضة `asStateFlow()`.
- ملف الشرح: [02_encapsulation.md](02_encapsulation.md)

## الدرس 03: الوراثة

```kotlin
open class Payment(val amount: Long) { open fun fee() = 0L }
class CardPayment(amount: Long) : Payment(amount) {
    override fun fee() = amount * 2 / 100
}
```

- الكلاسات final بشكل افتراضي، فلازم `open`.
- الأب بيتبني الأول بالكامل، وماتناديش open function من الـ constructor.
- لو عملت override لـ `equals` اعمل `hashCode` معاها.
- ملف الشرح: [03_inheritance.md](03_inheritance.md)

## الدرس 04: الـ abstract classes

```kotlin
abstract class CheckoutStep {
    fun run() { if (validate()) execute() }
    protected abstract fun validate(): Boolean
    protected abstract fun execute()
}
```

- مينفعش تعمل منه object، وبيجبر الأبناء على الأجزاء الناقصة.
- الـ template method: الأب يملك الترتيب والأبناء يملكوا التفاصيل.
- ملف الشرح: [04_abstract_classes.md](04_abstract_classes.md)

## الدرس 05: الـ interfaces

```kotlin
interface ShippingMethod {
    val name: String
    fun costCents(grams: Int): Long
    fun eta(): String = "3-5 days"
}
fun interface PriceRule { fun apply(cents: Long): Long }
```

- عقد من غير حالة، والكلاس ينفذ أي عدد منه.
- الـ diamond بيتحل بـ `super<Name>.method()`.
- الـ `fun interface` بيقبل lambda مكانه.
- ملف الشرح: [05_interfaces.md](05_interfaces.md)

## الدرس 06: الـ polymorphism

```kotlin
val notifiers: List<Notifier> = listOf(Sms(), Email())
for (n in notifiers) n.send("hi")          // dynamic dispatch
when (event) { is ItemAdded -> event.qty }  // smart cast
```

- الـ members بتتحدد بالنوع الحقيقي، والـ extensions والـ overloads بالنوع المكتوب.
- الـ `as?` آمن، والـ `as` بيرمي exception.
- ملف الشرح: [06_polymorphism.md](06_polymorphism.md)

## الدرس 07: الـ data classes

```kotlin
data class CartUiState(val items: List<String> = emptyList(), val isLoading: Boolean = false)
val next = state.copy(isLoading = true)
val (items, loading) = next
```

- بتولّد `equals` و `hashCode` و `toString` و `copy` و `componentN`.
- الـ `copy` سطحية، والـ properties اللي في الجسم مش داخلة في أي حاجة.
- ملف الشرح: [07_data_classes.md](07_data_classes.md)

## الدرس 08: الـ sealed والـ enums

```kotlin
sealed interface UiState {
    data object Loading : UiState
    data class Success(val items: List<String>) : UiState
}
fun render(s: UiState) = when (s) { UiState.Loading -> "..."; is UiState.Success -> "${s.items}" }
```

- الـ enum: objects ثابتة بنفس الشكل. الـ sealed: أنواع ثابتة كل واحد بداتاه.
- الـ `when` الشاملة من غير `else` بتخلي الـ compiler ينبهك لأي حالة جديدة.
- ملف الشرح: [08_sealed_and_enums.md](08_sealed_and_enums.md)

## الدرس 09: الـ object والـ companion

```kotlin
object AppConfig { var darkMode = false }
class Coupon private constructor(val code: String) {
    companion object { fun parse(raw: String): Coupon? = ... }
}
```

- الـ `object` singleton بيتعمل lazy وآمن مع الـ threads.
- الـ companion object حقيقي: ينفذ interfaces ويتباصى كقيمة.
- كلمة `Modifier` في أول السلسلة هي الـ companion.
- ملف الشرح: [09_object_and_companion.md](09_object_and_companion.md)

## الدرس 10: الـ value classes

```kotlin
@JvmInline
value class ProductId(val value: Int)
fun add(id: ProductId, qty: Quantity)   // التبديل مش بيعمل compile
```

- أمان أنواع بتكلفة صفر في أغلب الأحوال.
- بيتعمل boxing لو nullable أو generic أو interface.
- الـ `Dp` و `Color` و `TextUnit` value classes.
- ملف الشرح: [10_value_classes.md](10_value_classes.md)

## الدرس 11: الـ nested والـ inner والـ anonymous

```kotlin
class Store { class Address; inner class Checkout }
val cmp = object : Comparator<Item> { override fun compare(a: Item, b: Item) = ... }
```

- الـ nested هو الافتراضي ومالوش reference للخارجي.
- الـ inner شايل reference للخارجي، وده ممكن يعمل memory leak.
- ملف الشرح: [11_nested_inner_anonymous.md](11_nested_inner_anonymous.md)

## الدرس 12: الـ delegation

```kotlin
class LoggingCart(inner: CartApi) : CartApi by inner { override fun add(...) { ... } }
var nickname by Delegates.observable("") { _, old, new -> }
var qty by remember { mutableStateOf(1) }
```

- الـ `by` على interface بيولّد forwarding لكل الـ functions.
- الـ delegate مش بينادي الـ override بتاعك.
- أي object فيه `getValue` و `setValue` ينفع يبقى property delegate.
- ملف الشرح: [12_delegation.md](12_delegation.md)

## الدرس 13: الـ generics والـ variance

```kotlin
interface Source<out T> { fun next(): T }
interface Sink<in T> { fun put(item: T) }
inline fun <reified T> List<Any>.only(): List<T> = filter { it is T }.map { it as T }
```

- القاعدة: Producer out و Consumer in.
- الـ `MutableList` invariant و `List` covariant.
- الـ `reified` محتاج `inline`.
- ملف الشرح: [13_generics_and_variance.md](13_generics_and_variance.md)

## الدرس 14: الـ extensions والـ receivers

```kotlin
fun Long.toEgp() = "EGP %d.%02d".format(this / 100, this % 100)
fun receipt(block: Receipt.() -> Unit) = Receipt().apply(block).render()
```

- الـ extension مجرد static function، والـ member بيكسب دايمًا.
- الـ lambda with receiver أساس الـ DSLs.
- الـ `Modifier.weight` متاحة جوه `Row` بس لأنها member extension.
- ملف الشرح: [14_extensions_and_receivers.md](14_extensions_and_receivers.md)

## الدرس 15: الـ operators والمساواة

```kotlin
value class Price(val cents: Long) : Comparable<Price> {
    operator fun plus(o: Price) = Price(cents + o.cents)
    override fun compareTo(other: Price) = cents.compareTo(other.cents)
}
```

- كل operator ليه function باسم ثابت عليها `operator`.
- الـ `==` بتنادي `equals`، والـ `===` بتقارن الهوية.
- الـ `equals` و `hashCode` و `compareTo` لازم يتفقوا.
- ملف الشرح: [15_operators_and_equality.md](15_operators_and_equality.md)

## الدرس 16: الـ composition

```kotlin
class Logging(private val inner: Sender) : Sender {
    override fun send(msg: String) = "[log] " + inner.send(msg)
}
Retrying(Logging(Sms()), times = 3)
```

- السؤال "عنده إيه" غالبًا أصح من "هو نوع من إيه".
- الـ slots في `Scaffold` و `Button` بدل الوراثة.
- ملف الشرح: [16_composition_over_inheritance.md](16_composition_over_inheritance.md)

## الدرس 17: الـ SOLID

```kotlin
class CheckoutService(
    private val store: OrderStore,        // D
    private val policy: DiscountPolicy,   // O
)
```

- الحرف S سبب واحد للتغيير، والحرف O إضافة بدل تعديل، والحرف L الابن مايكسرش وعد الأب.
- الحرف I interfaces صغيرة على شكل أدوار، والحرف D اعتمد على abstractions واستلمها من برّه.
- ملف الشرح: [17_solid.md](17_solid.md)

## الدرس 18: الـ design patterns

```kotlin
val pay = PaymentStrategy { "cash $it" }          // Strategy
TaxDecorator(DiscountDecorator(BasePrice(), 10))  // Decorator
sealed class OrderState { ... }                   // State
```

- الـ Builder غالبًا named args، والـ Singleton كلمة `object`.
- الـ Modifier chain هي Decorator، والـ `StateFlow` هو Observer.
- ملف الشرح: [18_design_patterns.md](18_design_patterns.md)

## جداول القرار

### الاختيار بين open و abstract و interface

| السؤال | الـ open class | الـ abstract class | الـ interface |
|---|---|---|---|
| ينفع تعمل منه object؟ | آه | لأ | لأ |
| بيشيل حالة؟ | آه | آه | لأ |
| بيجبر الأبناء؟ | لأ | آه | آه |
| كام واحد للكلاس؟ | واحد | واحد | أي عدد |

### الاختيار بين enum و sealed

| السؤال | الـ enum | الـ sealed |
|---|---|---|
| كل حالة ليها داتا مختلفة؟ | لأ | آه |
| نسخة واحدة لكل حالة؟ | آه | لأ بالضرورة |
| تلف على كل القيم؟ | آه | لأ |

### الاختيار بين data و value و class عادي

| السؤال | الـ data class | الـ value class | الـ class عادي |
|---|---|---|---|
| المساواة | بالداتا | بالقيمة | بالهوية |
| عدد الـ properties | أي عدد | واحدة | أي عدد |
| التكلفة وقت التشغيل | object | غالبًا صفر | object |

### الاختيار بين الوراثة والـ composition والـ delegation

| السؤال | الوراثة | الـ composition | الـ delegation |
|---|---|---|---|
| العلاقة | نوع من | عنده | عنده مع forwarding تلقائي |
| بيتغير وقت التشغيل؟ | لأ | آه | آه |
| محتاج open؟ | آه | لأ | لأ |
| الأصل بينادي الـ override بتاعك؟ | آه | لأ | لأ |

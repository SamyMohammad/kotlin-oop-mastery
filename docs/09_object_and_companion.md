# 09 - Object and Companion Object

## المشكلة الأول

الشوب محتاج حاجات **مفيش منها غير نسخة واحدة** في التطبيق كله: إعدادات التطبيق، الـ logger، عدّاد أرقام الأوردرات. لو عملتها كلاس عادي:

```kotlin
class AppConfig { var darkMode = false }

val a = AppConfig()
a.darkMode = true
val b = AppConfig()
println(b.darkMode)   // false! نسخة تانية خالص
```

كل حد عمل نسخة شايف إعدادات مختلفة.

ومشكلة تانية: عايز تعمل `Product` بس بعد ما تتأكد من الداتا، وترجّع null لو غلط بدل ما ترمي exception. الـ constructor مينفعش يرجّع null. ولو جاي من Java، أول حاجة هتدوّر عليها هي `static`... وهتلاقي إن Kotlin مافيهاش `static` أصلًا.

الحل في Kotlin حاجتين: `object` للـ singletons، و `companion object` للحاجات المربوطة بالكلاس نفسه مش بـ object معين منه.

## الـ concept بالتفصيل

### الـ object declaration

```kotlin
object AppConfig {
    val currency = "EGP"
    var darkMode = false
}

AppConfig.darkMode = true
```

السطر ده بيعمل حاجتين مع بعض: بيعرّف كلاس، وبيعمل النسخة الوحيدة منه.

خصائصه:

- بيتعمل **lazy**: أول مرة حد يلمسه، مش وقت تشغيل البرنامج.
- إنشاؤه آمن مع الـ threads، لأن الـ JVM بيضمن إن تهيئة الكلاس بتحصل مرة واحدة.
- ينفع يكون فيه properties و functions و `init`.
- ينفع يورث من كلاس وينفذ interfaces، فينفع تباصيه لأي function مستنية النوع ده.
- مالوش constructor، لأنك مش انت اللي بتعمله.

### الـ companion object

```kotlin
class Order private constructor(val id: Int, val total: Long) {
    companion object {
        private var lastId = 0
        fun create(total: Long) = Order(++lastId, total)
    }
}

Order.create(500)
```

الـ companion هو object موجود جوه الكلاس ومربوط بيه. بتنادي الـ members بتاعته من اسم الكلاس مباشرة، وده شبه `static` في Java بالظبط في الشكل.

بس فيه فروق مهمة عن `static`:

| السؤال | الـ companion في Kotlin | الـ static في Java |
|---|---|---|
| هو object حقيقي؟ | آه | لأ |
| ينفع ينفذ interface؟ | آه | لأ |
| ينفع تباصيه كقيمة؟ | آه | لأ |
| ينفع تعمل له extension؟ | آه | لأ |
| بيشوف الـ private بتاع الكلاس؟ | آه | آه |

### الـ companion بينفذ interface

```kotlin
interface JsonParser<T> { fun fromJson(json: String): T }

class Order private constructor(...) {
    companion object : JsonParser<Order> {
        override fun fromJson(json: String): Order = ...
    }
}

fun <T> load(parser: JsonParser<T>, json: String): T = parser.fromJson(json)
load(Order, "id=42;total=900")
```

لما تكتب اسم الكلاس `Order` كقيمة، Kotlin بيفهم إنك تقصد الـ companion بتاعه. فالكلاس نفسه بقى "يعرف يعمل parse لنفسه"، وتقدر تباصيه لأي كود عام.

### الـ companion باسم

الاسم الافتراضي هو `Companion`، وتقدر تسميه:

```kotlin
class Invoice {
    companion object Numbering { fun next() = "INV-001" }
}

Invoice.next()
Invoice.Numbering.next()
```

### الـ factory بـ private constructor

ده من أهم استخدامات الـ companion:

```kotlin
class Product private constructor(val sku: String, val name: String, val priceCents: Long) {
    companion object {
        fun create(sku: String, name: String, priceCents: Long): Product? {
            if (!sku.startsWith("KS-") || priceCents < 0) return null
            return Product(sku, name, priceCents)
        }
    }
}
```

المميزات اللي الـ factory بيديهالك والـ constructor لأ:

- يرجّع null أو `Result` بدل ما يرمي exception.
- اسم واضح زي `fromJson` أو `sample` بدل اسم الكلاس بس.
- يعد النسخ أو يعمل cache ويرجّع نسخة موجودة.
- يرجّع ابن من الكلاس حسب الداتا.

والـ `private constructor` بيضمن إن مفيش طريق تاني لعمل `Product` غير الـ factory.

### الـ const val والـ val

```kotlin
companion object {
    const val MAX_CART_ITEMS = 50
    val startedAt = "boot-" + (40 + 2)
}
```

- الـ `const val` قيمتها معروفة وقت الـ compile، والـ compiler بيحطها مكان كل استخدام. شروطها: primitive أو `String`، ومكانها top-level أو جوه object أو companion.
- الـ `val` العادية بتتحسب وقت ما الـ companion يتجهز.

### الـ @JvmStatic

لو كود Java هينادي الـ function، هيكتب `Order.Companion.create(...)`. لو عايز يكتب `Order.create(...)` زي Kotlin، حط `@JvmStatic` على الـ function. ده مهم بس لو فيه كود Java في المشروع.

### الـ extension على الـ companion

```kotlin
fun Product.Companion.sample(): Product = create("KS-000", "Sample Pen", 100)!!

Product.sample()
```

ده بيخليك تضيف "factories" لكلاس من برّه، مثلًا في ملف الـ tests أو الـ previews، من غير ما تلمس الكلاس نفسه.

### الـ object والـ testing

الـ singleton سهل جدًا، بس ليه عيب: أي كود بيستخدم `AppConfig` مباشرة مربوط بيه، وصعب تبدّله في الـ tests. القاعدة: استخدم `object` للحاجات اللي مالهاش حالة أو حالتها بسيطة جدًا (constants، helpers). أي حاجة ليها سلوك تحب تبدّله، خليها interface ومرّرها من الـ constructor (الدرس 17).

## امشي مع الكود

### [L01_ObjectSingleton.kt](../src/main/kotlin/m09_object_companion/L01_ObjectSingleton.kt)

```
  main started - AppConfig not created yet
  AppConfig init runs (only once)
  currency = EGP
```

الـ `init` اشتغل بعد أول سطر في main، لحظة أول قراية لـ `AppConfig`. ده الـ lazy initialization.

```
  a and b: same
```

مهما كتبت `AppConfig`، هو نفس الـ object.

```
  LOG #1: order processed
  LOG #2: order processed
```

الكلاس `ConsoleLogger` object بينفذ `Logger`، فاتباصى لـ `process` زي أي قيمة. والعداد جواه فضل يزيد لأنها نفس النسخة.

### [L02_CompanionObject.kt](../src/main/kotlin/m09_object_companion/L02_CompanionObject.kt)

```
  Order(id=1, total=500)
  Order(id=2, total=1200)
```

الـ factory بيدي أرقام متسلسلة، والـ `lastId` متخبي جوه الـ companion.

```
  Order(id=42, total=900)
```

باصينا `Order` نفسه لـ `load`، والـ companion اشتغل كـ `JsonParser<Order>`.

### [L03_FactoriesAndConstants.kt](../src/main/kotlin/m09_object_companion/L03_FactoriesAndConstants.kt)

```
  ok       = Product(KS-101, Pen, 500)
  badSku   = null
  badPrice = null
  created so far = 1
```

الداتا الغلط رجّعت null من غير exception، والعداد زاد مرة واحدة بس.

```
  Product(KS-000, Sample Pen, 100)
```

الـ `sample()` extension على الـ companion، اتنادت كأنها جزء من الكلاس.

## في Compose

### ليه كل modifier بيبدأ بكلمة Modifier؟

لما تكتب:

```kotlin
Modifier.padding(16.dp).fillMaxWidth()
```

الكلمة `Modifier` في الأول مش اسم كلاس بتنادي عليه static function. ده الـ companion object بتاع الـ interface اللي اسمه `Modifier`. والـ `Modifier.Companion` نفسه **بينفذ** `Modifier`:

```kotlin
public interface Modifier {
    // ...
    public companion object : Modifier {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial
        override fun any(predicate: (Element) -> Boolean): Boolean = false
        // ...
    }
}
```

يعني الـ companion هو "الـ modifier الفاضي": مابيعملش أي حاجة، وبيبدأ منه أي سلسلة. ده بالظبط L02 لما باصينا `Order` كـ JsonParser. وده كمان سبب الـ convention ده في أي composable:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp)) { /* ... */ }
}
```

القيمة الافتراضية `Modifier` هي الـ companion: modifier فاضي جاهز، فمش محتاج null ولا `?.`.

### الـ MaterialTheme: function و object بنفس الاسم

الـ `MaterialTheme` في Compose حاجتين مع بعض:

- دالة من نوع composable بتحدد الـ theme لكل اللي جواها.
- و object فيه properties بترجّع الـ theme الحالي.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun PriceTag(price: String) {
    Text(
        text = price,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium,
    )
}
```

الـ colorScheme بتاعة `MaterialTheme` قراية من object واحد في التطبيق كله، بس القيمة نفسها بتيجي من أقرب `MaterialTheme(...)` فوق في الشجرة. الـ object هنا مجرد مدخل ثابت للقيمة.

### الـ companion في الـ value classes

الكلاس `Dp` ليه companion فيه قيمة خاصة اسمها `Dp.Companion.Unspecified` بتعني "مفيش قيمة". بتستخدمها كده:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun Badge(size: Dp = Dp.Unspecified) {
    val finalSize = if (size == Dp.Unspecified) 24.dp else size
    Box(Modifier.size(finalSize))
}
```

ده نفس `const` و factories الـ companion، بس على value class (الدرس 10).

### الـ private constructor مع factory في Compose

الكلاس `VectorPainter` والكلاس `LazyListState` الاتنين الـ constructor بتاعهم `internal`. يعني انت مينفعش تعملهم بنفسك، ولازم تستخدم functions زي `rememberVectorPainter` و `rememberLazyListState`. نفس فكرة `Product.create` في L03: الـ factory بيضمن إن الـ object بيتعمل بالطريقة الصح، وفي حالة Compose كمان بيتحفظ مع الـ composition بدل ما يتعمل من الأول مع كل إعادة رسم.

## أخطاء شائعة

1. الغلطة: `object` فيه حالة بتتغير وبيتستخدم من كل حتة في التطبيق.
   الصح: الحالة المشتركة اللي بتتغير مكانها repository أو ViewModel بيتمرر، مش global object.

2. الغلطة: افتكار إن الـ object بيتعمل وقت تشغيل البرنامج.
   الصح: بيتعمل أول ما حد يلمسه.

3. الغلطة: الـ constructor public والـ factory موجود جنبه.
   الصح: لو الـ factory هو الطريق الصح، خلي الـ constructor `private`.

4. الغلطة: `const val` لنوع مش primitive.
   الصح: الـ `const` لـ primitives و `String` بس، والباقي `val` عادية.

5. الغلطة: `Modifier.padding()` جوه composable بدل استخدام الـ parameter `modifier`.
   الصح: ابدأ من الـ `modifier` اللي جالك عشان اللي بينادي يقدر يتحكم في الشكل.

## أسئلة Interview

1. لغة Kotlin مافيهاش `static`، طب بنعمل إيه بداله؟

<details><summary>الإجابة</summary>

بنستخدم top-level functions، أو `object`، أو `companion object`. الـ companion أقربهم لـ static من ناحية الشكل، بس هو object حقيقي.

</details>

2. إمتى الـ object بيتعمل؟ وهل آمن مع الـ threads؟

<details><summary>الإجابة</summary>

بيتعمل أول مرة حد يلمسه، والـ JVM بيضمن إن ده يحصل مرة واحدة بس حتى لو أكتر من thread وصلوا في نفس الوقت.

</details>

3. إيه الفرق بين companion object و static في Java؟

<details><summary>الإجابة</summary>

الـ companion object حقيقي، ينفع ينفذ interfaces ويتباصى كقيمة ويتعمل له extensions. الـ static مجرد members مالهاش object.

</details>

4. ليه ممكن تفضّل factory function عن constructor؟

<details><summary>الإجابة</summary>

لأنه يقدر يرجّع null أو `Result`، وليه اسم واضح، ويقدر يعمل cache أو يرجّع ابن من الكلاس، ومع private constructor بيبقى الطريق الوحيد للإنشاء.

</details>

5. ليه بنكتب `modifier: Modifier = Modifier` في الـ composables؟

<details><summary>الإجابة</summary>

لأن `Modifier` كقيمة هو الـ companion object، وهو بينفذ الـ interface كـ modifier فاضي. فالـ default مش محتاج null ولا شروط.

</details>

## Self-check

- [ ] أقدر أشرح ليه `init` في L01 اتطبع بعد أول سطر في main.
- [ ] أعرف أعمل companion بينفذ interface وأباصيه كقيمة.
- [ ] أعرف أكتب factory بـ private constructor يرجّع null.
- [ ] أعرف الفرق بين `const val` و `val`.
- [ ] أقدر أشرح ليه أي سلسلة modifiers بتبدأ بكلمة `Modifier`.
- [ ] ملف Exercises.kt بيطبع ALL PASS

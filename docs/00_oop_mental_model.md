# 00 - OOP Mental Model

## المشكلة الأول

تخيّل إنك بتبني تطبيق e-commerce صغير. أول حاجة بتعملها إنك تحط المنتجات اللي في السلة في `list` وتكتب functions بتحسب الإجمالي:

```kotlin
val lines = mutableListOf<Pair<String, Long>>()

fun addLine(lines: MutableList<Pair<String, Long>>, name: String, priceCents: Long) {
    lines.add(name to priceCents)
}

fun totalOf(lines: List<Pair<String, Long>>): Long = lines.sumOf { it.second }
```

الكود ده شغّال، بس فيه مشكلة كبيرة مستخبية:

- أي حد في المشروع يقدر يضيف سعر بالسالب.
- أي حد يقدر ينادي `lines.clear()` ويمسح السلة كلها.
- القاعدة "السعر لازم يكون موجب" مش مكتوبة في مكان واحد، فكل function لازم تفتكرها لوحدها.
- لما المشروع يكبر، الداتا بتبقى في حتة والقواعد اللي بتحميها في حتة تانية خالص.

الـ OOP بيحل المشكلة دي بفكرة بسيطة جدًا: **حط الداتا والقواعد اللي بتحميها في وحدة واحدة**. الوحدة دي اسمها object.

## الـ concept بالتفصيل

### يعني إيه object؟

الـ object عنده تلات حاجات:

1. حالة (state): الداتا اللي جواه، زي المنتجات اللي في السلة.
2. سلوك (behaviour): الـ functions اللي بتشتغل على الحالة دي، زي `add` و `total`.
3. هوية (identity): كل object ليه وجود مستقل، يعني سلتين فيهم نفس المنتجات لسه اتنين مختلفين.

الـ class هو القالب، والـ object هو النسخة الحقيقية اللي اتعملت من القالب ده:

```kotlin
class Cart {                      // القالب
    private val lines = mutableListOf<Pair<String, Long>>()
    fun add(name: String, priceCents: Long) { ... }
    fun total(): Long = ...
}

val myCart = Cart()               // object
val yourCart = Cart()             // object تاني مستقل
```

### الأعمدة الأربعة (the four pillars)

كل كتب الـ OOP بتتكلم عن أربع أفكار أساسية. هنا هتشوفهم بسرعة، وكل واحد ليه module كامل بعد كده:

| العمود | الفكرة في جملة | المثال من الشوب | الـ module |
|---|---|---|---|
| الـ Encapsulation | الحالة متخبية والـ methods بس هي اللي تغيّرها | رصيد `GiftCard` مايتغيرش غير بـ `deposit` | الدرس 02 |
| الـ Inheritance | كلاس بياخد كود كلاس تاني ويبني عليه | الكلاس `CardPayment` هو نوع من `Payment` | الدرس 03 |
| الـ Polymorphism | نفس النداء بيتصرف بشكل مختلف حسب الـ object | النداء `describe()` على أي `Payment` | الدرس 06 |
| الـ Abstraction | تعرف الشيء بيعمل إيه من غير ما تعرف بيعمله إزاي | أي `Shipping` بيحسب `costCents` | الدرس 05 |

خلي بالك: الأعمدة دي مش قواعد منفصلة. هي أدوات بتخدم هدف واحد، وهو إن الكود يفضل سهل التغيير لما المشروع يكبر.

### الـ Kotlin بيختلف عن Java في إيه؟

لو جاي من Java، الحاجات دي هتفرق معاك من أول يوم:

- الكلاسات في Kotlin مقفولة (final) بشكل افتراضي. لازم تكتب `open` عشان حد يورث منها.
- مفيش `static`. بدلها فيه `object` و `companion object` (الدرس 09).
- الـ property في Kotlin مش field. هي field مستخبي ومعاه getter و setter بيتعملوا أوتوماتيك (الدرس 01).
- الـ `public` هو الافتراضي، مش package-private زي Java.
- الـ null جزء من الـ type نفسه: `String` غير `String?`.

### شجرة الأنواع في Kotlin

كل type في Kotlin ليه مكان في الشجرة دي:

```
            Any?
           /    \
         Any    null
       /  |  \
    Int String Cart ...
       \  |  /
       Nothing
```

الفكرة هنا:

- الـ `Any` هو أب كل الكلاسات (زي `Object` في Java). أي قيمة مش null هي `Any`.
- الـ `Any?` أوسع من `Any`، لأنه بيقبل null كمان.
- الـ `Unit` هو اللي بترجعه function مش بترجع حاجة. هو object حقيقي، مش كلمة فاضية زي `void`.
- الـ `Nothing` هو type مالوش ولا قيمة. function بترجع `Nothing` معناها إنها عمرها ما بترجع بشكل طبيعي (بترمي exception أو loop لا نهائي).

### ليه `Nothing` مفيد؟

لأنه تحت كل الأنواع. يعني ينفع تحطه في أي مكان، والـ compiler بيفهم منه حاجة مهمة:

```kotlin
fun fail(message: String): Nothing = throw IllegalStateException(message)

val name: String = input ?: fail("name missing")
```

هنا الـ compiler عارف إن `fail` مش هترجع أبدًا، فبيعتبر إن `name` نوعه `String` مش `String?`. ده نفس اللي بيحصل مع `TODO()` و `error()` في الـ standard library.

### الـ object مش بس داتا

غلطة شائعة إنك تفتكر الـ object مجرد "شنطة داتا". الـ object الحقيقي بيتحمّل مسؤولية قواعده. قارن:

```kotlin
// شنطة داتا: أي حد يكسر القاعدة
class CartBag { val lines = mutableListOf<Pair<String, Long>>() }

// object حقيقي: القاعدة جوه
class Cart {
    private val lines = mutableListOf<Pair<String, Long>>()
    fun add(name: String, priceCents: Long) {
        require(priceCents >= 0) { "price must not be negative" }
        lines.add(name to priceCents)
    }
}
```

## امشي مع الكود

### [L01_ProceduralVsObjects.kt](../src/main/kotlin/m00_mental_model/L01_ProceduralVsObjects.kt)

الدرس ده بيكتب نفس السلة مرتين: مرة procedural ومرة object.

```
============================================================
  1. Same result, two styles
============================================================
  procedural total = 3500
  object total     = 3500
   >> Same numbers. The difference shows up when someone makes a mistake.
```

النتيجة واحدة في الحالتين. يعني الـ OOP مش بيخلّي الكود "يشتغل أحسن" في الحالة العادية. الفرق بيظهر لما حد يغلط:

```
============================================================
  2. A bad price
============================================================
  procedural total = 0   <- negative price accepted, money vanished
  procedural lines after someone called clear() = 0
  object refused: price must not be negative
  object total     = 3500, lines = 2
```

النسخة الـ procedural قبلت سعر بالسالب والإجمالي بقى صفر، وبعدها أي حد قدر يمسح الـ list. النسخة الـ object رفضت السعر الغلط والإجمالي فضل سليم، لأن القاعدة جوه الكلاس والـ list نفسها `private`.

### [L02_FourPillarsTour.kt](../src/main/kotlin/m00_mental_model/L02_FourPillarsTour.kt)

جولة سريعة على الأعمدة الأربعة، كل واحد بمثال صغير:

```
  gift card balance = 1500
   >> You cannot write card.balanceCents = 0 - it is private.
```

الإيداع بالسالب اتجاهل لأن `GiftCard` بيحمي رصيده (encapsulation).

```
  card *4242, payment of 2000
  CardPayment is a Payment? true
```

الكلاس `CardPayment` ورث من `Payment` واستخدم `super.describe()` عشان يبني على الكلام القديم (inheritance).

```
  card *1111, payment of 100
  cash, payment of 200
  payment of 300
```

نفس الـ loop ونفس النداء `describe()`، وكل object رد بطريقته (polymorphism).

```
  FlatShipping: 2500 cents for 800g
  WeightShipping: 1800 cents for 800g
```

الكود اللي بينادي يعرف بس الـ interface اللي اسمه `Shipping`، ومايعرفش أي حاجة عن طريقة الحساب جوه (abstraction).

### [L03_TypeHierarchy.kt](../src/main/kotlin/m00_mental_model/L03_TypeHierarchy.kt)

```
  Int is Any? true
  String is Any? true
  Cart is Any? true
```

أي قيمة مش null هي `Any`، حتى الأرقام. في Kotlin مفيش فرق في الكود بين primitive و object، الـ compiler هو اللي بيقرر يستخدم `int` أو `Integer` في الـ bytecode.

```
  maybe is Any? false
  maybe == null? true
```

القيمة null مش `Any`. هي بتدخل بس في `Any?`.

```
  hello() returned: kotlin.Unit
  same object as Unit? same
```

الـ function اللي مش بترجع حاجة بترجع فعلًا object اسمه `Unit`، ومفيش منه غير نسخة واحدة في البرنامج كله.

```
  name = Pen   (type is String, not String?)
  thrown: IllegalStateException(name missing)
```

الـ `fail` نوعها `Nothing`، فالـ compiler سمح إن `name` يبقى `String` عادي. ولما القيمة كانت null فعلًا، الـ exception اترمى.

## في Compose

لما تكتب Compose، أغلب الكود اللي بتكتبه functions مش classes:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun CartSummary(total: Long) {
    Text("Total: $total")
}
```

يعني Compose مش OOP؟ لأ خالص. الفكرة إن الـ UI نفسه بيتوصف بـ functions، بس كل الأدوات اللي تحت منها objects متصممة بعناية:

- الـ annotation اللي اسمها `@Composable` بتخلي الـ compiler plugin يضيف parameter مستخبي من نوع `Composer` لكل function. الـ `Composer` ده object بيحتفظ بالـ state بين كل recomposition والتانية.
- أي modifier بتكتبه زي `Modifier.padding(16.dp)` هو object، و `Modifier` نفسه interface (هتشوفه في الدرس 05).
- الـ `16.dp` قيمة من نوع value class (الدرس 10).
- الـ `remember { mutableStateOf(0) }` بيرجع object بيعمل delegation (الدرس 12).

خليك فاكر الصورة دي: **الـ Composable function بتوصف الشاشة، والـ objects اللي تحتها هي اللي بتشيل الحالة والقواعد**. ده نفس الدرس اللي في L01: الداتا والقواعد مع بعض.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
// الشاشة: function بتوصف
@Composable
fun CartScreen(cart: Cart) {
    Text("Items total: ${cart.total()}")
}

// الحالة والقواعد: object
class Cart {
    private val lines = mutableListOf<Pair<String, Long>>()
    fun add(name: String, priceCents: Long) {
        require(priceCents >= 0)
        lines.add(name to priceCents)
    }
    fun total(): Long = lines.sumOf { it.second }
}
```

في باقي الكورس، كل module فيه قسم Compose بيوريك فين المفهوم ده مستخدم في Compose نفسه.

## أخطاء شائعة

1. الغلطة: إنك تفتكر الـ OOP يعني "حط كل حاجة في class".
   الصح: الـ class مفيد لما يكون عنده حالة وقواعد يحميها. function بسيطة مالهاش state ممكن تفضل top-level عادي في Kotlin.

2. الغلطة: إنك تعمل كل الـ properties `public` وبعدين تكتب القواعد برّه الكلاس.
   الصح: القاعدة مكانها جوه الكلاس، والداتا تبقى `private` أو `private set`.

3. الغلطة: إنك تفتكر إن `Unit` زي `void` بالظبط.
   الصح: الـ `Unit` قيمة حقيقية. ينفع تحطها في `List<Unit>` أو ترجعها من lambda.

4. الغلطة: إنك تكتب `Any` وانت قصدك أي حاجة حتى null.
   الصح: لو null مسموح يبقى `Any?`.

5. الغلطة: إنك تكتب function بترمي exception دايمًا ونوعها `Unit`.
   الصح: خليها `Nothing`، عشان الـ compiler يفهم إن الكود اللي بعدها مش هيتنفذ ويساعدك في الـ null safety.

## أسئلة Interview

1. إيه الفرق بين class و object؟

<details><summary>الإجابة</summary>

الـ class هو التعريف أو القالب. الـ object هو نسخة حقيقية في الذاكرة اتعملت من القالب ده، وليها حالة خاصة بيها وهوية مستقلة.

</details>

2. اشرح الأعمدة الأربعة للـ OOP بمثال واحد لكل واحد.

<details><summary>الإجابة</summary>

الـ encapsulation يعني الرصيد `private` ومايتغيرش غير بـ `deposit`. الـ inheritance يعني `CardPayment` بيورث من `Payment`. الـ polymorphism يعني `describe()` بتتنفذ حسب نوع الـ object الحقيقي. الـ abstraction يعني الكود بيتعامل مع `Shipping` من غير ما يعرف طريقة الحساب.

</details>

3. إيه الفرق بين `Any` و `Any?` و `Nothing`؟

<details><summary>الإجابة</summary>

الـ `Any` أب كل الأنواع اللي مش null. الـ `Any?` أوسع وبيقبل null. الـ `Nothing` تحت كل الأنواع ومالوش قيم، وبيتستخدم لـ function عمرها ما بترجع.

</details>

4. ليه `TODO()` ينفع تحطها في function بترجع `String`؟

<details><summary>الإجابة</summary>

لأن `TODO()` نوعها `Nothing`، و `Nothing` subtype من كل الأنواع، فالـ compiler بيقبلها مكان أي type.

</details>

5. هل `Unit` زي `void` في Java؟

<details><summary>الإجابة</summary>

قريب منه في المعنى، بس `Unit` object حقيقي وليه قيمة واحدة. ده بيخليه ينفع مع الـ generics، يعني نوع الـ lambda اللي مش بترجع حاجة هو نوع عادي زي أي نوع تاني.

</details>

6. إيه أهم فرق بين الكلاسات في Kotlin و Java؟

<details><summary>الإجابة</summary>

في Kotlin الكلاس final بشكل افتراضي، والـ visibility الافتراضية `public`، ومفيش `static`، والـ properties بتولّد getters و setters لوحدها، والـ null جزء من الـ type.

</details>

## Self-check

- [ ] أقدر أشرح ليه النسخة الـ object في L01 حمت الداتا والنسخة الـ procedural لأ.
- [ ] أقدر أدي مثال من الشوب لكل عمود من الأعمدة الأربعة.
- [ ] أقدر أرسم شجرة الأنواع من `Any?` لحد `Nothing`.
- [ ] أعرف ليه `fail()` في L03 نوعها `Nothing`.
- [ ] أعرف إن `@Composable` functions بتوصف الشاشة والـ objects اللي تحتها بتشيل الحالة.
- [ ] ملف Exercises.kt بيطبع ALL PASS

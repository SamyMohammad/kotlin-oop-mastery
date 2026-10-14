# 15 - Operators and Equality

## المشكلة الأول

عندك `Price` في الشوب (الدرس 10). عشان تحسب إجمالي سطر في السلة:

```kotlin
val lineTotal = unitPrice.times(qty).plus(shipping).minus(discount)
if (lineTotal.compareTo(freeShippingLimit) > 0) { ... }
```

الكود صح بس صعب يتقري. لو الأرقام دي كانت `Long` كنت هتكتب `unit * qty + shipping - discount`. ليه الـ types بتاعتك تبقى أسوأ من الأرقام العادية؟

وعلى الجانب التاني، فيه مشكلة أخطر مستخبية في المساواة: كلاس عملتله `equals` بس نسيت `hashCode`، والـ `HashSet` بقى يشيل نفس المنتج مرتين، والـ `contains` بيقول لأ على حاجة موجودة. bugs زي دي بتاخد ساعات عشان تتكشف.

الـ module ده عن الاتنين: إزاي تخلي الـ types بتاعتك تتكتب بشكل طبيعي بالـ operators، وإزاي تكتب `equals` و `hashCode` و `compareTo` من غير ما تكسر العقود اللي المكتبات معتمدة عليها.

## الـ concept بالتفصيل

### الـ operator conventions

الـ Kotlin بتربط كل operator بـ function اسمها ثابت، ولازم الـ function يكون عليها كلمة `operator`:

| اللي بتكتبه | اللي بيتنادى |
|---|---|
| علامة الجمع `a + b` | الـ `a.plus(b)` |
| علامة الطرح `a - b` | الـ `a.minus(b)` |
| علامة الضرب `a * b` | الـ `a.times(b)` |
| السالب `-a` | الـ `a.unaryMinus()` |
| النفي `!a` | الـ `a.not()` |
| المقارنة `a < b` | الـ `a.compareTo(b) < 0` |
| القراية `a[i]` | الـ `a.get(i)` |
| الكتابة `a[i] = v` | الـ `a.set(i, v)` |
| الاحتواء `x in a` | الـ `a.contains(x)` |
| النداء `a(x)` | الـ `a.invoke(x)` |
| المدى `a..b` | الـ `a.rangeTo(b)` |
| الـ destructuring | الـ `component1()` و `component2()` وهكذا |

```kotlin
@JvmInline
value class Price(val cents: Long) : Comparable<Price> {
    operator fun plus(other: Price) = Price(cents + other.cents)
    operator fun times(qty: Int) = Price(cents * qty)
    override operator fun compareTo(other: Price) = cents.compareTo(other.cents)
}

val lineTotal = unitPrice * qty + shipping
if (lineTotal > freeShippingLimit) { ... }
```

### الـ Comparable بيدّيك كتير

لو الكلاس نفذ `Comparable<T>`، بتاخد ببلاش:

- كل علامات المقارنة: `<` و `>` و `<=` و `>=`.
- الـ `sorted()` و `maxOf` و `minOf` و `coerceIn`.
- استخدامه في `sortedSetOf` و `TreeMap`.

### علامة `+=` بطريقتين

```kotlin
var total = Price(0)
total += pen          // مفيش plusAssign، فبتبقى total = total + pen

val basket = Basket()
basket += "Pen"       // فيه plusAssign، فبيعدّل نفس الـ basket
```

القاعدة:

- لو فيه `plusAssign`، بيتنادى، وده بيعدّل الـ object نفسه. مناسب للحاجات اللي بتتجمع زي الـ lists.
- لو مفيش، Kotlin بتحوّلها لـ `a = a + b`، وده محتاج المتغير يكون `var`. مناسب للقيم اللي مابتتغيرش زي `Price`.
- لو الاتنين موجودين والمتغير `var`، الـ compiler بيرفض لأنه مش عارف تقصد إيه.

### الـ invoke

```kotlin
class DiscountRule(private val percent: Int) {
    operator fun invoke(cents: Long): Long = cents * (100 - percent) / 100
}

val tenOff = DiscountRule(10)
tenOff(10_000)
```

ده object تقدر تناديه كأنه function. مفيد للـ use cases في الـ clean architecture: `getProducts()` بدل `getProducts.execute()`.

### المساواة: `==` و `===`

- الـ `a == b` بتتحول لـ `a?.equals(b) ?: (b === null)`. يعني آمنة مع null، ومش هتضرب `NullPointerException`.
- الـ `a === b` معناها "نفس الـ object في الذاكرة". مش بتنادي `equals` خالص.

ملاحظة: علامة `==` نفسها مينفعش تعمل لها overload بشكل مباشر، انت بتعمل override لـ `equals`.

### عقد `equals`

لو كتبت `equals` بنفسك، لازم تلتزم بالقواعد دي:

1. الانعكاس: أي object بيساوي نفسه.
2. التماثل: لو `a == b` يبقى `b == a`.
3. التعدي: لو `a == b` و `b == c` يبقى `a == c`.
4. الثبات: نفس الإجابة كل مرة طول ما الداتا ماتغيرتش.
5. المقارنة مع null دايمًا false.

### عقد `hashCode`

القاعدة الذهبية: **لو اتنين متساويين بـ `equals`، لازم `hashCode` بتاعهم يكون واحد**. العكس مش لازم (اتنين مختلفين ممكن يبقى ليهم نفس الـ hash).

ليه؟ الـ `HashSet` بيقسم العناصر على "أدراج" حسب الـ hash. لما تسأله `contains(x)`، بيروح لدرج `x.hashCode()` بس ويدوّر فيه بـ `equals`. لو الـ hash مختلف، هيدوّر في الدرج الغلط ومش هيلاقيه أبدًا.

### الـ compareTo لازم يتفق مع equals

لو `compareTo` بيقول صفر لاتنين `equals` بتقول عليهم مختلفين، الـ collections المرتبة زي `TreeSet` هتعتبرهم عنصر واحد وهتمسح واحد منهم. الحل: خلي `compareTo` يقارن كل الحاجات اللي `equals` بتقارنها، أو استخدم `Comparator` منفصل للترتيب بدل `Comparable`.

### إمتى ماتعملش overload لـ operator؟

- لما المعنى مش واضح. `user + order`؟ ده معناه إيه؟
- لما الـ operator هيعمل حاجة تقيلة أو ليها side effects مخفية.
- لما الـ function ليها اسم أوضح، زي `merge` أو `applyDiscount`.

الـ operator لازم يكون معناه بديهي لأي حد بيقرا الكود.

## امشي مع الكود

### [L01_OperatorOverloading.kt](../src/main/kotlin/m15_operators_equality/L01_OperatorOverloading.kt)

```
  pen + mug   = EGP 50.00
  mug - pen   = EGP 40.00
  pen * 3     = EGP 15.00
  -pen        = EGP -5.00
```

الحسابات اتكتبت زي الأرقام العادية، وكلها بنوع `Price` آمن.

```
  pen < mug   = true
  max         = EGP 45.00
  sorted      = [EGP 1.00, EGP 5.00, EGP 45.00]
```

الـ `Comparable` وحده ادّانا المقارنة والترتيب و `maxOf`.

```
  total       = EGP 50.00
  basket      = [Pen, Mug]
```

الـ `total` اتعمل له reassign بقيمة جديدة، والـ `basket` اتعدّل مكانه.

### [L02_IndexAndInvoke.kt](../src/main/kotlin/m15_operators_equality/L02_IndexAndInvoke.kt)

```
  cart = {pen=3}
  'pen' in cart = true, 'mug' in cart = false
```

الـ `cart["mug"] = 0` شالت الصنف، لأن الـ `set` بتاعتنا فيها القاعدة دي.

```
  tenOff(10000) = 9000
```

نادينا object كأنه function.

```
  w=1080 h=1920
```

كلاس عادي (مش data) بقى يتعمل له destructuring لأننا كتبنا `component1` و `component2`.

### [L03_Equality.kt](../src/main/kotlin/m15_operators_equality/L03_Equality.kt)

```
  a == b  -> true
  a === b -> false
  null == a -> false   (no NullPointerException)
```

الـ `==` قارنت الداتا، والـ `===` قارنت الهوية، والمقارنة مع null كانت آمنة.

```
  good set size   = 1
  broken set size = 2
  broken contains BrokenSku(X)? false
```

الـ `BrokenSku` عنده `equals` من غير `hashCode`. الـ set شال اتنين متساويين، ومش لاقي عنصر موجود فيه.

```
  TreeSet keeps 2 scores: [Mona, Sara]
```

الـ `Omar` اختفى لأن `compareTo` قال إنه زي `Sara` (نفس النقط) رغم إن `equals` بتقول مختلفين.

## في Compose

### الـ Dp والحسابات

الـ `Dp.plus` و `times` و `div` كلهم operators على الـ value class:

```kotlin
@Stable public inline operator fun plus(other: Dp): Dp = Dp((this.value + other.value) + 0f)
```

فتقدر تكتب:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
val cardPadding = 16.dp
val imageSize = 64.dp

@Composable
fun ProductRow(product: Product) {
    Row(Modifier.padding(cardPadding)) {
        Box(Modifier.size(imageSize))
        Spacer(Modifier.width(cardPadding / 2))
        Column(Modifier.heightIn(min = imageSize + cardPadding * 2)) {
            Text(product.name)
        }
    }
}
```

والـ `Dp` بينفذ `Comparable<Dp>`، فتقدر تقارن: `if (screenWidth > 600.dp)` لتحديد الـ layout للتابلت.

### الـ Offset في السحب

الـ `Offset` value class فيه `plus` و `minus` و `times`. ده بيخلي كود السحب بسيط:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun DraggableSticker() {
    var offset by remember { mutableStateOf(Offset.Zero) }
    Box(
        Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += dragAmount     // offset = offset + dragAmount
                }
            }
            .size(80.dp)
    )
}
```

الـ `offset += dragAmount` هنا هي `+=` اللي من غير `plusAssign`: الـ `Offset` مابيتغيرش، فبيتعمل واحد جديد ويتحط في الـ delegated property، وده اللي بيخلي Compose يعيد الرسم. نفس `total += pen` في L01.

### الـ Modifier.then

```kotlin
public infix fun then(other: Modifier): Modifier
```

الـ `then` مش operator، بس هي `infix`، يعني تقدر تكتبها من غير نقطة وأقواس:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
val base = Modifier.padding(8.dp)
val clickableCard = base then Modifier.clickable { }
```

الـ `infix` مفيد لما الـ function بتربط حاجتين بشكل طبيعي، زي `to` في `"pen" to 3`.

### الـ destructuring في MutableState

الـ `MutableState` فيه `component1` و `component2`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
val (query, setQuery) = remember { mutableStateOf("") }
TextField(value = query, onValueChange = setQuery)
```

الـ `component1()` بترجّع القيمة، والـ `component2()` بترجّع function بتغيّرها. نفس L02 بالظبط.

## أخطاء شائعة

1. الغلطة: override لـ `equals` من غير `hashCode`.
   الصح: الاتنين مع بعض دايمًا، أو data class.

2. الغلطة: `compareTo` بيقارن جزء من الداتا و `equals` بيقارن كلها.
   الصح: خليهم متفقين، أو استخدم `Comparator` منفصل للترتيب.

3. الغلطة: `plus` و `plusAssign` الاتنين على نفس الكلاس والمتغير `var`.
   الصح: الـ compiler هيشتكي. اختار واحد: قيمة ثابتة ليها `plus`، و container ليه `plusAssign`.

4. الغلطة: operators بمعنى مش واضح زي `order + customer`.
   الصح: استخدم function باسم واضح.

5. الغلطة: `===` للمقارنة بين strings أو أرقام.
   الصح: استخدم `==`. الـ `===` للهوية بس.

## أسئلة Interview

1. إزاي تعمل overload لـ operator في Kotlin؟

<details><summary>الإجابة</summary>

تكتب function بالاسم المتفق عليه (زي `plus` أو `get` أو `invoke`) وتحط عليها كلمة `operator`.

</details>

2. إيه الفرق بين `==` و `===`؟

<details><summary>الإجابة</summary>

الـ `==` بتنادي `equals` وآمنة مع null. الـ `===` بتقارن هل دول نفس الـ object في الذاكرة.

</details>

3. إيه اللي بيحصل لو `equals` اتعمل لها override و `hashCode` لأ؟

<details><summary>الإجابة</summary>

اتنين objects متساويين هيبقى ليهم hash مختلف، فالـ `HashSet` و `HashMap` هيعاملوهم كأنهم مختلفين، والـ `contains` و `get` هيفشلوا.

</details>

4. إمتى `a += b` بتنادي `plusAssign` وإمتى `plus`؟

<details><summary>الإجابة</summary>

لو `plusAssign` موجودة بتتنادى. لو مش موجودة والمتغير `var`، بتتحول لـ `a = a.plus(b)`. لو الاتنين موجودين والمتغير `var`، ده ambiguity والـ compiler بيرفض.

</details>

5. ليه `compareTo` لازم يتفق مع `equals`؟

<details><summary>الإجابة</summary>

لأن الـ collections المرتبة زي `TreeSet` بتستخدم `compareTo` عشان تقرر التكرار. لو رجّع صفر لعنصرين مختلفين، واحد منهم هيختفي.

</details>

6. إيه فايدة `operator fun invoke`؟

<details><summary>الإجابة</summary>

بتخلي الـ object يتنادى كأنه function. منتشرة في الـ use cases عشان الكود يتقري زي `getProducts()`.

</details>

## Self-check

- [ ] أعرف جدول الـ operators والـ functions المقابلة.
- [ ] أعرف الفرق بين `plus` و `plusAssign` وإمتى كل واحد.
- [ ] أقدر أكتب `equals` و `hashCode` صح من غير data class.
- [ ] أقدر أشرح ليه `Omar` اختفى في L03.
- [ ] أقدر أشرح `offset += dragAmount` في Compose خطوة بخطوة.
- [ ] ملف Exercises.kt بيطبع ALL PASS

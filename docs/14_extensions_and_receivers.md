# 14 - Extensions and Receivers

## المشكلة الأول

في كل مكان في الشوب بتعرض السعر بنفس الشكل `EGP 12.50`. أول حل بيبقى helper class:

```kotlin
object PriceUtils {
    fun format(cents: Long): String = "EGP %d.%02d".format(cents / 100, cents % 100)
}

PriceUtils.format(product.priceCents)
```

شغال، بس بيتقري بالعكس: الفعل الأول وبعده الحاجة. ولو عندك `StringUtils` و `DateUtils` و `ListUtils`، الكود بيبقى مليان كلاسات utils مالهاش معنى في الـ domain. ومش هتقدر تضيف `format` لـ `Long` نفسه لأنك مش صاحب الكلاس.

ومشكلة تانية: عايز تبني أوردر بشكل مقري:

```kotlin
val b = OrderBuilder()
b.line("Pen", 2, 500)
b.line("Mug", 1, 3000)
b.discountPercent(10)
val text = b.render()
```

الـ `b.` بتتكرر في كل سطر. الـ Kotlin بتحل الاتنين بفكرة واحدة اسمها **receiver**.

## الـ concept بالتفصيل

### الـ extension function

```kotlin
fun Long.toEgp(): String = "EGP %d.%02d".format(this / 100, this % 100)

product.priceCents.toEgp()
```

الـ `Long` قبل النقطة اسمه الـ receiver type، وجوه الـ function الكلمة `this` بتشاور على القيمة اللي اتنادت عليها. شكلها كأنك ضفت function لـ `Long`، بس في الحقيقة هي function عادية static. في الـ bytecode بتبقى كده تقريبًا:

```java
public static String toEgp(long $this) { ... }
```

النتيجة المهمة:

- مش بتقدر توصل للـ `private` أو `protected` بتاع الكلاس، لأنها مش جزء منه.
- بتتحدد من النوع المكتوب مش النوع الحقيقي (الفخ اللي في الدرس 06).
- لو الكلاس فيه member function بنفس الشكل، الـ member بيكسب دايمًا.

### الـ nullable receiver

```kotlin
fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this.trim()

val missing: String? = null
missing.orDash()   // من غير ?.
```

الـ `this` هنا ممكن يكون null، والـ function بتتعامل معاه جواها. ده سبب إن `isNullOrBlank()` و `orEmpty()` في الـ standard library بتتنادى على قيم nullable من غير `?.`.

### الـ extension property

```kotlin
val Product.isExpensive: Boolean
    get() = priceCents > 100_000
```

نفس الفكرة بس property. **مالهاش backing field**، فلازم تكون محسوبة. مينفعش تكتب `val Product.cache = 5`.

### الـ function type with receiver

```kotlin
(Receipt) -> Unit          // lambda عادية: جواها it.addLine(...)
Receipt.() -> Unit         // lambda with receiver: جواها addLine(...)
```

النوع التاني معناه إن الـ lambda هتتنفذ **كأنها جوه** `Receipt`. والـ function اللي بتستقبلها بتحدد الـ receiver:

```kotlin
fun receipt(block: Receipt.() -> Unit): String {
    val r = Receipt()
    r.block()
    return r.render()
}

receipt {
    addLine("Pen x2")
    addLine("Mug x1")
}
```

ده أساس كل DSL في Kotlin: `buildString` و `apply` و Gradle Kotlin DSL و Compose نفسه.

### الـ scope functions

| الـ function | الـ object جواها | بترجّع | استخدام شائع |
|---|---|---|---|
| الـ `let` | الكلمة `it` | نتيجة الـ lambda | تحويل قيمة أو التعامل مع null |
| الـ `run` | الكلمة `this` | نتيجة الـ lambda | حساب قيمة من object |
| الـ `with` | الكلمة `this` | نتيجة الـ lambda | نفس `run` بس مش extension |
| الـ `apply` | الكلمة `this` | الـ object نفسه | تجهيز object |
| الـ `also` | الكلمة `it` | الـ object نفسه | حاجة جانبية زي logging |

الفرق بين `this` و `it` هو الفرق بين lambda with receiver و lambda عادية.

### الـ member extensions والـ scoped DSL

لما تكتب extension **جوه** كلاس، هي بتبقى متاحة بس جوه الـ scope بتاعه:

```kotlin
class RowBuilder {
    fun Cell.weight(w: Int) { ... }
}
```

الـ `Cell.weight` موجودة بس لما يكون فيه `RowBuilder` كـ receiver، يعني جوه block الـ receiver بتاعه `RowBuilder`. برّه الـ block ده، `cell.weight(2)` مش موجودة أصلًا. كده الـ API بيمنعك تستخدم حاجة في مكان مالهاش معنى فيه.

### الـ DslMarker

في الـ DSLs المتداخلة، الـ block الداخلي يقدر يشوف الـ receivers اللي برّه كلهم. يعني جوه `row { }` تقدر بالغلط تنادي `row` تاني من الـ `screen` اللي برّه. الـ annotation اللي بتتعمل بـ `@DslMarker` بتمنع ده: جوه أي block تقدر تنادي functions الـ receiver الأقرب بس، إلا لو كتبت `this@screen` صراحة.

## امشي مع الكود

### [L01_ExtensionFunctions.kt](../src/main/kotlin/m14_extensions_receivers/L01_ExtensionFunctions.kt)

```
  EGP 12.50
  EGP 0.07
```

الـ `toEgp()` بقت كأنها جزء من `Long`.

```
  null    -> -
  blank   -> -
  padded  -> Sara
```

الـ extension على `String?` اتنادت على null من غير `?.`.

```
  member describe: save10
  SAVE10!
```

الـ extension اللي بنفس اسم الـ member اتجاهلت، والـ extension الجديدة اشتغلت.

### [L02_ExtensionProperties.kt](../src/main/kotlin/m14_extensions_receivers/L02_ExtensionProperties.kt)

```
  Pen: expensive=false, in stock
  Laptop: expensive=true, only 3 left
  Mug: expensive=false, out of stock
```

فيه properties محسوبة اتضافت لكلاس الـ `Product` من برّه.

```
  stock value = 7560000
```

ودي extension property على `List<Product>`.

### [L03_LambdaWithReceiver.kt](../src/main/kotlin/m14_extensions_receivers/L03_LambdaWithReceiver.kt)

```
  Pen x2 | Mug x1 | Total 40.00
```

جوه الـ block نادينا `addLine` مباشرة لأن الـ receiver هو `Receipt`.

```
  also sees 3 items
  sizeAfter=2, apply returned same list: same
  upper=[PEN, MUG, CAP]
```

الـ `run` رجّع الحجم، والـ `apply` رجّع نفس الـ list، والـ `let` رجّع list جديدة.

### [L04_ScopedDsl.kt](../src/main/kotlin/m14_extensions_receivers/L04_ScopedDsl.kt)

```
  Image(25%) | Name and price(75%)
  Qty(50%) | Remove(50%)
```

الـ `weight` اتنادت جوه `row { }` بس. والسطر المتعلّق عليه برّه الـ row مش بيعمل compile. وفي الصف التاني، من غير weight، العناصر اتقسمت بالتساوي.

## في Compose

### كل modifier هو extension

```kotlin
public fun Modifier.padding(all: Dp): Modifier
```

الـ `Modifier.padding` extension function على الـ interface `Modifier`. فلما تكتب سلسلة:

```kotlin
Modifier.padding(16.dp).fillMaxWidth().clickable { }
```

كل حلقة هي extension بترجّع `Modifier` جديد، فتقدر تنادي اللي بعدها. وده معناه إنك تقدر تعمل modifiers بتاعتك بنفس الشكل، كأنها جزء من المكتبة:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
fun Modifier.priceTag(onSale: Boolean): Modifier =
    this
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .then(if (onSale) Modifier.background(Color(0xFFFFCDD2)) else Modifier)

@Composable
fun Price(text: String, onSale: Boolean) {
    Text(text, Modifier.priceTag(onSale))
}
```

لاحظ `this` في أول الـ function: ده الـ modifier اللي اتنادت عليه الـ extension، وبنبني عليه عشان مانضيّعش اللي قبله في السلسلة.

### الـ RowScope.weight: scoped extension حقيقي

```kotlin
public interface RowScope {
    public fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier
}

@Composable
public inline fun Row(
    modifier: Modifier = Modifier,
    // ...
    content: @Composable RowScope.() -> Unit,
)
```

ده بالظبط L04:

- الـ `RowScope.weight` member extension جوه interface اسمه `RowScope`.
- الـ `content` بتاع `Row` نوعه lambda with receiver والـ receiver هو `RowScope`، فجوه الـ block تقدر تنادي اللي في `RowScope`.
- النتيجة: `Modifier.weight(1f)` متاحة جوه `Row { }` بس.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun CartLineRow(line: CartLine) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(line.name, Modifier.weight(1f))      // متاحة: احنا جوه RowScope
        Text("x${line.qty}")
        Button(onClick = { }) { Text("Remove") }
    }
}

@Composable
fun Broken() {
    Text("Hi", Modifier.weight(1f))   // error: weight مش موجودة هنا
}
```

ليه Compose اختار كده؟ لأن الـ weight مالوش أي معنى برّه Row أو Column. بدل ما يرمي exception وقت التشغيل، الـ API مش بيسمح بالغلطة أصلًا.

### الـ BoxScope.align

نفس الفكرة بالظبط: الـ `BoxScope.align` متاحة جوه `Box { }` بس، لأن محاذاة عنصر جوه Box مالهاش معنى برّه Box.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ProductImageWithBadge() {
    Box(Modifier.size(120.dp)) {
        Image(painter = painterResource(R.drawable.pen), contentDescription = null)
        Text("-20%", Modifier.align(Alignment.TopEnd))
    }
}
```

### الـ Button والـ Card كمان scoped

الـ `Button` بياخد content الـ receiver بتاعه `RowScope`، يعني المحتوى بتاعه بيترص جنب بعض وتقدر تستخدم `weight` جواه. والـ `Card` بياخد content الـ receiver بتاعه `ColumnScope`، فالمحتوى بيترص تحت بعض. نوع الـ receiver بيقولك الـ layout من غير ما تقرا الـ documentation.

## أخطاء شائعة

1. الغلطة: افتكار إن الـ extension بتغيّر الكلاس الأصلي أو تشوف الـ private.
   الصح: هي function static من برّه، بتشوف الـ public بس.

2. الغلطة: extension بنفس اسم member function وانتظار إنها تتنادى.
   الصح: الـ member بيكسب دايمًا. اختار اسم تاني.

3. الغلطة: custom modifier بيبدأ بـ `Modifier.padding(...)` بدل `this.padding(...)`.
   الصح: ابدأ بـ `this` عشان تحافظ على اللي قبلك في السلسلة.

4. الغلطة: `let` و `run` و `apply` و `also` متداخلين في سطر واحد.
   الصح: اختار واحدة حسب الجدول، والكود يفضل يتقري.

5. الغلطة: DSL متداخل من غير `@DslMarker`.
   الصح: من غيره الـ block الداخلي بيشوف functions الخارجي وده بيعمل bugs غريبة.

## أسئلة Interview

1. إيه اللي بيحصل للـ extension function في الـ bytecode؟

<details><summary>الإجابة</summary>

بتبقى static function بتاخد الـ receiver كأول parameter، في كلاس اسمه على اسم الملف.

</details>

2. لو فيه member function و extension بنفس الشكل، مين بيتنادى؟

<details><summary>الإجابة</summary>

الـ member دايمًا. الـ extension مش بتقدر تعمل override أو shadow لـ member.

</details>

3. إيه الفرق بين lambda عادية بتاخد `T` و lambda with receiver على `T`؟

<details><summary>الإجابة</summary>

الأولى lambda عادية والـ object بيتباصى كـ `it`. التانية lambda with receiver والـ object بيبقى `this`، فتقدر تنادي functions بتاعته مباشرة.

</details>

4. ليه `Modifier.weight` مش متاحة برّه `Row`؟

<details><summary>الإجابة</summary>

لأنها member extension جوه `RowScope`، ومش متاحة غير لما `RowScope` يكون receiver. والـ content بتاع `Row` الـ receiver بتاعه `RowScope`.

</details>

5. إمتى تستخدم `apply` وإمتى `let`؟

<details><summary>الإجابة</summary>

الـ `apply` لتجهيز object وترجيعه نفسه. الـ `let` لتحويل قيمة لحاجة تانية، أو مع `?.` للتعامل مع null.

</details>

6. إيه فايدة `@DslMarker`؟

<details><summary>الإجابة</summary>

بتمنع الـ block الداخلي في DSL متداخل إنه ينادي functions الـ receiver الخارجي ضمنيًا، فبتمنع أخطاء زي إنك تعمل row جوه row بالغلط.

</details>

## Self-check

- [ ] أقدر أكتب extension function و extension property وأشرح شكلهم في الـ bytecode.
- [ ] أعرف ليه الـ member بيكسب الـ extension.
- [ ] أعرف أكتب function بتاخد lambda with receiver وأبني بيها DSL صغير.
- [ ] أعرف أختار الـ scope function الصح من الجدول.
- [ ] أقدر أكتب custom modifier وأشرح ليه بيبدأ بـ `this`.
- [ ] أقدر أشرح ليه `Modifier.weight` شغالة جوه `Row` بس.
- [ ] ملف Exercises.kt بيطبع ALL PASS

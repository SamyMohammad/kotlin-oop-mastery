# 11 - Nested, Inner and Anonymous Classes

## المشكلة الأول

عندك كلاس `Store` للفرع، ومحتاج كلاس صغير للعنوان، وكلاس للكاشير اللي بيشتغل في الفرع ده بالذات، و listener بيتنادى لما العميل يضغط على زرار. كتابة كل واحد من دول في ملف لوحده كلاس top-level بيبعثر الكود، وبيخلي كلاسات مالهاش أي معنى برّه `Store` متاحة للمشروع كله.

وفيه مشكلة أخطر في Android: الـ listener اللي بيتعمل جوه Activity ممكن يمسك فيها، ولما الـ Activity تتقفل، الـ listener لسه عايش في مكان تاني، فالـ Activity كلها (بالصور والـ views) بتفضل في الذاكرة. ده memory leak كلاسيكي.

الـ module ده بيشرح إزاي تكتب كلاسات جوه كلاسات أو جوه functions، وإيه الفرق المهم جدًا بين اللي بيمسك في الـ object الخارجي واللي لأ.

## الـ concept بالتفصيل

### الـ nested class (الافتراضي)

```kotlin
class Store(val name: String) {
    class Address(val city: String)
}

val a = Store.Address("Cairo")
```

الكلاس `Address` موجود جوه `Store` من ناحية التنظيم والاسم بس. **مالوش أي علاقة بأي object من `Store`**:

- مينفعش يقرا `name` لأنه مش عارف أنهي store.
- بتعمله من غير ما يكون عندك store أصلًا.
- ده نفس `static class` في Java.

### الـ inner class

```kotlin
class Store(val name: String) {
    private var visits = 0

    inner class Checkout(val counter: Int) {
        fun open(): String {
            visits++
            return "${this@Store.name} counter $counter"
        }
    }
}

val maadi = Store("Maadi")
val c = maadi.Checkout(1)
```

الـ `inner` معناها إن كل object من `Checkout` شايل reference مخفي للـ `Store` اللي اتعمل منه:

- يقدر يقرا ويغيّر حتى الـ `private` بتاع الـ store.
- لازم يكون عندك store عشان تعمله: `maadi.Checkout(1)`.
- لو فيه تعارض في الأسماء، الصيغة `this@Store` بتشاور على الـ object الخارجي صراحة.

### الفرق بين Kotlin و Java

| الحالة | في Kotlin | في Java |
|---|---|---|
| كلاس جوه كلاس من غير كلمة | زي nested، مالوش reference | زي inner، شايل reference |
| علشان يشيل reference | لازم تكتب `inner` | الافتراضي |
| علشان مايشيلش | الافتراضي | لازم تكتب `static` |

يعني Kotlin عكست الافتراضي، والسبب هو الـ leaks: الأأمن هو الافتراضي، والخطر لازم تطلبه بنفسك.

### الـ memory leak

الـ inner object بيخلّي الـ outer object عايش طول ما هو عايش. تخيّل:

```kotlin
class ProductActivity : ComponentActivity() {
    inner class PriceListener : PriceUpdates.Listener {
        override fun onPrice(p: Long) { /* بيستخدم views الـ Activity */ }
    }
    override fun onCreate(...) {
        PriceUpdates.register(PriceListener())   // object عايش طول عمر التطبيق
    }
}
```

الـ `PriceUpdates` شايل الـ listener، والـ listener شايل الـ Activity. لما المستخدم يلف الشاشة، Android بيعمل Activity جديدة، بس القديمة مش هتتمسح لأن فيه حد ماسك فيها. ونفس المشكلة مع الـ object expressions والـ lambdas اللي بتستخدم حاجة من الـ Activity.

الحل: ألغي التسجيل في الوقت المناسب، أو ماتمسكش في الـ Activity أصلًا. وفي Compose، الحاجات دي بتتعمل بـ `DisposableEffect` اللي فيه مكان مخصوص للتنضيف.

### الـ object expression (الـ anonymous object)

```kotlin
val byPrice = object : Comparator<Item> {
    override fun compare(a: Item, b: Item) = a.priceCents.compareTo(b.priceCents)
}
```

ده بيعمل كلاس من غير اسم ومنه object واحد بس في نفس اللحظة. تقدر:

- تنفذ interface أو تورث من كلاس.
- تنفذ أكتر من interface مع بعض: `object : Discount, Describable { ... }`.
- تحط جواه state زي عداد.

خلي بالك من الفرق بين **object declaration** (الدرس 09، singleton ليه اسم) و **object expression** (ده، object جديد من غير اسم كل مرة الكود يتنفذ).

### نوع الـ anonymous object

```kotlin
val tracker = object {
    var hits = 0
    fun hit() = ++hits
}
tracker.hit()
```

جوه نفس الـ function أو كـ property من نوع `private`، الـ compiler فاكر النوع الحقيقي فتقدر توصل لـ `hits`. لو رجّعته من function عامة، النوع بيبقى `Any` (أو الـ interface اللي بينفذه)، والـ members الزيادة بتختفي.

### الـ object expression ولا lambda؟

لو الـ interface فيه function واحدة (SAM)، الـ lambda أقصر:

```kotlin
val byName = Comparator<Item> { a, b -> a.name.compareTo(b.name) }
```

استخدم `object :` لما تحتاج:

- حالة (state) جوه الـ object.
- أكتر من function.
- أكتر من interface.
- ترجع لنفس الـ object بـ `this` (في الـ lambda، `this` بيشاور على الكلاس اللي برّه).

### الـ local classes والـ capture

ينفع تعرّف كلاس أو function جوه function. وأي حاجة من دول (وكمان الـ lambdas والـ object expressions) تقدر **تمسك** المتغيرات المحلية، حتى لو `var`، وتغيّرها:

```kotlin
fun makeCounter(): () -> Int {
    var count = 0
    return { ++count }
}
```

الـ `count` المفروض يموت لما `makeCounter` تخلص، بس الـ lambda ماسكة فيه فبيفضل عايش. وكل نداء جديد لـ `makeCounter` بيعمل `count` جديد خالص. في Java ده مكانش مسموح غير مع متغيرات "effectively final".

## امشي مع الكود

### [L01_NestedVsInner.kt](../src/main/kotlin/m11_nested_inner_anonymous/L01_NestedVsInner.kt)

```
  Address in Cairo
```

الـ `Address` اتعمل من غير أي store.

```
  Maadi branch counter 1 (visit #1)
  Maadi branch counter 2 (visit #2)
  Maadi branch counter 1 (visit #3)
```

الكاشيرين الاتنين من نفس الفرع، فبيزوّدوا نفس العداد الـ `private` اللي جوه الـ store.

```
  Zamalek branch counter 1 (visit #1)
```

فرع تاني بعداد مستقل. كل inner object مربوط بالـ store بتاعه هو.

### [L02_ObjectExpressions.kt](../src/main/kotlin/m11_nested_inner_anonymous/L02_ObjectExpressions.kt)

```
  [Pen, Mug, Cap]
```

ترتيب بالسعر بـ comparator من غير اسم.

```
  1000 off: 4500 -> 3500
```

فيه object واحد بينفذ interface الخصم و interface الوصف مع بعض.

```
  hits = 2
```

الـ anonymous object شايل عداد خاص بيه، وقدرنا نوصل له لأن النوع محلي.

### [L03_LocalClassesAndCapture.kt](../src/main/kotlin/m11_nested_inner_anonymous/L03_LocalClassesAndCapture.kt)

```
  #1: 500 (running 500)
  #2: 4500 (running 5000)
  #3: 1000 (running 6000)
  TOTAL: 6000
```

الكلاس المحلي `Line` غيّر المتغير `runningTotal` اللي متعرّف في الـ function اللي برّه.

```
  1
  2
  3
  a new counter starts again: 1
```

كل counter ليه `count` خاص بيه، والـ lambda خلّته عايش.

## في Compose

### الـ PreviewParameterProvider

عشان تعمل preview لـ composable بأكتر من داتا، بتستخدم interface اسمه `PreviewParameterProvider`:

```kotlin
public interface PreviewParameterProvider<T> {
    public val values: Sequence<T>
    public val count: Int get() = values.count()
}
```

الـ annotation اللي اسمها `@PreviewParameter` بتاخد **reference لكلاس** مش object، عشان الـ tooling هو اللي بيعمل النسخة. فهنا لازم كلاس ليه اسم، والـ nested class مكانه مثالي:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ProductCard(product: Product) { /* ... */ }

class ProductCardPreviews {
    // nested: مالوش علاقة بأي object خارجي، والـ tooling يقدر يعمله لوحده
    class Products : PreviewParameterProvider<Product> {
        override val values = sequenceOf(
            Product(1, "Pen", 500),
            Product(2, "Very long product name that should wrap nicely", 129_900),
        )
    }
}

@Preview
@Composable
fun ProductCardPreview(
    @PreviewParameter(ProductCardPreviews.Products::class) product: Product,
) {
    ProductCard(product)
}
```

لو كتبت الـ provider كـ `inner class`، الـ tooling مش هيقدر يعمله، لأنه محتاج object من الكلاس الخارجي. ولو كتبته object expression، مش هتقدر تديه للـ annotation أصلًا لأنه مالوش اسم.

### الـ callbacks في Compose: lambdas مش anonymous objects

في نظام الـ Views القديم كنت بتكتب:

```kotlin
button.setOnClickListener(object : View.OnClickListener {
    override fun onClick(v: View) { addToCart() }
})
```

في Compose كل الـ events عبارة عن function types:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun AddToCartButton(onAdd: () -> Unit) {
    Button(onClick = onAdd) { Text("Add to cart") }
}
```

ليه؟ لأن الـ lambda أخف وأوضح، وأغلب الـ callbacks محتاجة function واحدة بس. ده بالظبط القاعدة اللي في L02: لو الـ interface فيه function واحدة، الـ lambda أحسن.

### الـ capture في الـ composables

الـ lambdas في Compose بتمسك المتغيرات زي L03 بالظبط:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun QuantityPicker() {
    var qty by remember { mutableStateOf(1) }
    Row {
        Button(onClick = { qty-- }) { Text("-") }
        Text("$qty")
        Button(onClick = { qty++ }) { Text("+") }
    }
}
```

الـ lambdas اللي في `onClick` ماسكة `qty` وبتغيّره. والفرق عن L03 إن `qty` هنا مش متغير عادي، ده delegated property مربوطة بـ `MutableState` (الدرس 12)، فلما يتغير Compose بيعيد رسم الـ `Text`.

### الـ Modifier.Node كـ nested abstract class

الكلاس `Modifier.Node` اللي شفناه في الدرس 04 هو مثال على nested class جوه interface. الاسم الكامل `Modifier.Node` بيوضح إنه جزء من عالم الـ modifiers، بس مالوش علاقة بأي modifier object معين.

## أخطاء شائعة

1. الغلطة: `inner class` من غير سبب.
   الصح: ابدأ بـ nested (الافتراضي). استخدم `inner` بس لو محتاج حالة الـ object الخارجي فعلًا.

2. الغلطة: listener من نوع inner أو lambda بتمسك في Activity ومتسجّل في حاجة عايشة أطول منها.
   الصح: ألغي التسجيل في الوقت المناسب، أو في Compose استخدم `DisposableEffect`.

3. الغلطة: `object :` لـ interface فيه function واحدة.
   الصح: استخدم lambda.

4. الغلطة: افتكار إن `object : X {}` بيعمل singleton.
   الصح: الـ object expression بيعمل object جديد كل مرة السطر يتنفذ. الـ singleton هو `object Name`.

5. الغلطة: رجوع anonymous object من function عامة ومحاولة الوصول لـ members زيادة.
   الصح: النوع بيبقى `Any`. اعمل interface أو كلاس ليه اسم.

## أسئلة Interview

1. إيه الفرق بين nested class و inner class في Kotlin؟

<details><summary>الإجابة</summary>

الـ nested مالوش reference للـ object الخارجي وده الافتراضي. الـ inner شايل reference ليه، فيقدر يوصل لحالته، وبتعمله من object خارجي.

</details>

2. ليه Kotlin خلّت الافتراضي nested عكس Java؟

<details><summary>الإجابة</summary>

لأن الـ reference المخفي للـ object الخارجي سبب شائع للـ memory leaks. الأأمن بقى الافتراضي، واللي محتاج الـ reference يكتب `inner` صراحة.

</details>

3. إيه الفرق بين object declaration و object expression؟

<details><summary>الإجابة</summary>

الـ object declaration ليه اسم وبيعمل singleton واحد lazy. الـ object expression مالوش اسم وبيعمل object جديد كل مرة الكود يتنفذ.

</details>

4. هل الـ lambda في Kotlin تقدر تغيّر متغير `var` محلي؟

<details><summary>الإجابة</summary>

آه. Kotlin بتحط المتغير في box، فالـ lambda تقدر تقراه وتغيّره، وبيفضل عايش طول ما الـ lambda عايشة.

</details>

5. ليه الـ PreviewParameterProvider لازم يكون كلاس ليه اسم ومش inner؟

<details><summary>الإجابة</summary>

لأن الـ annotation بتاخد reference لكلاس، والـ tooling بيعمل النسخة بنفسه من غير parameters. الـ inner محتاج object خارجي، والـ anonymous مالوش اسم يتحط في الـ annotation.

</details>

## Self-check

- [ ] أقدر أشرح ليه `Address` في L01 مش قادر يقرا `name`.
- [ ] أقدر أشرح سيناريو memory leak بسبب inner class في Android.
- [ ] أعرف أكتب object expression بينفذ اتنين interfaces وفيه state.
- [ ] أعرف إمتى أختار lambda وإمتى `object :`.
- [ ] أقدر أكتب `PreviewParameterProvider` كـ nested class.
- [ ] ملف Exercises.kt بيطبع ALL PASS

# 18 - Design Patterns

## المشكلة الأول

لما المشروع يكبر، هتلاقي نفس المشاكل بتتكرر بأشكال مختلفة:

- عندك أكتر من طريقة دفع، والـ checkout لازم يشتغل معاهم كلهم.
- لازم تختار نوع الـ object حسب داتا جاية من المستخدم.
- عندك object بيتبني على خطوات كتير.
- أكتر من حتة في الـ UI لازم تتبلّغ لما المخزون يتغير.
- الأوردر بيتنقل بين حالات بقواعد، وكل حالة بتسمح بحاجات مختلفة.
- عايز تضيف ضريبة أو خصم للسعر من غير ما تلمس كود الحساب.
- عندك SDK قديم بيتكلم بشكل مختلف عن الـ interfaces بتاعتك.

الـ design patterns هي **حلول معروفة ليها أسماء** للمشاكل دي. الأسماء مهمة لأنها لغة مشتركة: لما تقول لزميلك "اعمله decorator"، هو فاهم الشكل كله في كلمة.

ومهم تعرف حاجة: أغلب الـ patterns اتكتبت أصلًا لـ Java في التسعينات. في Kotlin، كتير منها بيبقى أبسط بكتير، أو مدمج في اللغة نفسها، بفضل الأدوات اللي اتعلمتها في الـ modules اللي فاتت.

## الـ concept بالتفصيل

### الـ Strategy

**مجموعة algorithms ممكن تتبادل ورا interface واحد، والاختيار وقت التشغيل.**

```kotlin
fun interface PaymentStrategy { fun pay(amountCents: Long): String }

class Checkout(private val strategy: PaymentStrategy) {
    fun complete(total: Long) = strategy.pay(total)
}
```

في Kotlin الـ strategy غالبًا `fun interface` أو مجرد lambda. ده نفس الـ Open/Closed في الدرس 17: طريقة دفع جديدة تبقى strategy جديدة، والـ `Checkout` مايتغيرش.

### الـ Factory

**مكان واحد بيقرر أنهي object يتعمل حسب الـ input.**

```kotlin
object PaymentFactory {
    fun from(method: String, details: String): PaymentChoice = when (method) {
        "card" -> PaymentChoice.Ok(CardPayment(details.takeLast(4)))
        "cash" -> PaymentChoice.Ok(cashOnDelivery)
        else -> PaymentChoice.Unknown(method)
    }
}
```

الفايدة إن الـ `when` على النوع موجود في مكان واحد بس، مش مبعتر في كل الشاشات. وفي Kotlin الـ factory بيتكتب كـ `object`، أو companion function (الدرس 09)، أو top-level function. ولاحظ إنه بيرجّع sealed result بدل ما يرمي exception.

### الـ Builder

**بناء object معقد خطوة بخطوة.**

في Java الـ builder كان ضروري لأن مفيش named ولا default arguments. في Kotlin:

```kotlin
data class Order(val id: Int, val items: List<String>, val giftWrap: Boolean = false, val note: String? = null)

Order(id = 2, items = listOf("Pen"), giftWrap = true)
```

ده builder جاهز ومقري. والـ builder الحقيقي لسه مفيد لما البناء فيه loops أو شروط، وساعتها بيتكتب كـ DSL (الدرس 14).

### الـ Observer

**فيه object بيحتفظ بقايمة listeners ويبلّغهم لما حاجة تتغير.**

```kotlin
class StockLevel(initial: Int) {
    private val listeners = mutableListOf<(Int) -> Unit>()
    fun subscribe(listener: (Int) -> Unit): () -> Unit { ... return { listeners -= listener } }
    fun update(newValue: Int) { ... listeners.forEach { it(newValue) } }
}
```

تفاصيل مهمة:

- الـ subscribe بترجّع function لإلغاء الاشتراك. من غيرها هيبقى عندك memory leaks (الدرس 11).
- بنلف على نسخة من القايمة (`toList()`) عشان لو listener ألغى اشتراكه وهو بيتبلّغ مايحصلش crash.
- لو القيمة ماتغيرتش، مفيش تبليغ.

في الشغل الحقيقي مش هتكتب ده بنفسك. الـ `StateFlow` والـ `State` في Compose observers متقدمين بيعملوا كل ده وأكتر.

### الـ State pattern

**الـ object بيغيّر سلوكه لما حالته الداخلية تتغير، وكل حالة بتحدد التحركات المسموحة منها.**

```kotlin
sealed class OrderState(val name: String) {
    open fun pay(): OrderState = reject("pay")
    data object Created : OrderState("Created") {
        override fun pay() = Paid
    }
    data object Paid : OrderState("Paid") { ... }
}
```

بدل `when (status)` ضخمة في كل function، كل حالة كلاس بيعرف قواعده. وده بيجمع الدرس 06 (polymorphism) مع الدرس 08 (sealed).

### الـ Decorator

**فيه object بيلف object تاني من نفس الـ interface ويضيف سلوك قبله أو بعده.**

```kotlin
class TaxDecorator(private val inner: PriceCalculator) : PriceCalculator {
    override fun price(baseCents: Long) = inner.price(baseCents) * 114 / 100
}

TaxDecorator(DiscountDecorator(BasePrice(), 10))
```

ده بالظبط الـ composition في الدرس 16، والـ `by` في الدرس 12 بيسهّله لو الـ interface كبير. والترتيب مهم: خصم ثابت قبل الضريبة غير بعدها.

### الـ Adapter

**فيه object بيلف API ليه شكل مختلف عشان يناسب الـ interface اللي انت محتاجه.**

```kotlin
class CourierAdapter(private val sdk: OldCourierSdk) : ShippingMethod {
    override fun costCents(weightGrams: Int): Long = (sdk.calc(weightGrams / 1000.0) * 100).toLong()
}
```

الـ SDK القديم بيتكلم كيلو وجنيه، والشوب بيتكلم جرام وقروش. الـ adapter بيترجم، وباقي الكود مايعرفش إن فيه SDK قديم أصلًا.

### الـ Repository

**يخبّي مصدر الداتا ورا interface شبه الـ collection.** شفناه في الدرس 13 كـ `Repository<T>`، وفي الدرس 17 كمثال على الـ Dependency Inversion. الـ ViewModel بيطلب منه منتجات، ومايعرفش لو جاية من API أو داتابيز أو cache.

### الـ Singleton

في Kotlin هو كلمة واحدة: `object` (الدرس 09). وخلي بالك من عيبه: صعب تبدّله في الـ tests، فاستخدمه للحاجات اللي مالهاش حالة أو حالتها بسيطة.

### جدول سريع

| الـ pattern | المشكلة | الشكل في Kotlin |
|---|---|---|
| الـ Strategy | سلوك ممكن يتبدل | الـ `fun interface` أو lambda |
| الـ Factory | اختيار النوع حسب input | الـ `object` أو companion function |
| الـ Builder | بناء معقد | الـ named args، أو DSL |
| الـ Observer | تبليغ بالتغيير | الـ `StateFlow` و `State` |
| الـ State | سلوك حسب الحالة | الـ sealed class |
| الـ Decorator | إضافة سلوك بالتغليف | الـ composition و `by` |
| الـ Adapter | توفيق interfaces مختلفة | كلاس wrapper |
| الـ Repository | إخفاء مصدر الداتا | الـ interface مع generics |
| الـ Singleton | نسخة واحدة | الكلمة `object` |

## امشي مع الكود

### [L01_StrategyAndFactory.kt](../src/main/kotlin/m18_patterns/L01_StrategyAndFactory.kt)

```
  charged 10000 to card *4242 (+2% fee: 200)
  debited 10000 from wallet 010-111
  collect 10000 in cash on delivery
```

نفس `Checkout` بتلات strategies، والتالتة مجرد lambda.

```
  charged 5000 to card *1111 (+2% fee: 100)
  collect 5000 in cash on delivery
  unsupported payment method: bitcoin
```

الـ factory حوّل النص لـ strategy، ورجّع نتيجة واضحة للطريقة المش مدعومة بدل crash.

### [L02_BuilderAndDsl.kt](../src/main/kotlin/m18_patterns/L02_BuilderAndDsl.kt)

```
  OrderJava(id=1, items=[Pen, Mug], giftWrap=true, note=null)
  Order(id=2, items=[Pen, Mug], giftWrap=true, note=null)
```

النتيجة واحدة، بس النسخة التانية مش محتاجة كلاس builder خالص.

```
  Order(id=4, items=[Pen, Pen, Pen, Mug], giftWrap=true, note=null)
```

الـ DSL مناسب لما فيه تكرار أو خطوات.

### [L03_ObserverAndState.kt](../src/main/kotlin/m18_patterns/L03_ObserverAndState.kt)

```
  badge shows 10
  badge shows 5
  ALERT: only 2 pens left
```

المشترك الجديد خد القيمة الحالية فورًا. والتحديث بنفس القيمة ماوصلش لحد. وبعد إلغاء الاشتراك، البادج وقف.

```
  state: Created
     cannot ship when Created
  state: Paid
  state: Shipped
     cannot cancel when Shipped
```

كل حالة رفضت التحركات اللي مش مسموحة منها، من غير أي `when` في الكود اللي بينادي.

### [L04_DecoratorAndAdapter.kt](../src/main/kotlin/m18_patterns/L04_DecoratorAndAdapter.kt)

```
  base -> 10% off -> +14% VAT: 10260
  base -> +14% VAT -> 10% off: 10260
```

نفس الـ decorators بترتيبين. في الحالة دي النتيجة واحدة لأن الضرب تبديلي، بس مع خصم بمبلغ ثابت الترتيب هيفرق (جرّب في التمرين).

```
  2000g via courier = 3500 cents
```

الـ SDK حسب بالكيلو والجنيه، والـ adapter رجّع قروش زي ما الشوب متوقع.

## في Compose

### الـ Modifier chain هو Decorator

كل modifier بيلف اللي قبله ويضيف سلوك:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
Modifier
    .padding(8.dp)        // يلف: يضيف مسافة
    .background(Color.White)
    .clickable { }        // يلف: يضيف click
```

والـ `Modifier.then` هو اللي بيعمل التغليف ده جوه. والترتيب بيفرق بالظبط زي `TaxDecorator`: لو `padding` قبل `background`، المسافة مش هتتلون. لو بعده، هتتلون. ده أشهر سؤال في Compose، وإجابته في الـ Decorator pattern.

### الـ State و StateFlow هما Observer

الـ `StateFlow` بيعمل كل اللي `StockLevel` بيعمله: بيدّي المشترك الجديد القيمة الحالية، ومابيبلّغش لو القيمة اتساوت، والاشتراك بيتلغى أوتوماتيك مع الـ lifecycle لما تستخدم `collectAsStateWithLifecycle`.

ومن جوه Compose نفسه:

- الـ `mutableStateOf` بترجّع state observable. أي composable بيقرا `value` بيبقى مشترك أوتوماتيك، ولما القيمة تتغير بيتعاد رسمه بس.
- الـ `derivedStateOf` observer مبني على observers: بيحسب قيمة من states تانية ومش بيبلّغ غير لو النتيجة نفسها اتغيرت.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ProductList(products: List<Product>) {
    val listState = rememberLazyListState()
    val showBackToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 5 }
    }
    Box {
        LazyColumn(state = listState) {
            items(products) { ProductCard(it) }
        }
        if (showBackToTop) {
            Button(onClick = { }, Modifier.align(Alignment.BottomEnd)) { Text("Top") }
        }
    }
}
```

الـ `firstVisibleItemIndex` بيتغير مع كل scroll، بس `showBackToTop` مش بيتغير غير لما يعدّي 5. فالزرار مش بيتعاد رسمه مع كل حركة.

### الـ snapshotFlow هو Adapter

الـ `snapshotFlow` بيحوّل قراية Compose state لـ `Flow`، عشان تقدر تستخدم operators زي `debounce`. يعني بيوفّق بين نظامين مختلفين للـ observing، زي `CourierAdapter` بالظبط.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .distinctUntilChanged()
        .collect { index -> analytics.logScroll(index) }
}
```

### الـ remember*State هي Factories

الـ `rememberLazyListState` بترجّع `LazyListState`، والـ constructor بتاعه `internal`. يعني مش بتعمله بنفسك، الـ factory بيعمله ويحفظه مع الـ composition. ده نفس `Product.create` في الدرس 09.

### الـ sealed UiState هو State pattern

الشاشة اللي بترسم الحالة بـ `when` على sealed state (الدرس 08) هي State pattern على مستوى الـ UI: كل حالة بتحدد الشكل اللي يتعرض.

### الـ Modifier.Node والـ Modifier.composed

الطريقة القديمة لعمل modifier فيه حالة كانت `Modifier.composed`. الـ function دي مش deprecated رسميًا، بس الـ documentation بتقول إنها مش مُنصح بيها تاني لأسباب أداء. الطريقة الحديثة إنك تورث من `Modifier.Node` (الدرس 04)، وده template method: الـ framework بيملك الـ lifecycle وانت بتملا الـ hooks.

## أخطاء شائعة

1. الغلطة: تطبيق patterns عشان الشكل، زي builder لـ data class فيها تلات properties.
   الصح: الـ pattern حل لمشكلة. لو المشكلة مش موجودة، الكود الأبسط أحسن.

2. الغلطة: observer من غير طريقة لإلغاء الاشتراك.
   الصح: الـ subscribe يرجّع حاجة تلغيه، وتلغيه في الوقت الصح.

3. الغلطة: `when (status)` متكررة في كل function للأوردر.
   الصح: State pattern بـ sealed class، وكل حالة تعرف قواعدها.

4. الغلطة: الوراثة عشان تضيف سلوك لكلاس موجود.
   الصح: Decorator يلف نفس الـ interface.

5. الغلطة: كود الشاشة بيكلم الـ SDK القديم مباشرة.
   الصح: Adapter بيترجم، فتغيير الـ SDK بيلمس مكان واحد.

6. الغلطة: ترتيب modifiers عشوائي.
   الصح: افتكر إنها decorators: كل واحد بيلف اللي قبله.

## أسئلة Interview

1. إيه الفرق بين Strategy و State؟

<details><summary>الإجابة</summary>

الاتنين بيستخدموا polymorphism. في الـ Strategy اللي بينادي بيختار الـ algorithm من برّه. في الـ State الـ object نفسه بيغيّر حالته، وكل حالة بتحدد الحالة اللي بعدها.

</details>

2. ليه الـ Builder نادر في Kotlin؟

<details><summary>الإجابة</summary>

لأن الـ named والـ default arguments بيدّوا نفس الوضوح من غير كلاس إضافي، والـ data class بيدّي `copy` للتعديلات. والـ builder الحقيقي بيتكتب كـ DSL لما البناء يكون فيه خطوات.

</details>

3. إيه الفرق بين Decorator و Adapter؟

<details><summary>الإجابة</summary>

الـ Decorator بيلف object من نفس الـ interface ويضيف سلوك. الـ Adapter بيلف object من interface مختلف عشان يناسب الـ interface المطلوب.

</details>

4. ليه ترتيب الـ modifiers بيفرق في Compose؟

<details><summary>الإجابة</summary>

لأنهم decorators: كل modifier بيلف اللي قبله. فـ padding قبل background غير padding بعده، بالظبط زي الخصم قبل الضريبة وبعدها.

</details>

5. إزاي `StateFlow` بيطبّق Observer pattern؟

<details><summary>الإجابة</summary>

بيحتفظ بقيمة حالية، وبيدّيها لأي مشترك جديد فورًا، وبيبلّغ المشتركين لما القيمة تتغير بس، والاشتراك بيتلغى مع الـ coroutine أو الـ lifecycle.

</details>

6. إيه فايدة `derivedStateOf`؟

<details><summary>الإجابة</summary>

بيحسب قيمة من states تانية، ومابيبلّغش غير لو النتيجة نفسها اتغيرت. ده بيقلل إعادة الرسم لما الـ state الأصلي بيتغير كتير والنتيجة لأ.

</details>

## Self-check

- [ ] أقدر أسمي الـ pattern المناسب لكل مشكلة في أول الدرس.
- [ ] أعرف أكتب Strategy بـ `fun interface` و Factory بيرجّع sealed result.
- [ ] أعرف أكتب Observer فيه إلغاء اشتراك، و State machine بـ sealed class.
- [ ] أعرف أكتب Decorator و Adapter وأشرح الفرق.
- [ ] أقدر أشرح ليه ترتيب الـ modifiers بيفرق باستخدام Decorator.
- [ ] ملف Exercises.kt بيطبع ALL PASS

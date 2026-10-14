# 17 - SOLID

## المشكلة الأول

بعد شهور من الشغل على الشوب، فيه كلاس اسمه `OrderManager` بقى 800 سطر. بيعمل validation للأوردر، ويحسب السعر والشحن والخصومات، ويكلم الداتابيز، ويبعت إيميل. وفيه `when` على نوع الكوبون فيها 15 حالة.

النتيجة:

- كل تعديل صغير، زي تغيير رسوم الشحن، بيلمس نفس الملف اللي فيه كود الداتابيز.
- مفيش طريقة تعمل test لحساب السعر من غير ما تشغّل داتابيز حقيقية.
- كل كوبون جديد معناه تعديل في كود شغال ومتجرب، ومعاه خطر تكسر الكوبونات القديمة.
- اتنين developers بيعدّلوا نفس الملف في نفس الأسبوع فبيحصل merge conflicts كل شوية.

الـ SOLID خمس مبادئ بتشرح **ليه** الكود ده بقى صعب، و**إزاي** تفككه. هي مش قواعد مقدسة، هي أسئلة تسألها لما الكود يبدأ يوجعك. وكل مبدأ فيهم بيستخدم أدوات اتعلمتها في الـ modules اللي فاتت.

## الـ concept بالتفصيل

### الحرف S: Single Responsibility

**الكلاس يكون ليه سبب واحد بس يتغير عشانه.**

الكلمة المهمة هي "سبب". مش "function واحدة" ولا "حاجة واحدة". السبب غالبًا شخص أو جهة بتطلب التغيير: قواعد التسعير بيطلبها فريق المبيعات، شكل الإيميل بيطلبه التسويق، الداتابيز بيغيّرها فريق الـ backend.

علامات إن المبدأ مكسور:

- اسم الكلاس فيه Manager أو Helper أو Utils وبيعمل كل حاجة.
- الـ imports فيها داتابيز و UI و network في نفس الملف.
- صعب تشرح الكلاس بيعمل إيه في جملة واحدة من غير كلمة "و".

الحل: قسّمه لكلاسات كل واحد مركز (`OrderValidator`، `PriceCalculator`، `OrderRepository`، `ReceiptSender`)، وكلاس صغير بينسّق بينهم (`PlaceOrder`).

### الحرف O: Open/Closed

**مفتوح للإضافة، مقفول للتعديل.**

لما تحتاج سلوك جديد، المفروض **تضيف** كود جديد مش **تعدّل** كود قديم متجرب. الأداة الأساسية هي الـ interfaces والـ polymorphism (الدروس 05 و 06):

```kotlin
fun interface DiscountRule { fun amountOff(subtotal: Long): Long }

class CartTotals(private val rules: List<DiscountRule>) { ... }

val loyalty = DiscountRule { if (it > 15_000) 1_000 else 0 }   // إضافة، من غير تعديل
```

غلطة شائعة في فهمه: إنك "ماتعدّلش الكود أبدًا". لأ، bug fix تعديل عادي. المبدأ عن **نقاط التوسع المتوقعة**: الحاجات اللي بتتغير كتير (أنواع الخصومات، طرق الدفع، قنوات الإشعارات) لازم يبقى ليها شكل plugin.

### الحرف L: Liskov Substitution

**أي كود شغال مع الأب لازم يفضل شغال مع أي ابن.**

الابن مسموح يضيف، بس مش مسموح يكسر وعد الأب. المثال الكلاسيكي:

```kotlin
open class Rectangle(open var width: Int, open var height: Int)
class SquareBad(side: Int) : Rectangle(side, side)   // تغيير العرض بيغيّر الطول
```

أي function مكتوبة على أساس إن "تغيير العرض مش بيأثر على الطول" هتطلع نتيجة غلط مع `SquareBad`. الكود عمل compile، والـ is-a في الكلام صحيحة (المربع مستطيل في الرياضة)، بس **السلوك** مختلف.

علامات الكسر:

- الابن بيرمي `UnsupportedOperationException` من function في الأب.
- الكود اللي بينادي بيعمل `if (x is Child)` عشان يتعامل مع حالة خاصة.
- الابن بيقبل مدخلات أقل أو بيرجّع نتايج غريبة عن اللي الأب بيوعد بيها.

الحل غالبًا: objects مش بتتغير، أو hierarchy مختلفة (sealed interface)، أو composition بدل الوراثة.

### الحرف I: Interface Segregation

**ماتجبرش حد يعتمد على functions مش محتاجها.**

لو interface فيه `pay` و `refund` و `subscribe`، والكاش عند الاستلام بيعرف يعمل `pay` بس، هيضطر يرمي exceptions من الباقي. وده كمان كسر للـ Liskov.

الحل: interfaces صغيرة على شكل أدوار، زي `Payer` و `Refunder` و `Subscriber`. كل كلاس بينفذ اللي يقدر عليه، وكل function بتطلب الدور اللي محتاجاه بس. والنتيجة إن النداء المستحيل بيتحول من crash وقت التشغيل لـ error وقت الـ compile.

### الحرف D: Dependency Inversion

**الكود المهم مايعتمدش على التفاصيل. الاتنين يعتمدوا على abstraction.**

```kotlin
// غلط: الـ service بيعمل الداتابيز بنفسه
class CheckoutServiceBad { private val orders = MySqlOrders() }

// صح: الـ service بيستلم الـ abstraction من برّه
class CheckoutService(private val store: OrderStore)
```

اسم "inversion" جاي من إن اتجاه الاعتماد بيتقلب: بدل ما منطق الشوب يعتمد على MySQL، الاتنين بيعتمدوا على `OrderStore`. والطريقة العملية اسمها dependency injection: تباصي الاعتمادات من الـ constructor. مكتبات زي Hilt و Koin بتعمل ده أوتوماتيك، بس الفكرة نفسها مجرد constructor parameter.

أهم مكسب: الـ testing. تقدر تباصي `FakeOrderStore` وتتأكد من السلوك من غير أي داتابيز.

### الخمسة مع بعض

| المبدأ | السؤال اللي تسأله | الأداة من الكورس |
|---|---|---|
| الحرف S | الكلاس ده بيتغير لكام سبب؟ | تقسيم الكلاسات (الدرس 01 و 02) |
| الحرف O | عشان أضيف حالة، هعدّل ولا هضيف؟ | الـ interfaces والـ polymorphism (05 و 06) |
| الحرف L | هل أي ابن ينفع مكان الأب من غير مفاجآت؟ | الـ sealed والـ immutability (07 و 08) |
| الحرف I | هل حد مجبور ينفذ حاجة مش بيستخدمها؟ | الـ interfaces الصغيرة (05) |
| الحرف D | هل الكود المهم بيعمل اعتماداته بنفسه؟ | الـ constructor injection والـ composition (16) |

## امشي مع الكود

### [L01_SingleResponsibility.kt](../src/main/kotlin/m17_solid/L01_SingleResponsibility.kt)

```
  total = 32500
  INSERT 1 32500
  To sara@mail.com: order 1 = 32500
```

النسختين طلّعوا نفس النتيجة بالظبط. الـ refactor مغيّرش السلوك، غيّر الشكل بس.

```
  PriceCalculator can be tested alone: 60000
```

حساب السعر بقى ينفع يتجرب لوحده من غير داتابيز ولا إيميل.

### [L02_OpenClosed.kt](../src/main/kotlin/m17_solid/L02_OpenClosed.kt)

```
  percent10           -> 18000
  percent10 + fixed50 -> 13000
  percent10 + loyalty -> 17000
```

الكلاس `CartTotals` ماتغيرش خالص، وقاعدة loyalty جديدة اتضافت من برّه.

### [L03_LiskovSubstitution.kt](../src/main/kotlin/m17_solid/L03_LiskovSubstitution.kt)

```
  Rectangle: expected area 20, got 20
  SquareBad: expected area 20, got 16
```

نفس الـ function، ونوع ابن شكله سليم، والنتيجة غلط. ده كسر الـ Liskov.

```
  Rect(width=5, height=4) area=20
  Square(side=4) area=16
```

بعد ما الأشكال بقت immutable وفي sealed interface، كل شكل بيحترم وعد `Shape`.

### [L04_InterfaceSegregation.kt](../src/main/kotlin/m17_solid/L04_InterfaceSegregation.kt)

```
  refund blew up at runtime: cash cannot refund online
```

الـ interface الضخم خلّى الكاش يوعد بحاجة مش يقدر يعملها.

```
  cash collected: 5000
  card charged: 5000
  card refunded: 5000
```

بعد التقسيم، `returnItem` بتطلب `Refunder`، فالكاش مش هيتباصى لها أصلًا.

### [L05_DependencyInversion.kt](../src/main/kotlin/m17_solid/L05_DependencyInversion.kt)

```
  MySQL INSERT order 2
  fake saved order 3
  fake recorded: [3]
```

نفس `CheckoutService` اشتغل مع الداتابيز الحقيقية ومع fake، من غير أي تعديل جواه.

## في Compose

### الـ S: الـ stateful والـ stateless composables

الـ composable اللي بيجيب الداتا ويعرضها ويتعامل مع الـ clicks، عنده كذا سبب للتغيير. الحل اسمه state hoisting:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
// Stateful: بيوصل الـ ViewModel بالشاشة بس
@Composable
fun CartRoute(viewModel: CartViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CartContent(state = state, onRemove = viewModel::remove)
}

// Stateless: بيرسم بس، ومش عارف أي حاجة عن ViewModel
@Composable
fun CartContent(state: CartUiState, onRemove: (Int) -> Unit) {
    LazyColumn {
        items(state.items) { line ->
            Row {
                Text("${line.productId} x${line.qty}", Modifier.weight(1f))
                Button(onClick = { onRemove(line.productId) }) { Text("Remove") }
            }
        }
    }
}
```

الـ `CartContent` ليه مسؤولية واحدة: يرسم حالة معينة. ينفع تعمل له preview وtest بأي داتا.

### الـ O: الـ Modifier والـ slots

الـ composables المكتوبة كويس بتاخد `modifier: Modifier` و slots. ده بيخليك **تضيف** padding أو click أو محتوى جديد من برّه، من غير ما تفتح الـ composable وتعدّله. نفس `CartTotals` في L02.

### الـ L: أي Shape شغال

الـ `Modifier.clip` بتقبل أي `Shape`، وأي `Shape` لازم ينفذ `createOutline` صح. لو عملت `Shape` بيرمي exception أو بيرجّع outline فاضي لأحجام معينة، أي مكان بيستخدم `clip` هيتكسر. احترام عقد الـ interface هو اللي بيخلّي `clip(RoundedCornerShape(8.dp))` و `clip(TicketShape(...))` يشتغلوا بنفس الثقة.

### الـ I: lambdas ضيقة بدل الـ ViewModel كله

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
// سيء: الزرار معتمد على الـ ViewModel كله
@Composable
fun AddButtonBad(viewModel: CartViewModel) {
    Button(onClick = { viewModel.add(1) }) { Text("Add") }
}

// كويس: بيطلب اللي محتاجه بس
@Composable
fun AddButton(onAdd: () -> Unit) {
    Button(onClick = onAdd) { Text("Add") }
}
```

الـ `AddButton` مايعرفش إن فيه ViewModel أصلًا. تقدر تستخدمه في أي شاشة، وتعمل له preview من غير ما تعمل ViewModel.

### الـ D: الـ ViewModel بيستلم الـ Repository

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
interface ProductRepository {
    suspend fun products(): List<Product>
}

class ProductsViewModel(private val repo: ProductRepository) : ViewModel() {
    // ...
}
```

في التطبيق بتباصي النسخة اللي بتكلم الـ API، وفي الـ tests بتباصي fake. والـ ViewModel نفسه ماتغيرش.

### الـ CompositionLocal: dependency inversion في الـ UI

أحيانًا حاجة زي "فورمات العملة" محتاجها composables كتير في أعماق الشجرة، وتمريرها في كل parameter متعب. الـ Compose فيه أداة اسمها `CompositionLocal`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
fun interface PriceFormatter { fun format(cents: Long): String }

val LocalPriceFormatter = staticCompositionLocalOf<PriceFormatter> {
    PriceFormatter { "EGP %d.%02d".format(it / 100, it % 100) }
}

@Composable
fun PriceText(cents: Long) {
    Text(LocalPriceFormatter.current.format(cents))   // بيعتمد على الـ abstraction
}

@Composable
fun UsdStore(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPriceFormatter provides PriceFormatter { "USD ${it / 100}" }) {
        content()
    }
}
```

الـ `PriceText` بيعتمد على `PriceFormatter` (abstraction)، واللي فوقه في الشجرة هو اللي بيقرر التنفيذ بـ `CompositionLocalProvider`. والـ `staticCompositionLocalOf` بترجّع `ProvidableCompositionLocal`، وده ابن من الـ sealed class اللي اسمها `CompositionLocal`. خلي بالك: استخدمه للحاجات الشبه ثابتة على مستوى الـ UI (theme، formatter)، مش كبديل لتمرير الداتا العادية.

## أخطاء شائعة

1. الغلطة: فهم الـ S على إنها "كل كلاس function واحدة".
   الصح: سبب واحد للتغيير. كلاس فيه خمس functions عن التسعير بس مسؤولية واحدة.

2. الغلطة: interface لكل كلاس "عشان الـ D" حتى لو مفيش أي تنفيذ تاني متوقع.
   الصح: الـ abstraction ليها تكلفة. حطها عند الحدود اللي بتتغير أو محتاجة fake في الـ tests (داتابيز، network، وقت، random).

3. الغلطة: ابن بيرمي `UnsupportedOperationException`.
   الصح: ده كسر Liskov و ISP مع بعض. قسّم الـ interface.

4. الغلطة: الـ ViewModel بيعمل الـ repository بنفسه.
   الصح: استلمه من الـ constructor.

5. الغلطة: composable بياخد الـ ViewModel كله وهو محتاج function واحدة.
   الصح: باصي lambda أو قيم.

6. الغلطة: تطبيق كل المبادئ على كود صغير من أول يوم.
   الصح: طبّقها لما الألم يظهر، أو عند الحدود الواضحة. الـ SOLID أداة، مش هدف.

## أسئلة Interview

1. اشرح الـ Single Responsibility بمثال.

<details><summary>الإجابة</summary>

الكلاس يكون ليه سبب واحد للتغيير. زي `OrderManager` اللي بيعمل validation وتسعير وحفظ وإيميل، بيتقسم لأربع كلاسات وكلاس منسّق، فتغيير الشحن يلمس `PriceCalculator` بس.

</details>

2. إزاي تطبّق Open/Closed في Kotlin؟

<details><summary>الإجابة</summary>

بـ interface أو `fun interface` للجزء اللي بيتغير، والكلاس الأساسي يستقبل implementations منه. أي حالة جديدة تبقى implementation جديدة من غير تعديل الكلاس.

</details>

3. ليه المربع اللي بيورث من المستطيل بيكسر Liskov؟

<details><summary>الإجابة</summary>

لأن المستطيل بيوعد إن العرض والطول مستقلين، والمربع بيغيّر الاتنين مع بعض. أي كود معتمد على الوعد ده هيطلع نتيجة غلط مع المربع.

</details>

4. إيه علاقة Interface Segregation بالـ composables؟

<details><summary>الإجابة</summary>

الـ composable ياخد اللي محتاجه بس، زي lambda `onAdd`، بدل الـ ViewModel كله. ده بيقلل الاعتماد ويخلّيه قابل لإعادة الاستخدام والـ preview.

</details>

5. إيه الفرق بين Dependency Inversion و Dependency Injection؟

<details><summary>الإجابة</summary>

الـ Inversion مبدأ: الكود المهم يعتمد على abstractions مش تفاصيل. الـ Injection تقنية لتطبيقه: الاعتمادات بتتباصى من برّه، غالبًا من الـ constructor.

</details>

6. إمتى تستخدم `CompositionLocal`؟

<details><summary>الإجابة</summary>

لحاجات على مستوى الـ UI محتاجها composables كتير في أعماق الشجرة وبتتغير نادرًا، زي theme أو formatter. مش بديل لتمرير الداتا العادية كـ parameters.

</details>

## Self-check

- [ ] أقدر أشرح كل حرف في SOLID بجملة ومثال من الشوب.
- [ ] أقدر أعمل refactor لـ `LegacyCheckout` في التمرين وأشرح كل مبدأ اتطبق فين.
- [ ] أعرف أكتشف كسر Liskov من علامات زي `UnsupportedOperationException`.
- [ ] أعرف أقسم composable لـ stateful و stateless.
- [ ] أعرف أستخدم `CompositionLocal` وأعرف إمتى مااستخدمهوش.
- [ ] ملف Exercises.kt بيطبع ALL PASS

# 07 - Data Classes

## المشكلة الأول

شاشة السلة عندها حالة: المنتجات، وهل فيه تحميل، وهل فيه رسالة خطأ. لو كتبتها كلاس عادي:

```kotlin
class CartUiState(val items: List<String>, val isLoading: Boolean)

val a = CartUiState(listOf("Pen"), false)
val b = CartUiState(listOf("Pen"), false)

println(a == b)   // false
println(a)        // CartUiState@1b6d3586
```

حالتين فيهم نفس الداتا بالظبط، والبرنامج شايفهم مختلفين. والطباعة مالهاش أي معنى. وعشان تغيّر حاجة واحدة لازم تكتب constructor كامل وتنقل كل الـ properties بإيدك.

ولو حاولت تحل ده بإنك تخلي الـ properties `var` وتغيّرها مكانها، هتدخل في مشاكل أكبر: أي حد ماسك reference هيشوف التغيير فجأة، والـ UI مش هيعرف إن حاجة اتغيرت.

الحل في Kotlin كلمة واحدة: `data`.

## الـ concept بالتفصيل

### الـ compiler بيكتب إيه بالنيابة عنك؟

```kotlin
data class Product(val id: Int, val name: String, val priceCents: Long)
```

لكل property في الـ **primary constructor** الـ compiler بيولّد:

| الـ function | بتعمل إيه |
|---|---|
| الـ `equals` | بتقارن كل الـ properties واحدة واحدة |
| الـ `hashCode` | رقم محسوب من نفس الـ properties، ومتوافق مع `equals` |
| الـ `toString` | بتطبع `Product(id=1, name=Pen, priceCents=500)` |
| الـ `componentN` | دالة لكل property بالترتيب، عشان الـ destructuring |
| الـ `copy` | بتعمل object جديد وتغيّر اللي انت عايزه بس |

لو كتبتهم بإيدك هيبقوا حوالي 40 سطر. والأهم إنهم هيفضلوا صح لما تضيف property جديدة، لأنك مش هتنسى تحدّثهم.

### المساواة البنيوية والمساواة المرجعية

- علامة `==` بتنادي `equals`. في الـ data class معناها "نفس الداتا".
- علامة `===` بتسأل "نفس الـ object في الذاكرة؟". دي مش بتتأثر بالـ data class.

```kotlin
val s2 = s1.copy(items = s1.items + "Pen")
s1 == s2    // false: داتا مختلفة
s2 === s2.copy()   // false: object جديد
s2 == s2.copy()    // true: نفس الداتا
```

### الـ copy والحالة اللي مش بتتغير

الفكرة الأساسية: **بدل ما تغيّر الـ object، اعمل واحد جديد**.

```kotlin
val s1 = CartState()
val s2 = s1.copy(items = s1.items + "Pen")
val s3 = s2.copy(couponCode = "SAVE10")
```

كل خطوة snapshot مستقلة. اللي ماسك `s1` لسه شايف سلة فاضية. مفيش حد هيتفاجئ إن الداتا اتغيرت من تحته. وده أساس الـ state management في Compose.

### الـ copy سطحية

الـ `copy()` بتنسخ الـ references، مش الـ objects اللي بتشاور عليها:

```kotlin
data class LeakyState(val items: MutableList<String>)

val copied = original.copy()
copied.items.add("Stolen TV")    // original اتغير كمان!
```

الاتنين بيشاوروا على نفس الـ list. عشان كده القاعدة: الـ properties تبقى `val` **ومن أنواع مش بتتغير**: `List` مش `MutableList`، وتعمل list جديدة بـ `+` أو `map` بدل `add`.

### الـ destructuring

```kotlin
val (id, name, price) = Product(7, "Mug", 4500)
for ((_, n, p) in cart) println("$n costs $p")
```

بيشتغل بفضل `component1()` و `component2()` وهكذا. خلي بالك: الـ destructuring **بالترتيب مش بالاسم**. لو حد بدّل ترتيب الـ properties في الكلاس، `val (id, name)` هتاخد قيم غلط من غير أي error. استخدم الـ destructuring مع الكلاسات الصغيرة والواضحة، زي `Pair` أو entry في map.

### القيود

- لازم يكون فيه property واحدة على الأقل في الـ primary constructor.
- كل parameters الـ primary constructor لازم تكون `val` أو `var`.
- مينفعش يكون `open` أو `abstract` أو `sealed` أو `inner`.
- ينفع ينفذ interfaces ويورث من كلاس open.

### الـ properties اللي في جسم الكلاس

```kotlin
data class Order(val id: Int, val total: Long) {
    var note: String = ""
}
```

الـ `note` **مش** داخلة في `equals` ولا `toString` ولا `copy`. أوردرين بنفس الـ id والإجمالي هيبقوا متساويين حتى لو الملاحظة مختلفة، والـ `copy()` هتضيّع الملاحظة. ده ممكن يكون مقصود (مثلًا cache)، بس غالبًا بيبقى bug.

### الـ var كمفتاح في HashSet

الـ `HashSet` بيحط العنصر في "درج" حسب الـ `hashCode` وقت الإضافة. لو غيّرت property بتأثر على الـ hash بعد كده، العنصر بيفضل في الدرج القديم، والـ `contains` بيدوّر في درج جديد فمش بيلاقيه. الحل: data classes من غير `var`.

### الاختيار بين data class و class عادي و value class

| السؤال | الـ data class | الـ class عادي | الـ value class |
|---|---|---|---|
| الغرض | داتا ليها قيمة | الـ object ليه هوية وسلوك | غلاف بنوع واضح لقيمة واحدة |
| المساواة | بالداتا | بالهوية | بالقيمة |
| عدد الـ properties | واحدة أو أكتر | أي عدد | واحدة بس |
| بيعمل object في الذاكرة؟ | آه | آه | غالبًا لأ |
| أمثلة | حالة الشاشة، رد API | الـ ViewModel، الـ repository | السعر، رقم المنتج |

الـ value classes هتتشرح في الدرس 10.

## امشي مع الكود

### [L01_GeneratedMembers.kt](../src/main/kotlin/m07_data_classes/L01_GeneratedMembers.kt)

```
  PlainPoint(1,2) vs PlainPoint(1,2): different
  DataPoint(1,2)  vs DataPoint(1,2):  same
```

كلمة `data` غيّرت معنى `==` من الهوية للداتا.

```
  plain set size = 2
  data set size  = 1
```

الـ `Set` شال التكرار بس مع الـ data class، لأن `hashCode` و `equals` متوافقين.

```
  id=7 name=Mug price=4500
  Pen costs 500
```

الـ destructuring شغال في التعريف وفي الـ loop، والـ underscore معناها "مش محتاج القيمة دي".

### [L02_CopyAndImmutability.kt](../src/main/kotlin/m07_data_classes/L02_CopyAndImmutability.kt)

```
  original: Product(id=1, name=Pen, priceCents=1000, tags=[])
  sale:     Product(id=1, name=Pen, priceCents=800, tags=[])
```

الخصم رجّع منتج جديد، والأصلي فضل زي ما هو.

```
  s1 = CartState(items=[], couponCode=null)
  s2 = CartState(items=[Pen], couponCode=null)
  s3 = CartState(items=[Pen], couponCode=SAVE10)
```

تلات snapshots مستقلة، كل واحدة اتعملت من اللي قبلها.

```
  copied.items   = [Pen, Stolen TV]
  original.items = [Pen, Stolen TV]
  same list object? same
```

الـ copy السطحية: الاتنين بيشاوروا على نفس الـ `MutableList`.

### [L03_DataClassPitfalls.kt](../src/main/kotlin/m07_data_classes/L03_DataClassPitfalls.kt)

```
  a == b ? true
  a.toString() = Order(id=1, total=500)
  a.copy().note = ''
```

الملاحظة اتجاهلت في المقارنة والطباعة، واتمسحت في الـ copy.

```
  contains before = true
  contains after  = false
  set still has 1 element(s)
```

العنصر لسه جوه الـ set بس مستحيل يتلاقي. ده bug صعب جدًا تكتشفه في مشروع حقيقي.

## في Compose

### الـ UiState كـ data class

ده الشكل القياسي لحالة أي شاشة:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
data class CartUiState(
    val items: List<CartLine> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class CartViewModel(private val repo: CartRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun onAddClicked(productId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        // ...
    }
}
```

ليه data class بالذات؟

- الـ `copy` بيخليك تغيّر حاجة واحدة وتسيب الباقي في سطر واحد.
- الـ `StateFlow` مش بيبعت قيمة جديدة لو `equals` قالت إنها نفس القديمة. لو الحالة كلاس عادي، كل `copy` يدوي هيتعامل كأنه تغيير حتى لو الداتا هي هي. مع data class المقارنة بالداتا، فمفيش إعادة رسم على الفاضي.
- الـ `toString` بيطلع في الـ logs بشكل مقروء، وده بيفرق جدًا وانت بتعمل debug.

### الـ stability والـ recomposition

الـ Compose compiler بيقسم أي type لـ stable أو unstable:

- الـ type بيبقى **stable** لو Compose يقدر يثق إن `equals` بتعبّر عن التغيير. الـ data class اللي كل properties بتاعتها `val` ومن أنواع stable (زي `String` و `Int` و `Boolean` و data classes تانية stable) بيبقى stable أوتوماتيك.
- الـ type بيبقى **unstable** لو فيه `var`، أو property نوعها `List` أو `Map` العادية (لأن الـ compiler مش يقدر يضمن إن مفيش `MutableList` ورا الـ interface، نفس فخ الدرس 02).

ليه ده مهم؟ لما الـ parent composable يتعاد رسمه، Compose بيقرر يتخطى (skip) الـ child لو الـ parameters ماتغيرتش:

- للـ parameters الـ stable: بيقارن بـ `equals`. لو متساويين، الـ child مش بيتعاد.
- للـ parameters الـ unstable (مع strong skipping اللي بقى الافتراضي في Kotlin 2.0.20 وبعده): بيقارن بـ `===`. يعني لو عملت `copy()` بنفس الداتا، الـ object جديد، فالـ child هيتعاد رسمه.

لو متأكد إن الكلاس مش بيتغير فعلًا، تقدر توعد الـ compiler بالـ annotation اللي اسمها `@Immutable`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Immutable
data class CartUiState(
    val items: List<CartLine> = emptyList(),   // List بقت مقبولة بوعدك
    val isLoading: Boolean = false,
)

@Composable
fun CartContent(state: CartUiState, onAdd: (Int) -> Unit) {
    LazyColumn {
        items(state.items) { line -> Text("${line.productId} x${line.qty}") }
    }
}
```

الـ `@Immutable` وعد إن كل الـ public properties مش هتتغير أبدًا بعد الإنشاء. والـ `@Stable` وعد أضعف: ممكن تتغير، بس Compose هيتبلّغ بأي تغيير (زي `MutableState`). لو كسرت الوعد، الـ UI هيعرض داتا قديمة. فاستخدمهم بس وانت متأكد، زي ما في القاعدة: properties من نوع `val` وlists عمرك ما بتعمل لها cast وتغيّرها.

### مثال من Compose نفسه

الكلاس `TextFieldValue` (اللي بيمثل النص والـ cursor في الـ text field) مش data class، هو كلاس عادي عليه `@Immutable`. والسبب إن فريق Compose كان عايز يتحكم بنفسه في `equals` و `copy` و `toString`، ومايكشفش `componentN`. ده بيوريك إن data class مش الإجابة الوحيدة، بس القاعدة واحدة: **حالة الـ UI immutable وبتتغير بإنشاء نسخة جديدة**.

## أخطاء شائعة

1. الغلطة: `var` جوه UiState وتعديله مكانه.
   الصح: كل حاجة `val`، والتغيير بـ `copy()`.

2. الغلطة: `MutableList` كـ property في data class.
   الصح: `List` وتبني list جديدة بـ `+` أو `map` أو `filter`.

3. الغلطة: property مهمة في جسم الكلاس بدل الـ constructor.
   الصح: أي property جزء من هوية الداتا لازم تكون في الـ primary constructor.

4. الغلطة: destructuring لـ data class كبيرة فيها properties من نفس النوع.
   الصح: استخدم الأسماء `state.isLoading` بدل الترتيب، عشان تغيير الترتيب مايكسرش الكود في صمت.

5. الغلطة: `@Immutable` على كلاس فيه `var` أو list بتتغير.
   الصح: الـ annotation وعد للـ compiler. لو كسرته الشاشة هتعرض حاجة قديمة.

6. الغلطة: استخدام data class لـ object ليه هوية وسلوك، زي ViewModel أو connection.
   الصح: الـ data class للداتا بس.

## أسئلة Interview

1. إيه الـ functions اللي data class بيولّدها؟

<details><summary>الإجابة</summary>

الـ `equals` والـ `hashCode` والـ `toString` والـ `copy` والـ `componentN`، كلهم مبنيين على properties الـ primary constructor بس.

</details>

2. هل `copy()` بتعمل deep copy؟

<details><summary>الإجابة</summary>

لأ، سطحية. بتنسخ الـ references، فلو فيه property نوعها mutable الاتنين هيشاوروا على نفس الـ object.

</details>

3. ليه property في جسم الـ data class ممكن تعمل bug؟

<details><summary>الإجابة</summary>

لأنها مش داخلة في `equals` و `hashCode` و `toString` و `copy`. اتنين objects مختلفين فيها هيتعاملوا كأنهم متساويين، والـ copy هتضيّعها.

</details>

4. ليه UiState في Compose بيكون data class immutable؟

<details><summary>الإجابة</summary>

عشان التغيير يبقى بإنشاء نسخة جديدة بـ `copy`، فالـ `StateFlow` والـ Compose يقارنوا القديم بالجديد بـ `equals` ويقرروا يعيدوا الرسم ولا لأ، ومفيش حد يشوف الداتا بتتغير من تحته.

</details>

5. إيه الفرق بين `@Immutable` و `@Stable`؟

<details><summary>الإجابة</summary>

الـ `@Immutable` بيوعد إن الـ public properties عمرها ما هتتغير بعد الإنشاء. الـ `@Stable` بيوعد إن أي تغيير هيتبلّغ لـ Compose، و `equals` لنفس النسختين هتدي نفس النتيجة دايمًا.

</details>

6. ليه data class فيها `List` بتتعتبر unstable؟

<details><summary>الإجابة</summary>

لأن `List` interface ممكن يكون وراه `MutableList` بيتغير، فالـ compiler مش يقدر يضمن إن `equals` النهارده هتفضل صحيحة بكرة.

</details>

## Self-check

- [ ] أقدر أكتب بإيدي اللي الـ compiler بيولّده لـ data class صغيرة.
- [ ] أعرف الفرق بين `==` و `===` وأطبقه على `copy`.
- [ ] أقدر أشرح فخ الـ shallow copy وفخ الـ var كمفتاح.
- [ ] أعرف أكتب UiState وأحدّثها بـ `copy` في ViewModel.
- [ ] أقدر أشرح stable و unstable وإمتى Compose بيتخطى composable.
- [ ] ملف Exercises.kt بيطبع ALL PASS

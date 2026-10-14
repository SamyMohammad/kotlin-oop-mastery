# 12 - Delegation

## المشكلة الأول

عندك `InMemoryCart` شغال كويس، وعايز تضيف logging لكل عملية `add`. أول فكرة هي الوراثة:

```kotlin
class LoggingCart : InMemoryCart() {
    override fun add(name: String, qty: Int) {
        println("add $name")
        super.add(name, qty)
    }
}
```

المشاكل:

- لازم `InMemoryCart` يكون `open`، وده بيفتح الباب لمشاكل الدرس 03.
- اللوجينج مربوط بنوع سلة واحد. لو عندك `RemoteCart` محتاج كلاس تاني بنفس الكود.
- لو عايز logging وكمان caching، هتعمل `LoggingCachingCart` و `CachingLoggingCart`... الكلاسات بتتضاعف.

البديل إنك **تلف** أي سلة وتحوّل لها الشغل. بس لو الـ interface فيه عشر functions، هتكتب عشرة functions كل واحدة سطر واحد بيحوّل. الـ Kotlin بتكتبهم بالنيابة عنك بكلمة واحدة: `by`.

ونفس الفكرة على مستوى الـ properties: لو عندك عشر properties محتاجة نفس القاعدة (تتقص من المسافات، تتسجل كل تغيير، تتحسب مرة واحدة)، بدل ما تكرر الـ setter عشر مرات، تحط القاعدة في object واحد والـ properties **تحوّل** عليه.

## الـ concept بالتفصيل

### الـ class delegation

```kotlin
interface CartApi {
    fun add(name: String, qty: Int)
    fun remove(name: String)
    fun lines(): Map<String, Int>
}

class LoggingCart(
    private val inner: CartApi,
    private val log: (String) -> Unit,
) : CartApi by inner {
    override fun add(name: String, qty: Int) {
        log("add $name x$qty")
        inner.add(name, qty)
    }
}
```

الـ `: CartApi by inner` معناها: "أي function في `CartApi` ماعملتلهاش override، حوّلها لـ `inner`". الـ compiler بيولّد:

```kotlin
override fun remove(name: String) = inner.remove(name)
override fun lines() = inner.lines()
```

الشروط:

- الـ delegation بيشتغل مع **interfaces** بس، مش مع كلاسات.
- الـ object اللي بتحوّل عليه بيتحدد وقت الإنشاء، غالبًا من الـ constructor.

النتيجة: `LoggingCart` يشتغل مع أي `CartApi`، ومفيش أي كلاس محتاج يبقى `open`.

### فخ الـ class delegation

الـ delegate مايعرفش إنه ملفوف. لو هو بينادي function من نفسه، هينادي النسخة بتاعته هو مش الـ override بتاعك:

```kotlin
class BasicGreeter : Greeter {
    override fun name() = "customer"
    override fun greet() = "Hello, ${name()}"
}

class VipGreeter(inner: Greeter) : Greeter by inner {
    override fun name() = "VIP customer"
}

VipGreeter(BasicGreeter()).greet()   // "Hello, customer"
```

ده الفرق الجوهري بين الـ delegation والوراثة. في الوراثة، `this` جوه الأب بيشاور على الابن، فالـ override بيتنادى. في الـ delegation، `this` جوه الـ delegate بيشاور على الـ delegate نفسه. لو محتاج تغيّر `greet` كمان، لازم تعمل لها override بنفسك.

### الـ property delegation

```kotlin
var email: String by NormalizedEmail()
```

الـ compiler بيترجمها لحاجة كده:

```kotlin
private val email$delegate = NormalizedEmail()
var email: String
    get() = email$delegate.getValue(this, ::email)
    set(value) = email$delegate.setValue(this, ::email, value)
```

يعني أي object فيه الـ functions دي ينفع يبقى delegate:

```kotlin
operator fun getValue(thisRef: R, property: KProperty<*>): T
operator fun setValue(thisRef: R, property: KProperty<*>, value: T)   // للـ var بس
```

وفيه interfaces جاهزة بنفس الشكل: `ReadOnlyProperty` و `ReadWriteProperty`. الـ `property` parameter بيديك اسم الـ property، وده مفيد في الـ logging أو في قراية قيمة من map باسم الـ property.

### الـ delegates الجاهزة

| الـ delegate | بيعمل إيه | مثال من الشوب |
|---|---|---|
| الـ `lazy` | بيحسب أول مرة ويحفظ | حساب تكلفة الشحن |
| الـ `Delegates.observable` | بينادي callback بعد كل تغيير | تسجيل تغييرات العنوان |
| الـ `Delegates.vetoable` | بينادي callback قبل التغيير ويقدر يرفضه | كمية من 1 لـ 10 بس |
| الـ `Delegates.notNull` | زي lateinit بس بيشتغل مع primitives | رقم المستخدم بعد الـ login |
| الـ map | بيقرا القيمة من map باسم الـ property | صف جاي من JSON |
| الـ property تانية `this::x` | بيحوّل على property تانية | اسم قديم deprecated |

### الـ provideDelegate

ده موضوع متقدم: لو الـ delegate محتاج يعمل حاجة **وقت ربطه** بالـ property (مثلًا يتأكد إن اسمها صح أو يسجّلها في مكان)، بيعمل function اسمها `provideDelegate` بترجّع الـ delegate الحقيقي. مكتبات زي Gradle Kotlin DSL بتستخدمه. مش هتحتاجه كتير، بس كويس تعرف إنه موجود.

### الاختيار بين delegation و inheritance

| السؤال | الـ delegation | الـ inheritance |
|---|---|---|
| محتاج الكلاس الأصلي يبقى open؟ | لأ | آه |
| ينفع تلف أي implementation؟ | آه | لأ، نوع واحد ثابت |
| ينفع تركّب أكتر من wrapper؟ | آه | صعب |
| الأصل بينادي الـ override بتاعك؟ | لأ | آه |
| بيشتغل مع | الـ interfaces | الكلاسات |

## امشي مع الكود

### [L01_ClassDelegation.kt](../src/main/kotlin/m12_delegation/L01_ClassDelegation.kt)

```
    [log] add Pen x2
    [log] add Mug x1
  lines = {Mug=1}
```

الـ `add` اتسجلت، والـ `remove` و `lines` اتحولوا للسلة الأصلية من غير ما نكتبهم.

```
  vip.name()  = VIP customer
  vip.greet() = Hello, customer
```

الفخ: `greet` اتحولت لـ `BasicGreeter`، وهو نادى `name()` بتاعته هو.

### [L02_PropertyDelegates.kt](../src/main/kotlin/m12_delegation/L02_PropertyDelegates.kt)

```
  quantity = 3
```

القيمة 50 والقيمة 0 اترفضوا من الـ `vetoable`، والكمية فضلت 3.

```
    address: '' -> 'Maadi'
    address: 'Maadi' -> 'Maadi, Street 9'
```

الـ `observable` بلّغ عن كل تغيير بالقيمة القديمة والجديدة واسم الـ property.

```
  Pen costs 500
```

الـ properties اتقرت من map باسمها.

### [L03_CustomDelegate.kt](../src/main/kotlin/m12_delegation/L03_CustomDelegate.kt)

```
  name = 'Sara Ahmed'
```

الـ `Trimmed` شال المسافات وقت الكتابة.

```
    age set to 13
    age set to 30
    age set to 120
```

الـ `Clamped` delegate بياخد parameters، وبيحط القيمة جوه المدى المسموح. القاعدة مكتوبة مرة واحدة وتنفع لأي property.

### [L04_StandardDelegates.kt](../src/main/kotlin/m12_delegation/L04_StandardDelegates.kt)

```
  currencyCode = USD
```

الكتابة في الاسم القديم وصلت للاسم الجديد. مفيش قيمتين ممكن يختلفوا.

```
  read before set: IllegalStateException
  userId = 77
```

الـ `notNull` بيرمي exception واضح لو اتقرى بدري، وبيشتغل مع `Int` اللي `lateinit` مش بيقبله.

## في Compose

### الـ by remember { mutableStateOf() }

أشهر سطر في Compose هو delegation:

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

خلينا نفكّه حتة حتة:

- الـ `mutableStateOf(1)` factory بترجّع object من نوع `MutableState<Int>`، وده interface فيه `value`.
- الـ `remember { ... }` بتحفظ الـ object ده بين كل إعادة رسم والتانية، فمش بيتعمل من الأول.
- الـ `by` بتحوّل قراية وكتابة `qty` على الـ object ده.

بس ازاي `MutableState` ينفع يبقى delegate وهو مالوش `getValue`؟ الإجابة في extension functions:

```kotlin
public inline operator fun <T> State<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value
public inline operator fun <T> MutableState<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T)
```

الـ `State.getValue` و `MutableState.setValue` operators من نوع extension، موجودين في package الـ runtime. ده نفس اللي عملناه في L03 بس بـ extension بدل member. وده السبب في إن أحيانًا IntelliJ بيقولك تعمل import لـ `androidx.compose.runtime.getValue` و `setValue` لما تكتب `by`.

ومن غير `by` كان الكود هيبقى:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
val qty = remember { mutableStateOf(1) }
Text("${qty.value}")
Button(onClick = { qty.value++ }) { Text("+") }
```

الـ delegation بيشيل `.value` من كل مكان. والأهم إن كل قراية لـ `qty.value` (مباشرة أو من الـ delegate) هي اللي بتخلّي Compose يعرف إن الـ `Text` ده محتاج يتعاد رسمه لما القيمة تتغير.

### الـ collectAsStateWithLifecycle

نفس الفكرة مع الـ ViewModel:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun CartScreen(viewModel: CartViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Text("Items: ${state.items.size}")
}
```

الـ `collectAsStateWithLifecycle` extension على `StateFlow` بترجّع `State<T>`، والـ `by` بيخلّي `state` يتقرا كأنه قيمة عادية.

### الـ by viewModels()

في الـ Activity:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
class MainActivity : ComponentActivity() {
    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CartScreen(viewModel) }
    }
}
```

الـ `viewModels` extension بترجّع `Lazy<VM>`، و `Lazy` ليه `getValue` operator. يعني ده `by lazy` متقدم: الـ ViewModel مش بيتعمل غير أول مرة تقراه، ولما يتعمل بيجيب النسخة اللي عايشة من بعد الـ rotation بدل ما يعمل واحدة جديدة.

## أخطاء شائعة

1. الغلطة: افتكار إن الـ delegate هينادي الـ override بتاعك.
   الصح: الـ delegate بينادي نفسه. أي function محتاج سلوكك فيها لازم تعمل لها override.

2. الغلطة: محاولة `by` مع كلاس بدل interface.
   الصح: الـ class delegation بيشتغل مع interfaces بس.

3. الغلطة: `var x by mutableStateOf(0)` جوه composable من غير `remember`.
   الصح: من غير `remember` الحالة بتتعمل من الأول مع كل إعادة رسم وبتضيع.

4. الغلطة: نفس الـ setter بالـ validation مكتوب في عشر properties.
   الصح: اكتب delegate واحد بالقاعدة، والـ properties تحوّل عليه.

5. الغلطة: نسيان import لـ `getValue` و `setValue` في Compose والاستغراب من الـ error.
   الصح: الـ operators دي extensions ومحتاجة import من `androidx.compose.runtime`.

## أسئلة Interview

1. إيه هو الـ class delegation في Kotlin؟

<details><summary>الإجابة</summary>

إنك تكتب `: Interface by obj` فالـ compiler يولّد forwarding لكل function في الـ interface على `obj`، وانت تعمل override للي عايز تغيّره بس. ده composition من غير boilerplate.

</details>

2. إيه الفرق في السلوك بين delegation و inheritance لما الأصل ينادي function من نفسه؟

<details><summary>الإجابة</summary>

في الوراثة، `this` بيشاور على الابن، فالـ override بيتنادى. في الـ delegation، الـ delegate بينادي نسخته هو، والـ override بتاع الـ wrapper مش بيتنادى.

</details>

3. إيه المطلوب عشان object يبقى property delegate؟

<details><summary>الإجابة</summary>

يكون فيه operator اسمها `getValue`، وكمان `setValue` لو الـ property من نوع `var`. ممكن يكونوا members أو extensions، أو تنفذ `ReadOnlyProperty` أو `ReadWriteProperty`.

</details>

4. إيه الفرق بين `observable` و `vetoable`؟

<details><summary>الإجابة</summary>

الـ `observable` بيتنادى بعد التغيير وبس بيبلّغ. الـ `vetoable` بيتنادى قبل التغيير وبيرجّع Boolean يقرر يقبل ولا يرفض.

</details>

5. اشرح `var count by remember { mutableStateOf(0) }`.

<details><summary>الإجابة</summary>

الـ `mutableStateOf` بترجّع `MutableState`، والـ `remember` بتحفظه بين كل إعادة رسم، والـ `by` بيحوّل القراية والكتابة على `value` باستخدام extension operators `getValue` و `setValue`. وكل قراية بتسجّل إن الـ composable ده محتاج يتعاد رسمه لو القيمة اتغيرت.

</details>

6. ليه `by viewModels()` مش بيعمل ViewModel جديد بعد الـ rotation؟

<details><summary>الإجابة</summary>

لأنه بيرجّع `Lazy` بيجيب الـ ViewModel من الـ `ViewModelStore` بتاع الـ Activity، واللي بيفضل عايش بعد تغيير الـ configuration.

</details>

## Self-check

- [ ] أقدر أكتب wrapper بـ `by` بيغيّر function واحدة ويحوّل الباقي.
- [ ] أقدر أشرح ليه `vip.greet()` في L01 طبعت customer.
- [ ] أعرف أكتب delegate بـ operators وبـ `ReadWriteProperty`.
- [ ] أعرف أختار الـ delegate الجاهز المناسب من الجدول.
- [ ] أقدر أشرح كل كلمة في `var qty by remember { mutableStateOf(1) }`.
- [ ] ملف Exercises.kt بيطبع ALL PASS

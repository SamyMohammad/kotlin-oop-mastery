# 02 - Encapsulation and Visibility

## المشكلة الأول

السلة في الشوب ليها قواعد واضحة:

- كل سطر في السلة لازم الكمية بتاعته أكبر من صفر.
- المنتج الواحد مايظهرش في سطرين، لو اتضاف تاني الكمية بتزيد.

لو السلة شكلها كده:

```kotlin
class Cart {
    val items = mutableListOf<CartLine>()
}

cart.items.add(CartLine(productId = 1, qty = -4))   // كمية سالبة
cart.items.add(CartLine(productId = 1, qty = 2))    // نفس المنتج مرتين
cart.items.clear()                                  // أي حد يمسح السلة
```

القواعد دي اتكسرت من غير ما الكلاس يعرف. والأسوأ إنك لما تلاقي bug في الإجمالي، مش هتعرف مين اللي غيّر الـ list، لأن أي سطر في المشروع ممكن يكون هو.

الـ encapsulation معناه إن **الكلاس هو الوحيد اللي يقدر يغيّر حالته**، وبالتالي هو الوحيد المسؤول عن القواعد.

## الـ concept بالتفصيل

### الـ encapsulation مش معناه "خلي كل حاجة private"

كتير بيفهموا الـ encapsulation على إنه كلمة `private` قبل كل field. ده مجرد أداة. الهدف الحقيقي هو حماية الـ **invariants**، يعني القواعد اللي لازم تفضل صح طول عمر الـ object:

- رصيد المحفظة عمره ما يبقى سالب.
- كل سطر في السلة كميته موجبة.
- الـ history بتاع المعاملات مايتغيرش من برّه.

لو القاعدة محمية جوه الكلاس، يبقى عندك مكان واحد تراجعه لما يحصل bug.

### الـ visibility modifiers الأربعة

| الكلمة | مين يشوف الـ member | مين يشوف الـ top-level declaration |
|---|---|---|
| الكلمة `public` | أي حد (ده الافتراضي) | أي حد |
| الكلمة `internal` | أي كود في نفس الـ Gradle module | أي كود في نفس الـ Gradle module |
| الكلمة `protected` | الكلاس والكلاسات اللي بتورث منه | مش موجودة للـ top-level |
| الكلمة `private` | الكلاس نفسه بس | الملف نفسه بس |

ملاحظات مهمة:

- الـ `internal` مقصود بيه الـ module زي مكتبة أو feature module في Android. المشروع ده كله module واحد، فـ `internal` هنا شبه `public`.
- الـ `private` على مستوى الـ top-level معناها "جوه الملف ده بس"، مش جوه الـ package. ده بديل Kotlin لـ package-private في Java.
- الـ `protected` في Kotlin أضيق من Java. في Java الكلاسات اللي في نفس الـ package بتشوفه، في Kotlin لأ.

### الافتراضي في Kotlin مقابل Java

| الحاجة | في Kotlin | في Java |
|---|---|---|
| الـ visibility الافتراضية | يعني `public` | يعني package-private |
| فيه package-private؟ | لأ | آه |
| الـ protected بيتشاف في نفس الـ package؟ | لأ | آه |

### الـ private set

أبسط طريقة تخلي property للقراية بس من برّه:

```kotlin
var balanceCents: Long = 0
    private set
```

أي حد يقرا `wallet.balanceCents`، بس `wallet.balanceCents = 0` مش هتعمل compile برّه الكلاس.

### الـ backing property

لما الحالة نفسها نوعها mutable، زي `MutableList` أو `MutableStateFlow`، بنستخدم convention مشهور جدًا:

```kotlin
private val _items = mutableListOf<CartLine>()   // التخزين الحقيقي
val items: List<CartLine> get() = _items.toList() // الوش اللي بيشوفه الناس
```

الاسم اللي بيبدأ بـ underscore هو النسخة الـ mutable المستخبية، والاسم من غير underscore هو النسخة اللي بتتعرض.

### الفخ: `List` مش immutable

ده من أهم الحاجات في الـ module ده. النوع `List` في Kotlin معناه **read-only view**، مش list مستحيل تتغير:

```kotlin
class LeakyCart {
    private val _items = mutableListOf("Pen")
    val items: List<String> = _items      // نفس الـ object
}

(leakyCart.items as MutableList<String>).add("Stolen TV")  // اشتغلت
```

الـ `items` و `_items` هما نفس الـ object في الذاكرة. النوع بس اللي اتضيّق. أي حد يعمل cast يقدر يغيّر الداتا.

الحل إنك ترجّع **نسخة** باستخدام `toList()`. ساعتها حتى لو حد عمل cast وغيّر، هو بيغيّر نسخته هو بس.

نقطة دقيقة: الـ `toList()` لو الـ list فيها عنصر واحد أو مفيهاش عناصر بترجع list مش بتقبل تعديل أصلًا، فالـ cast والإضافة هيضربوا `UnsupportedOperationException`. في الحالتين الداتا الأصلية آمنة.

### الـ defensive copies

النسخ بيتعمل في اتجاهين:

1. وانت بتستقبل داتا من برّه: لو حد ادّاك `MutableList` في الـ constructor واحتفظت بيها زي ما هي، هو لسه ماسك reference ويقدر يغيّرها بعدين. الحل `initial.toMutableList()`.
2. وانت بتطلّع داتا لبرّه: ترجّع `toList()` بدل الـ list الأصلية.

### الـ encapsulation والـ methods

بدل ما تعرض الحالة وتسيب الناس يغيّروها، اعرض **عمليات** ليها معنى:

```kotlin
// غلط: بيعرض الحالة
cart.items.add(CartLine(1, 2))

// صح: عملية ليها اسم وقاعدة
cart.add(productId = 1, qty = 2)
```

الـ `add` هنا بتعمل validation، وبتدمج السطور المتكررة، وبتضمن الـ invariants. اللي بينادي مش محتاج يعرف أي حاجة عن التفاصيل دي.

## امشي مع الكود

### [L01_VisibilityModifiers.kt](../src/main/kotlin/m02_encapsulation/L01_VisibilityModifiers.kt)

```
  nickname=S, lastLoginDay=14
  password ok? true
```

الـ `nickname` عامة والـ `lastLoginDay` من نوع `internal`، والاتنين اتغيروا عادي من `main`. أما `passwordHash` فهي `private`، والطريقة الوحيدة للتعامل معاها هي `checkPassword`.

```
  Sara has 100 points
```

الكلاس `VipAccount` ورث من `Account` وقدر يزوّد `points` لأنها `protected`. من `main` نفسها مكناش هنقدر نوصلها.

```
  Kotlin Shop total: EGP 123.45
```

الـ function اللي اسمها `formatCents` نوعها `private` على مستوى الملف، فمحدش برّه الملف ده يقدر يستخدمها.

### [L02_PrivateSetAndBackingProperty.kt](../src/main/kotlin/m02_encapsulation/L02_PrivateSetAndBackingProperty.kt)

```
  count = 2
```

الـ `count` بيتقرا من برّه، بس الزيادة بتحصل بـ `increment()` بس.

```
  leaky cart now has: [Pen, Stolen TV]
   >> The cast worked because items IS the private MutableList.
```

هنا الفخ بالظبط: النوع `List` ماحماش الداتا لأن الـ object هو هو.

```
  snapshot the caller changed: [Pen, Mug, Stolen TV]
  safe cart still has:         [Pen, Mug]
```

مع `toList()` اللي بينادي غيّر نسخته هو، والسلة الأصلية فضلت سليمة.

### [L03_InvariantsAndDefensiveCopies.kt](../src/main/kotlin/m02_encapsulation/L03_InvariantsAndDefensiveCopies.kt)

```
  items = [CartLine(productId=1, qty=5), CartLine(productId=2, qty=1)]
  total qty = 6
  rejected: qty must be positive
```

المنتج رقم 1 اتضاف مرتين واتدمج في سطر واحد كميته 5، والكمية صفر اترفضت. الـ invariants محمية من جوه.

```
  caller's list = []
  wishlist      = [Mug, Pen]
```

اللي بينادي مسح الـ list بتاعته بعد ما عمل الـ `Wishlist`، بس الـ wishlist ماتأثرتش لأنها عملت نسخة وقت الإنشاء.

```
  wishlist      = [Mug, Pen, Cap]
```

التكرار والاسم الفاضي اتجاهلوا، لأن الـ object هو اللي بيقرر إيه اللي صح.

## في Compose

أشهر مثال للـ encapsulation في Android كله هو شكل الـ ViewModel الحديث:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
data class CartUiState(
    val items: List<CartLine> = emptyList(),
    val isLoading: Boolean = false,
)

class CartViewModel : ViewModel() {

    // التخزين الحقيقي: mutable ومستخبي
    private val _uiState = MutableStateFlow(CartUiState())

    // الوش اللي الشاشة بتشوفه: read-only
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    // العمليات الوحيدة المسموح بيها
    fun addToCart(productId: Int) {
        _uiState.update { state ->
            state.copy(items = state.items + CartLine(productId, 1))
        }
    }
}

@Composable
fun CartScreen(viewModel: CartViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Text("Items: ${state.items.size}")
    Button(onClick = { viewModel.addToCart(1) }) { Text("Add pen") }
}
```

خلي بالك من التفاصيل دي:

- الـ `MutableStateFlow` هو التخزين الحقيقي، ومكتوب `private` باسم يبدأ بـ underscore. ده نفس الـ backing property اللي في L02.
- الـ `StateFlow` هو النوع اللي بيتعرض للشاشة. الـ interface ده مفيهوش أي طريقة للكتابة، فيه `value` للقراية بس.
- الـ `asStateFlow` بترجّع wrapper للقراية بس. ليه مش كفاية نكتب `val uiState: StateFlow<CartUiState> = _uiState`؟ لنفس سبب الفخ في L02: الشاشة ممكن تعمل cast لـ `MutableStateFlow` وتكتب فيه. الـ wrapper بيقفل الباب ده تمامًا.
- الشاشة مش بتغيّر الحالة بنفسها. هي بتنادي `addToCart` وبس. ده بالظبط "اعرض عمليات مش حالة".

### ليه ده مهم في Compose بالذات؟

في Compose الـ UI بيتعاد رسمه كل ما الحالة تتغير. لو أي composable يقدر يكتب في الحالة، هيبقى صعب جدًا تعرف مين غيّر إيه، وممكن يحصل loop: الشاشة تكتب، الحالة تتغير، الشاشة تترسم تاني وتكتب تاني. الـ encapsulation بيخلي التغيير يمشي في اتجاه واحد بس:

```
UI event  ->  ViewModel function  ->  _uiState.update  ->  uiState  ->  UI
```

ده اسمه unidirectional data flow، وهو أساس أي شاشة Compose نظيفة.

## أخطاء شائعة

1. الغلطة: `val items: List<T> = _items` وانت فاكر إن الـ `List` حامياك.
   الصح: ارجع `_items.toList()`، أو استخدم collections مستحيل تتغير.

2. الغلطة: عرض `MutableStateFlow` للشاشة مباشرة.
   الصح: اعمله `private` واعرض `asStateFlow()`.

3. الغلطة: الاحتفاظ بالـ list اللي جاتلك في الـ constructor زي ما هي.
   الصح: اعمل نسخة بـ `toMutableList()` عشان اللي بعتهالك مايقدرش يغيّرها بعدين.

4. الغلطة: كتابة getter و setter لكل field زي Java.
   الصح: الـ property في Kotlin بتعمل ده لوحدها. استخدم `private set` لما تحتاج تمنع الكتابة.

5. الغلطة: استخدام `internal` وانت فاكره زي package-private.
   الصح: الـ `internal` معناه الـ module كله. لو عايز تقفل على ملف واحد استخدم `private` على مستوى الـ top-level.

6. الغلطة: الـ validation برّه الكلاس، في الشاشة مثلًا.
   الصح: القاعدة مكانها جوه الكلاس اللي بيملك الداتا.

## أسئلة Interview

1. إيه الهدف الحقيقي من الـ encapsulation؟

<details><summary>الإجابة</summary>

حماية الـ invariants، يعني القواعد اللي لازم تفضل صحيحة للـ object. الـ `private` مجرد وسيلة. النتيجة إن فيه مكان واحد بس مسؤول عن الحالة وقواعدها.

</details>

2. إيه الفرق بين `internal` و `private` على مستوى الـ top-level؟

<details><summary>الإجابة</summary>

الـ `internal` بيتشاف في الـ Gradle module كله. الـ `private` على مستوى الـ top-level بيتشاف في الملف ده بس.

</details>

3. هل `List` في Kotlin immutable؟

<details><summary>الإجابة</summary>

لأ. هي read-only interface. الـ object اللي وراها ممكن يكون `MutableList`، وأي حد معاه reference ليه بنوعه الحقيقي أو بيعمل cast يقدر يغيّره.

</details>

4. ليه بنكتب `_uiState` و `uiState` في الـ ViewModel؟

<details><summary>الإجابة</summary>

ده backing property pattern. النسخة الـ mutable `private` عشان الـ ViewModel بس يكتب فيها، والنسخة اللي بتتعرض نوعها `StateFlow` عشان الشاشة تقرا بس.

</details>

5. ليه `asStateFlow()` أحسن من إنك تكتب النوع `StateFlow` بس؟

<details><summary>الإجابة</summary>

لأن تضييق النوع بس بيسيب نفس الـ object، وحد ممكن يعمل cast لـ `MutableStateFlow` ويكتب. الـ `asStateFlow()` بترجّع wrapper مالوش أي طريقة للكتابة.

</details>

6. إيه الفرق بين `protected` في Kotlin و Java؟

<details><summary>الإجابة</summary>

في Java الـ `protected` بيتشاف في الكلاسات اللي بتورث وكمان في نفس الـ package. في Kotlin بيتشاف في الكلاسات اللي بتورث بس.

</details>

## Self-check

- [ ] أقدر أشرح الفرق بين الأربع visibility modifiers بجدول من دماغي.
- [ ] أقدر أشرح ليه `LeakyCart` في L02 اتسرقت و `SafeCart` لأ.
- [ ] أعرف أكتب backing property صح لـ list ولـ state flow.
- [ ] أعرف إمتى أعمل defensive copy وأنا بستقبل الداتا وإمتى وأنا بطلّعها.
- [ ] أقدر أكتب ViewModel فيه `_uiState` و `uiState` وأشرح ليه.
- [ ] ملف Exercises.kt بيطبع ALL PASS

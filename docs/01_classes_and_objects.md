# 01 - Classes and Objects

## المشكلة الأول

عندك منتج في الشوب: له رقم واسم وسعر ومخزون. أول نسخة بتكتبها غالبًا شكلها كده:

```kotlin
class Product {
    var id = 0
    var name = ""
    var priceCents = 0L
    var stock = 0
}

val p = Product()
p.name = ""          // اسم فاضي
p.priceCents = -500  // سعر بالسالب
p.stock = -3         // مخزون بالسالب
```

الكلاس ده مجرد شنطة. ممكن يتعمل منه object نصه فاضي ونصه غلط، ومفيش أي حاجة بتمنع ده. في الـ module ده هتتعلم إزاي تبني كلاس **مايتولدش غلط من الأول**، وإزاي كل property يحمي نفسه.

## الـ concept بالتفصيل

### الـ primary constructor

في Kotlin الـ constructor الأساسي مكتوب في رأس الكلاس نفسه:

```kotlin
class Customer(
    val name: String,            // parameter + property للقراية بس
    var email: String,           // parameter + property للقراية والكتابة
    phone: String,               // parameter بس، مش property
    val isVip: Boolean = false,  // قيمة افتراضية
)
```

الفرق المهم هنا:

- لو كتبت `val` أو `var` قبل الاسم، الـ parameter بيبقى property في الكلاس وتقدر توصله من برّه.
- لو ماكتبتش، ده parameter عادي. موجود بس وقت إنشاء الـ object، وتقدر تستخدمه في `init` أو في initializer لـ property تانية.

ده بيوفّر عليك كود كتير كان بيتكتب في Java: الـ field والـ constructor والـ getter والـ setter، كلهم في سطر واحد.

### الـ default والـ named arguments

بدل ما تكتب أربع constructors زي Java، بتحط قيم افتراضية:

```kotlin
val omar = Customer(name = "Omar", phone = "011...", email = "o@mail.com", isVip = true)
```

الـ named arguments بتخلي الكود يتقري لوحده، وتقدر ترتّبهم زي ما تحب. ده من أكتر الحاجات اللي بتخلي الـ Builder pattern نادر في Kotlin (هتشوف ده في الدرس 18).

### الـ secondary constructor

لو محتاج طريقة تانية لإنشاء الـ object، بتكتب `constructor` جوه الكلاس. القاعدة الأساسية: **لازم يحوّل على الـ primary** باستخدام `this(...)`:

```kotlin
constructor(name: String) : this(name, email = "guest@shop.local", phone = "000")
```

ده بيضمن إن أي object، مهما اتعمل بأي طريقة، هيعدّي على نفس الـ validation اللي في الـ primary.

### الـ init block والـ validation

الـ `init` block بيتنفذ كجزء من الـ primary constructor. ده المكان الطبيعي للـ validation:

```kotlin
class Product(val id: Int, val name: String, priceCents: Long) {
    init {
        require(priceCents >= 0) { "price must not be negative" }
    }
}
```

الـ `require` بترمي `IllegalArgumentException` لو الشرط مش متحقق. يعني مستحيل يتعمل `Product` بسعر سالب. الـ object يا إما يتولد سليم، يا إما مايتولدش خالص.

### ترتيب الـ initialization

ده من أكتر الأسئلة اللي بتيجي في الـ interviews. الترتيب كالآتي:

1. الـ arguments بتتحسب وبتتباصى للـ primary constructor.
2. جسم الكلاس بيتنفذ من فوق لتحت: كل property initializer وكل `init` block بالترتيب اللي مكتوبين بيه.
3. لو الـ object اتعمل عن طريق secondary constructor، جسمه بيتنفذ في الآخر.

```
primary args
   |
   v
property / init / property / init ...   (top to bottom)
   |
   v
secondary constructor body (if used)
```

الفخ هنا: لو `init` block قرا property مكتوبة تحته، الـ compiler بيمنعك لو قريتها مباشرة. بس لو قريتها من خلال function، الـ compiler مش هيلاحظ، وهتلاقي القيمة null أو صفر. الحل البسيط: **خلي الـ init blocks بعد الـ properties اللي بتستخدمها**.

### الـ property مش field

في Java بتكتب field وبعدين getter و setter. في Kotlin الـ property هي الثلاثة مع بعض:

```kotlin
var stock: Int = 0
```

الـ compiler بيعمل منها field مستخبي اسمه `stock`، و `getStock()` و `setStock()`. لما تكتب `item.stock = 5` انت فعليًا بتنادي الـ setter.

### الـ custom getter

لو كتبت getter من غير قيمة ابتدائية، الـ property بتبقى محسوبة كل مرة ومفيش field خالص:

```kotlin
val priceLabel: String
    get() = "EGP %d.%02d".format(priceCents / 100, priceCents % 100)
```

كل مرة تقرا `priceLabel` الـ getter بيتنفذ من الأول. ده مناسب لحاجة رخيصة الحساب ومشتقة من داتا تانية.

### الـ custom setter والـ `field`

الكلمة `field` جوه الـ setter معناها الـ backing field المستخبي:

```kotlin
var stock: Int = 0
    set(value) {
        if (value >= 0) field = value
    }
```

خلي بالك: لو كتبت `stock = value` جوه الـ setter، انت بتنادي الـ setter نفسه تاني، وده loop لا نهائي وهيضرب `StackOverflowError`.

### الـ private set

```kotlin
var soldCount: Int = 0
    private set
```

أي حد يقدر يقرا القيمة، بس الكلاس بس هو اللي يقدر يغيّرها. ده من أهم أدوات الـ encapsulation وهتشوفه تاني في الدرس 02.

### الـ const val

الـ `const val` قيمة معروفة وقت الـ compile، والـ compiler بيحطها مكان أي استخدام ليها. ليها شروط: لازم تكون top-level أو جوه `object` أو `companion object`، ونوعها primitive أو `String`.

### الـ lateinit

```kotlin
lateinit var userName: String
```

ده وعد منك للـ compiler: "القيمة دي هتتحط قبل ما حد يقراها". شروطه:

- لازم يكون `var`.
- النوع مش nullable.
- النوع مش primitive، يعني مينفعش `lateinit var count: Int`.

لو قريته قبل ما يتحط، هيضرب `UninitializedPropertyAccessException`. وتقدر تتأكد منه بـ `this::userName.isInitialized`.

### الـ lazy

```kotlin
val searchIndex: Map<Char, List<String>> by lazy {
    names.groupBy { it.first().lowercaseChar() }
}
```

الكود جوه `lazy` مش بيتنفذ غير أول مرة حد يقرا القيمة، وبعدها النتيجة بتتحفظ. ده مناسب لحاجة تقيلة ممكن ماحدش يحتاجها أصلًا.

بشكل افتراضي `lazy` آمن مع الـ threads (`LazyThreadSafetyMode.SYNCHRONIZED`). لو متأكد إن thread واحد بس هيقراها، ممكن تستخدم `LazyThreadSafetyMode.NONE` عشان تكون أسرع.

### إمتى `lateinit` وإمتى `lazy`؟

| السؤال | الاختيار `lateinit` | الاختيار `lazy` |
|---|---|---|
| مين بيحدد القيمة؟ | حد من برّه بيحطها بعدين | الكلاس نفسه بيحسبها |
| نوع الـ property | يعني `var` | يعني `val` |
| ينفع primitive؟ | لأ | آه |
| مثال | قيمة جاية من lifecycle أو DI | حاجة تقيلة بتتحسب مرة واحدة |

## امشي مع الكود

### [L01_Constructors.kt](../src/main/kotlin/m01_classes/L01_Constructors.kt)

```
  Sara <sara@mail.com> phone=********678 vip=false
```

الـ `phone` parameter عادي، فاستخدمناه في initializer عشان نعمل `maskedPhone`، بس مش هتقدر تكتب `sara.phone`.

```
  Omar <omar@mail.com> phone=********432 vip=true
```

الـ named arguments اتكتبت بترتيب مختلف عن ترتيب التعريف، وده عادي جدًا.

```
  secondary constructor ran for Guest
  Guest <guest@shop.local> phone=000 vip=false
```

الـ secondary constructor حوّل على الـ primary بقيم افتراضية، وبعدين جسمه اتنفذ.

```
  Pen costs 900 cents after 10% off
  rejected: price must not be negative
```

الـ `Product` في الشوب بيرفض أي سعر سالب من الـ `init`، والسعر نفسه `private set` فمحدش يغيّره غير `applyDiscount`.

### [L02_InitOrder.kt](../src/main/kotlin/m01_classes/L02_InitOrder.kt)

```
  1. property `first` initializer
  2. first init block
     label inside first init = null
  3. property `second` initializer
  4. second init block, label = order-7
```

كل حاجة اتنفذت من فوق لتحت بالظبط. وفي الـ init الأول، الـ `label` كان لسه null لأنه متعرّف تحته.

```
  4. second init block, label = order-8
  5. secondary constructor body (gift)
```

لما استخدمنا الـ secondary constructor، جسمه اتنفذ في الآخر خالص بعد كل الـ initialization.

### [L03_Properties.kt](../src/main/kotlin/m01_classes/L03_Properties.kt)

```
  EGP 45.50
  EGP 45.50
  getter ran 2 times
```

الـ getter المحسوب بيتنفذ مع كل قراية. لو الحساب تقيل، ده مؤشر إنك محتاج `lazy` أو property مخزّنة.

```
     rejected stock = -4
  stock = 10
```

الـ setter رفض القيمة السالبة والـ `field` فضل زي ما هو.

```
  stock = 7, sold = 3
```

الـ `soldCount` اتغير من جوه `sell` بس. من برّه الكلاس مقدرتش تكتب فيه.

### [L04_LateinitAndLazy.kt](../src/main/kotlin/m01_classes/L04_LateinitAndLazy.kt)

```
  ready? false
  reading too early threw UninitializedPropertyAccessException
  ready? true
  Welcome, Sara
```

القراية قبل الوقت ضربت exception واضح بدل `NullPointerException` غامض.

```
  catalog created, index not built yet
     computing search index...
  p -> [Pen, Paper]
  m -> [Mug]
```

الـ index اتحسب أول ما احتجناه بس، ومرة واحدة رغم إننا قريناه مرتين.

## في Compose

أي شاشة في Compose بتعرض داتا جاية من classes زي اللي اتعلمتها. الـ model ده مثلًا:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
class Product(val id: Int, val name: String, priceCents: Long) {
    init { require(priceCents >= 0) }

    var priceCents: Long = priceCents
        private set

    val priceLabel: String
        get() = "EGP %d.%02d".format(priceCents / 100, priceCents % 100)
}

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(text = product.name)
            Text(text = product.priceLabel)
        }
    }
}
```

لاحظ الحاجات دي:

- الـ composable اللي اسمه `Card` و الـ composable اللي اسمه `Text` بيقروا properties بس. الـ UI مش بيغيّر الـ model.
- الـ `priceLabel` getter محسوب، فـ `ProductCard` دايمًا بيعرض السعر الصح حتى لو الخصم اتغير.
- الـ `ProductCard` نفسها function فيها default argument زي اللي اتعلمته في L01: `modifier: Modifier = Modifier`. ده convention رسمي في Compose، كل composable عام لازم يقبل `modifier` بقيمة افتراضية.
- الـ named arguments زي `text = product.name` منتشرة جدًا في Compose لأن الـ composables فيها parameters كتير بقيم افتراضية.

### ليه الـ model في Compose لازم يكون read-only؟

لو الـ `Product` فيه `var` عام والـ UI غيّره، Compose مش هيعرف إن حاجة اتغيرت ومش هيعيد الرسم. القاعدة إن الـ UI بياخد داتا للقراية بس، والتغيير بيحصل في مكان واحد (ViewModel) وبيطلع object جديد. هتفهم السبب بالتفصيل في الدرس 07 (data classes والـ stability).

### الـ lazy والـ lateinit في Android

- الـ `lateinit` منتشر في كود Android القديم مع الـ Views، زي `lateinit var binding: ActivityMainBinding` اللي بيتحط في `onCreate`.
- الـ `lazy` بيتستخدم لحاجات تقيلة في الـ ViewModel أو الـ repository، زي parser أو formatter.
- في Compose نفسه، المكافئ لـ "احسب مرة واحدة واحفظها" جوه الـ composition هو `remember`، وهتشوفه بالتفصيل في الدرس 12.

## أخطاء شائعة

1. الغلطة: كتابة `stock = value` جوه الـ setter.
   الصح: استخدم `field = value`، وإلا هيبقى loop لا نهائي.

2. الغلطة: الـ validation في function منفصلة بتتنادى "لما حد يفتكر".
   الصح: حط الـ validation في `init` عشان كل object يتأكد منه وقت ولادته.

3. الغلطة: `init` block بيقرا property متعرّفة تحته.
   الصح: رتّب الكلاس بحيث الـ properties تيجي الأول وبعدها الـ init اللي بيستخدمها.

4. الغلطة: `lateinit var count: Int`.
   الصح: الـ `lateinit` مش بينفع مع primitives. استخدم قيمة ابتدائية أو `Delegates.notNull()`.

5. الغلطة: getter محسوب بيعمل شغل تقيل وبيتقرا جوه loop.
   الصح: لو الحساب تقيل والقيمة مش بتتغير، استخدم `lazy` أو property مخزّنة.

6. الغلطة: كتابة secondary constructors كتير بدل default arguments.
   الصح: في Kotlin الـ default والـ named arguments بيغنوا عن أغلب الـ overloads.

## أسئلة Interview

1. إيه الفرق بين parameter و property في الـ primary constructor؟

<details><summary>الإجابة</summary>

لو قبله `val` أو `var` يبقى property موجودة طول عمر الـ object. لو من غيرهم يبقى parameter موجود بس وقت الإنشاء، وتقدر تستخدمه في `init` والـ initializers.

</details>

2. رتّب خطوات الـ initialization في كلاس فيه properties و init blocks و secondary constructor.

<details><summary>الإجابة</summary>

الأول الـ arguments للـ primary، بعدها جسم الكلاس من فوق لتحت (properties و init blocks بترتيب كتابتهم)، وفي الآخر جسم الـ secondary constructor لو اتستخدم.

</details>

3. إمتى property بيبقى ليها backing field وإمتى لأ؟

<details><summary>الإجابة</summary>

الـ backing field بيتعمل لو الـ property ليها قيمة ابتدائية أو لو الـ getter أو الـ setter استخدموا `field`. لو الـ getter محسوب ومفيش `field`، مفيش field خالص.

</details>

4. إيه الفرق بين `lateinit` و `lazy`؟

<details><summary>الإجابة</summary>

الـ `lateinit` لـ `var` بتتحط قيمتها من برّه بعدين، ومش بينفع مع primitives أو nullable. الـ `lazy` لـ `val` الكلاس بيحسبها بنفسه أول ما حد يقراها، ومرة واحدة بس.

</details>

5. ليه الـ secondary constructor لازم يحوّل على الـ primary؟

<details><summary>الإجابة</summary>

عشان أي object يعدّي على نفس الـ initialization والـ validation اللي في الـ primary والـ init blocks. ده بيضمن إن مفيش طريقة تعمل object من غير ما يتأكد منه.

</details>

6. هل `lazy` آمن مع الـ threads؟

<details><summary>الإجابة</summary>

آه بشكل افتراضي، لأنه بيستخدم `SYNCHRONIZED`. وتقدر تختار `PUBLICATION` أو `NONE` لو عارف ظروف الاستخدام.

</details>

7. إيه الفرق بين `const val` و `val`؟

<details><summary>الإجابة</summary>

الـ `const val` قيمتها معروفة وقت الـ compile وبتتحط مكان الاستخدام مباشرة، ولازم تكون primitive أو `String` وفي top-level أو object. الـ `val` العادية بتتحسب وقت التشغيل.

</details>

## Self-check

- [ ] أقدر أكتب كلاس بـ primary constructor فيه `val` و `var` و parameter عادي وأشرح الفرق.
- [ ] أقدر أتوقع ترتيب الطباعة في L02 من غير ما أشغّله.
- [ ] أعرف إمتى أستخدم `field` وليه `stock = value` جوه الـ setter غلط.
- [ ] أعرف أختار بين `lateinit` و `lazy`.
- [ ] أقدر أكتب `ProductCard` بتعرض model read-only وأشرح ليه.
- [ ] ملف Exercises.kt بيطبع ALL PASS

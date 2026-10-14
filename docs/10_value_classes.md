# 10 - Value Classes

## المشكلة الأول

بص على الـ function دي:

```kotlin
fun addToCart(productId: Int, qty: Int)
```

وبعدين حد كتب:

```kotlin
addToCart(qty, productId)
```

الكود عمل compile واشتغل. والنتيجة؟ طلبنا 1042 قطعة من المنتج رقم 2. الاتنين `Int` فالـ compiler مش قادر يفرّق.

ونفس الحاجة مع الفلوس: السعر `Long`، والخصم `Long`، ورقم الأوردر `Long`. أي واحد ممكن يتحط مكان التاني. المشكلة دي ليها اسم: **primitive obsession**، يعني إنك بتمثّل مفاهيم مختلفة في الـ domain بنفس النوع البدائي.

الحل الطبيعي إنك تعمل كلاس لكل مفهوم: `ProductId` و `Quantity` و `Price`. بس كلاس عادي معناه object جديد في الذاكرة لكل رقم، وده مكلف في حاجات بتتعمل ملايين المرات (زي الأبعاد في الـ UI). الـ value class بيديك الأمان ده **من غير التكلفة**.

## الـ concept بالتفصيل

### التعريف

```kotlin
@JvmInline
value class ProductId(val value: Int)

@JvmInline
value class Price(val cents: Long) {
    init { require(cents >= 0) }
    operator fun plus(other: Price) = Price(cents + other.cents)
    fun format() = "EGP %d.%02d".format(cents / 100, cents % 100)
}
```

القواعد:

- لازم property واحدة بس في الـ primary constructor، ونوعها `val`.
- الـ annotation اللي اسمها `@JvmInline` لازمة على الـ JVM.
- ينفع يكون فيه `init` و functions و operators و properties محسوبة (من غير backing field).
- ينفع ينفذ interfaces، بس مينفعش يورث من كلاس ولا حد يورث منه.
- مالوش هوية: المساواة بالقيمة، و `===` ممنوعة.

### الأمان وقت الـ compile

```kotlin
fun add(productId: ProductId, qty: Quantity)

add(Quantity(2), ProductId(1042))
// error: Argument type mismatch: actual type is 'Quantity', but 'ProductId' was expected.
```

الغلطة اللي كانت bug وقت التشغيل بقت error وقت الـ compile. ودي أرخص لحظة ممكنة تكتشف فيها غلطة.

### التكلفة صفر (في أغلب الأحوال)

الـ compiler بيشيل الغلاف في الـ bytecode، فالـ `Price` بيبقى `long` عادي:

```kotlin
fun pay(p: Price)        // في الـ bytecode بتاخد long
val p = Price(500)       // مفيش object، مجرد long
```

### إمتى الغلاف بيرجع (boxing)؟

| الاستخدام | بيعمل object؟ |
|---|---|
| متغير أو parameter من نفس النوع | لأ |
| النوع nullable زي `Price?` | آه |
| جوه generics زي `List<Price>` | آه |
| مستخدم كـ `Any` أو كـ interface | آه |

يعني الـ boxing بيحصل على "الأطراف" بس. في أغلب الكود الحسابي هو long عادي.

### الأسماء المشوهة (name mangling)

عشان `pay(p: Price)` و `pay(cents: Long)` الاتنين بقوا `long` في الـ bytecode، الـ compiler بيغيّر اسم الأولى لحاجة زي `pay-XYZ123`. ده مش هيفرق معاك في Kotlin، بس معناه إن كود Java صعب يناديها مباشرة.

### الـ value class والـ data class والـ typealias

| السؤال | الـ value class | الـ data class | الـ typealias |
|---|---|---|---|
| بيعمل نوع جديد فعلًا؟ | آه | آه | لأ، مجرد اسم تاني |
| بيمنع خلط الأنواع؟ | آه | آه | لأ |
| عدد الـ properties | واحدة | أي عدد | ما ينطبقش |
| تكلفة وقت التشغيل | غالبًا صفر | الـ object كامل | صفر |
| ليه `copy` و `componentN`؟ | لأ | آه | ما ينطبقش |

الـ `typealias Price = Long` بيخلي الكود يتقري أحسن بس **مش** بيمنع إنك تباصي أي `Long`. لو الهدف الأمان، الـ typealias مش كفاية.

### إمتى تستخدم value class؟

- معرّفات: `ProductId`، `UserId`، `OrderId`.
- كميات بوحدات: `Price`، `Weight`، `Distance`.
- قيم ليها validation: `Email`، `PhoneNumber`، `Quantity`.
- قيم بتتعمل كتير جدًا في كود سريع، زي الأبعاد والألوان في الـ UI.

## امشي مع الكود

### [L01_TypeSafetyProblem.kt](../src/main/kotlin/m10_value_classes/L01_TypeSafetyProblem.kt)

```
  product 1042 x2
  product 2 x1042
```

نفس الـ function، الـ arguments اتبدلت، والنتيجة غلط تمامًا من غير أي تحذير.

```
  correct: 4000
  swapped: -4000
```

ونفس المشكلة مع الفلوس: سعر سالب طلع من طرح بالعكس.

### [L02_ValueClassBasics.kt](../src/main/kotlin/m10_value_classes/L02_ValueClassBasics.kt)

```
  product 1042 x2
```

الـ `TypedCart` بيقبل `ProductId` و `Quantity` بس. السطر المتعلّق عليه في الكود (اللي فيه التبديل) مش بيعمل compile.

```
  Quantity(0) rejected: quantity must be positive
```

الـ validation في الـ `init` شغال زي أي كلاس.

```
  4500 x 3 + 1000 = EGP 145.00
```

الـ operators خلت الحساب يتكتب طبيعي، وكله بأنواع آمنة.

### [L03_BoxingRules.kt](../src/main/kotlin/m10_value_classes/L03_BoxingRules.kt)

```
  a == b ? same
```

المساواة بالقيمة. والـ `===` مش بتعمل compile أصلًا.

```
  paying EGP 5.00
  nullable: EGP 7.00
  sum of list: EGP 19.00
```

نفس الكود شغال سواء الـ value class اتعمل لها boxing أو لأ. الفرق في الأداء بس، مش في السلوك.

```
  runtime class when boxed: Price
  toString: Price(cents=500)
```

لما اتحطت في `Any` بقت object حقيقي من نوع `Price`. والـ `toString` الافتراضي شكله زي الـ data class.

## في Compose

### الـ Dp: أشهر value class في Android

كل مقاس بتكتبه في Compose نوعه `Dp`:

```kotlin
@JvmInline
public value class Dp(public val value: Float) : Comparable<Dp> {
    public inline operator fun plus(other: Dp): Dp
    public inline operator fun times(other: Float): Dp
    // ...
}

public inline val Int.dp: Dp
```

فلما تكتب:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
Modifier.padding(16.dp)
```

الـ `16.dp` بيستخدم extension property اسمها `Int.dp` بترجّع `Dp`. والسطر ده مش بيعمل compile:

```kotlin
Modifier.padding(16)   // error: Int مش Dp
```

ده بالظبط L02: الـ compiler منعك تحط رقم من غير وحدة. في نظام الـ Views القديم كان كل حاجة `Int` أو `Float`، وكان سهل جدًا تخلط pixels مع dp مع sp.

وفي نفس الوقت، Compose بيعمل حسابات أبعاد ملايين المرات وهو بيرسم. لو كل `Dp` كان object، الـ garbage collector كان هيتعب جدًا. الـ value class بيخليه float عادي وقت التشغيل.

### الـ Color: ULong متخبي

```kotlin
@JvmInline
public value class Color(public val value: ULong)
```

اللون في Compose رقم واحد 64 bit فيه الأحمر والأخضر والأزرق والشفافية ونوع الـ color space كلهم متخزنين جنب بعض. الـ `Color` بيديك functions زي `red` و `alpha` و `copy(alpha = 0.5f)`، بس وقت التشغيل هو رقم واحد بس.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
val brand = Color(0xFF6200EE)
val faded = brand.copy(alpha = 0.4f)

@Composable
fun SaleBanner() {
    Box(Modifier.fillMaxWidth().height(48.dp).background(faded))
}
```

### الـ TextUnit: رقم ووحدة في Long واحد

الـ `TextUnit` بيشيل قيمة ووحدتها (`sp` أو `em`) في `Long` واحد. ده بيمنعك تحط `16.dp` مكان حجم خط، لأن `fontSize` نوعه `TextUnit` مش `Dp`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
Text("Price", fontSize = 18.sp)      // صح
Text("Price", fontSize = 18.dp)      // error: Dp مش TextUnit
```

### الـ Offset والقيمة الخاصة Unspecified

الـ `Offset` بيشيل x و y في `Long` واحد. وكتير من الـ value classes دي ليها قيمة خاصة في الـ companion زي `Dp.Companion.Unspecified` معناها "مفيش قيمة". ده بديل عن `Dp?` اللي كان هيعمل boxing (فاكر الجدول؟). يعني فريق Compose اختار قيمة خاصة بدل null عشان الأداء.

## أخطاء شائعة

1. الغلطة: `typealias UserId = String` وانت فاكر إنه بيحميك.
   الصح: الـ typealias اسم تاني بس. استخدم value class لو عايز الـ compiler يمنع الخلط.

2. الغلطة: value class فيها أكتر من property.
   الصح: لو محتاج أكتر من قيمة استخدم data class، أو اعمل packing زي `Offset` لو الأداء حرج جدًا.

3. الغلطة: استخدام `===` على value class.
   الصح: مالوش هوية. استخدم `==`.

4. الغلطة: `List<Dp?>` في كود رسم بيتنادى كتير.
   الصح: الـ nullable والـ generics بيعملوا boxing. استخدم قيمة خاصة زي `Unspecified`.

5. الغلطة: تحويل `Dp` لـ `Float` في كل حتة عشان تعمل حساب.
   الصح: الـ `Dp` فيه operators، اكتب `8.dp + 4.dp` مباشرة.

## أسئلة Interview

1. إيه هو الـ value class وليه بنستخدمه؟

<details><summary>الإجابة</summary>

غلاف حول قيمة واحدة بيعمل نوع جديد للـ compiler، فبيمنع خلط المفاهيم زي id وكمية. وفي أغلب الأحوال مابيعملش object وقت التشغيل، فالتكلفة صفر.

</details>

2. إمتى الـ value class بيتعمل له boxing؟

<details><summary>الإجابة</summary>

لما يبقى nullable، أو جوه generics، أو مستخدم كـ `Any` أو كـ interface.

</details>

3. إيه الفرق بين value class و typealias؟

<details><summary>الإجابة</summary>

الـ typealias مجرد اسم تاني لنفس النوع، فمش بيمنع أي خلط. الـ value class نوع جديد فعلًا، والـ compiler بيرفض إنك تحط نوع مكان التاني.

</details>

4. ليه `Dp` في Compose value class؟

<details><summary>الإجابة</summary>

عشان الأمان، فمينفعش تحط رقم من غير وحدة أو تخلط dp مع sp، وعشان الأداء، لأنه وقت التشغيل float عادي من غير objects، ودي حاجة مهمة جدًا في كود الرسم.

</details>

5. ليه الـ functions اللي بتاخد value class اسمها بيتغير في الـ bytecode؟

<details><summary>الإجابة</summary>

عشان الـ value class بيبقى نفس النوع البدائي في الـ bytecode، فمن غير تغيير الاسم كانت functions زي `pay(Price)` و `pay(Long)` هتتعارض.

</details>

## Self-check

- [ ] أقدر أشرح primitive obsession بمثال من الشوب.
- [ ] أعرف أكتب value class فيها validation و operators.
- [ ] أعرف جدول الـ boxing من دماغي.
- [ ] أعرف الفرق بين value class و data class و typealias.
- [ ] أقدر أشرح ليه `Modifier.padding(16)` مش بتعمل compile.
- [ ] ملف Exercises.kt بيطبع ALL PASS

# 06 - Polymorphism

## المشكلة الأول

لما الأوردر يتشحن، الشوب لازم يبلّغ العميل. أول نسخة من الكود بتبقى كده غالبًا:

```kotlin
fun notifyCustomer(channel: String, to: String, msg: String): String =
    when (channel) {
        "sms" -> "SMS to $to: $msg"
        "email" -> "EMAIL to $to: [Kotlin Shop] $msg"
        "push" -> "PUSH to device of $to: $msg"
        else -> error("unknown channel")
    }
```

كل قناة جديدة يعني تعديل في الـ `when` ده، وفي كل `when` تاني شبهه في المشروع (واحد للتكلفة، وواحد للـ logging، وواحد للـ retry...). والـ `else` بيخبي الغلط لحد وقت التشغيل.

الـ polymorphism بيقلب الفكرة: بدل ما **الكود اللي بينادي** يسأل "انت نوعك إيه؟" ويتصرف، **كل object يعرف يتصرف لوحده**:

```kotlin
for (n in notifiers) n.send("Sara", "your order shipped")
```

الـ loop ده مش هيتغير أبدًا، مهما ضفت قنوات.

## الـ concept بالتفصيل

### الكلمة نفسها

الكلمة Polymorphism معناها "أشكال كتير". في البرمجة معناها إن **نداء واحد** ممكن يتنفذ **بأكتر من طريقة** حسب الـ object. في Kotlin عندك نوعين:

| النوع | بيتحدد إمتى | بيتحدد على أساس إيه | المثال |
|---|---|---|---|
| الـ subtype polymorphism | وقت التشغيل | النوع الحقيقي للـ object | الـ override |
| الـ ad-hoc polymorphism | وقت الـ compile | الأنواع المكتوبة للـ arguments | الـ overloading |

وفيه نوع تالت اسمه parametric polymorphism وهو الـ generics (الدرس 13).

### الـ static type والـ runtime type

```kotlin
val n: SmsNotifier = UrgentSms()
```

السطر ده فيه نوعين:

- النوع الـ static هو `SmsNotifier`: ده اللي الـ compiler شايفه، وبيحدد إيه الـ functions اللي مسموح تناديها.
- النوع الـ runtime هو `UrgentSms`: ده الـ object الحقيقي في الذاكرة.

### الـ dynamic dispatch

لما تنادي member function زي `n.send(...)`، الـ JVM بيبص على **النوع الحقيقي** وقت التشغيل ويشغّل نسخته. بشكل مبسط: كل كلاس عنده جدول (اسمه vtable) فيه عنوان كل function، والـ override بيبدّل العنوان في جدول الكلاس الابن. النداء بيروح للجدول بتاع الـ object الحقيقي.

النتيجة: `n.send(...)` بتشغّل نسخة `UrgentSms` رغم إن المتغير مكتوب `SmsNotifier`.

### الفخ: الـ extensions مش polymorphic

الـ extension function مش جزء من الكلاس. هي function عادية static، الـ compiler بيختارها من **النوع المكتوب**:

```kotlin
fun SmsNotifier.kind() = "plain sms"
fun UrgentSms.kind() = "urgent sms"

val n: SmsNotifier = UrgentSms()
n.kind()     // "plain sms"
```

نفس الـ object، بس النتيجة اتحددت من النوع المكتوب. لو محتاج سلوك يتغير حسب النوع الحقيقي، لازم يكون member function مع `open` و `override`، مش extension.

### الـ overloading

```kotlin
fun describe(n: Notifier) = "describe(Notifier)"
fun describe(n: SmsNotifier) = "describe(SmsNotifier)"

val asBase: Notifier = SmsNotifier()
describe(asBase)   // "describe(Notifier)"
```

الاختيار هنا حصل وقت الـ compile بناءً على النوع المكتوب للـ argument، مش النوع الحقيقي. ده عكس الـ override بالظبط.

في Kotlin أغلب الـ overloads اللي كانت بتتكتب في Java بتتحل بـ default arguments. ولو كود Java محتاج ينادي function فيها defaults، الـ annotation اللي اسمها `@JvmOverloads` بتولّد الـ overloads أوتوماتيك.

### الـ type checks والـ smart casts

ساعات فعلًا محتاج تعرف النوع، زي لما تعرض أحداث مختلفة:

```kotlin
fun render(event: CartEvent): String = when (event) {
    is ItemAdded -> "+${event.qty} ${event.name}"
    is ItemRemoved -> "-${event.name}"
    is CouponApplied -> "coupon ${event.code}"
}
```

بعد `is ItemAdded`، الـ compiler بيعامل `event` على إنه `ItemAdded` جوه الفرع ده من غير ما تعمل cast بنفسك. ده اسمه smart cast.

| العامل | لما النوع غلط |
|---|---|
| الفحص `is` | بيرجع false |
| الـ cast بـ `as` | بيرمي `ClassCastException` |
| الـ cast بـ `as?` | بيرجع null |

### حدود الـ smart cast

الـ smart cast بيشتغل بس لو الـ compiler متأكد إن القيمة مش هتتغير بين الفحص والاستخدام:

- متغير محلي `val`: شغال.
- متغير محلي `var` مش بيتغير بين الفحص والاستخدام: شغال.
- الـ property من نوع `var`: مش شغال، لأن كود تاني ممكن يغيّرها.
- الـ property بـ getter خاص بيها أو من module تاني: مش شغال.

الحل المعتاد: انسخ القيمة في `val` محلي وافحصه هو.

### إمتى `when` على النوع صح وإمتى غلط؟

- غلط: لما الـ `when` بيحدد **سلوك** المفروض يكون جوه الكلاسات نفسها، وبيتكرر في أماكن كتير. ده المشكلة اللي في أول الدرس.
- صح: لما الأنواع **داتا** والسلوك تابع لطبقة تانية زي الـ UI، ومع sealed types بحيث الـ compiler يتأكد إنك غطيت كل الحالات (الدرس 08).

## امشي مع الكود

### [L01_DynamicDispatch.kt](../src/main/kotlin/m06_polymorphism/L01_DynamicDispatch.kt)

```
  SMS to Sara: your order shipped
  EMAIL to Sara: [Kotlin Shop] your order shipped
  PUSH to device of Sara: your order shipped
  URGENT SMS to Sara: YOUR ORDER SHIPPED
```

نداء واحد في loop واحد، وأربع نتايج مختلفة.

```
  static type  = SmsNotifier
  runtime type = UrgentSms
  member send(): URGENT SMS to Omar: HI
```

الـ member function اتحددت من النوع الحقيقي.

```
  n.kind() = plain sms
  u.kind() = urgent sms
```

ده نفس الـ object بالظبط (عملنا cast للمتغير التاني)، ومع ذلك الـ extension طلّعت نتيجتين مختلفتين، لأنها اتحددت من النوع المكتوب.

### [L02_Overloading.kt](../src/main/kotlin/m06_polymorphism/L02_Overloading.kt)

```
  EGP 125
  USD 125
  EGP 125.50
```

تلات overloads بنفس الاسم، والـ compiler اختار حسب عدد الـ parameters ونوعها.

```
  declared Notifier:    describe(Notifier)
  declared SmsNotifier: describe(SmsNotifier)
```

الاتنين objects من نفس النوع وقت التشغيل، بس الـ overload اتحدد من النوع المكتوب.

### [L03_SmartCasts.kt](../src/main/kotlin/m06_polymorphism/L03_SmartCasts.kt)

```
  +2 Pen
  coupon SAVE10
  -Mug
```

في كل فرع الـ compiler عمل smart cast، فقدرنا نوصل لـ `qty` و `code` من غير cast يدوي.

```
  as? Int -> null
  as Int  -> threw ClassCastException
```

الـ `as?` آمن ويرجع null، والـ `as` بيرمي exception.

## في Compose

### الـ Image مش فارق معاه نوع الـ Painter

الـ composable اللي اسمه `Image` ليه overload بيقبل `Painter`:

```kotlin
@Composable
public fun Image(painter: Painter, contentDescription: String?, modifier: Modifier = Modifier /* ... */)
```

وانت ممكن تباصي له أي ابن من `Painter`:

- الكلاس `BitmapPainter` بيرسم صورة bitmap.
- الكلاس `VectorPainter` بيرسم أيقونة vector، وبتجيبه من `rememberVectorPainter`.
- أو painter خاص بيك زي `DiscountBadgePainter` اللي عملناه في الدرس 04.

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ProductImage(product: Product) {
    val painter: Painter = when {
        product.imageBitmap != null -> remember(product.id) { BitmapPainter(product.imageBitmap) }
        product.isOnSale -> remember { DiscountBadgePainter(Color.Red) }
        else -> rememberVectorPainter(Icons.Default.ShoppingCart)
    }
    Image(painter = painter, contentDescription = product.name)
}
```

الـ `Image` جواه بينادي `onDraw` على الـ painter. مفيش أي `is BitmapPainter` جوه Compose. كل painter بيرسم نفسه، ده dynamic dispatch.

### الـ Shape: أي شكل في أي مكان

الـ `RoundedCornerShape` و `GenericShape` و الـ `TicketShape` اللي عملناه في الدرس 05 كلهم `Shape`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
val triangle = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

@Composable
fun BadgeRow() {
    Row {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.Green))
        Box(Modifier.size(40.dp).clip(CircleShape).background(Color.Blue))
        Box(Modifier.size(40.dp).clip(triangle).background(Color.Red))
    }
}
```

الـ `Modifier.clip` واحدة، وكل شكل بيحسب الـ outline بتاعه بنفسه.

### الـ overloading في Compose

الـ Compose بيستخدم overloading كتير:

- الـ composable اللي اسمه `Icon` ليه overload بيقبل ImageVector وتاني بيقبل `Painter` وتالت بيقبل ImageBitmap.
- الـ `CircularProgressIndicator` ليه overload من غير progress (بيلف على طول) وتاني بياخد lambda بترجع نسبة التقدم.
- الـ `RoundedCornerShape` ليه factory functions بـ `Dp` أو CornerSize أو نسبة مئوية.

ده ad-hoc polymorphism: الـ compiler بيختار حسب الـ arguments اللي كتبتها.

## أخطاء شائعة

1. الغلطة: `when (type)` على String أو enum لتحديد السلوك، ومتكرر في كذا مكان.
   الصح: خلي السلوك member function في الكلاسات نفسها، وخلي كل كلاس يعمل override.

2. الغلطة: الاعتماد على extension function عشان تعمل سلوك مختلف لكل نوع.
   الصح: الـ extensions بتتحدد من النوع المكتوب. السلوك اللي بيتغير لازم يكون member.

3. الغلطة: استخدام `as` في كل حتة وانت مش متأكد.
   الصح: استخدم `as?` مع elvis، أو `is` مع smart cast.

4. الغلطة: الاستغراب من إن الـ smart cast مش شغال على `var` property.
   الصح: انسخها في `val` محلي الأول.

5. الغلطة: افتكار إن الـ overload بيتحدد من النوع الحقيقي.
   الصح: الـ overload بيتحدد وقت الـ compile من النوع المكتوب.

## أسئلة Interview

1. إيه الفرق بين overloading و overriding؟

<details><summary>الإجابة</summary>

الـ overloading functions بنفس الاسم وparameters مختلفة، والـ compiler بيختار وقت الـ compile من الأنواع المكتوبة. الـ overriding ابن بيغيّر function الأب بنفس الشكل، والاختيار بيحصل وقت التشغيل من النوع الحقيقي.

</details>

2. إيه هو الـ dynamic dispatch؟

<details><summary>الإجابة</summary>

اختيار نسخة الـ member function وقت التشغيل حسب النوع الحقيقي للـ object، مش النوع المكتوب للمتغير.

</details>

3. ليه الـ extension functions مش polymorphic؟

<details><summary>الإجابة</summary>

لأنها بتتحول لـ static functions بتاخد الـ receiver كـ parameter، والـ compiler بيختارها من النوع المكتوب. مش جزء من جدول الكلاس.

</details>

4. إيه الفرق بين `as` و `as?`؟

<details><summary>الإجابة</summary>

الـ `as` بيرمي `ClassCastException` لو النوع غلط. الـ `as?` بيرجع null.

</details>

5. ليه الـ smart cast مش بيشتغل على `var` property؟

<details><summary>الإجابة</summary>

لأن القيمة ممكن تتغير بين الفحص والاستخدام من مكان تاني، فالـ compiler مش يقدر يضمن النوع. الحل إنك تنسخها في `val` محلي.

</details>

6. اديني مثال على polymorphism في Compose.

<details><summary>الإجابة</summary>

الـ `Image` بيقبل أي `Painter`، والـ `Modifier.clip` بتقبل أي `Shape`. الـ composable بينادي `onDraw` أو `createOutline` من غير ما يعرف النوع الحقيقي.

</details>

## Self-check

- [ ] أقدر أشرح الفرق بين static type و runtime type بمثال.
- [ ] أعرف ليه `n.kind()` في L01 طلعت plain sms.
- [ ] أعرف إن الـ overload بيتحدد وقت الـ compile.
- [ ] أعرف أستخدم `is` و `as?` و smart cast صح، وأعرف حدود الـ smart cast.
- [ ] أقدر أكتب composable بيعرض أكتر من نوع Painter من غير `is`.
- [ ] ملف Exercises.kt بيطبع ALL PASS

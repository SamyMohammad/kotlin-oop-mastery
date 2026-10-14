# 05 - Interfaces

## المشكلة الأول

صفحة الـ checkout محتاجة تعرض طرق الشحن وتحسب تكلفة كل واحدة. لو الكود مربوط بالكلاسات نفسها:

```kotlin
fun shippingOptions(weight: Int, standard: StandardShipping, pickup: PickupPoint): List<String> =
    listOf(
        "Standard: ${standard.costCents(weight)}",
        "Pickup: ${pickup.costForPickup(weight)}",   // اسم function مختلف
    )
```

كل ما تضيف شركة شحن جديدة، لازم تعدّل الـ checkout نفسه. والأسوأ إن كل كلاس سمّى الـ function بتاعته بشكل مختلف، فمفيش طريقة توحّدهم.

الحل إنك تتفق على **عقد**: أي حاجة اسمها طريقة شحن لازم يكون ليها `name` و `costCents` و `eta`. الـ checkout يتعامل مع العقد بس، وأي كلاس يوقّع على العقد يشتغل معاه أوتوماتيك. العقد ده اسمه interface.

## الـ concept بالتفصيل

### الـ interface عقد مش كلاس

```kotlin
interface ShippingMethod {
    val name: String
    fun costCents(weightGrams: Int): Long
    fun eta(): String = "3-5 days"
}

class StandardShipping : ShippingMethod {
    override val name = "Standard"
    override fun costCents(weightGrams: Int) = 2000L + weightGrams / 10
}
```

الـ interface بيقول **إيه** اللي الكلاس يقدر يعمله، مش **إزاي**. ولما تكتب `: ShippingMethod` بعد اسم الكلاس، انت بتوقّع على العقد، والـ compiler بيتأكد إنك نفذت كل حاجة مطلوبة.

### كلاس واحد وأكتر من interface

الكلاس يقدر يورث من كلاس واحد بس، بس يقدر ينفذ أي عدد من الـ interfaces:

```kotlin
class Parcel(private val id: String) : Printable, Trackable {
    override fun printLabel() = "PARCEL #$id"
    override fun trackingUrl() = "https://track.example/$id"
}
```

الأفضل تفكر في الـ interface على إنه **قدرة**: الطرد يقدر يتطبع ويقدر يتتبع. ده بيخلي الـ interfaces صغيرة ومركزة، وده موضوع مبدأ اسمه Interface Segregation في الدرس 17.

### الـ default methods

الـ function في الـ interface ممكن يكون ليها جسم:

```kotlin
interface PricedItem {
    val priceCents: Long
    fun label(): String = "item - $priceCents"
}
```

أي كلاس بينفذ الـ interface بياخد `label()` جاهزة، ولو حب يغيّرها يعمل override. ده بيخليك تضيف function جديدة لـ interface قديم من غير ما تكسر كل الكلاسات اللي بتنفذه.

### الـ properties في الـ interface

الـ interface ينفع يكون فيه properties، بس **من غير حالة**:

```kotlin
interface PricedItem {
    val priceCents: Long            // لازم الكلاس يحددها
    val isFree: Boolean             // محسوبة من getter
        get() = priceCents == 0L
    // val currency = "EGP"         // ممنوع: Property initializers are not allowed in interfaces
}
```

الكلاس ممكن ينفذ الـ property كقيمة ثابتة، أو getter، أو حتى في الـ constructor: `class GiftCard(override val priceCents: Long) : PricedItem`.

### مشكلة الـ diamond

لو كلاس نفذ اتنين interfaces فيهم default method بنفس الاسم، Kotlin مش هتختار بالنيابة عنك:

```kotlin
interface Shippable { fun describe() = "ships in a box" }
interface Downloadable { fun describe() = "downloads instantly" }

class Bundle : Shippable, Downloadable {
    override fun describe() =
        super<Shippable>.describe() + " + " + super<Downloadable>.describe()
}
```

لو ماعملتش override، الـ compiler بيرفض. والصيغة `super<Name>` بتحدد أنهي نسخة عايز تنادي. ممكن تجمع الاتنين أو تختار واحدة.

### الـ fun interface

الـ interface اللي فيه function واحدة بس abstract ممكن تكتبه `fun interface`، وساعتها lambda تقدر تقوم مكانه:

```kotlin
fun interface PriceRule {
    fun apply(cents: Long): Long
}

val halfPrice = PriceRule { it / 2 }
```

ده اسمه SAM conversion (Single Abstract Method). نفس الحاجة اللي بتحصل مع `Runnable` و `Comparator` في Java.

### الـ fun interface ولا function type؟

| السؤال | الاختيار `fun interface` | الاختيار function type عادي |
|---|---|---|
| ليه اسم واضح في الكود؟ | آه | لأ (إلا بـ typealias) |
| ينفع يكون فيه functions تانية بكود؟ | آه | لأ |
| محتاج تعريف؟ | آه | لأ |
| ينفع كلاس ينفذه؟ | آه | آه بس بشكل أقل وضوحًا |

القاعدة: لو المفهوم مهم في الـ domain وليه اسم (قاعدة سعر، validator)، استخدم `fun interface`. لو مجرد callback صغير، الـ function type كفاية.

### الاختيار بين interface و abstract class

| السؤال | الـ interface | الـ abstract class |
|---|---|---|
| كام واحد ينفع الكلاس ياخده؟ | أي عدد | واحد بس |
| ينفع يشيل حالة؟ | لأ | آه |
| ليه constructor؟ | لأ | آه |
| ينفع يكون فيه كود؟ | آه (default methods) | آه |
| مناسب لـ | قدرة أو عقد | أساس مشترك لعيلة كلاسات |

القاعدة العملية: ابدأ بـ interface. لو لقيت إنك محتاج حالة مشتركة أو constructor، ساعتها فكّر في abstract class.

## امشي مع الكود

### [L01_InterfaceBasics.kt](../src/main/kotlin/m05_interfaces/L01_InterfaceBasics.kt)

```
  PARCEL #A1
  INVOICE 1001
```

الـ `printAll` بتستقبل `List<Printable>`، فقبلت طرد وفاتورة رغم إنهم كلاسات مالهمش أي علاقة ببعض غير العقد.

```
  Standard: 2150 cents, 3-5 days
  Pickup at Maadi: 500 cents, next day
```

الـ checkout اتعامل مع `ShippingMethod` بس. الكلاس `StandardShipping` استخدم الـ `eta()` الافتراضية، و `PickupPoint` عمل لها override.

### [L02_DefaultMethodsAndProperties.kt](../src/main/kotlin/m05_interfaces/L02_DefaultMethodsAndProperties.kt)

```
  Mug - 4500
  Sticker (free)
  Gift card worth 10000
```

الكلاسين الأولانيين استخدموا `label()` الافتراضية، والكارت عمل لها override.

```
  Gift card: isFree=true
```

الكارت اتعمل بسعر صفر في الـ loop التالت، والـ `isFree` المحسوبة في الـ interface اشتغلت عليه زي أي حد.

### [L03_DiamondConflict.kt](../src/main/kotlin/m05_interfaces/L03_DiamondConflict.kt)

```
  bundle: ships in a box + downloads instantly
  ships in a box
```

الكلاس `Bundle` جمع النسختين، و `PhysicalOnly` اختار واحدة.

```
  as Shippable:    bundle: ships in a box + downloads instantly
  as Downloadable: bundle: ships in a box + downloads instantly
```

مهما كان النوع اللي شايف بيه الـ object، الـ override بتاعه هو اللي بيتنفذ. ده أول طعم للـ polymorphism (الدرس 06).

### [L04_FunInterface.kt](../src/main/kotlin/m05_interfaces/L04_FunInterface.kt)

```
  class:  9000
  lambda: 9500
  trailing lambda: 5000
```

نفس الـ function قبلت كلاس كامل وlambda. ولما الـ parameter الأخير يكون fun interface، تقدر تكتب الـ lambda برّه القوسين.

```
  10% then -500: 8500
```

الـ `then` default method جوه الـ fun interface نفسه، وبتركّب قاعدتين ورا بعض.

## في Compose

### الـ Modifier: أهم interface في Compose

كل `Modifier.padding(...)` وكل `Modifier.clickable { }` بتكتبه هو object بينفذ interface اسمه `Modifier`:

```kotlin
public interface Modifier {
    public fun <R> foldIn(initial: R, operation: (R, Element) -> R): R
    public fun any(predicate: (Element) -> Boolean): Boolean
    public infix fun then(other: Modifier): Modifier = // ...

    public interface Element : Modifier { /* ... */ }
    public companion object : Modifier { /* ... */ }
}
```

حاجات تستاهل تلاحظها:

- الـ `then` default method بتركّب modifier على modifier. نفس فكرة الـ then بتاعة PriceRule في L04 بالظبط.
- جوه الـ interface فيه interface تاني اسمه Element، وكل modifier مفرد (زي padding لوحده) هو Element.
- فيه companion object بينفذ نفس الـ interface. ده سبب إنك بتبدأ دايمًا بكلمة `Modifier`، وهتفهمه بالتفصيل في الدرس 09.

كل composable بيقبل `modifier: Modifier`. هو مايعرفش ولا يهمه الـ modifier جاي منين، هو بيتعامل مع العقد بس. ده L01 بالظبط.

### الـ Shape: interface بـ function واحدة

```kotlin
public interface Shape {
    public fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline
}
```

أي شكل في Compose (دايرة، مستطيل بزوايا مدورة، شكل خاص بيك) بينفذ العقد ده. تقدر تعمل شكل تذكرة للكوبونات:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
class TicketShape(private val notchRadius: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height / 2 - notchRadius)
            arcTo(
                rect = Rect(Offset(size.width, size.height / 2), notchRadius),
                startAngleDegrees = 270f, sweepAngleDegrees = -180f, forceMoveTo = false,
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun CouponCard(code: String) {
    Box(
        Modifier
            .clip(TicketShape(notchRadius = 24f))
            .background(Color(0xFFFFE082))
            .padding(16.dp)
    ) {
        Text(code)
    }
}
```

الـ `Modifier.clip` بتقبل أي `Shape`. مش فارق معاها لو `TicketShape` أو دايرة أو أي حاجة تانية.

### الـ Arrangement.Horizontal

لما تكتب `Row(horizontalArrangement = Arrangement.SpaceBetween)`، انت بتباصي object بينفذ interface اسمه `Arrangement.Horizontal`. فيه property بقيمة افتراضية `spacing` (زي `isFree` في L02) و function واحدة بتحسب أماكن العناصر. تقدر تنفذه بنفسك لو عايز توزيع مش موجود في المكتبة.

### الـ Indication وتطور الـ interfaces

الـ interface اللي اسمه `Indication` (المسؤول عن تأثير الضغط زي الـ ripple) مثال حقيقي على مشكلة بتحصل لما API تكبر: الـ function القديمة فيه بقت deprecated، والطريقة الجديدة إنك تنفذ interface ابن اسمه `IndicationNodeFactory`. الـ interface الابن بيورث من الأب ويضيف function جديدة، فالكود القديم اللي بيستقبل `Indication` لسه شغال. ده سبب من أسباب إن الـ interfaces الصغيرة أسهل في التطوير من الكلاسات الكبيرة.

## أخطاء شائعة

1. الغلطة: محاولة حط قيمة جوه interface زي `val currency = "EGP"`.
   الصح: الـ interface مايشيلش حالة. استخدم getter: `val currency: String get() = "EGP"`.

2. الغلطة: interface ضخم فيه عشر functions، والكلاسات بتنفذ نصهم بـ `TODO()`.
   الصح: قسّمه لـ interfaces صغيرة، كل واحد قدرة واحدة.

3. الغلطة: تجاهل مشكلة الـ diamond ومحاولة نداء `super.describe()` من غير تحديد.
   الصح: استخدم `super<Name>.describe()` واختار صراحة.

4. الغلطة: كتابة كلاس كامل عشان تنفذ interface فيه function واحدة.
   الصح: خليه `fun interface` واستخدم lambda.

5. الغلطة: الكود بيعتمد على الكلاسات نفسها بدل الـ interface.
   الصح: خلي الـ parameters والـ properties من نوع الـ interface، عشان تقدر تبدّل التنفيذ من غير ما تلمس الكود.

## أسئلة Interview

1. إيه الفرق بين interface و abstract class؟

<details><summary>الإجابة</summary>

الـ interface مالوش حالة ولا constructor، والكلاس يقدر ينفذ أي عدد منه. الـ abstract class ليه حالة و constructor، والكلاس يورث من واحد بس. الاتنين ينفع يكون فيهم functions بكود.

</details>

2. ينفع الـ interface يكون فيه properties؟

<details><summary>الإجابة</summary>

آه، بس من غير backing field. يا إما abstract الكلاس يحددها، يا إما محسوبة بـ getter.

</details>

3. لو كلاس نفذ اتنين interfaces فيهم نفس الـ default method، إيه اللي بيحصل؟

<details><summary>الإجابة</summary>

الـ compiler بيجبرك تعمل override، وتقدر تنادي أي نسخة بـ `super<InterfaceName>.method()`.

</details>

4. يعني إيه `fun interface` وإيه فايدته؟

<details><summary>الإجابة</summary>

هو interface فيه function واحدة بس abstract، وده بيسمح بـ SAM conversion: تكتب lambda مكان object كامل. وبيفضل ليه اسم ويقدر يكون فيه default methods، على عكس الـ function type.

</details>

5. ليه `Modifier` في Compose interface مش class؟

<details><summary>الإجابة</summary>

عشان أي حاجة تقدر تنفذ العقد وتتركب مع غيرها بـ `then`، والـ composables تستقبل أي modifier من غير ما تعرف نوعه. ده بيخلي الـ API مفتوح لأي modifier جديد من غير تعديل في المكتبة.

</details>

6. إمتى تختار interface صغير عن interface كبير؟

<details><summary>الإجابة</summary>

دايمًا لما تقدر. الـ interface الصغير أسهل في التنفيذ والـ testing، والكلاس ممكن ينفذ أكتر من واحد حسب قدراته. ده مبدأ Interface Segregation.

</details>

## Self-check

- [ ] أقدر أشرح ليه `printAll` في L01 قبلت كلاسات مالهاش علاقة ببعض.
- [ ] أعرف أكتب property محسوبة في interface وأعرف ليه مينفعش قيمة ثابتة.
- [ ] أقدر أحل مشكلة diamond بـ `super<Name>`.
- [ ] أعرف أكتب `fun interface` وأستخدمه بـ lambda.
- [ ] أقدر أكتب custom `Shape` وأستخدمه مع `Modifier.clip`.
- [ ] ملف Exercises.kt بيطبع ALL PASS

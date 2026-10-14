# 04 - Abstract Classes

## المشكلة الأول

في الدرس اللي فات كتبنا `open class Payment` وفيها `open fun fee(): Long = 0`. خلي بالك من القيمة الافتراضية دي: لو حد عمل طريقة دفع جديدة ونسي يعمل override لـ `fee()`، الكود هيشتغل عادي والرسوم هتبقى صفر. مفيش error ولا تحذير، والشوب هيخسر فلوس في صمت.

ونفس المشكلة مع الخصومات:

```kotlin
open class Discount(val code: String) {
    open fun amountOff(subtotalCents: Long): Long = 0   // قيمة افتراضية ملهاش معنى
}
```

هل فيه حاجة اسمها "خصم عادي"؟ لأ. كل خصم لازم يكون نسبة أو مبلغ ثابت أو حاجة محددة. القيمة الافتراضية هنا كذبة.

الـ abstract class بيقول للـ compiler: **الكلاس ده مش كامل لوحده، وأي ابن لازم يكمّل الناقص**. ولو نسي، الكود مش هيعمل compile أصلًا.

## الـ concept بالتفصيل

### الـ abstract class

```kotlin
abstract class Discount(val code: String) {
    abstract fun amountOff(subtotalCents: Long): Long          // مالهاش جسم

    fun apply(subtotalCents: Long): Long =                     // كود مشترك
        (subtotalCents - amountOff(subtotalCents)).coerceAtLeast(0)
}
```

القواعد:

- مينفعش تعمل منه object مباشرة: `Discount("X")` بتدي error.
- هو `open` لوحده، مش محتاج تكتب `open abstract`.
- الـ members اللي عليها `abstract` مالهاش جسم، وكل كلاس concrete بيورث لازم ينفذها.
- ينفع يكون فيه members عادية بكود، و properties بحالة، و constructor.
- الـ abstract member نفسه `open` تلقائيًا.

### الـ abstract property

مش بس functions، الـ properties كمان ممكن تكون abstract:

```kotlin
abstract class Product(val name: String) {
    abstract val category: String
}

class Book(name: String) : Product(name) {
    override val category = "books"
}
```

الابن ممكن ينفّذها بقيمة ثابتة، أو بـ getter محسوب، أو حتى يحطها في الـ constructor بتاعه: `class EBook(override val category: String)`.

### الـ template method pattern

ده أشهر استخدام للـ abstract classes. الأب بيملك **ترتيب الخطوات**، والأبناء بيملكوا **تفاصيل كل خطوة**:

```kotlin
abstract class CheckoutStep(private val name: String) {

    fun run(): Boolean {              // مش open: محدش يغيّر الترتيب
        if (!validate()) return false
        execute()
        afterExecute()
        return true
    }

    protected abstract fun validate(): Boolean   // خطوة إجبارية
    protected abstract fun execute()             // خطوة إجبارية
    protected open fun afterExecute() {}         // hook اختياري
}
```

النقط المهمة هنا:

- الـ `run()` مش `open`، فالضمان "مفيش execute من غير validate" مستحيل حد يكسره.
- الخطوات `protected`، يعني الأبناء ينفذوها، بس محدش من برّه يناديها لوحدها ويتخطى الترتيب.
- فيه فرق بين خطوة `abstract` (لازم تتعمل) و hook من نوع `open` بجسم فاضي (اختياري).

### إمتى abstract وإمتى open وإمتى interface؟

| السؤال | الاختيار `open class` | الاختيار `abstract class` | الاختيار `interface` |
|---|---|---|---|
| ينفع تعمل منه object؟ | آه | لأ | لأ |
| ينفع يشيل حالة؟ | آه | آه | لأ (properties من غير field) |
| ليه constructor؟ | آه | آه | لأ |
| بيجبر الابن على حاجة؟ | لأ | آه | آه |
| كام واحد تقدر تورث منه؟ | واحد بس | واحد بس | أي عدد |

القاعدة العملية:

- لو فيه سلوك افتراضي معقول وصحيح فعلًا، استخدم `open class`.
- لو الأبناء بيشاركوا حالة أو constructor أو خطوات ثابتة، والباقي لازم يتحدد، استخدم `abstract class`.
- لو هو مجرد عقد أو قدرة (يقدر يتشحن، يقدر يتطبع)، استخدم `interface`. ده الدرس 05.

### الـ abstract class والـ constructor

حتى لو مينفعش تعمل منه object، الـ constructor بتاع الـ abstract class بيشتغل لما الابن يتبني. ده بيخليك تحط validation مشترك مرة واحدة:

```kotlin
abstract class Discount(val code: String) {
    init { require(code.isNotBlank()) }
}
```

وخلي بالك من الفخ اللي في الدرس 03: نفس المشكلة موجودة هنا. لو الـ `init` بتاع الـ abstract class نادى abstract function، الابن هينفذها قبل ما properties بتاعته تتجهز.

### الفرق مع Java

الـ abstract classes في Kotlin شبه Java جدًا. الفروق الصغيرة:

- مفيش داعي تكتب `open` على الـ abstract members.
- الـ abstract properties موجودة في Kotlin، في Java لازم تعمل abstract getter.
- في Kotlin ينفع abstract member يعمل override لـ open member من الأب، فيرجّعه إجباري تاني.

## امشي مع الكود

### [L01_AbstractMembers.kt](../src/main/kotlin/m04_abstract/L01_AbstractMembers.kt)

```
  Kotlin in Depth [books] ships for 3000
  Clean Code [books] ships for 2000
  Compose Notes [digital] ships for 0
```

الـ `summary()` اتكتبت مرة واحدة في `Product`، وبتستخدم `category` و `shippingCents()` اللي كل ابن حددهم بنفسه.

```
  SAVE10: 20000 -> 18000
  MINUS50: 20000 -> 15000
  HUGE: 20000 -> 0
```

الخصم `HUGE` كان أكبر من الإجمالي، بس `apply()` في الأب ضمنت إن النتيجة متنزلش تحت الصفر. القاعدة دي مكتوبة مرة واحدة ومحدش يقدر ينساها.

### [L02_TemplateMethod.kt](../src/main/kotlin/m04_abstract/L02_TemplateMethod.kt)

```
  [reserve] start
     reserving 2 item(s)
  [reserve] done
  [charge] start
     charging 15000 cents
     sending receipt email
  [charge] done
```

الخطوتين ماشيين بنفس الترتيب بالظبط. الكلاس `ChargeCard` بس هو اللي استخدم الـ hook الاختياري `afterExecute` عشان يبعت إيميل.

```
  [reserve] start
  [reserve] validation failed, stopping
```

الـ validation فشل، فالـ `execute()` ماتنادتش خالص. ده ضمان بيقدمه الأب للكل.

### [L03_AbstractVsOpen.kt](../src/main/kotlin/m04_abstract/L03_AbstractVsOpen.kt)

```
  default channel: hi
  default channel: hi
```

الكلاس `QuietNotifier` ماعملش override لأي حاجة وعمل compile عادي، لأن الأب `open` مش abstract. ده ممكن يكون bug مستخبي.

```
  sms from Shop: order shipped
```

الكلاس `SmsNotifier` اتجبر يحدد `channel()`، وخد الـ `sender` من constructor الأب.

## في Compose

### الـ Painter: abstract class حقيقي

أي صورة بتعرضها في Compose بتمر بـ `Painter`. ده abstract class بالشكل ده تقريبًا:

```kotlin
public abstract class Painter {
    public abstract val intrinsicSize: Size
    protected abstract fun DrawScope.onDraw()
    protected open fun applyAlpha(alpha: Float): Boolean = false
    // ...
}
```

لاحظ إنه نفس شكل L02 بالظبط:

- الـ `intrinsicSize` و `onDraw` من نوع abstract، يعني كل painter لازم يقول حجمه الطبيعي ويرسم نفسه.
- الـ `applyAlpha` وأخواتها hooks من نوع `open` بقيمة افتراضية، يعني اختيارية.
- الـ `onDraw` نوعها `protected`، فمحدش من برّه يناديها. الـ framework هو اللي بيناديها في الوقت الصح، بعد ما يجهّز الـ alpha والـ color filter. ده template method.

تقدر تعمل painter خاص بيك لبادج الخصم:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
class DiscountBadgePainter(private val color: Color) : Painter() {
    override val intrinsicSize: Size = Size(48f, 48f)

    override fun DrawScope.onDraw() {
        drawCircle(color = color)
    }
}

@Composable
fun SaleIcon() {
    Image(
        painter = remember { DiscountBadgePainter(Color.Red) },
        contentDescription = "On sale",
    )
}
```

الـ `Image` composable مايعرفش ولا يهمه نوع الـ painter. هو بس بيتعامل مع `Painter` المجرد، وده اللي هتشوفه في الدرس 06 تحت اسم polymorphism.

### الـ Modifier.Node

الطريقة الحديثة لعمل modifier خاص بحالة هي إنك تورث من `Modifier.Node`. ده abstract class موجود جوه الـ interface اللي اسمه `Modifier`:

```kotlin
public interface Modifier {
    public abstract class Node : DelegatableNode {
        public open fun onAttach() {}
        public open fun onDetach() {}
        // ...
    }
}
```

الـ `onAttach` و `onDetach` hooks اختيارية بنفس فكرة `afterExecute` في L02. الـ framework بيناديهم لما الـ modifier يدخل الشاشة أو يخرج منها، وانت بتملا اللي محتاجه بس.

### الـ ViewModel نفسه abstract

الكلاس `ViewModel` في Android مكتوب abstract. مينفعش تعمل `ViewModel()` لوحده، لازم تورث منه. وفيه hook من نوع `protected open` اسمه `ViewModel.onCleared` الـ framework بيناديه لما الشاشة تتقفل نهائيًا. ده template method تاني: الـ framework بيملك الـ lifecycle، وانت بتملا التفاصيل.

## أخطاء شائعة

1. الغلطة: `open fun` بقيمة افتراضية ملهاش معنى، زي رسوم صفر أو خصم صفر.
   الصح: لو مفيش قيمة افتراضية صحيحة فعلًا، خليها `abstract` عشان الـ compiler يجبر كل ابن.

2. الغلطة: الـ template function نفسها `open`.
   الصح: خليها final (من غير `open`) عشان ترتيب الخطوات يفضل مضمون.

3. الغلطة: الخطوات الداخلية `public`.
   الصح: خليها `protected` عشان محدش ينادي خطوة لوحدها ويتخطى الـ validation.

4. الغلطة: abstract class مفيهوش أي حالة ولا كود مشترك.
   الصح: ده interface متنكّر. حوّله لـ interface عشان الكلاسات تقدر تنفذ أكتر من واحد.

5. الغلطة: نداء abstract member من الـ `init`.
   الصح: نفس فخ الدرس 03. الابن لسه ماتبناش.

## أسئلة Interview

1. إيه الفرق بين abstract class و interface في Kotlin؟

<details><summary>الإجابة</summary>

الـ abstract class ممكن يشيل حالة وليه constructor، والكلاس يورث من واحد بس. الـ interface مفيهوش حالة ولا constructor، والكلاس يقدر ينفذ أي عدد منه. الاتنين ممكن يكون فيهم functions بكود وfunctions من غير كود.

</details>

2. إيه هو الـ template method pattern؟

<details><summary>الإجابة</summary>

الأب فيه function ثابتة بتحدد ترتيب الخطوات، وبتنادي functions من نوع abstract أو open الأبناء بيحددوها. الأب بيضمن الترتيب، والأبناء بيتحكموا في التفاصيل.

</details>

3. هل الـ abstract class ممكن يكون ليه constructor؟ ليه؟

<details><summary>الإجابة</summary>

آه. مينفعش تعمل منه object مباشرة، بس الـ constructor بيشتغل كجزء من بناء أي ابن، فينفع يستقبل parameters ويعمل validation ويجهز حالة مشتركة.

</details>

4. إمتى تفضّل `abstract` عن `open` بقيمة افتراضية؟

<details><summary>الإجابة</summary>

لما مفيش قيمة افتراضية صحيحة لكل الحالات. الـ `abstract` بيحوّل النسيان من bug وقت التشغيل لـ error وقت الـ compile.

</details>

5. اذكر abstract class مشهور في Compose أو Android وإيه اللي بيجبرك عليه.

<details><summary>الإجابة</summary>

الكلاس `Painter` بيجبرك تحدد `intrinsicSize` وتكتب `onDraw`. والكلاس `ViewModel` abstract ومينفعش تعمل منه object مباشرة، وفيه hook اختياري اسمه `onCleared`.

</details>

## Self-check

- [ ] أعرف ليه `Discount("X")` مش بتعمل compile.
- [ ] أقدر أكتب template method فيها خطوات إجبارية و hook اختياري.
- [ ] أقدر أختار بين open و abstract و interface باستخدام الجدول.
- [ ] أقدر أكتب custom `Painter` وأشرح مين اللي بينادي `onDraw`.
- [ ] ملف Exercises.kt بيطبع ALL PASS

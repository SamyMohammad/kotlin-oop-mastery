# 03 - Inheritance

## المشكلة الأول

الشوب بيقبل أكتر من طريقة دفع: كارت، وكاش عند الاستلام، ويمكن محفظة إلكترونية بعدين. كل طريقة ليها مبلغ، وليها رسوم، وليها وصف. لو كتبت كل واحدة لوحدها:

```kotlin
class CardPayment(val amountCents: Long, val last4: String) {
    fun fee() = amountCents * 2 / 100
    fun total() = amountCents + fee()
    fun describe() = "Card *$last4 - Payment of $amountCents"
}

class CashOnDelivery(val amountCents: Long) {
    fun fee() = 1500L
    fun total() = amountCents + fee()                 // نفس الكود تاني
    fun describe() = "Cash on delivery - Payment of $amountCents"
}
```

الـ `total()` مكتوبة مرتين بنفس الشكل. لو قررت تضيف ضريبة على الإجمالي، لازم تفتكر تعدّلها في كل كلاس. وكمان مفيش نوع واحد تقدر تحط فيه كل طرق الدفع في `List` واحدة.

الـ inheritance بيحل الاتنين: كود مشترك في كلاس أب، وكل كلاس ابن **هو نوع من** الأب.

## الـ concept بالتفصيل

### علاقة is-a

الـ inheritance مناسب لما تقدر تقول جملة "الـ X **هو** Y" وتكون صح في كل الأحوال:

- الدفع بالكارت هو دفع. صح.
- الكاش عند الاستلام هو دفع. صح.
- السلة هي list. غلط! السلة **عندها** list، ودي علاقة has-a (هتشوفها في الدرس 16).

### الكلاسات final بشكل افتراضي

في Kotlin أي كلاس وأي function مقفولين للوراثة إلا لو قلت غير كده:

```kotlin
class Product
class Book : Product()   // error: This type is final, so it cannot be inherited from.
```

عشان تسمح بالوراثة:

```kotlin
open class Payment(val amountCents: Long) {
    open fun fee(): Long = 0          // مسموح للأبناء يغيّروها
    fun total() = amountCents + fee() // مش open، ماحدش يغيّرها
}
```

ليه Kotlin اختارت كده؟ بسبب مشكلة اسمها **fragile base class**: لو أي حد يقدر يورث من كلاسك ويغيّر أي function فيه، أي تعديل صغير في الأب ممكن يكسر أبناء انت أصلًا ماتعرفش إنهم موجودين. لما يكون الافتراضي final، انت بتقرر بوعي إيه اللي ينفع يتغير.

### الـ override و super

```kotlin
class CardPayment(amountCents: Long, val last4: String) : Payment(amountCents) {
    override fun fee(): Long = amountCents * 2 / 100
    override fun describe(): String = "Card *$last4 - " + super.describe()
}
```

- الكلمة `override` إجبارية. لو نسيتها الـ compiler هيرفض، وده بيحميك من إنك تعمل override بالغلط.
- الـ `super.describe()` بتنادي نسخة الأب. ده بيخليك تبني على الكلام القديم بدل ما تكرره.
- أي `override` نفسه بيبقى open للأحفاد. لو عايز تقفله اكتب `final override`.

### الـ template بسيط من غير abstract

لاحظ إن `total()` في `Payment` مش open، بس جواها بتنادي `fee()` اللي هي open. يعني الأب بيحدد **الخطوات** والأبناء بيغيّروا **تفصيلة** واحدة بس. ده شكل بدائي من template method pattern، وهتشوفه كامل في الدرس 04.

### ترتيب الـ constructors في الوراثة

القاعدة: **الأب بيتبني بالكامل الأول، وبعدين الابن**.

```kotlin
open class Account(val owner: String) {
    init { log("Account init") }
}

class SellerAccount(owner: String, val storeName: String) : Account(owner) {
    init { log("SellerAccount init") }
}
```

الترتيب:

1. الـ arguments بتتحسب.
2. الـ constructor بتاع `Account` بيشتغل بالكامل (properties و init blocks).
3. بعدين properties و init blocks بتوع `SellerAccount`.

لو الابن مالوش primary constructor، كل secondary constructor لازم ينادي `super(...)` بنفسه.

### الفخ الكبير: open function في الـ constructor

بما إن الأب بيتبني الأول، لو الـ init بتاعه نادى function الابن عامل لها override، الـ override هيشتغل **قبل** ما properties الابن تاخد قيمها:

```kotlin
open class Screen {
    init { render() }          // خطر
    open fun render() {}
}

class ProductScreen : Screen() {
    private val title: String = "Blue Pen"
    override fun render() = println(title)   // هتطبع null
}
```

الـ `title` نوعه `String` مش `String?`، ومع ذلك طبع null! ده واحد من الأماكن القليلة اللي الـ null safety في Kotlin مش بيحميك فيها. الحل: **ماتناديش open members من الـ constructor أو الـ init**. IntelliJ بيحذرك بالرسالة "Calling non-final function in constructor".

### اللي بيجي من `Any` مع كل كلاس

كل كلاس في Kotlin بيورث من `Any` تلات functions:

| الـ function | السلوك الافتراضي |
|---|---|
| الـ `equals` | بيقارن الهوية: هل دول نفس الـ object؟ |
| الـ `hashCode` | رقم مبني على الهوية |
| الـ `toString` | اسم الكلاس وبعده @ ورقم hex |

لو عملت override لـ `equals` لازم تعمل override لـ `hashCode` كمان، وإلا الـ object هيتصرف غلط في `HashSet` و `HashMap`. القاعدة: لو اتنين `equals` يبقى لازم `hashCode` بتاعهم يكون واحد.

### الفرق بين Kotlin و Java

| الحاجة | في Kotlin | في Java |
|---|---|---|
| الكلاس بشكل افتراضي | قفول (final) | مفتوح |
| الـ method بشكل افتراضي | قفولة | مفتوحة |
| كلمة الوراثة | النقطتين `:` | الكلمة `extends` |
| الـ override | كلمة إجبارية | الـ annotation اختيارية |
| نداء الأب | الكلمة `super` | الكلمة `super` |

## امشي مع الكود

### [L01_OpenAndOverride.kt](../src/main/kotlin/m03_inheritance/L01_OpenAndOverride.kt)

```
  Payment of 10000 | fee=0 total=10000
  Card *4242 - Payment of 10000 | fee=200 total=10200
  Cash on delivery - Payment of 10000 | fee=1500 total=11500
```

الـ `total()` واحدة ومكتوبة في الأب بس، ومع ذلك كل نوع طلّع إجمالي مختلف لأنه بينادي `fee()` الخاصة بيه.

```
  Discount:            0% off
  SeasonalDiscount:    10% off
  BlackFridayDiscount: 50% off
```

الكلاس `BlackFridayDiscount` استخدم `super.percent()` اللي رجّعت 10 وضربها في 5. والـ `label()` اشتغلت صح في التلاتة لأنها بتنادي `percent()` اللي بتتحدد حسب نوع الـ object.

### [L02_ConstructorChaining.kt](../src/main/kotlin/m03_inheritance/L02_ConstructorChaining.kt)

```
  Account init: owner=Sara
  SellerAccount init: store=Sara's Mugs
```

الأب اتبنى الأول دايمًا.

```
  Account init: owner=guest
  GuestAccount() body
  Account init: owner=guest-77
  GuestAccount(sessionId) body
```

كل secondary constructor اختار يبعت قيمة مختلفة للأب، وجسمه اتنفذ بعد ما الأب خلص.

### [L03_OpenCallInConstructor.kt](../src/main/kotlin/m03_inheritance/L03_OpenCallInConstructor.kt)

```
  Screen init calls render()
  ProductScreen.render(): title=null stock=0
  --- object finished, render() again ---
  ProductScreen.render(): title=Blue Pen stock=12
```

أول نداء حصل جوه init الأب، والابن لسه ماتبناش، فالـ `title` كان null والـ `stock` كان صفر. بعد ما الـ object خلص، نفس الـ function طبعت القيم الصح.

### [L04_AnyMembers.kt](../src/main/kotlin/m03_inheritance/L04_AnyMembers.kt)

```
  Coupon(SAVE10) vs Coupon(SAVE10): different
```

الـ `equals` الافتراضية بتقارن الهوية، فاتنين coupons بنفس الكود طلعوا مختلفين.

```
  ValueCoupon vs ValueCoupon: same
  set size = 2
```

بعد ما عملنا override لـ `equals` و `hashCode` مع بعض، الـ `Set` شال التكرار صح.

## في Compose

### الوراثة في كود Android اللي حوالين Compose

نقطة البداية لأي تطبيق Compose هي Activity بتورث من `ComponentActivity`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)   // لازم، الأب بيجهّز حاجات كتير
        setContent {
            ShopTheme { ProductListScreen() }
        }
    }
}
```

الكلاس `ComponentActivity` كلاس open بيورث من كلاس تاني وبينفّذ interfaces كتير زي LifecycleOwner و ViewModelStoreOwner. لما بتورث منه، انت بتاخد كل ده ببلاش. ولاحظ `super.onCreate`: لو نسيتها، الأب مش هيعمل التجهيزات بتاعته والتطبيق هيقع.

وبنفس الشكل الـ ViewModel:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
class CheckoutViewModel(private val timer: PriceTimer) : ViewModel() {
    override fun onCleared() {
        timer.stop()          // تنضيف قبل ما الـ ViewModel يموت
        super.onCleared()
    }
}
```

الكلاس `ViewModel` في Android نوعه abstract، والـ function اللي اسمها `ViewModel.onCleared` نوعها `protected open`. يعني متصممة إن الأبناء يعملوا لها override، ومش متاحة لأي حد من برّه يناديها.

### الـ Compose نفسه مش بيعتمد على الوراثة

لو جاي من نظام الـ Views القديم، كنت بتعمل custom view كده:

```kotlin
class PriceTextView(context: Context) : TextView(context) { ... }
```

في Compose مفيش ولا composable بيورث من التاني. لو عايز زرار شكله مختلف، مش بتورث من `Button`، بتعمل composable جديد **يستخدم** `Button` جواه:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun BuyButton(price: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Buy for $price")
    }
}
```

ده اسمه composition، وهو الموضوع الرئيسي للدرس 16. الفكرة إن Compose اتعلم من مشاكل الوراثة في الـ Views (كلاسات ضخمة، وسلوك غريب بييجي من أب بعيد)، واختار يبني الـ UI بتجميع functions صغيرة.

الوراثة لسه موجودة في الأماكن اللي العلاقة فيها is-a فعلًا وفي framework محتاج نقطة بداية: الـ Activity والـ ViewModel، وكمان الـ `Painter` و `Modifier.Node` اللي هتشوفهم في الدرس 04.

## أخطاء شائعة

1. الغلطة: إنك تنسى `open` وتستغرب إن الوراثة مش شغالة.
   الصح: الكلاس والـ function لازم يكونوا `open` بشكل صريح.

2. الغلطة: نداء open function من الـ `init` بتاع الأب.
   الصح: خلي الـ constructor يجهّز حالة بس، والسلوك يتنادى بعد ما الـ object يخلص.

3. الغلطة: override لـ `equals` من غير `hashCode`.
   الصح: الاتنين مع بعض دايمًا، أو استخدم data class.

4. الغلطة: الوراثة عشان تاخد كود بس من غير علاقة is-a حقيقية، زي `class Cart : ArrayList<Item>()`.
   الصح: لو العلاقة has-a استخدم property وcomposition.

5. الغلطة: نسيان `super.onCreate` أو `super.onCleared` في كلاسات الـ framework.
   الصح: كلاسات الـ framework بتعتمد على إنك تنادي الأب، إلا لو الـ documentation بتقول غير كده.

6. الغلطة: هرم وراثة عميق خمس أو ست مستويات.
   الصح: خلي الهرم قصير، وفكّر في interfaces و composition قبل ما تزوّد مستوى.

## أسئلة Interview

1. ليه الكلاسات في Kotlin final بشكل افتراضي؟

<details><summary>الإجابة</summary>

عشان تتجنب مشكلة الـ fragile base class. لما الوراثة تكون قرار صريح، صاحب الكلاس يعرف إيه اللي مسموح يتغير ويقدر يعدّل الباقي بأمان.

</details>

2. إيه ترتيب التنفيذ لما تعمل object من كلاس ابن؟

<details><summary>الإجابة</summary>

الـ arguments الأول، بعدها الأب بالكامل (properties و init blocks)، بعدها properties و init blocks بتوع الابن، وفي الآخر جسم الـ secondary constructor لو اتستخدم.

</details>

3. إيه اللي بيحصل لو الأب نادى open function من الـ init والابن عامل لها override؟

<details><summary>الإجابة</summary>

الـ override بتاع الابن بيشتغل قبل ما properties الابن تتعمل لها initialize، فبيشوف قيم افتراضية زي null وصفر، حتى لو النوع non-null.

</details>

4. إيه الفرق بين `override` و `final override`؟

<details><summary>الإجابة</summary>

الـ `override` العادي بيفضل open للأحفاد. الـ `final override` بيقفله فمحدش تحت في الهرم يقدر يغيّره تاني.

</details>

5. ليه لازم `equals` و `hashCode` يتعمل لهم override مع بعض؟

<details><summary>الإجابة</summary>

لأن الـ hash collections بتدوّر بالـ `hashCode` الأول وبعدين تقارن بـ `equals`. لو اتنين objects متساويين بـ `equals` بس الـ hash مختلف، الـ `HashSet` هيحتفظ بالاتنين.

</details>

6. إمتى تستخدم الوراثة وإمتى composition؟

<details><summary>الإجابة</summary>

الوراثة لما العلاقة is-a حقيقية وثابتة، أو لما framework بيطلب كلاس أب. الـ composition لما العلاقة has-a أو لما محتاج تجمع سلوكيات مختلفة مع بعض.

</details>

## Self-check

- [ ] أعرف ليه `class Book : Product()` مش بتعمل compile لو `Product` مش open.
- [ ] أقدر أتوقع ترتيب الطباعة في L02 و L03.
- [ ] أقدر أشرح ليه `title` طلع null في L03 رغم إن نوعه `String`.
- [ ] أعرف أستخدم `super` عشان أبني على سلوك الأب.
- [ ] أعرف ليه Compose بيفضّل composition عن الوراثة، وإيه الأماكن اللي الوراثة لسه فيها.
- [ ] ملف Exercises.kt بيطبع ALL PASS

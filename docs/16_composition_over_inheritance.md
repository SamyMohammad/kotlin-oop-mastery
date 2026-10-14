# 16 - Composition over Inheritance

## المشكلة الأول

الشوب بيبعت إشعارات بقناتين: SMS و Email. وبعدين طلبوا logging. وبعدين retry لو الإرسال فشل. وبعدين تحويل الرسالة لحروف كبيرة في العروض. لو حليتها بالوراثة:

```
Notifier
 ├── SmsNotifier
 │    ├── LoggingSmsNotifier
 │    │    └── RetryingLoggingSmsNotifier
 │    └── RetryingSmsNotifier
 └── EmailNotifier
      ├── LoggingEmailNotifier
      │    └── RetryingLoggingEmailNotifier
      └── RetryingEmailNotifier
```

كل ميزة جديدة **بتضاعف** عدد الكلاسات. وكود الـ logging متكرر في كل كلاس بيحتاجه. ولو عايز logging قبل الـ retry في حالة وبعده في حالة تانية، محتاج كلاسات أكتر.

المشكلة في السؤال نفسه. الوراثة بتسأل "الكلاس ده **نوع من** إيه؟". بس الـ logging مش نوع من الإشعارات، ده **قدرة** بنضيفها لأي إشعار. السؤال الصح: "الكلاس ده **عنده** إيه؟". وده اسمه composition.

## الـ concept بالتفصيل

### العلاقة is-a والعلاقة has-a

| العلاقة | السؤال | الأداة | مثال |
|---|---|---|---|
| الـ is-a | الكلاس ده نوع من إيه؟ | الوراثة | الكارت نوع من وسائل الدفع |
| الـ has-a | الكلاس ده عنده إيه؟ | الـ composition | الـ logger عنده sender بيحوّل له |

قاعدة سريعة: لو الجملة "X نوع من Y" صحيحة دايمًا وفي كل السياقات، الوراثة ممكنة. لو الجملة الأصح "X عنده Y" أو "X بيستخدم Y"، يبقى composition.

### المشاكل الحقيقية في الوراثة

1. انفجار الكلاسات: زي المثال اللي فوق، كل تركيبة محتاجة كلاس.
2. الـ fragile base class: تعديل في الأب ممكن يكسر أبناء ماتعرفش عنهم حاجة (الدرس 03).
3. الربط وقت الـ compile: الكلاس بيورث مرة واحدة، ومينفعش يغيّر الأب وقت التشغيل.
4. التسريب: الابن بيورث كل حاجة في الأب حتى اللي مش محتاجها.
5. الـ open-call trap: فخ نداء open function من الـ constructor (الدرس 03).

### الـ composition بالشكل العملي

```kotlin
interface Sender { fun send(msg: String): String }

class Sms : Sender { ... }
class Email : Sender { ... }

class Logging(private val inner: Sender) : Sender {
    override fun send(msg: String) = "[log] " + inner.send(msg)
}

class Retrying(private val inner: Sender, private val times: Int) : Sender { ... }
```

كل ميزة كلاس صغير **عنده** `Sender` ويلفّه. والتركيبات بتتعمل وقت التشغيل:

```kotlin
Retrying(Logging(Sms()), times = 3)
Logging(Retrying(Email(), times = 2))
```

بدل تكلفة "القنوات مضروبة في اتنين أس عدد المميزات"، بقت "القنوات زائد المميزات". وده كمان اسمه Decorator pattern (هتشوفه في الدرس 18).

### الـ composition والـ delegation

في الدرس 12 شفت `by`. هو اختصار للـ composition:

```kotlin
class Logging(private val inner: Sender) : Sender by inner {
    override fun send(msg: String) = "[log] " + inner.send(msg)
}
```

لو الـ interface فيه functions كتير وانت عايز تغيّر واحدة بس، `by` بتوفّر عليك كتابة الباقي.

### الـ slot APIs

نفس الفكرة على مستوى الـ functions: بدل ما تعمل subclass لشاشة عشان تغيّر الـ header، الشاشة تاخد الـ header نفسه كـ parameter:

```kotlin
fun renderScreen(
    header: (() -> String)? = null,
    footer: (() -> String)? = null,
    body: () -> String,
): String
```

كل جزء متغير في الشاشة بقى "slot"، يعني lambda بيملاها اللي بينادي. والأجزاء الاختيارية قيمتها null أو فاضية.

### الـ wrappers بدل الـ subclasses

لو عايز شاشة الشوب دايمًا فيها نفس الـ header والـ footer:

```kotlin
fun renderShopScreen(title: String, body: () -> String) =
    renderScreen(header = { "Kotlin Shop / $title" }, footer = { "Free delivery" }, body = body)
```

الـ `renderShopScreen` **بتستخدم** `renderScreen`، مش بتورث منها. لو `renderScreen` اتغيرت جواها، الـ wrapper مش هيتأثر طول ما الـ parameters زي ما هي.

### إمتى الوراثة لسه صح؟

الـ composition مش معناها "ماتورثش أبدًا". الوراثة مناسبة لما:

- العلاقة is-a حقيقية وثابتة، زي sealed hierarchy للحالات (الدرس 08).
- الـ framework نفسه بيطلب كلاس أب، زي `ComponentActivity` و `ViewModel` و `Painter`.
- عيلة كلاسات بتشارك template ثابت (الدرس 04).

حتى في الحالات دي، خلي الهرم قصير: مستوى أو اتنين.

## امشي مع الكود

### [L01_InheritanceExplosion.kt](../src/main/kotlin/m16_composition/L01_InheritanceExplosion.kt)

```
  [log] SMS: order shipped
  [log] EMAIL: order shipped
  [retry] [log] SMS: order shipped
```

كود الـ logging مكتوب في كل كلاس من جديد.

```
  2 channels, 1 optional features -> 4 classes
  2 channels, 4 optional features -> 32 classes
  3 channels, 4 optional features -> 48 classes
```

العدد بيتضاعف مع كل ميزة. مستحيل تصون ده.

### [L02_ComposeBehaviours.kt](../src/main/kotlin/m16_composition/L02_ComposeBehaviours.kt)

```
  [retry x3] [log] SMS: hi
  [retry x2] EMAIL: HI
```

نفس الكلاسات الخمسة عملت أي تركيبة، وبأي ترتيب.

```
  SMS: order 7 shipped
  [log] [retry x2] EMAIL: order 7 shipped
```

الـ `OrderService` ماتغيرش خالص. اتغير بس الـ sender اللي اتباصى له. ده نفس فكرة الـ Dependency Inversion في الدرس 17.

### [L03_SlotStyleApi.kt](../src/main/kotlin/m16_composition/L03_SlotStyleApi.kt)

```
  == Cart ==
  2 items, EGP 50.00
  -- Checkout --
```

كل الـ slots اتملت.

```
  Nothing in your cart yet
```

الـ body بس، والـ header والـ footer اختفوا لأنهم null.

```
  == Kotlin Shop / Product ==
  Blue Pen - EGP 5.00
  -- Free delivery over EGP 500 --
```

الـ wrapper ثبّت الـ header والـ footer وساب الـ body للي بينادي.

## في Compose

### الـ Compose كله composition

في نظام الـ Views القديم كان فيه هرم وراثة ضخم: View ثم TextView ثم Button ثم CompoundButton ثم CheckBox. لو عايز زرار بشكل خاص، كنت بتورث من `Button` وتلعب في functions زي `onDraw`. والكلاس الأب كان فيه آلاف السطور.

في Compose **مفيش ولا composable بيورث من التاني**. كل حاجة functions بتنادي functions. ده سبب الاسم Compose.

### الـ Scaffold: slot API كامل

```kotlin
@Composable
public fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    // ...
    content: @Composable (PaddingValues) -> Unit,
)
```

ده نفس `renderScreen` في L03 بالظبط، بس بـ composables:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun CartScreen(state: CartUiState, onCheckout: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Cart") }) },
        bottomBar = {
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                Text("Checkout")
            }
        },
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(state.items) { line -> Text("${line.productId} x${line.qty}") }
        }
    }
}
```

الـ `Scaffold` مايعرفش أي حاجة عن السلة. هو بيوفّر الـ layout، وانت بتملا الـ slots. والـ slots اللي ماملتهاش (زي `floatingActionButton`) قيمتها الافتراضية فاضية.

### الـ Button نفسه slot

الـ `Button` مابياخدش `text: String`. بياخد `content` (lambda الـ receiver بتاعها `RowScope`)، فتقدر تحط فيه أي حاجة:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
Button(onClick = onAdd) {
    Icon(Icons.Default.ShoppingCart, contentDescription = null)
    Spacer(Modifier.width(8.dp))
    Text("Add to cart")
}
```

لو `Button` كان بياخد `String` بس، كنت هتحتاج IconButton و TextButton و IconTextButton... وده انفجار الكلاسات تاني. الـ slot حلّها بـ API واحد.

### الـ wrapper composables بدل الوراثة

عايز زرار الشوب الأساسي بنفس الشكل في كل التطبيق؟ اعمل composable بيستخدم `Button`:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ShopPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().height(52.dp),
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
```

ده نفس `renderShopScreen` في L03: wrapper ثبّت شوية قرارات وساب الباقي parameters. ولو `Button` اتغير جواه في تحديث للمكتبة، الـ wrapper بتاعك هيكمل يشتغل.

### الـ Modifier كمان composition

كل modifier بيتركب على اللي قبله:

```kotlin
Modifier.padding(8.dp).background(Color.White).clickable { }
```

ده نفس `Retrying(Logging(Sms()))` في L02، بس بشكل سلسلة. كل modifier صغير بيعمل حاجة واحدة، والتركيبة هي اللي بتعمل الشكل النهائي. وده موضوع الـ Decorator pattern في الدرس 18.

## أخطاء شائعة

1. الغلطة: الوراثة عشان تاخد كود مشترك بس، من غير علاقة is-a حقيقية.
   الصح: استخرج الكود المشترك في كلاس تاني، والكلاسين يستخدموه (has-a).

2. الغلطة: كلاس لكل تركيبة من المميزات.
   الصح: كلاس لكل ميزة بيلف الـ interface، والتركيبات وقت التشغيل.

3. الغلطة: composable بياخد `title: String` و `icon: ImageVector?` و `subtitle: String?` و...
   الصح: slot من نوع composable lambda، واللي بينادي يحط اللي هو عايزه.

4. الغلطة: محاولة "تورث" من composable.
   الصح: مستحيل أصلًا، الـ composables functions. اعمل wrapper بيناديه.

5. الغلطة: افتكار إن الـ composition معناها "ممنوع الوراثة خالص".
   الصح: الوراثة صح للـ is-a الحقيقية وللـ framework classes، بس بهرم قصير.

## أسئلة Interview

1. يعني إيه "Composition over Inheritance"؟

<details><summary>الإجابة</summary>

إنك تبني السلوك بتجميع objects صغيرة كل واحد عنده دور (has-a) بدل ما تعمل هرم وراثة (is-a). ده بيدّي مرونة وقت التشغيل ويمنع انفجار الكلاسات والـ fragile base class.

</details>

2. إيه مشكلة انفجار الكلاسات؟

<details><summary>الإجابة</summary>

لما كل ميزة اختيارية محتاجة كلاس لكل تركيبة، العدد بيتضاعف مع كل ميزة جديدة. مع الـ composition كل ميزة كلاس واحد بس.

</details>

3. إيه هو الـ slot API في Compose؟

<details><summary>الإجابة</summary>

إن الـ composable ياخد أجزاءه المتغيرة كـ composable lambdas، زي `topBar` و `content` في `Scaffold`، واللي بينادي يملاها بأي محتوى.

</details>

4. ليه Compose مابيستخدمش وراثة بين الـ composables؟

<details><summary>الإجابة</summary>

لأن الـ composables functions مش classes، والتصميم مقصود عشان يتجنب مشاكل هرم الـ Views القديم. التخصيص بيحصل بالـ parameters والـ slots والـ wrappers.

</details>

5. إمتى تختار الوراثة رغم كل ده؟

<details><summary>الإجابة</summary>

لما العلاقة is-a حقيقية وثابتة زي sealed hierarchy، أو لما الـ framework بيطلب كلاس أب زي `ViewModel` و `ComponentActivity`.

</details>

## Self-check

- [ ] أقدر أفرق بين is-a و has-a في أمثلة من الشوب.
- [ ] أقدر أحسب انفجار الكلاسات وأشرح ليه الـ composition بتحله.
- [ ] أعرف أكتب wrapper class بيلف interface ويضيف ميزة.
- [ ] أعرف أصمم function أو composable بـ slots.
- [ ] أقدر أكتب wrapper composable زي `ShopPrimaryButton` بدل الوراثة.
- [ ] ملف Exercises.kt بيطبع ALL PASS

# 08 - Sealed Types and Enums

## المشكلة الأول

شاشة المنتجات ليها تلات حالات: بتحمّل، أو جابت المنتجات، أو حصل خطأ. أول محاولة غالبًا بتبقى كده:

```kotlin
data class ProductsState(
    val isLoading: Boolean = false,
    val products: List<String>? = null,
    val error: String? = null,
)
```

الشكل ده بيسمح بحالات مستحيلة: `isLoading = true` وفي نفس الوقت فيه `error`؟ ولا `products` موجودة و `error` موجود؟ كل مكان بيقرا الحالة لازم يفكر في كل التركيبات دي. وأي حد ممكن يعمل حالة غلط بالخطأ.

ومحاولة تانية بـ String:

```kotlin
val status = "shiped"   // typo، والـ compiler مش هيقول حاجة
```

الحل إنك توصف **الحالات الممكنة بالظبط**، ولا حالة زيادة: يا إما enum لو كل الحالات شكلها واحد، يا إما sealed type لو كل حالة ليها داتا مختلفة.

## الـ concept بالتفصيل

### الـ enum class

```kotlin
enum class PaymentMethod(val feePercent: Int) {
    CARD(2), WALLET(1), CASH(0);

    fun feeFor(amountCents: Long) = amountCents * feePercent / 100
}
```

الـ enum هو مجموعة **objects ثابتة** معروفة وقت الـ compile. كل constant هو object واحد بس في البرنامج كله (singleton).

اللي تقدر تعمله مع enum:

- الـ properties في الـ constructor، زي `feePercent`.
- الـ functions عادية.
- الـ abstract function وكل constant ينفذها بطريقته.
- ينفذ interfaces.
- الـ `entries` بترجّع كل القيم بالترتيب، و `name` و `ordinal` لكل واحد.
- الـ `valueOf("CARD")` بتحوّل نص لـ constant، وبترمي exception لو النص غلط.

ملاحظة: الـ `entries` هي البديل الحديث لـ `values()`. بترجّع list ثابتة بدل ما تعمل array جديدة كل مرة.

### الـ sealed class

```kotlin
sealed class ProductsUiState {
    data object Loading : ProductsUiState()
    data class Success(val products: List<String>) : ProductsUiState()
    data class Error(val message: String, val canRetry: Boolean) : ProductsUiState()
}
```

الـ sealed class هو مجموعة **أنواع ثابتة** (مش objects). كل نوع ليه شكل الداتا بتاعه:

- الحالة `Loading` مالهاش داتا، فـ object واحد كفاية.
- الحالة `Success` معاها list.
- الحالة `Error` معاها رسالة وهل ينفع نعيد المحاولة.

القاعدة اللي بتخلي الـ sealed مختلف: **كل الأبناء لازم يكونوا في نفس الـ package ونفس الـ module**. فالـ compiler عارف القايمة كاملة، ومحدش برّه يقدر يضيف ابن جديد.

### الـ data object

```kotlin
data object Loading : ProductsUiState()
```

ده object عادي بس معاه `toString` بيطبع الاسم بس، و `equals` و `hashCode` مظبوطين. استخدمه دايمًا للحالات اللي مالهاش داتا جوه sealed hierarchy.

### الاختيار بين enum و sealed

| السؤال | الـ enum | الـ sealed |
|---|---|---|
| الحاجة الثابتة | الـ objects نفسها | الأنواع |
| كل حالة ليها داتا مختلفة؟ | لأ، كلهم نفس الشكل | آه |
| ينفع تعمل نسخ كتير من نفس الحالة؟ | لأ، نسخة واحدة | آه، `Success(...)` جديدة كل مرة |
| ينفع تلف على كل القيم؟ | آه بـ `entries` | لأ بشكل مباشر |
| مثال | طرق الدفع، الـ tiers | حالة الشاشة، نتيجة الدفع |

### الـ sealed class ولا sealed interface؟

| السؤال | الـ sealed class | الـ sealed interface |
|---|---|---|
| ليه constructor وحالة مشتركة؟ | آه | لأ |
| الابن ينفع يكون جزء من أكتر من hierarchy؟ | لأ | آه |
| ينفع enum ينفذه؟ | لأ | آه |

القاعدة العملية: ابدأ بـ sealed interface. لو احتجت property مشتركة بقيمة في الأب، استخدم sealed class.

### الـ when الشاملة (exhaustive)

```kotlin
fun render(state: ProductsUiState): String = when (state) {
    ProductsUiState.Loading -> "spinner"
    is ProductsUiState.Success -> "list of ${state.products.size}"
    is ProductsUiState.Error -> "error ${state.message}"
}
```

لما تستخدم `when` كـ expression (يعني ليها قيمة بترجع) على sealed type أو enum، لازم تغطي كل الحالات. ولو غطيتهم، مش محتاج `else`.

السحر الحقيقي بيظهر لما تضيف حالة جديدة. لو ضفت `Empty`، كل `when` شاملة في المشروع هتوقف الـ compile برسالة زي:

```
'when' expression must be exhaustive. Add the 'Empty' branch or an 'else' branch.
```

الـ compiler بيديك قايمة بكل مكان لازم تعدّله. ده أمان مجاني.

### ليه `else` خطر؟

```kotlin
fun canCancelRisky(status: OrderStatus) = when (status) {
    OrderStatus.Placed -> true
    else -> false
}
```

الكود ده صح النهارده. بس لو ضفت حالة `OnHold` بكرة، الـ `else` هيقرر بالنيابة عنك إنها مش ممكن تتلغي، من غير ما حد يسألك. على الـ sealed types، اكتب كل حالة صراحة.

### الـ state machine

الـ sealed types ممتازة لوصف حاجة بتتنقل بين حالات بقواعد، زي الأوردر: اتعمل ثم اتشحن ثم وصل، أو اتلغى. كل حالة بتشيل الداتا اللي تخصها بس (رقم الشحنة موجود بس في `Shipped`). وهتشوف ده بشكل كامل في الدرس 18 تحت اسم State pattern.

## امشي مع الكود

### [L01_Enums.kt](../src/main/kotlin/m08_sealed_enums/L01_Enums.kt)

```
  0 CARD [card] -> card, 2% fee
  1 WALLET [wallet] -> e-wallet, 1% fee
  2 CASH [cash] -> cash on delivery
```

كل constant نفّذ الـ abstract function بطريقته، وكل واحد نفّذ property الـ interface.

```
  valueOf("WALLET") = WALLET
  valueOf("BITCOIN") threw IllegalArgumentException
```

الـ `valueOf` بترمي exception لو النص مش موجود. لو بتحوّل داتا جاية من API، استخدم `entries.find { it.name == s }` عشان ترجع null بدل الـ crash.

### [L02_SealedClasses.kt](../src/main/kotlin/m08_sealed_enums/L02_SealedClasses.kt)

```
  spinner
  list of 2: Pen, Mug
  error 'No internet' [Retry]
```

كل حالة اتعرضت بالداتا اللي معاها بس. مفيش طريقة توصل لـ `products` وانت في حالة `Error`.

```
  toString of a data object: Loading
```

الـ `data object` طبع اسمه بس، مش اسم الكلاس ورقم hash.

### [L03_SealedInterfaces.kt](../src/main/kotlin/m08_sealed_enums/L03_SealedInterfaces.kt)

```
  NetworkDown: retry? true
  CardDeclined: retry? false
  OutOfStock: retry? false
  ServerBusy: retry? true
```

الـ `NetworkDown` و `ServerBusy` جزء من hierarchy الأخطاء وكمان من hierarchy تانية للحاجات اللي ينفع نعيد محاولتها. ده مستحيل مع sealed class لأن الكلاس يورث من أب واحد بس.

### [L04_ExhaustiveWhen.kt](../src/main/kotlin/m08_sealed_enums/L04_ExhaustiveWhen.kt)

```
  Order placed | can cancel: true
  On the way (EG123) | can cancel: false
```

الـ `label()` extension على `OrderStatus` بتستخدم when شاملة، والـ `canCancel` كمان.

```
  canCancelRisky : else hides new subtypes, today it returns false for Shipped
```

النتيجة صح النهارده بالصدفة. بس مع أي حالة جديدة، النسخة اللي فيها `else` مش هتنبهك.

## في Compose

### شاشة كاملة مبنية على sealed UiState

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(val products: List<Product>) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

@Composable
fun ProductsScreen(state: ProductsUiState, onRetry: () -> Unit) {
    when (state) {
        ProductsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is ProductsUiState.Success -> LazyColumn {
            items(state.products) { product -> ProductCard(product) }
        }
        is ProductsUiState.Error -> Column {
            Text(state.message)
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}
```

لاحظ:

- الـ `when` هنا شاملة من غير `else`. لو ضفت حالة Empty بكرة، الشاشة دي مش هتعمل compile لحد ما ترسم الحالة الجديدة. ده بيمنع شاشة فاضية من غير سبب.
- الـ smart cast شغال جوه كل فرع: `state.products` متاحة بس في فرع Success.
- الـ `CircularProgressIndicator` اللي من غير parameters هو الـ overload اللي بيلف على طول، مناسب لحالة تحميل مش عارفين مدتها.

### الاختيار بين sealed و data class فيها flags

الـ sealed UiState ممتاز لما الحالات **منفصلة فعلًا**. بس فيه شاشات محتاجة تعرض داتا قديمة **وفي نفس الوقت** بتحمّل داتا جديدة (pull to refresh). ساعتها الـ sealed هيخفي المنتجات وقت الـ refresh، وده مش مطلوب. الحل الشائع:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: ErrorType? = null,          // sealed للأخطاء بس
)

sealed interface ErrorType {
    data object NoInternet : ErrorType
    data class Server(val code: Int) : ErrorType
}
```

يعني بتخلط الاتنين: data class للحالة العامة، و sealed للأجزاء اللي فعلًا ليها شكل مختلف.

### الـ navigation routes

الـ `NavHost` بياخد `startDestination` و lambda فيها الشاشات. بدل ما تكتب أسماء الـ routes كنصوص متفرقة، اجمعها في sealed hierarchy:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Cart : Screen("cart")
    data object ProductDetails : Screen("product/{id}") {
        fun createRoute(id: Int) = "product/$id"
    }
}

@Composable
fun ShopNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(onOpen = { id -> navController.navigate(Screen.ProductDetails.createRoute(id)) }) }
        composable(Screen.Cart.route) { CartScreen() }
        composable(Screen.ProductDetails.route) { ProductDetailsScreen() }
    }
}
```

كده مستحيل تكتب `"prodcut/5"` غلط في حتة، لأن كل route بييجي من مكان واحد.

### ملاحظة عن الـ enums في الـ UI

الـ enum مناسب لاختيارات ثابتة في الـ UI زي ترتيب المنتجات (بالسعر، بالاسم، بالأحدث). وتقدر تلف على `entries` عشان تعمل قايمة الاختيارات أوتوماتيك، وأي قيمة جديدة هتظهر في الـ UI لوحدها.

## أخطاء شائعة

1. الغلطة: فرع `else` في `when` على sealed type.
   الصح: اكتب كل حالة صراحة عشان الـ compiler ينبهك لما تضيف حالة جديدة.

2. الغلطة: data class فيها `isLoading` و `data` و `error` كلهم nullable لحالات منفصلة.
   الصح: sealed type بيمنع الحالات المستحيلة.

3. الغلطة: `valueOf` على نص جاي من برّه من غير حماية.
   الصح: `entries.find { it.name == text }` وتتعامل مع الـ null.

4. الغلطة: الاعتماد على `ordinal` في التخزين أو الـ API.
   الصح: الـ `ordinal` بيتغير لو رتبت القيم. خزّن الـ `name` أو property ثابتة.

5. الغلطة: `object` عادي جوه sealed hierarchy.
   الصح: استخدم `data object` عشان `toString` و `equals` يبقوا مظبوطين.

6. الغلطة: sealed لكل حاجة حتى لو الشاشة محتاجة داتا وتحميل مع بعض.
   الصح: اخلط data class مع sealed للأجزاء المنفصلة فعلًا.

## أسئلة Interview

1. إيه الفرق بين enum و sealed class؟

<details><summary>الإجابة</summary>

الـ enum مجموعة objects ثابتة كلهم نفس الشكل وكل واحد singleton. الـ sealed class مجموعة أنواع ثابتة، وكل نوع ممكن يكون ليه داتا مختلفة وتعمل منه نسخ كتير.

</details>

2. ليه `when` على sealed type مش محتاجة `else`؟

<details><summary>الإجابة</summary>

لأن الـ compiler عارف كل الأبناء، لأنهم لازم يكونوا في نفس الـ package والـ module. فيقدر يتأكد إنك غطيت كل الحالات.

</details>

3. إمتى تستخدم sealed interface بدل sealed class؟

<details><summary>الإجابة</summary>

لما مش محتاج حالة أو constructor مشترك في الأب، أو لما نوع واحد لازم يكون جزء من أكتر من hierarchy، أو لما enum محتاج ينفذه.

</details>

4. إيه الـ `data object` وليه بنستخدمه؟

<details><summary>الإجابة</summary>

هو object معاه `toString` بيطبع الاسم بس و `equals` و `hashCode` مظبوطين. مناسب للحالات اللي مالهاش داتا جوه sealed hierarchy.

</details>

5. ليه sealed UiState مش دايمًا الاختيار الصح؟

<details><summary>الإجابة</summary>

لأن الحالات فيه منفصلة، فمينفعش تعرض داتا قديمة وانت بتحمّل جديدة. في الشاشات اللي فيها refresh، data class مع sealed للأخطاء بيبقى أنسب.

</details>

6. إيه خطر استخدام `ordinal`؟

<details><summary>الإجابة</summary>

بيعتمد على ترتيب الكتابة. لو حد رتّب القيم أو ضاف قيمة في النص، كل الأرقام المتخزنة هتشاور على قيم غلط.

</details>

## Self-check

- [ ] أقدر أختار بين enum و sealed لأي مثال بسرعة.
- [ ] أعرف أكتب enum فيه constructor و abstract function.
- [ ] أعرف أكتب sealed UiState وشاشة Compose بـ `when` شاملة.
- [ ] أقدر أشرح ليه `else` على sealed type خطر.
- [ ] أعرف إمتى أخلط data class مع sealed.
- [ ] ملف Exercises.kt بيطبع ALL PASS

# 13 - Generics and Variance

## المشكلة الأول

الشوب محتاج يخزّن منتجات وعملاء وأوردرات. أول نسخة:

```kotlin
class ProductRepository { fun save(p: Product) { ... }; fun byId(id: Int): Product? { ... } }
class CustomerRepository { fun save(c: Customer) { ... }; fun byId(id: Int): Customer? { ... } }
class OrderRepository { ... }   // نفس الكود للمرة التالتة
```

نفس الكود بالظبط بيتكرر، والفرق الوحيد هو النوع. ولو حاولت تحلها بـ `Any`:

```kotlin
class Repository { fun save(item: Any); fun byId(id: Int): Any? }

val p = repo.byId(1) as Product   // cast في كل حتة، وممكن يقع وقت التشغيل
repo.save("مش منتج")              // مقبول!
```

خسرت الأمان. الـ generics بتديك الاتنين: **كود واحد**، و**نوع محدد** يتأكد منه الـ compiler.

## الـ concept بالتفصيل

### الـ generic class

```kotlin
class Box<T>(val value: T) {
    fun <R> map(transform: (T) -> R): Box<R> = Box(transform(value))
}

val b: Box<Long> = Box(4500L)
val s: Box<String> = b.map { "EGP ${it / 100}" }
```

الـ `T` اسمه type parameter. كل استخدام بيحدد قيمته: `Box<Long>` أو `Box<String>`. الـ compiler بيفهم النوع من الـ arguments في أغلب الأحوال، فمش محتاج تكتبه.

### الـ generic function

```kotlin
fun <T> firstOrDefault(items: List<T>, default: T): T = items.firstOrNull() ?: default
```

الـ `<T>` قبل اسم الـ function. ونفس الكود بيشتغل مع `List<String>` و `List<Int>` وأي نوع.

### الـ constraints

أحيانًا محتاج تنادي حاجة على `T`. ساعتها لازم تقول للـ compiler إيه اللي `T` يقدر يعمله:

```kotlin
fun <T : Comparable<T>> largest(items: List<T>): T? { ... item > best ... }
```

الـ `T : Comparable<T>` معناها إن أي نوع هتباصيه لازم يكون بيعرف يقارن نفسه بنفسه. فجوه الـ function تقدر تستخدم `>`. ولو حاولت تباصي نوع مش comparable، الـ compiler هيرفض.

ولو محتاج أكتر من شرط:

```kotlin
fun <T> cheapestLabel(items: List<T>): String where T : Named, T : Priced
```

### الـ repository الـ generic

```kotlin
interface Entity { val id: Int }

class InMemoryRepository<T : Entity> : Repository<T> {
    private val items = linkedMapOf<Int, T>()
    override fun save(item: T) { items[item.id] = item }
}
```

الـ `T : Entity` بيضمن إن أي حاجة متخزنة ليها `id`. و `InMemoryRepository<Product>` مش هيقبل `Customer`.

### الـ variance: السؤال الأصعب

الكتاب نوع من المنتجات (`Book : Item`). هل علبة كتب هي علبة منتجات؟ الإجابة: **حسب العلبة بتعمل إيه**.

تخيّل لو `MutableList<Book>` كانت تتعامل كأنها `MutableList<Item>`:

```kotlin
val books: MutableList<Book> = mutableListOf(Book("A"))
val items: MutableList<Item> = books   // لو ده كان مسموح...
items.add(Item("pen"))                 // ...قلم دخل جوه list الكتب
val b: Book = books[1]                 // crash
```

عشان كده فيه تلات أنواع من العلاقات:

| النوع | الكتابة | يعني إيه | مثال |
|---|---|---|---|
| الـ invariant | `Box<T>` | مفيش علاقة خالص | الـ `MutableList` |
| الـ covariant | `Source<out T>` | علبة كتب هي علبة منتجات | الـ `List` |
| الـ contravariant | `Sink<in T>` | مستقبل المنتجات هو مستقبل كتب | الـ `Comparator` |

### الـ out (covariance)

```kotlin
interface Source<out T> { fun next(): T }
```

الـ `out` وعد إن `T` بيطلع بس، ومش بيدخل أبدًا (مفيش function بتاخد `T` كـ parameter). فلو حاجة بتطلّع كتب، تقدر تعتبرها بتطلّع منتجات، لأن أي كتاب هو منتج. الـ `List<out E>` في Kotlin مكتوبة كده، وده سبب إن `List<Book>` بتتحط في `List<Item>` عادي.

### الـ in (contravariance)

```kotlin
interface Sink<in T> { fun put(item: T): String }
```

الـ `in` وعد إن `T` بيدخل بس. فلو حاجة بتقبل أي منتج، أكيد هتقبل كتاب. يعني `Sink<Item>` ينفع يتحط مكان `Sink<Book>`. الاتجاه معكوس، وده سبب اسم contravariance.

قاعدة الحفظ: **Producer out, Consumer in**.

### الـ use-site variance

لو الكلاس نفسه invariant بس انت هتقرا منه بس في function معينة:

```kotlin
fun printAll(items: Array<out Item>) = items.joinToString { it.name }
printAll(arrayOf(Book("X")))
```

الـ `Array<out Item>` معناها "أنا هقرا بس من الـ array دي". ده شبه `? extends Item` في Java. والـ `in` شبه `? super`.

### الـ type erasure

وقت التشغيل الـ JVM بيمسح الـ type arguments. يعني `List<String>` و `List<Int>` الاتنين بيبقوا `List` بس. فالسطر ده مش بيعمل compile:

```kotlin
if (x is List<String>)   // error: Cannot check for instance of erased type
```

### الـ reified

```kotlin
inline fun <reified T> List<Any>.filterIsType(): List<T> = filter { it is T }.map { it as T }

mixed.filterIsType<String>()
```

الـ `inline` بتخلي الـ compiler ينسخ جسم الـ function مكان كل نداء، فبيقدر يحط النوع الحقيقي مكان `T`. والـ `reified` بتسمح لك تستخدم `T` كنوع حقيقي: `is T` و `T::class`. الشرط إن الـ function تكون `inline`.

### الـ star projection

```kotlin
fun describe(list: List<*>): String
```

الـ `List<*>` معناها "list فيها حاجة، مش مهم إيه". تقدر تقرا منها كـ `Any?`، بس مش هتقدر تضيف لها حاجة لأنك مش عارف النوع.

## امشي مع الكود

### [L01_GenericClassesAndFunctions.kt](../src/main/kotlin/m13_generics/L01_GenericClassesAndFunctions.kt)

```
  Box(4500) -> Box(EGP 45)
```

الـ `map` حوّلت `Box<Long>` لـ `Box<String>` بأمان.

```
  products  = [Product(id=1, name=Pen), Product(id=2, name=Mug)]
  customer1 = Customer(id=1, email=sara@mail.com)
```

نفس الـ repository اشتغل مع نوعين، وكل واحد بيرجّع النوع الصح من غير cast.

```
  Ok(value=1250)
  Failed(reason=not a number: abc)
```

الـ `Failed` نوعها `Outcome<Nothing>`. وبما إن `Nothing` تحت كل الأنواع و `Outcome` مكتوبة بـ `out`، الـ `Failed` تنفع مكان أي `Outcome<T>`.

### [L02_Constraints.kt](../src/main/kotlin/m13_generics/L02_Constraints.kt)

```
  9
  zucchini
  null
```

الـ `largest` اشتغلت مع أرقام ونصوص، لأن الاتنين `Comparable`.

```
  Cable (9000)
```

الـ function وصلت لـ `name` و `priceCents` على `T` لأن الـ `where` اشترط الاتنين.

### [L03_Variance.kt](../src/main/kotlin/m13_generics/L03_Variance.kt)

```
  Kotlin in Action
  binned Clean Code
```

الـ `BookShelf` (منتج كتب) اتباصى مكان `Source<Item>`. والـ `AnyItemBin` (مستقبل منتجات) اتباصى مكان `Sink<Book>`.

```
  read-only view sees 1 item(s)
```

الـ `MutableList<Book>` مارضيتش تبقى `MutableList<Item>`، بس رضيت تبقى `List<Item>` لأن `List` covariant.

### [L04_ReifiedAndStar.kt](../src/main/kotlin/m13_generics/L04_ReifiedAndStar.kt)

```
  strings = [Pen, Mug]
  ints    = [500, 42]
```

الـ `reified` خلّى `is T` يشتغل وقت التشغيل.

```
  String
  Book
```

حتى اسم النوع نفسه متاح جوه الـ function.

## في Compose

### الـ State<out T>

```kotlin
public interface State<out T> {
    public val value: T
}
```

الـ `State` بيطلّع قيمة بس، فهو covariant. ده معناه إن الكود ده بيعمل compile:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ItemTitle(item: State<Item>) {
    Text(item.value.name)
}

@Composable
fun BookScreen() {
    val book: State<Book> = remember { mutableStateOf(Book("Kotlin")) }
    ItemTitle(book)   // State<Book> مقبولة كـ State<Item>
}
```

وبالمقارنة، `MutableState<T>` من غير `out`، لأن فيه `var value` يعني `T` بيدخل ويطلع. فمينفعش `MutableState<Book>` تتحط مكان `MutableState<Item>`، ولو اتسمح كان حد ممكن يكتب منتج عادي جوه حالة كتاب. نفس منطق `MutableList` بالظبط. والـ `StateFlow` كمان مكتوب `StateFlow<out T>` لنفس السبب.

### الـ LazyColumn والـ items<T>

```kotlin
public inline fun <T> LazyListScope.items(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
)
```

الـ `items` generic، فالـ lambda بتاخد النوع الصح من غير cast:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun ProductList(products: List<Product>, onClick: (Product) -> Unit) {
    LazyColumn {
        items(products, key = { it.id }) { product ->
            Text(product.name, Modifier.clickable { onClick(product) })
        }
    }
}
```

الـ `product` جوه الـ lambda نوعه `Product`، والـ compiler عرف ده من الـ list.

### الـ composable الـ generic بتاعك

تقدر تعمل قايمة تشتغل مع أي نوع:

ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.

```kotlin
@Composable
fun <T> SelectableList(
    items: List<T>,
    selected: T?,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    LazyColumn {
        items(items) { item ->
            val prefix = if (item == selected) "> " else "  "
            Text(prefix + label(item), Modifier.clickable { onSelect(item) })
        }
    }
}

// نفس الـ composable لطرق الشحن ولطرق الدفع
SelectableList(shippingMethods, selectedShipping, { it.name }, onShippingChange)
SelectableList(PaymentMethod.entries, selectedPayment, { it.name }, onPaymentChange)
```

### الـ reified في Android

الـ `viewModels` مكتوبة كده:

```kotlin
public inline fun <reified VM : ViewModel> ComponentActivity.viewModels(...): Lazy<VM>
```

الـ `reified VM : ViewModel` بيخليها تعرف نوع الـ ViewModel وقت التشغيل عشان تعمل منه نسخة، والـ bound بيضمن إنه ViewModel فعلًا. فبتكتب `by viewModels<CartViewModel>()` أو بس `by viewModels()` والـ compiler يفهم النوع من الـ property.

## أخطاء شائعة

1. الغلطة: `Any` و casts بدل generics.
   الصح: type parameter، والـ compiler يتأكد من النوع.

2. الغلطة: الاستغراب من إن `MutableList<Book>` مش `MutableList<Item>`.
   الصح: ده مقصود عشان الأمان. استخدم `List<Item>` لو هتقرا بس.

3. الغلطة: `is List<String>`.
   الصح: النوع بيتمسح. استخدم `filterIsInstance` أو `inline reified`.

4. الغلطة: `reified` في function مش `inline`.
   الصح: الـ reified محتاج `inline` عشان النوع يتنسخ مكان النداء.

5. الغلطة: `out` على type parameter بيدخل كـ parameter.
   الصح: الـ compiler هيرفض. الـ `out` بس لو `T` بيطلع، والـ `in` بس لو بيدخل.

## أسئلة Interview

1. إيه فايدة الـ generics؟

<details><summary>الإجابة</summary>

تكتب كود واحد يشتغل مع أنواع كتير، مع الحفاظ على أمان الأنواع وقت الـ compile، ومن غير casts.

</details>

2. اشرح `out` و `in` بمثال.

<details><summary>الإجابة</summary>

الـ `out T` معناها إن `T` بيطلع بس، فـ `Source<Book>` ينفع مكان `Source<Item>`. الـ `in T` معناها إن `T` بيدخل بس، فـ `Sink<Item>` ينفع مكان `Sink<Book>`. القاعدة: Producer out و Consumer in.

</details>

3. ليه `MutableList` invariant و `List` covariant؟

<details><summary>الإجابة</summary>

لأن `MutableList` بتقبل إضافة، فلو كانت covariant كان ممكن تضيف `Item` عادي جوه list كتب. الـ `List` للقراية بس، فمفيش خطر.

</details>

4. إيه هو الـ type erasure، وإزاي `reified` بيحله؟

<details><summary>الإجابة</summary>

الـ JVM بيمسح الـ type arguments وقت التشغيل، فمينفعش تعمل `is T`. الـ `inline` مع `reified` بينسخوا جسم الـ function مكان كل نداء بالنوع الحقيقي، فالنوع بيبقى معروف.

</details>

5. ليه `State` في Compose مكتوب `State<out T>`؟

<details><summary>الإجابة</summary>

لأنه بيطلّع القيمة بس. ده بيخلّي `State<Book>` تنفع مكان `State<Item>`. أما `MutableState` فـ invariant لأنه بيقبل كتابة.

</details>

6. إيه الفرق بين `List<*>` و `List<Any?>`؟

<details><summary>الإجابة</summary>

الـ `List<Any?>` list بتشيل أي حاجة. الـ `List<*>` list فيها نوع معين مش معروف لك، فتقرا منها كـ `Any?` بس. والفرق بيبان أكتر مع الأنواع اللي بتقبل كتابة: `MutableList<*>` مش هتقبل إضافة أي حاجة.

</details>

## Self-check

- [ ] أقدر أكتب generic class و generic function.
- [ ] أعرف أحط constraint بـ `:` وبـ `where`.
- [ ] أقدر أشرح ليه `MutableList<Book>` مش `MutableList<Item>` بمثال.
- [ ] أعرف أكتب interface بـ `out` وتاني بـ `in`.
- [ ] أعرف أكتب `inline reified` function وأعرف ليه بتحتاج inline.
- [ ] أقدر أكتب composable generic زي `SelectableList`.
- [ ] ملف Exercises.kt بيطبع ALL PASS

# Kotlin OOP Mastery

مشروع Kotlin عادي (من غير Android) بيشرح كل مفاهيم الـ OOP في Kotlin من الصفر لحد الاحتراف. كل مفهوم ليه module فيه دروس بتتشغّل، وتمارين بتتصحّح أوتوماتيك، وملف شرح `.md` عميق فيه أمثلة حقيقية من Jetpack Compose.

## إزاي تفتحه وتشغّله

1. افتح IntelliJ واختار `File` ثم `Open` واختار الفولدر `kotlin-oop-mastery` نفسه.
2. لو سألك Trust project اختار Trust، واستنى أول Gradle sync يخلص.
3. افتح أي ملف درس زي `L01_Constructors.kt`، ودوس على السهم الأخضر اللي جنب `fun main()`.
4. الـ output بيظهر في الـ Run window، وكل درس ليه نسخة من الـ output المتوقع في فولدر `expected-output`.

## إزاي تذاكر كل module

1. اقرا ملف الشرح في فولدر `docs` الأول.
2. شغّل الدروس بالترتيب وقارن الـ output بالشرح.
3. حل التمارين في ملف `Exercises.kt` (امسح `TODO()` واكتب الحل).
4. شغّل `Exercises.kt` لحد ما يطبع ALL PASS.
5. قارن حلّك بملف `Solutions.kt` بعد ما تخلص، مش قبل.

## الـ modules

| الـ module | الموضوع | أهم فكرة | ملف الشرح |
|---|---|---|---|
| الدرس 00 | الصورة الكاملة للـ OOP | الداتا والقواعد اللي بتحميها في وحدة واحدة | [00_oop_mental_model.md](docs/00_oop_mental_model.md) |
| الدرس 01 | الكلاسات والـ objects | الـ object يتولد سليم أو مايتولدش | [01_classes_and_objects.md](docs/01_classes_and_objects.md) |
| الدرس 02 | الـ encapsulation | حماية الـ invariants، و `List` مش immutable | [02_encapsulation.md](docs/02_encapsulation.md) |
| الدرس 03 | الوراثة | الكلاسات final بشكل افتراضي، والأب بيتبني الأول | [03_inheritance.md](docs/03_inheritance.md) |
| الدرس 04 | الـ abstract classes | الأب يملك الترتيب والأبناء يملكوا التفاصيل | [04_abstract_classes.md](docs/04_abstract_classes.md) |
| الدرس 05 | الـ interfaces | عقد من غير حالة، و `Modifier` نفسه interface | [05_interfaces.md](docs/05_interfaces.md) |
| الدرس 06 | الـ polymorphism | الـ members بتتحدد وقت التشغيل والـ extensions لأ | [06_polymorphism.md](docs/06_polymorphism.md) |
| الدرس 07 | الـ data classes | التغيير بـ `copy` والـ stability في Compose | [07_data_classes.md](docs/07_data_classes.md) |
| الدرس 08 | الـ sealed والـ enums | الـ `when` الشاملة من غير `else` | [08_sealed_and_enums.md](docs/08_sealed_and_enums.md) |
| الدرس 09 | الـ object والـ companion | الـ companion حقيقي، وهو أول `Modifier` في أي سلسلة | [09_object_and_companion.md](docs/09_object_and_companion.md) |
| الدرس 10 | الـ value classes | أمان الأنواع بتكلفة صفر زي `Dp` | [10_value_classes.md](docs/10_value_classes.md) |
| الدرس 11 | الـ nested والـ inner والـ anonymous | الـ inner بيمسك في الخارجي وممكن يعمل leak | [11_nested_inner_anonymous.md](docs/11_nested_inner_anonymous.md) |
| الدرس 12 | الـ delegation | معنى `by remember { mutableStateOf() }` | [12_delegation.md](docs/12_delegation.md) |
| الدرس 13 | الـ generics والـ variance | الـ Producer out و Consumer in | [13_generics_and_variance.md](docs/13_generics_and_variance.md) |
| الدرس 14 | الـ extensions والـ receivers | ليه `weight` متاحة جوه `Row` بس | [14_extensions_and_receivers.md](docs/14_extensions_and_receivers.md) |
| الدرس 15 | الـ operators والمساواة | الـ `equals` و `hashCode` و `compareTo` لازم يتفقوا | [15_operators_and_equality.md](docs/15_operators_and_equality.md) |
| الدرس 16 | الـ composition | السؤال "عنده إيه" بدل "هو نوع من إيه" | [16_composition_over_inheritance.md](docs/16_composition_over_inheritance.md) |
| الدرس 17 | الـ SOLID | خمس أسئلة تسألها لما الكود يوجعك | [17_solid.md](docs/17_solid.md) |
| الدرس 18 | الـ design patterns | الـ Modifier chain هو Decorator | [18_design_patterns.md](docs/18_design_patterns.md) |

## ترتيب المذاكرة المقترح

1. الأساس من الدرس 00 للدرس 08: الكلاسات والأعمدة الأربعة والـ data والـ sealed. ذاكرهم بالترتيب ومتعديش درس قبل ما التمرين يطبع ALL PASS.
2. أدوات Kotlin من الدرس 09 للدرس 15: الـ object والـ value classes والـ delegation والـ generics والـ extensions والـ operators. دي اللي بتخليك تقرا كود Compose وتفهمه.
3. التصميم من الدرس 16 للدرس 18: الـ composition والـ SOLID والـ patterns. دول بيجمعوا كل اللي فات.

وللمراجعة السريعة في أي وقت افتح [cheatsheet.md](docs/cheatsheet.md).

## أوامر التحقق

الأوامر دي بتتأكد إن كل الدروس والتمارين والشرح سليمين:

```
./gradlew verifyAll
python tools/check_bidi.py docs README.md
python tools/check_compose_refs.py
```

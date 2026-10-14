# Kotlin OOP Mastery

دليلك الكامل لاحتراف الـ OOP في Kotlin، من أول "يعني إيه object" لحد الـ SOLID والـ design patterns، وكل مفهوم مربوط بأمثلة حقيقية من Jetpack Compose.

## الريبو دي عبارة عن إيه؟

الريبو دي **كورس كامل تذاكره بنفسك**. مش مجرد شرح تقراه، ده مشروع Kotlin حقيقي فيه:

- ملفات شرح عميقة بالعامية المصرية لكل مفهوم (19 ملف، كل واحد من 300 لـ 400 سطر).
- دروس Kotlin بتتشغّل، كل درس بيطبع output بيوريك المفهوم بعينك (63 درس).
- تمارين بتتصحّح أوتوماتيك وبتقولك PASS أو FAIL لكل سؤال.
- حلول نموذجية تقارن بيها بعد ما تخلص.
- أمثلة Compose حقيقية في كل ملف شرح، والـ APIs اللي فيها متراجعة على الـ source الرسمي.
- ملخص صفحة واحدة للمراجعة السريعة.

المشروع ده Kotlin عادي على الـ JVM، **من غير Android**. يعني بيفتح في IntelliJ ويشتغل في ثانية، من غير emulator ولا Gradle sync تقيل. أمثلة Compose موجودة في ملفات الشرح للقراية والفهم، مش للتشغيل.

## الكورس ده لمين؟

- لو بتكتب Kotlin وحاسس إن مفاهيم زي `sealed` و `by` و `out` و `companion` مش واضحة في دماغك.
- لو بتكتب Compose ومش فاهم ليه `Modifier.weight` شغالة جوه `Row` بس، أو إيه اللي بيحصل في `by remember`.
- لو بتجهز لـ interview في Android أو Kotlin.
- لو جاي من Java وعايز تعرف Kotlin بتختلف في إيه بالظبط.

المطلوب منك قبل ما تبدأ: تعرف أساسيات Kotlin (المتغيرات، الـ functions، الـ if والـ when، الـ lists، والـ lambdas البسيطة).

## الهدف النهائي

لما تخلص الكورس، المفروض تقدر:

1. تشرح أي مفهوم OOP في Kotlin بكلامك ومثال من دماغك.
2. تتوقع output أي كود فيه وراثة أو delegation أو generics قبل ما تشغّله.
3. تقرا أي كود Compose وتعرف كل مفهوم OOP متخبي وراه.
4. تصمم كلاسات نظيفة بتحترم الـ SOLID وتختار الـ pattern المناسب.
5. تجاوب أسئلة الـ interview في كل الموضوعات دي بثقة.

## خريطة الريبو

```
kotlin-oop-mastery/
├── README.md                     <- انت هنا: الدليل
├── docs/                         <- ملفات الشرح (ابدأ من هنا في كل درس)
│   ├── 00_oop_mental_model.md
│   ├── 01_classes_and_objects.md
│   ├── ...
│   ├── 18_design_patterns.md
│   ├── cheatsheet.md             <- ملخص الكورس كله في صفحة
│   └── reference/
│       └── compose_signatures.md <- الـ Compose APIs الرسمية اللي الشرح بيستشهد بيها
├── src/main/kotlin/
│   ├── common/                   <- helpers للطباعة والتصحيح (مش محتاج تلمسها)
│   ├── tools/                    <- أداة التحقق (مش محتاج تلمسها)
│   ├── m00_mental_model/         <- كل module فولدر لوحده
│   │   ├── L01_....kt            <- الدروس: شغّلها بالترتيب
│   │   ├── L02_....kt
│   │   ├── shop/                 <- نسخة الشوب الخاصة بالدرس ده (لو موجودة)
│   │   └── exercises/
│   │       ├── Exercises.kt      <- انت بتحل هنا
│   │       ├── Checks.kt         <- الأسئلة والتصحيح (اقراه، ماتعدّلوش)
│   │       └── solutions/
│   │           └── Solutions.kt  <- الحل النموذجي (افتحه بعد ما تخلص)
│   ├── m01_classes/
│   └── ...                       <- لحد m18_patterns
├── expected-output/              <- الـ output المتوقع لكل درس
└── tools/                        <- scripts بتراجع تنسيق ملفات الشرح
```

## تشكيلة كل module

كل module مبني بنفس الشكل بالظبط، فبعد أول module هتعرف تتحرك لوحدك:

| الجزء | مكانه | دوره |
|---|---|---|
| ملف الشرح | فولدر `docs` | المفهوم بالتفصيل وأمثلة Compose وأسئلة interview |
| الدروس | ملفات `L01` و `L02` وهكذا | كود بيتشغّل وبيوريك المفهوم بالـ output |
| الشوب | فولدر `shop` | نفس المتجر بيكبر ويتطور من درس للتاني |
| التمارين | ملف `Exercises.kt` | مكان الحل بتاعك |
| التصحيح | ملف `Checks.kt` | الأسئلة والاختبارات |
| الحل | ملف `Solutions.kt` | الحل النموذجي للمقارنة |
| الـ output المتوقع | فولدر `expected-output` | تقارن بيه لو حاجة طلعت غريبة |

### ملف الشرح من جوه

كل ملف شرح ماشي على نفس الترتيب:

1. **المشكلة الأول**: موقف حقيقي من الشوب بيوريك ليه المفهوم ده موجود أصلًا.
2. **الـ concept بالتفصيل**: القواعد، وإزاي الـ compiler بيشوفها، والفرق عن Java.
3. **امشي مع الكود**: شرح output كل درس سطر بسطر.
4. **في Compose**: فين المفهوم ده مستخدم في Compose نفسه، بأمثلة كاملة.
5. **أخطاء شائعة**: الغلطة والصح.
6. **أسئلة Interview**: والإجابات مستخبية تحت كلمة "الإجابة"، جاوب الأول وبعدين افتحها.
7. **الـ Self-check**: قايمة تعلّم عليها، ولو كلها اتعلّمت يبقى خلصت الـ module.

### قصة الشوب

كل الأمثلة بتدور حوالين متجر إلكتروني واحد: منتجات وسلة وأوردرات ودفع وشحن. المتجر بيتطور معاك:

```
m01  Product عادي فيه validation
m02  Cart محمي من التعديل من برّه
m03  Payment وأبناؤه (كارت، كاش)
m05  ShippingMethod كـ interface
m07  Product بقى data class
m08  OrderStatus بقى sealed
m10  Price بقى value class
m12  LoggingCart بالـ delegation
m13  Repository<T> generic
m17  Checkout متفكك على مبادئ SOLID
m18  Strategy و State و Decorator و Adapter
```

كل module عنده نسخة الشوب الخاصة بيه في فولدر `shop`، فتقدر ترجع لأي مرحلة وتشوف الكود كان شكله إيه.

## التجهيز (مرة واحدة)

1. نزّل IntelliJ IDEA (النسخة المجانية Community كفاية) أو استخدم Android Studio.
2. افتح IntelliJ واختار `File` ثم `Open`، واختار الفولدر `kotlin-oop-mastery` نفسه.
3. لو سألك Trust project اختار Trust.
4. استنى أول Gradle sync يخلص (بيحمّل Kotlin بس، مفيش أي مكتبات تانية).
5. للتجربة: افتح `src/main/kotlin/m00_mental_model/L01_ProceduralVsObjects.kt` ودوس على السهم الأخضر جنب `fun main()`.

لو شفت الـ output في الـ Run window، كله تمام وجاهز تبدأ.

## إزاي تذاكر الـ module الواحد

دي الخطوات اللي تمشي عليها في **كل** module، بالترتيب:

1. **اقرا "المشكلة الأول" بس** في ملف الشرح. حاول تفكر: انت كنت هتحل المشكلة دي إزاي؟
2. **اقرا "الـ concept بالتفصيل"** كامل، وعلى مهلك.
3. **شغّل كل درس بالترتيب**. قبل ما تدوس Run، خمّن الـ output في دماغك. بعد التشغيل قارن.
4. **اقرا "امشي مع الكود"** عشان تتأكد إنك فهمت كل سطر في الـ output صح.
5. **العب في الكود**: غيّر قيمة، شيل كلمة `open`، ضيف حالة لـ sealed. شوف إيه اللي بيكسر وليه.
6. **اقرا "في Compose"**. ده الجزء اللي بيربط المفهوم بشغلك الحقيقي.
7. **حل التمارين**: افتح `Checks.kt` اقرا الأسئلة، وبعدين اكتب الحل في `Exercises.kt` مكان كل `TODO()`.
8. **شغّل `Exercises.kt`** لحد ما يطبع ALL PASS.
9. **قارن بـ `Solutions.kt`**. حتى لو حلك شغال، ممكن تلاقي طريقة أنظف.
10. **جاوب أسئلة الـ Interview** بصوت عالي أو اكتبها، وبعدين افتح الإجابة.
11. **علّم على الـ Self-check**. لو فيه نقطة مش متأكد منها، ارجع لها قبل ما تكمل.

### شكل التصحيح

لما تشغّل `Exercises.kt` قبل ما تحل، هتشوف حاجة زي كده:

```
============================================================
  m01 checks
============================================================
  [FAIL (not implemented yet)] price label formats cents
  [FAIL (not implemented yet)] negative price rejected
  ...
  Result: 0 passed, 8 failed
```

ومع كل حل صح، الـ FAIL بتتحول PASS. ولما كله يعدّي:

```
  Result: 8 passed, 0 failed
  ALL PASS - module mastered.
```

قاعدة مهمة: **ماتفتحش `Solutions.kt` قبل ما تحاول بجد**. لو وقفت، ارجع لملف الشرح الأول، وبعدها للتلميح المكتوب فوق كل `TODO()`.

## الـ Path بتاعك

الكورس متقسم لأربع مراحل. كل مرحلة بتبني على اللي قبلها، فامشي بالترتيب.

### المرحلة 1: الأساس (الأسبوع الأول)

الهدف إنك تفهم يعني إيه object وتتقن الكلاسات والحماية والوراثة.

| اليوم | الـ module | هتطلع منه بإيه |
|---|---|---|
| اليوم 1 | الدرس 00: الصورة الكاملة | ليه الـ OOP أصلًا، والأعمدة الأربعة، وشجرة الأنواع |
| اليوم 2 | الدرس 01: الكلاسات | الـ constructors وترتيب الـ init والـ properties والـ lazy |
| اليوم 3 | الدرس 02: الـ encapsulation | الحماية الحقيقية وفخ الـ `List` وشكل الـ ViewModel |
| اليوم 4 و 5 | الدرس 03: الوراثة | الـ open والـ override وفخ الـ constructor |

نقطة تفتيش: تقدر تشرح ليه `title` طلع null في درس الوراثة التالت؟ لو آه، كمّل.

### المرحلة 2: التجريد والأنواع (الأسبوع التاني)

الهدف إنك تعرف تصمم عقود وتختار النوع الصح لكل موقف.

| اليوم | الـ module | هتطلع منه بإيه |
|---|---|---|
| اليوم 6 | الدرس 04: الـ abstract | الـ template method وإزاي `Painter` متصمم |
| اليوم 7 | الدرس 05: الـ interfaces | العقود والـ diamond والـ fun interface، و `Modifier` |
| اليوم 8 | الدرس 06: الـ polymorphism | الـ dispatch وقت التشغيل مقابل وقت الـ compile |
| اليوم 9 | الدرس 07: الـ data classes | الـ copy والـ immutability والـ stability في Compose |
| اليوم 10 | الدرس 08: الـ sealed والـ enums | الـ UiState والـ when الشاملة |

نقطة تفتيش: تقدر تختار بين enum و sealed و data class لأي حالة شاشة في ثواني؟

### المرحلة 3: أدوات Kotlin القوية (الأسبوع التالت)

الهدف إنك تقرا أي كود Compose وتفهم كل كلمة فيه.

| اليوم | الـ module | هتطلع منه بإيه |
|---|---|---|
| اليوم 11 | الدرس 09: الـ object والـ companion | الـ singletons والـ factories، وليه بنبدأ بكلمة `Modifier` |
| اليوم 12 | الدرس 10: الـ value classes | الـ `Dp` و `Color` من جوه |
| اليوم 13 | الدرس 11: الـ nested والـ inner | الـ memory leaks والـ anonymous objects |
| اليوم 14 و 15 | الدرس 12: الـ delegation | معنى `by remember { mutableStateOf() }` بالظبط |
| اليوم 16 و 17 | الدرس 13: الـ generics | الـ out والـ in والـ reified، و `State<out T>` |
| اليوم 18 | الدرس 14: الـ extensions | الـ DSLs، وليه `weight` جوه `Row` بس |
| اليوم 19 | الدرس 15: الـ operators | الـ `Dp + Dp` وعقود المساواة |

نقطة تفتيش: افتح أي شاشة Compose في مشروع من مشاريعك، وحاول تسمّي كل مفهوم OOP فيها.

### المرحلة 4: التصميم (الأسبوع الرابع)

الهدف إنك تصمم كود نظيف وتتكلم عنه بلغة المحترفين.

| اليوم | الـ module | هتطلع منه بإيه |
|---|---|---|
| اليوم 20 | الدرس 16: الـ composition | ليه Compose مافيهوش وراثة، والـ slot APIs |
| اليوم 21 و 22 | الدرس 17: الـ SOLID | الخمس مبادئ بأمثلة before و after، وتمرين refactor حقيقي |
| اليوم 23 و 24 | الدرس 18: الـ patterns | الـ Strategy والـ State والـ Decorator والـ Observer في Compose |
| اليوم 25 | المراجعة | الـ cheatsheet كامل وأسئلة الـ interview من كل الدروس |

نقطة تفتيش نهائية: تقدر تشرح ليه ترتيب الـ modifiers بيفرق، باستخدام الـ Decorator pattern؟

### لو وقتك أقل

- **مسار سريع للـ interview (أسبوع)**: الدروس 01 و 03 و 05 و 06 و 07 و 08 و 12 و 13 و 17، وأسئلة الـ interview في كل ملف.
- **مسار Compose بس**: اقرا جزء "في Compose" في كل الملفات بالترتيب، وارجع للدرس الكامل لما حاجة تقف معاك.
- الجدول متوقع ساعة لساعتين في اليوم. لو بتذاكر أكتر، ادمج يومين في يوم، بس **ماتعديش module قبل ما تمريناته تعدّي كلها**.

## تتبّع تقدمك

انسخ القايمة دي في ملف عندك أو علّم عليها هنا:

- [ ] الدرس 00: الصورة الكاملة للـ OOP
- [ ] الدرس 01: الكلاسات والـ objects
- [ ] الدرس 02: الـ encapsulation
- [ ] الدرس 03: الوراثة
- [ ] الدرس 04: الـ abstract classes
- [ ] الدرس 05: الـ interfaces
- [ ] الدرس 06: الـ polymorphism
- [ ] الدرس 07: الـ data classes
- [ ] الدرس 08: الـ sealed والـ enums
- [ ] الدرس 09: الـ object والـ companion
- [ ] الدرس 10: الـ value classes
- [ ] الدرس 11: الـ nested والـ inner والـ anonymous
- [ ] الدرس 12: الـ delegation
- [ ] الدرس 13: الـ generics والـ variance
- [ ] الدرس 14: الـ extensions والـ receivers
- [ ] الدرس 15: الـ operators والمساواة
- [ ] الدرس 16: الـ composition
- [ ] الدرس 17: الـ SOLID
- [ ] الدرس 18: الـ design patterns

## فهرس الـ modules

| الـ module | الموضوع | أهم فكرة | ملف الشرح | الدروس |
|---|---|---|---|---|
| الدرس 00 | الصورة الكاملة للـ OOP | الداتا والقواعد اللي بتحميها في وحدة واحدة | [00_oop_mental_model.md](docs/00_oop_mental_model.md) | عدد 3 |
| الدرس 01 | الكلاسات والـ objects | الـ object يتولد سليم أو مايتولدش | [01_classes_and_objects.md](docs/01_classes_and_objects.md) | عدد 4 |
| الدرس 02 | الـ encapsulation | حماية الـ invariants، و `List` مش immutable | [02_encapsulation.md](docs/02_encapsulation.md) | عدد 3 |
| الدرس 03 | الوراثة | الكلاسات final بشكل افتراضي، والأب بيتبني الأول | [03_inheritance.md](docs/03_inheritance.md) | عدد 4 |
| الدرس 04 | الـ abstract classes | الأب يملك الترتيب والأبناء يملكوا التفاصيل | [04_abstract_classes.md](docs/04_abstract_classes.md) | عدد 3 |
| الدرس 05 | الـ interfaces | عقد من غير حالة، و `Modifier` نفسه interface | [05_interfaces.md](docs/05_interfaces.md) | عدد 4 |
| الدرس 06 | الـ polymorphism | الـ members بتتحدد وقت التشغيل والـ extensions لأ | [06_polymorphism.md](docs/06_polymorphism.md) | عدد 3 |
| الدرس 07 | الـ data classes | التغيير بـ `copy` والـ stability في Compose | [07_data_classes.md](docs/07_data_classes.md) | عدد 3 |
| الدرس 08 | الـ sealed والـ enums | الـ `when` الشاملة من غير `else` | [08_sealed_and_enums.md](docs/08_sealed_and_enums.md) | عدد 4 |
| الدرس 09 | الـ object والـ companion | الـ companion حقيقي، وهو أول `Modifier` في أي سلسلة | [09_object_and_companion.md](docs/09_object_and_companion.md) | عدد 3 |
| الدرس 10 | الـ value classes | أمان الأنواع بتكلفة صفر زي `Dp` | [10_value_classes.md](docs/10_value_classes.md) | عدد 3 |
| الدرس 11 | الـ nested والـ inner والـ anonymous | الـ inner بيمسك في الخارجي وممكن يعمل leak | [11_nested_inner_anonymous.md](docs/11_nested_inner_anonymous.md) | عدد 3 |
| الدرس 12 | الـ delegation | معنى `by remember { mutableStateOf() }` | [12_delegation.md](docs/12_delegation.md) | عدد 4 |
| الدرس 13 | الـ generics والـ variance | الـ Producer out و Consumer in | [13_generics_and_variance.md](docs/13_generics_and_variance.md) | عدد 4 |
| الدرس 14 | الـ extensions والـ receivers | ليه `weight` متاحة جوه `Row` بس | [14_extensions_and_receivers.md](docs/14_extensions_and_receivers.md) | عدد 4 |
| الدرس 15 | الـ operators والمساواة | الـ `equals` و `hashCode` و `compareTo` لازم يتفقوا | [15_operators_and_equality.md](docs/15_operators_and_equality.md) | عدد 3 |
| الدرس 16 | الـ composition | السؤال "عنده إيه" بدل "هو نوع من إيه" | [16_composition_over_inheritance.md](docs/16_composition_over_inheritance.md) | عدد 3 |
| الدرس 17 | الـ SOLID | خمس أسئلة تسألها لما الكود يوجعك | [17_solid.md](docs/17_solid.md) | عدد 5 |
| الدرس 18 | الـ design patterns | الـ Modifier chain هو Decorator | [18_design_patterns.md](docs/18_design_patterns.md) | عدد 4 |

وللمراجعة السريعة في أي وقت: [cheatsheet.md](docs/cheatsheet.md).

## نصايح عشان تحترف فعلًا

- **خمّن قبل ما تشغّل**. أكبر فايدة في الدروس إنك تتوقع الـ output الأول. لو طلع غير توقعك، ده أهم لحظة تعلّم.
- **اكسر الكود عن قصد**. شيل `open`، ضيف `else`، غيّر `val` لـ `var`. رسائل الـ compiler نفسها بتعلّم.
- **ماتحفظش، افهم السبب**. كل ملف شرح بيبدأ بمشكلة، لو فهمت المشكلة هتفتكر الحل لوحدك.
- **اربط بشغلك**. بعد كل module افتح مشروع Compose بتاعك ودوّر على المفهوم ده فيه.
- **اشرح لحد**. لو قدرت تشرح المفهوم لصاحبك في دقيقتين من غير ما تبص في الملف، يبقى فهمته.
- **ارجع للـ cheatsheet** كل أسبوع، المراجعة المتباعدة بتثبّت المعلومة.

## أسئلة متكررة

**هل لازم أعرف Compose قبل ما أبدأ؟**
لأ. جزء "في Compose" في كل ملف مفهوم لوحده، ولو مش بتشتغل Compose لسه تقدر تعدّيه وترجع له بعدين.

**ليه أمثلة Compose مش بتتشغّل؟**
عشان المشروع يفضل خفيف ويفتح في ثانية من غير Android. الأمثلة كاملة ومكتوبة بـ APIs حقيقية، تقدر تنسخها في أي مشروع Android وتجربها.

**حليت التمرين بطريقة غير `Solutions.kt`، كده غلط؟**
لأ. لو `Exercises.kt` بيطبع ALL PASS يبقى حلك صح. قارن بس عشان تشوف لو فيه طريقة أبسط.

**الـ output عندي مختلف عن ملف الشرح؟**
قارن بالملف اللي في فولدر `expected-output` بنفس اسم الدرس. لو انت غيّرت في كود الدرس، ده طبيعي.

**عدّلت حاجة وعايز أرجّع الدرس زي ما كان؟**
لو بتستخدم git، اكتب الأمر ده وحط مسار الملف:

```
git checkout -- src/main/kotlin/m01_classes/L01_Constructors.kt
```

## للتأكد إن المشروع كله سليم (اختياري)

الأوامر دي للي عايز يتأكد إن كل الدروس والتمارين والشرح شغالين:

```
./gradlew verifyAll
python tools/check_bidi.py docs README.md
python tools/check_compose_refs.py
```

الأمر الأول بيشغّل كل الدروس ويقارنها بالـ output المتوقع، وبيتأكد إن كل الحلول النموذجية بتعدّي. وخلي بالك إنه بيتأكد من الحلول النموذجية مش من حلّك، فحلّك انت بتشغّله من `Exercises.kt` نفسه.

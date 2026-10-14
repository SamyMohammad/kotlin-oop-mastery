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

## أوامر التحقق

الأوامر دي بتتأكد إن كل الدروس والتمارين والشرح سليمين:

```
./gradlew verifyAll
python tools/check_bidi.py docs README.md
python tools/check_compose_refs.py
```

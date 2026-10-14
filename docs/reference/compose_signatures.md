# Compose Signature Reference

Verified on 2026-10-14 against androidx source on GitHub (branch androidx-main,
https://raw.githubusercontent.com/androidx/androidx/androidx-main/) and
kotlinx.coroutines source (master). Context7 (/websites/developer_android_develop_ui_compose)
was used for usage guidance. Signatures are abbreviated with `// ...`; only the parts that
matter for the OOP concept are kept. Module docs may only cite symbols that have a heading here.

## m00

### @Composable
Artifact: `androidx.compose.runtime:runtime`
Concept: an annotation, not a class. The compiler plugin rewrites annotated functions (m00).
```kotlin
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.PROPERTY_GETTER)
public annotation class Composable
```

### Composer
Artifact: `androidx.compose.runtime:runtime`
Concept: sealed interface holding composition state; compiler API, never called directly (m00).
```kotlin
public sealed interface Composer {
    @ComposeCompilerApi public val applier: Applier<*>
    // ...
}
```

## m01

### Text
Artifact: `androidx.compose.material3:material3`
Concept: composable with many default parameters, `modifier` first optional (m01).
```kotlin
@Composable
public fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    // ...
)
```

### Card
Artifact: `androidx.compose.material3:material3`
Concept: composable with a content slot that has a receiver scope (m01, m14).
```kotlin
@Composable
public fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
)
```

### Column
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: layout composable with receiver-scoped content, same pattern as Row (m01, m14).
```kotlin
@Composable
public inline fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
)
```
Note: Column was not in the research list; its shape mirrors the verified Row signature.

## m02

### ViewModel
Artifact: `androidx.lifecycle:lifecycle-viewmodel`
Concept: abstract base class you extend (m02, m03).
```kotlin
public expect abstract class ViewModel {
    @EmptySuper protected open fun onCleared()
    public open fun addCloseable(closeable: AutoCloseable)
    // ...
}
```

### MutableStateFlow
Artifact: `org.jetbrains.kotlinx:kotlinx-coroutines-core`
Concept: sub-interface that adds writing to a read-only interface (m02).
```kotlin
public interface MutableStateFlow<T> : StateFlow<T>, MutableSharedFlow<T> {
    public override var value: T
}
public fun <T> MutableStateFlow(value: T): MutableStateFlow<T>
```

### StateFlow
Artifact: `org.jetbrains.kotlinx:kotlinx-coroutines-core`
Concept: read-only, covariant (`out T`) interface (m02, m13).
```kotlin
public interface StateFlow<out T> : SharedFlow<T> {
    public val value: T
}
```

### asStateFlow
Artifact: `org.jetbrains.kotlinx:kotlinx-coroutines-core`
Concept: extension that hands out a read-only view of a mutable object (m02).
```kotlin
public fun <T> MutableStateFlow<T>.asStateFlow(): StateFlow<T>
```

## m03

### ComponentActivity
Artifact: `androidx.activity:activity`
Concept: open class with a superclass chain plus many interfaces (m03).
```kotlin
public open class ComponentActivity() :
    androidx.core.app.ComponentActivity(),
    ContextAware,
    LifecycleOwner,
    ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory,
    SavedStateRegistryOwner,
    // ...
```

### ViewModel.onCleared
Artifact: `androidx.lifecycle:lifecycle-viewmodel`
Concept: protected open hook a subclass overrides (m03, m04 template method).
```kotlin
@EmptySuper
protected open fun onCleared()
```

## m04

### Painter
Artifact: `androidx.compose.ui:ui-graphics`
Concept: abstract class with abstract members and protected hooks (m04, m06).
```kotlin
public abstract class Painter {
    public abstract val intrinsicSize: Size
    protected abstract fun DrawScope.onDraw()
    protected open fun applyAlpha(alpha: Float): Boolean = false
    protected open fun applyColorFilter(colorFilter: ColorFilter?): Boolean = false
    protected open fun applyLayoutDirection(layoutDirection: LayoutDirection): Boolean = false
    // ...
}
```

### Modifier.Node
Artifact: `androidx.compose.ui:ui`
Concept: abstract class nested in the Modifier interface, with open lifecycle hooks (m04, m11).
```kotlin
public interface Modifier {
    public abstract class Node : DelegatableNode {
        @EmptySuper public open fun onAttach() {}
        @EmptySuper public open fun onDetach() {}
        // ...
    }
}
```

## m05

### Modifier
Artifact: `androidx.compose.ui:ui`
Concept: interface with a nested Element interface and a companion object that is itself a Modifier (m05, m09).
```kotlin
@Stable
public interface Modifier {
    public fun <R> foldIn(initial: R, operation: (R, Element) -> R): R
    public fun <R> foldOut(initial: R, operation: (Element, R) -> R): R
    public fun any(predicate: (Element) -> Boolean): Boolean
    public fun all(predicate: (Element) -> Boolean): Boolean
    public infix fun then(other: Modifier): Modifier = // ...

    public interface Element : Modifier { /* default bodies for foldIn/foldOut/any/all */ }

    public companion object : Modifier { /* the empty modifier */ }
}
```

### Shape
Artifact: `androidx.compose.ui:ui-graphics`
Concept: single-method interface implemented by many shapes (m05, m06, m17 LSP).
```kotlin
public interface Shape {
    public fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline
}
```

### Arrangement.Horizontal
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: interface nested in the Arrangement object, with a default property (m05).
```kotlin
public object Arrangement {
    public interface Horizontal {
        public val spacing: Dp
            get() = 0.dp
        public fun Density.arrange(totalSize: Int, sizes: IntArray, layoutDirection: LayoutDirection, outPositions: IntArray)
    }
    // ...
}
```

### Indication
Artifact: `androidx.compose.foundation:foundation`
Concept: interface extended by a sub-interface; the old member is deprecated at ERROR level (m05).
```kotlin
@Stable
public interface Indication {
    @Deprecated(RememberUpdatedInstanceDeprecationMessage, level = DeprecationLevel.ERROR)
    @Composable
    public fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance
}
```

### IndicationNodeFactory
Artifact: `androidx.compose.foundation:foundation`
Concept: the modern sub-interface you implement for custom indications (m05).
```kotlin
@Stable
public interface IndicationNodeFactory : Indication {
    public fun create(interactionSource: InteractionSource): DelegatableNode
}
```

## m06

### BitmapPainter
Artifact: `androidx.compose.ui:ui-graphics`
Concept: concrete subclass of the abstract Painter (m06).
```kotlin
public class BitmapPainter(
    private val image: ImageBitmap,
    private val srcOffset: IntOffset = IntOffset.Zero,
    private val srcSize: IntSize = IntSize(image.width, image.height),
) : Painter()
```

### VectorPainter
Artifact: `androidx.compose.ui:ui`
Concept: Painter subclass with an internal constructor; created through a factory (m06, m09).
```kotlin
public class VectorPainter internal constructor(/* ... */) : Painter()

public fun rememberVectorPainter(image: ImageVector): VectorPainter
```

### GenericShape
Artifact: `androidx.compose.foundation:foundation`
Concept: Shape implementation whose behaviour is a lambda with receiver (m06, m14).
```kotlin
public class GenericShape(
    private val builder: Path.(size: Size, layoutDirection: LayoutDirection) -> Unit
) : Shape
```

### RoundedCornerShape
Artifact: `androidx.compose.foundation:foundation`
Concept: concrete class on the abstract base CornerBasedShape, plus factory-function overloads (m06).
```kotlin
public class RoundedCornerShape(
    topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize,
) : CornerBasedShape(/* ... */)

public fun RoundedCornerShape(size: Dp): RoundedCornerShape
public fun RoundedCornerShape(corner: CornerSize): RoundedCornerShape

public abstract class CornerBasedShape(/* ... */) : Shape, Interpolatable
```

### Image
Artifact: `androidx.compose.foundation:foundation`
Concept: composable overloads; one takes any Painter (m06 polymorphism).
```kotlin
@Composable
public fun Image(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    // ...
)

@Composable
public fun Image(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    // ...
)
```
Note: the bitmap overload was verified; the painter overload is the standard sibling overload.

## m07

### @Immutable
Artifact: `androidx.compose.runtime:runtime-annotation`
Concept: marker annotation promising all public properties never change (m07).
```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class Immutable
```

### @Stable
Artifact: `androidx.compose.runtime:runtime-annotation`
Concept: marker annotation promising changes are observable by Compose (m07).
```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@StableMarker
public annotation class Stable
```

## m08

### NavHost
Artifact: `androidx.navigation:navigation-compose`
Concept: composable configured by a DSL lambda with receiver (m08, m14).
```kotlin
@Composable
public fun NavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit,
)
```

### CircularProgressIndicator
Artifact: `androidx.compose.material3:material3`
Concept: overloads distinguished by parameters (determinate vs indeterminate) (m06, m08).
```kotlin
@Composable
public fun CircularProgressIndicator(progress: () -> Float, modifier: Modifier = Modifier /* ... */)

@Composable
public fun CircularProgressIndicator(modifier: Modifier = Modifier /* ... */)
```

### TextFieldValue
Artifact: `androidx.compose.ui:ui-text`
Concept: a regular (not data) class marked @Immutable (m07, m08).
```kotlin
@Immutable
public class TextFieldValue public constructor(
    public val annotatedString: AnnotatedString,
    selection: TextRange = TextRange.Zero,
    composition: TextRange? = null,
)
```

## m09

### Modifier.Companion
Artifact: `androidx.compose.ui:ui`
Concept: companion object that implements its own interface: the empty Modifier (m09).
```kotlin
public interface Modifier {
    public companion object : Modifier {
        public override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial
        public override fun <R> foldOut(initial: R, operation: (Element, R) -> R): R = initial
        public override fun any(predicate: (Element) -> Boolean): Boolean = false
        public override fun all(predicate: (Element) -> Boolean): Boolean = true
    }
}
```

### MaterialTheme
Artifact: `androidx.compose.material3:material3`
Concept: a composable function AND an object with the same name; the object exposes the current theme (m09).
```kotlin
@Composable
public fun MaterialTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit,
)

public object MaterialTheme {
    public val colorScheme: ColorScheme @Composable get() = // ...
    public val typography: Typography @Composable get() = // ...
    public val shapes: Shapes @Composable get() = // ...
}
```
Note: the object form is inferred from the default arguments `MaterialTheme.colorScheme` in the verified function signature.

### Dp.Companion.Unspecified
Artifact: `androidx.compose.ui:ui-unit`
Concept: sentinel constant in a value class's companion (m09, m10).
```kotlin
@JvmInline
public value class Dp(public val value: Float) : Comparable<Dp> {
    public companion object {
        public val Unspecified: Dp
        // ...
    }
}
```

## m10

### Dp
Artifact: `androidx.compose.ui:ui-unit`
Concept: value class wrapping a Float, with member operators (m10, m15).
```kotlin
@Immutable
@JvmInline
public value class Dp public constructor(public val value: Float) : Comparable<Dp> {
    @Stable public inline operator fun plus(other: Dp): Dp
    @Stable public inline operator fun minus(other: Dp): Dp
    @Stable public inline operator fun times(other: Float): Dp
    @Stable public inline operator fun times(other: Int): Dp
    @Stable public inline operator fun div(other: Float): Dp
    @Stable public inline operator fun div(other: Dp): Float
    // ...
}
```

### Color
Artifact: `androidx.compose.ui:ui-graphics`
Concept: value class wrapping a ULong (packed color bits) (m10).
```kotlin
@Immutable
@JvmInline
public value class Color(public val value: ULong) {
    // red, green, blue, alpha, component1..4, copy(...)
}
```

### TextUnit
Artifact: `androidx.compose.ui:ui-unit`
Concept: value class packing a number and its unit (sp/em) into one Long (m10).
```kotlin
@Immutable
@JvmInline
public value class TextUnit internal constructor(internal val packedValue: Long) {
    public inline operator fun times(other: Float): TextUnit
    // ...
}
```

### Int.dp
Artifact: `androidx.compose.ui:ui-unit`
Concept: extension property on a built-in type (m10, m14).
```kotlin
public inline val Int.dp: Dp
```

## m11

### PreviewParameterProvider
Artifact: `androidx.compose.ui:ui-tooling-preview`
Concept: generic interface with one abstract and one default property (m11).
```kotlin
public interface PreviewParameterProvider<T> {
    public val values: Sequence<T>
    public val count: Int
        get() = values.count()
}
```

### @PreviewParameter
Artifact: `androidx.compose.ui:ui-tooling-preview`
Concept: annotation taking a class reference to a provider implementation (m11).
```kotlin
public annotation class PreviewParameter(
    public val provider: KClass<out PreviewParameterProvider<*>>,
    // ...
)
```

## m12

### remember
Artifact: `androidx.compose.runtime:runtime`
Concept: inline generic function caching a value in the composition (m12).
```kotlin
@Composable
public inline fun <T> remember(crossinline calculation: @DisallowComposableCalls () -> T): T

@Composable
public inline fun <T> remember(key1: Any?, crossinline calculation: @DisallowComposableCalls () -> T): T
```

### mutableStateOf
Artifact: `androidx.compose.runtime:runtime`
Concept: factory function returning an interface type (m12, m18).
```kotlin
public fun <T> mutableStateOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
): MutableState<T>
```

### MutableState
Artifact: `androidx.compose.runtime:runtime`
Concept: invariant sub-interface adding a writable value and destructuring (m12, m13, m15).
```kotlin
public interface MutableState<T> : State<T> {
    override var value: T
    public operator fun component1(): T
    public operator fun component2(): (T) -> Unit
}
```

### State.getValue
Artifact: `androidx.compose.runtime:runtime`
Concept: extension operator enabling `val x by state` (m12).
```kotlin
public inline operator fun <T> State<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value
```

### MutableState.setValue
Artifact: `androidx.compose.runtime:runtime`
Concept: extension operator enabling `var x by mutableState` (m12).
```kotlin
public inline operator fun <T> MutableState<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T)
```

### viewModels
Artifact: `androidx.activity:activity`
Concept: reified extension returning a Lazy delegate (m12, m13).
```kotlin
public inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
    noinline factoryProducer: (() -> Factory)? = null,
): Lazy<VM>
```

### collectAsStateWithLifecycle
Artifact: `androidx.lifecycle:lifecycle-runtime-compose`
Concept: extension adapting a StateFlow into a Compose State (m12, m18 adapter).
```kotlin
@Composable
fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T>
```

## m13

### State
Artifact: `androidx.compose.runtime:runtime`
Concept: read-only covariant interface: `State<Book>` is a `State<Item>` (m13).
```kotlin
public interface State<out T> {
    public val value: T
}
```

### LazyListScope.items
Artifact: `androidx.compose.foundation:foundation`
Concept: generic inline extension on a DSL scope interface (m13, m14).
```kotlin
public inline fun <T> LazyListScope.items(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
)
```

### LazyColumn
Artifact: `androidx.compose.foundation:foundation`
Concept: composable whose content is a LazyListScope DSL (m13).
```kotlin
@Composable
public fun LazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    // ...
    content: LazyListScope.() -> Unit,
)
```

## m14

### RowScope
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: scope interface used as the receiver of Row content; holds member extensions like weight (m14).
```kotlin
public interface RowScope {
    public fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier
    // ...
}
```

### ColumnScope
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: scope interface used as the receiver of Column and Card content (m14).
```kotlin
public interface ColumnScope {
    public fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier
    // ...
}
```
Note: mirrors the verified RowScope declaration.

### BoxScope
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: scope interface used as the receiver of Box content (m14).
```kotlin
public interface BoxScope {
    public fun Modifier.align(alignment: Alignment): Modifier
    // ...
}
```

### RowScope.weight
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: member extension declared inside a scope interface: only callable inside Row (m14).
```kotlin
public interface RowScope {
    @Stable
    public fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier
}
```

### BoxScope.align
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: member extension in the BoxScope interface (m14).
```kotlin
public interface BoxScope {
    @Stable
    public fun Modifier.align(alignment: Alignment): Modifier
}
```

### Modifier.padding
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: top-level extension function on the Modifier interface, with overloads (m14).
```kotlin
public fun Modifier.padding(start: Dp = 0.dp, top: Dp = 0.dp, end: Dp = 0.dp, bottom: Dp = 0.dp): Modifier
public fun Modifier.padding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): Modifier
public fun Modifier.padding(all: Dp): Modifier
public fun Modifier.padding(paddingValues: PaddingValues): Modifier
```

### Row
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: content lambda with RowScope receiver (m14).
```kotlin
@Composable
public inline fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit,
)
```

### Box
Artifact: `androidx.compose.foundation:foundation-layout`
Concept: content lambda with BoxScope receiver (m14).
```kotlin
@Composable
public inline fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
)
```

## m15

### Dp.plus
Artifact: `androidx.compose.ui:ui-unit`
Concept: member operator on a value class (m15).
```kotlin
@Stable public inline operator fun plus(other: Dp): Dp = Dp((this.value + other.value) + 0f)
```

### Offset
Artifact: `androidx.compose.ui:ui-geometry`
Concept: value class packing x and y into a Long, with member operators (m10, m15).
```kotlin
@JvmInline
public value class Offset(public val packedValue: Long) {
    public operator fun minus(other: Offset): Offset
    public operator fun plus(other: Offset): Offset
    public operator fun times(operand: Float): Offset
    public operator fun div(operand: Float): Offset
    public companion object {
        public val Zero: Offset
        public val Unspecified: Offset
    }
}
```

### Modifier.then
Artifact: `androidx.compose.ui:ui`
Concept: infix member that chains two modifiers (m15, m18 decorator).
```kotlin
public interface Modifier {
    public infix fun then(other: Modifier): Modifier
}
```

## m16

### Scaffold
Artifact: `androidx.compose.material3:material3`
Concept: slot API: several named composable lambdas with defaults (m16).
```kotlin
@Composable
public fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    // ...
    content: @Composable (PaddingValues) -> Unit,
)
```

### Button
Artifact: `androidx.compose.material3:material3`
Concept: slot with a receiver type `RowScope.() -> Unit` (m14, m16).
```kotlin
@Composable
public fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // ...
    content: @Composable RowScope.() -> Unit,
)
```

### Icon
Artifact: `androidx.compose.material3:material3`
Concept: overloads by parameter type (ImageVector / Painter / ImageBitmap) (m06, m16).
```kotlin
@Composable
public fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
)
```

## m17

### CompositionLocal
Artifact: `androidx.compose.runtime:runtime`
Concept: sealed generic class; ProvidableCompositionLocal is the providable form (m17).
```kotlin
public sealed class CompositionLocal<T>(defaultFactory: () -> T) {
    public inline val current: T
}

public abstract class ProvidableCompositionLocal<T> internal constructor(
    defaultFactory: () -> T,
) : CompositionLocal<T>(defaultFactory)
```

### staticCompositionLocalOf
Artifact: `androidx.compose.runtime:runtime`
Concept: factory returning an abstract type (m17).
```kotlin
public fun <T> staticCompositionLocalOf(defaultFactory: () -> T): ProvidableCompositionLocal<T>
```

### CompositionLocalProvider
Artifact: `androidx.compose.runtime:runtime`
Concept: provides a value to everything inside its content (m17 dependency inversion).
```kotlin
@Composable
public fun CompositionLocalProvider(value: ProvidedValue<*>, content: @Composable () -> Unit)
```
Usage: `CompositionLocalProvider(LocalX provides value) { ... }`.

### Modifier.clip
Artifact: `androidx.compose.ui:ui`
Concept: accepts ANY Shape implementation (m17 LSP).
```kotlin
@Stable
public fun Modifier.clip(shape: Shape): Modifier = graphicsLayer(shape = shape, clip = true)
```

## m18

### snapshotFlow
Artifact: `androidx.compose.runtime:runtime`
Concept: adapter from Compose state reads to a Flow (m18).
```kotlin
public fun <T> snapshotFlow(block: () -> T): Flow<T>
```

### derivedStateOf
Artifact: `androidx.compose.runtime:runtime`
Concept: factory returning read-only State computed from other states (m18 observer).
```kotlin
public fun <T> derivedStateOf(calculation: () -> T): State<T>
```

### Modifier.composed
Artifact: `androidx.compose.ui:ui`
Concept: older stateful-modifier factory; not @Deprecated in source but no longer recommended, use Modifier.Node (m18).
```kotlin
public fun Modifier.composed(
    inspectorInfo: InspectorInfo.() -> Unit = NoInspectorInfo,
    factory: @Composable Modifier.() -> Modifier,
): Modifier
```

### LazyListState
Artifact: `androidx.compose.foundation:foundation`
Concept: public class with an internal constructor: instances come from a factory (m09, m18).
```kotlin
public class LazyListState internal constructor(/* ... */)
```

### rememberLazyListState
Artifact: `androidx.compose.foundation:foundation`
Concept: remembered factory function returning the state object (m18 factory).
```kotlin
@Composable
public fun rememberLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
): LazyListState
```

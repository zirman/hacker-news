package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.unit.Dp

/**
 * Creates a Ripple using the provided values and values inferred from the theme.
 *
 * A Ripple is a Material implementation of [Indication] that expresses different [Interaction]s by
 * drawing ripple animations and state layers.
 *
 * A Ripple responds to [PressInteraction.Press] by starting a new ripple animation, and responds to
 * other [Interaction]s by showing a fixed state layer with varying alpha values depending on the
 * [Interaction].
 *
 * [MaterialTheme] provides Ripples using [androidx.compose.foundation.LocalIndication], so a Ripple
 * will be used as the default [Indication] inside components such as
 * [androidx.compose.foundation.clickable] and [androidx.compose.foundation.indication], in addition
 * to Material provided components that use a Ripple as well.
 *
 * You can also explicitly create a Ripple and provide it to custom components in order to change
 * the parameters from the default, such as to create an unbounded ripple with a fixed size.
 *
 * To create a Ripple with a manually defined color that can change over time, see the other
 * [ripple] overload with a [ColorProducer] parameter. This will avoid unnecessary recompositions
 * when changing the color, and preserve existing ripple state when the color changes.
 *
 * @param bounded If true, ripples are clipped by the bounds of the target layout. Unbounded ripples
 *   always animate from the target layout center, bounded ripples animate from the touch position.
 * @param radius the radius for the ripple. If [Dp.Unspecified] is provided then the size will be
 *   calculated based on the target layout size.
 * @param color the color of the ripple. This color is usually the same color used by the text or
 *   iconography in the component. This color will then have [RippleDefaults.RippleAlpha] applied to
 *   calculate the final color used to draw the ripple. If [Color.Unspecified] is provided the color
 *   used will be [LocalContentColor] instead.
 */
@Stable
fun ripple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified
): IndicationNodeFactory {
    return if (radius == Dp.Unspecified && color == Color.Unspecified) {
        if (bounded) return DefaultBoundedRipple else DefaultUnboundedRipple
    } else {
        RippleNodeFactory(bounded, radius, color)
    }
}

/**
 * Creates a Ripple using the provided values and values inferred from the theme.
 *
 * A Ripple is a Material implementation of [Indication] that expresses different [Interaction]s by
 * drawing ripple animations and state layers.
 *
 * A Ripple responds to [PressInteraction.Press] by starting a new ripple animation, and responds to
 * other [Interaction]s by showing a fixed state layer with varying alpha values depending on the
 * [Interaction].
 *
 * [MaterialTheme] provides Ripples using [androidx.compose.foundation.LocalIndication], so a Ripple
 * will be used as the default [Indication] inside components such as
 * [androidx.compose.foundation.clickable] and [androidx.compose.foundation.indication], in addition
 * to Material provided components that use a Ripple as well.
 *
 * You can also explicitly create a Ripple and provide it to custom components in order to change
 * the parameters from the default, such as to create an unbounded ripple with a fixed size.
 *
 * To create a Ripple with a static color, see the [ripple] overload with a [Color] parameter. This
 * overload is optimized for Ripples that have dynamic colors that change over time, to reduce
 * unnecessary recompositions.
 *
 * @param color the color of the ripple. This color is usually the same color used by the text or
 *   iconography in the component. This color will then have [RippleDefaults.RippleAlpha] applied to
 *   calculate the final color used to draw the ripple. If you are creating this [ColorProducer]
 *   outside of composition (where it will be automatically remembered), make sure that its instance
 *   is stable (such as by remembering the object that holds it), or remember the returned [ripple]
 *   object to make sure that ripple nodes are not being created each recomposition.
 * @param bounded If true, ripples are clipped by the bounds of the target layout. Unbounded ripples
 *   always animate from the target layout center, bounded ripples animate from the touch position.
 * @param radius the radius for the ripple. If [Dp.Unspecified] is provided then the size will be
 *   calculated based on the target layout size.
 */
@Stable
fun ripple(
    color: ColorProducer,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified
): IndicationNodeFactory {
    return RippleNodeFactory(bounded, radius, color)
}

/** Default values used by [ripple]. */
object RippleDefaults {
    /**
     * Represents the default [RippleAlpha] that will be used for a ripple to indicate different
     * states.
     */
    val RippleAlpha: RippleAlpha =
        RippleAlpha(
            pressedAlpha = StateTokens.PressedStateLayerOpacity,
            focusedAlpha = StateTokens.FocusStateLayerOpacity,
            draggedAlpha = StateTokens.DraggedStateLayerOpacity,
            hoveredAlpha = StateTokens.HoverStateLayerOpacity
        )
}

/**
 * CompositionLocal used for providing [RippleConfiguration] down the tree. This acts as a
 * tree-local 'override' for ripples used inside components that you cannot directly control, such
 * as to change the color of a specific component's ripple, or disable it entirely by providing
 * `null`.
 *
 * In most cases you should rely on the default theme behavior for consistency with other components
 * - this exists as an escape hatch for individual components and is not intended to be used for
 *   full theme customization across an application. For this use case you should instead build your
 *   own custom ripple that queries your design system theme values directly using
 *   [createRippleModifierNode].
 */
val LocalRippleConfiguration: ProvidableCompositionLocal<RippleConfiguration?> =
    compositionLocalOf {
        RippleConfiguration()
    }

/**
 * Configuration for [ripple] appearance, provided using [LocalRippleConfiguration]. In most cases
 * the default values should be used, for custom design system use cases you should instead build
 * your own custom ripple using [createRippleModifierNode]. To disable the ripple, provide `null`
 * using [LocalRippleConfiguration].
 *
 * @param color the color override for the ripple. If [Color.Unspecified], then the default color
 *   from the theme will be used instead. Note that if the ripple has a color explicitly set with
 *   the parameter on [ripple], that will always be used instead of this value.
 * @param rippleAlpha the [RippleAlpha] override for this ripple. If null, then the default alpha
 *   will be used instead.
 */
@Immutable
class RippleConfiguration(
    val color: Color = Color.Unspecified,
    val rippleAlpha: RippleAlpha? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RippleConfiguration) return false

        if (color != other.color) return false
        if (rippleAlpha != other.rippleAlpha) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + (rippleAlpha?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "RippleConfiguration(color=$color, rippleAlpha=$rippleAlpha)"
    }
}

@Stable
private class RippleNodeFactory
private constructor(
    private val bounded: Boolean,
    private val radius: Dp,
    private val colorProducer: ColorProducer?,
    private val color: Color
) : IndicationNodeFactory {
    constructor(
        bounded: Boolean,
        radius: Dp,
        colorProducer: ColorProducer
    ) : this(bounded, radius, colorProducer, Color.Unspecified)

    constructor(bounded: Boolean, radius: Dp, color: Color) : this(bounded, radius, null, color)

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        val colorProducer = colorProducer ?: ColorProducer { color }
        return DelegatingThemeAwareRippleNode(interactionSource, bounded, radius, colorProducer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RippleNodeFactory) return false

        if (bounded != other.bounded) return false
        if (radius != other.radius) return false
        if (colorProducer != other.colorProducer) return false
        return color == other.color
    }

    override fun hashCode(): Int {
        var result = bounded.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + colorProducer.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}

private class DelegatingThemeAwareRippleNode(
    private val interactionSource: InteractionSource,
    private val bounded: Boolean,
    private val radius: Dp,
    private val color: ColorProducer,
) : DelegatingNode(), CompositionLocalConsumerModifierNode, ObserverModifierNode {
    private var rippleNode: DelegatableNode? = null

    override fun onAttach() {
        updateConfiguration()
    }

    override fun onObservedReadsChanged() {
        updateConfiguration()
    }

    /**
     * Handles [LocalRippleConfiguration] changing between null / non-null. Changes to
     * [RippleConfiguration.color] and [RippleConfiguration.rippleAlpha] are handled as part of the
     * ripple definition.
     */
    private fun updateConfiguration() {
        observeReads {
            val configuration = currentValueOf(LocalRippleConfiguration)
            if (configuration == null) {
                removeRipple()
            } else {
                if (rippleNode == null) attachNewRipple()
            }
        }
    }

    private fun attachNewRipple() {
        val calculateColor = ColorProducer {
            val userDefinedColor = color()
            if (userDefinedColor.isSpecified) {
                userDefinedColor
            } else {
                // If this is null, the ripple will be removed, so this should always be non-null in
                // normal use
                val rippleConfiguration = currentValueOf(LocalRippleConfiguration)
                if (rippleConfiguration?.color?.isSpecified == true) {
                    rippleConfiguration.color
                } else {
                    currentValueOf(LocalContentColor)
                }
            }
        }

        val calculateRippleAlpha = {
            // If this is null, the ripple will be removed, so this should always be non-null in
            // normal use
            val rippleConfiguration = currentValueOf(LocalRippleConfiguration)
            rippleConfiguration?.rippleAlpha ?: RippleDefaults.RippleAlpha
        }

        rippleNode =
            delegate(
                createRippleModifierNode(
                    interactionSource,
                    bounded,
                    radius,
                    calculateColor,
                    calculateRippleAlpha
                )
            )
    }

    private fun removeRipple() {
        rippleNode?.let { undelegate(it) }
        rippleNode = null
    }
}

private val DefaultBoundedRipple =
    RippleNodeFactory(bounded = true, radius = Dp.Unspecified, color = Color.Unspecified)
private val DefaultUnboundedRipple =
    RippleNodeFactory(bounded = false, radius = Dp.Unspecified, color = Color.Unspecified)

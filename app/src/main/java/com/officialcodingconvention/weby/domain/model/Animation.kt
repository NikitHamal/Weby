package com.officialcodingconvention.weby.domain.model

import java.util.UUID

data class ElementAnimation(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val type: AnimationType,
    val trigger: AnimationTrigger,
    val duration: Int = 300,
    val delay: Int = 0,
    val easing: EasingFunction = EasingFunction.EASE,
    val iterations: Int = 1,
    val direction: AnimationDirection = AnimationDirection.NORMAL,
    val fillMode: AnimationFillMode = AnimationFillMode.FORWARDS,
    val keyframes: List<AnimationKeyframe> = emptyList(),
    val scrollConfig: ScrollAnimationConfig? = null
)

enum class AnimationType {
    FADE_IN, FADE_OUT,
    SLIDE_UP, SLIDE_DOWN, SLIDE_LEFT, SLIDE_RIGHT,
    SCALE_IN, SCALE_OUT,
    ROTATE,
    BOUNCE,
    PULSE,
    SHAKE,
    FLIP,
    CUSTOM
}

enum class AnimationTrigger {
    ON_LOAD,
    ON_CLICK,
    ON_HOVER,
    ON_SCROLL_INTO_VIEW,
    ON_SCROLL,
    ON_FORM_SUBMIT,
    MANUAL
}

enum class EasingFunction(val cssValue: String) {
    LINEAR("linear"),
    EASE("ease"),
    EASE_IN("ease-in"),
    EASE_OUT("ease-out"),
    EASE_IN_OUT("ease-in-out"),
    EASE_IN_BACK("cubic-bezier(0.6, -0.28, 0.735, 0.045)"),
    EASE_OUT_BACK("cubic-bezier(0.175, 0.885, 0.32, 1.275)"),
    EASE_IN_OUT_BACK("cubic-bezier(0.68, -0.55, 0.265, 1.55)"),
    EASE_IN_CIRC("cubic-bezier(0.6, 0.04, 0.98, 0.335)"),
    EASE_OUT_CIRC("cubic-bezier(0.075, 0.82, 0.165, 1)"),
    EASE_IN_OUT_CIRC("cubic-bezier(0.785, 0.135, 0.15, 0.86)"),
    EASE_IN_EXPO("cubic-bezier(0.95, 0.05, 0.795, 0.035)"),
    EASE_OUT_EXPO("cubic-bezier(0.19, 1, 0.22, 1)"),
    EASE_IN_OUT_EXPO("cubic-bezier(1, 0, 0, 1)"),
    EASE_IN_QUAD("cubic-bezier(0.55, 0.085, 0.68, 0.53)"),
    EASE_OUT_QUAD("cubic-bezier(0.25, 0.46, 0.45, 0.94)"),
    EASE_IN_OUT_QUAD("cubic-bezier(0.455, 0.03, 0.515, 0.955)"),
    EASE_IN_CUBIC("cubic-bezier(0.55, 0.055, 0.675, 0.19)"),
    EASE_OUT_CUBIC("cubic-bezier(0.215, 0.61, 0.355, 1)"),
    EASE_IN_OUT_CUBIC("cubic-bezier(0.645, 0.045, 0.355, 1)"),
    EASE_IN_QUART("cubic-bezier(0.895, 0.03, 0.685, 0.22)"),
    EASE_OUT_QUART("cubic-bezier(0.165, 0.84, 0.44, 1)"),
    EASE_IN_OUT_QUART("cubic-bezier(0.77, 0, 0.175, 1)"),
    EASE_IN_QUINT("cubic-bezier(0.755, 0.05, 0.855, 0.06)"),
    EASE_OUT_QUINT("cubic-bezier(0.23, 1, 0.32, 1)"),
    EASE_IN_OUT_QUINT("cubic-bezier(0.86, 0, 0.07, 1)"),
    EASE_IN_SINE("cubic-bezier(0.47, 0, 0.745, 0.715)"),
    EASE_OUT_SINE("cubic-bezier(0.39, 0.575, 0.565, 1)"),
    EASE_IN_OUT_SINE("cubic-bezier(0.445, 0.05, 0.55, 0.95)"),
    SPRING("cubic-bezier(0.175, 0.885, 0.32, 1.5)")
}

enum class AnimationDirection(val cssValue: String) {
    NORMAL("normal"),
    REVERSE("reverse"),
    ALTERNATE("alternate"),
    ALTERNATE_REVERSE("alternate-reverse")
}

enum class AnimationFillMode(val cssValue: String) {
    NONE("none"),
    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    BOTH("both")
}

data class AnimationKeyframe(
    val offset: Float,
    val properties: Map<String, String>
)

data class ScrollAnimationConfig(
    val startOffset: Float = 0f,
    val endOffset: Float = 1f,
    val scrub: Boolean = false,
    val pin: Boolean = false,
    val markers: Boolean = false
)

data class Interaction(
    val id: String = UUID.randomUUID().toString(),
    val trigger: InteractionTrigger,
    val action: InteractionAction,
    val targetElementId: String? = null,
    val value: String? = null,
    val delay: Int = 0
)

enum class InteractionTrigger {
    CLICK, DOUBLE_CLICK, MOUSE_ENTER, MOUSE_LEAVE, FOCUS, BLUR, SCROLL, LOAD
}

enum class InteractionAction {
    SHOW, HIDE, TOGGLE_VISIBILITY,
    ADD_CLASS, REMOVE_CLASS, TOGGLE_CLASS,
    SET_ATTRIBUTE, REMOVE_ATTRIBUTE,
    NAVIGATE_TO, SCROLL_TO,
    PLAY_ANIMATION, PAUSE_ANIMATION,
    SUBMIT_FORM, RESET_FORM,
    OPEN_MODAL, CLOSE_MODAL,
    CUSTOM_SCRIPT
}

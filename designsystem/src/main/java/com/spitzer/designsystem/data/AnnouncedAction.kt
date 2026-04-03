package com.spitzer.designsystem.data

/**
 * Represents an executable action paired with a descriptive label.
 *
 * This data class is primarily used to provide semantic information to accessibility services,
 * ensuring that functional callbacks have associated text for screen reader announcements.
 *
 * @property action The callback to be executed when the action is triggered.
 * @property description A localized, human-readable string describing the action for accessibility purposes.
 */
data class AnnouncedAction(
    val action: () -> Unit,
    val description: String
)

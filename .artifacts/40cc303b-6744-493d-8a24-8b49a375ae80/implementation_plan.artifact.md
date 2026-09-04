# Implementation Plan - Fix Blurry UI and Improve Logo Visibility

The user's screenshot shows a heavily blurred main content area, making the text unreadable. This is caused by an incorrect application of `Modifier.blur` in the `GlassCard` component, which blurs both its background and its children. Additionally, the app logo in the top bar is quite small and could be improved.

## User Review Required

> [!IMPORTANT]
> The "Glassmorphism" effect was incorrectly implemented by applying a blur to the entire card container. In Jetpack Compose, this blurs all child components (like text and buttons), rendering them unreadable. I will remove the blur from the card itself to restore sharpness while keeping the translucent background for the "Glass" look.

## Proposed Changes

### UI Components

#### [MODIFY] [GlassComponents.kt](file:///D:/collagedeckk/app/src/main/java/com/example/ui/components/GlassComponents.kt)
- Remove `Modifier.blur(blurRadius)` from `GlassCard` and `Modifier.glassEffect`.
- This ensures that content placed inside `GlassCard` (like assignment details) remains perfectly sharp.

### UI Screens

#### [MODIFY] [AssignmentScreen.kt](file:///D:/collagedeckk/app/src/main/java/com/example/ui/screens/AssignmentScreen.kt)
- Increase the logo size in the `TopAppBar` from `20.dp` to `24.dp` for better visibility.
- Increase the containing `Box` size from `32.dp` to `36.dp`.

#### [MODIFY] [HomeScreen.kt](file:///D:/collagedeckk/app/src/main/java/com/example/ui/screens/HomeScreen.kt)
- Adjust the logo icon size in the `TopAppBar` if necessary to match the new standard.

#### [MODIFY] [MarketplaceScreen.kt](file:///D:/collagedeckk/app/src/main/java/com/example/ui/screens/MarketplaceScreen.kt)
- Adjust the logo icon size in the `TopAppBar`.

#### [MODIFY] [GigScreen.kt](file:///D:/collagedeckk/app/src/main/java/com/example/ui/screens/GigScreen.kt)
- Adjust the logo icon size in the `TopAppBar`.

## Verification Plan

### Automated Tests
- Run `gradle_build` to ensure the project compiles.

### Manual Verification
- Deploy to a device and verify that the "Assignment Vault" list items are sharp and readable.
- Verify that the logo in the top bar is clearly visible and correctly sized.

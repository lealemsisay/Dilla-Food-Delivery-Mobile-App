# Dilla Food Delivery (Android · Kotlin · Jetpack Compose)

## Run
1. Android Studio (Koala 2024.1+ recommended) → **File ▸ Open** → select this folder.
2. Let Gradle sync (needs internet the first time). JDK 17 is bundled with Android Studio.
3. Pick an emulator/device (Android 7.0+, API 24) and press **Run**.

## What works now (on-device demo, no backend)
- Sign in screen with role chooser (Customer / Restaurant / Rider / Admin) and +251 phone validation.
- Customer: restaurants → **Choose your friends → Select your food → Checkout** (same layout as the 3 reference phones),
  bill split with deterministic rounding, scheduled delivery slots, cash payment record, order tracking timeline, delivery PIN.
- Restaurant: accept/reject/prepare orders, toggle menu availability.  Rider: go online, pick up, confirm delivery with the customer PIN.
- Admin: order counts, revenue, order list.
Use one device and sign out/in as each role to see an order move through the whole flow.

## What is NOT done yet (needs your decisions/accounts)
- **Firebase**: add `google-services.json`, the Google Services Gradle plugin and Firebase BOM, then replace the in-memory state in
  `data/Models.kt` (`AppVm`, `Demo`) with repositories (Auth, Firestore, Storage, FCM). Starter rules: `firebase/firestore.rules`.
- Sample restaurants/menu/friends in `Demo` are placeholders - remove once Firestore data exists.
- Digital payments (provider not chosen), maps/live GPS, geofence zones, push notifications, Cloud Functions for server-side pricing, scheduling jobs and group finalization.
- Food photos: emoji tiles are used; load real images from Cloud Storage (e.g. Coil) later.

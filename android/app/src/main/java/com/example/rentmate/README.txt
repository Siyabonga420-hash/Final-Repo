RENTMATE PHASE 1 - TENANT HOUSEHOLD APPROVAL GATE

1. Extract these 4 .kt files to:
   C:\Users\Prince\Documents\RentMate-FullStack\RENTMATE2\app\src\main\java\com\example\rentmate
   Back up originals first.
2. Rebuild and run Android Studio.
3. Sign in as tenant: see households, request to join, pending state.
4. Manager approves in web portal, then tenant taps Refresh status.

Important:
- Existing backend supports these tenant endpoints; no backend edits needed for this first phase.
- The manager-created household ADMIN APPROVAL workflow is NOT yet implemented; do not treat this as complete.
- The dashboard UI remains unchanged; the old Create/Join dialog is removed.
- Existing app API feature routes may require separate fixes.

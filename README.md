Build for Android:
`gradle :android-app:assembleDebug`

Run on iOS
Run project `ios-app/iosApp.xcodeproj`

Run on Desktop
`gradle :desktop-app:run`

Hot run with auto refresh on Desktop
`gradle :desktop-app:hotRunDesktop --auto`

Build for Wear OS
`gradle :wear-app:assembleDebug`

Screenshots:
Android:
<img src="https://github.com/user-attachments/assets/0e37409a-65e2-44d9-8367-fb3c0e108cf3" width="320"/>
<img src="https://github.com/user-attachments/assets/6532598b-65e4-4c20-b97b-fe2876b34395" width="320"/>

Android Foldable:
<img src="https://github.com/user-attachments/assets/9aa6c92a-28ce-4775-8e39-cfd9d3fd3277" width="640"/>

iOS:
<img src="https://github.com/user-attachments/assets/8e65715d-f8e8-4d8f-811f-40f0fcb7c39f" width="320"/>
<img src="https://github.com/user-attachments/assets/a74416fc-df62-458b-b259-878056d08820" width="320"/>

TODO:
 + Move features into separate api/impl modules
 + Saved State, View Model, Lifecycle, Paging* KMP libraries
 + Modifier.onVisibilityChanged()
 + Modifier.contentType(Username)
 + Open specific user's stores
 + Transition animations
 + Add comment quote button to reply
 + Post stories
 + Offline mode
 + hide loader on error
 + Add copy text to item context menu
 + WorkManager get update follow notifications
 + Customize theme colors
 + Glance Widget
 + Follow item with local notifications on updates
 + Chat bubbles
 + Show your reply immediately
 + Highlight new items/Update items when back new or back online/Jump to unread items
 + Synchronize expanded items between devices
 + Google Play Review API
 + Register account
 + Use android.icu.text.MessageFormat to translate plurals
 + TalkBack
 + Voice Access
 + Switch Access
 + Modifier.semantics()
 + Read comments using voice synthesis
 + Customize font from google font lib
 + Search
 + Swipe Left/Right
 + Favorites paging
 + Fix arrows keys leaving text field when replying to thread
 + Replying queues when offline

# METRO ASSIST — Final Build Prompt

BUILD A COMPLETE FULL-STACK APPLICATION + ANDROID APP: METRO ASSIST

Act as a senior full-stack software developer, UI/UX designer, database architect and Android developer.

Build a fully functional, responsive application called METRO ASSIST – Delhi Metro Divyangjan Assistance & Station Coordination System, a controlled prototype for Delhi Metro Pink Line station coordination.

Do not create only a landing page or static frontend. Build the actual working application with frontend, backend, database, authentication, real-time communication, alarms, notifications and administrator panel, plus a Progressive Web App and a release-signed Android APK.

IMPORTANT: This is an independent prototype. Do not connect to DMRC internal systems, claim official DMRC approval, or use real passenger data without authorization.

## 1. TECHNOLOGY

Use:

- React / Next.js (App Router) for the frontend.
- Tailwind CSS for styling.
- Supabase for PostgreSQL database, authentication and real-time subscriptions when credentials are supplied. If Supabase or any external credential is missing, implement a fully working local mode with SQLite, server-side API routes and SSE realtime, with the same schema and behaviour. Never replace core workflows with frontend-only fake data.
- Server-side API functions for business logic.
- Socket-based, SSE or Supabase real-time notifications.
- Progressive Web App (PWA) support for mobile installation.
- An Android WebView wrapper packaged as a signed APK.

Keep the application modular, scalable and production-oriented.

## 2. APPLICATION BRANDING

Application name: METRO ASSIST.

Subtitle: Divyangjan Assistance & Station Coordination System.

Design:

- Professional Delhi Metro-inspired appearance.
- Navy blue, white and subtle yellow accents.
- Modern dashboard.
- Large icons and buttons.
- Mobile-first responsive design.
- Easy-to-read typography.
- Accessible contrast.
- Clear visual alarm notifications.

Create a professional login screen and a separate administrator portal.

## 3. STATION LOGIN

Create a station login page.

Features:

- Station dropdown listing every active station.
- Password input.
- Login button.
- Selected station displayed after login.
- Secure authentication.
- Station-specific access.

Initial prototype password requirement: `12345`.

For security, use a temporary initial password only during testing. Require administrator-controlled unique station credentials before deployment.

All stations must be connected for network-wide coordination: a request or notice created at one station is delivered to the relevant station (or all stations for broadcasts) in real time, on the same central backend.

The administrator can import the complete verified Pink Line station list. For the demonstration, seed the 38-station Pink Line list used by this project. The stored sequence is synthetic demonstration data and must be clearly labelled as such — it is not the official DMRC route order.

## 4. MAIN DASHBOARD

After login display three large cards:

A. VISUALLY IMPAIRED
B. WHEELCHAIR
C. OTHER INFORMATION

Display dashboard statistics:

- Total requests.
- Pending requests.
- Acknowledged requests.
- Trains approaching.
- Completed requests.
- Active information notices.

Display a live assistance request table.

## 5. VISUALLY IMPAIRED AND WHEELCHAIR REQUEST FORM

When staff selects either assistance type, open a form.

Fields:

1. Assistance Type.
2. Boarding Station – automatically selected from login.
3. Assistance Required At – destination station dropdown.
4. Number of Passengers.
5. Train Number.
6. Train Set Number.
7. Circular Direction: `+ Circular` or `− Circular`.
8. Create Request button.

Example:

- Boarding Station: Anand Vihar
- Destination: Lajpat Nagar
- Assistance: Visually Impaired
- Passengers: 02
- Train Number: 2890
- Train Set: 23
- Direction: + Circular

On submitting:

- Generate a unique request ID.
- Save the request to the database.
- Notify the destination station immediately.
- Display a high-priority popup.
- Play an audible alarm.
- Flash the notification until staff acknowledges or silences it.

## 6. DESTINATION STATION ACKNOWLEDGEMENT

Only the destination station can acknowledge an assistance request.

Display:

- Request ID.
- Boarding station.
- Destination station.
- Passenger count.
- Assistance type.
- Train number.
- Train set.
- Circular direction.

Buttons:

- ACKNOWLEDGE.
- SILENCE ALARM.

After acknowledgement:

- Change request status to ACKNOWLEDGED.
- Save acknowledgement timestamp and staff identity.
- Notify the boarding station.
- Display the updated status on both dashboards.

Prevent duplicate acknowledgements. If the server reports that the request/notice was already acknowledged, treat it as success: clear the alarm locally, show an informational message and refresh.

## 7. TRAIN DEPARTURE AND MANUAL MOVEMENT

After destination acknowledgement, the boarding station must have a TRAIN DEPARTED button.

After departure, display an UPDATE TRAIN LOCATION button.

When clicked, show all stations in the correct selected circular direction.

Staff selects the current train station manually.

The system must:

- Save current train location.
- Save update timestamp.
- Calculate stations remaining before destination.
- Validate the direction and station sequence.
- Prevent backwards movement.
- Prevent movement beyond destination.
- Maintain movement history.

## 8. THREE-STATION SECOND ALARM

This is a critical feature.

When the train reaches exactly THREE STATIONS BEFORE THE DESTINATION:

- Trigger an automatic second notification.
- Play a loud audible alarm at the destination station.
- Display a flashing red popup.
- Show train number and train set.
- Show passenger count and assistance type.
- Display exactly:

  “TRAIN APPROACHING – PLEASE PREPARE FOR DIVYANGJAN ASSISTANCE.”

Use 3 minutes per station as the estimated travel time.

Display the estimated arrival time based on remaining stations.

IMPORTANT: The three-station alarm must be triggered by the actual manually updated station location, NOT simply by a timer.

Send this second notification only once per request.

Provide an alarm acknowledgement/silence control.

## 9. ARRIVAL AND COMPLETION

At the destination station, display ASSISTANCE PROVIDED.

Only destination station staff can mark the request completed.

Save:

- Completion time.
- Destination station.
- Staff identity.
- Request ID.

Update the status to COMPLETED on all relevant dashboards.

## 10. OTHER INFORMATION MODULE

Create a separate module with three options:

A. LOST & FOUND
B. SEPARATE PASSENGER
C. OTHER

After category selection, show a large manual description box.

Example: “Blue bag containing important documents found near Platform 2.”

Provide:

- Category.
- Description.
- Originating station automatically selected.
- Optional image attachment.
- Submit button.

On submission:

- Generate a unique information ID.
- Broadcast the information to ALL active stations.
- Display a popup notification at every station.
- Save information in a common notice board.
- Display originating station and timestamp.
- Allow staff to acknowledge receipt.
- Allow administrator to close outdated notices.

## 11. ALARMS AND NOTIFICATION DELIVERY

Every new assistance request and every Other Information broadcast must raise an alarm, not only the three-station event.

Implement:

- Real-time station notifications.
- High-priority popups for every new request and broadcast.
- Audible alarms (after ENABLE SOUND).
- Flashing visual alerts.
- Browser notifications.
- PWA push notification support.
- Reconnection after network interruption.
- Duplicate alert protection.
- Notification delivery history.
- “ENABLE SOUND” control after login.
- TEST ALARM functionality.

Use persistent notification delivery for background or disconnected devices. Do not rely solely on a browser tab remaining open.

Alarm overlays must always be dismissible: ACKNOWLEDGE and SILENCE ALARM. Never leave a modal stuck on an “already closed” error.

## 12. ADMINISTRATOR PANEL

Create a separate secure admin dashboard.

Administrator features:

A. Station management:

- Upload verified station list through CSV or Excel.
- Add station.
- Edit station.
- Activate/deactivate station.

B. Route management:

- Configure Plus Circular sequence.
- Configure Minus Circular sequence.
- Verify route ordering.
- Edit route sequences.

C. User management:

- Create station accounts.
- Reset passwords.
- Activate/deactivate accounts.
- Manage administrator permissions.

D. Request management:

- View all station requests.
- Monitor pending acknowledgements.
- View train movement.
- View completed requests.
- View request history.

E. Reports:

- Daily report.
- Weekly report.
- Monthly report.
- Station-wise assistance report.
- Visually impaired passenger statistics.
- Wheelchair assistance statistics.
- Average acknowledgement time.
- Average assistance completion time.

F. System management:

- Notification settings.
- Alarm settings.
- Audit logs.
- Database backup and export.

## 13. STATION DATABASE AND ROUTE ENGINE

Do not invent or assume the official Pink Line station sequence.

Create an administrator-uploadable station master.

Required columns:

- station_code
- station_name
- plus_sequence
- minus_sequence
- active_status

The system must validate:

- No duplicate station codes.
- No duplicate station names.
- No duplicate sequence numbers in the same direction.
- All active stations have both direction sequences.
- Boarding and destination stations are valid.

Allow the administrator to upload the official verified station data.

Use this database to calculate station distances and three-station alerts.

## 14. DATABASE DESIGN

Create tables:

- stations
- route_sequences
- users
- assistance_requests
- request_acknowledgements
- train_movements
- notifications
- other_information
- information_acknowledgements
- audit_logs

Create appropriate foreign keys and indexes.

Enable Row Level Security when Supabase is used. In local SQLite mode, enforce the same rules in server-side API handlers.

Security rules:

- Staff can access only permitted station operations.
- Only the destination station can acknowledge and complete its requests.
- Only the boarding station can update departure and train movement.
- All active stations can receive Other Information broadcasts.
- Only administrators can manage station data and accounts.

## 15. REAL-TIME SYSTEM AND PWA

Implement:

- SSE or Supabase realtime channel for live updates.
- Reconnection and heartbeat handling.
- Server-sent alarms for new requests and broadcasts.
- Web Push with VAPID keys when configured.
- Service worker that never caches API responses.
- Installable PWA with icons, manifest and offline shell.
- Browser notification permission flow.

## 16. SAMPLE DEMONSTRATION MODE

Create sample demo accounts for all seeded stations. Use synthetic demonstration data.

The original two-station scenario must work:

1. Log in as Anand Vihar.
2. Create a visually impaired request.
3. Enter 2 passengers.
4. Train number 2890.
5. Train set 23.
6. Destination Lajpat Nagar.
7. Select + Circular.
8. Open a second browser session as Lajpat Nagar.
9. Receive the notification and alarm.
10. Acknowledge the request.
11. Return to Anand Vihar.
12. Mark train departed.
13. Update train location station by station.
14. At three stations before Lajpat Nagar, trigger the second alarm.
15. At Lajpat Nagar, mark assistance provided.
16. Confirm completed status at both stations.

Also test wheelchair assistance and Other Information broadcasts, including the alarm at every station.

## 17. ANDROID APP

Package the web application in a native Android WebView app and provide a signed APK.

Normal Android behaviour:

- Launcher icon and native splash screen.
- Tap icon → app opens and connects automatically.
- No setup form, no Connection Settings screen, no manual server address entry anywhere.
- Users must never be asked to connect to station Wi-Fi.

Connection order on every launch:

1. Last successfully used server.
2. Production HTTPS server URL baked into the app at build time from an environment configuration file (`android-apk/default-server.txt` or `METRO_DEFAULT_SERVER`).
3. LAN discovery on port 3000, verifying each candidate through an identity endpoint such as `/api/health`.

Never use or accept `localhost`, `127.0.0.1`, `::1` or `0.0.0.0` in the production app.

If connected, show the login screen, or the dashboard directly when the session is still valid.

If disconnected, show exactly:

- Title: “Unable to connect”
- Message: “Please check your internet connection and try again.”
- A TRY AGAIN button.
- Automatic retry every few seconds, plus immediate retry when Wi-Fi or mobile data returns.

Also implement:

- Persistent cookies/session across app restarts.
- Live SSE updates and in-app alarm overlays with audio after ENABLE SOUND.
- Back navigation and double-back exit.
- CSV/JSON/SQLite downloads saved to a Downloads folder.
- Image uploads through the Android file picker.
- Release signing with a project keystore reused for every future update.

Build realities to document honestly:

- A WebView app cannot host the Next.js server, SQLite database or SSE stream.
- Mobile data and public use require the server to be deployed at a publicly reachable HTTPS address, which must be baked into the APK at build time.
- Provide a working local mode for station Wi-Fi and document what must be configured for public use.
- Do not claim mobile-data support without a public backend URL.

## 18. DELIVERY

After building, provide:

- Working application preview.
- Installable signed Android APK.
- Complete source code.
- Database configuration.
- Deployment instructions.
- Admin login setup.
- Station CSV template.
- Documentation of alarms, realtime, PWA and APK build/signing.

## 19. BUILD RULES

Build the application in working stages:

1. Complete responsive frontend.
2. Database and secure authentication.
3. Assistance request workflow.
4. Real-time notifications and acknowledgement.
5. Manual train tracking and second alarm.
6. Other Information broadcasting.
7. Admin panel and verified station import.
8. Testing, PWA and Android APK.

Do not leave buttons non-functional.
Do not use fake frontend-only data for core workflows.
Use actual database operations.
If external services or API credentials are missing, clearly explain what must be configured and provide a working local development mode.

## 20. CONSTRAINTS (VERBATIM)

- IMPORTANT: This is an independent prototype. Do not connect to DMRC internal systems, claim official DMRC approval, or use real passenger data without authorization.
- For security, use a temporary initial password only during testing. Require administrator-controlled unique station credentials before deployment.
- Do not invent or assume the official Pink Line station sequence.
- Do not leave buttons non-functional. Do not use fake frontend-only data for core workflows. Use actual database operations. If external services or API credentials are missing, clearly explain what must be configured and provide a working local development mode.

The final result must be a fully functional Metro Assist application, not merely a website design.

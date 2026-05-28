# Smart City IoT Parking System

Μια ολοκληρωμένη full-stack End-to-End IoT λύση για τη διαχείριση έξυπνης στάθμευσης σε πραγματικό χρόνο (Smart City). Το σύστημα χρησιμοποιεί έτοιμο serverless cloud backend στο Microsoft Azure, επιτρέποντας την άμεση δοκιμή της native Android εφαρμογής και την αποστολή δεδομένων τηλεμετρίας μέσω Azure CLI.

---

## 🏗️ Αρχιτεκτονική Συστήματος & Ροή Δεδομένων

Το σύστημα βασίζεται σε event-driven αρχιτεκτονική και υλοποιείται με την παρακάτω ροή:

```
[ IoT Sensors ] ──(Azure CLI )──> [ Azure IoT Hub ]
                                               │
                                         (Event Grid)
                                               ▼
                                   [ backend-sensors (Azure) ]
                                               │
                                         (Cosmos DB SDK)
                                               ▼
                                    [ Azure Cosmos DB (Live) ]
                                               ▲
                                         (Cosmos DB SDK)
                                               │
                                    [ backend-auth (Azure) ]
                                               ▲
                                          (Retrofit)
                                               │
                                        [ mobile-app (Android) ]
# Smart City IoT Parking System

Μια ολοκληρωμένη full-stack End-to-End IoT λύση για τη διαχείριση έξυπνης στάθμευσης σε πραγματικό χρόνο (Smart City). Το σύστημα χρησιμοποιεί έτοιμο serverless cloud backend στο Microsoft Azure, επιτρέποντας την άμεση δοκιμή της native Android εφαρμογής και την αποστολή δεδομένων τηλεμετρίας μέσω Azure CLI.

---

## 🏗️ Αρχιτεκτονική Συστήματος & Ροή Δεδομένων

Το σύστημα βασίζεται σε event-driven αρχιτεκτονική και υλοποιείται με την παρακάτω ροή:

```text
[ IoT Sensors ] ──(Azure CLI / MQTT)──> [ Azure IoT Hub ]
                                               │
                                         (Event Grid)
                                               ▼
                                   [ backend-sensors (Azure) ]
                                               │
                                         (Cosmos DB SDK)
                                               ▼
                                    [ Azure Cosmos DB (Live) ]
                                               ▲
                                         (Cosmos DB SDK)
                                               │
                                    [ backend-auth (Azure) ]
                                               ▲
                                          (Retrofit)
                                               │
                                        [ mobile-app (Android) ]
# Smart City IoT Parking System

Μια ολοκληρωμένη full-stack End-to-End IoT λύση για τη διαχείριση έξυπνης στάθμευσης σε πραγματικό χρόνο (Smart City). Το σύστημα χρησιμοποιεί έτοιμο serverless cloud backend στο Microsoft Azure, επιτρέποντας την άμεση δοκιμή της native Android εφαρμογής και την αποστολή δεδομένων τηλεμετρίας μέσω Azure CLI.

---

## 🏗️ Αρχιτεκτονική Συστήματος & Ροή Δεδομένων

Το σύστημα βασίζεται σε event-driven αρχιτεκτονική και υλοποιείται με την παρακάτω ροή:

```text
[ IoT Sensors ] ──(Azure CLI / MQTT)──> [ Azure IoT Hub ]
                                               │
                                         (Event Grid)
                                               ▼
                                   [ backend-sensors (Azure) ]
                                               │
                                         (Cosmos DB SDK)
                                               ▼
                                    [ Azure Cosmos DB (Live) ]
                                               ▲
                                         (Cosmos DB SDK)
                                               │
                                    [ backend-auth (Azure) ]
                                               ▲
                                          (Retrofit)
                                               │
                                        [ mobile-app (Android) ]
```
IoT Telemetry: Προσομοιωμένοι αισθητήρες στέλνουν την κατάσταση των θέσεων (Free/Occupied) απευθείας στο Azure IoT Hub χρησιμοποιώντας εντολές CLI.

Azure Cloud Backend: Το backend τρέχει ήδη live στο Azure (Azure Functions & Cosmos DB), επεξεργάζεται τα δεδομένα των αισθητήρων και διαχειρίζεται το User Authentication.

mobile-app (Android): Η εφαρμογή συνδέεται στα live cloud endpoints, επιτρέποντας στους χρήστες να κάνουν Login/Register και να βλέπουν σε πραγματικό χρόνο τη διαθεσιμότητα των θέσεων.

📂 Δομή του Repository (Monorepo)
login-register-function-app/: Ο πηγαίος κώδικας των Azure Functions (Python) για το User Auth, ο οποίος είναι ήδη deployed στο Cloud.

sensors-functino-app/: Ο κώδικας των Functions για την επεξεργασία των IoT σημάτων.

mobile-app/: Ο κώδικας της native εφαρμογής Android (Java) προς δοκιμή και αξιολόγηση.

🚀 Οδηγίες Δοκιμής & Τεσταρίσματος (Testing Guide)
1. Εκτέλεση και Δοκιμή του Mobile App (Android Studio)
Δεν χρειάζεται να στήσετε τοπική βάση δεδομένων ή local Azure Functions. Η εφαρμογή είναι έτοιμη να επικοινωνήσει απευθείας με το Live Cloud Backend:

Ανοίξτε τον φάκελο mobile-app μέσω του Android Studio.

Προσθέστε το Google Maps API Key σας στο αρχείο local.properties:

Plaintext
MAPS_API_KEY=your_api_key_here
Κάντε Gradle Sync και τρέξτε την εφαρμογή σε έναν Emulator ή φυσική συσκευή.

Μπορείτε να κάνετε άμεσα Register νέο χρήστη και Login στην εφαρμογή.

2. Εξομοίωση Αισθητήρα & Αποστολή Δεδομένων στο IoT Hub
Για να τεστάρετε τη ζωντανή αλλαγή κατάστασης των θέσεων πάρκινγκ στην Android εφαρμογή, μπορείτε να στείλετε ένα IoT μήνυμα (Telemetry Payload) απευθείας στο Azure IoT Hub.

Ανοίξτε το τερματικό σας, βεβαιωθείτε ότι έχετε το Azure CLI με το IoT extension (az extension add --name azure-iot), και εκτελέστε την παρακάτω εντολή:

Bash
```
az iot device send-d2c-message \
  -n SensorsHub \
  -d Sensor_05 \
  --data '{"data": "{\"parkingId\": \"5\", \"status\": 1}"}'
```



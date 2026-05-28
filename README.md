# Smart City IoT Parking System

Μια ολοκληρωμένη full-stack End-to-End IoT λύση για τη διαχείριση έξυπνης στάθμευσης σε πραγματικό χρόνο (Smart City). Το σύστημα αποτελείται από φυσικούς αισθητήρες (ή εξομοιωτές) που καταγράφουν την πληρότητα των θέσεων, ένα serverless cloud backend στο Microsoft Azure για την επεξεργασία και δρομολόγηση των δεδομένων, και μια native Android εφαρμογή για την πλοήγηση και ενημέρωση των οδηγών.

---

## 🏗️ Αρχιτεκτονική Συστήματος & Ροή Δεδομένων

Το σύστημα βασίζεται σε event-driven αρχιτεκτονική και υλοποιείται με την παρακάτω ροή:

```text
[ IoT Sensors ] ──(MQTT/HTTP)──> [ Azure IoT Hub ]
                                         │
                                   (Event Grid)
                                         ▼
                             [ backend-sensors (Azure Function) ]
                                         │
                                   (Cosmos DB SDK)
                                         ▼
                              [ Azure Cosmos DB ]
                                         ▲
                                   (Cosmos DB SDK)
                                         │
                              [ backend-auth (Azure Function) ]
                                         ▲
                                    (Retrofit)
                                         │
                                  [ mobile-app (Android) ]
IoT Sensors: Οι αισθητήρες στέλνουν την κατάσταση των θέσεων (Free/Occupied) στο Azure IoT Hub.

Azure IoT Hub & Event Grid: Λαμβάνει τα μηνύματα και πυροδοτεί (trigger) την αντίστοιχη Azure Function.

backend-sensors (Function): Επεξεργάζεται τα payloads των αισθητήρων και ενημερώνει τη βάση δεδομένων.

backend-auth (Function): Διαχειρίζεται με ασφάλεια το User Authentication (Registration & Login) των χρηστών.

Azure Cosmos DB: Η NoSQL βάση δεδομένων που αποθηκεύει σε πραγματικό χρόνο την κατάσταση των θέσεων και τα προφίλ των χρηστών.

mobile-app (Android): Ο οδηγός βλέπει live τις διαθέσιμες θέσεις, κάνει κρατήσεις και πλοηγείται σε αυτές.

📂 Δομή του Repository (Monorepo)
Το repository είναι οργανωμένο ως Monorepo, διαχωρίζοντας καθαρά το backend από το frontend:

backend-auth/ (login-register-function-app): Azure Functions (Python) υπεύθυνες για το user authentication, το registration και την έκδοση ασφαλών tokens.

backend-sensors/ (sensors-functino-app): Azure Functions (Python) που ενεργοποιούνται μέσω IoT Hub triggers για την επεξεργασία των δεδομένων τηλεμετρίας.

mobile-app/: Ο κώδικας της native εφαρμογής Android (Java) που χρησιμοποιεί Retrofit για την επικοινωνία με τα Azure endpoints και Google Maps API για την πλοήγηση.

🚀 Τεχνολογικό Stack
Cloud Infrastructure: Microsoft Azure (IoT Hub, Azure Functions, Event Grid)

Database: Azure Cosmos DB (NoSQL API)

Backend Runtime: Python 3.11 / Azure Functions Core Tools v4

Mobile App: Android SDK (Java), Retrofit 2, Google Maps SDK

🛠️ Οδηγίες Εγκατάστασης & Local Setup
1. Backend (Azure Functions)
Για να τρέξεις τις συναρτήσεις τοπικά, θα πρέπει να προσθέσεις ένα αρχείο local.settings.json μέσα στους αντίστοιχους φακέλους (backend-auth και backend-sensors):

JSON
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "python",
    "CosmosDBConnectionString": "YOUR_AZURE_COSMOS_DB_CONNECTION_STRING"
  }
}
Εκτέλεση τοπικά μέσω terminal:

Bash
func start
2. Mobile App (Android Studio)
Άνοιξε τον φάκελο mobile-app μέσω του Android Studio.

Πρόσθεσε το Google Maps API Key σου στο αρχείο local.properties:

Plaintext
MAPS_API_KEY=your_api_key_here
Κάνε Gradle Sync και τρέξε την εφαρμογή σε έναν Emulator ή φυσική συσκευή.


---

### Πώς να το ανεβάσεις τώρα μαζί με το Android App:
Μόλις σώσεις το README.md στην κεντρική ρίζα και μεταφέρεις τα αρχεία του Android στο `mobile-app`, τρέξε τις τελικές εντολές στο Terminal του Android Studio για να κλείσει το project υποδειγματικά:

```bash
git add .
git commit -m "docs: update README with full system architecture and monorepo structure"
git push -u origin main --force

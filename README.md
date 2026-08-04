# Quester – Progetto Mobile (Kotlin)

## Descrizione del progetto
**Quester** è un'app mobile pensata per aiutare l’utente a gestire attività quotidiane e obiettivi personali in modo più motivante, trasformandoli in una piccola esperienza di gioco.

L’idea principale è contrastare la procrastinazione attraverso un sistema di missioni, progressi e ricompense: completare i compiti dà soddisfazione immediata e rende più semplice costruire abitudini positive.


> Nota: questo perimetro iniziale ci permette di sviluppare una base stabile, coerente con l’architettura MVVM e facilmente testabile.
## Obiettivo
L’obiettivo del progetto è sviluppare un’app Android in **Kotlin** che unisca:
- organizzazione personale
- monitoraggio dei progressi
- meccaniche di gamification

## Funzionalità previste (versione iniziale)
Le funzionalità che vogliamo implementare sono:

- Creazione e gestione di missioni personali
  - missioni giornaliere
  - missioni settimanali
  - eventi/sfide speciali
- Tracciamento avanzamento missioni
- Sistema di punti esperienza (XP)
- Progressione di livello (level-up)
- Ricompense virtuali (valuta in-app)
- Dashboard riepilogativa dei progressi
- Sezione premi/negozio
- Notifiche e promemoria

> Nota: il progetto è in fase iniziale, quindi alcune funzionalità potranno essere modificate o semplificate durante lo sviluppo.

## Stato progetto

### ✅ Backend/Logica completata (MVP)
- Data layer con Room:
  - `User`, `Mission`, `SubTask`, `ShopItem`, `OwnedCosmetic`
- DAO:
  - `UserDao`, `MissionDao`, `SubTaskDao`, `ShopDao`, `OwnedCosmeticDao`
- Database:
  - `AppDatabase` + `DatabaseProvider`
- Repository:
  - `UserRepository`, `MissionRepository`, `AuthRepository`
- Auth:
  - registrazione/login con hash password PBKDF2
  - sessione persistente via DataStore (`SessionManager`)
  - `AuthService` per orchestrazione auth end-to-end
- Mission orchestration:
  - creazione missione da form (`createMissionFromForm`)
  - toggle subtask con auto-completamento missione
  - reward XP idempotente (no doppio reward)
- Gamification:
  - `CurrencyService` (coins su level-up/eventi/redeem)
- Shop:
  - `ShopService.purchase` con check saldo/item/owned
- Reminder:
  - `ReminderService` con WorkManager (schedule/cancel)
  - `ReminderWorker` predisposto per notifica locale

### ⏳ Da completare
- UI Compose definitiva
- ViewModel completi per tutte le schermate
- Navigation app (Auth/Home/Profile/Shop/Mission details)

---

## Requisiti tecnici
- minSdk: 24
- targetSdk: 34
- Kotlin: 2.0.21
- AGP: 8.5.2
- Room: 2.6.1
- WorkManager: 2.9.1
- DataStore Preferences: 1.1.1

---

## Test

### Test strumentali implementati
- `AppDatabaseTest`
- `AuthRepositoryTest`
- `MissionServiceTest`
- `CurrencyServiceTest`
- `ShopServiceTest`
- `ReminderServiceTest`

### Esecuzione test
```bash
./gradlew connectedDebugAndroidTest
```
## Contesto accademico
Progetto realizzato per l’esame di **Programmazione Mobile**.

## Team
- Giovanni De Luca
- Gabriele Di Carlo

## Note
Questa README verrà aggiornata progressivamente con:
- struttura del progetto
- istruzioni di avvio
- dettagli implementativi
- screenshot/mockup

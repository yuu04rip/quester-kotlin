# Quester – Progetto Mobile (Kotlin)

##  Descrizione del progetto

**Quester** è un'app mobile pensata per aiutare l'utente a gestire attività quotidiane e obiettivi personali in modo più motivante, trasformandoli in una piccola esperienza di gioco.

L'idea principale è contrastare la procrastinazione attraverso un sistema di missioni, progressi e ricompense: completare i compiti dà soddisfazione immediata e rende più semplice costruire abitudini positive.

> Nota: questo perimetro iniziale ci permette di sviluppare una base stabile, coerente con l'architettura MVVM e facilmente testabile.

---

##  Obiettivo

L'obiettivo del progetto è sviluppare un'app Android in **Kotlin** che unisca:
- Organizzazione personale
- Monitoraggio dei progressi
- Meccaniche di gamification

---

##  Funzionalità implementate

###  Gestione Missioni
- Creazione e gestione di missioni personali
  - **Missioni Giornaliere** – attività quotidiane
  - **Missioni Settimanali** – obiettivi a medio termine
  - **Missioni Speciali** – sfide ed eventi
- Tracciamento avanzamento missioni con subtask
- Sistema di completamento automatico
- Modifica ed eliminazione missioni
- Reset missioni

###  Gamification
- Sistema di **Punti Esperienza (XP)** lineare
  - Formula: `XP = 100 + (livello - 1) * 50`
  - Livello massimo: **50**
- **Monete** come valuta in-app
  - Guadagni da missioni (1, 5, 15 monete)
  - Guadagni da Level Up (3, 5, 8, 12, 20 monete)
- **Ricompense fisse** per tipo di missione:
  | Tipo | XP | Monete |
  |------|----|--------|
  | Giornaliera | 30 | 1 |
  | Settimanale | 120 | 5 |
  | Speciale | 400 | 15 |

###  Profilo e Personalizzazione
- Visualizzazione profilo utente
- **Avatar personalizzabile** con cosmetici:
  - Cornici/Stendardi (3 disponibili – 30 monete)
  - Cosmetici personaggio (6 disponibili – 100 monete)
  - Temi (2 disponibili – 500 monete)
- Modifica nome utente
- Eliminazione account

###  Negozio
- Acquisto cosmetici con monete
- Gestione oggetti posseduti
- Prezzi bilanciati:
  - Cornici: 30 monete
  - Cosmetici: 100 monete
  - Temi: 500 monete

###  Reward Finale
Al raggiungimento del livello 50, l'utente riceve:
- 👑 **Corona dell'Eroe** – cosmetico esclusivo
- ✦ **Tema Regale** – personalizzazione app

---

## 🛠 Tecnologie e Architettura

###  Data Layer (Room)
- **Entity**: `User`, `Mission`, `SubTask`, `ShopItem`, `OwnedCosmetic`
- **DAO**: `UserDao`, `MissionDao`, `SubTaskDao`, `ShopDao`, `OwnedCosmeticDao`
- **Database**: `AppDatabase` + `DatabaseProvider`

### 🏗 Repository
- `UserRepository` – gestione utenti, XP, monete, cosmetici
- `MissionRepository` – gestione missioni e subtask
- `AuthRepository` – autenticazione

###  Autenticazione
- Registrazione/Login con hash password **PBKDF2**
- Sessione persistente via **DataStore** (`SessionManager`)
- `AuthService` per orchestrazione auth end-to-end

###  Servizi di Gioco
- `MissionService` – creazione, completamento, reset missioni
- `CurrencyService` – gestione monete (level-up, eventi speciali)
- `ShopService` – acquisto cosmetici con check saldo/possesso
- `ReminderService` – promemoria con **WorkManager**

### 🖥 UI (Compose)
- `AuthScreen` – login/registrazione
- `NavBar` – navigazione a 3 schermate (Profilo, Missioni, Negozio)
- `ProfileScreen` – profilo utente con avatar personalizzabile
- `MissionListScreen` – lista missioni con filtri e dialog
- `ShopScreen` – negozio cosmetici

---

## 📱 Schermate Principali

### 1. Autenticazione
- Login con username o email
- Registrazione con username, email (opzionale), password
- Validazione campi con messaggi fantasy

### 2. Profilo
- Avatar personalizzabile
- Nome utente con modifica (✎)
- Livello e XP
- Barra di progresso XP
- Statistiche (XP totali, monete)
- Cosmetici posseduti
- Pulsanti: "Esci dal Regno" e "Lascia il Regno" (elimina account)

### 3. Missioni
- Lista missioni con filtro (Tutte, In corso, Completate)
- Barra di ricerca
- Creazione missione con tipo, titolo, descrizione, subtask
- Modifica missione
- Eliminazione con undo (Snackbar)
- Reset missione
- Dettaglio missione con toggle subtask

### 4. Negozio
- Visualizzazione cosmetici disponibili
- Indicazione oggetti posseduti
- Acquisto con monete
- Feedback con Snackbar

---

##  Sistema Economico

### Guadagni
| Fonte | Ricompensa |
|-------|------------|
| Missione Giornaliera | 1 moneta, 30 XP |
| Missione Settimanale | 5 monete, 120 XP |
| Missione Speciale | 15 monete, 400 XP |
| Level Up (1-10) | 3 monete |
| Level Up (11-20) | 5 monete |
| Level Up (21-30) | 8 monete |
| Level Up (31-40) | 12 monete |
| Level Up (41-50) | 20 monete |

### Spese
| Cosmetico | Prezzo |
|-----------|--------|
| Cornice | 30 monete |
| Cosmetico | 100 monete |
| Tema | 500 monete |

---

##  Progressione Livelli

Formula lineare: `XP = 100 + (livello - 1) * 50`

| Livello | XP per salire | XP Totale |
|---------|---------------|-----------|
| 1 → 2 | 100 | 100 |
| 2 → 3 | 150 | 250 |
| 3 → 4 | 200 | 450 |
| 5 → 6 | 300 | 1.000 |
| 10 → 11 | 550 | 3.250 |
| 20 → 21 | 1.050 | 11.500 |
| 30 → 31 | 1.550 | 24.750 |
| 40 → 41 | 2.050 | 43.000 |
| 49 → 50 | 2.550 | 63.750 |

---

##  Test

### Test implementati
- `AppDatabaseTest` – test database Room
- `AuthRepositoryTest` – test autenticazione
- `MissionServiceTest` – test missioni e ricompense
- `CurrencyServiceTest` – test monete e level-up
- `ShopServiceTest` – test acquisti
- `ReminderServiceTest` – test promemoria

### Esecuzione test
```bash
./gradlew connectedDebugAndroidTest
```

---

##  Struttura del Progetto

```
app/src/main/java/com/example/quester/
├── data/
│   ├── dao/          # Room DAO
│   ├── database/     # AppDatabase + DatabaseProvider
│   ├── model/        # Entity
│   ├── repository/   # Repository
│   └── session/      # SessionManager (DataStore)
├── domain/
│   └── service/      # Servizi (Auth, Mission, Currency, Shop, Reminder)
├── ui/
│   ├── components/   # Componenti UI riutilizzabili
│   ├── screens/      # Schermate (Auth, Profile, Mission, Shop)
│   │   ├── auth/
│   │   ├── mission/
│   │   ├── profile/
│   │   ├── shop/
│   │   └── customization/
│   └── theme/        # Tema app (colori, font, typography)
├── utils/            # Utility (StringUtils, NotificationHelper)
└── MainActivity.kt   # Entry point
```

---

## 🔧 Requisiti Tecnici

| Componente | Versione |
|------------|----------|
| **minSdk** | 24 |
| **targetSdk** | 34 |
| **Kotlin** | 2.0.21 |
| **AGP** | 8.5.2 |
| **Room** | 2.6.1 |
| **WorkManager** | 2.9.1 |
| **DataStore** | 1.1.1 |
| **Compose BOM** | 2024.12.01 |
| **Coil** | 2.7.0 |

---

##  Avvio del Progetto

1. **Clona il repository**
   ```bash
   git clone [url-repository]
   cd quester-kotlin
   ```

2. **Apri il progetto in Android Studio** (Ladybug o superiore)

3. **Sincronizza Gradle**
  - File → Sync Project with Gradle Files

4. **Esegui l'app**
  - Seleziona un dispositivo/emulatore
  - Clicca su Run ▶️

---

##  Tema e Stile

L'app utilizza un tema **Fantasy** con:
- Colori dinamici tramite `MaterialTheme.colorScheme`
- Font personalizzato (`northeternal.ttf`) per titoli
- Icone personalizzate (monete, stelle animate)
- Supporto temi scuro/chiaro

---

##  Team

- **Giovanni De Luca**
- **Gabriele Di Carlo**

---

##  Note

Questa README verrà aggiornata progressivamente con:
- Screenshot e mockup
- Diagrammi di architettura
- Dettagli implementativi aggiuntivi
---

**Ultimo aggiornamento:** Agosto 2026
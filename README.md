# Quester – Progetto Mobile (Kotlin)

## Descrizione del Progetto

**Quester** è un'applicazione mobile sviluppata in Kotlin che unisce la gestione delle attività quotidiane a meccaniche di gamification. L'obiettivo principale è contrastare la procrastinazione trasformando le routine personali in missioni, fornendo ricompense e tracciando i progressi per favorire abitudini positive.

---

## Obiettivo

Sviluppare un'applicazione Android nativa in Kotlin basata su un'architettura pulita e scalabile, integrando organizzazione personale, persistenza locale dei dati e un sistema di progressione basato su livelli e valuta virtuale.

---

## Funzionalità Principali

### Gestione Missioni

* **Tipologie di missioni**: Missioni Giornaliere, Settimanali e Speciali.
* **Subtask**: Tracciamento dei singoli passaggi interni ad ogni missione con barra di avanzamento dinamica.
* **Azioni**: Creazione, modifica, eliminazione con supporto all'undo tramite Snackbar, completamento automatico e reset.

### Gamification

* **Sistema XP**: Punti esperienza con progressione lineare calcolata tramite la formula dedicata e livello massimo fissato a 50.
* **Valuta in-game**: Guadagno di monete attraverso il completamento delle attività e il superamento dei livelli.
* **Ricompense**: Assegnazione di XP e monete differenziata in base alla tipologia di missione.

### Profilo e Personalizzazione

* Gestione delle informazioni utente e visualizzazione delle statistiche complessive.
* **Avatar personalizzabile**: Modifica di cornici, cosmetici del personaggio e temi grafici dell'applicazione.
* Gestione dell'account con opzioni di disconnessione ed eliminazione.

### Negozio

* Catalogo per l'acquisto di elementi estetici utilizzando la valuta accumulata nel corso delle attività.
* Verifica automatica del saldo e dello stato di possesso degli oggetti.

---

## Tecnologie e Architettura

L'applicazione adotta il pattern architetturale **MVVM (Model-View-ViewModel)** con separazione netta dei layer:

* **Data Layer**: Persistenza locale gestita tramite **Room Database** (Entity, DAO e provider centralizzato) e preferenze/sessioni affidate a **DataStore**.
* **Repository Pattern**: Astrazione dell'accesso ai dati per utenti, missioni e autenticazione (`UserRepository`, `MissionRepository`, `AuthRepository`).
* **Autenticazione**: Sistema sicuro basato su hashing delle password con algoritmo **PBKDF2** e orchestrazione tramite `AuthService`.
* **Servizi di Business Logic**: Gestione centralizzata di missioni, economia di gioco, acquisti e promemoria in background tramite **WorkManager**.
* **User Interface**: Interfaccia reattiva sviluppata interamente con **Jetpack Compose**.

---

## Requisiti Tecnici

| Componente | Versione / Specifica |
| --- | --- |
| **minSdk** | 24 |
| **targetSdk** | 34 |
| **Kotlin** | 2.0.21 |
| **Android Gradle Plugin** | 8.5.2 |
| **Room** | 2.6.1 |
| **WorkManager** | 2.9.1 |
| **DataStore** | 1.1.1 |
| **Compose BOM** | 2024.12.01 |
| **Coil** | 2.7.0 |

---

## Testing

La robustezza del codice è validata attraverso una suite di test strutturati:

* **Unit Test**: Validazione della logica di business, calcolo degli XP e dei livelli.
* **Instrumented Test**: Verifica delle operazioni CRUD sul database Room, dei repository, dei servizi di gioco e della schedulazione dei promemoria.

---

## Struttura del Progetto

```text
app/src/main/java/com/example/quester/
├── data/
│   ├── dao/          # Data Access Objects (Room)
│   ├── database/     # Database principale e provider
│   ├── model/        # Entità del database
│   ├── repository/   # Repository di accesso ai dati
│   └── session/      # Gestione sessione utente (DataStore)
├── domain/
│   └── service/      # Servizi di logica applicativa
├── ui/
│   ├── components/   # Componenti grafici riutilizzabili
│   ├── screens/      # Schermate dell'applicazione (Auth, Profile, Mission, Shop)
│   └── theme/        # Definizione di colori, tipografia e temi
├── utils/            # Classi di utilità e supporto
└── MainActivity.kt   # Entry point dell'applicazione

```

---

## Team di Sviluppo

* **Giovanni De Luca**
* **Gabriele Di Carlo**
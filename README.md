# MusicRoom
42 project where you have to develop a music streaming service

## Team members & work repartition:
| name      | worked on        |
|-----------|------------------|
| nalebrun  | Android app (most of the logic, friends, music player, settings, auth), Backend (spotiscraper, ytdownloader) |
| lvodak    | Backend (auth, ...TODO), Android app (profile page) |
| jgoudema  | Frontend (artist, playlist, library, album) |
| ajossera  | Backend (web socket, ...TODO) |

## The Git :
### Stucture
```text
main
│
├── android-app         // the main branch for the kotlin android app
│   ├── /auth/login     // ex  sub branches for linked feature
│   └── /fix-bug-xxxx   // ex
│
├── dev                 // the main branch for the nest backend
│   ├── /auth/login     // ex
│   └── /fix-bug-xxxx   // ex
```
for each feature or bug fix: an `issue`, a `branch` and a `pull request` have to be oppened

### Rules
PR have to be reviewed before merged on main.

## Requirement
The android app need a certified SSL certificate on the api to work (We used Let's Encrypt). Therefore, the api must be accessible via HTTPS. Meaning that the backend cannot be runned in localhost to test with the app (api routes are still testable with curl, bruno, postman or other http tool when ran in localhost). It need a domain name to get a SSL certificate so we deployed the api on a staging server.

`Android studio` ( Quail 1 | 2026.1.1 ) has been used to dev the android app. The app is made for android device on SDK version 37 (minimal supported version 28).

The `Backend` need node.js to run the `NestJS` app and `docker` for the architecture.

## How to launch the project

### Backend :
1. clone the project
2. fill the secretsa and .env files
3. make the project (It will be launched with docker (stucture explained under) )
```bash
make up
```
4. done

### Andoid app
1. clone the project
2. open the `android-app` folder in Android Studio (a cli also exist but the steps will not be explained here)
3. install the SDK if not already installed
4. Wait for the gradle build to be done
#### for emulator : 
5. Create an emulator in android studio
6. launch the app with the "green play button"
#### for physical device : 
5. enable developer options on your device
6. connect the phone to the pc for communication via ADB
7. install the app with the "green play button"

## Docker Structure

The project is orchestrated using Docker Compose. The following diagram illustrate the service architecture, networking, and volume mounts:

Note : The `Spot-Scraper` and the `Adminer DB Ui` are exposed directly so they can be disabled without needing to touch the reverse proxy conf. They are only there to be accessed on local and/or for debug/developement purpose.

```mermaid
flowchart TD
    Internet((Internet))
    
    %% External Access
    Internet <-->|Caddy Port| Caddy[Caddy Reverse Proxy]
    Internet <-->|Port 8080| Adminer[Adminer DB UI]
    Internet <-->|Port 8031| Scraper[Spoti-Scraper]

    subgraph Docker_Compose [Docker Compose: music_room_network]
        direction TB
        
        Caddy -->|Proxy| Backend[NestJS Backend]
        
        %% Database Connections
        Backend -->|Prisma| DB[(Postgres DB)]
        Adminer -->|SQL| DB
        Scraper -->|SQL| DB

        subgraph Storage [Persistent Storage]
            direction LR
            DL[./dl MP3 Files]
            PG[(Postgres Data)]
        end

        %% Volume Mounts
        Backend --- DL
        Scraper --- DL
        DB --- PG
    end
```

## Backend - deep dive 

Git main branch for this project part : `dev`

The backend is a clasic Nestjs Structure based on Services that will be composed with Service for logic and link to the database, controllers for the API's and Modules for the inside structure and dependencies between services. The full backend use the principle of RestAPI and is documented with swaggers.
The database used for the project, PostgreSQL, is connected to the backend through a ORM named prisma, that will handles most query's through each related services. The tables schemas are written manually in the scema.prisma.
There is 4 distinct kind of service: Music related, User related, Devices and Notifications.
For the authentifictaion, we have used the concept of JSON Web Token (JWT), to keep the authentification after log in. The account creation is protected with an email verification system (SMTP). And in addition, the oauth service has been implemented for google authentification.
The Musics services handle the link between artist, music and albums as well as the playlist system that will be share with other users.
The Devices service mostly used a websocket system to communicate change in playlists and shared Music Room (and so does the playlist one).
A Firebase Notification system was attempted, but is not used, but could be easely implemented in the front end to join what is already present (some part commented) in the backend.
For each Services that used Controllers and therefore API's, dto files are present to structure the body of each request API as well as the answer body, to ensure and enforce a proper used of each route used.
All of of this is dockerized and run on a containerized caddy server that handle https request and can handle http with the right change in .env files. The backend and Database are each in separte containers.
The credentials and sensitives data are kept in secrets files or .env depending on the level of security needed.

### Websocket devices

```mermaid
    sequenceDiagram
    participant A as Device A (Client)
    participant BE as Backend
    participant B as Device B (Host)

    Note over BE: Maintains the list of<br/>connected devices via Socket.IO

    %% Connection request
    A->>BE: Request connection to B
    BE->>BE: Check if B is connected
    BE->>B: Forward connection request from A

    alt B rejects the request
        B-->>BE: Reject connection
        BE-->>A: Connection rejected

    else B accepts the request
        B->>BE: Accept + send current playback state

        Note over B,BE: Playlist<br/>Play / Pause state<br/>Current track index<br/>Playback position

        BE->>BE: Create room
        BE-->>A: Send initial playback state

        Note over A,B: Both devices are now synchronized

        %% Host changes
        B->>BE: Playback state changed
        BE->>BE: Sending updated state to the room
        BE-->>A: Updated playback state

        %% Client changes
        A->>BE: Request playback change
        BE->>BE: Check permissions

        alt Modification not allowed
            BE-->>A: Reject modification
        else Modification allowed
            BE->>BE: Apply modification
            BE->>BE: Sending updated state to the room
            A<<-->>B: Updated playback state
        end
    end
```
### Websocket playlist

```mermaid
sequenceDiagram
    participant C as Client
    participant BE as Backend
    participant R as Socket.IO Room

    Note over BE: Each playlist has a dedicated room<br/>playlist_{playlistId}

    %% Join playlist
    C->>BE: join_playlist(playlistId)
    BE->>BE: Check permission

    alt Playlist not found / access denied
        BE-->>C: Join rejected
    else Join allowed
        BE->>R: Join room
        BE->>R: Broadcast userId join, updated version
        R-->>C: Playlist joined
    end

    %% Add music
    C->>BE: add_music(playlistId, songId, version)
    BE->>BE: Check permission

    alt Version mismatch
        BE-->>C: Reject modification
    else Version is valid
        BE->>BE: Add music
        BE->>R: Broadcast music added updated version
        R-->>C: Music added
    end

    %% Move music
    C->>BE: move_music(playlistId, musicId, index, version)
    BE->>BE: Check permission

    alt Version mismatch,
        BE-->>C: Reject modification
    else Version is valid
        BE->>BE: Move music
        BE->>R: Broadcast the music moved, updated version)
        R-->>C: Music moved
    end

    %% Leave playlist
    C->>BE: leave_playlist(playlistId)
    BE->>R: Leave playlist_{playlistId}
    BE->>R: Broadcast leave_playlist
    R-->>C: Client left
```

## Android-app - deep dive

Git main branch for this project part : `android-app`

Music Room is a Kotlin-based Android application designed for collaborative music playback and management. The project strictly follows the MVVM (Model-View-ViewModel) architectural pattern to ensure a clean separation of concerns between the UI, business logic, and data layers. While developed using Kotlin, which offers potential for future iOS compatibility via Kotlin Multiplatform, the application is currently optimized and intended exclusively for the Android platform.

### 1. App Structure (File Tree)

```text
app/src/main/java/be/nalebrun/musicroom/
├── apiJsonStruct/          # Data classes for API requests and responses
│   ├── requests/
│   └── responds/
├── di/                     # Dependency Injection (Hilt modules)
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── keys/                   # Preference keys
│   └── PreferenceKey
├── repositories/           # Data layer (API, Socket, Preferences)
│   ├── APIRepository.kt
│   ├── CredentialRepository.kt
│   ├── MusicRepository.kt
│   ├── SettingsRepository.kt
│   ├── SocketIORepository.kt
│   └── UiMessageManager.kt
├── services/               # Background services
│   └── PlaybackService.kt  # Media3 MediaSession & ExoPlayer
├── ui/                     # UI Layer (Jetpack Compose)
│   ├── element/            # Reusable UI components (MiniPlayer, Navigation, etc.)
│   ├── screen/             # Main screens (Auth, Library, MusicPlayer, etc.)
│   └── theme/              # Material Design theme definitions
├── viewmodel/              # ViewModel Layer
│   ├── AuthViewModel.kt
│   ├── MusicViewModel.kt
│   ├── NavigationViewModel.kt
│   ├── SocketViewModel.kt
│   └── ...
├── DataStore.kt            # DataStore initialization
├── MainActivity.kt         # Entry point
├── MusicRoomApp.kt         # Application class
├── NavGraph.kt             # Navigation definition
└── ...
```

### 2. Some Interaction Diagrams

#### A. Auth Flow
Process of logging in and transitioning to the main application.

```mermaid
sequenceDiagram
    participant UI as AuthUi
    participant VM as AuthViewModel
    participant Repo as APIRepository
    participant Cred as CredentialRepository
    participant Nav as NavigationViewModel

    UI->>VM: login(email, password)
    VM->>Repo: login(email, password)
    Repo-->>VM: return apiLoginJson (JWT)
    VM->>Cred: setJWT(token)
    VM->>Nav: navigateTo("search")
    Nav-->>UI: (Observed via NavGraph) trigger navigate("search")
```

#### B. Music Playback Flow
How the UI interacts with the playback engine.

```mermaid
sequenceDiagram
    participant UI as MiniPlayer / MusicPlayerUi
    participant VM as MusicViewModel
    participant Repo as MusicRepository
    participant Service as PlaybackService
    participant Exo as ExoPlayer (Media3)

    UI->>VM: play() / pause()
    VM->>Repo: play() / pause()
    Repo->>Service: (MediaController) sendCommand
    Service->>Exo: play() / pause()
    Exo-->>Service: onIsPlayingChanged
    Service-->>Repo: update flow (isPlaying)
    Repo-->>VM: collect state
    VM-->>UI: Update Play/Pause Icon
```

#### C. Navigation Example
Centralized navigation logic via `NavigationViewModel`.

```mermaid
sequenceDiagram
    participant UI_A as Screen A
    participant VM as AnyViewModel
    participant NavVM as NavigationViewModel
    participant Graph as NavGraph (LaunchedEffect)
    participant Controller as NavHostController

    UI_A->>VM: User action (e.g., Click Profile)
    VM->>NavVM: navigateTo("profile")
    NavVM-->>Graph: navigationEvent changed
    Graph->>Controller: navigate("profile")
    Controller-->>Graph: OnDestinationChanged
```

### 3. Key Components Description

- **NavigationViewModel**: Acts as a central event bus for navigation. ViewModels call `navigateTo(route)` which is observed by the `NavGraph` in a `LaunchedEffect`.
- **MusicRepository**: The single source of truth for current playback state, queue, and remote control status.
- **PlaybackService**: A Media3 `MediaSessionService` that wraps `ExoPlayer`, allowing playback to continue in the background and integrating with system media controls.
- **SocketIORepository**: Handles the low-level Socket.IO connection, reconnections, and event listeners.
- **SocketViewModel**: Orchestrates the business logic for real-time features like joining a "music room" and syncing playback commands between devices.

## Server Client WS communication
ToDo

## Usage of AI
AI has been used to accelerate some repetitve task. The generated code has always been reviewed and edited when needed. And sometime it has been used to get information faster than searching on internet. Documentation has been done by human.

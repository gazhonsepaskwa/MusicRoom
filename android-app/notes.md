# backstack
```https://developer.android.com/guide/components/activities/tasks-and-back-stack``` \
Stack of activities that are currently visible to the user.
When a activity is oppened it pushed onto the backstack. When you go back with navigation controle, the activity is removed from the backstack and you land back on the previous activity.

# DI
Design patern that say that a class don't create his dependencies by itself but the depedencies are given as params.
ex : 
```
// DI
class MyClass(private val dependency: Dependency) {
}
// no DI
class MyClass {
    private val dependency: Dependency = Dependency()
}
```

# Repositories
Classes that are responsible for fetching data from the network or the local database.


# Architecture Stack
┌─────────────────────────────────────────────┐\
│  MainActivity                               │\
│  ┌───────────────────────────────────────┐  │\
│  │  NavHost (navigation controller)      │  │\
│  │  ┌─────────────────────────────────┐  │  │\
│  │  │  AuthViewModel (shared state)   │  │  │\
│  │  │  - jwtState: StateFlow          │  │  │\
│  │  │  - loginResult: StateFlow       │  │  │\
│  │  │  - ...                          │  │  │\
│  │  │                                 │  │  │\
│  │  │  ┌────────────┐  ┌────────────┐ │  │  │\
│  │  │  │ AuthScreen │  │SearchScreen│ │  │  │\
│  │  │  │(composable)│  │(composable)│ │  │  │\
│  │  │  └────┬───────┘  └─────┬──────┘ │  │  │\
│  │  │       │                │        │  │  │\
│  │  │       └────────────────┘        │  │  │\
│  │  └─────────────────────────────────┘  │  │\
│  │  ┌─────────────────────────────────┐  │  │\
│  │  │  AuthRepository                 │  │  │\
│  │  │  CredentialRepository           │  │  │\
│  │  └─────────────────────────────────┘  │  │\
│  └───────────────────────────────────────┘  │\
└─────────────────────────────────────────────┘


see navigation graph

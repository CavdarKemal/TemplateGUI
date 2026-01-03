# TemplateGUI

Ein Template-Projekt fuer Java Swing MDI-Anwendungen mit PostgreSQL-Unterstuetzung und Docker-Integration.

## Features

- **MDI-Framework**: Multiple Document Interface mit BaseView/BaseViewPanel Pattern
- **CustomerTreeView**: Hierarchische Verwaltung von Kunden/Szenarien/Testfaellen mit Checkbox-Tree
- **DatabaseView**: Generische Datenbankansicht mit SQL-Editor und Ergebnistabelle
- **Konfigurationsmanagement**: AppConfig Singleton mit Property-Gruppen
- **Docker-Support**: PostgreSQL Container und optionale GUI-Container

## Voraussetzungen

- Java 17+
- Maven 3.6+
- Docker (optional, fuer PostgreSQL)

## Build

```bash
# Kompilieren
mvn clean compile

# Distribution erstellen
mvn package

# Distribution entpacken
unzip target/TemplateGUI-1.0.0-SNAPSHOT-distribution.zip -d target/
```

## Starten

### Windows
```cmd
cd target\TemplateGUI-1.0.0-SNAPSHOT
startGUI.cmd
```

### Linux/macOS
```bash
cd target/TemplateGUI-1.0.0-SNAPSHOT
java -cp "lib/*" de.template.gui.design.MainFrame
```

## Docker

### PostgreSQL starten
```bash
cd docker
docker-compose up -d postgres
```

### PostgreSQL stoppen
```bash
cd docker
docker-compose down
```

## Projektstruktur

```
TemplateGUI/
├── pom.xml                     # Maven Build-Konfiguration
├── docker/
│   ├── docker-compose.yml      # Docker Services
│   ├── Dockerfile              # GUI Container (optional)
│   └── init-db.sql             # Datenbank-Initialisierung
├── delivery/
│   ├── config.properties       # Anwendungskonfiguration
│   ├── log4j.properties        # Logging-Konfiguration
│   └── startGUI.cmd            # Windows Startskript
├── docs/
│   ├── gui.md                  # GUI-Architektur
│   └── Maven-Artefakt-Integration.md
└── src/
    ├── assembly/
    │   └── distribution.xml    # Assembly Deskriptor
    └── main/
        ├── java/de/template/gui/
        │   ├── design/         # GUI Panels (Layout)
        │   ├── view/           # Views (Logik)
        │   ├── model/          # Datenmodelle
        │   ├── util/           # Utilities
        │   └── exception/      # Exceptions
        └── resources/
            └── icons/          # GUI Icons
```

## Packages

| Package | Beschreibung |
|---------|--------------|
| `de.template.gui.design` | GUI-Komponenten (Panels, Frames) |
| `de.template.gui.view` | View-Logik und Event-Handler |
| `de.template.gui.model` | Datenmodelle (AppConfig, ConnectionInfo, etc.) |
| `de.template.gui.util` | Utilities (ConnectionManager, TestDataLoader, etc.) |
| `de.template.gui.exception` | Exceptions |

## Architektur

Das Projekt folgt dem **Design-View-Trennung** Pattern:

- **Panel-Klassen** (design/): Enthalten nur GUI-Komponenten und Layout
- **View-Klassen** (view/): Enthalten Geschaeftslogik und Event-Handler

```
BaseView (abstrakt)
   └── createPanel() → BaseViewPanel
   └── setupToolbarActions()
   └── setupListeners()

BaseViewPanel (abstrakt)
   └── initComponents()
   └── Toolbar, Content, StatusBar
```

## Basiert auf

Dieses Template vereint Funktionalitaeten aus:
- **StandardMDIGUI**: MDI-Framework, Views, Design-Pattern
- **ITSQ-Test**: Maven-Artefakt-Integration, Assembly, Dokumentation

## Lizenz

Proprietary

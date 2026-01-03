# Claude Code Kontext - TemplateGUI

## Projekt-Uebersicht

**Pfad:** `E:\Projekte\ClaudeCode\TemplateGUI`
**Typ:** Java Swing MDI-Anwendung (Multi-Document Interface)
**Build:** Maven, Java 17
**Package:** `de.de.cavdar.gui`
**Ziel:** Template-Projekt das Funktionalitaeten aus StandardMDIGUI und ITSQ-Test vereint

## Quellprojekte

| Projekt | Beitrag |
|---------|---------|
| **StandardMDIGUI** | MDI-Framework, Views, Design-Pattern, Docker-Setup |
| **ITSQ-Test** | Maven-Artefakt-Integration, Assembly-Konzept, Dokumentation |

## Erstellungsdatum

**03.01.2026** - Initiale Erstellung durch Zusammenfuehrung der Quellprojekte

## Letzte Aenderungen

**03.01.2026** - UI-Refactoring und Bugfixes:
- **Dual-Toolbar Layout**: Config-Toolbar (Einstellungen) + View-Toolbar (View-Buttons)
- **Kein linkes Split-Panel mehr**: Vereinfachtes Layout
- **ItsqTreeView**: Neue View zum Browsen des eingebetteten ITSQ-Verzeichnisses
- **Config-Filter**: Nur `*-config.properties` Dateien werden geladen
- **isReloading-Flag**: Verhindert unbeabsichtigtes Speichern beim Config-Wechsel
- **BaseView.config**: AppConfig-Feld in BaseView fuer alle Subklassen verfuegbar

## Projektstruktur

```
TemplateGUI/
├── pom.xml                     # Maven Build mit Artefakt-Integration
├── README.md                   # Projektueberblick
├── docker/
│   ├── docker-compose.yml      # PostgreSQL + App Services
│   ├── Dockerfile              # Multi-Stage Build
│   └── init-db.sql             # DB-Initialisierung
├── delivery/
│   ├── config.properties       # Anwendungskonfiguration
│   ├── log4j.properties        # Logging-Konfiguration
│   └── startGUI.cmd            # Windows Startskript
├── docs/
│   ├── CLAUDE_CONTEXT.md       # Dieses Dokument
│   ├── DOCKER_GUIDE.md         # Docker-Anleitung
│   ├── Maven-Artefakt-Integration.md
│   └── gui.md                  # GUI-Architektur
└── src/
    ├── assembly/
    │   └── distribution.xml    # Assembly Deskriptor
    └── main/
        ├── java/de/de.cavdar/gui/
        │   ├── design/         # GUI Panels (Layout)
        │   ├── view/           # Views (Logik)
        │   ├── model/          # Datenmodelle
        │   ├── util/           # Utilities
        │   └── exception/      # Exceptions
        └── resources/
            └── icons/          # 38 PNG-Icons
```

## Package-Struktur

```
de.de.cavdar.gui/
├── design/                     # GUI-Panels (fuer GUI-Designer)
│   ├── BaseViewPanel.java      # Abstract Basis fuer View-Panels
│   ├── MainFrame.java          # Hauptfenster mit MDI
│   ├── EmbeddablePanel.java    # Abstract fuer einbettbare Panels
│   ├── SettingsPanel.java      # Einstellungen (links oben)
│   ├── TreePanel.java          # Tree-Panel (links unten)
│   ├── DesktopPanel.java       # MDI-Desktop (rechts)
│   ├── DatabaseViewPanel.java  # DB-View mit SplitPane
│   ├── CustomerTreeViewPanel.java
│   ├── EditorPanel.java        # Editor-Wrapper
│   ├── InternalFrameEditor.java # JFormDesigner Editor
│   └── ...ViewPanel.java       # Weitere View-Panels
│
├── view/                       # View-Logik
│   ├── ViewInfo.java           # Interface fuer View-Metadaten
│   ├── BaseView.java           # Abstract, implementiert ViewInfo, hat config-Feld
│   ├── MainView.java           # Einstiegspunkt mit main()
│   ├── DatabaseView.java       # DB-Verbindungen, SQL-Ausfuehrung
│   ├── CustomerTreeView.java   # Checkbox-Tree fuer Kunden (JSON-Dateien)
│   ├── ItsqTreeView.java       # Tree-View fuer ITSQ-Verzeichnis (Assembly)
│   ├── EditorView.java         # Text-Editor
│   ├── SampleView.java
│   ├── ProzessView.java
│   ├── AnalyseView.java
│   └── TreeView.java
│
├── model/
│   ├── AppConfig.java          # Singleton Konfiguration
│   ├── ConfigEntry.java        # Record fuer Config-Eintraege
│   ├── ConnectionInfo.java     # DB-Verbindungsdaten
│   ├── TestCustomer.java       # Kunde mit Szenarien
│   ├── TestScenario.java       # Szenario mit Testfaellen
│   └── TestCrefo.java          # Testfall
│
├── util/
│   ├── ConnectionManager.java  # DB-Connection Management
│   ├── TestDataLoader.java     # JSON Laden/Speichern
│   ├── IconLoader.java         # PNG-Icons laden
│   ├── CheckboxTreeCellRenderer.java
│   └── CheckboxTreeCellEditor.java
│
└── exception/
    ├── ConfigurationException.java
    └── ViewException.java
```

## Design-View-Trennung Pattern

Jede View besteht aus zwei Klassen:

1. **Panel-Klasse** (`design/`): Nur GUI-Komponenten, kein Logik
2. **View-Klasse** (`view/`): Nur Logik und Event-Handler

```
BaseView (abstrakt)
   └── createPanel() → BaseViewPanel
   └── setupToolbarActions()
   └── setupListeners()

BaseViewPanel (abstrakt)
   └── initComponents()
   └── Toolbar, Content, StatusBar
```

## ViewInfo Interface

Jede View implementiert:

```java
public interface ViewInfo {
    String getMenuLabel();           // Menu-Text
    String getToolbarLabel();        // Toolbar-Text (null = kein Button)
    Icon getIcon();                  // Icon fuer Menu/Toolbar
    KeyStroke getKeyboardShortcut(); // Tastaturkuerzel
    String getMenuGroup();           // Submenu-Gruppe
}
```

## Registrierte Views

| View | Shortcut | Menu-Gruppe | Icon |
|------|----------|-------------|------|
| SampleView | Ctrl+1 | Analyse | client.png |
| ProzessView | Ctrl+2 | Verwaltung | gear_run.png |
| AnalyseView | Ctrl+3 | Analyse | table_sql.png |
| TreeView | Ctrl+4 | Navigation | folder_view.png |
| CustomerTreeView | Ctrl+5 | Verwaltung | folder_cubes.png |
| EditorView | Ctrl+E | Views | folder_edit.png |
| ItsqTreeView | Ctrl+I | Navigation | folder_cubes.png |

## Maven-Artefakt-Integration

Das Projekt integriert externe Artefakte (z.B. `testfaelle`) via maven-dependency-plugin:

```xml
<!-- Properties -->
<testfaelle.groupId>testfaelle</testfaelle.groupId>
<testfaelle.artifactId>itsq</testfaelle.artifactId>
<testfaelle.classifier>distribution-${testfaelle.branch}</testfaelle.classifier>
<testfaelle.type>zip</testfaelle.type>

<!-- Profiles -->
<profile id="snapshot"> <!-- Standard -->
    <testfaelle.version>1.1.0-SNAPSHOT</testfaelle.version>
    <testfaelle.branch>TEST-01</testfaelle.branch>
</profile>
<profile id="release">
    <testfaelle.version>1.1.0</testfaelle.version>
    <testfaelle.branch>TEST-01</testfaelle.branch>
</profile>
```

**Build-Kommandos:**
```bash
mvn package              # SNAPSHOT
mvn package -Prelease    # Release
```

## Distribution-Struktur

Nach `mvn package`:

```
target/TemplateGUI-1.0.0-SNAPSHOT-distribution.zip
└── TemplateGUI-1.0.0-SNAPSHOT/
    ├── config.properties
    ├── log4j.properties
    ├── startGUI.cmd
    ├── lib/                # Alle JARs
    │   ├── TemplateGUI-1.0.0-SNAPSHOT.jar
    │   ├── postgresql-42.7.4.jar
    │   ├── jackson-*.jar
    │   └── slf4j-*.jar
    └── ITSQ/               # Testfaelle-Artefakt
        ├── ARCHIV-BESTAND/
        └── REF-EXPORTS/
```

## Docker-Setup

```yaml
# docker-compose.yml
services:
  postgres:
    image: postgres:15-alpine
    ports: ["5432:5432"]
    environment:
      POSTGRES_DB: templatedb
      POSTGRES_USER: template
      POSTGRES_PASSWORD: template123
```

**Starten:**
```bash
cd docker
docker-compose up -d postgres
```

## Konfiguration (config.properties)

| Gruppe | Keys |
|--------|------|
| WINDOW | LAST_WINDOW_*, LAST_*_SPLIT_DIVIDER |
| LATEST | LAST_DB_CONNECTION, LAST_TEST_*, CUSTOMER_FILE_HISTORY |
| FLAGS | DUMP_IN_REST_CLIENT, SFTP_UPLOAD_ACTIVE, ... |
| DATABASE | DB_CONNECTIONS, SQL_HISTORY, SQL_FAVORITES |
| TESTS | TEST-SOURCES, TEST-TYPES, ITSQ_REVISIONS |

## Abhaengigkeiten

| Dependency | Version | Zweck |
|------------|---------|-------|
| log4j | 1.2.12 | Logging |
| slf4j-api | 2.0.9 | Logging Facade |
| slf4j-reload4j | 2.0.6 | SLF4J Binding |
| postgresql | 42.7.4 | JDBC Driver |
| jackson-databind | 2.17.0 | JSON Parsing |

## Lokale Umgebung

- **Java:** 17+
- **Maven:** 3.6+
- **Docker:** Optional fuer PostgreSQL

## Prompt zum Fortsetzen

```
Ich arbeite am Java-Projekt TemplateGUI unter E:\Projekte\ClaudeCode\TemplateGUI.
Bitte lies die Datei docs/CLAUDE_CONTEXT.md fuer den Kontext.
```

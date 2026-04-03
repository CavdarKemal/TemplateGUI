# TemplateGUI – Tutorial

> **Zielgruppe:** Erfahrene Java-Entwickler ohne Vorkenntnisse in Swing MDI  
> **Projekt:** [TemplateGUI auf GitHub](https://github.com/CavdarKemal/TemplateGUI)

---

## Inhaltsverzeichnis

1. [Was ist dieses Projekt?](#1-was-ist-dieses-projekt)
2. [Technologie-Stack](#2-technologie-stack)
3. [Projektstruktur](#3-projektstruktur)
4. [Voraussetzungen und Setup](#4-voraussetzungen-und-setup)
5. [Anwendung starten](#5-anwendung-starten)
6. [Das MDI-Hauptfenster](#6-das-mdi-hauptfenster)
7. [Das Kern-Muster: Design-View-Trennung](#7-das-kern-muster-design-view-trennung)
8. [Die registrierten Views](#8-die-registrierten-views)
9. [Konfigurationsverwaltung: AppConfig](#9-konfigurationsverwaltung-appconfig)
10. [Datenbankintegration](#10-datenbankintegration)
11. [Eigene View erstellen](#11-eigene-view-erstellen)
12. [Maven-Artefakt-Integration](#12-maven-artefakt-integration)
13. [Docker-Setup](#13-docker-setup)
14. [Beziehung zu StandardMDIGUI und ITSQ-Explorer](#14-beziehung-zu-standardmdigui-und-itsq-explorer)
15. [Nächste Schritte](#15-nächste-schritte)

---

## 1. Was ist dieses Projekt?

**TemplateGUI** ist eine vollständige **Java Swing MDI-Anwendung** — eine Kombination aus zwei Quellprojekten:

- **StandardMDIGUI**: MDI-Infrastruktur, Design-View-Muster, Docker-Integration
- **ITSQ-Test**: Maven-Artefakt-Integration, Testdatenstrukturen, Delivery-Konzept

### Positionierung

```
StandardMDIGUI (Framework, Java 23)
        ↓  Muster und Infrastruktur übernommen
TemplateGUI   (vollständiges Template, Java 17)
        ↓  spezialisiert, um Migrationstools erweitert
ITSQ-Explorer (Fachanwendung, Java 17)
```

TemplateGUI ist der **mittlere Schritt**: Es enthält mehr Funktionalität als StandardMDIGUI (konkrete Views wie ProzessView und ItsqExplorerView), ist aber noch kein fertiges Fachprodukt.

### Was das Projekt bietet

| Funktion | Beschreibung |
|----------|-------------|
| **MDI-Desktop** | Mehrere interne Fenster in einem Hauptfenster |
| **Doppelte Toolbar** | Config-Toolbar oben, View-Toolbar darunter |
| **DatabaseView** | Generischer SQL-Client für JDBC-Datenquellen |
| **ItsqTreeView** | Navigations-Baum für ITSQ-Verzeichnisstrukturen |
| **ItsqExplorerView** | Erweiterter ITSQ-Manager mit Filter und Editoren |
| **ProzessView** | Vorlage für Prozess-/Workflow-Ansichten |
| **AppConfig** | Persistente, typsichere Konfigurationsverwaltung |
| **Maven-Artefakte** | Download von Testdaten aus Maven-Repository |

---

## 2. Technologie-Stack

| Komponente | Version | Rolle |
|------------|---------|-------|
| **Java** | 17 | Laufzeitumgebung |
| **Swing** | Standard-Bibliothek | GUI-Framework |
| **Maven** | 3.6+ | Build und Artefakt-Download |
| **PostgreSQL** | via JDBC | Datenbankanbindung |
| **RSyntaxTextArea** | 3.5.2 | XML/SQL-Syntaxhervorhebung |
| **Jackson** | 2.17 | JSON-Verarbeitung |
| **Log4j** | 1.2.12 | Logging |
| **JUnit 5** | 5.10.2 | Tests |
| **AssertJ Swing** | 3.17 | GUI-Tests |
| **Docker** | aktuell | PostgreSQL-Container |

---

## 3. Projektstruktur

```
TemplateGUI/
├── pom.xml
├── README.md
├── delivery/                             # Distributions-Dateien
│   ├── ene-config.properties             # Standard-Konfiguration
│   ├── abe-config.properties
│   ├── gee-config.properties
│   ├── log4j.properties
│   └── startGUI.cmd                      # Windows-Starter
├── docker/
│   ├── docker-compose.yml                # PostgreSQL-Container
│   └── init-db.sql                       # DB-Initialisierung
├── docs/
│   ├── gui.md                            # GUI-Architektur-Doku
│   ├── CLAUDE_CONTEXT.md                 # Entwicklungskontext
│   ├── DOCKER_GUIDE.md                   # Docker-Anleitung
│   ├── ENVIRONMENT_LOCKING.md            # Umgebungs-Lock-System
│   └── Maven-Artefakt-Integration.md     # Artefakt-Integration
└── src/main/java/de/cavdar/gui/
    ├── Main.java                          # Einstiegspunkt
    ├── design/                            # GUI-Schicht (Layout only)
    │   ├── base/                          # BaseViewPanel, MainFrame, DesktopPanel
    │   ├── db/                            # DatabaseViewPanel
    │   ├── json/                          # TreeViewPanel
    │   ├── prozess/                       # ProzessViewPanel
    │   └── itsq/                          # ItsqExplorer-Panels (JFormDesigner)
    ├── view/                              # Logik-Schicht
    │   ├── base/                          # BaseView, ViewInfo
    │   ├── db/                            # DatabaseView
    │   ├── json/                          # ItsqTreeView
    │   ├── itsq/                          # ItsqExplorerView
    │   └── prozess/                       # ProzessView
    ├── model/base/                        # AppConfig, ConnectionInfo
    ├── util/                              # ConnectionManager, IconLoader
    ├── exception/                         # Exceptions
    └── itsq/                              # ITSQ-Fachlogik
        ├── design/                        # JFormDesigner-Panels
        ├── model/                         # ItsqItem-Hierarchie
        ├── tree/                          # Baum-Knoten
        └── view/                          # ITSQ-View-Implementierungen
```

---

## 4. Voraussetzungen und Setup

### Systemvoraussetzungen

- JDK 17 oder neuer
- Maven 3.6+
- Internetzugang (Maven-Artefakt-Download beim ersten Build)
- Docker Desktop (optional, für PostgreSQL)

### Maven-Artefakt-Setup

Das Projekt lädt Testdaten als Maven-Artefakt herunter. Die Koordinaten stehen in `pom.xml`:

```xml
<properties>
    <testfaelle.groupId>testfaelle</testfaelle.groupId>
    <testfaelle.artifactId>itsq</testfaelle.artifactId>
    <testfaelle.version>1.1.0-SNAPSHOT</testfaelle.version>
</properties>
```

### Bauen

```cmd
cd E:\Projekte\ClaudeCode\TemplateGUI
ci.cmd 17
```

Der Build-Prozess:
1. Maven-Abhängigkeiten herunterladen
2. ITSQ-Testdaten-Artefakt herunterladen und entpacken
3. Kompilieren
4. JAR + Distribution-ZIP erstellen

---

## 5. Anwendung starten

### Aus der IDE

Hauptklasse: `de.cavdar.gui.Main`

VM-Optionen empfohlen:
```
-Dfile.encoding=UTF-8 -Xms512m -Xmx2g
```

### Aus der Distribution

```cmd
mvn clean package
cd target\TemplateGUI-1.0.0-SNAPSHOT
startGUI.cmd
```

### Mit eigener Konfiguration

```cmd
java -Dconfig.file=C:\MeinProjekt\meine-config.properties -cp "lib/*" de.cavdar.gui.Main
```

### Main.java — Einstiegspunkt und View-Registrierung

```java
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();

            // Views registrieren — Reihenfolge bestimmt Toolbar-Position:
            frame.registerView(ProzessView::new);
            frame.registerView(ItsqTreeView::new);
            frame.registerView(ItsqExplorerView::new);

            frame.setVisible(true);
        });
    }
}
```

---

## 6. Das MDI-Hauptfenster

### Zwei-Toolbar-Architektur

```
┌──────────────────────────────────────────────────────────────┐
│  Menüleiste: Datei │ Ansicht │ Fenster │ Hilfe               │
├──────────────────────────────────────────────────────────────┤
│  CONFIG-TOOLBAR:  [Config ▼] [DB-Verb. ▼] [Testquelle ▼]    │
├──────────────────────────────────────────────────────────────┤
│  VIEW-TOOLBAR:    [Prozess] [ITSQ-Baum] [ITSQ-Explorer]      │
├──────────────┬───────────────────────────────────────────────┤
│              │                                               │
│  LINKES      │          DESKTOP                              │
│  PANEL       │   ┌──────────────┐ ┌──────────────┐         │
│              │   │  View 1      │ │  View 2      │         │
│  Einstellg.  │   │              │ │              │         │
│  Baum-Nav.   │   └──────────────┘ └──────────────┘         │
│              │                                               │
└──────────────┴───────────────────────────────────────────────┘
```

**Config-Toolbar (oben):** Globale Einstellungen — welche Konfigurationsdatei, welche DB-Verbindung, welche Testquelle aktiv ist.

**View-Toolbar (darunter):** Eine Schaltfläche pro registrierter View. Klick öffnet die View als internes Fenster.

### Fensteranordnung

Im Menü `Fenster`:

| Aktion | Wirkung |
|--------|---------|
| Nebeneinander | Alle Views horizontal anordnen |
| Übereinander | Alle Views vertikal anordnen |
| Kaskade | Views gestaffelt überlagern |
| Alle schließen | Alle internen Fenster schließen |

---

## 7. Das Kern-Muster: Design-View-Trennung

Jede View besteht aus exakt zwei Klassen:

| Klasse | Paket | Basisklasse | Zweck |
|--------|-------|-------------|-------|
| `*Panel` | `design/` | `BaseViewPanel` | GUI-Aufbau, Layout — kein Code |
| `*View` | `view/` | `BaseView` | Logik, Ereignisse — kein Layout |

### Panel (design/)

```java
public class MeinViewPanel extends BaseViewPanel {

    private JButton btnStarten;
    private JTextArea taLog;

    @Override
    protected void initComponents() {
        btnStarten = new JButton("Starten");
        taLog      = new JTextArea(15, 50);
        taLog.setEditable(false);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(btnStarten, BorderLayout.NORTH);
        getContentPanel().add(new JScrollPane(taLog), BorderLayout.CENTER);
    }

    // Nur Getter — kein Listener, kein Geschäftscode:
    public JButton getBtnStarten() { return btnStarten; }
    public JTextArea getTaLog()    { return taLog;      }
}
```

### View (view/)

```java
public class MeinView extends BaseView {

    private MeinViewPanel panel;

    public MeinView() { super("Meine View"); }

    @Override
    protected BaseViewPanel createPanel() {
        panel = new MeinViewPanel();
        return panel;
    }

    @Override
    protected void setupToolbarActions() {
        panel.getBtnStarten().addActionListener(e -> starteAktion());
    }

    // ViewInfo-Implementierung:
    @Override public String getMenuLabel() { return "Meine View"; }
    @Override public String getMenuGroup() { return "Werkzeuge"; }
    @Override public KeyStroke getKeyboardShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK);
    }

    // Geschäftslogik:
    private void starteAktion() {
        executeTask(() -> {
            String ergebnis = langsameOperation();
            SwingUtilities.invokeLater(() ->
                panel.getTaLog().append(ergebnis + "\n")
            );
        });
    }
}
```

### Template Method in BaseView

```
BaseView.init()
    └── createPanel()         ← überschreiben (Panel erstellen)
    └── setupToolbarActions() ← überschreiben (Listener einrichten)
    └── setupListeners()      ← optional überschreiben

BaseView.executeTask(Runnable)
    └── SwingWorker.execute() (Hintergrundthread)
        └── automatisch: Fortschrittsbalken an/aus
        └── automatisch: Abbrechen-Button
        └── automatisch: Fehlerdialog bei Exception
```

---

## 8. Die registrierten Views

### ProzessView (Strg+2)

Vorlage für Prozess- und Workflow-Ansichten. Enthält das grundlegende Layout mit Steuerelementen auf der linken Seite und einem Inhaltbereich rechts.

Dient als Ausgangspunkt für eigene Prozess-Monitor-Ansichten.

### ItsqTreeView (Strg+I)

Baumnavigator für ITSQ-Verzeichnisstrukturen. Zeigt:

- `ARCHIV-BESTAND/` — Archivierte Testdaten
- `REF-EXPORTS/` — Referenz-Exporte

Jeder Knoten kann aufgeklappt werden. Ein Klick auf eine Datei öffnet den Inhalt im Detail-Panel.

### ItsqExplorerView (Strg+J)

Erweiterter ITSQ-Manager mit:

- **TestSet-Auswahl:** ComboBox mit zuletzt verwendeten Verzeichnissen
- **Mehrfachfilter:** Textsuche, Quelle (ARCHIV-BESTAND / REF-EXPORTS), Phase (PHASE-1 / PHASE-2), Nur-Aktive-Toggle
- **Hierarchischer Baum:** Wurzel → Kunden → Szenarien → Dateien
- **Kontextsensitive Detailansicht:** Verschiedene Panels je nach Knotentyp (CardLayout)
- **Dual-Mode-Editor:**
  - XML-Modus: RSyntaxTextArea mit Syntaxhervorhebung
  - Properties-Modus: Tabellen-Editor mit CRUD-Schaltflächen

#### Filter-Kombination

```
Textfilter (Groß-/Kleinschreibung ignoriert)
  + Quelle (ARCHIV-BESTAND oder REF-EXPORTS)
  + Phase (PHASE-1 oder PHASE-2)
  + Nur aktive Einträge
──────────────────────────────────────────
→ Gefilterter Baum mit passenden Knoten
```

### DatabaseView

Generischer SQL-Client:

1. Verbindung speichern/laden/löschen
2. Tabellen-Browser (linker Baum mit Lazy Loading)
3. SQL-Editor mit Ausführung (Strg+Enter)
4. Ergebnis-Tabelle
5. SQL-Verlauf und Favoriten
6. CSV-Export

---

## 9. Konfigurationsverwaltung: AppConfig

### Grundprinzip

`AppConfig` ist ein **Singleton** für persistente Konfiguration. Alle Einstellungen (Fensterposition, zuletzt verwendete Verzeichnisse, DB-Verbindungen) werden automatisch in einer `.properties`-Datei gespeichert.

### Verwendung

```java
AppConfig cfg = AppConfig.getInstance();

// Lesen
String basePath = cfg.getProperty("TEST-BASE-PATH");
String[] sources = cfg.getArray("TEST-SOURCES");   // semikolon-getrennt
boolean admin   = cfg.getBool("ADMIN_FUNCS_ENABLED");
int hoehe       = cfg.getInt("LAST_WINDOW_HEIGHT", 800);

// Schreiben
cfg.setProperty("LAST_USED_DIR", "/mein/verzeichnis");
cfg.save();
```

### Eigenschaftsgruppen

```properties
# ─── WINDOW ───────────────────────────────────────────────
LAST_WINDOW_HEIGHT=825
LAST_WINDOW_WIDTH=1428
LAST_MAIN_SPLIT_DIVIDER=250
LAST_LEFT_SPLIT_DIVIDER=300

# ─── LATEST ───────────────────────────────────────────────
LAST_TESTSET_PATH=/pfad/zum/testset
LAST_DB_CONNECTION=MeineDB

# ─── FLAGS ────────────────────────────────────────────────
ADMIN_FUNCS_ENABLED=true

# ─── CUSTOMERS ────────────────────────────────────────────
AVAILABLE_CUSTOMERS=c01;c02;c03;c04

# ─── DATABASE ─────────────────────────────────────────────
DB_CONNECTIONS=MeineDB|org.postgresql.Driver|jdbc:postgresql://localhost:5432/db|user|pass

# ─── TESTS ────────────────────────────────────────────────
TEST-BASE-PATH=/pfad/zu/tests
TEST-SOURCES=ARCHIV-BESTAND;REF-EXPORTS
```

### Konfigurationsdatei zur Laufzeit wechseln

In der Config-Toolbar gibt es eine ComboBox für die aktive Konfigurationsdatei. Enthält das Projekt mehrere `*-config.properties`-Dateien, können diese hier gewählt werden.

---

## 10. Datenbankintegration

### ConnectionManager

```java
ConnectionManager cm = ConnectionManager.getInstance();

// Verbindung abrufen (aus AppConfig geladen)
Connection conn = cm.getConnection("MeineDB");

// SQL ausführen
PreparedStatement ps = conn.prepareStatement("SELECT * FROM kunden WHERE id = ?");
ps.setInt(1, 42);
ResultSet rs = ps.executeQuery();

// Aufräumen
rs.close();
ps.close();
cm.releaseConnection("MeineDB");
```

### Unterstützte Datenbanken

| Datenbank | JDBC-URL-Beispiel |
|-----------|------------------|
| PostgreSQL | `jdbc:postgresql://localhost:5432/meinedb` |
| MySQL | `jdbc:mysql://localhost:3306/meinedb` |
| Oracle | `jdbc:oracle:thin:@localhost:1521:orcl` |
| H2 | `jdbc:h2:file:./data/meinedb` |
| SQLite | `jdbc:sqlite:meinedb.db` |

### Verbindung in AppConfig speichern

Verbindungen werden in einem kompakten Format in `config.properties` gespeichert:

```properties
DB_CONNECTIONS=Name|Treiber|URL|Benutzer|Base64-Passwort
```

Mehrere Verbindungen mit `;;` getrennt.

---

## 11. Eigene View erstellen

### Kurzanleitung (3 Schritte)

**Schritt 1: Panel erstellen** (`design/MeinViewPanel.java`)

```java
public class MeinViewPanel extends BaseViewPanel {
    private JButton btnAktion;

    @Override
    protected void initComponents() {
        btnAktion = new JButton("Aktion");
        getContentPanel().add(btnAktion);
    }

    public JButton getBtnAktion() { return btnAktion; }
}
```

**Schritt 2: View erstellen** (`view/MeinView.java`)

```java
public class MeinView extends BaseView {
    private MeinViewPanel panel;

    public MeinView() { super("Meine View"); }

    @Override
    protected BaseViewPanel createPanel() {
        panel = new MeinViewPanel();
        return panel;
    }

    @Override
    protected void setupToolbarActions() {
        panel.getBtnAktion().addActionListener(e -> executeTask(() -> {
            String ergebnis = meineArbeit();
            SwingUtilities.invokeLater(() -> System.out.println(ergebnis));
        }));
    }

    @Override public String getMenuLabel() { return "Meine View"; }
}
```

**Schritt 3: In Main.java registrieren**

```java
frame.registerView(MeinView::new);
```

Fertig — View erscheint automatisch im Menü und der Toolbar.

---

## 12. Maven-Artefakt-Integration

### Testdaten aus Repository laden

Das Projekt lädt Testdaten als ZIP-Artefakt aus einem Maven-Repository:

```xml
<!-- In pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>unpack-testfaelle</id>
            <phase>generate-resources</phase>
            <goals><goal>unpack</goal></goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>${testfaelle.groupId}</groupId>
                        <artifactId>${testfaelle.artifactId}</artifactId>
                        <version>${testfaelle.version}</version>
                        <type>zip</type>
                        <outputDirectory>${project.build.directory}/testfaelle</outputDirectory>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Build-Profile

```cmd
REM Standard (SNAPSHOT)
ci.cmd 17

REM Release
mvn clean package -Prelease

REM Mit spezifischem Branch
mvn clean package -Dtestfaelle.branch=feature-xyz
```

### Distribution-Assembly

```xml
<!-- src/assembly/distribution.xml -->
<assembly>
    <id>distribution</id>
    <formats><format>zip</format></formats>
    <fileSets>
        <fileSet>
            <directory>${project.build.directory}</directory>
            <includes><include>*.jar</include></includes>
        </fileSet>
        <fileSet>
            <directory>delivery</directory>
            <!-- config.properties, log4j.properties, startGUI.cmd -->
        </fileSet>
    </fileSets>
</assembly>
```

---

## 13. Docker-Setup

### PostgreSQL für Entwicklung

```cmd
cd docker
docker-compose up postgres -d
```

Verbindungsdetails:

| Parameter | Wert |
|-----------|------|
| Host | `localhost` |
| Port | `5432` |
| Datenbank | `templatedb` |
| Benutzer | `template` |
| Passwort | `template` |

### Datenbank-Schema

```sql
-- docker/init-db.sql wird beim ersten Start automatisch ausgeführt
CREATE TABLE IF NOT EXISTS beispiel (
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    datum TIMESTAMP DEFAULT NOW()
);
```

### Vollständige Anleitung

Siehe [`docs/DOCKER_GUIDE.md`](docs/DOCKER_GUIDE.md) für X11-Setup (Windows/Linux) und vollständige Compose-Konfiguration.

---

## 14. Beziehung zu StandardMDIGUI und ITSQ-Explorer

### Von StandardMDIGUI übernommen

| Komponente | Beschreibung |
|------------|-------------|
| `BaseView` / `BaseViewPanel` | MDI-Framework mit Template-Method-Muster |
| `MainFrame` | Doppelte Toolbar, View-Registrierung |
| `DesktopPanel` | MDI-Fensterverwaltung |
| `ViewInfo` | Metadaten-Interface (Menü, Toolbar, Kürzel) |
| `AppConfig` | Konfigurationsmanager |
| `ConnectionManager` | DB-Verbindungsverwaltung |
| Docker-Konfiguration | PostgreSQL + App in Containern |

### Hinzugefügt gegenüber StandardMDIGUI

| Komponente | Beschreibung |
|------------|-------------|
| `ItsqTreeView` | ITSQ-Verzeichnisbaum |
| `ItsqExplorerView` | Erweiterter ITSQ-Manager |
| `ProzessView` | Prozess-Ansicht |
| ITSQ-Datenmodell | `ItsqItem`-Hierarchie, Baum-Knoten |
| Maven-Artefakt-Integration | `dependency-plugin` + Assembly |
| Umgebungs-Lock-System | `EnvironmentLockManager` |

### Weitergegeben an ITSQ-Explorer

ITSQ-Explorer basiert auf der TemplateGUI-Architektur und erweitert sie um:
- Migrations-Tool (OLD→NEW Strukturwandel)
- Erweiterte Properties-Editoren
- Konsistenz-Tests

---

## 15. Nächste Schritte

### Eigene ITSQ-Testdaten verwenden

```java
AppConfig cfg = AppConfig.getInstance();
cfg.setProperty("TEST-BASE-PATH", "C:/MeineTestDaten");
cfg.save();
```

Dann in der ItsqTreeView das Verzeichnis auswählen — die Struktur wird automatisch eingelesen.

### Umgebungs-Lock verstehen

Wenn zwei Instanzen von TemplateGUI gleichzeitig dieselbe Umgebung öffnen würden, könnten Konfigurationen korrumpieren. Der `EnvironmentLockManager` verhindert das:

```
Erste Instanz startet → Lock auf Port 47100 → OK
Zweite Instanz startet → Port 47100 belegt → automatisch auf freie Umgebung gewechselt
```

Details: [`docs/ENVIRONMENT_LOCKING.md`](docs/ENVIRONMENT_LOCKING.md)

### Weiterführende Dokumentation

| Dokument | Inhalt |
|----------|--------|
| [`docs/gui.md`](docs/gui.md) | Vollständige GUI-Architektur mit Klassendiagrammen |
| [`docs/DOCKER_GUIDE.md`](docs/DOCKER_GUIDE.md) | Docker-Setup für Windows und Linux |
| [`docs/Maven-Artefakt-Integration.md`](docs/Maven-Artefakt-Integration.md) | Artefakt-Download und Profile |

---

*Erstellt: April 2026*

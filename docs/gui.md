# TemplateGUI Dokumentation

## Übersicht

Die TemplateGUI ist eine Java Swing MDI-Anwendung (Multiple Document Interface) mit strikter **Design-View-Trennung**. Das Architekturkonzept basiert auf dem StandardMDIGUI-Framework.

## Architektur-Prinzipien

### Design-View-Trennung

```
┌─────────────────────────────────────────────────────────────────┐
│                         ViewInfo                                │
│                        (Interface)                              │
└──────────────────────────┬──────────────────────────────────────┘
                           │ implements
┌──────────────────────────┴──────────────────────────────────────┐
│                         BaseView                                │
│                   (abstrakte Klasse)                            │
│              extends JInternalFrame                             │
│                                                                 │
│  Template Methods:                                              │
│  - createPanel()         → Panel erstellen                      │
│  - setupToolbarActions() → Button-Actions binden                │
│  - setupListeners()      → Weitere Listener (optional)          │
│                                                                 │
│  Features:                                                      │
│  - executeTask(Runnable) → Async mit SwingWorker                │
│  - Cancel-Mechanismus                                           │
│  - Progress-Anzeige                                             │
└──────────────────────────┬──────────────────────────────────────┘
                           │ verwendet
┌──────────────────────────┴──────────────────────────────────────┐
│                      BaseViewPanel                              │
│                   (abstrakte Klasse)                            │
│                                                                 │
│  Wrapper für JFormDesigner-generierte Panels                    │
│                                                                 │
│  Abstrakte Methoden:                                            │
│  - getViewToolbar()  → JToolBar                                 │
│  - getProgressBar()  → JProgressBar                             │
│  - getCancelButton() → JButton                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Kernprinzip: Komposition statt Vererbung

- **Design-Klassen** (JFormDesigner): Nur GUI-Komponenten, keine Logik
- **Panel-Wrapper**: Kapseln JFormDesigner-Panels, bieten einheitliche Schnittstelle
- **View-Klassen**: Nur Business-Logik und Event-Handler

## Package-Struktur

```
de.template.gui
├── design/                          # GUI-Komponenten
│   ├── BaseViewPanel.java           # Basis-Panel mit Toolbar/Status
│   ├── MainFrame.java               # Hauptfenster mit Dual-Toolbar Layout
│   ├── DesktopPanel.java            # MDI-Desktop fuer Views
│   ├── DatabaseViewPanel.java       # Panel fuer Datenbank-View
│   ├── CustomerTreeViewPanel.java   # Panel fuer Kunden-Tree
│   ├── ItsqTreeViewPanel.java       # Panel fuer ITSQ-Tree
│   ├── EditorPanel.java             # Wrapper fuer Editor
│   ├── InternalFrameEditor.java     # JFormDesigner-generiert
│   └── ...ViewPanel.java            # Weitere View-Panels
│
├── model/                           # Datenmodelle
│   ├── AppConfig.java               # Singleton Konfigurationsverwaltung
│   ├── ConfigEntry.java             # Typ-sicherer Konfigurations-Eintrag
│   ├── ConnectionInfo.java          # Datenbank-Verbindungsinfo
│   ├── TestCustomer.java            # Kunde mit Szenarien
│   ├── TestScenario.java            # Szenario mit Testfaellen
│   └── TestCrefo.java               # Testfall
│
├── util/                            # Utility-Klassen
│   ├── ConnectionManager.java       # Verbindungsverwaltung
│   ├── TestDataLoader.java          # JSON Laden/Speichern
│   └── IconLoader.java              # Icon-Laden aus Resources
│
├── exception/                       # Exceptions
│   └── ConfigurationException.java  # Konfigurations-Fehler
│
└── view/                            # Business-Logik
    ├── ViewInfo.java                # Interface fuer View-Metadaten
    ├── BaseView.java                # Abstrakte View-Basisklasse (mit config-Feld)
    ├── MainView.java                # Einstiegspunkt mit main()
    ├── DatabaseView.java            # Datenbank-View mit SQL-Editor
    ├── CustomerTreeView.java        # Checkbox-Tree fuer Kunden (JSON)
    ├── ItsqTreeView.java            # Tree fuer ITSQ-Verzeichnis
    ├── EditorView.java              # Text-Editor
    └── ...View.java                 # Weitere Views
```

## Klassen-Dokumentation

### ViewInfo (Interface)

Definiert Metadaten für automatische Menü- und Toolbar-Generierung.

```java
public interface ViewInfo {
    String getMenuLabel();                    // Menü-Text (erforderlich)
    default String getToolbarLabel();         // Toolbar-Text (optional)
    default Icon getIcon();                   // Icon (optional)
    default KeyStroke getKeyboardShortcut();  // Tastenkürzel (optional)
    default String getMenuGroup();            // Menü-Gruppe (optional)
    default String getToolbarTooltip();       // Tooltip (optional)
}
```

### BaseViewPanel (Abstrakt)

Abstrakte Basisklasse für alle GUI-Panels. Dient als Wrapper für JFormDesigner-generierte Komponenten.

```java
public abstract class BaseViewPanel extends JPanel {
    // Abstrakte Methoden - von Subklassen zu implementieren
    public abstract JToolBar getViewToolbar();
    public abstract JProgressBar getProgressBar();
    public abstract JButton getCancelButton();

    // Progress-Steuerung
    public void setProgressVisible(boolean visible, boolean indeterminate);
}
```

### BaseView (Abstrakt)

Abstrakte Basisklasse für alle Views. Implementiert das Template Method Pattern.

```java
public abstract class BaseView extends JInternalFrame implements ViewInfo {
    protected BaseViewPanel panel;
    protected SwingWorker<Void, Void> currentWorker;

    // Template Methods - von Subklassen zu implementieren
    protected abstract BaseViewPanel createPanel();
    protected abstract void setupToolbarActions();
    protected void setupListeners() { }  // Optional

    // Async-Task-Ausführung mit Progress
    protected void executeTask(Runnable taskLogic);
}
```

### MainView

Hauptanwendungsklasse mit MDI-Desktop-Management.

**Features:**
- JDesktopPane für MDI-Fenster
- View-Registrierung mit `registerView(Supplier<BaseView>)`
- Menü-Actions (Open, Exit, Copy, Paste, Tile)
- Toolbar-Actions (New Editor, New TreeView)

## Neue View erstellen

### Schritt 1: JFormDesigner-Panel erstellen

Erstellen Sie ein neues Panel in JFormDesigner (z.B. `InternalFrameMyView.java`).

### Schritt 2: Panel-Wrapper erstellen

```java
package de.template.gui.design;

public class MyViewPanel extends BaseViewPanel {
    private InternalFrameMyView myView;

    @Override
    protected void initComponents() {
        myView = new InternalFrameMyView();
        add(myView, BorderLayout.CENTER);
    }

    @Override
    public JToolBar getViewToolbar() {
        return myView.getToolBarMain();
    }

    @Override
    public JProgressBar getProgressBar() {
        return myView.getProgressBar();  // oder null
    }

    @Override
    public JButton getCancelButton() {
        return myView.getCancelButton();  // oder null
    }

    // Getter für spezifische Komponenten
    public JButton getMyButton() {
        return myView.getMyButton();
    }
}
```

### Schritt 3: View-Klasse erstellen

```java
package de.template.gui.view;

public class MyViewView extends BaseView {
    private MyViewPanel myPanel;

    public MyViewView() {
        super("Meine View");
    }

    @Override
    protected BaseViewPanel createPanel() {
        myPanel = new MyViewPanel();
        return myPanel;
    }

    @Override
    protected void setupToolbarActions() {
        myPanel.getMyButton().addActionListener(e -> doSomething());
    }

    @Override
    protected void setupListeners() {
        // Weitere Listener hier
    }

    // ViewInfo Implementation
    @Override
    public String getMenuLabel() {
        return "Meine View";
    }

    @Override
    public KeyStroke getKeyboardShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK);
    }

    // Business-Logik
    private void doSomething() {
        executeTask(() -> {
            // Hintergrund-Arbeit hier
            // Progress wird automatisch angezeigt
        });
    }
}
```

### Schritt 4: View registrieren (optional)

In `MainView.registerDefaultViews()`:

```java
private void registerDefaultViews() {
    registerView(TreeViewView::new);
    registerView(EditorView::new);
    registerView(MyViewView::new);  // Neue View
}
```

## Async-Task-Handling

Die `executeTask()`-Methode führt Hintergrundaufgaben mit SwingWorker aus:

```java
private void processData() {
    executeTask(() -> {
        // Dies läuft im Hintergrund-Thread
        for (int i = 0; i < 100; i++) {
            // Arbeit...

            // UI-Updates müssen auf EDT erfolgen:
            SwingUtilities.invokeLater(() -> {
                updateUI();
            });
        }
    });
}
```

**Features:**
- Automatische Progress-Bar-Anzeige (indeterminate)
- Cancel-Button wird sichtbar
- Bei Abbruch: Meldung "Aktion abgebrochen"

## Menü-Struktur

| Menü | Item | Shortcut | Funktion |
|------|------|----------|----------|
| File | Open | Ctrl+O | Datei in neuem Editor öffnen |
| File | Exit | Ctrl+Q | Anwendung beenden |
| Edit | Copy | Ctrl+C | Text aus aktivem Editor kopieren |
| Edit | Paste | Ctrl+V | Text in aktiven Editor einfügen |
| Window | Horizontal | - | Fenster horizontal anordnen |
| Window | Vertical | - | Fenster vertikal anordnen |

## Toolbar

| Button | Funktion |
|--------|----------|
| New Editor | Neues Editor-Fenster erstellen |
| New TreeView | Neues TreeView-Fenster erstellen |

## Abhängigkeiten

```xml
<dependencies>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-reload4j</artifactId>
        <version>2.0.6</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.12</version>
    </dependency>
</dependencies>
```

## Anwendung starten

### Aus IDE (IntelliJ)

Main-Klasse: `de.template.gui.view.MainView`

### Aus Distribution

```cmd
cd target\TemplateGUI-1.0.0-SNAPSHOT
startGUI.cmd
```

Mit Debug-Modus:
```cmd
startGUI.cmd D
```

## Klassendiagramm

```
                          ┌───────────────┐
                          │   ViewInfo    │
                          │  (interface)  │
                          └───────┬───────┘
                                  │
┌─────────────────┐       ┌───────┴───────┐
│  BaseViewPanel  │◄──────│   BaseView    │
│   (abstract)    │       │  (abstract)   │
└────────┬────────┘       └───────┬───────┘
         │                        │
    ┌────┴────┐              ┌────┴────┐
    │         │              │         │
┌───┴───┐ ┌───┴───┐    ┌─────┴───┐ ┌───┴─────┐
│TreeView│ │Editor │    │TreeView │ │ Editor  │
│ Panel  │ │ Panel │    │  View   │ │  View   │
└───┬────┘ └───┬───┘    └─────────┘ └─────────┘
    │          │
    │          │
┌───┴──────────┴───┐
│ JFormDesigner    │
│ Generated Panels │
└──────────────────┘
```

## Konfigurationsverwaltung

### AppConfig

Singleton-Konfigurationsmanager fuer die Anwendung. Verwaltet Properties aus `config.properties`.

```java
// Zugriff auf Konfiguration
AppConfig cfg = AppConfig.getInstance();

// Werte lesen
String value = cfg.getProperty("KEY");
String value = cfg.getProperty("KEY", "default");
String[] array = cfg.getArray("KEY");        // Semikolon-getrennt
boolean flag = cfg.getBool("KEY");
int num = cfg.getInt("KEY", defaultValue);

// Werte setzen und speichern
cfg.setProperty("KEY", "value");
cfg.save();

// Konfiguration aus anderer Datei laden
cfg.loadFrom("path/to/config.properties");
cfg.reload();
```

**Konfigurationsdatei-Prioritaet:**
1. System Property: `-Dconfig.file=path`
2. Environment Variable: `CONFIG_FILE_PATH`
3. Default: `config.properties` (im Arbeitsverzeichnis)

### ConnectionManager

Utility-Klasse fuer die Verwaltung von Datenbankverbindungen.

```java
// Verbindungen laden und abrufen
ConnectionManager.loadConnections();
List<ConnectionInfo> conns = ConnectionManager.getConnections();
String[] names = ConnectionManager.getConnectionNames();
ConnectionInfo conn = ConnectionManager.getConnection("name");

// Verbindung speichern/loeschen
ConnectionManager.saveConnection(conn);
ConnectionManager.deleteConnection("name");

// Letzte verwendete Verbindung
String last = ConnectionManager.getLastConnectionName();
ConnectionManager.setLastConnectionName("name");

// Listener fuer Aenderungen
ConnectionManager.addListener(listener);
ConnectionManager.removeListener(listener);
```

### ConnectionInfo

Datenmodell fuer eine Datenbankverbindung.

```java
ConnectionInfo conn = new ConnectionInfo(
    "MeinDB",                              // Name
    "org.postgresql.Driver",               // JDBC Driver
    "jdbc:postgresql://localhost:5432/db", // URL
    "user",                                // Username
    "pass"                                 // Password
);

// Serialisierung (Passwort Base64-kodiert)
String data = conn.serialize();
ConnectionInfo restored = ConnectionInfo.deserialize(data);
```

## DatabaseView

Die DatabaseView bietet einen vollstaendigen SQL-Client mit:

### Features

- **Verbindungsverwaltung**: Speichern, Laden, Loeschen von Verbindungen
- **Tabellen-Browser**: Lazy-Loading von Tabellen und Spalten mit Typen
- **SQL-Editor**: Syntax fuer SQL-Abfragen
- **SQL-History**: Automatische Speicherung ausgefuehrter Abfragen
- **Favoriten**: Wichtige Abfragen als Favoriten speichern
- **CSV-Export**: Ergebnisse als CSV exportieren

### SQL-History und Favoriten

```properties
# In ene-config.properties gespeichert:
SQL_HISTORY=SELECT * FROM users;;SELECT COUNT(*) FROM orders
SQL_FAVORITES=SELECT * FROM products WHERE active=true
```

### Unterstuetzte JDBC-Treiber

| Datenbank | Treiber-Klasse |
|-----------|----------------|
| PostgreSQL | org.postgresql.Driver |
| MySQL | com.mysql.cj.jdbc.Driver |
| Oracle | oracle.jdbc.OracleDriver |
| SQL Server | com.microsoft.sqlserver.jdbc.SQLServerDriver |
| H2 | org.h2.Driver |
| SQLite | org.sqlite.JDBC |

## SettingsPanel

Das SettingsPanel wird links im Hauptfenster angezeigt und bietet schnellen Zugriff auf:

- **DB-Verbindung**: Auswahl der aktiven Datenbankverbindung
- **Datenbank-Button**: Oeffnet die DatabaseView mit der ausgewaehlten Verbindung
- **Kunde**: Auswahl des aktiven Kunden (aus AVAILABLE_CUSTOMERS)
- **Standard-Treiber**: Anzeige des konfigurierten Standard-JDBC-Treibers

Das Panel reagiert automatisch auf Aenderungen der Verbindungsliste (Observer Pattern).

## Anwendungslayout

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Config-Toolbar                                                              │
│  [Config▼][↻] [DB▼][🗄] [Source▼] [Type▼] [Rev▼] ☐Dump ☐SFTP ☐Export...     │
├─────────────────────────────────────────────────────────────────────────────┤
│  View-Toolbar                                                                │
│  [Views:] [Sample] [Prozess] [Analyse] [Tree] [Customer] [Editor] [ITSQ]    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│                         JDesktopPane                                         │
│                                                                              │
│      ┌─────────────────────────────┐   ┌─────────────────────────────┐      │
│      │ CustomerTreeView            │   │ DatabaseView                │      │
│      │  📁 Kunden                  │   │  [SQL-Editor]               │      │
│      │   └─📁 Szenarien           │   │  [Ergebnis-Tabelle]         │      │
│      │      └─📄 Testfaelle       │   │                             │      │
│      └─────────────────────────────┘   └─────────────────────────────┘      │
│                                                                              │
│      ┌─────────────────────────────┐                                        │
│      │ ItsqTreeView                │                                        │
│      │  📁 ITSQ                    │                                        │
│      │   └─📁 ARCHIV-BESTAND      │                                        │
│      │   └─📁 REF-EXPORTS         │                                        │
│      └─────────────────────────────┘                                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Dual-Toolbar Konzept

**Config-Toolbar** (obere Zeile):
- Konfigurationsdatei-Auswahl (`*-config.properties`)
- DB-Verbindungs-Auswahl
- Testquellen, Testtypen, ITSQ-Revisionen
- Feature-Flags (Checkboxen)

**View-Toolbar** (zweite Zeile):
- Buttons zum Oeffnen der registrierten Views
- Dynamisch basierend auf `registerView()` Aufrufen

## Konfigurationsdatei (config.properties)

```properties
# Window - Fensterposition und Groesse
LAST_WINDOW_WIDTH=1200
LAST_WINDOW_HEIGHT=800
LAST_WINDOW_X_POS=100
LAST_WINDOW_Y_POS=100
LAST_LEFT_SPLIT_DIVIDER=250
LAST_MAIN_SPLIT_DIVIDER=300

# Database - Datenbankverbindungen
DB_CONNECTIONS=MeinDB|org.postgresql.Driver|jdbc:postgresql://...|user|cGFzcw==
LAST_DB_CONNECTION=MeinDB
SQL_HISTORY=SELECT * FROM users
SQL_FAVORITES=

# Settings - Anwendungseinstellungen
AVAILABLE_CUSTOMERS=Kunde1;Kunde2;Kunde3
DEFAULT_DRIVER=org.postgresql.Driver
```

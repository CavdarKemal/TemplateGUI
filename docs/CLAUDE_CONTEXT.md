# Claude Code Kontext - TemplateGUI

## Projekt-Uebersicht

**Pfad:** `E:\Projekte\ClaudeCode\TemplateGUI`
**Typ:** Java Swing MDI-Anwendung (Multi-Document Interface)
**Build:** Maven, Java 17
**Package:** `de.cavdar.gui`
**Ziel:** Template-Projekt das Funktionalitaeten aus StandardMDIGUI und ITSQ-Test vereint

## Quellprojekte

| Projekt | Beitrag |
|---------|---------|
| **StandardMDIGUI** | MDI-Framework, Views, Design-Pattern, Docker-Setup |
| **ITSQ-Test** | Maven-Artefakt-Integration, Assembly-Konzept, Dokumentation |

## Erstellungsdatum

**03.01.2026** - Initiale Erstellung durch Zusammenfuehrung der Quellprojekte

## Letzte Aenderungen

**05.01.2026** - ItsqEditorView Dual-Modus:
- **XML-Modus**: RSyntaxTextArea mit Syntax-Highlighting, Suche (F3/Shift+F3)
- **Properties-Modus**: Tabellen-Editor mit Name/Wert-Spalten, CRUD-Buttons
- **ItsqEditorPanel**: JFormDesigner-Toolbar mit Neu/Aendern/Loeschen/Filter/Speichern
- **CardLayout**: Automatischer Modus-Wechsel basierend auf Dateityp (.xml vs .cfg/.properties)
- **PropertiesTableModel**: Innere Klasse mit Filter und Kommentar-Erhaltung

**05.01.2026** - Itsq-Panels und Views ueberarbeitet:
- **ItsqArchivBestandPhasePanel/View**: Aktualisiert
- **ItsqCustomerPanel/View**: Aktualisiert
- **ItsqRefExportsPhasePanel/View**: Aktualisiert

**04.01.2026** - Package-Refactoring:
- **Main.java** nach `de.cavdar.gui` verschoben (vorher MainView.java in view/)
- **Package `jfd` umbenannt zu `itsq`**: Bessere Namenskonvention
- **Sub-Packages eingefuehrt**: design/base, design/prozess, view/base, view/prozess, model/base, etc.

**04.01.2026** - Typisiertes Tree-Model (ItsqTreeModel):
- **Package `de.cavdar.gui.itsq.model`**: Model-Klassen (UserObjects)
- **Package `de.cavdar.gui.itsq.tree`**: TreeNode-Klassen
- **ItsqExplorerView**: Verwendet typisierte Nodes mit `instanceof`-Checks

## Projektstruktur

```
TemplateGUI/
├── pom.xml                     # Maven Build mit Artefakt-Integration
├── README.md                   # Projektueberblick
├── docs/
│   ├── CLAUDE_CONTEXT.md       # Dieses Dokument
│   └── gui.md                  # GUI-Architektur
└── src/main/java/de/cavdar/gui/
    ├── Main.java               # Einstiegspunkt mit main()
    ├── design/                 # GUI Panels (mit Sub-Packages)
    │   ├── base/               # BaseViewPanel, MainFrame, DesktopPanel
    │   ├── prozess/            # ProzessViewPanel
    │   ├── db/                 # DatabaseViewPanel
    │   └── json/               # ItsqTreeViewPanel
    ├── view/                   # Views (mit Sub-Packages)
    │   ├── base/               # BaseView, ViewInfo
    │   ├── prozess/            # ProzessView
    │   ├── db/                 # DatabaseView
    │   └── json/               # ItsqTreeView
    ├── model/base/             # AppConfig, ConfigEntry, ConnectionInfo
    ├── util/                   # Utilities
    ├── exception/              # Exceptions
    └── itsq/                   # ITSQ Explorer
        ├── design/             # JFD-generierte Panels
        ├── model/              # ItsqItem Model-Klassen
        ├── tree/               # TreeNode-Klassen
        └── view/               # View-Klassen
```

## Package-Struktur

```
de.cavdar.gui/
├── Main.java                   # Einstiegspunkt mit main()
├── design/
│   ├── base/                   # BaseViewPanel, MainFrame, DesktopPanel, SettingsPanel
│   ├── prozess/                # ProzessViewPanel
│   ├── db/                     # DatabaseViewPanel
│   └── json/                   # ItsqTreeViewPanel
├── view/
│   ├── base/                   # BaseView, ViewInfo
│   ├── prozess/                # ProzessView
│   ├── db/                     # DatabaseView
│   └── json/                   # ItsqTreeView
├── model/base/                 # AppConfig, ConfigEntry, ConnectionInfo
├── util/                       # ConnectionManager, IconLoader, TestEnvironmentManager
├── exception/                  # ConfigurationException
└── itsq/                       # ITSQ Explorer (JFormDesigner)
    ├── design/                 # ItsqMainPanel, ItsqTreePanel, etc.
    ├── model/                  # ItsqItem, ItsqRoot, ItsqCustomer, etc.
    ├── tree/                   # ItsqTreeModel, ItsqTreeNode, etc.
    └── view/                   # ItsqExplorerView, ItsqItemSelectable, etc.
```

## Design-View-Trennung Pattern

1. **Panel-Klasse** (`design/`): Nur GUI-Komponenten
2. **View-Klasse** (`view/`): Nur Logik und Event-Handler

## Registrierte Views

| View | Shortcut | Icon |
|------|----------|------|
| ProzessView | Ctrl+2 | gear_run.png |
| ItsqTreeView | Ctrl+I | folder_cubes.png |
| ItsqExplorerView | Ctrl+J | folder_cubes.png |

## Abhaengigkeiten

| Dependency | Version | Zweck |
|------------|---------|-------|
| log4j | 1.2.12 | Logging |
| slf4j-api | 2.0.9 | Logging Facade |
| postgresql | 42.7.4 | JDBC Driver |
| jackson-databind | 2.17.0 | JSON Parsing |
| junit-jupiter | 5.10.2 | Unit Tests |
| assertj-swing | 3.17.1 | GUI Tests |

## Prompt zum Fortsetzen

```
Ich arbeite am Java-Projekt TemplateGUI unter E:\Projekte\ClaudeCode\TemplateGUI.
Bitte lies docs/CLAUDE_CONTEXT.md und docs/gui.md fuer den Kontext.

Aktueller Stand (05.01.2026):
- ItsqEditorView mit Dual-Modus (XML/Properties) fertig
- ItsqEditorPanel Toolbar via JFormDesigner (Neu/Aendern/Loeschen/Filter/Speichern)
- CardLayout wechselt automatisch je nach Dateityp
- PropertiesTableModel mit Filter und Kommentar-Erhaltung

Naechste moegliche Aufgaben:
- GUI testen und Feintuning
- Weitere Views ueberarbeiten
- Neue Features implementieren
```

---
title: Statusbalken
---
Mit dem Review-Plugin werden Pull Requests im SCM-Manager unterstützt. Das SCM-CI-Plugin kann sich in diese Pull Requests einklinken und sie mit nützlichen Informationen anreichern.

In den Pull Request Details zeigt eine Statusbar Informationen von allen integrierten CI-Systemen für diesen Pull Request an.

Mit nur einer Jenkins Instanz und einem Build-Job für einen Feature-Branch könnte das wie folgt aussehen:

![Pull Request Statusbar](assets/statusbar-pending.png)

Dieser Statusbalken könnte je Build-Job-Status in folgenden Varianten auftreten:

### Laufend
Der Jenkins-Build-Job ist gestartet und noch nicht abgeschlossen.

![Statusbar Pending](assets/status-pending.png)

### Erfolgreich
Der Jenkins-Build-Job ist erfolgreich durchgelaufen und im Jenkins grün.

![Statusbar Successful](assets/status-success.png)

### Instabil
Der Jenkins-Build-Job ist gelb. Womöglich sind Unit-Tests fehlgeschlagen.

![Statusbar Unstable](assets/status-unstable.png)

### Fehlerhaft
Der Jenkins-Build-Job ist rot. Der Job konnte nicht erfolgreich durchlaufen, weil ein Fehler aufgetreten ist.

![Statusbar Unstable](assets/status-failure.png)

Sollte dabei nur eine der Analysen fehlschlagen, würde der Statusbalken bereits rot angezeigt werden. Nur wenn alle Analysen fehlerfrei durchlaufen, wird ein grüner Haken angezeigt.

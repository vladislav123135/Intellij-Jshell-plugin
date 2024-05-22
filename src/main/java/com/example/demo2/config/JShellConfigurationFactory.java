package com.example.demo2.config;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

//In IntelliJ IDEA benötigt jede Art von ausführbaren Aktionen (wie das Ausführen eines Java-Programms, eines Scripts usw.) eine zugehörige Konfiguration. Diese Konfigurationen steuern, wie das Programm oder Script ausgeführt wird, welche Argumente es erhält, welches Arbeitsverzeichnis es nutzt, und mehr.
//JShellConfigurationFactory erstellt Vorlagen für diese Konfigurationen. Das bedeutet, dass sie die nötigen Informationen liefert, um eine Laufzeitumgebung für JShell-Skripte einzurichten.
// Dies schließt Dinge wie das Setup von Klassenpfaden, das Hinzufügen von Bibliotheken und das Konfigurieren von Umgebungsvariablen ein.
public class JShellConfigurationFactory extends ConfigurationFactory {

    public JShellConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    // erstellt und gibt eine neue Instanz von JShellRunConfiguration zurück, die mit dem übergebenen Project verknüpft ist.
    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new JShellRunConfiguration(project, this, "JShell");
    }
    // stellt sicher, dass die Konfigurationsfactory eindeutig identifizierbar ist,
    @Override
    public @NotNull String getId() {
        return "JShellConfiguration";
    }
}

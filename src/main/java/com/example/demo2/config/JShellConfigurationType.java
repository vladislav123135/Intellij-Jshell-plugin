package com.example.demo2.config;

import com.example.demo2.config.JShellConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


//in dieser Klasse wird eine Konfiguration für die Ausführung von JShell-Skripten definiert.
public class JShellConfigurationType implements ConfigurationType {

    private final ConfigurationFactory factory;

    public JShellConfigurationType() {
        factory = new JShellConfigurationFactory(this);
    }

    //Gibt den Namen zurück, der in der Benutzeroberfläche der IDE angezeigt wird, wenn Benutzer eine neue Konfiguration dieses Typs erstellen möchten.
    @Override
    public @NotNull String getDisplayName() {
        return "JShell";
    }
    //Gibt eine kurze Beschreibung für diesen Konfigurationstyp zurück, die den Benutzern hilft zu verstehen, was dieser Konfigurationstyp macht.
    @Override
    public String getConfigurationTypeDescription() {
        return "JShell Run Configuration";
    }

    //Hier können wir einen ICON für config erstellen/zurückgeben
    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public @NotNull String getId() {
        return "JShellRunConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{factory};
    }
}


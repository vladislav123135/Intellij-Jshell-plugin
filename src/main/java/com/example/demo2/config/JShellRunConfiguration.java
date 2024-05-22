package com.example.demo2.config;

import com.example.demo2.runtime.JShellCommandLineState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JShellRunConfiguration extends RunConfigurationBase<Object> {
    private String scriptPath;

    public JShellRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new JShellCommandLineState(environment, this);
    }

    @Override
    public @Nullable SettingsEditor<? extends RunConfigurationBase<?>> getConfigurationEditor() {
        return new SettingsEditor<JShellRunConfiguration>() {
            private JTextField scriptPathField;

            @Override
            protected void resetEditorFrom(@NotNull JShellRunConfiguration configuration) {
                scriptPathField.setText(configuration.getScriptPath());
            }

            @Override
            protected void applyEditorTo(@NotNull JShellRunConfiguration configuration) {
                configuration.setScriptPath(scriptPathField.getText());
            }

            @Override
            @NotNull
            protected JComponent createEditor() {
                JPanel panel = new JPanel(new BorderLayout());

                JLabel label = new JLabel("Script Path:");
                panel.add(label, BorderLayout.WEST);

                scriptPathField = new JTextField();
                panel.add(scriptPathField, BorderLayout.CENTER);

                return panel;
            }
        };
    }

    @Override
    public void checkConfiguration() {
        if (scriptPath == null || scriptPath.trim().isEmpty()) {
            throw new RuntimeException("Script path cannot be empty");
        }
    }
}

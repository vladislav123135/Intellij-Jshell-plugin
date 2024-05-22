package com.example.demo2.actions;

import com.example.demo2.config.JShellRunConfiguration;
import com.example.demo2.config.JShellConfigurationFactory;
import com.example.demo2.config.JShellConfigurationType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class JShellRunAction extends AnAction {
    //  Diese Methode wird ausgelöst, wenn der Benutzer die Aktion auslöst
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            if (project == null) return;

            // Hole die aktuell ausgewählte Datei
            VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
            if (file == null) return;

            // Überprüfe, ob die Dateiendung .jsh ist
            String filePath = file.getPath();
            if (!filePath.endsWith(".jsh")) {
                Messages.showErrorDialog(project, "This file is not a JShell script (.jsh).", "Invalid File Type");
                return;
            }

            JShellConfigurationType type = new JShellConfigurationType();
            JShellConfigurationFactory factory = new JShellConfigurationFactory(type);
            JShellRunConfiguration configuration = new JShellRunConfiguration(project, factory, "JShell");
            configuration.setScriptPath(filePath);

            // Erstellen und Ausführen der Konfiguration
            RunManager runManager = RunManager.getInstance(project);
            RunnerAndConfigurationSettings settings = runManager.createConfiguration(configuration, factory);
            runManager.addConfiguration(settings);

            try {
                ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings);
                ProgramRunnerUtil.executeConfiguration(builder.build(), false, false);
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }



package com.example.demo2.runtime;

import com.example.demo2.config.JShellRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


// diese Klasse wird verwendet, um eine JShell-Sitzung zu starten, basierend auf der jsh-datei
public class JShellCommandLineState extends CommandLineState {

    private final JShellRunConfiguration configuration;

    public JShellCommandLineState(@NotNull ExecutionEnvironment environment, @NotNull JShellRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        // Überprüfung der Dateipfad
        String scriptPath = configuration.getScriptPath();
        if (scriptPath == null || scriptPath.trim().isEmpty()) {
            throw new ExecutionException("No script path specified");
        }

        File file = new File(scriptPath);
        if (!file.exists() || !file.canRead()) {
            throw new ExecutionException("Script file not found or unreadable");
        }

        //Erstellt einen Prozess, der die JShell mit dem gegebenen Skript startet
        GeneralCommandLine commandLine = new GeneralCommandLine("jshell");
        commandLine.addParameter("--startup=DEFAULT");

        ProcessHandler processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void startNotified(ProcessEvent event) {
                super.startNotified(event);
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processHandler.getProcessInput().write((line + "\n" ).getBytes(StandardCharsets.UTF_8));
                        processHandler.getProcessInput().flush();
                    }
                    processHandler.getProcessInput().close();
                } catch (IOException e) {
                    System.err.println("Error reading script file: " + e.getMessage());
                }
            }
        });

        return processHandler;
    }


}
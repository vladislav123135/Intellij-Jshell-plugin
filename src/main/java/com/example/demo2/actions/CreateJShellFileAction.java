package com.example.demo2.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.fileEditor.FileEditorManager;
import java.io.IOException;
//  Hier wird eine Aktion definiert, um eine neue .jsh-Datei im Projektverzeichnis zu erstellen und zu öffnen.
public class CreateJShellFileAction extends AnAction {
    //Diese Methode wird ausgelöst, wenn die Aktion durch das Benutzerinterface triggered wird.
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        //Überprüft, ob ein Project-Objekt vorhanden ist.
        if (project == null) return;
        //In einem runWriteAction, da Schreibzugriffe erforderlich sind
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String basePath = project.getBasePath();
                if (basePath == null) return;

                VirtualFile baseDir = LocalFileSystem.getInstance().findFileByPath(basePath);
                if (baseDir == null) return;

                // Erstellt eine neue .jsh-Datei im Basisverzeichnis des Projekts.
                VirtualFile jshFile = baseDir.createChildData(this, "newScript.jsh");
                //Öffnet diese Datei im Editor, sodass der Benutzer direkt mit der Bearbeitung beginnen kann.
                FileEditorManager.getInstance(project).openFile(jshFile, true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}

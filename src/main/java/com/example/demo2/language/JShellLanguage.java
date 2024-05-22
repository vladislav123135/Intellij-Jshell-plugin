package com.example.demo2.language;

import com.intellij.lang.Language;

// Da JShell spezielle Syntax und Befehle hat, die sich von standardmäßigem Java unterscheiden, ermöglicht die Definition einer eigenen Language-Klasse, dass Plugins und Erweiterungen diese Unterschiede erkennen und entsprechend damit umgehen können.
public class JShellLanguage extends Language {
    public static final JShellLanguage INSTANCE = new JShellLanguage();
    public static final String ID = "JShell";

    private JShellLanguage() {
        super(ID);
    }
}

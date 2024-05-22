package com.example.demo2.fileType;

import com.example.demo2.language.JShellLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

// Diese Klasse definiert, wie Dateien mit der Endung .jsh behandelt werden sollen.
public class JShellFileType extends LanguageFileType {
    public static final JShellFileType INSTANCE = new JShellFileType();

    private JShellFileType() {
        super(JShellLanguage.INSTANCE);
    }

    @Override
    public String getName() {
        return "JShell File";
    }

    @Override
    public String getDescription() {
        return "JShell script file";
    }

    @Override
    public String getDefaultExtension() {
        return "jsh";
    }




    @Override
    public Icon getIcon() {
        return loadPngIcon("/META-INF/shellIcon.png", 16, 16);
    }

    private Icon loadPngIcon(String path, int width, int height) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                System.err.println("Resource not found: " + path);
                return null;
            }

            BufferedImage originalImage = ImageIO.read(inputStream);
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

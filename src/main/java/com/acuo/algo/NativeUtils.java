/*
 * Class NativeUtils is published under the The MIT License:
 *
 * Copyright (c) 2012 Adam Heinrich <adam@adamh.cz>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.acuo.algo;


import com.acuo.algo.OSUtils.OSType;
import com.acuo.algo.OSUtils.UnsupportedOSException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static com.acuo.algo.OSUtils.getOperatingSystemType;


/**
 * A simple library class which helps with loading dynamic libraries stored in the
 * JAR archive. These libraries usualy contain implementation of some methods in
 * native code (using JNI - Java Native Interface).
 */
public class NativeUtils {

    private NativeUtils() {
    }

    public static void load(String... libNames) {
        Arrays.stream(libNames).forEach(NativeUtils::load);
    }

    private static void load(String libName) {
        OSType os = getOperatingSystemType();
        switch (os) {
            case Linux:
                NativeUtils.loadLibraryFromJar("/lib"+libName+".so");
                break;
            case MacOSX:
                NativeUtils.loadLibraryFromJar("/lib"+libName+".dylib");
                break;
            case Windows:
                NativeUtils.loadLibraryFromJar("/"+libName+".dll");
                break;
            default:
                throw new UnsupportedOSException(os);
        }
    }

    private static void loadLibraryFromJar(String path) {

        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "." + parts[parts.length - 1] : null; // Thanks, davs! :-)
        }

        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

        try {

            // Prepare temporary file
            Path natives = Files.createTempDirectory("natives");
            Path temp = Files.createFile(Paths.get(natives.toString(), prefix + suffix));
            DeletePathsAtShutdown.register(natives);
            DeletePathsAtShutdown.register(temp);

            if (Files.notExists(temp)) {
                throw new FileNotFoundException("File " + temp + " does not exist.");
            }

            // Open and check input stream
            InputStream is = NativeUtils.class.getResourceAsStream(path);
            if (is == null) {
                throw new FileNotFoundException("File " + path + " was not found inside JAR.");
            }

            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);

            is.close();

            addLibraryPath(natives.toAbsolutePath().toString());

            // Finally, load the library
            System.load(temp.toFile().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addLibraryPath(String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }
}
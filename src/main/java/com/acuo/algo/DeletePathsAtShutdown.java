package com.acuo.algo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

final class DeletePathsAtShutdown {

  private static LinkedHashSet<Path> paths = new LinkedHashSet<>();

  static {
    Runtime.getRuntime().addShutdownHook(
        new Thread(DeletePathsAtShutdown::shutdownHook));
  }

  private static void shutdownHook() {
    LinkedHashSet<Path> local;

    synchronized(paths) {
      local = paths;
      paths = null;
    }

    ArrayList<Path> toBeDeleted = new ArrayList<>(local);
    Collections.reverse(toBeDeleted);
    for (Path p : toBeDeleted) {
      try {
        Files.delete(p);
      } catch (IOException | RuntimeException e) {
        // do nothing - best-effort
      }
    }
  }

  static synchronized void register(Path p) {
    if (paths == null) {
      throw new IllegalStateException("ShutdownHook already in progress.");
    }
    paths.add(p);
  }
}
package com.acuo.algo;

import java.util.Locale;

class OSUtils {

    public enum OSType {
		Windows, MacOSX, Linux, Other
	}

	private static OSType detectedOS;

	static OSType getOperatingSystemType() {
		if (detectedOS == null) {
			String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if ((OS.contains("mac")) || (OS.contains("darwin")))
				detectedOS = OSType.MacOSX;
			else if (OS.contains("win"))
				detectedOS = OSType.Windows;
			else if (OS.contains("nux"))
				detectedOS = OSType.Linux;
			else
				detectedOS = OSType.Other;
		}
		return detectedOS;
	}

	static class UnsupportedOSException extends RuntimeException {
		private static final long serialVersionUID = -4811942511439627417L;

		UnsupportedOSException(OSType os) {
			super("Unsupported OS: " + os.name() + " (" + System.getProperty("os.name") + ") ");
		}
	}

	static class UnsupportedArchitectureException extends RuntimeException {
		private static final long serialVersionUID = -4811942511439627417L;

		public UnsupportedArchitectureException(String arch) {
			super("Unsupported architecture: " + arch);
		}
	}
}
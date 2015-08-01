package io.honeybadger.reporter.dto;

import java.io.Serializable;

import static io.honeybadger.reporter.ErrorReporter.APPLICATION_PACKAGE_PROP_KEY;

/**
 * One single line on a backtrace.
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.9
 */
public class BacktraceElement implements Serializable {
    private static final long serialVersionUID = -4455225669072193184L;

    /**
     * Enum representing all of the valid context values on the Honeybadger API.
     */
    static enum Context {
        /** Backtrace not-belonging to the calling application. **/
        ALL("all"),
        /** Backtrace belonging to the calling application. **/
        APP("app");

        private final String name;

        Context(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }
    }

    public final String file;
    public final String method;
    public final String number;
    public final String context;

    public BacktraceElement(String number, String file, String method) {
        this.number = number;
        this.file = file;
        this.method = method;
        this.context = calculateContext(method).getName();
    }

    public BacktraceElement(StackTraceElement element) {
        this.number = String.valueOf(element.getLineNumber());
        this.file = String.valueOf(element.getFileName());
        this.method = formatMethod(element);
        this.context = calculateContext(method).getName();
    }

    static String formatMethod(StackTraceElement element) {
        return String.format("%s.%s",
                element.getClassName(), element.getMethodName());
    }

    static Context calculateContext(String method) {
        final String appPackage = System.getProperty(APPLICATION_PACKAGE_PROP_KEY);
        final Context methodContext;

        if (appPackage == null || appPackage.isEmpty()) {
            methodContext = Context.ALL;
        } else if (method == null || method.isEmpty()) {
            methodContext = Context.ALL;
        } else if (method.startsWith(appPackage)) {
            methodContext = Context.APP;
        } else {
            methodContext = Context.ALL;
        }

        return methodContext;
    }
}
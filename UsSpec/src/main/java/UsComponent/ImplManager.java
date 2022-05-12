package UsComponent;

import java.util.List;

public class ImplManager {

    private static UsSpec usSpec;
    private static List<CliOptions> cliOptions;
    public static void registerExporter(UsSpec Specifikacija) {
        usSpec = Specifikacija;
    }
    public static UsSpec getUsSpec() {
        return usSpec;
    }
    public static void setCliOptions(List<CliOptions> cliOptions) {
        ImplManager.cliOptions = cliOptions;
    }
    public static List<CliOptions> getCliOptions() {
        return cliOptions;
    }
}

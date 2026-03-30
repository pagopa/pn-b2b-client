package it.pagopa.pn.interop.cucumber;

public class ParallelRunnerWrapper {
    public static void main(String[] args) throws Exception {
        ProcessBuilder nrt = new ProcessBuilder(
            "mvn", "test",
            "-Dtest=it.pagopa.pn.interop.cucumber.NrtTest",
            "-Dspring.profiles.active=nrt"
        );
        ProcessBuilder m2mv3 = new ProcessBuilder(
            "mvn", "test",
            "-Dtest=it.pagopa.pn.interop.cucumber.M2MV3Test",
            "-Dspring.profiles.active=m2mv3"
        );

        Process p1 = nrt.inheritIO().start();
        Process p2 = m2mv3.inheritIO().start();

        int exit1 = p1.waitFor();
        int exit2 = p2.waitFor();

        if (exit1 != 0 || exit2 != 0) {
            throw new RuntimeException("Uno dei runner è fallito");
        }
    }
}


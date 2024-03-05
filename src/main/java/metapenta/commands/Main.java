package metapenta.commands;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;


public class Main {
    Map<String, Set<String>> genMapNotations = new HashMap<>();
    Map<String, Set<String>> clusterMapNotations = new HashMap<>();

    int[] functionalNotationColums = new int[]{2, 6};

    String functionalNotationFilePath;
    String clustersFilePath;

    String fileOut;

    public Main(String functionalNotationFilePath, String clustersFilePath, String fileOut){
        this.functionalNotationFilePath = functionalNotationFilePath;
        this.clustersFilePath = clustersFilePath;
        this.fileOut = fileOut;
    }

    public void concatenateNotationsWithOrthologyInformation() {
        try (BufferedReader functionalNotationFile = Files.newBufferedReader(Paths.get(functionalNotationFilePath));
             BufferedReader clustersFile = Files.newBufferedReader(Paths.get(clustersFilePath))) {

            processFunctionalNotationFile(functionalNotationFile);
            processClustersFile(clustersFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        writeClusterNotationCsv(fileOut);
    }

    private void processFunctionalNotationFile(BufferedReader functionalNotationFile) throws IOException {
        String line;
        functionalNotationFile.readLine(); // Skip header
        while ((line = functionalNotationFile.readLine()) != null) {
            String[] data = line.split("\t");
            String gen = data[0];
            Set<String> functionalNotations = new HashSet<>();

            for (int columNumber : functionalNotationColums) {
                String functionalNotation = data[columNumber].trim();

                if (!functionalNotation.equals(".")) {
                    functionalNotations.add(cleanBlastResult(functionalNotation));
                }
            }

            if (!functionalNotations.isEmpty()) {
                genMapNotations.put(gen, functionalNotations);
            }
        }
    }

    private void processClustersFile(BufferedReader clustersFile) throws IOException {
        String line;
        while ((line = clustersFile.readLine()) != null) {
            String[] data = line.split("\t");
            String cluster = data[0];
            for (int i = 1; i < data.length; i++) {
                String genName = data[i];
                Set<String> currentClusterNotations = genMapNotations.getOrDefault(genName, Collections.emptySet());
                if (!currentClusterNotations.isEmpty()) {
                    clusterMapNotations.computeIfAbsent(cluster, k -> new HashSet<>()).addAll(currentClusterNotations);
                }
            }
        }
    }

    private void writeClusterNotationCsv(String fileOut) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileOut))) {
            for (Map.Entry<String, Set<String>> entry : clusterMapNotations.entrySet()) {
                String cluster = entry.getKey();
                Set<String> notations = entry.getValue();

                StringBuilder allNotationsString = new StringBuilder();
                for (String notation : notations) {
                    allNotationsString.append(notation).append(",");
                }

                writer.write(cluster + "$" + allNotationsString);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cleanBlastResult(String blast) {
        if (blast.equals(".")) {
            return "";
        }
        Matcher matcher = Pattern.compile("RecName: .*?;").matcher(blast);
        if (matcher.find()) {
            return matcher.group(0).substring(14);
        } else {
            throw new IllegalArgumentException(blast);
        }
    }

    public static void main(String[] args) {
        String functionalNotationFilePath = "C:\\Users\\v25a0\\OneDrive\\Documents\\Repositories\\Flag\\MetaPeNTACore\\src\\main\\java\\metapenta\\commands\\data\\Plunatus_563_report.tsv";
        String clustersFilePath = "C:\\Users\\v25a0\\OneDrive\\Documents\\Repositories\\Flag\\MetaPeNTACore\\src\\main\\java\\metapenta\\commands\\data\\all_phaseolus_new_annotation_025_clusters.txt";
        String fileOut = "data/all_cluster_notations_new_notation_functional_notation.csv";

        Main main = new Main(functionalNotationFilePath, clustersFilePath, fileOut);
        main.concatenateNotationsWithOrthologyInformation();
    }
}
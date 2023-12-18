import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectionSimulator {
    public static class Votant {
        private List<String> preferences;

        public Votant(List<String> preferences) {
            this.preferences = preferences;
        }

        public List<String> getPreferences() {
            return preferences;
        }
    }

    public static void main(String[] args) {
        List<String> candidates = List.of("A", "B", "C");
        int numVoters = 60;

        List<Votant> voters = simulateVoters(candidates, numVoters);

        List<String> prefGlobal = calculateGlobalPreferences(candidates, numVoters, voters);
        System.out.println("Voici la préférence globale selon les votants : " + prefGlobal);

        String condorcetWinner = findCondorcetWinner(candidates, numVoters, voters);

        System.out.println("\nGagnant selon le critère de Condorcet : " + condorcetWinner+"\n");
        

        

        // Vote Uninominal à un Tour
        String pluralityWinner = runPluralityVote(candidates, numVoters, voters);
        System.out.println("\nGagnant selon le vote uninominal à un tour : " + pluralityWinner+"\n");
        System.out.println("Le critère de Condorcet a-t-il été respecté? " + isCondorcet(pluralityWinner,condorcetWinner)+"\n");
        

        // Vote Alternatif (Vote par Classement) qui respecte le critère de Condorcet
        String rankedVoteWinner = runRankedVote(candidates, numVoters, voters);
        System.out.println("\nGagnant selon le vote alternatif : " + rankedVoteWinner+"\n");
        System.out.println("Le critère de Condorcet a-t-il été respecté? " + isCondorcet(rankedVoteWinner,condorcetWinner)+"\n");
    }
    public static String isCondorcet(String winner, String condorcet){
            if(condorcet==winner){
                return "Oui";
            }else{
                return "Non";
            }
    }

    private static List<Votant> simulateVoters(List<String> candidates, int numVoters) {
        List<Votant> voters = new ArrayList<>();

        for (int i = 0; i < numVoters; i++) {
            List<String> preferences = new ArrayList<>(candidates);
            Collections.shuffle(preferences);
            voters.add(new Votant(preferences));
        }

        return voters;
    }

    private static List<String> calculateGlobalPreferences(List<String> candidates, int numVoters, List<Votant> voters) {
        Map<String, Integer> points = new HashMap<>();

        for (String candidate : candidates) {
            points.put(candidate, 0);
        }

        for (Votant voter : voters) {
            List<String> voterPreferences = voter.getPreferences();
            for (int i = 0; i < candidates.size(); i++) {
                String candidate = voterPreferences.get(i);
                points.put(candidate, points.get(candidate) + (numVoters - i));
            }
        }

        List<String> globalPreferences = new ArrayList<>(candidates);
        globalPreferences.sort((c1, c2) -> Integer.compare(points.get(c2), points.get(c1)));

        return globalPreferences;
    }

    //  comparer les préférences de chaque votant pour chaque paire de candidats, 
    //et le candidat qui remporte le plus grand nombre de duels est déclaré gagnant selon le critère de Condorcet.
    private static String findCondorcetWinner(List<String> candidates, int numVoters, List<Votant> voters) {
        Map<String, Integer> wins = new HashMap<>();

        for (String candidate : candidates) {
            wins.put(candidate, 0);
        }

        for (Votant voter : voters) {
            List<String> voterPreferences = voter.getPreferences();
            for (String candidate1 : candidates) {
                for (String candidate2 : candidates) {
                    if (!candidate1.equals(candidate2)) {
                        compareCandidatesAndUpdateWins(candidate1, candidate2, voterPreferences, wins);
                    }
                }
            }
        }

        for (String cand : candidates) {
            System.out.println("Nombre de votes pour " + cand + ": " + wins.get(cand));
        }

        // Affichage des votes de chaque candidat selon les préférences
        displayHistogram(wins);
        displayPairwiseComparisons(wins);

        return wins.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static void compareCandidatesAndUpdateWins(String candidate1, String candidate2, List<String> preferences,
                                                       Map<String, Integer> wins) {
        int index1 = preferences.indexOf(candidate1);
        int index2 = preferences.indexOf(candidate2);

        if (index1 < index2) {
            wins.put(candidate1, wins.get(candidate1) + 1);
        } else {
            wins.put(candidate2, wins.get(candidate2) + 1);
        }
    }

    private static void displayHistogram(Map<String, Integer> wins) {
        int maxLength = wins.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    
        for (String candidate : wins.keySet()) {
            int count = wins.get(candidate);
            int barLength = (int) ((double) count / maxLength * 50); // 50 is the maximum width
    
            System.out.printf("%s: %d ", candidate, count);
            for (int i = 0; i < barLength; i++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    private static void displayPairwiseComparisons(Map<String, Integer> wins) {
        for (String candidate1 : wins.keySet()) {
            for (String candidate2 : wins.keySet()) {
                if (!candidate1.equals(candidate2)) {
                    int count1 = wins.get(candidate1);
                    int count2 = wins.get(candidate2);
                    System.out.printf("%d préfèrent %s > %s contre %d pour %s > %s\n",
                            count1, candidate1, candidate2, count2, candidate2, candidate1);
                }
            }
        }
    }
    private static String runPluralityVote(List<String> candidates, int numVoters, List<Votant> voters) {
        Map<String, Integer> votes = new HashMap<>();
    
        for (String candidate : candidates) {
            votes.put(candidate, 0);
        }
    
        for (Votant voter : voters) {
            String topChoice = voter.getPreferences().get(0);
            votes.put(topChoice, votes.get(topChoice) + 1);
        }
    
        System.out.println("Résultats du vote uninominal à un tour :");
        for (String candidate : candidates) {
            System.out.println(candidate + ": " + votes.get(candidate) + " votes");
        }
    
        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    private static String runRankedVote(List<String> candidates, int numVoters, List<Votant> voters) {
        Map<String, Integer> points = new HashMap<>();
    
        for (String candidate : candidates) {
            points.put(candidate, 0);
        }
    
        for (Votant voter : voters) {
            List<String> voterPreferences = voter.getPreferences();
    
            for (int i = 0; i < candidates.size(); i++) {
                String candidate = voterPreferences.get(i);
                points.put(candidate, points.get(candidate) + (numVoters - i));
            }
        }
    
        System.out.println("Résultats du vote alternatif (par classement) :");
        for (String candidate : candidates) {
            System.out.println(candidate + ": " + points.get(candidate) + " points");
        }
    
        return points.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
}

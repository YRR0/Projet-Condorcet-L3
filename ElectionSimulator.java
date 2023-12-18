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

        System.out.println("Gagnant selon le critère de Condorcet : " + condorcetWinner);
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
}

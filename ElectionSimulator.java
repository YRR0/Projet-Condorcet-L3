import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectionSimulator {

    public static void main(String[] args) {
        // Exemple avec trois candidats A, B, C et un grand nombre de votants
        List<String> candidates = List.of("A", "B", "C");
        int numVoters = 500000; // Simulation avec un grand nombre de votants

        
        // Simuler un scrutin aléatoire pour chaque votant
        List<List<String>> ballots = simulateRandomVotes(candidates, numVoters);
        
        List<String> prefGlobal = calculateGlobalPreferences(candidates, numVoters, ballots);
        System.out.println(" Voici la preference Global selon les votants ");
        for(String a : prefGlobal) System.out.print(a+" ");
        System.out.println();

        // Calculer le gagnant selon le critère de Condorcet
        String condorcetWinner = findCondorcetWinner(candidates, numVoters, ballots);

        // Afficher les résultats
        System.out.println("Gagnant selon le critère de Condorcet : " + condorcetWinner);
    }

    // Simuler un vote aléatoire pour chaque votant
    private static List<List<String>> simulateRandomVotes(List<String> candidates, int numVoters) {
        List<List<String>> ballots = new ArrayList<>();

        for (int i = 0; i < numVoters; i++) {
            List<String> ballot = new ArrayList<>(candidates);
            Collections.shuffle(ballot);
            ballots.add(ballot);
        }

        return ballots;
    }

    // Trouver le gagnant selon le critère de Condorcet
    private static String findCondorcetWinner(List<String> candidates, int numVoters, List<List<String>> ballots) {
        // Initialiser un tableau pour stocker les victoires de chaque candidat contre les autres
        Map<String, Integer> wins = new HashMap<>();
        for (String candidate : candidates) {
            wins.put(candidate, 0);
        }

        // Comparer chaque paire de candidats pour chaque votant
        for (int i = 0; i < numVoters; i++) {
            for (String candidate1 : candidates) {
                for (String candidate2 : candidates) {
                    if (!candidate1.equals(candidate2)) {
                        List<String> currentBallot = ballots.get(i);
                        compareCandidatesAndUpdateWins(candidate1, candidate2, currentBallot, wins);
                    }
                }
            }
        }

        // Affichage des votes de chaque candidat selon les préférences
        for (String cand : candidates) {
            System.out.println("Nombre de votes pour " + cand + ": " + wins.get(cand));
        }
        
        
        // Trouver le candidat avec le plus grand nombre de victoires
        return wins.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Comparer les positions des candidats dans le vote et mettre à jour les victoires
    private static void compareCandidatesAndUpdateWins(String candidate1, String candidate2, List<String> ballot,
                                                       Map<String, Integer> wins) {
        int index1 = ballot.indexOf(candidate1);
        int index2 = ballot.indexOf(candidate2);

        // Si candidate1 est préféré à candidate2, incrémenter winsForCandidate1
        // Sinon, incrémenter winsForCandidate2
        if (index1 < index2) {
            wins.put(candidate1, wins.get(candidate1) + 1);
        } else {
            wins.put(candidate2, wins.get(candidate2) + 1);
        }
    }

     // Fonction pour calculer la liste de préférences globale
     private static List<String> calculateGlobalPreferences(List<String> candidates, int numVoters, List<List<String>> votersPreferences) {
        // Initialiser le tableau des points pour chaque candidat
        Map<String, Integer> points = new HashMap<>();
        for (String candidate : candidates) {
            points.put(candidate, 0);
        }

        // Parcourir les préférences de chaque votant et attribuer des points
        for (List<String> voterPreferences : votersPreferences) {
            for (int i = 0; i < candidates.size(); i++) {
                String candidate = voterPreferences.get(i);
                points.put(candidate, points.get(candidate) + (numVoters - i));
            }
        }

        // Trier les candidats en fonction du nombre de points
        List<String> globalPreferences = new ArrayList<>(candidates);
        globalPreferences.sort((c1, c2) -> Integer.compare(points.get(c2), points.get(c1)));

        return globalPreferences;
    }
}

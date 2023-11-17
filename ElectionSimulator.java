import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectionSimulator {

    public static void main(String[] args) {
        // Exemple avec trois candidats A, B, C et quatre votants
        List<String> candidates = List.of("A", "B", "C");
        int numVoters = 10000;

        // Simuler un scrutin aléatoire
        List<String> randomBallot = simulateRandomVote(candidates);
        System.out.println("Vote aléatoire : " + randomBallot);

        // Calculer le gagnant selon différentes méthodes électorales
        String condorcetWinner = findCondorcetWinner(candidates, numVoters, randomBallot);
        //String alternativeWinner = findAlternativeWinner(candidates, numVoters, randomBallot);

        // Afficher les résultats
        System.out.println("Gagnant selon le critère de Condorcet : " + condorcetWinner);
        //System.out.println("Gagnant selon une méthode alternative : " + alternativeWinner);
    }

    // Simuler un vote aléatoire
    private static List<String> simulateRandomVote(List<String> candidates) {
        List<String> ballot = new ArrayList<>(candidates);
        Collections.shuffle(ballot);
        return ballot;
    }

    // Trouver le gagnant selon le critère de Condorcet
    private static String findCondorcetWinner(List<String> candidates, int numVoters, List<String> ballot) {
        // Initialiser un tableau pour stocker les victoires de chaque candidat contre les autres
        Map<String, Integer> wins = new HashMap<>();
        for (String candidate : candidates) {
            wins.put(candidate, 0);
        }

        // Comparer chaque paire de candidats pour chaque votant
        for (String candidate1 : candidates) {
            for (String candidate2 : candidates) {
                if (!candidate1.equals(candidate2)) { 
                    // Comparer les positions de candidate1 et candidate2 dans le vote
                    int winsForCandidate1 = 0;
                    int winsForCandidate2 = 0;

                    for (int i = 0; i < numVoters; i++) {
                        int index1 = ballot.indexOf(candidate1);
                        int index2 = ballot.indexOf(candidate2);

                        // Si candidate1 est préféré à candidate2, incrémentez winsForCandidate1
                        // Sinon, incrémentez winsForCandidate2
                        if (index1 < index2) {
                            winsForCandidate1++;
                        } else {
                            winsForCandidate2++;
                        }
                    }

                    // Mettre à jour le tableau des victoires
                    if (winsForCandidate1 > winsForCandidate2) {
                        wins.put(candidate1, wins.get(candidate1) + 1);
                    } else {
                        wins.put(candidate2, wins.get(candidate2) + 1);
                    }
                }
            }
        }

        // Affichage des votes de chaque candidat selon les préférences 
        for(String cand : candidates){
            System.out.println("Voici le nombre de vote pour ce candidat "+ cand +": " + wins.get(cand));
        }

        // Trouver le candidat avec le plus grand nombre de victoires
        return wins.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Trouver le gagnant selon une méthode alternative 
    /*private static String findAlternativeWinner(List<String> candidates, int numVoters, List<String> ballot) {
        // Utiliser le vote alternatif pour déterminer le gagnant
        Map<String, Integer> votes = new HashMap<>();

        for (String candidate : candidates) {
            votes.put(candidate, 0);
        }

        for (int i = 0; i < numVoters ; i++) {
            String firstChoice = ballot.get(i);
            votes.put(firstChoice, votes.get(firstChoice) + 1);
        }

        // Trouver le candidat avec le plus grand nombre de votes
        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }*/
    
}

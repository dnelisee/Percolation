/**
 * Demo — classe de démonstration des fonctionnalités du projet Percolation.
 *
 * Chaque méthode statique ci-dessous est un test autonome illustrant une
 * fonctionnalité précise du projet. Elles peuvent être appelées indépendamment
 * depuis main() en commentant/décommentant les lignes souhaitées.
 *
 * Tests disponibles (dans l'ordre de main) :
 *
 *   1. testGridDisplay()        — affichage d'une grille partiellement noircie à la main
 *   2. testRandomShadow()       — noircissement aléatoire et visualisation de la grille
 *   3. testNaivePercolation()   — détection de percolation par DFS récursif (méthode naive)
 *   4. testUnionFindNaive()     — Union-Find version naive (quick-find) sur un petit exemple
 *   5. testUnionFindFast()      — Union-Find version fast (quick-union, arbres non équilibrés)
 *   6. testUnionFindLog()       — Union-Find version log (union par rang + compression de chemin)
 *   7. testFastPercolation()    — détection Union-Find en parcourant les bords
 *   8. testLogPercolation()     — détection O(log n) via les deux nœuds virtuels de bord
 *   9. testMonteCarloSmall()    — estimation Monte-Carlo du seuil sur 10 simulations
 *  10. testMonteCarloTimed()    — comparaison du temps d'exécution sur 100 simulations
 */
public class Demo {

    // =========================================================================
    // Test 1 : affichage de la grille
    // =========================================================================

    /**
     * Initialise la grille, noircit manuellement quelques cases et l'affiche.
     * Vérifie que l'encodage 1D (indice = i*size + j) et l'affichage sont corrects.
     * Résultat attendu : une grille 10×10 avec une diagonale et une colonne noircies.
     */
    static void testGridDisplay() {
        System.out.println("=== Test 1 : affichage de la grille ===");
        Percolation.init();

        // noircit la diagonale principale
        for (int i = 0; i < Percolation.size; i++) {
            Percolation.grid[i * Percolation.size + i] = true;
        }
        // noircit toute la colonne 5
        for (int i = 0; i < Percolation.size; i++) {
            Percolation.grid[i * Percolation.size + 5] = true;
        }

        Percolation.print();
        System.out.println();
    }

    // =========================================================================
    // Test 2 : noircissement aléatoire
    // =========================================================================

    /**
     * Noircit 30 cases au hasard via randomShadow() et affiche la grille résultante.
     * Vérifie que : aucune case n'est noircie deux fois, les indices retournés
     * sont dans [0, length-1], et la propagation Union-Find est appelée à chaque fois.
     */
    static void testRandomShadow() {
        System.out.println("=== Test 2 : noircissement aléatoire (30 cases) ===");
        Percolation.init();

        for (int i = 0; i < 30; i++) {
            int idx = Percolation.randomShadow();
            System.out.print("Case noircie : " + idx + " (ligne " + idx / Percolation.size
                    + ", col " + idx % Percolation.size + ")  ");
        }
        System.out.println("\nGrille après 30 cases noircies :");
        Percolation.print();
        System.out.println();
    }

    // =========================================================================
    // Test 3 : détection naive de percolation (DFS récursif)
    // =========================================================================

    /**
     * Construit manuellement un chemin noir vertical complet (colonne 0, ligne 0 à 9)
     * et vérifie que isNaivePercolation() le détecte correctement.
     * Ensuite, supprime la case centrale pour vérifier qu'il renvoie false.
     * Illustre la détection par DFS récursif sans Union-Find.
     */
    static void testNaivePercolation() {
        System.out.println("=== Test 3 : détection naive de percolation (DFS) ===");
        Percolation.init();

        // construit un chemin vertical sur la colonne 0 (ligne 0 à size-1)
        for (int i = 0; i < Percolation.size; i++) {
            Percolation.grid[i * Percolation.size] = true;
        }

        System.out.println("Chemin vertical complet (colonne 0) :");
        Percolation.print();
        boolean result = Percolation.isNaivePercolation(0); // teste depuis la case (0,0)
        System.out.println("isNaivePercolation(0) = " + result + "  (attendu : true)");

        // brise le chemin en supprimant la case du milieu
        Percolation.grid[5 * Percolation.size] = false;
        result = Percolation.isNaivePercolation(0);
        System.out.println("Après suppression de la case (5,0) :");
        System.out.println("isNaivePercolation(0) = " + result + "  (attendu : false)");
        System.out.println();
    }

    // =========================================================================
    // Test 4 : Union-Find version naive (quick-find)
    // =========================================================================

    /**
     * Teste naiveFind et naiveUnion sur un ensemble de 6 éléments.
     * Effectue plusieurs unions et vérifie que les représentants sont corrects.
     * La version naive maintient directement le représentant dans equiv[],
     * union est en O(n) mais find est en O(1).
     */
    static void testUnionFindNaive() {
        System.out.println("=== Test 4 : Union-Find naive (quick-find) ===");
        UnionFind.init(6); // 6 éléments : 0, 1, 2, 3, 4, 5

        System.out.println("Représentants initiaux (chacun est son propre représentant) :");
        for (int i = 0; i < 6; i++)
            System.out.print(UnionFind.naiveFind(i) + " ");
        System.out.println();

        UnionFind.naiveUnion(0, 1); // {0,1}, {2}, {3}, {4}, {5}
        UnionFind.naiveUnion(2, 3); // {0,1}, {2,3}, {4}, {5}
        UnionFind.naiveUnion(1, 3); // {0,1,2,3}, {4}, {5}

        System.out.println("Après union(0,1), union(2,3), union(1,3) :");
        for (int i = 0; i < 6; i++)
            System.out.print("find(" + i + ")=" + UnionFind.naiveFind(i) + "  ");
        System.out.println();
        System.out.println("0 et 2 même classe ? " + (UnionFind.naiveFind(0) == UnionFind.naiveFind(2))
                + "  (attendu : true)");
        System.out.println("0 et 4 même classe ? " + (UnionFind.naiveFind(0) == UnionFind.naiveFind(4))
                + "  (attendu : false)");
        System.out.println();
    }

    // =========================================================================
    // Test 5 : Union-Find version fast (quick-union)
    // =========================================================================

    /**
     * Teste fastFind et fastUnion sur le même exemple que le test 4.
     * Contrairement à la version naive, equiv[] encode un arbre : find remonte
     * jusqu'à la racine. L'union est plus rapide (O(1) hors find) mais find
     * peut être O(n) si les arbres dégénèrent.
     */
    static void testUnionFindFast() {
        System.out.println("=== Test 5 : Union-Find fast (quick-union, arbres) ===");
        UnionFind.init(6);

        UnionFind.fastUnion(0, 1);
        UnionFind.fastUnion(2, 3);
        UnionFind.fastUnion(1, 3);

        System.out.println("Après union(0,1), union(2,3), union(1,3) :");
        for (int i = 0; i < 6; i++)
            System.out.print("find(" + i + ")=" + UnionFind.fastFind(i) + "  ");
        System.out.println();
        System.out.println("0 et 2 même classe ? " + (UnionFind.fastFind(0) == UnionFind.fastFind(2))
                + "  (attendu : true)");
        System.out.println("0 et 4 même classe ? " + (UnionFind.fastFind(0) == UnionFind.fastFind(4))
                + "  (attendu : false)");
        System.out.println();
    }

    // =========================================================================
    // Test 6 : Union-Find version log (union par rang + compression de chemin)
    // =========================================================================

    /**
     * Teste logFind et logUnion (les versions actuellement actives via find/union).
     * logUnion attache le petit arbre sous le grand (union par rang),
     * logFind compresse les chemins en faisant pointer chaque nœud sur son grand-père.
     * Les deux optimisations ensemble donnent une complexité amortie O(log n).
     * Ce test vérifie aussi que les résultats sont cohérents avec les versions précédentes.
     */
    static void testUnionFindLog() {
        System.out.println("=== Test 6 : Union-Find log (union par rang + compression) ===");
        UnionFind.init(6);

        UnionFind.union(0, 1); // délègue à logUnion
        UnionFind.union(2, 3);
        UnionFind.union(1, 3);

        System.out.println("Après union(0,1), union(2,3), union(1,3) via logUnion :");
        for (int i = 0; i < 6; i++)
            System.out.print("find(" + i + ")=" + UnionFind.find(i) + "  ");
        System.out.println();
        System.out.println("0 et 2 même classe ? " + (UnionFind.find(0) == UnionFind.find(2))
                + "  (attendu : true)");
        System.out.println("0 et 4 même classe ? " + (UnionFind.find(0) == UnionFind.find(4))
                + "  (attendu : false)");
        System.out.println();
    }

    // =========================================================================
    // Test 7 : détection de percolation via Union-Find (isFastPercolation)
    // =========================================================================

    /**
     * Construit le même chemin vertical que le test 3, mais cette fois la détection
     * utilise isFastPercolation() qui exploite Union-Find pour comparer les représentants.
     * Vérifie true sur chemin complet, false après suppression d'une case.
     * Note : propagateUnion() doit être appelé pour chaque case noircie.
     */
    static void testFastPercolation() {
        System.out.println("=== Test 7 : détection percolation via Union-Find (isFastPercolation) ===");
        Percolation.init();

        // noircit et propage les unions pour la colonne 0
        for (int i = 0; i < Percolation.size; i++) {
            int idx = i * Percolation.size;
            Percolation.grid[idx] = true;
            Percolation.propagateUnion(idx);
        }

        boolean result = Percolation.isFastPercolation(0);
        System.out.println("isFastPercolation(0) sur chemin complet = " + result + "  (attendu : true)");

        // brise le chemin et réinitialise
        Percolation.init();
        for (int i = 0; i < Percolation.size; i++) {
            if (i == 5) continue; // trou en ligne 5
            int idx = i * Percolation.size;
            Percolation.grid[idx] = true;
            Percolation.propagateUnion(idx);
        }
        result = Percolation.isFastPercolation(0);
        System.out.println("isFastPercolation(0) avec trou en ligne 5 = " + result + "  (attendu : false)");
        System.out.println();
    }

    // =========================================================================
    // Test 8 : détection O(log n) via les nœuds virtuels (isLogPercolation)
    // =========================================================================

    /**
     * Illustre l'optimisation finale : deux nœuds virtuels (indices length et length+1)
     * représentent respectivement le bord haut et le bord bas de la grille.
     * propagateUnion() les relie automatiquement lors du noircissement des cases de bord.
     * isLogPercolation() se réduit alors à un seul test : find(length) == find(length+1).
     * Ce test construit un chemin complet et vérifie la détection.
     */
    static void testLogPercolation() {
        System.out.println("=== Test 8 : détection O(log n) via nœuds virtuels (isLogPercolation) ===");
        Percolation.init();

        // construit le chemin via randomShadow pour activer propagateUnion correctement
        // on force le chemin vertical en colonne 0
        for (int i = 0; i < Percolation.size; i++) {
            int idx = i * Percolation.size;
            Percolation.grid[idx] = true;
            Percolation.propagateUnion(idx);
        }

        boolean result = Percolation.isLogPercolation();
        System.out.println("isLogPercolation() sur chemin complet = " + result + "  (attendu : true)");

        Percolation.init();
        // chemin incomplet : colonne 0 sauf ligne 5
        for (int i = 0; i < Percolation.size; i++) {
            if (i == 5) continue;
            int idx = i * Percolation.size;
            Percolation.grid[idx] = true;
            Percolation.propagateUnion(idx);
        }
        result = Percolation.isLogPercolation();
        System.out.println("isLogPercolation() avec trou en ligne 5 = " + result + "  (attendu : false)");
        System.out.println();
    }

    // =========================================================================
    // Test 9 : estimation Monte-Carlo (petite)
    // =========================================================================

    /**
     * Effectue 10 simulations Monte-Carlo et affiche l'estimation du seuil de percolation.
     * La valeur théorique pour une grille infinie est ≈ 0.5927.
     * Sur 10 simulations, l'estimation sera approximative (±0.05 typiquement).
     * Ce test sert à vérifier que la boucle complète fonctionne correctement.
     */
    static void testMonteCarloSmall() {
        System.out.println("=== Test 9 : estimation Monte-Carlo (10 simulations) ===");
        Percolation.init();
        double estimation = Percolation.monteCarlo(10);
        System.out.println("Seuil estimé sur 10 simulations : " + estimation);
        System.out.println("Valeur théorique attendue       : ~0.5927");
        System.out.println();
    }

    // =========================================================================
    // Test 10 : comparaison de temps (100 simulations)
    // =========================================================================

    /**
     * Mesure le temps d'exécution de monteCarlo() sur 100 simulations.
     * Affiche l'estimation finale et la durée en millisecondes.
     * Permet d'observer le gain des optimisations Union-Find par rapport
     * à la version naive : sur une grille 10×10 avec 100 simulations,
     * le temps devrait être inférieur à quelques centaines de ms.
     */
    static void testMonteCarloTimed() {
        System.out.println("=== Test 10 : Monte-Carlo chronométré (100 simulations) ===");
        Percolation.init();
        long start = System.currentTimeMillis();
        double estimation = Percolation.monteCarlo(100);
        long duration = System.currentTimeMillis() - start;
        System.out.println("Seuil estimé sur 100 simulations : " + estimation);
        System.out.println("Durée d'exécution                : " + duration + " ms");
        System.out.println();
    }

    // =========================================================================
    // Point d'entrée
    // =========================================================================

    /**
     * Lance tous les tests dans l'ordre.
     * Pour n'exécuter qu'un sous-ensemble, commenter les appels non souhaités.
     */
    public static void main(String[] args) {
        testGridDisplay();
        testRandomShadow();
        testNaivePercolation();
        testUnionFindNaive();
        testUnionFindFast();
        testUnionFindLog();
        testFastPercolation();
        testLogPercolation();
        testMonteCarloSmall();
        testMonteCarloTimed();
    }
}

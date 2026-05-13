/**
 * Percolation — simulation de percolation sur une grille carrée.
 *
 * Modélise une grille N×N de cases blanches ou noires. On noircit des cases
 * aléatoirement jusqu'à ce qu'il existe un chemin de cases noires reliant
 * le bord supérieur au bord inférieur (percolation). La proportion de cases
 * noires à ce moment est le seuil de percolation, estimé par méthode Monte-Carlo.
 *
 * Encodage de la grille :
 *   La grille N×N est aplatie en un tableau 1D de taille N*N.
 *   La case (ligne i, colonne j) correspond à l'indice i*size + j.
 *   grid[k] == true  →  case noire
 *   grid[k] == false →  case blanche
 *
 * Trois méthodes de détection de percolation sont implémentées :
 *   - isNaivePercolation : DFS récursif depuis une case, sans Union-Find.
 *   - isFastPercolation  : utilise Union-Find, parcourt les bords pour vérifier la connexité.
 *   - isLogPercolation   : O(log n) — compare deux nœuds virtuels représentant les bords
 *                          haut (indice length) et bas (indice length+1) de la grille.
 *
 * isPercolation() délègue actuellement à isFastPercolation.
 */
public class Percolation {

    static final int size = 10;          // côté de la grille (N)
    static final int length = size * size; // nombre total de cases (N²)
    static boolean[] grid = new boolean[length]; // grille aplatie : true = noir, false = blanc

    /**
     * Réinitialise la grille (toutes les cases à blanc) et la structure Union-Find.
     * À appeler entre deux simulations dans monteCarlo().
     * Union-Find est initialisé avec length+2 éléments :
     * les indices 0..length-1 sont les cases, length est le nœud virtuel du bord haut,
     * length+1 est le nœud virtuel du bord bas.
     */
    static void init() {
        for (int i = 0; i < length; i++) {
            grid[i] = false;
        }

        // initialise the union structure
        UnionFind.init(length + 2);

    }

    /**
     * Affiche la grille dans la console.
     * 'x' = case noire, '_' = case blanche.
     * Les lignes correspondent aux lignes de la grille (axe i),
     * les colonnes aux colonnes (axe j).
     */
    static void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i * size + j]) {
                    System.out.print("x");
                } else {
                    System.out.print("_");
                }
            }
            System.out.println();
        }
    }

    /**
     * Noircit aléatoirement une case blanche de la grille.
     * Tire des indices au hasard jusqu'à trouver une case blanche (hypothèse :
     * la grille n'est pas trop pleine, donc la recherche est rapide en moyenne).
     * Après avoir noirci la case, propage les unions Union-Find vers ses voisins noirs.
     *
     * @return l'indice de la case noircie
     */
    static int randomShadow() {
        /*
         * 1. On choisit un indice au hasard
         * 2. S'il cette case est blanche alors on la noircit et c'est fini
         * 3. Sinon on recommence jusqu'à ce qu'on trouve une case blanche
         */
        int n;
        while (true) {
            n = (int) ((length - 1) * Math.random());
            if (!grid[n]) {
                grid[n] = true;
                break;
            }
        }

        propagateUnion(n);

        return n;
    }

    /**
     * Détecte récursivement si la case n appartient à un demi-chemin noir
     * vers le bord supérieur (up=true) ou inférieur (up=false) de la grille.
     * Parcours DFS avec mémoïsation dans seen[] pour éviter les cycles.
     *
     * @param seen tableau des cases déjà visitées pendant ce parcours (évite les cycles)
     * @param n    indice de la case courante (supposée noire)
     * @param up   true → cherche vers le haut (ligne 0), false → vers le bas (ligne size-1)
     * @return true si un chemin noir atteint le bord cible depuis n
     */
    static boolean detectPath(boolean[] seen, int n, boolean up) {
        // we will suppose that n is black
        if (!grid[n]) {
            return false;
        }

        int i = n / size; // ligne de n
        int j = n % size; // colonne de n

        // to avoid recursion upon two elements.
        // We make it seen because at the end of detectPath, it will have been seen
        seen[n] = true;

        // we test the elt just ahead n : n - size
        if (up) {
            if (i == 0) {
                return grid[n]; // cas de base : atteint le bord haut
            }
            if (!seen[n - size] && grid[n - size] && detectPath(seen, n - size, up)) {
                return true;
            } else {
                seen[n - size] = true;
            }
        } else {
            if (i == size - 1) {
                return grid[n]; // cas de base : atteint le bord bas
            }
            if (!seen[n + size] && grid[n + size] && detectPath(seen, n + size, up)) {
                return true;
            } else {
                seen[n + size] = true;
            }

        }

        // we test the elt at the right of n : n + 1
        if (j != size - 1) {
            if (!seen[n + 1] && grid[n + 1] && detectPath(seen, n + 1, up)) {
                return true;
            } else {
                seen[n + 1] = true;
            }
        }

        // we test the elt at the left of n : n - 1
        if (j != 0) {
            if (!seen[n - 1] && grid[n - 1] && detectPath(seen, n - 1, up)) {
                return true;
            } else {
                seen[n - 1] = true;
            }
        }

        return false;

    }

    /**
     * Détecte la percolation via DFS récursif (version naive, sans Union-Find).
     * Lance deux demi-parcours depuis n : l'un vers le haut, l'autre vers le bas.
     * Le tableau seen[] est réinitialisé entre les deux pour éviter les faux négatifs
     * (une case utile pour monter peut aussi être utile pour descendre).
     *
     * @param n indice d'une case noire de la ligne 0 (point de départ habituel)
     * @return true s'il existe un chemin noir de la ligne 0 à la ligne size-1 passant par n
     */
    static boolean isNaivePercolation(int n) {
        // we will suppose that n is black
        boolean[] seen = new boolean[length]; // false for all elements
        boolean up = detectPath(seen, n, true);
        // reset the seen array because an elt that could offer a road to up can give a
        // road to down
        seen = new boolean[length];
        boolean down = detectPath(seen, n, false);

        return up && down;
    }

    /**
     * Point d'entrée unique pour le test de percolation depuis une case n.
     * Délègue actuellement à isFastPercolation (Union-Find).
     * Changer l'implémentation appelée ici permet de comparer les performances
     * sans modifier le reste du code.
     */
    static boolean isPercolation(int n) {
        return isFastPercolation(n);
    }

    /**
     * Lance une simulation complète de percolation sur la grille (supposée vide).
     * Phase 1 : noircit des cases au hasard jusqu'à avoir au moins une case noire
     *           sur le bord haut ET une sur le bord bas.
     * Phase 2 : continue de noircir jusqu'à détecter un chemin complet haut→bas.
     *
     * @return la proportion de cases noires au moment où la percolation est atteinte
     */
    static double percolation() {
        // we dark case until there is at least one dark case at the top and at the
        // bottom of the matrix

        /*
         * Tant qu'il n'y a pas au moins une case noir sur le bord du haut et sur le
         * bord du bas,
         * on continue de noircir aléatoirement.
         * 
         * Dès que la condition est respectée, on cherche s'il y a percolation à partir
         * d'un des éléments du haut (ou du bas si on veut)
         * 
         * Dès qu'il y a percolation, on calcul le seuil et on le renvoie
         */

        // ce code cherchant de bas en haut marche très bien
        int n;
        boolean top = false, down = false;
        while (!(top && down)) {
            // System.out.println("coucou, je cherche en haut et en bas...");
            n = randomShadow();
            if (n < size) {
                top = true;  // une case du bord haut est noircie
            } else if (n >= length - size) {
                down = true; // une case du bord bas est noircie
            }
        }

        // System.out.println("fin trouver en haut et en bas");
        // System.out.println("Debut de la recherche de la percolation...");

        boolean perco = false;
        while (!perco) {
            // vérifie chaque case de la ligne 0 : si l'une d'elle percole, c'est terminé
            for (int i = 0; i < size; i++) {
                if (grid[i] && isPercolation(i)) {
                    perco = true;
                    break;
                }
            }

            randomShadow(); // noircit une case supplémentaire si pas encore percolation
        }

        // calcule la proportion de cases noires (seuil de percolation atteint)
        int nb_black = 0;
        for (int i = 0; i < length; i++) {
            if (grid[i]) {
                nb_black++;
            }
        }

        return (double) nb_black / length;
    }

    /**
     * Estime le seuil de percolation par méthode Monte-Carlo :
     * effectue n simulations indépendantes et retourne la moyenne des seuils obtenus.
     * La grille est réinitialisée entre chaque simulation via init().
     *
     * @param n nombre de simulations
     * @return estimation du seuil de percolation (entre 0 et 1)
     */
    static double monteCarlo(int n) {
        init();
        System.out.println("processing ...");
        double estimation = 0;
        for (int i = 0; i < n; i++) {
            estimation += percolation();
            init();
        }

        estimation /= n;

        return estimation;
    }

    /**
     * Met à jour la structure Union-Find après qu'une case x a été noircie.
     * Fusionne la classe de x avec celles de ses voisins noirs (haut, bas, gauche, droite).
     * Si x est sur le bord haut (ligne 0), le relie au nœud virtuel length (bord haut).
     * Si x est sur le bord bas (ligne size-1), le relie au nœud virtuel length+1 (bord bas).
     * Ces deux nœuds virtuels permettent à isLogPercolation() de tester la percolation
     * en un seul appel find().
     *
     * @param x indice de la case venant d'être noircie
     */
    static void propagateUnion(int x) {
        // x est supposé noir
        // ms au cas où :
        grid[x] = true;

        // on relie x à tous ses voisins noirs
        int i = x / size; // ligne de x
        int j = x % size; // colonne de x

        // s'il a un voisin de haut noir
        if (i > 0 && grid[x - size])
            UnionFind.union(x, x - size);

        // s'il a un voisin de bas noir
        if (i < size - 1 && grid[x + size])
            UnionFind.union(x, x + size);

        // s'il a un voisin de gauche noir
        if (j > 0 && grid[x - 1])
            UnionFind.union(x, x - 1);

        // s'il a un voisin de droite noir
        if (j < size - 1 && grid[x + 1])
            UnionFind.union(x, x + 1);

        // modification des classes d'équivalence des deux éléments
        // de bord pour l'optimisation du nbre de recherches
        if (i == 0) {
            UnionFind.union(x, length);   // relie au nœud virtuel bord haut
        }

        if (i == size - 1) {
            UnionFind.union(x, length + 1); // relie au nœud virtuel bord bas
        }
    }

    /**
     * Détecte la percolation via Union-Find en parcourant explicitement les bords.
     * Pour la case n, vérifie si son représentant est identique à celui d'au moins
     * une case du bord haut (indices 0..size-1) ET d'au moins une du bord bas
     * (indices length-size..length-1).
     * Plus efficace que la version naive mais encore linéaire en size.
     *
     * @param n indice d'une case noire à tester
     * @return true si n est connecté à la fois au bord haut et au bord bas
     */
    static boolean isFastPercolation(int n) {
        boolean up = false;
        boolean down = false;

        // on vérifie s'il est relié à un case du haut
        for (int i = 0; i < size; i++) {
            if (UnionFind.find(n) == UnionFind.find(i)) {
                up = true;
                break;
            }
        }

        // on vérifie s'il est relié à une case du bas
        for (int i = length - size; i < length; i++) {
            if (UnionFind.find(n) == UnionFind.find(i)) {
                down = true;
                break;
            }
        }

        return up && down;
    }

    /**
     * Détecte la percolation en O(log n) grâce aux deux nœuds virtuels.
     * Les nœuds length (bord haut) et length+1 (bord bas) sont reliés via
     * propagateUnion() à chaque case noire de leur bord respectif.
     * La percolation est donc équivalente à : find(length) == find(length+1).
     * Pas besoin de connaître la dernière case noircie.
     *
     * @return true si le bord haut et le bord bas sont dans la même classe d'équivalence
     */
    static boolean isLogPercolation() {
        return UnionFind.find(length) == UnionFind.find(length + 1);
    }

    /**
     * Point d'entrée principal.
     * Attend un entier n en argument (nombre de simulations Monte-Carlo).
     * Affiche l'estimation du seuil de percolation et le temps d'exécution.
     * Usage : java Percolation <n>
     */
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        long time = System.currentTimeMillis();
        double estimation = monteCarlo(n);
        time = System.currentTimeMillis() - time;

        System.out.println("Estimation = " + estimation + " and duration = " + time);
    }

}

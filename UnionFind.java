/**
 * UnionFind — structure de données Union-Find (aussi appelée Disjoint Set Union).
 *
 * Représente efficacement des classes d'équivalence sur un ensemble de n éléments
 * entiers (0 à n-1). Deux éléments sont dans la même classe s'ils appartiennent
 * au même chemin noir dans la grille de percolation.
 *
 * Trois niveaux d'implémentation sont fournis, du plus simple au plus efficace :
 *
 *   1. Naive (quick-find) : find en O(1), union en O(n)
 *      → equiv[i] contient directement le représentant de i.
 *
 *   2. Fast (quick-union) : find en O(n) pire cas, union en O(n) pire cas
 *      → equiv définit une forêt d'arbres ; find remonte jusqu'à la racine.
 *
 *   3. Log (union par rang + compression de chemin) : find et union en O(log n)
 *      → logUnion attache le petit arbre sous le grand (via height/rang).
 *      → logFind compresse les chemins en faisant pointer chaque nœud sur son grand-père.
 *
 * Les fonctions find() et union() sont les points d'entrée utilisés par Percolation ;
 * elles délèguent actuellement à logFind / logUnion.
 *
 * Deux éléments virtuels supplémentaires (indices length et length+1) sont ajoutés
 * par Percolation pour représenter la bordure haute et la bordure basse de la grille,
 * ce qui permet de tester la percolation en O(log n) avec un seul appel à find().
 */
public class UnionFind {
    static int[] equiv;  // equiv[i] = père de i dans l'arbre (ou i lui-même si racine)
    static int[] height; // height[i] = hauteur (rang) de l'arbre enraciné en i (utilisé par logUnion)

    /**
     * Initialise la structure pour len éléments.
     * Chaque élément est sa propre classe d'équivalence (singleton) :
     * equiv[i] = i, height[i] = 1.
     * Appelé par Percolation.init() avec len = size*size + 2
     * (les 2 éléments virtuels pour les bords haut et bas).
     */
    static void init(int len) {
        equiv = new int[len];
        height = new int[len];

        for (int i = 0; i < len; i++) {
            equiv[i] = i;
            height[i] = 1;
        }
    }

    // -------------------------------------------------------------------------
    // Version 1 : Naive (quick-find)
    // -------------------------------------------------------------------------

    /**
     * Retourne le représentant canonique de x en O(1).
     * Dans la version naive, equiv[i] contient directement le représentant de i.
     */
    static int naiveFind(int x) {
        return equiv[x];
    }

    /**
     * Fusionne les classes de x et y : tous les membres de la classe de x
     * adoptent le représentant de y. Complexité O(n).
     * Retourne le nouveau représentant commun.
     */
    static int naiveUnion(int x, int y) {
        int ri = naiveFind(x);
        for (int i = 0; i < equiv.length; i++) {
            if (naiveFind(i) == ri) {
                equiv[i] = naiveFind(y);
            }
        }
        return naiveFind(y);
    }

    // -------------------------------------------------------------------------
    // Version 2 : Fast (quick-union, arbre sans optimisation)
    // -------------------------------------------------------------------------

    /**
     * Remonte l'arbre depuis x jusqu'à la racine (le représentant canonique).
     * Complexité O(profondeur de l'arbre), pouvant atteindre O(n) en pire cas.
     */
    static int fastFind(int x) {
        while (x != equiv[x]) {
            x = equiv[x];
        }
        return x;
    }

    /**
     * Fusionne les classes de x et y en rattachant la racine de x à celle de y.
     * Ne tient pas compte des hauteurs : peut déséquilibrer les arbres.
     * Retourne la racine commune.
     */
    static int fastUnion(int x, int y) {
        int temp = find(y);

        x = find(x);
        equiv[x] = temp; 

        return temp;
    }

    // -------------------------------------------------------------------------
    // Version 3 : Log (union par rang + compression de chemin par grand-père)
    // -------------------------------------------------------------------------

    /**
     * Remonte l'arbre depuis x en appliquant la compression de chemin par grand-père :
     * à chaque étape, equiv[x] est court-circuité vers equiv[equiv[x]],
     * ce qui réduit la hauteur des arbres au fil des appels.
     * Complexité amortie O(log n).
     */
    static int logFind(int x) {

        while (x != equiv[x]) {
            equiv[x] = equiv[equiv[x]]; // compression : x pointe sur son grand-père
            x = equiv[x];
        }
        return x;

    }

    /**
     * Fusionne les classes de x et y en attachant l'arbre de moindre hauteur
     * sous la racine de l'arbre de plus grande hauteur (union par rang).
     * Cela borne la hauteur des arbres à O(log n).
     * Note : les hauteurs sont des rangs approximatifs (non recalculés après compression).
     * Retourne la nouvelle racine commune.
     */
    static int logUnion(int x, int y) {

        // we harmonise the code by make that we 
        // always have height[three(x)] < height[three(y)] 
        if(height[x] > height[y]) {
            int temp = y; 
            y = x; 
            x = temp; 
        } 

        int i = find(x), j = find(y);
        equiv[i] = j;

        return j;

    }

    // -------------------------------------------------------------------------
    // Points d'entrée utilisés par Percolation (délèguent à la version log)
    // -------------------------------------------------------------------------

    /** Retourne le représentant canonique de x (version log, O(log n) amorti). */
    static int find(int x) {
        return logFind(x);
    }

    /** Fusionne les classes de x et y (version log, O(log n) amorti). */
    static int union(int x, int y) {
        return logUnion(x, y);
    }

}

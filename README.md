# Percolation Threshold — Union-Find

Estimation du seuil de percolation sur une grille carrée par méthode Monte-Carlo, avec une structure de données Union-Find optimisée.

---

## Concept

On considère une grille N×N de cases initialement blanches. Des cases sont noircies aléatoirement une à une. La **percolation** est atteinte dès qu'il existe un chemin de cases noires reliant le bord supérieur au bord inférieur (sans diagonales). La proportion de cases noires à ce moment est le **seuil de percolation**, dont la valeur théorique pour une grille infinie est **≈ 0.5927**.

---

## Algorithme

**Phase 1 — Noircissement aléatoire**

À chaque étape, une case blanche est choisie uniformément au hasard et noircie. La structure Union-Find est mise à jour immédiatement pour fusionner la case avec ses voisins noirs.

**Phase 2 — Détection de la percolation**

La percolation est testée après chaque noircissement. Trois méthodes de détection sont implémentées, du moins efficace au plus efficace :

| Méthode | Principe | Complexité |
|---|---|---|
| `isNaivePercolation` | DFS récursif depuis une case du bord haut | O(N²) par appel |
| `isFastPercolation` | Union-Find, parcours explicite des bords | O(N · log N) par appel |
| `isLogPercolation` | Deux nœuds virtuels de bord, un seul `find()` | O(log N) par appel |

**Phase 3 — Monte-Carlo**

La simulation est répétée n fois ; la moyenne des seuils obtenus est retournée comme estimation.

---

## Structure Union-Find

Trois niveaux d'implémentation cohabitent dans `UnionFind.java` :

| Version | `find` | `union` | Mécanisme |
|---|---|---|---|
| Naive (quick-find) | O(1) | O(n) | `equiv[i]` = représentant direct |
| Fast (quick-union) | O(n) pire cas | O(n) pire cas | Arbres non équilibrés |
| Log (active) | O(log n) amorti | O(log n) amorti | Union par rang + compression de chemin par grand-père |

Les fonctions `find()` et `union()` délèguent à la version log.

Deux **nœuds virtuels** supplémentaires (indices `length` et `length+1`) représentent les bords haut et bas de la grille. Chaque case noircie sur un bord est automatiquement fusionnée avec le nœud correspondant, ce qui réduit le test de percolation à une seule comparaison : `find(length) == find(length+1)`.

---

## Structure du projet

```
.
├── bin/               — classes compilées (.class), généré par javac
├── Percolation.java   — grille, noircissement, détection, Monte-Carlo
├── UnionFind.java     — structure Union-Find (3 versions)
└── Demo.java          — démonstration commentée de toutes les fonctionnalités
```

---

## Compilation

```bash
javac -d bin *.java 
```

---

## Exécution

### Programme principal — estimation Monte-Carlo

Lance `n` simulations et affiche l'estimation du seuil et le temps d'exécution.

```bash
java -cp bin Percolation <n>
```

Exemple :

```bash
java -cp bin Percolation 100
# Estimation = 0.5931 and duration = 212 ms
```

### Classe Demo — démonstration des fonctionnalités

Lance les 10 tests dans l'ordre. Aucun argument requis.

```bash
java -cp bin Demo
```

Les tests exécutés sont les suivants :

| # | Méthode | Ce qui est testé |
|---|---|---|
| 1 | `testGridDisplay()` | Initialisation et affichage de la grille avec cases noircies manuellement |
| 2 | `testRandomShadow()` | Noircissement aléatoire de 30 cases et affichage de la grille résultante |
| 3 | `testNaivePercolation()` | Détection DFS sur chemin complet, puis vérification du false après suppression d'une case |
| 4 | `testUnionFindNaive()` | Union-Find naive (quick-find) : unions et vérification des représentants |
| 5 | `testUnionFindFast()` | Union-Find fast (quick-union, arbres non équilibrés) sur le même exemple |
| 6 | `testUnionFindLog()` | Union-Find log (union par rang + compression) : vérification de cohérence |
| 7 | `testFastPercolation()` | Détection via Union-Find en parcourant les bords (true et false vérifiés) |
| 8 | `testLogPercolation()` | Détection O(log n) via nœuds virtuels de bord (true et false vérifiés) |
| 9 | `testMonteCarloSmall()` | Estimation Monte-Carlo sur 10 simulations, comparée à la valeur théorique ≈ 0.5927 |
| 10 | `testMonteCarloTimed()` | Même estimation sur 100 simulations avec mesure du temps d'exécution |

Pour n'exécuter qu'un sous-ensemble des tests, commenter les appels non souhaités dans `Demo.main()`.

---

## Paramètre clé

La taille de la grille est définie par la constante `Percolation.size` (par défaut 10). La modifier suffit pour tester sur des grilles plus grandes ; le seuil estimé converge vers ≈ 0.5927 à mesure que la taille augmente.

---

## Contexte

Développé dans le cadre du cours **INF371** à l'École Polytechnique. 

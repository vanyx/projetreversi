package reversi;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**********INFOS**********/

// Le projet a été réalisé par Thomas BENALOUANE et Fabien LE MORZADEC
// Nous avons traité toutes les questions, à l'exception de celle du niveau 3 de l'IA


/*************************/

public class jeu {

    //déclaration des variables globales
    static int[][] plateau;
    static int taille;
    static int joueur;
    static boolean passe;
    static int[][] save; //plateau de sauvegarde
    static boolean aSauvgardee = false; //bouleen indiquant si le joueur a sauvegardé
    static boolean bot; //bouleen indiquant si l'ordinateur joue
    static int niveauIA; //entier indiquant le niveau de l'IA choisit

    /************************ Partie 1 ************************/
    public static boolean init(int taille, boolean regle) {
        joueur = 1; //c'est au joueur 1 de placer le premier pion
        passe = false; //le joueur précédent n'a pas passé son tour

        if (taille % 2 != 0 || taille < 0) //si la taille n'est pas paire et >0, pas de cration de plateau
            return false;
        else {
            plateau = new int[taille][taille]; //initialisation du plateau de taille "taille"
            jeu.taille = taille;
            if (regle == false) { //placement "croisé" au centre
                plateau[taille / 2 - 1][taille / 2 - 1] = 1;
                plateau[taille / 2][taille / 2 - 1] = 2;
                plateau[taille / 2][taille / 2] = 1;
                plateau[taille / 2 - 1][taille / 2] = 2;
            } else if (regle == true) { //placement "par paire" au centre
                plateau[taille / 2 - 1][taille / 2 - 1] = 1;
                plateau[taille / 2][taille / 2 - 1] = 1;
                plateau[taille / 2][taille / 2] = 2;
                plateau[taille / 2 - 1][taille / 2] = 2;
            }
            return true;
        }
    }

    public static boolean caseCorrecte(int i, int j) { //verification que la case de coordonnées (i,j) existe
        if (i >= 0 && i < taille && j >= 0 && j < taille)
            return true;
        return false;
    }

    public static boolean caseCorrecte(int i) { //verification que la case i existe
        if (i >= 0 && i < taille * taille)
            return true;
        return false;
    }

    public static int conversion2D1D(int ligne, int colonne) { //conversion de coordonnées à un numero de case
        int caseNumber = -1;

        if (caseCorrecte(ligne, colonne)) { //si la case (i,j) existe, alors on l'associe au numero de case correspondant
            caseNumber = ligne * jeu.plateau.length + colonne;
        }
        if (caseCorrecte(caseNumber)) { //on re-verifie que la case existe
            return caseNumber;
        }
        return caseNumber;
    }

    public static int conversion1DLigne(int numero) { //donne l'indice de ligne d'une case
        int indice = -1;
        if (caseCorrecte(numero)) {
            indice = numero / taille;
        }
        return indice;
    }

    public static int conversion1DColonne(int numero) { //donne l'indice de colonne d'une case
        int indice = -1;
        if (caseCorrecte(numero)) {
            indice = numero % taille;
        }
        return indice;
    }

    public static void affiche(int[] casesSurlignees) { //permet d'afficher la grille
        int chiffre = 0;
        char lettre = 'A';
        int index = 0; //index permet de savoir si un # a été affiché

        if (casesSurlignees.length == 0) index = -1;

        //on affiche la premiere ligne de lettres
        System.out.print("  |");
        for (int i = 0; i < taille; i++) {
            if (lettre == 91)
                lettre = 97; //condition si on depasse les majuscules pour aller directement aux minuscules
            System.out.print(lettre + "|");
            lettre++; //on passe à la lettre suivante
        }
        System.out.println();

        //on parcourt plateau pour l'afficher
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (j == 0) { //on affiche la colonne de chiffres
                    if (i <= 9) System.out.print(" ");//on affiche un espace pour aligner les colonnes avant 10
                    System.out.print(chiffre + "|");
                    chiffre++;
                }
                index = -1;
                //on affiche un # si la case est dans casesSurlignees
                for (int k = 0; k < casesSurlignees.length; k++) {
                    if (casesSurlignees[k] == conversion2D1D(i, j) && plateau[i][j] == 0) {
                        index = 1;
                        System.out.print("#|");
                        break;//on sort dès que un # est affiche
                    }
                }
                //si la case vaut 1, on affiche X (correspondant à une case du joueur 1)
                if (plateau[i][j] == 1 && index == -1) {
                    System.out.print("X|");
                }
                //si la case vaut 2, on affiche O (correspondant à une case du joueur 2)
                if (plateau[i][j] == 2 && index == -1) {
                    System.out.print("O|");
                }
                //si la case vaut 0, on affiche un espace
                if (plateau[i][j] == 0 && index == -1) {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }

    public static void affiche() { //affiche le plateau sans cases surlignées
        int[] casesSurLignees = {};
        affiche(casesSurLignees);
    }

    public static int score() { //affiche le nombre de pion que possède chaque joueur, et retourne celui qui en a le plus
        int compteur1 = 0; //compte les pions du joueur 1
        int compteur2 = 0; //compte les pions du joueur 2
        //parcours le plateau et incremente les compteurs respectifs à chaque pion
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                if (plateau[i][j] == 1) compteur1++;
                if (plateau[i][j] == 2) compteur2++;
            }
        }
        //affiche les scores
        System.out.println("joueur 1 |" + compteur1 + "|" + compteur2 + "| joueur 2");

        //on retourne le joueur qui a le plus de pions
        if (compteur1 > compteur2) return 1;
        else if (compteur2 > compteur1) return 2;
        else return 0;
    }

    public static int[][] sauvegarde() { //retourne une copie du plateau
        save = new int[taille][taille]; //creation d'un nouveau plateau de meme taille
        for (int i = 0; i < save.length; i++) {
            for (int j = 0; j < save[i].length; j++) {
                save[i][j] = plateau[i][j]; //recopie du plateau
            }
        }
        return save;
    }

    public static void charge(int[][] plateau, int joueur, boolean passe) {
        jeu.plateau = plateau;
        jeu.joueur = joueur;
        jeu.passe = passe;
    }

    /************************ Partie 2 ************************/

    //les fonctions puissance et conversion sont utilisées pour convertir un nombre d'une chaine de caractères en entier
    public static double puissance(double a, int e) {
        if (e == 0) return 1;
        return puissance(a, e - 1) * a;
    }

    public static int conversion(String s) {
        int a = 0;
        for (int i = 0; i < s.length(); i++) { //on utilise la décompostion decimale
            a += (s.charAt(i) - 48) * puissance(10, s.length() - 1 - i);
        }
        return a;
    }

    //on utilise ici les codes ASCII pour referencer les lettres et les chiffres d'une chaine de caracteres
    public static boolean verifierFormat(String input) {
        String colonne = ""; //chaine de caractères créée pour les indices de colonne
        String ligne = ""; //chaine de caractères créée pour les indices de ligne
        boolean lettre = false; //utilisé par la suite pour detecter quand il y la premiere lettre
        boolean correct = true; //utilisé pour effectuer des operations si la chaine est correcte

        //on verifie que le format est bon, cad qu'après un ou plusieurs chiffres il n'y ai que des lettres
        if (input.charAt(0) >= 48 && input.charAt(0) <= 57) {//on verifie que les coordonnees de input commencent par un chiffre
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) >= 65 && input.charAt(i) <= 90 || input.charAt(i) >= 97 && input.charAt(i) <= 122)
                    lettre = true; //si une lettre est detectée, alors "lettre" est vrai
                //si il y a eu detection de lettre, et que le caractère suivant est un chiffre, alors le format n'est pas bon
                if (lettre == true && input.charAt(i) >= 48 && input.charAt(i) <= 57) correct = false;
            }
        } else
            correct = false; //si les coordonnées de input ne commencent pas par un chiffre alors le format n'est pas bon

        if (correct == true) {
            //on atribue les chiffres à colonne et les lettres à ligne
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) >= 48 && input.charAt(i) <= 57) ligne += input.charAt(i);
                else colonne += input.charAt(i);
            }
            if (colonne.length() > 1) correct = false;//si il y a plusieurs lettres alors le format n'est pas correcte

            if (conversion(ligne) >= taille)
                correct = false;//si l'indice de ligne n'est pas dans le plateau alors correct vaut false

            //on regarde, si c'est une majuscule, si elle est dans le tableau
            if (correct == true) {
                if (colonne.charAt(0) < 97) {
                    if (colonne.charAt(0) - 64 <= taille) correct = true;

                    else correct = false;
                }
                //on regarde, si c'est une minuscule, si elle est dans le tableau
                if (colonne.charAt(0) >= 97) {
                    if (colonne.charAt(0) - 96 + 26 <= taille) correct = true;
                    else correct = false;
                }
            }
        }
        return correct; //on retourne la variable apres avoir passé tout les tests
    }

    public static int conversionChaineNumero(String input) {
        int caseNumber = -1; //initialisation à -1 par défaut
        String colonne = "";//chaine de caractère corespondant à l'indice lettre
        int colonne2 = 0;//valeur de cette indice en chiffre
        String ligne = "";//chaine de caractere à l'indice "chiffre" de la ligne

        //on regarde si le format est bon
        if (verifierFormat(input) == false) return -1;
            //on part du principe que le format est valide, et que la case existe
        else {
            //on parcourt le input et on associe les chiffres à "ligne" et les lettres à "colonne"
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) >= 48 && input.charAt(i) <= 57) ligne += input.charAt(i);
                else colonne += input.charAt(i);
            }
            //on convertit la lettre si c'est une majuscule en son indice, qu'on atribue à colonne2
            if (colonne.charAt(0) < 97) colonne2 = colonne.charAt(0) - 65;
                //on convertit la lettre si c'est une minuscule
            else if (colonne.charAt(0) >= 97) colonne2 = colonne.charAt(0) - 97 + 26;
            //on convertit les coordonnees en un numero de case
            caseNumber = conversion2D1D(conversion(ligne), colonne2);
            //dernier test: on verifie une derniere fois que la case existe
            if (caseCorrecte(caseNumber) == false) caseNumber = -1;

            return caseNumber;
        }
    }

    public static boolean contientPiece(int numero) {
        //s'il n'y a pas de pion sur la case concernée alors on retourne false
        if (plateau[conversion1DLigne(numero)][conversion1DColonne(numero)] == 0)
            return false;
        else return true;  //sinon on retourne true
    }

    public static void retournePiece(int numero) {
        if (contientPiece(numero) == true) { //test qu'il y ai bien une piece sur la case concernee.

            if (plateau[conversion1DLigne(numero)][conversion1DColonne(numero)] == 1)
                plateau[conversion1DLigne(numero)][conversion1DColonne(numero)] = 2;
            else if (plateau[conversion1DLigne(numero)][conversion1DColonne(numero)] == 2)
                plateau[conversion1DLigne(numero)][conversion1DColonne(numero)] = 1;
        }
    }

    //fonction qui permet d'ajouter une valeur à un tableau
    public static int[] push(int[] t, int a) {
        int[] t2 = new int[t.length + 1];
        for (int i = 0; i < t.length; i++) {//on recopie le tableau
            t2[i] = t[i];
        }
        t2[t.length] = a;//on attribue la dernière valeur
        return t2;
    }

    public static int[] retourneDir(int joueur, int numero, String direction) {
        int adversaire = 0;
        if (joueur == 1) adversaire = 2; //on crée une variable de valeur du joueur adverse
        if (joueur == 2) adversaire = 1;

        boolean valide = false;
        int[] tabVide = {};
        int[] cases = {}; //tableau qui va contenir les cases possiblement retournables
        //on fonctionne ici dans un repere
        int abscisse = conversion1DColonne(numero);
        int ordonnee = conversion1DLigne(numero);

        int x = 0; //"coeficient de direction" en abscisse
        int y = 0; //"coeficient de direction" en ordonnée
        if (direction.contains("N")) y = -1;
        if (direction.contains("S")) y = 1;
        if (direction.contains("E")) x = 1;
        if (direction.contains("O")) x = -1;

        //condition pour demarrer: on regarde si la case suivante appartient à l'adversaire
        if (caseCorrecte(ordonnee + y, abscisse + x) == true && plateau[ordonnee + y][abscisse + x] == adversaire) {
            valide = true;
        }
        while (valide == true) {
            if (caseCorrecte(ordonnee + y, abscisse + x) == false) {//si la case suivante n'existe pas, rien n'est retournable
                valide = false;
                return tabVide;
            } else {
                abscisse += x;
                ordonnee += y;
                //si la case est adverse alors on l'ajoute au tableau
                if (plateau[ordonnee][abscisse] == adversaire) {
                    cases = push(cases, conversion2D1D(ordonnee, abscisse));
                } else { // sinon: si elle est a nous on retourne le tableau, si elle est vide on ne retourne rien
                    if (plateau[ordonnee][abscisse] == joueur) {
                        if (cases.length == 0) {
                            return tabVide;
                        } else return cases;
                    } else if (plateau[ordonnee][abscisse] == 0) return tabVide;
                }
            }
        }
        return tabVide;
    }

    public static int[] possibleCoups() {
        int[] cases = {}; //tableau des coups possibles
        String[] directions = {"N", "NE", "E", "SE", "S", "SO", "O", "NO"};
        int indice = -1; //indice permet de savoir si la case est jouable
        //on parcourt le plateau
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                indice = -1;
                //si la case est vide, on test toutes les directions pour voir si elle est jouable
                if (plateau[i][j] == 0) {
                    for (int k = 0; k < directions.length; k++) {
                        if (retourneDir(joueur, conversion2D1D(i, j), directions[k]).length > 0)
                            indice++;
                    }
                }
                //si la case est jouable, on l'ajoute à cases
                if (indice != -1)
                    cases = push(cases, conversion2D1D(i, j));
            }
        }
        return cases;
    }

    /**
     * Joue un coup à la case donné si celui-ci est valide
     *
     * @param numero Numéro de la case
     * @return Si le coup est joué ou non
     */
    public static boolean joueCoup(int numero) {
        int valide = -1;
        String[] directions = {"N", "NE", "E", "SE", "S", "SO", "O", "NO"};
        int[] possible = possibleCoups();
        //on parcours le tableau des coups possibles et on regarde si numero est dedans
        for (int i = 0; i < possible.length; i++) {
            if (numero == possible[i]) valide = 1;
        }
        //s'il est dedans:
        if (valide != -1) {
            //on joue la case "numero"
            plateau[conversion1DLigne(numero)][conversion1DColonne(numero)] = joueur;
            //on retourne les pieces retournables
            for (int i = 0; i < directions.length; i++) {
                int[] t2 = retourneDir(joueur, numero, directions[i]);
                if (t2.length != 0) {
                    for (int j = 0; j < t2.length; j++) {
                        plateau[conversion1DLigne(t2[j])][conversion1DColonne(t2[j])] = joueur;
                    }
                }
            }
            return true;
        } else return false;
    }

    public static void aide(int version) {
        if (version == 0) {//on affiche tout les coups possibles
            int[] t = possibleCoups();
            affiche(t);
        } else if (version == 1) {//on affiche le coups qu'aurait joué l'IA de niveau 1
            int numero = premierIA(possibleCoups());
            int[] t = {numero};
            affiche(t);
        } else if (version == 2) {//on affiche le coups qu'aurait joué l'IA de niveau 2
            int numero = deuxiemeIA(possibleCoups());
            int[] t = {numero};
            affiche(t);
        }
    }

    /************************ Partie 3 ************************/
    //fonction qui permet de changer le joueur courant
    public static void changeJoueur() {
        if (joueur == 1) joueur = 2;
        else if (joueur == 2) joueur = 1;
    }

    //foction qui retourne le gagnant avec la regle true
    public static int scoreRegleTrue() {
        int winner = 0;
        int compteur1 = 0; //compte les pions du joueur 1
        int compteur2 = 0; //compte les pions du joueur 2
        //parcours le plateau et incrémente les compteurs respectifs à chaque pion
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                if (plateau[i][j] == 1) compteur1++;
                if (plateau[i][j] == 2) compteur2++;
            }
        }
        //on regarde si il y a un gagnant
        if (compteur1 > compteur2) {
            if (compteur1 - compteur2 < 5) winner = 0;
            else winner = 1;
        } else if (compteur2 > compteur1) {
            if (compteur2 - compteur1 < 5) winner = 0;
            else winner = 2;
        } else if (compteur1 == compteur2) {
            winner = 0;
        }
        return winner;
    }

    public static void winner(boolean regle) {//fonction permettant d'afficher le gagnant
        //on affiche le gagnant en fonction de la règle
        if (regle == false) {
            if (score() == 0) System.out.println("Egalité");
            else System.out.println("Le gagant est le joueur " + score());
        } else if (regle == true) {
            if (scoreRegleTrue() == 0) System.out.println("Egalité");
            else System.out.println("Le gagnant est le joueur  " + scoreRegleTrue());
        }
    }

    public static void jeuBoucle(boolean regle) {

        boolean play = true; //permet de savoir si on peut toujours jouer
        boolean instruction = false; //variable utilisée pour savoir si le joueur a rentrée une instruction
        boolean passeCourant = false; //variable utilisée pour savoir si le joueur courant a passé son tour
        boolean save2 = false; //utilisée pour savoir si le joueur a bien rentrée après 'save' une instruction valide
        int version = 0;

        while (play == true) {//tant que l'on peut jouer
            passeCourant = false;
            affiche();//on affiche le plateau avant chaque coup
            System.out.println("joueur courant : " + joueur);
            if (passe == true && possibleCoups().length == 0) { //condition de fin de partie
                score();//on affiche les scores
                winner(regle);//on affiche le gagnant
                System.out.println("Fin du jeu, retour au menu");
                menu(); // on retourne au menu
                play = false;
                return;
            } else if (possibleCoups().length == 0) { //si le joueur n'a pas de coup possible, il passe son tour
                System.out.println("Pas de coups possible, le joueur passe son tour");
                passe = true;//indique pour le prochain tour qu'il a passé son tour
                changeJoueur();//on change le joueur courant
            } else if (possibleCoups().length > 0) { //si le joueur a des coups possibles
                score();//on affiche le score

                if (joueur == 2 && bot == true) {//si le joueur joue contre l'IA et que c'est au tour de L'IA
                    int numero;
                    //en fonction du niveau de l'IA selectioné, l'IA choisit un numero
                    if (niveauIA == 1) {
                        numero = premierIA(possibleCoups());
                    } else {
                        numero = deuxiemeIA(possibleCoups());
                    }
                    joueCoup(numero);//on joue le coup choisit par l'IA
                    passe = false;
                    changeJoueur();//on change le joueur courant
                    System.out.println("L'ordinateur vient de jouer");

                } else {//si c'est au joueur de jouer, ou si l'IA ne joue pas
                    //on affiche les instructions et on indique au joueur qu'il peut directement jouer
                    if (regle == false)
                        System.out.println("Entrer une instruction (save, help, exit) ou des coordonnées");
                    if (regle == true)
                        System.out.println("Entrer une instruction (save, help, pass, exit) ou des coordonnées");

                    Scanner sc = new Scanner(System.in);//le joueur rentre une commande
                    String s = sc.next();
                    if (s.equalsIgnoreCase("help")) {//si il demande de l'aide
                        System.out.println("Entrer une version:");
                        System.out.println("* Version 0 : affiche tout les coups possibles");
                        System.out.println("* Version 1 ou 2 : affiche le coup que jouerait l'IA du même niveau");
                        while (instruction == false) {
                            Scanner sc2 = new Scanner(System.in);
                            int s2 = sc.nextInt();
                            if (s2 == 0) {
                                version = 0;
                                instruction = true;
                            } else if (s2 == 1) {
                                version = 1;
                                instruction = true;
                            } else if (s2 == 2) {
                                version = 2;
                                instruction = true;
                            } else System.out.println("Entrer une version valide");
                        }
                        aide(version);//on affiche l'aide demandée

                    } else if (s.equalsIgnoreCase("save")) {//si il veut sauvegarder
                        save = sauvegarde();//on créé une sauvegarde
                        aSauvgardee = true;//on indique qu'une sauvegarde existe (utile dans la fonction menu)
                        instruction = true;
                        while (save2 == false) {
                            System.out.println("A present voulez-vous quitter ?");
                            Scanner sc2 = new Scanner(System.in);
                            String s2 = sc.next();
                            if (s2.equalsIgnoreCase("non")) {
                                save2 = true;
                                instruction = true;
                            } else if (s2.equalsIgnoreCase("oui")) {//si il veut quitter la partie s'arrete
                                save2 = true;
                                score();
                                winner(regle);//on affiche le gagnant
                                System.out.println("Fin du jeu, retour au menu");
                                menu();//on retourne au menu
                                return;
                            }
                        }
                        save2 = false;

                    } else if (s.equalsIgnoreCase("exit")) {//si il veut quitter la partie s'arrete
                        score();
                        winner(regle);
                        System.out.println("Fin du jeu, retour au menu");
                        play = false;
                        menu();
                        return;

                    } else if (s.equalsIgnoreCase("pass") && regle == true) {
                        if (passe == true) { //si le joueur precedent a passé son tour, la partie s'arrete
                            score();
                            winner(regle);
                            System.out.println("Fin du jeu, retour au menu");
                            menu();
                            play = false;
                            return;

                        } else if (passe == false) {
                            passe = true;//indique pour le prochain tour qu'il a passé son tour
                            changeJoueur();//on change le joueur courant
                            passeCourant = true;//on indique que le joueur courant a passé son tour
                        }
                        instruction = true;
                    }

                    if (passeCourant == false) {//si le joueur courant n'a pas passé son tour alors il peut jouer
                        int numero;
                        //si le joueur a rentré precedemment une instruction alors il doit rentrer des coordonnées
                        if (instruction == true) {
                            System.out.println("Entrer des coordonnées :");
                            Scanner sc2 = new Scanner(System.in);
                            String s2 = sc2.next();
                            numero = conversionChaineNumero(s2);
                        }
                        //sinon, on recupere ce qu'il avait rentré precedemment
                        else numero = conversionChaineNumero(s);

                        boolean here = false; //variable verifiant que la case est parmis les coups possibles
                        for (int i = 0; i < possibleCoups().length; i++) {
                            if (numero == possibleCoups()[i]) {
                                here = true;//on verifie que le coup est possible
                            }
                        }
                        while (here == false) {//tant qu'elle n'est pas la on re-demande au joueur des coordonnées
                            System.out.println("Entrer des coordonnées valides :");
                            Scanner sc2 = new Scanner(System.in);
                            String s2 = sc2.next();
                            numero = conversionChaineNumero(s2);
                            for (int i = 0; i < possibleCoups().length; i++) {
                                if (numero == possibleCoups()[i]) {
                                    here = true;
                                }
                            }
                        }

                        joueCoup(numero);//enfin on joue le coup
                        passe = false;//on indique que le joueur n'a pas passé son tour
                        changeJoueur();//et on change le joueur courant

                    }
                    instruction = false;//on re-met instruction à false
                }
            }
        }
    }

    public static void menu() {
        int taille = 0;//taille du plateau
        boolean reglePlateau = false;//règle de plateau (initialisation du positionnement)
        boolean regleJeu = false;//règle du jeu
        boolean instruction = false;//permet de savoir que le joueur a rentré une instruction valide
        boolean nouvellePartie = false;//permet de savoir si le joueur veut commencer une nouvelle partie
        boolean charger = false;//permet de savoir si le joueur veut charger une partie existante

        while (instruction == false) {//tant que l'instruction n'est pas valide
            System.out.println("* Pour commencer une nouvelle partie, entrer 'start'");
            System.out.println("* Pour poursuivre une partie à partir d'une sauvegarde, entrer 'charger'");
            System.out.println("* Pour quitter, entrer 'fin'");
            Scanner sc = new Scanner(System.in);
            String s = sc.next();
            if (s.equalsIgnoreCase("start")) {
                nouvellePartie = true;
                instruction = true;
            } else if (s.equalsIgnoreCase("charger")) {
                charger = true;
                instruction = true;
            } else if (s.equalsIgnoreCase("fin")) {
                return;
            }
        }
        instruction = false;

        while (instruction == false) {//tant que l'instruction n'est pas valide
            System.out.println("Voulez-vous jouer contre l'ordinateur ?");
            Scanner sc = new Scanner(System.in);
            String s = sc.next();
            if (s.equalsIgnoreCase("oui")) {
                bot = true;//permet de savoir que le joueur veut jouer contre l'ordinateur
                while (instruction == false) {//tant que l'instruction n'est pas valide
                    System.out.println("Veuillez selectionner un niveau de difficulté :");
                    System.out.println("* Niveau 1: l'ordinateur joue au hasard (taper '1')");
                    System.out.println("* Niveau 2: l'ordinateur joue la case qui retourne le plus de pièces (taper '2')");
                    Scanner sc2 = new Scanner(System.in);
                    int s2 = sc2.nextInt();
                    if (s2 == 1) {
                        niveauIA = 1;
                        instruction = true;
                    } else if (s2 == 2) {
                        niveauIA = 2;
                        instruction = true;
                    }
                }
            } else if (s.equalsIgnoreCase("non")) {
                bot = false;
                instruction = true;
            }
        }
        instruction = false;

        if (nouvellePartie == true) {//si le joueur veut commencer une nouvelle partie
            while (instruction == false) {//selection de la taille du nouveau plateau
                System.out.println("Entrer une taille de plateau");
                Scanner sc = new Scanner(System.in);
                int s = sc.nextInt();
                if (s % 2 == 0 && 2 <= s && s <= 52) {
                    instruction = true;
                    taille = s;
                } else {
                    System.out.println("La taille doit etre paire, et inferieure ou égale à 52");
                }
            }
            instruction = false;

            while (instruction == false) {//selection de la règle du plateau
                System.out.println("Entrer la règle de plateau:");
                System.out.println("* true:  pour l'initialisation en 'colonne'");
                System.out.println("* false: pour l'initialisation 'croisée'");
                Scanner sc = new Scanner(System.in);
                String s = sc.next();
                if (s.equalsIgnoreCase("true")) {
                    instruction = true;
                    reglePlateau = true;
                } else if (s.equalsIgnoreCase("false")) {
                    instruction = true;
                    reglePlateau = false;
                }
            }
            instruction = false;

            while (instruction == false) {//selection de la règle du jeu
                System.out.println("Entrer la règle du jeu:");
                System.out.println("* true:  - le joueur peut passer son tour");
                System.out.println("         - il y a un gagnant si la difference des scores est superieure à 5");
                System.out.println("* false: - le joueur ne peut pas passer son tour");
                System.out.println("         - le gagnant est le joueur ayant le plus de pions");
                Scanner sc = new Scanner(System.in);
                String s = sc.next();
                if (s.equalsIgnoreCase("true")) {
                    instruction = true;
                    regleJeu = true;
                } else if (s.equalsIgnoreCase("false")) {
                    instruction = true;
                    regleJeu = false;
                }
            }
            init(taille, reglePlateau);//on créé le plateau avec les paramètres rentrés par le joueur
            jeuBoucle(regleJeu);

        } else if (charger == true) {//si le joueur veut jouer à partir d'une sauvegarde
            if (aSauvgardee == true) {//si une sauvegarde existe
                plateau = save;//on joue à partir de la sauvegarde
                jeuBoucle(regleJeu);
            } else {//sinon on affiche qu'il n'existe pas de sauvegarde
                System.out.println("Pas de sauvegarde existante");
                menu();
            }
        }
    }

    /************************ Partie 4 ************************/
    public static int entierAleatoire(int a, int b) {
        //retourne un entier aléatoire entre a (inclus) et b (inclus)
        return ThreadLocalRandom.current().nextInt(a, b + 1);
    }

    public static int premierIA(int[] coup) {
        //on retourne au hasard une case parmis les coups possibles
        return coup[entierAleatoire(0, coup.length - 1)];
    }

    public static int deuxiemeIA(int[] coup) {
        int indiceMax = 0; //indice de la case qui retournerait le plus de pions
        int max = 0; //nombre de pions retournés maximum
        int actuel = 0;//nombre de pions retournés par chaque coup
        String[] directions = {"N", "NE", "E", "SE", "S", "SO", "O", "NO"};
        //on parcourt le tableau de tout les coups possibles
        for (int i = 0; i < coup.length; i++) {
            actuel = 0;
            //on parcourt toutes les directions pour compter le nombre de pions que retournerait chaque coup
            for (int j = 0; j < directions.length; j++) {
                actuel += retourneDir(joueur, coup[i], directions[j]).length;
            }
            if (max < actuel) {// si ce nombre est > au maximum
                max = actuel;//alors il devient le maximum
                indiceMax = i;//et l'indice du max devient l'indice de cette case
            }
        }
        return coup[indiceMax];
    }

    /*************************** main ***************************/

    public static void main(String[] args) {
        menu();
    }
}

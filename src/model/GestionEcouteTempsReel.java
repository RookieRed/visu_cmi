package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import executable.Main;


public class GestionEcouteTempsReel {
	
	private JTree arbre;
	private TreeSelectionListener tlstTempsReel, tlstHistorique;
	private Map<String, DefaultMutableTreeNode> noeudsCapteur;
	private Set<Capteur> capteursEcoutes, capteursConnectes;
	private Map<String, DonneesCapteurHistorique> capteursEnregistres;
	
	
	public GestionEcouteTempsReel(final JTree arbre) {
		this.capteursEcoutes = new HashSet<Capteur>();
		this.arbre = arbre;
		ReaderJSON reader = new ReaderJSON();
		reader.lireFichier(Main.HISTORIQUE);
		this.capteursEnregistres = reader.lireCapteurs();
		this.tlstTempsReel = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode noeud = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();
				if(noeud != null){
					Set<Capteur> nouvelleSelection = GestionEcouteTempsReel.this.selectionnerCapteurs(noeud);
					//Desinscription des capteurs deja ecoutes
					GestionEcouteTempsReel.inscrireCapteursPourEcoute(false, capteursEcoutes);
					//Inscription des capteurs a ecouter
					GestionEcouteTempsReel.inscrireCapteursPourEcoute(true, nouvelleSelection);
					capteursEcoutes = nouvelleSelection;
					GestionEcouteTempsReel.this.actualiserValeursTableau();
				}
			}
		};
		this.tlstHistorique = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode noeud = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();
				if(noeud != null && noeud.getUserObject() instanceof Capteur){
					Capteur capteur = (Capteur) noeud.getUserObject();
					DonneesCapteurHistorique d = capteursEnregistres.get(capteur.getId());
					ArrayList<Object[]> donnees = new ArrayList<Object[]>();
					
					for (String jour : d.valeurs.keySet()) {
						Iterator<DonneesCapteur> it = d.valeurs.get(jour).iterator();
						while(it.hasNext()){
							DonneesCapteur data = it.next();
							Object[] t = new Object[3];
							t[0] = jour;
							t[1] = data.getX();
							t[2] = data.getY();
							donnees.add(t);
						}
					}

					Object[][] tableData = new Object[donnees.size()][3];
					
					for (int incr=0; incr<tableData.length; incr++) {
						tableData[incr] = donnees.get(incr);
					}
				}
			}
		};
		this.capteursConnectes = new HashSet<Capteur>();
		this.arbre.addTreeSelectionListener(tlstTempsReel);
		this.noeudsCapteur = new HashMap<String, DefaultMutableTreeNode>();
	}
	
	private void actualiserValeursTableau() {
		for(Capteur c : capteursEcoutes){
			float valeur = c.getValeur();
		}
	}
	

	private Set<Capteur> selectionnerCapteurs(DefaultMutableTreeNode noeud){
		Set<Capteur> selection = new HashSet<Capteur>();
		if(noeud.isLeaf()){
			Object contenu = noeud.getUserObject();
			if(contenu instanceof Capteur){
				selection.add((Capteur) contenu);
			}
		}
		else {
			Enumeration<DefaultMutableTreeNode> enfants = noeud.children();
			while(enfants.hasMoreElements()){
				DefaultMutableTreeNode enfant = enfants.nextElement();
				selection.addAll(this.selectionnerCapteurs(enfant));
			}
		}
		return selection;
	}
	
	private static void inscrireCapteursPourEcoute(boolean inscription, Set<Capteur> capteurs){
		if(!capteurs.isEmpty()){
			//Inscription des capteurs
			RequeteServeur requete = RequeteServeur.construireInscriptionCapteurs(capteurs, inscription);
			ConnexionServeur.getInstance().executerRequete(requete);
		}
	}
	
	private DefaultMutableTreeNode ajouterCapteurArbre(Capteur capteur){
		DefaultTreeModel model = (DefaultTreeModel) arbre.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode feuille = null;
		
		if(capteur.estExterieur()){
			DefaultMutableTreeNode ext = (DefaultMutableTreeNode) root.getChildAt(1);
			feuille = new DefaultMutableTreeNode(capteur);
			
			//tri des capteurs exterieurs
			Enumeration<DefaultMutableTreeNode> enumNoeudsCapteurs = ext.children();
			boolean insertOK = false;
			while(enumNoeudsCapteurs.hasMoreElements()){
				DefaultMutableTreeNode noeud = enumNoeudsCapteurs.nextElement();
				Coordonnees coordonnees = (Coordonnees) ((Capteur) noeud.getUserObject()).getPosition();
				if(coordonnees.getLatitude() <= ((Coordonnees) capteur.getPosition()).getLatitude()
						&& coordonnees.getLongitude() <= ((Coordonnees) capteur.getPosition()).getLatitude()){
					int position = ext.getIndex(noeud);
					ext.insert(feuille, position);
					insertOK = true;
					break;
				}
			}
			if(!insertOK) ext.add(feuille);
		}
		
		else{
			boolean ok = false;
			DefaultMutableTreeNode interieur = (DefaultMutableTreeNode) root.getChildAt(0);
			Enumeration<DefaultMutableTreeNode> batiments = interieur.children();
			Position position = (Position) capteur.getPosition();
			//Recuperation du batiment
			do {
				DefaultMutableTreeNode batiment = batiments.nextElement();
				//Recuperation de l'etage
				if(batiment.getUserObject().equals(position.getBatiment())){
					Enumeration<DefaultMutableTreeNode> etages = batiment.children();
					do {
						DefaultMutableTreeNode etage = etages.nextElement();
						//Recuperation de la salle
						if(etage.getUserObject().equals(position.getEtage()+"")){
							Enumeration<DefaultMutableTreeNode> salles = etage.children();
							do {
								DefaultMutableTreeNode salle = salles.nextElement();
								if(salle.getUserObject().equals(position.getSalle())){
									feuille = new DefaultMutableTreeNode(capteur);
									salle.add(feuille);
									ok = true;
								}
							} while (!ok && salles.hasMoreElements());
						}
					} while (!ok && etages.hasMoreElements());
				}
			} while (!ok && batiments.hasMoreElements());
			if(!ok) feuille = null;
		}
		arbre.repaint();
		return feuille;
	}
	
	public void afficherCapteursEcoutes(){
		for(Capteur capteur : this.capteursConnectes){
			DefaultMutableTreeNode feuille = ajouterCapteurArbre(capteur);
			if(feuille != null)
				this.noeudsCapteur.put(capteur.getId(), feuille);
		}
		this.arbre.repaint();
		this.arbre.removeTreeSelectionListener(tlstHistorique);
		this.arbre.addTreeSelectionListener(tlstTempsReel);
	}
	
	public void afficherCapteursEnregistres(){
		this.capteursEcoutes.clear();
		for(DonneesCapteurHistorique donnee : this.capteursEnregistres.values()){
			DefaultMutableTreeNode feuille = ajouterCapteurArbre(donnee.capteur);
			if(feuille != null)
				this.noeudsCapteur.put(donnee.capteur.getId(), feuille);
		}
		this.arbre.repaint();
		this.arbre.removeTreeSelectionListener(tlstTempsReel);
		this.arbre.addTreeSelectionListener(tlstHistorique);
		
	}
	
	public void ajouterCapteurEcoute(Capteur capteur) {
		DefaultMutableTreeNode feuille = ajouterCapteurArbre(capteur);
		if(feuille != null){
			this.noeudsCapteur.put(capteur.getId(), feuille);
			this.capteursConnectes.add(capteur);
		}
	}
	
	
	public void retirerTousCapteursArbre(){
		this.noeudsCapteur.clear();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.arbre.getModel().getRoot();
		retirerCapteursNoeud(root);
	}
	
	private static void retirerCapteursNoeud(DefaultMutableTreeNode noeud){
		if(noeud.isLeaf()){
			if(noeud.getUserObject() instanceof Capteur){
				((DefaultMutableTreeNode) noeud.getParent()).remove(noeud);
			}
		}
		else {
			Enumeration<DefaultMutableTreeNode> enumNoeudsCapteurs = noeud.children();
			while(enumNoeudsCapteurs.hasMoreElements()){
				DefaultMutableTreeNode n = enumNoeudsCapteurs.nextElement();
				retirerCapteursNoeud(n);
			}
		}
	}
	
	public void retirerCapteur(String idCapteur) {
		DefaultMutableTreeNode feuille;
		if((feuille = noeudsCapteur.get(idCapteur)) != null){
			feuille.setParent(null);
			this.noeudsCapteur.remove(idCapteur);
			this.capteursEcoutes.remove((Capteur) feuille.getUserObject());
			this.capteursEcoutes.remove((Capteur) feuille.getUserObject());
			this.actualiserValeursTableau();
		}
		this.arbre.repaint();
	}

	public void actualiserValeurCapteur(String idCapteur, String valeur) {
		//Actualisation de l'arbre
		DefaultMutableTreeNode feuille;
		if((feuille = noeudsCapteur.get(idCapteur)) != null){
			Capteur c = (Capteur) feuille.getUserObject();
			float valeurFloat = Float.parseFloat(valeur);
			c.actualiserValeur(valeurFloat);
			if(capteursEcoutes.contains(c)){
				GestionEcouteTempsReel.this.actualiserValeursTableau();
			}
			//Ajout de la valeur dans le fichier JSON
			WriterJSON writer = new WriterJSON();
			writer.lireFichier(Main.HISTORIQUE);
			writer.addCapteur(c, valeurFloat);
		}
	}
}

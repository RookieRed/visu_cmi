package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DonneesCapteurHistorique {
	
		Capteur capteur;
		Map<String, ArrayList<DonneesCapteur>> valeurs;
	
	/**
	 * Constructeur du capteur gerant l'historique des valeurs
	 * @param capteur capteur contentant les variables initiales d'un capteur
	 */
	public DonneesCapteurHistorique(Capteur capteur) {
		this.capteur = capteur;
		this.valeurs = new HashMap<>();
	}
	/**
	 * Ajoute un couple date+heure : valeur au capteur
	 * @param date date du capteur (format = dd/mm/yy)
	 * @param heure heure du capteur (format = 12.55)
	 * @param valeur valeur du capteur en double 
	 */
	public void ajouterValeur(String date, double heure, double valeur) {
		String heureString = "" + heure;
		DonneesCapteur data = new DonneesCapteur(heure, valeur);
		ArrayList<DonneesCapteur> donnees = this.valeurs.get(date);
		if(donnees == null){
			donnees = new ArrayList<DonneesCapteur>();
			donnees.add(data);
			this.valeurs.put(date, donnees);
		}
		else {
			this.valeurs.get(date).add(data);
		}
	}
	
	public Map<String, ArrayList<DonneesCapteur>> getValeurs() {
		return this.valeurs;
	}
}

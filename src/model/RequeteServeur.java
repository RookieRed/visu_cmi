package model;

import java.util.ArrayList;
import java.util.Set;

import model.RequeteServeur.Primitive;

public class RequeteServeur {
	
	/**
	 * Enumération des différentes fonctions reconnues par le serveur
	 * @author rookiered
	 */
	public enum Primitive {
		ConnexionCapteur,
		ConnexionVisu,
		DeconnexionCapteur,
		DeconnexionVisu,
		ValeurCapteur,
		InscriptionCapteur,
		DesinscriptionCapteur
	}
	private String requete;
	private Primitive type;
	
	private RequeteServeur(){
		this.requete = "";
	}
	
	/**
	 * Construit une requête simple contenant uniquement la primitive
	 * @param primitive de la requete
	 */
	public RequeteServeur(Primitive primitive) {
		this.requete = primitive.name();
	}

	/**
	 * Construit et retourne une nouvelle requete autour d'un capteur : il peut s'agir
	 * de sa connexion, de l'envoie de donnees ou encore de sa deconnexion.
	 * Il faudra ensuite envoyer au serveur via la classe ConnexionServeur
	 * @param type le type de requete parmi l'énumération Primitive
	 * @param capteur le capteur dont on souhaite envoyer les donnees
	 * @return la requete serveur ou null en cas de probleme
	 */
	public static RequeteServeur construirePourCapteur(Primitive type, Capteur capteur){
		if(capteur == null)
			return null;
		
		RequeteServeur requete = new RequeteServeur();
		requete.type = type;
		requete.requete = type.name() + ";";
		switch (type) {
		case ConnexionCapteur:
			requete.requete += capteur.getId() +";"
				+ capteur.getTypeMesure() +";";
			if(capteur.estExterieur()){
				Coordonnees c = (Coordonnees) capteur.getPosition();
				requete.requete += c.getLatitude() + ";"
						+ c.getLongitude();
			}
			else {
				Position p = (Position) capteur.getPosition();
				requete.requete += p.getBatiment() + ";"
						+ p.getEtage() + ";"
						+ p.getSalle() + ";"
						+ p.getPositionRelative();
			}
			return requete;
			
		case DeconnexionCapteur:
			requete.requete += capteur.getId();
			return requete;
			
		case ValeurCapteur:
			requete.requete += capteur.getValeur();
			return requete;
		
		default:
			return null;
		}
	}
	
	/**
	 * Construit une nouvelle requete a envoyer au serveur. Cette methode est a utiliser
	 * pour connecter ou deconnecter une interface de visualisation au serveur
	 * @param type : le type de requete a envoyer : connexion ou deconnexion
	 * @param identifiant : l'identifiant de l'interface de simulation
	 * @return null en cas d'erreur ou la requete a envoyer sinon
	 */
	public static RequeteServeur construirePourInterface(Primitive type, String identifiant){
		if(identifiant == null || identifiant.length() == 0)
			return null;
		
		if(type == Primitive.ConnexionVisu || type == Primitive.DeconnexionVisu){
			RequeteServeur requete = new RequeteServeur();
			requete.type = type;
			requete.requete = type.name() + ";";
			requete.requete += identifiant;
			return requete;
		}
		return null;
	}
	
	/**
	 * 
	 * @param capteurs
	 * @return
	 */
	public static RequeteServeur construireInscriptionCapteurs(Set<Capteur> capteurs, boolean inscription){
		RequeteServeur requete = new RequeteServeur();
		if(capteurs.size() > 0){
			if(inscription)
				requete.type = Primitive.InscriptionCapteur;
			else 
				requete.type = Primitive.DesinscriptionCapteur;
			requete.requete = requete.type.name();
			for(Capteur c : capteurs){
				requete.requete += ";";
				requete.requete += c.getId();
			}
			return requete;
		}
		return null;
	}
	
	 public String getRequete(){
		 return this.requete;
	 }
	 
	 public Primitive getType(){
		 return this.type;
	 }
}

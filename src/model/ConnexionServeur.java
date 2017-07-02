package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import api.API;
import api.API.Action;

import executable.Main;
import model.ConnexionServeur.ReponseServeur;
import model.RequeteServeur.Primitive;

public class ConnexionServeur {
	
	public enum ReponseServeur {
		ConnexionOK,
		ConnexionKO,
		DeconnexionOK,
		DeconnexionKO,
		InscriptionCapteurOK,
		InscriptionCapteurKO,
		DesinscriptionCapteurOK,
		DesinscriptionCapteurKO
	}
	
	private Socket socket;
	private Thread thread;
	private boolean estEcouteLancee;
	
	private static ConnexionServeur instance = null;
	
	
	private ConnexionServeur() {
		this.thread = null;
		this.estEcouteLancee = false;
	}
	
	/**
	 * Retourne l'instance de ConnexionServeur de l'application
	 * @return null si la classe n'a pas ete instanciee par la methode instancier, l'instance du singleton sinon
	 */
	public static ConnexionServeur getInstance(){
		if(instance == null){
			instance = new ConnexionServeur();
		}
		return instance;
	}
	
	/**
	 * Instancie une nouvelle connexion au serveur dont les prametres sont precises :
	 * @param ip l'adresse IP du serveur sous forme de chaine de caracteres
	 * @param port le port du serveur sous forme d'entier
	 * @return vrai si la connexion s'est correctement effectu�e, faux sinon
	 */
	public boolean connecter(String ip, int port){
		try {
			this.socket = new Socket(ip, port);
			//Envoie de la requete de connexion
			this.executerRequete(RequeteServeur.construirePourInterface(Primitive.ConnexionVisu, "id"));
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String reponse = br.readLine();
			if(ReponseServeur.valueOf(reponse) == ReponseServeur.ConnexionKO){
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Deconnecte l'application du serveur, interrompt l'ecoute du serveur et ferme la socket instanciee
	 */
	public void deconnecter(){
		if(this.socket != null){
			this.executerRequete(new RequeteServeur(Primitive.DeconnexionVisu));
			this.interrompreEcoute();
			try {
				this.socket.close();
				this.socket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Permet la communication avec le serveur : envoie la requete passee en parametre
	 * et attend la reponse du serveur. La reponse est la valeur de retour
	 * @param r la requete de type RequeteServeur
	 * @return la reponse du serveur en cas de succes, ou null si pas de reponse
	 */
	public void executerRequete(RequeteServeur r){
		if(r.getRequete().length() > 0) {
			try {
				//Envoie de la requete au serveur
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				pw.println(r.getRequete());
				pw.flush();
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
	
	
	public void lancerEcoute(){
		final Thread actuel = Thread.currentThread();
		this.estEcouteLancee = true;
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while(ConnexionServeur.this.estEcouteLancee){
						String requete = br.readLine();
						String[] tabRequete = requete.split(";");
						API api = new API();
						
						switch (tabRequete[0]){
							case "CapteurPresent":
								Capteur capteur;
								Mesure mesure = ConnexionServeur.gestionTypeMesureCapteur(tabRequete[2]);
								
								//Capteur interieur
								if(tabRequete.length == 7){
									Position position = new Position(tabRequete[3], Integer.parseInt(tabRequete[4]), tabRequete[5], tabRequete[6]);
									capteur = new Capteur(tabRequete[1], Integer.MIN_VALUE,
											Integer.MAX_VALUE, mesure , position);
								}
								
								//Capteur exterieur
								else if(tabRequete.length == 5) {
									capteur = new Capteur(tabRequete[1], Integer.MIN_VALUE, Integer.MAX_VALUE, mesure,
											new Coordonnees(Float.parseFloat(tabRequete[3]), Float.parseFloat(tabRequete[4])));
								}
								api.connecterCapteur(capteur);
								
								break;
								
							case "CapteurDeco":
								api.executer(Action.connectSensor, tabRequete[1]+",0");
								break;
	
							case "ValeurCapteur":
								api.actualiserValeurCapteur(tabRequete[1], tabRequete[2]);
								break;
							
							case "DeconnexionOK":
								return;
							case "DeconnexionKO":
								return;
							default:
								break;
						}
					}
				}
				catch (IOException e) {}
			}
		});
		this.thread.start();
	}
	

	public void interrompreEcoute(){
		this.estEcouteLancee = false;
		if(this.thread != null){
			this.thread.interrupt();
			this.thread = null;
		}
	}
	

	private static Mesure gestionTypeMesureCapteur(String type){
		//Gestion du type de mesure du capteur
		Mesure mesure;
		try{
			type = Normalizer.normalize(type, Normalizer.Form.NFD);
		    type = type.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
			mesure = new Mesure(TypeCapteur.valueOf(type));
		} catch(IllegalArgumentException e) {
			mesure = new Mesure(TypeCapteur.Inconnu);
		}
		return mesure;
	}
}

package api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import model.Capteur;
import model.Coordonnees;
import model.Position;

import org.json.JSONException;
import org.json.JSONObject;

public class API {
	
	/**
	 * 
	 * @author rookiered
	 *
	 */
	public enum Action {
		insertSensor,
		connectSensor,
		insertMeasure,
	}
	
	// Donnees de connexin
	public static final String ADRESSE = "http://fustel.rookiered.xyz/api.php";
	public static final int PORT = 80;
	
	/**
	 * 
	 * @param a
	 * @param params
	 * @return
	 */
	public ReponseAPI executer(Action a, String params){
		HttpURLConnection connexion = null;
		
		// Cr�ation de l'objet connexion
		String get = "?action=" + a.toString();
		if(params != null && params.length() > 0)
			get += "&params=" + params;
		try {
			URL url = new URL(ADRESSE + get);
			connexion = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			System.err.println("Connexion � l'API impossible : " + e.getMessage());
			return null;
		}
		
		// Envoi de la requete
		try {
			// Parametres GET
			connexion.setRequestMethod("GET");
			
			//Créaton de la requete, HEADERS
			connexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connexion.setRequestProperty("Content-Length",
					""+(ADRESSE.length() + get.toString().length()));
			connexion.setUseCaches(true);
			connexion.setDoOutput(true);
			
			// Envoie
			OutputStream os = connexion.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("utf-8")));
			writer.write(get);
			writer.flush();
			
			// Lecture de la réponse
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connexion.getInputStream(), "utf-8"));
			String ligne = "",
					repStr = "";
			while((ligne = br.readLine()) != null)
				repStr += ligne+"\n";
			repStr = repStr.substring(0, repStr.length() - 1);
			System.out.println("[rep API] : " + repStr);
			
			// Parse de l'objet JSON de réponse
			JSONObject repJSON = new JSONObject(repStr);
			if(repJSON.getBoolean("error")){
				return new ReponseAPI(null, true, repJSON.getString("message"));
			}
			else {
				return new ReponseAPI(repJSON.getJSONArray("data"), false, null);
			}
			
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			return new ReponseAPI(null, true, e.getMessage());
		}
		
		return null;
	}

	public void connecterCapteur(Capteur capteur) {
		ReponseAPI rep = this.executer(Action.connectSensor, capteur.getId());
		if(!rep.erreur()){
			boolean capExiste;
			try {
				capExiste = rep.getDonnees().getJSONArray(0).getBoolean(0);
			} catch (JSONException e) {
				System.err.println("Reponse API erreur : " + rep.getDonnees().toString());
				return;
			}
			
			// Ajout du capteur dans la BdD
			if(!capExiste){
				String params = capteur.getId() + ";"
					+ capteur.getType().getIdMysql() + ";"
					+ capteur.getIntervalleMin() + ";"
					+ capteur.getIntervalleMax();
				// Création du paramètre de la postion
				String location = "&location=";
				if(capteur.estExterieur()){
					Coordonnees c = (Coordonnees) capteur.getPosition();
					location += c.getLongitude() + ";" + c.getLatitude();
				}
				else {
					Position p = (Position) capteur.getPosition();
					location += p.getBatiment() + ";"
						+ p.getEtage() + ";"
						+ p.getSalle() + ";"
						+ p.getPositionRelative();
				}

				// Execution de la requete d'insertion
				rep = this.executer(Action.insertSensor, params+location);
				if(rep.erreur()){
					System.err.println("Erreur insertion capteur " + rep.getMessageErreur());
					return;
				}
				System.out.println("Ajout du capteur " + capteur.getId() + " dans la BdD");
			}
			else {
				System.out.println("Connexion / deconnexion capteur " + capteur.getId());
			}
		}
		else {
			System.err.println("Erreur insertion capteur " + rep.getMessageErreur());
		}
	}
	
	
	
}

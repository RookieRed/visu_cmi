package api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import model.Capteur;

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
		updateSensor,
		updateSensorLocation,
		connectSensor,
		insertMeasure,
		selectSensorMeasures
	}
	
	// Données de connexin
	public static final String ADRESSE = "http://fustel.rookiered.xyz/api.php";
	public static final int PORT = 80;
	
	private HttpURLConnection connexion;
	
	public API() {	
		//Lancement de la connexion
		URL url;
		try {
			url = new URL(ADRESSE);
			this.connexion = (HttpURLConnection) url.openConnection();
		}
		catch (MalformedURLException e) {
			System.err.println("Erreur paralètrage URL API");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param a
	 * @param params
	 * @return
	 */
	public ReponseAPI executer(Action a, String params){
		if(this.connexion == null)
			return null;
		
		// Envoi de la requete
		try {
			// Parametres GET
			this.connexion.setRequestMethod("GET");
			String get = "?action=" + a.toString();
			if(params != null && params.length() > 0)
				get += "&params=" + params;
			
			//Créaton de la requete, HEADERS
			this.connexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			this.connexion.setRequestProperty("Content-Length",
					""+(ADRESSE.length() + get.toString().length()));
			this.connexion.setUseCaches(true);
			this.connexion.setDoOutput(true);
			
			// Envoie
			OutputStream os = this.connexion.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("utf-8")));
			writer.write(get);
			writer.flush();
			
			// Lecture de la réponse
			BufferedReader br = new BufferedReader(new InputStreamReader(
					this.connexion.getInputStream(), "utf-8"));
			String ligne = "",
					repStr = "";
			while((ligne = br.readLine()) != null)
				repStr += ligne+"\n";
			repStr = repStr.substring(0, repStr.length() - 1);
			
			// Parse de l'objet JSON de réponse
			JSONObject repJSON = new JSONObject(repStr);
			if(repJSON.getBoolean("error")){
				return new ReponseAPI(null, true, repJSON.getString("errorMessage"));
			}
			else {
				return new ReponseAPI(repJSON.getJSONObject("donnees"), false, null);
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
		// TODO Auto-generated method stub
		
	}
	
	
	
}

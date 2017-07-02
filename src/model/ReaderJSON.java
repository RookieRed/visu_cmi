package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReaderJSON {

    private JSONObject json;

    public ReaderJSON() {
    	this.json = new JSONObject();
    }
    /**
     * Lecture d'un fichier externe
     * @param fileName url du fichier (path)
     * @return true si le fichier est correctement lu
     */
    public boolean lireFichier(final String fileName) {
    	String contenu = "";
    	try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String line = bufferedReader.readLine();
            while (line != null) {
                contenu += line + "\n";
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            this.json = new JSONObject(contenu);
        } catch (Exception e) {
        	return false;
        }
    	return true;
    }
    /**
     * Obtenir la liste des batiments
     * @return L'arrayListe de tous les batiments pr�sents dans le fichier
     */
    public String[] getBatiments() {
        List<String> al = new ArrayList<String>();
        try {
            JSONArray arr = json.getJSONArray("batiments");
            for (int i = 0; i < arr.length(); i++) {
                String nomBat = arr.getJSONObject(i).getString("nom");
                al.add(nomBat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String[] batiments = new String[al.size()];
        batiments = al.toArray(batiments);
        return batiments;
    }
    /**
     * Retourne la liste des �tages pour un batiment donn�
     * @param nomBatiment nom du batiment
     * @return l'arrayList des �tages
     */
    public String[] getEtages(String nomBatiment) {
        ArrayList<String> al = new ArrayList<String>();
        try {
            JSONArray arr = json.getJSONArray("batiments");
            for (int i = 0; i < arr.length(); i++) {
                String nomBat = arr.getJSONObject(i).getString("nom");
                if (nomBat.equals(nomBatiment)) {
                    JSONArray etages = arr.getJSONObject(i).getJSONArray("etages");
                    for (int j = 0; j < etages.length(); j++) {
                        String etage = etages.getJSONObject(j).getString("num");
                        al.add(etage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] etages = new String[al.size()];
        etages = al.toArray(etages);
        return etages;
    }
    /**
     * Retourne la liste des salles pour un batiment et un �tage donn�
     * @param nomBatiment nom du batiment
     * @param numEtage etage du batiment
     * @return l'arrayList des salles
     */
    public String[] getSalles(String nomBatiment, String numEtage) {
        ArrayList<String> al = new ArrayList<String>();
        
        try {
            JSONArray arr = json.getJSONArray("batiments");
            
            for (int i = 0; i < arr.length(); i++) {
                String nomBat = arr.getJSONObject(i).getString("nom");
                
                if (nomBat.equals(nomBatiment)) {
                    JSONArray etages = arr.getJSONObject(i).getJSONArray("etages");
                    
                    for (int j = 0; j < etages.length(); j++) {
                        String etage = etages.getJSONObject(j).getString("num");
                        
                        if (etage.equals(numEtage)) {
                            JSONArray salles = etages.getJSONObject(j).getJSONArray("salles");
                            
                            for (int k = 0; k < salles.length(); k++) {
                            	String salle = salles.getJSONObject(k).getString("num");
                                al.add(salle);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String[] salles = new String[al.size()];
        salles = al.toArray(salles);
        return salles;
    }

    /**
     * Recupere l'id des capteurs d�ja pr�sents dans le fichier
     * @return un tableau d'id
     */
    public String[] getCapteurs() {
        List<String> al = new ArrayList<String>();
        try {
        	JSONArray capteursExt = json.getJSONArray("capteurs-exterieur");
            for (int i = 0; i < capteursExt.length(); i++) {
                String nomBat = capteursExt.getJSONObject(i).getString("id");
                al.add(nomBat);
            }
            JSONArray capteursInt = json.getJSONArray("capteurs-interieur");
            for (int i = 0; i < capteursInt.length(); i++) {
				String nomBat = capteursInt.getJSONObject(i).getString("id");
				al.add(nomBat);
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] capteurs = new String[al.size()];
        capteurs = al.toArray(capteurs);
        return capteurs;
    }
    /**
     * 
     * @param id id du capteur recherch�
     * @return true si le capteur est deja pr�sent false sinon
     */
    public boolean issetCapteur(String id){
        for (String capteur : this.getCapteurs()){
            if(capteur.equals(id))
                return true;
        }
        return false;
    }

    public JSONObject getJson() {
        return json;
    }
    /**
     * Obtenir la liste des capteurs pr�sent dans le fichier contenant l'historique des capteurs
     * @return ArrayList de capteursMinutesSecondes
     */
    public HashMap<String, DonneesCapteurHistorique> lireCapteurs() {
    	HashMap<String, DonneesCapteurHistorique> capteurs = new HashMap();
        try {
        	String date = null;
        	double heure = 0;
        	double valeur = 0;
        	Capteur capteur;
        	DonneesCapteurHistorique capteurMinuteSeconde = null;
            JSONArray capteursInterieur = json.getJSONArray("capteurs-exterieur");
            for (int i = 0; i < capteursInterieur.length(); i++) {
                String id_capteur_int = capteursInterieur.getJSONObject(i).getString("id");
                String type_capteur_int = capteursInterieur.getJSONObject(i).getString("type");
                JSONObject position_int = new JSONObject(capteursInterieur.getJSONObject(i).getJSONObject("position").toString(1));
				float longitude = position_int.getLong("long");
            	float latitude = position_int.getLong("lat");
				JSONArray valeurs_int = capteursInterieur.getJSONObject(i).getJSONArray("valeurs");
				capteur = new Capteur(id_capteur_int, Integer.MIN_VALUE, Integer.MAX_VALUE, new Mesure(TypeCapteur.valueOf(type_capteur_int)), new Coordonnees(latitude, longitude));
            	capteurMinuteSeconde = new DonneesCapteurHistorique(capteur);
            	for (int ii = 0; ii < valeurs_int.length(); ii++) {
                	JSONObject valeur_int = new JSONObject(valeurs_int.getString(ii));
                	date = valeur_int.getString("date");
                	heure = valeur_int.getDouble("heure");
                	valeur = valeur_int.getDouble("valeur");
                	capteurMinuteSeconde.ajouterValeur(date, heure, valeur);
                }
                capteurs.put(id_capteur_int, capteurMinuteSeconde);
            }
                
                JSONArray capteursExterieur = json.getJSONArray("capteurs-interieur");
            for (int j = 0; j < capteursExterieur.length(); j++) {
				String id_capteur_ext = capteursExterieur.getJSONObject(j).getString("id");
				String type_capteur_ext = capteursExterieur.getJSONObject(j).getString("type");
				JSONObject position_ext = new JSONObject(capteursExterieur.getJSONObject(j).getJSONObject("position").toString(1));
				String batiment = position_ext.getString("bat");
				int etage = position_ext.getInt("etage");
				String salle = position_ext.getString("salle");
				JSONArray valeurs_int = capteursInterieur.getJSONObject(j).getJSONArray("valeurs");
				capteur = new Capteur(id_capteur_ext, Integer.MIN_VALUE, Integer.MAX_VALUE, new Mesure(TypeCapteur.valueOf(type_capteur_ext)), new Position(batiment, etage, salle, ""));
				capteurMinuteSeconde = new DonneesCapteurHistorique(capteur);
            	for (int jj = 0; jj < valeurs_int.length(); jj++) {
                	JSONObject test2 = new JSONObject(valeurs_int.getString(jj));
                	capteurMinuteSeconde.ajouterValeur(date, heure, valeur);
            	}
                capteurs.put(id_capteur_ext, capteurMinuteSeconde);
            }
        } catch (Exception e) {}
        return capteurs;
	}
}

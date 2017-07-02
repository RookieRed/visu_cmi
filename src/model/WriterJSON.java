package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import executable.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WriterJSON {

    private JSONObject json;

    public WriterJSON() {
        this.json = new JSONObject();
    }


    /**
     * Si le capteur existe dans le fichier histo_capteurs.json, on ajoute uniquement la mesure au capteur existant
     * sinon on ajout le capteur complet
     * @param capteur a ajouter à la liste ou ajouter une valeur
     * @param mesure a ajouter
     */
    public void addCapteur(Capteur capteur, float mesure){

        ReaderJSON reader = new ReaderJSON();
        reader.lireFichier(Main.HISTORIQUE);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        DateFormat heureFormat = new SimpleDateFormat("HH");
        Date heure = new Date();
        DateFormat minutesFormat = new SimpleDateFormat("mm");
        Date minutes = new Date();
        DateFormat secondesFormat = new SimpleDateFormat("ss");
        Date secondes = new Date();

        String dateMesure = dateFormat.format(date);
        int mi = new Integer(minutesFormat.format(minutes));
        int sec = new Integer(secondesFormat.format(secondes));
        String heuresMesure = heureFormat.format(heure) + "." + String.valueOf(mi*10/60) + String.valueOf(sec*10/60);
        
        // Si le capteur est un capteur extérieur
        if (capteur.estExterieur()) {
        	//Si le capteur existe on ajoute la mesure
        	if(reader.issetCapteur(capteur.getId())){
                try {
                JSONArray capteursOld = getJson().getJSONArray("capteurs-interieur");
                JSONArray capteurs = getJson().getJSONArray("capteurs-exterieur");
                for (int i = 0; i < capteurs.length(); i++) {
                    String idCap = capteurs.getJSONObject(i).getString("id");
                    if (idCap.equals(capteur.getId())) {
                        JSONArray mesures = capteurs.getJSONObject(i).getJSONArray("valeurs");
                        JSONObject mesu = new JSONObject();
                        mesu.put("valeur", mesure);
                        mesu.put("date", dateMesure);
                        mesu.put("heure", heuresMesure);
                        mesures.put(mesu);
                        JSONObject caps = new JSONObject();
                        caps.put("capteurs-interieur", capteursOld);
                        caps.put("capteurs-exterieur", capteurs);
                        try (FileWriter file = new FileWriter(Main.HISTORIQUE)) {
                            file.write(caps.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }else{
                try {
                    JSONArray capteurs = getJson().getJSONArray("capteurs-exterieur");
                    JSONArray capteursOld = getJson().getJSONArray("capteurs-interieur");
                    JSONObject cap = new JSONObject();
                    JSONObject position = new JSONObject();
                    position.put("long", ((Coordonnees) capteur.getPosition()).getLongitude());
                    position.put("lat", ((Coordonnees) capteur.getPosition()).getLatitude());
                    JSONArray mesures = new JSONArray();
                    JSONObject mesu = new JSONObject();
                    mesu.put("valeur", mesure);
                    mesu.put("date", dateMesure);
                    mesu.put("heure", heuresMesure);
                    mesures.put(mesu);
                    cap.put("position", position);
                    cap.put("id", capteur.getId());
                    cap.put("valeurs", mesures);
                    cap.put("type", capteur.getTypeMesure());
                    capteurs.put(cap);
                    JSONObject caps = new JSONObject();
                    caps.put("capteurs-interieur", capteursOld);
                    caps.put("capteurs-exterieur", capteurs);
                    try (FileWriter file = new FileWriter(Main.HISTORIQUE)) {
                        file.write(caps.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
        	if(reader.issetCapteur(capteur.getId())){        	
                try {
            	JSONArray capteursOld = getJson().getJSONArray("capteurs-exterieur");
                JSONArray capteurs = getJson().getJSONArray("capteurs-interieur");
                for (int i = 0; i < capteurs.length(); i++) {
                    String idCap = capteurs.getJSONObject(i).getString("id");
                    if (idCap.equals(capteur.getId())) {
                        JSONArray mesures = capteurs.getJSONObject(i).getJSONArray("valeurs");
                        JSONObject mesu = new JSONObject();
                        mesu.put("valeur", mesure);
                        mesu.put("date", dateMesure);
                        mesu.put("heure", heuresMesure);
                        mesures.put(mesu);
                        JSONObject caps = new JSONObject();
                        caps.put("capteurs-interieur", capteurs);
                        caps.put("capteurs-exterieur", capteursOld);
                        try (FileWriter file = new FileWriter(Main.HISTORIQUE)) {
                            file.write(caps.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }else{
                try {
                    JSONArray capteurs = getJson().getJSONArray("capteurs-interieur");
                    JSONArray capteursOld = getJson().getJSONArray("capteurs-exterieur");
                    JSONObject cap = new JSONObject();
                    JSONObject position = new JSONObject();
                    position.put("bat", ((Position) capteur.getPosition()).getBatiment());
                    position.put("etage", ((Position) capteur.getPosition()).getEtage());
                    position.put("salle", ((Position) capteur.getPosition()).getSalle());
                    JSONArray mesures = new JSONArray();
                    JSONObject mesu = new JSONObject();
                    mesu.put("valeur", mesure);
                    mesu.put("date", dateMesure);
                    mesu.put("heure", heuresMesure);
                    mesures.put(mesu);
                    cap.put("position", position);
                    cap.put("id", capteur.getId());
                    cap.put("valeurs", mesures);
                    cap.put("type", capteur.getTypeMesure());
                    capteurs.put(cap);
                    JSONObject caps = new JSONObject();
                    caps.put("capteurs-interieur", capteurs);
                    caps.put("capteurs-exterieur", capteursOld);
                    try (FileWriter file = new FileWriter(Main.HISTORIQUE)) {
                        file.write(caps.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }


    /**
     * Permet de sélectionner le fichier dans lequel on va écrire
     * @param fileName le chemin vers le fichier
     * @return un booléen en cas de succes ou echec
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

    public JSONObject getJson() {
        return json;
    }

    public static void main(String[] args) {
        WriterJSON writer = new WriterJSON();
        writer.lireFichier(Main.HISTORIQUE);
        writer.addCapteur(new Capteur("test2", 0, 10, new Mesure(TypeCapteur.EauChaude), new Position("U1", 2, "amphi", "")), 11);
        writer.addCapteur(new Capteur("test2", 0, 10, new Mesure(TypeCapteur.EauChaude), new Position("U1", 2, "amphi", "")), 11);
        writer.addCapteur(new Capteur("test4", 0, 10, new Mesure(TypeCapteur.Luminosite), new Coordonnees(10.15F, 50.12F)), 12);
    }
}

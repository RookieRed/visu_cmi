package model;


public enum TypeCapteur {    
    Temperature ("Temp\u00e9rature", 0),
    Humidite ("Humidit\u00e9", 1),
    Luminosite ("Luminosit\u00e9", 2),
    VolumeSonore ("Volume sonore", 3),
    ConsommationEclairage ("Consommation \u00e9clairage", 4),
    EauFroide ("Eau froide", 5),
    EauChaude ("Eau chaude", 6),
    VitesseVent ("Vitesse vent", 7),
    PressionAtmospherique ("Pression atmosph\u00e9srique", 8),
    Inconnu ("Inconnu", 9);
    
    private String nom;
    private int idMysql;
    
    TypeCapteur(String nom, int idMysql) {
        this.nom = nom;
    	this.idMysql = idMysql;
    }
    
    public String getValue() {
    	return this.nom;
	}

    public String getIdMysql() {
        return this.idMysql;
    }

	public static String[] getValues() {
		String[] values = new String[values().length];
		for (int i=0; i<TypeCapteur.values().length; i++) {
			values[i] = TypeCapteur.values()[i].getValue();
		}
		return values;
	}
}

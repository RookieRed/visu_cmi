package model;


public enum TypeCapteur {    
    Temperature ("Temp\u00e9rature"),
    Humidite ("Humidit\u00e9"),
    Luminosite ("Luminosit\u00e9"),
    VolumeSonore ("Volume sonore"),
    ConsommationEclairage ("Consommation \u00e9clairage"),
    EauFroide ("Eau froide"),
    EauChaude ("Eau chaude"),
    VitesseVent ("Vitesse vent"),
    PressionAtmospherique ("Pression atmosph\u00e9srique"),
    Inconnu ("Inconnu")
    ;
    
    private String nom;
    
    TypeCapteur(String nom) {
    	this.nom = nom;
    }
    
    public String getValue() {
    	return this.nom;
	}

	public static String[] getValues() {
		String[] values = new String[values().length];
		for (int i=0; i<TypeCapteur.values().length; i++) {
			values[i] = TypeCapteur.values()[i].getValue();
		}
		return values;
	}
}

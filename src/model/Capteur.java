package model;

public class Capteur{

	private boolean exterieur;
	private String 	id;
	private Mesure mesure;
	private IPosition position;
	private int intervalleMin;
	private int intervalleMax;

			///////////////////////
			//   CONSTRUCTEURS   //
			///////////////////////

	/**
	 * Constructeur des capteurs d'exterieur
	 * @param id l'identifiant unique du capteur
	 * @param mesure le type de mesure qu'il possede
	 * @param c les coordonnees x y du capteur
	 */
	public Capteur(String id, int intervalleMin, int intervalleMax, Mesure mesure, Coordonnees c) {
		this.exterieur = true;
		this.intervalleMin = intervalleMin;
		this.intervalleMax = intervalleMax;
		this.id = id;
		this.mesure = mesure;
		this.position = c;
	}
	
	/**
	 * Constructeur des capteurs d'interieur
	 * @param id l'identifiant unique du capteur
	 * @param mesure le type de mesure qu'il possede
	 * @param p l'objet d�signant la position du capteur dans un batiement
	 */
	public Capteur(String id, int intervalleMin, int intervalleMax, Mesure mesure, Position p) {
		this.exterieur = false;
		this.intervalleMin = intervalleMin;
		this.intervalleMax = intervalleMax;
		this.id = id;
		this.mesure = mesure;
		this.position = p;
	}

	
			//////////////////
			//   METHODES   //
			//////////////////
	
	/**
	 * Met a jour la valeur de la mesure du capteur
	 * @param valeur la nouvelle valeur mesuree
	 */
	public void actualiserValeur(float valeur){
		this.mesure.setValeur(valeur);
	}
	
	/**
	 * Recupere la valeur d'un capteur
	 * @return la valeur du capteur
	 */
	public float getValeur(){
		return this.mesure.getValeur();
	}
	
	public int getIntervalleMin() {
		return this.intervalleMin;
	}
	
	public int getIntervalleMax() {
		return this.intervalleMax;
	}
	
	/**
	 * Recupere le type de donnees du capteur
	 * @return le type de donnees sous forme de string
	 */
	public String getTypeMesure(){
		return this.mesure.getType();
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	public String etatCapteur() {
		return id + ", mesure = " + mesure + " [" + position + "]";
	}
	
			/////////////////
			//   GETTERS   //
			/////////////////

	public String getId() {
		return id;
	}
	public IPosition getPosition(){
		return this.position;
	}
	public boolean estExterieur(){
		return this.exterieur;
	}

	public String intOrExt(){
		return this.exterieur? "ext" : "int";
	}
}

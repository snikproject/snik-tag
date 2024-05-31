package eu.snik.tag;

/** Static helper fields for SNIK prefixes. */
public enum Snik {

	/** Prefix for the successor of the BB subontology, BB2, which is based on the third edition of the blue book. */
	BB2("http://www.snik.eu/ontology/bb2/"),
	/** Meta ontology containing the predicates of SNIK. Also contains Subtop. */
	META("http://www.snik.eu/ontology/meta/");
	
	private Snik(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public String toString() {
		return this.prefix;
	}
	
	private final String prefix;
}
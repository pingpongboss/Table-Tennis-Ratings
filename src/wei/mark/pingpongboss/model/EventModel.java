package wei.mark.pingpongboss.model;

public class EventModel {
	// @Id
	Long key;

	String provider;
	String playerId;
	String id;

	// @Unindexed
	String name;
	// @Unindexed
	String date;
	// @Unindexed
	String ratingBefore;
	// @Unindexed
	String ratingChange;
	// @Unindexed
	String ratingAfter;
	// @Unindexed
	String matches;
	// @Unindexed
	String record;

	public EventModel() {
	}

	@Override
	public String toString() {
		return String.format("%s %s (%s)", name, date, ratingChange);
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRatingBefore() {
		return ratingBefore;
	}

	public void setRatingBefore(String ratingBefore) {
		this.ratingBefore = ratingBefore;
	}

	public String getRatingChange() {
		return ratingChange;
	}

	public void setRatingChange(String ratingChange) {
		this.ratingChange = ratingChange;
	}

	public String getRatingAfter() {
		return ratingAfter;
	}

	public void setRatingAfter(String ratingAfter) {
		this.ratingAfter = ratingAfter;
	}

	public String getMatches() {
		return matches;
	}

	public void setMatches(String matches) {
		this.matches = matches;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}
}

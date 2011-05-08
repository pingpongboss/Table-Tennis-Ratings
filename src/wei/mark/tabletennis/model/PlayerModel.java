package wei.mark.tabletennis.model;

import java.util.Date;

//import javax.persistence.Id;

import wei.mark.tabletennisratingsserver.util.ProviderParser.ParserUtils;

//import com.googlecode.objectify.annotation.Cached;
//import com.googlecode.objectify.annotation.Unindexed;

//@Cached
public class PlayerModel {
//	@Id
	Long key;

	String provider;
	String id;
	String lastName;
	String firstName;

//	@Unindexed
	String rating;
//	@Unindexed
	String[] clubs;
//	@Unindexed
	String state;
//	@Unindexed
	String country;
//	@Unindexed
	String lastPlayed;
//	@Unindexed
	String expires;
//	@Unindexed
	Date refreshed;
//	@Unindexed
	String[] searchHistory;

	public PlayerModel() {
	}

	@Override
	public String toString() {
		return String.format("%s, %s (%s)", lastName, firstName, rating);
	}

	public String toDetailedString() {
		String clubsString;

		if (clubs == null)
			clubsString = null;
		else {
			StringBuilder sb = new StringBuilder();
			for (String club : clubs) {
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(club);
			}
			clubsString = sb.toString();
		}

		return String.format("%s\t%s\t%s, %s\t%s\t%s\t%s\t%s\t%s\t", id,
				expires, lastName, firstName, rating, clubsString, state,
				country, lastPlayed);
	}

	public String getBaseRating() {
		if ("usatt".equals(provider))
			return getRating();
		else if ("rc".equals(provider)) {
			try {
				// get up to the +- symbol
				return getRating().substring(0, getRating().indexOf(177));
			} catch (Exception ex) {
			}
		}
		return null;
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

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getName() {
		if (firstName == null)
			return lastName;
		else
			return String.format("%s, %s", lastName, firstName);
	}

	public void setName(String name) {
		this.lastName = ParserUtils.getLastName(name);
		this.firstName = ParserUtils.getFirstName(name);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getClubs() {
		return clubs;
	}

	public void setClubs(String[] clubs) {
		this.clubs = clubs;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLastPlayed() {
		return lastPlayed;
	}

	public void setLastPlayed(String lastPlayed) {
		this.lastPlayed = lastPlayed;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public Date getRefreshed() {
		return refreshed;
	}

	public void setRefreshed(Date refreshed) {
		this.refreshed = refreshed;
	}

	public String[] getSearchHistory() {
		return searchHistory;
	}

	public void setSearchHistory(String[] searchHistory) {
		this.searchHistory = searchHistory;
	}
}

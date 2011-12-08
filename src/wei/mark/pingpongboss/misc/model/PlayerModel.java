package wei.mark.pingpongboss.misc.model;

import java.util.Date;
import java.util.List;

import wei.mark.pingpongboss.util.ParserUtils;
import android.os.Parcel;
import android.os.Parcelable;

//import com.googlecode.objectify.annotation.Cached;
//import com.googlecode.objectify.annotation.Unindexed;

//@Cached
public class PlayerModel implements Parcelable {
	public static final Parcelable.Creator<PlayerModel> CREATOR = new Parcelable.Creator<PlayerModel>() {
		public PlayerModel createFromParcel(Parcel in) {
			return new PlayerModel(in);
		}

		public PlayerModel[] newArray(int size) {
			return new PlayerModel[size];
		}
	};

	// @Id
	Long key;

	String provider;
	String id;
	String lastName;
	String firstName;
	long popularity;
	String facebookId;

	// @Unindexed
	String rating;
	// @Unindexed
	String[] clubs;
	// @Unindexed
	String state;
	// @Unindexed
	String country;
	// @Unindexed
	String lastPlayed;
	// @Unindexed
	String expires;
	// @Unindexed
	Date refreshed;
	// @Unindexed
	List<String> searchHistory;
	// @Unindexed
	String playerId;

	public PlayerModel() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(key);
		out.writeString(provider);
		out.writeString(id);
		out.writeString(lastName);
		out.writeString(firstName);
		out.writeString(facebookId);
		out.writeString(rating);
		out.writeStringArray(clubs);
		out.writeString(state);
		out.writeString(country);
		out.writeString(lastPlayed);
		out.writeString(expires);
		out.writeSerializable(refreshed);
		out.writeStringList(searchHistory);
		out.writeString(playerId);
	}

	private PlayerModel(Parcel in) {
		key = in.readLong();
		provider = in.readString();
		id = in.readString();
		lastName = in.readString();
		firstName = in.readString();
		facebookId = in.readString();
		rating = in.readString();
		clubs = in.createStringArray();
		state = in.readString();
		country = in.readString();
		lastPlayed = in.readString();
		expires = in.readString();
		refreshed = (Date) in.readSerializable();
		searchHistory = in.createStringArrayList();
		playerId = in.readString();
	}

	@Override
	public String toString() {
		return String.format("%s, %s (%s)", lastName, firstName, rating);
	}

	public String toDetailedString() {
		return String.format("%s (%s) %s", getName(), getRating(),
				toSubtextString());
	}

	// excluding main info like name and rating
	public String toSubtextString() {
		StringBuilder sb = new StringBuilder();

		String lastPlayedString = lastPlayed == null || lastPlayed.equals("") ? "has never played"
				: String.format("last played on %s", lastPlayed);

		if (provider.equals("usatt")) {
			sb.append(String.format("from %s and %s", state, lastPlayedString));
		} else if (provider.equals("rc")) {
			if (clubs == null)
				sb.append(String.format("from %s and %s", state == null
						|| state.equals("") ? country : state, lastPlayedString));
			else {
				sb.append(String.format("from %s, plays at ", state == null
						|| state.equals("") ? country : state));

				for (int i = 0; i < clubs.length; i++) {
					String club = clubs[i];
					if (i != 0)
						sb.append(", ");
					sb.append(club);
				}

				sb.append(String.format(", and %s", lastPlayedString));
			}
		}
		return sb.toString();
	}

	public String getBaseRating() {
		if ("usatt".equals(provider))
			return getRating();
		else if ("rc".equals(provider)) {
			try {
				// get up to the +- symbol
				String base = getRating()
						.substring(0, getRating().indexOf(177));
				return base.equals("") ? getRating() : base;
			} catch (Exception ex) {
			}
		}
		return null;
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

	public String getProviderId() {
		if ("usatt".equals(provider))
			return getPlayerId();
		else if ("rc".equals(provider))
			return getId();
		else
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

	public long getPopularity() {
		return popularity;
	}

	public void setPopularity(long popularity) {
		this.popularity = popularity;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
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

	public List<String> getSearchHistory() {
		return searchHistory;
	}

	public void setSearchHistory(List<String> searchHistory) {
		this.searchHistory = searchHistory;
		if (searchHistory == null)
			setPopularity(0);
		else
			setPopularity(searchHistory.size());
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
}

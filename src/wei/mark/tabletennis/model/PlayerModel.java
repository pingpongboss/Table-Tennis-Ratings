package wei.mark.tabletennis.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerModel implements Parcelable {
	Long key;

	String provider;
	String id;

	String rating;
	String name;
	String[] clubs;
	String state;
	String country;
	String lastPlayed;
	String expires;
	Date refreshed;

	public PlayerModel() {
	}

	public PlayerModel(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(key);
		dest.writeString(provider);
		dest.writeString(id);
		dest.writeString(rating);
		dest.writeString(name);
		dest.writeStringArray(clubs);
		dest.writeString(state);
		dest.writeString(country);
		dest.writeString(lastPlayed);
		dest.writeString(expires);
		dest.writeSerializable(refreshed);
	}

	private void readFromParcel(Parcel in) {
		key = in.readLong();
		provider = in.readString();
		id = in.readString();
		rating = in.readString();
		name = in.readString();
		clubs = in.createStringArray();
		state = in.readString();
		country = in.readString();
		lastPlayed = in.readString();
		expires = in.readString();
		refreshed = (Date) in.readSerializable();
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", name, rating);
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

		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t", id, expires,
				name, rating, clubsString, state, country, lastPlayed);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public static Parcelable.Creator<PlayerModel> getCreator() {
		return CREATOR;
	}

	public static final Parcelable.Creator<PlayerModel> CREATOR = new Parcelable.Creator<PlayerModel>() {
		public PlayerModel createFromParcel(Parcel in) {
			return new PlayerModel(in);
		}

		public PlayerModel[] newArray(int size) {
			return new PlayerModel[size];
		}
	};
}

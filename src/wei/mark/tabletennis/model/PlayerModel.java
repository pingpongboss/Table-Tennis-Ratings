package wei.mark.tabletennis.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerModel implements Parcelable {
	private String mProvider;
	private String mRating;
	private String mName;
	private String mId;
	private String[] mClubs;
	private String mState;
	private String mCountry;
	private String mLastPlayed;
	private String mExpires;

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
		dest.writeString(mProvider);
		dest.writeString(mRating);
		dest.writeString(mName);
		dest.writeString(mId);
		dest.writeStringArray(mClubs);
		dest.writeString(mState);
		dest.writeString(mCountry);
		dest.writeString(mLastPlayed);
		dest.writeString(mExpires);
	}

	private void readFromParcel(Parcel in) {
		mProvider = in.readString();
		mRating = in.readString();
		mName = in.readString();
		mId = in.readString();
		mClubs = in.createStringArray();
		mState = in.readString();
		mCountry = in.readString();
		mLastPlayed = in.readString();
		mExpires = in.readString();
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", mName, mRating);
	}

	public String toDetailedString() {
		String clubs;

		if (mClubs == null)
			clubs = null;
		else {
			StringBuilder sb = new StringBuilder();
			for (String club : mClubs) {
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(club);
			}
			clubs = sb.toString();
		}

		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t", mId, mExpires,
				mName, mRating, clubs, mState, mCountry, mLastPlayed);
	}

	public String getProvider() {
		return mProvider;
	}

	public void setProvider(String provider) {
		this.mProvider = provider;
	}

	public String getRating() {
		return mRating;
	}

	public String getBaseRating() {
		if ("usatt".equals(mProvider))
			return getRating();
		else if ("rc".equals(mProvider)) {
			try {
				//get up to the +- symbol
				return getRating().substring(0, getRating().indexOf(177)); 
			} catch (Exception ex) {
			}
		}
		return null;
	}

	public void setRating(String rating) {
		this.mRating = rating;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String[] getClubs() {
		return mClubs;
	}

	public void setClubs(String[] clubs) {
		this.mClubs = clubs;
	}

	public String getState() {
		return mState;
	}

	public void setState(String state) {
		this.mState = state;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String country) {
		this.mCountry = country;
	}

	public String getLastPlayed() {
		return mLastPlayed;
	}

	public void setLastPlayed(String lastPlayed) {
		this.mLastPlayed = lastPlayed;
	}

	public String getExpires() {
		return mExpires;
	}

	public void setExpires(String expires) {
		this.mExpires = expires;
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

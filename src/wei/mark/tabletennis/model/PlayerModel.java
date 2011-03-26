package wei.mark.tabletennis.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerModel implements Parcelable {
	public String mProvider;
	
	public String mRank;
	public String mRating;
	public String mName;
	public String mId;
	public String[] mClubs;
	public String mState;
	public String mCountry;
	public String mLastPlayed;
	public String mExpires;

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
		dest.writeString(mRank);
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
		mRank = in.readString();
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

	public static final Parcelable.Creator<PlayerModel> CREATOR = new Parcelable.Creator<PlayerModel>() {
		public PlayerModel createFromParcel(Parcel in) {
			return new PlayerModel(in);
		}

		public PlayerModel[] newArray(int size) {
			return new PlayerModel[size];
		}
	};
}

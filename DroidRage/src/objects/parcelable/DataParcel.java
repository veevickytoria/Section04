package objects.parcelable;

import android.os.Parcel;

public abstract class DataParcel<D> implements IDataParcel<D> {
	protected D data;

	public DataParcel(D data) {
		this.data = data;
	}

	public DataParcel(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public D getData() {
		return this.data;
	}

	public abstract void readFromParcel(Parcel in);

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}

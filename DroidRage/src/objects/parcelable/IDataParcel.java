package objects.parcelable;

import android.os.Parcelable;

public interface IDataParcel<D> extends Parcelable {
	public D getData();
}

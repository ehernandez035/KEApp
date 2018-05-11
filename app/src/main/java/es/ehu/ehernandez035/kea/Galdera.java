package es.ehu.ehernandez035.kea;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

public class Galdera implements Parcelable {
    private final int qid;
    private final String galdera;
    private final String auk0;
    private final String auk1;
    private final String auk2;
    private final String auk3;

    public Galdera(int qid, String galdera, String auk0, String auk1, String auk2, String auk3) {
        this.qid = qid;
        this.galdera = galdera;

        ArrayList<String> aukerak = new ArrayList<>(4);
        aukerak.add(auk0);
        aukerak.add(auk1);
        aukerak.add(auk2);
        aukerak.add(auk3);

        Collections.shuffle(aukerak);

        this.auk0 = aukerak.get(0);
        this.auk1 = aukerak.get(1);
        this.auk2 = aukerak.get(2);
        this.auk3 = aukerak.get(3);
    }

    protected Galdera(Parcel in) {
        qid = in.readInt();
        galdera = in.readString();
        auk0 = in.readString();
        auk1 = in.readString();
        auk2 = in.readString();
        auk3 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(qid);
        dest.writeString(galdera);
        dest.writeString(auk0);
        dest.writeString(auk1);
        dest.writeString(auk2);
        dest.writeString(auk3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Galdera> CREATOR = new Creator<Galdera>() {
        @Override
        public Galdera createFromParcel(Parcel in) {
            return new Galdera(in);
        }

        @Override
        public Galdera[] newArray(int size) {
            return new Galdera[size];
        }
    };

    public String getGaldera() {
        return galdera;
    }

    public String getAuk0() {
        return auk0;
    }

    public String getAuk1() {
        return auk1;
    }

    public String getAuk2() {
        return auk2;
    }

    public String getAuk3() {
        return auk3;
    }

    public int getQid() {
        return qid;
    }
}

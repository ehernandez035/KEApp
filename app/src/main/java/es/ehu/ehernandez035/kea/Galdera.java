package es.ehu.ehernandez035.kea;

import android.os.Parcel;
import android.os.Parcelable;

public class Galdera implements Parcelable{
    private final String galdera;
    private final String op1;
    private final String op2;
    private final String op3;
    private final String op4;

    protected Galdera(Parcel in) {
        galdera = in.readString();
        op1 = in.readString();
        op2 = in.readString();
        op3 = in.readString();
        op4 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(galdera);
        dest.writeString(op1);
        dest.writeString(op2);
        dest.writeString(op3);
        dest.writeString(op4);
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

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }

    public String getOp3() {
        return op3;
    }

    public String getOp4() {
        return op4;
    }

    public Galdera (String galdera, String op1, String op2, String op3, String op4){
        this.galdera=galdera;
        this.op1=op1;
        this.op2=op2;
        this.op3 = op3;
        this.op4=op4;
    }
}

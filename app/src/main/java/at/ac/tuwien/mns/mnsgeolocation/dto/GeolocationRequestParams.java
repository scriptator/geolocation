package at.ac.tuwien.mns.mnsgeolocation.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class GeolocationRequestParams implements Parcelable, Serializable {
    private List<CellTower> cellTowers;
    private List<WifiAccessPoint> wifiAccessPoints;
    private boolean considerIp;
    private GeolocationRequestParams.FallbackOptions fallbacks;

    protected GeolocationRequestParams(Parcel in) {
        cellTowers = in.createTypedArrayList(CellTower.CREATOR);
        wifiAccessPoints = in.createTypedArrayList(WifiAccessPoint.CREATOR);
        considerIp = in.readByte() != 0;
        fallbacks = in.readParcelable(FallbackOptions.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cellTowers);
        dest.writeTypedList(wifiAccessPoints);
        dest.writeByte((byte) (considerIp ? 1 : 0));
        dest.writeParcelable(fallbacks, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GeolocationRequestParams> CREATOR = new Creator<GeolocationRequestParams>() {
        @Override
        public GeolocationRequestParams createFromParcel(Parcel in) {
            return new GeolocationRequestParams(in);
        }

        @Override
        public GeolocationRequestParams[] newArray(int size) {
            return new GeolocationRequestParams[size];
        }
    };

    public final List<CellTower> getCellTowers() {
        return this.cellTowers;
    }

    public final void setCellTowers(@NotNull List<CellTower> var1) {
        this.cellTowers = var1;
    }

    public final List<WifiAccessPoint> getWifiAccessPoints() {
        return this.wifiAccessPoints;
    }

    public final void setWifiAccessPoints(@NotNull List<WifiAccessPoint> var1) {
        this.wifiAccessPoints = var1;
    }

    public final boolean getConsiderIp() {
        return this.considerIp;
    }

    public final void setConsiderIp(boolean var1) {
        this.considerIp = var1;
    }

    public final GeolocationRequestParams.FallbackOptions getFallbacks() {
        return this.fallbacks;
    }

    public final void setFallbacks(@NotNull GeolocationRequestParams.FallbackOptions var1) {
        this.fallbacks = var1;
    }

    @NotNull
    public String toString() {
        return "GeolocationRequestParams(cellTowers=" + this.cellTowers + ", wifiAccessPoints=" + this.wifiAccessPoints + ", considerIp=" + this.considerIp + ", fallbacks=" + this.fallbacks + ')';
    }

    public GeolocationRequestParams() {
        this.cellTowers = new ArrayList<>();
        this.wifiAccessPoints = new ArrayList<>();
        this.fallbacks = new GeolocationRequestParams.FallbackOptions(false, false);
    }

    public static final class FallbackOptions implements Parcelable, Serializable {
        private boolean lacf;
        private boolean ipf;

        protected FallbackOptions(Parcel in) {
            lacf = in.readByte() != 0;
            ipf = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (lacf ? 1 : 0));
            dest.writeByte((byte) (ipf ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<FallbackOptions> CREATOR = new Creator<FallbackOptions>() {
            @Override
            public FallbackOptions createFromParcel(Parcel in) {
                return new FallbackOptions(in);
            }

            @Override
            public FallbackOptions[] newArray(int size) {
                return new FallbackOptions[size];
            }
        };

        public final boolean getLacf() {
            return this.lacf;
        }

        public final void setLacf(boolean var1) {
            this.lacf = var1;
        }

        public final boolean getIpf() {
            return this.ipf;
        }

        public final void setIpf(boolean var1) {
            this.ipf = var1;
        }

        public FallbackOptions() {
        }

        public FallbackOptions(boolean lacf, boolean ipf) {
            this.lacf = lacf;
            this.ipf = ipf;
        }
    }
}

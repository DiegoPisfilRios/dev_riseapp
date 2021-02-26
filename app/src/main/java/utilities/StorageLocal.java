package utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StorageLocal {
    Context context;
    public StorageLocal(Context context){
        this.context = context;
    }

    public String readLocalData(String variable){
        SharedPreferences localStorage = PreferenceManager.getDefaultSharedPreferences(context);
        String req = localStorage.getString(variable, "");

        return  req;
    }

    public void writeLocalData(String key, String value){
        SharedPreferences localS = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = localS.edit();

        editor.putString(key, value);
        editor.apply();
    }
}

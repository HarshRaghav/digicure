package android.example.health.utils;

import android.content.Context;
import android.example.health.R;

public class MessagePersistanceManager {
    private static MessagePersistanceManager messagePersistanceManager;

    public static MessagePersistanceManager getMessagePersistanceManager() {
        if (messagePersistanceManager == null) {
            messagePersistanceManager = new MessagePersistanceManager();
        }
        return messagePersistanceManager;
    }

    public String noInternetConnectionMessage(Context mContextCompat) {
        return mContextCompat.getResources().getString(R.string.no_internet_connection);
    }

    public String contactToSupportTeamMessage(Context mContextCompat) {
        return mContextCompat.getResources().getString(R.string.contact_to_support_team);
    }
}

package com.jkpg.jurgen.nl.vacationdroid.core.account;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.login.LoginActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class AccountActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setupDeleteButton();
    }

    private void setupDeleteButton(){
        ListView v = getListView();
        Button deleteAccountButton = new Button(this);
        deleteAccountButton.setText(R.string.deleteAccountButtonText);
        deleteAccountButton.setClickable(true);
        deleteAccountButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteAccount();
                return false;
            }
        });

        v.addFooterView(deleteAccountButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_log_out, menu);
        return true;
    }

    private void deleteAccount(){

        SharedPreferences pref = getSharedPreferences("vacation", Context.MODE_PRIVATE);
        final String name = pref.getString("username", null);

        APIJsonCall dashcall = new APIJsonCall("users/"+name, "DELETE", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.e("ACCOUNT", "Deleting the user "+name+"!");
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        dashcall.execute(new JsonObject());

        logOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * Logs the person out by removing their current saved data
     */
    private void logOut() {

        DBConnection db = new DBConnection(this);
        db.clearDb();

        SharedPreferences pref = getSharedPreferences("vacation", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString("token", null);
        edit.putString("username", null);
        edit.putString("password", null);

        edit.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true; //stops normal processing
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Don't show the Up/Back button/arrow in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            final String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                //update it
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private JsonObject jsonObject;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            updateDisplayedInfo();

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("firstName"));
            bindPreferenceSummaryToValue(findPreference("lastName"));
            bindPreferenceSummaryToValue(findPreference("email"));

        }

        /**
         * Gets the JsonObject for use in the future PUT calls
         */
        @Override
        public void onStart() {
            super.onStart();

            SharedPreferences pref = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE);
            String name = pref.getString("username", null);

            APIJsonCall dashcall = new APIJsonCall("users/"+name, "GET", getActivity()) {
                @Override
                public void JsonCallback(JsonObject obj) {
                    try {
                        jsonObject = obj;
                    } catch(Exception E) {
                        Log.e("WEB ERROR", E.getMessage());
                    }
                }
            };
            dashcall.execute(new JsonObject());
        }

        /**
         * Updates the JsonObject when the fragment ends/is closed and puts it to the server so it is updated on the web
         */
        @Override
        public void onStop() {

            super.onStop();

            final EditTextPreference firstNameTextPref = (EditTextPreference) findPreference("firstName");
            final EditTextPreference lastNameTextPref = (EditTextPreference) findPreference("lastName");
            final EditTextPreference emailTextPref = (EditTextPreference) findPreference("email");

            SharedPreferences pref = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE);
            String name = pref.getString("username", null);


            jsonObject.addProperty("firstName", firstNameTextPref.getText());
            jsonObject.addProperty("lastName", lastNameTextPref.getText());
            jsonObject.addProperty("email", emailTextPref.getText());

            //update on web
            APIJsonCall updateInfo = new APIJsonCall("users/"+name, "PUT", getActivity()) {
                @Override
                public void JsonCallback(JsonObject obj) {
                    try {
//                        Log.d("JASON after", v1.toString());
                    } catch(Exception E) {
                        Log.e("WEB ERROR", E.getMessage());
                    }
                }
            };
            updateInfo.execute(jsonObject);

        }

        public void updateDisplayedInfo() {

            final EditTextPreference firstNameTextPref = (EditTextPreference) findPreference("firstName");
            final EditTextPreference lastNameTextPref = (EditTextPreference) findPreference("lastName");
            final EditTextPreference emailTextPref = (EditTextPreference) findPreference("email");

            SharedPreferences pref = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE);
            String name = pref.getString("username", null);

            APIJsonCall dashcall = new APIJsonCall("users/"+name, "GET", getActivity()) {
                @Override
                public void JsonCallback(JsonObject obj) {
                    try {
                        Log.d("JASON", obj.toString());
                        firstNameTextPref.setText(obj.get("firstName").getAsString());
                        lastNameTextPref.setText(obj.get("lastName").getAsString());
                        emailTextPref.setText(obj.get("email").getAsString());
                    } catch(Exception E) {
                        Log.e("WEB ERROR", E.getMessage());
                    }
                }
            };
            dashcall.execute(new JsonObject());
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

    }
}

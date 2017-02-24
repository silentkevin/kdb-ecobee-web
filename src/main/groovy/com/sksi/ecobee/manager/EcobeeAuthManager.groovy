package com.sksi.ecobee.manager

import com.sksi.ecobee.data.EcobeeUser
import com.sksi.ecobee.data.EcobeeUserRepository
import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import com.sksi.ecobee.manager.model.EcobeeAuthorizeResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Component
@Transactional
@CompileStatic
@Slf4j
class EcobeeAuthManager {
    @Value('${com.sksi.ecobee.ecobeeApiKey}')
    String ecobeeApiKey

    @Autowired RestTemplate restTemplate
    @Autowired EcobeeUserRepository ecobeeUserRepository
    @Autowired UserRepository userRepository

    void initUser(User user) {
        if (user.ecobeeUser != null) {
            log.debug("User already initialized user={},ecobeeUser", user, user.ecobeeUser)
            return
        }

        String url = "https://www.ecobee.com/home/authorize?response_type=ecobeePin&client_id=" + ecobeeApiKey + "&scope=smartRead";
        log.debug("initUser getting url={}", url)
        EcobeeAuthorizeResponse resp = restTemplate.getForObject(url, EcobeeAuthorizeResponse.class)

        EcobeeUser ecobeeUser = new EcobeeUser()
        ecobeeUser.setPinCode(resp.getEcobeePin())
        ecobeeUser.setEcobeeCode(resp.getCode())
        ecobeeUser.setUser(user)
        ecobeeUserRepository.save(ecobeeUser)

        user.ecobeeUser = ecobeeUser
        userRepository.save(user)

        log.debug("generated login user={},ecobeeUser={}", user, ecobeeUser)
//
//
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        Map resp = new Gson().fromJson(response, Map.class);
//                        String pinCode = resp.get("ecobeePin").toString();
//                        String code = resp.get("code").toString();
//                        Log.i(LOG_TAG, "Response is: " + response);
//                        Log.i(LOG_TAG, "PIN Code: " + pinCode);
//                        textViewStatusText.setText(String.format("From login:  PIN %s code %s", pinCode, code));
//
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString("ecobeePinCode", pinCode);
//                        editor.putString("ecobeeCode", code);
//                        editor.commit();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    textViewStatusText.setText(String.format("Login failure: " + error.networkResponse.statusCode));
//                }
//            }
//        );
//        queue.add(stringRequest);

    }

    /*
        protected void doLogin() {
        getPrefs();

        String url = "https://www.ecobee.com/home/authorize?response_type=ecobeePin&client_id=" + ECOBEE_APP_KEY + "&scope=smartRead";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Map resp = new Gson().fromJson(response, Map.class);
                            String pinCode = resp.get("ecobeePin").toString();
                            String code = resp.get("code").toString();
                            Log.i(LOG_TAG, "Response is: " + response);
                            Log.i(LOG_TAG, "PIN Code: " + pinCode);
                            textViewStatusText.setText(String.format("From login:  PIN %s code %s", pinCode, code));

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("ecobeePinCode", pinCode);
                            editor.putString("ecobeeCode", code);
                            editor.commit();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textViewStatusText.setText(String.format("Login failure: " + error.networkResponse.statusCode));
                    }
                }
        );
        queue.add(stringRequest);
    }

    protected synchronized void doGetTokenIfNeeded(final Callable callback) {
        getPrefs();

        boolean refresh = false;
        if (ecobeeAccessToken != null && ecobeeAccessTokenExpiration != null) {
            refresh = true;
            Date tenMinutesAgo = new Date(new Date().getTime() - (60L * 10L * 1000L));
            if (ecobeeAccessTokenExpiration.after(tenMinutesAgo)) {
                Log.i(LOG_TAG, "token doesn't need refreshing");
                try { callback.call(); } catch (Exception e) { throw new RuntimeException(e); };
                return;
            }
            Log.i(LOG_TAG, "token DOES need refreshing");
        }

        String url = String.format("https://www.ecobee.com/home/token?grant_type=ecobeePin&code=%s&client_id=%s", ecobeeCode, ECOBEE_APP_KEY);
        if (refresh) {
            url = String.format("https://api.ecobee.com/token?grant_type=refresh_token&code=%s&client_id=%s", ecobeeRefreshToken, ECOBEE_APP_KEY);
        }
        Log.i(LOG_TAG, "POSTing to URL " + url);

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(LOG_TAG, "Response is: " + response);
                Map resp = new Gson().fromJson(response, Map.class);
                String accessToken = resp.get("access_token").toString();
                Integer expiresInSeconds = ((Double) resp.get("expires_in")).intValue();
                String refreshToken = resp.get("refresh_token").toString();

                Date expiresOn = new Date(new Date().getTime() + expiresInSeconds * 1000L);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ecobeeAccessToken", accessToken);
                editor.putString("ecobeeRefreshToken", refreshToken);
                editor.putLong("ecobeeAccessTokenExpiration", expiresOn.getTime());
                editor.commit();

                enteredPinButton.setVisibility(View.INVISIBLE);

                getPrefs();

                try { callback.call(); } catch (Exception e) { throw new RuntimeException(e); };
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e(LOG_TAG, "error on getting token", e);
                textViewStatusText.setText(String.format("Get token failed: " + e.networkResponse.statusCode + " " + new String(e.networkResponse.data)));
            }
        });
        queue.add(sr);
    }

    protected void doGetThermostatData() {
        doGetTokenIfNeeded(new Callable() {
            @Override
            public Object call() throws Exception {
                actualDoGetThermostatData();
                return null;
            }
        });
    }

    protected void actualDoGetThermostatData() {
        getPrefs();

        String url = String.format("https://api.ecobee.com/1/thermostat?format=json&body={\"selection\":{\"selectionType\":\"registered\",\"selectionMatch\":\"\",\"includeEvents\":true,\"includeRuntime\":true,\"includeSettings\":true}}");
        Log.i(LOG_TAG, "getting thermostat data from URL " + url);

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(LOG_TAG, "Response from get thermostat data is: " + response);
                Map resp = new Gson().fromJson(response, Map.class);
                Map thermostat = null;
                List thermostats = (List) resp.get("thermostatList");
                for (Object to : thermostats) {
                    Map t = (Map) to;
                    String name = (String) t.get("name");
                    if (name.equals(ecobeeDefaultThermostatId)) {
                        thermostat = t;
                    }
                }

                List events = (List) thermostat.get("events");
                String holdMode = "schedule";
                if (events.size() > 1) {
                    Map evt = (Map) events.get(0);
                    String startDateString = (String) evt.get("startDate") + " " + (String) evt.get("startTime");
                    String endDateString = (String) evt.get("endDate") + " " + (String) evt.get("endTime");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date startDate = null;
                    Date endDate = null;
                    try { startDate = sdf.parse(startDateString); } catch (Exception e) { throw new RuntimeException(e); }
                    try { endDate = sdf.parse(endDateString); } catch (Exception e) { throw new RuntimeException(e); }
                    Long diffInMillis = endDate.getTime() - startDate.getTime();
                    Long diffInSeconds = diffInMillis / 1000L;
                    Long diffInHours = diffInSeconds / 3600L;
                    if (diffInHours > 0) { holdMode = "2h"; }
                    if (diffInHours > 2) { holdMode = "4h"; }
                    if (diffInHours > 4) { holdMode = "8h"; }
                    if (diffInHours > 8) { holdMode = "hold"; }
                }
                setTimeRadioGroup(holdMode);
                serverSetTime = holdMode;

                Map settingsData = (Map) thermostat.get("settings");
                String thermostatMode = (String) settingsData.get("hvacMode");
                Map runtimeData = (Map) thermostat.get("runtime");
                Double currentTemp = ((Double) runtimeData.get("actualTemperature")) / 10.0d;
                Double currentHumidity = (Double) runtimeData.get("actualHumidity");
                String desiredTempKey = thermostatMode.equals("heat") ? "desiredHeat" : "desiredCool";
                Double desiredTemp = ((Double) runtimeData.get(desiredTempKey)) / 10.0d;
                String msg = String.format("Mode: %s\nCurrent Temperature: %.1f F\nCurrent Humidity: %.1f F\nDesired Temperature: %.1f F",
                        thermostatMode, currentTemp, currentHumidity, desiredTemp);
                Log.i(LOG_TAG, msg);
                textViewCurrentTemperature.setText(msg);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e(LOG_TAG, "error on getting thermostat data", e);
                textViewStatusText.setText(String.format("Get thermostat data failed: " + e.networkResponse.statusCode + " " + new String(e.networkResponse.data)));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> ret = new LinkedHashMap<>(super.getHeaders());
                ret.put("Content-Type", "text/json");
                ret.put("Authorization", "Bearer " + ecobeeAccessToken);
                return ret;
            }
        };
        queue.add(sr);
    }

     */
}

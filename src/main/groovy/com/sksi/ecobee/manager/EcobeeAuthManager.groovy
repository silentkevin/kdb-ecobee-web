package com.sksi.ecobee.manager

import com.fasterxml.jackson.databind.ObjectMapper
import com.sksi.ecobee.data.EcobeeUser
import com.sksi.ecobee.data.EcobeeUserRepository
import com.sksi.ecobee.data.Thermostat
import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import com.sksi.ecobee.manager.model.EcobeeAccessTokenResponse
import com.sksi.ecobee.manager.model.EcobeeAuthorizeResponse
import com.sksi.ecobee.manager.model.EventModel
import com.sksi.ecobee.manager.model.ThermostatListModel
import com.sksi.ecobee.manager.model.ThermostatModel
import org.joda.time.DateTime

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.math.RoundingMode

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
    @Autowired ObjectMapper objectMapper

    void initUser(User user) {
        if (user.ecobeeUser != null) {
            log.debug("User already initialized user={},ecobeeUser", user, user.ecobeeUser)
            return
        }

        String url = "https://www.ecobee.com/home/authorize?response_type=ecobeePin&client_id=" + ecobeeApiKey + "&scope=smartWrite";
        log.debug("initUser getting url={}", url)
        EcobeeAuthorizeResponse resp = restTemplate.getForObject(url, EcobeeAuthorizeResponse.class)

        EcobeeUser ecobeeUser = new EcobeeUser()
        ecobeeUser.setPinCode(resp.getEcobeePin())
        ecobeeUser.setEcobeeCode(resp.getCode())
        ecobeeUser.setUser(user)
        ecobeeUserRepository.save(ecobeeUser)

        user.ecobeeUser = ecobeeUser
        userRepository.save(user)

        log.debug("generated login username={},pin={},code={}", user.name, ecobeeUser.pinCode, ecobeeUser.ecobeeCode)
    }

    void getAccessToken(User user, Boolean refresh = false) {
        EcobeeUser ecobeeUser = user.ecobeeUser

        String url = String.format("https://www.ecobee.com/home/token?grant_type=ecobeePin&code=%s&client_id=%s", ecobeeUser.ecobeeCode, ecobeeApiKey)
        if (refresh) {
            url = String.format("https://api.ecobee.com/token?grant_type=refresh_token&code=%s&client_id=%s", ecobeeUser.refreshToken, ecobeeApiKey)
        }
        log.debug("posting to refresh={},url={}", refresh, url)
        EcobeeAccessTokenResponse resp = restTemplate.postForObject(url, [:], EcobeeAccessTokenResponse.class)
        ecobeeUser.accessToken = resp.accessToken
        ecobeeUser.refreshToken = resp.refreshToken
        DateTime expiration = DateTime.now().plusSeconds(resp.getExpiresIn())
        ecobeeUser.accessTokenExpirationDate = expiration.toDate()
        ecobeeUserRepository.save(ecobeeUser)
        log.debug("got an access token accessToken={},refreshToken={},expiration={}",
            ecobeeUser.accessToken, ecobeeUser.refreshToken, expiration)

        updateThermostats(ecobeeUser)
    }

    void refreshAccessTokenIfNeeded(EcobeeUser ecobeeUser) {
        DateTime tenMinutesBeforeExpiration = new DateTime(ecobeeUser.getAccessTokenExpirationDate()).minusMinutes(10)
        if (tenMinutesBeforeExpiration.isBefore(DateTime.now())) {
            log.debug("access token doesn't need refresh tenMinutesBeforeExpiration={},accessToken={}",
                tenMinutesBeforeExpiration, ecobeeUser.getAccessToken())
            this.getAccessToken(ecobeeUser.user, true)
        }
    }

    void updateThermostats(EcobeeUser ecobeeUser) {
        refreshAccessTokenIfNeeded(ecobeeUser)

        Map selection = [
            "selection": [
                "selectionType": "registered",
                "selectionMatch": "",
                "includeEvents": true,
                "includeRuntime": true,
                "includeSettings": true
            ]
        ]
        String selectionStr = objectMapper.writeValueAsString(selection)
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.ecobee.com/1/thermostat")
            .queryParam("format", "json")
            .queryParam("body", selectionStr)
            .build().toUri();

        String url = uri.toURL().toString()
        log.debug("getting url={}", url)

        HttpHeaders headers = new HttpHeaders()
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON))
        headers.set("Authorization", "Bearer " + ecobeeUser.getAccessToken())

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers)

        //ResponseEntity<Map> mapRet = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class)
        //def a = 4

        ResponseEntity<ThermostatListModel> ret = restTemplate.exchange(uri, HttpMethod.GET, entity, ThermostatListModel.class)
        ThermostatListModel model = ret.getBody()
        for (ThermostatModel t : model.thermostats) {
            Thermostat thermostat = ecobeeUser.thermostats?.find { it.name == t.name }
            if (!thermostat) {
                if (ecobeeUser.thermostats == null) {
                    ecobeeUser.thermostats = new TreeSet<>()
                }
                thermostat = new Thermostat(name: t.name)
                ecobeeUser.thermostats.add(thermostat)
                thermostat.setEcobeeUser(ecobeeUser)
                thermostat.ecobeeId = t.identifier
            }
            thermostat.hvacMode = t.settings.hvacMode
            thermostat.currentTemperature = t.runtime.actualTemperature / 10.0
            Integer desired = thermostat.hvacMode == "heat" ? t.runtime.desiredHeat : t.runtime.desiredCool

            BigDecimal dt = desired / 10.0
            dt = dt.setScale(0, RoundingMode.HALF_UP)
            thermostat.desiredTemperature = dt.intValue()

            thermostat.holdAction = t.settings.holdAction

            String holdMode = "Schedule"
            EventModel holdEvent = t.getEvents().find { it.type == "hold" }
            if (holdEvent) {
                DateTime startDate = holdEvent.getStartDate()
                DateTime endDate = holdEvent.getEndDate()

                Long diffInMillis = endDate.getMillis() - startDate.getMillis()
                BigDecimal diffInSeconds = diffInMillis / 1000;
                BigDecimal diffInHours = diffInSeconds / 3600;
                if (diffInHours > 0) { holdMode = "2H"; }
                if (diffInHours > 2.1) { holdMode = "4H"; }
                if (diffInHours > 4.1) { holdMode = "8H"; }
                if (diffInHours > 8.1) { holdMode = "Hold"; }

                thermostat.holdUntil = endDate.toDate()
            } else {
                thermostat.holdUntil = null
            }
            thermostat.holdMode = holdMode
        }
        ecobeeUserRepository.save(ecobeeUser)
    }

    void setHold(Thermostat thermostat, Integer desiredTemperature, String holdType, Integer hours) {
        refreshAccessTokenIfNeeded(thermostat.ecobeeUser)

        Map body = [
            "selection": [
                "selectionType": "registered",
                "selectionMatch": ""
            ],
            "functions": [
                [
                    "type": "setHold",
                    "params": [
                        "holdType": holdType,
                        "heatHoldTemp": desiredTemperature * 10,
                        "coolHoldTemp": desiredTemperature * 10,
                        "holdHours": hours
                    ]
                ]
            ]
        ]
        String bodyAsJson = objectMapper.writeValueAsString(body)

        HttpHeaders headers = new HttpHeaders()
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON))
        headers.set("Authorization", "Bearer " + thermostat.ecobeeUser.getAccessToken())
        HttpEntity<String> entity = new HttpEntity<String>(bodyAsJson, headers)

        String url = "https://api.ecobee.com/1/thermostat?format=json"
        ResponseEntity<Map> resp = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            Map.class
        )
    }

    /*
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

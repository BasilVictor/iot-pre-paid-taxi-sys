void booth_login() 
{
  StaticJsonDocument<700> doc;
  String body = "{\"user_id\":" + String(USER_ID_API) + ",\"password\":\"" + (PASSWORD_API) + "\"}";
  String request = "POST http://" + String(HOST) +":" + String(PORT) + "/api/auth/login HTTP/1.1\r\n" + 
                   "Content-Type: application/json\r\n" + 
                   "Content-Length: " + String(body.length()) + "\r\n\r\n" + 
                   String(body) + "\r\n\r\n";
  Serial.println(request);
  if(connect_ESP(request)) {
    if(read_until_ESP(keyword_keepalive,sizeof(keyword_keepalive),10000,0)) {
      if(read_until_ESP(keyword_rn,sizeof(keyword_rn),5000,0)) {
        if(read_until_ESP(keyword_rn,sizeof(keyword_rn),5000,0)) {
          if(read_until_ESP(keyword_closed,sizeof(keyword_closed),5000,1)) {
            String serial_data = String(scratch_data_from_ESP);
            String resp = serial_data.substring(1,serial_data.length()-(String(keyword_closed)).length());
            //Serial.println(serial_data);
            DeserializationError error = deserializeJson(doc, resp);
            if (error) {
              Serial.print(F("deserializeJson() failed: "));
              Serial.println(error.f_str());
              return;
            }
            if(doc["status"] == 1) {
              const char* token = doc["data"]["access-token"];
              authHeader = token;
            } else {
              const char* message = doc["message"];
              print_on_LCD(message);
            }
          }
        }
      }
    }
  }
}

void add_to_queue(String scannedRFID) 
{
  StaticJsonDocument<100> doc;
  String body = "{\"uid\":" + String(scannedRFID) + "}";
  String request = "POST http://" + String(HOST) +":" + String(PORT) + "/api/booth/addToQueue HTTP/1.1\r\n" + 
                   "access-token: " + String(authHeader) + "\r\n" +
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
//            Serial.println(serial_data);
            DeserializationError error = deserializeJson(doc, resp);
            if (error) {
              Serial.print(F("deserializeJson() failed: "));
              Serial.println(error.f_str());
              return;
            }
            const char* message = doc["message"];
            print_on_LCD(message);
            if(doc["status"] == 1) {
              // Gate opens
            } else {
              // Gate remains closed
            }
          }
        }
      }
    }
  }
}

String read_RFID()
{
  mfrc522.PICC_ReadCardSerial();
  Serial.print(F("\nPICC type: "));
  MFRC522::PICC_Type piccType = mfrc522.PICC_GetType(mfrc522.uid.sak);
  Serial.println(mfrc522.PICC_GetTypeName(piccType));

  // Check is the PICC of Classic MIFARE type
  if (piccType != MFRC522::PICC_TYPE_MIFARE_MINI &&  
    piccType != MFRC522::PICC_TYPE_MIFARE_1K &&
    piccType != MFRC522::PICC_TYPE_MIFARE_4K) {
    Serial.println(F("Your tag is not of type MIFARE Classic."));
    return "";
  }
  Serial.println(String(mfrc522.uid.uidByte[0])+" "+String(mfrc522.uid.uidByte[1])+" "+String(mfrc522.uid.uidByte[2])+" "+String(mfrc522.uid.uidByte[3]));
  String uidString = String(mfrc522.uid.uidByte[0])+String(mfrc522.uid.uidByte[1])+String(mfrc522.uid.uidByte[2])+String(mfrc522.uid.uidByte[3]);
  mfrc522.PICC_HaltA();
  mfrc522.PCD_StopCrypto1();
  return uidString;
}

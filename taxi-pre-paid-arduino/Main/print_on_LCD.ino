void print_on_LCD(char string[]) {
  lcd_status = 1;
  lcd.clear();
  int limit = 16;
  int line = 0;
  char *singleWord = NULL;
  lcd.setCursor(0, 0);
  singleWord = strtok(string, " ");
  while(singleWord != NULL) {
    if(strlen(singleWord)>limit) {
      if(line == 0)
        line = 1;
      else
        line = 0;
      limit = 16;
      lcd.setCursor(0, line);
    }
    for(int i=0;i<singleWord[i]!='\0';i++) {
      lcd.print(singleWord[i]);
      limit -= 1;
    }
    if(limit>0) {
      lcd.print(" ");
      limit -= 1;
    }
    singleWord = strtok(NULL, " ");
  }
  lcd_timeout = millis() + 5000;
}

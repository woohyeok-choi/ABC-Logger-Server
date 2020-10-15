openssl req -nodes -newkey rsa:2048 \
  -keyout abc-bi.key \
  -out abc-bi.crt \
  -x509 -days 365 \
  -subj "/C=KR/ST=Daejeon/L=Daejeon/O=KAIST/OU=Interactive Computing Lab/CN=ic.kaist.ac.kr" && \
  cat abc-bi.crt abc-bi.key > abc-bi.pem
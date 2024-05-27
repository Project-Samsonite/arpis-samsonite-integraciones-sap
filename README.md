# arpis-samsonite-integraciones-sap

## Generales

**ENV**
- INT_LOGS_LEVEL=[LOG LEVEL => DEBUG|INFO|WARN]
- INT_LOGS_PATH=[LOG PATH]
- INT_EMAIL_SMTP_PASSWORD=[PASS]
- INT_EMAIL_SMTP_USER=email@email.cl
- INT_EMAIL_TO=email_1@gmail.com,email_2@gmail.com, etc

**JVM args**
- -Dspring.profiles.active=[dev|qa|prod]

## Adicionalmente para "qa" y "prod"

**ENV**
- CONFIG_GENERAL=[PATH CONFIG]/sap-application.properties
- CONFIG_EMPRESAS=[PATH CONFIG]sap-empresas.properties
- CONFIG_MULTIVENDE=[PATH CONFIG]/sap.properties
- APP_PATH="[PATH JAR]"
- JAVA_HOME="[PATH OPENJDK]"
- INT_PROFILE=[qa|prod]

**JVM args**
- -Dspring.profiles.active=%INT_PROFILE%
- -Dspring.config.location=%CONFIG_GENERAL%,%CONFIG_EMPRESAS%,%CONFIG_MULTIVENDE%

## Servicio Windows ejemplo - Ejecuta consola CMD
- servicio-integracion.bat
